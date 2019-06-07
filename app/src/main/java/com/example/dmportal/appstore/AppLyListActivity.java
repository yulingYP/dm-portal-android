package com.example.dmportal.appstore;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.base.BaseActivity;
import com.example.base.BaseResponse;
import com.example.dmportal.MyActivityManager;
import com.example.dmportal.R;
import com.example.dmportal.appstore.adapter.ApplyInfoAdapter;
import com.example.dmportal.appstore.bean.ApplyInfo;
import com.example.dmportal.appstore.bean.ApplyRecord;
import com.example.dmportal.appstore.presenter.ApplyInfoPresenter;
import com.example.dmportal.appstore.utils.ARouterConstants;
import com.example.dmportal.appstore.utils.Constants;
import com.example.dmportal.appstore.utils.PermissionsUtil;
import com.example.dmportal.commontitlebar.CustomTitleBar;
import com.example.dmportal.main.presenter.MainPresenter;
import com.example.dmportal.main.presenter.MessagePresenter;
import com.example.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.AppLyListActivity)
public class AppLyListActivity extends BaseActivity<ApplyInfoPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.no_layout)
    LinearLayout lg_no;//暂无记录或网络

    @BindView(R.id.no_img)
    ImageView img_no;//暂无记录或网络

    @BindView(R.id.no_text)
    TextView tv_no;//暂无记录或网络

    @BindView(R.id.history_list_view)
    RecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    @BindView(R.id.search_icon)
    ImageView iv_search;

    @Autowired(name = "type")
    int type;//页面类型 0.权限审批 1.历史申请记录 2.历史审批记录

    @Autowired(name = "userId")
    int userId;//要查询的id

    @Autowired(name = "isSearch")
    boolean isSearch;//是不是搜索页

    @Autowired(name = "isAll")
    boolean isAll;//是否查询全部记录

    @Autowired(name = "checkCode")
    int checkCode;//-1.代表编辑框输入 其他.代表点击标签

    @Autowired(name = "content")
    String content;//编辑框输入内容

    @Autowired(name = "ARouterPath")
    String ARouterPath;

    private long requestId;//list中最后一个数据的id
    private List<ApplyInfo> applyInfoList;//申请记录列表
    private List<ApplyRecord> applyRecordList;//审批历史列表
    private ApplyInfoAdapter applyInfoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_ly_list);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();

    }

    private void initView() {
        if(type==0||type==1)
            applyInfoList = new ArrayList<>();
        else
            applyRecordList = new ArrayList<>();

        titleBar.setTitle(setMyTitle());
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    finish()
                );
        //搜索
        RxView.clicks(iv_search)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                        ARouter.getInstance()
                                .build(ARouterConstants.ListSearchActivity)
                                .withInt("type",type+10)
                                .navigation(this)
                );
        //下拉刷新监听
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            requestId = -1;
            if(applyInfoList!=null&&applyInfoList.size()>0) {
                applyInfoList.clear();
                if(applyInfoAdapter!=null)
                    applyInfoAdapter.notifyDataSetChanged();
            }else if(applyRecordList!=null&&applyRecordList.size()>0){
                applyRecordList.clear();
                if(applyInfoAdapter!=null)
                    applyInfoAdapter.notifyDataSetChanged();
            }
            httpPost();
        });
        //加载更多
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            requestId = setRequestId(type);
            httpPost();
        });

        //隐藏暂无页面
        lg_no.setVisibility(View.GONE);
        smartRefreshLayout.autoRefresh();
    }
    /**
     * 获取请假信息失败
     * @param msg m
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(smartRefreshLayout.getState().isOpening) {
                smartRefreshLayout.finishRefresh(true);
                smartRefreshLayout.finishLoadMore(true);
            }
            Toast.makeText(this,("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            setNoLayout(1);
        }
    }

    /**
     * 获取请假信息成功
     * @param data d
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_REQUEST_APPLY_LIST)
    }, thread = EventThread.MAIN_THREAD)
    public void getLeaveInfoList(BaseResponse<List<ApplyInfo>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(requestId<0) {//下拉刷新
                smartRefreshLayout.finishRefresh(true);
                smartRefreshLayout.finishLoadMore(true);
            }
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&applyInfoList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                smartRefreshLayout.finishLoadMoreWithNoMoreData();
            }
            else {//有数据
                int currentSize = applyInfoList.size();
                applyInfoList.addAll(data.getData());
                setNoLayout(0);
                if(applyInfoAdapter==null)
                    initList();
                else
                    applyInfoAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
                if(data.getData().size()<Constants.requestSize) {
                    //延时设置防止加载动画错误
                    new Handler().postDelayed(() -> smartRefreshLayout.setNoMoreData(true), Constants.clickdelay * 2);
                }
            }
        }
    }
    /**
     * 获取审批记录成功
     * @param data d
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPROVAL_HISTORY_LIST)
    }, thread = EventThread.MAIN_THREAD)
    public void getApprovalInfoList(BaseResponse<List<ApplyRecord>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(requestId < 0) {//下拉刷新
                smartRefreshLayout.finishRefresh(true);
                smartRefreshLayout.finishLoadMore(true);
            }
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&applyRecordList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                smartRefreshLayout.finishLoadMoreWithNoMoreData();
            }
            else {//有数据
                int currentSize = applyRecordList.size();
                applyRecordList.addAll(data.getData());
                setNoLayout(0);
                if(applyInfoAdapter==null)
                    initList();
                else
                    applyInfoAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
                if(data.getData().size()<Constants.requestSize)
                    smartRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }
    /**
     * 初始化列表
     */
    private void initList() {
        applyInfoAdapter = new ApplyInfoAdapter(this,applyInfoList,applyRecordList,ARouterPath,type,R.layout.item_leave_info);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(applyInfoAdapter);
    }

    /**
     * 设置该页面标题
     * @return 1
     */
    private int setMyTitle() {
        if(isSearch) {
            iv_search.setVisibility(View.GONE);
            return R.string.search_result;
        }
        if(type==0) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.approval_list;
        }
        else if(type == 1) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.apply_history_list;
        }
        else if(type==2) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.approval_history_list;
        }
        return R.string.no_title;
    }

    private void httpPost() {
        if(!PermissionsUtil.isNetworkConnected(this)){//网络检测
            Toast.makeText(this, R.string.no_net_tip_2,Toast.LENGTH_SHORT).show();
            smartRefreshLayout.finishRefresh(false);
            setNoLayout(2);
            return;
        }
        if(isAll) {//查询全部内容
            if (type == 0) {//待审批申请
                Integer stuAut=SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();//审批学生权限
                Integer teaAut=SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();//审批老师权限
                if (checkAuthority(stuAut,teaAut)) {
                    mPersenter.getApplyInfoList(SharedPreferencesUtil.getInstance().getUserId(), stuAut, teaAut, type,requestId);
                    if(requestId<=0)
                        new MessagePresenter(this).updateMsgStatus(userId,null,5);
                }else{
                    smartRefreshLayout.finishRefresh(true);
                    Toast.makeText(this, R.string.aut_error_tip_1, Toast.LENGTH_SHORT).show();
                    setNoLayout(3);
                }
            } else if (type == 1) {//历史申请记录
                mPersenter.getApplyInfoList(SharedPreferencesUtil.getInstance().getUserId(), null, null, type, requestId);
            } else if (type == 2) {//历史审批记录
                mPersenter.getHistoryApprovalApplyList(SharedPreferencesUtil.getInstance().getUserId(), requestId);
            }
        }else {
            if(type==0){//待审批内容
                Integer stuAut=SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();//审批学生权限
                Integer teaAut=SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();//审批老师权限
                if(checkAuthority(stuAut, teaAut))
                    mPersenter.getSearchApplyList(type,userId,checkCode,content,requestId,stuAut,teaAut);
                else {
                    smartRefreshLayout.finishRefresh(true);
                    Toast.makeText(this, R.string.aut_error_tip_1, Toast.LENGTH_SHORT).show();
                    setNoLayout(3);
                }
            }else if(type==1||type==2){//1.历史申请记录 2.历史审批记录
                mPersenter.getSearchApplyList(type,userId,checkCode,content,requestId, null, null);
            }else {
                smartRefreshLayout.finishRefresh(true);
                setNoLayout(0);
            }
        }
    }
    //检察权限
    private boolean checkAuthority(Integer stuAut, Integer teaAut) {

        if(stuAut<0)//没有审批学生权限
            stuAut=0;
        if(teaAut<0){//没有审批老师权限
            teaAut=0;
        }
        if(stuAut<=0&&teaAut<=0)
            return false;
        //不包含班长、班主任或辅导员或权限审批负责人权限
        if(!(String.valueOf(stuAut).contains("1")||String.valueOf(stuAut).contains("2")||String.valueOf(stuAut).contains("4")||String.valueOf(stuAut).contains("8")))
            stuAut=null;
        //不包含部门权限审批人权限
        if(String.valueOf(stuAut).contains("2"))
            teaAut=null;
        return !(stuAut == null && teaAut == null);

    }

    /**
     *  设置暂无类页面
     * @param showType 0.隐藏暂无页面 1.暂无记录 2.暂无网络
     */
    private void setNoLayout(int showType) {
        if(showType==0) {
            lg_no.setVisibility(View.GONE);
        }
        else if(showType==1){
            lg_no.setVisibility(View.VISIBLE);
            img_no.setImageResource(R.mipmap.no_history);
            tv_no.setText(R.string.no_history_tip);
        }
        else if(showType==2&&(((type==1||type==0||type==3)&&applyInfoList.size()==0)||(type==2&&applyRecordList.size()==0))){
            lg_no.setVisibility(View.VISIBLE);
            img_no.setImageResource(R.mipmap.nointernet);
            tv_no.setText(R.string.no_net_tip);
        } else if(showType==3){
            lg_no.setVisibility(View.VISIBLE);
            img_no.setImageResource(R.mipmap.no_aut);
            tv_no.setText(R.string.no_aut_tip);
        }
    }
    /**
     * 更新审批状态成功
     * @param applyId l
     */
    @Subscribe(tags = {
            @Tag("updateList")
    }, thread = EventThread.MAIN_THREAD)
    public void updateSuccess(Long applyId) {
        if(applyInfoAdapter!=null&&applyInfoList != null) {
            for (int i = applyInfoList.size()-1; i >= 0 ; i--) {
                if (applyInfoList.get(i).getApplyId()== applyId) {
                    applyInfoList.remove(i);
                }
            }
            applyInfoAdapter.notifyDataSetChanged();
            if(applyInfoList.size()==0)
                setNoLayout(1);
        }
    }
    @Override
    public ApplyInfoPresenter getPersenter() {
        return new ApplyInfoPresenter(this);
    }

    /**
     * 设置requestId
     * @param type 页面类型 0.权限审批 1.历史申请记录 2.历史审批记录
     * return requestId
     */
    public long setRequestId(int type) {
        if ((type == 0||type==1)&&applyInfoList!=null&&applyInfoList.size()>0)
            return applyInfoList.get(applyInfoList.size() - 1).getApplyId();
        else if(type==2&&applyRecordList!=null&&applyRecordList.size()>0)
            return applyRecordList.get(applyRecordList.size() - 1).getApplyId();
        return -1;
    }
}
