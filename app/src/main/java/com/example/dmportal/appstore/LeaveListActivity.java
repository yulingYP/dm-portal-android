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
import com.example.dmportal.appstore.adapter.LeaveInfoListAdapter;
import com.example.dmportal.appstore.bean.ApprovalRecord;
import com.example.dmportal.appstore.bean.LeaveInfo;
import com.example.dmportal.appstore.presenter.GetLeaveInfoHistoryPresenter;
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

@Route(path = ARouterConstants.LeaveListActivity)
public class LeaveListActivity extends BaseActivity<GetLeaveInfoHistoryPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.search_icon)
    ImageView iv_search;//搜索图标

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

    @Autowired(name = "userId")
    int userId;//要查询的id

    @Autowired(name = "type")
    int type;//页面类型 0.历史请假记录 1.待处理的请假记录 2.历史审批记录 3.销假

    @Autowired(name = "ARouterPath")
    String ARouterPath;

    @Autowired(name = "isSearch")
    boolean isSearch;//是不是搜索页

    @Autowired(name = "isAll")
    boolean isAll;//是否查询全部记录

    @Autowired(name = "checkCode")
    int checkCode;//-1.代表编辑框输入 其他.代表点击标签

    @Autowired(name = "content")
    String content;//编辑框输入内容

    private long requestId;//list中最后一个数据的id
    private List<LeaveInfo> submitLeaveInfoList;//请假记录列表
    private List<ApprovalRecord>approvalRecordList;//审批记录列表
    private LeaveInfoListAdapter leaveInfoListAdapter;//适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_history);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    private void initView() {
        if(type==0||type==1||type==3)
            submitLeaveInfoList = new ArrayList<>();
        else
            approvalRecordList = new ArrayList<>();

        titleBar.setTitle(setMyTitle());
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    finish()
                );
        //下拉刷新监听
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            requestId = -1;
            if(submitLeaveInfoList!=null&&submitLeaveInfoList.size()>0) {
                submitLeaveInfoList.clear();
                if(leaveInfoListAdapter!=null)
                    leaveInfoListAdapter.notifyDataSetChanged();
            }else if(approvalRecordList!=null&&approvalRecordList.size()>0){
                approvalRecordList.clear();
                if(leaveInfoListAdapter!=null)
                    leaveInfoListAdapter.notifyDataSetChanged();
            }
            httpPost();
        });
        //加载更多
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            requestId = setRequestId(type);
            httpPost();
        });

        //搜索
        RxView.clicks(iv_search)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o ->
                   ARouter.getInstance()
                           .build(ARouterConstants.ListSearchActivity)
                           .withInt("type",type)
                           .navigation()
                );
        //隐藏暂无页面
        lg_no.setVisibility(View.GONE);
        smartRefreshLayout.autoRefresh();
    }

    /**
     * 设置该页面标题
     * @return r
     */
    private int setMyTitle() {
        if(isSearch) {
            iv_search.setVisibility(View.GONE);
            return R.string.search_result;
        }
        if(type==0) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.leave_history;
        }
        else if(type == 1) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.approval_processing;
        }
        else if(type==2) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.approval_record;
        }
        else if(type==3) {
            iv_search.setVisibility(View.VISIBLE);
            return R.string.leave_stop_title;
        }
        return R.string.leave_history;
    }

    //清空记录，发送网络请求
    private void httpPost() {
        if(!PermissionsUtil.isNetworkConnected(this)){//网络检测
            Toast.makeText(this, R.string.no_net_tip_2,Toast.LENGTH_SHORT).show();
            smartRefreshLayout.finishRefresh(false);
            setNoLayout(2);
            return;
        }
        if(isAll) {//查询全部记录
            if (type == 0) {//历史请假记录
                mPersenter.getAllLeaveInfoList(userId, requestId);
            }
            else if (type == 1) {//待审批记录
                mPersenter.getAllApprovalList(userId, requestId, SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority(), SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority(), SharedPreferencesUtil.getInstance().getUserType());
                if(requestId<=0)
                 new MessagePresenter(this).updateMsgStatus(userId,null,2);
            }
            else if (type == 2)//历史审批记录
                mPersenter.getAllApprovalHistoryList(userId, requestId);
            else if(type==3){//销假
                mPersenter.getSearchLeaveInfoList(userId, requestId,30,type,"");
            }
        }else {
            if (type == 0||type==3)//历史请假记录||销假
                mPersenter.getSearchLeaveInfoList(userId,requestId,checkCode,type,checkCode==-1?content:"");
            else if (type == 1)//待审批记录
                mPersenter.getSearchApprovalList(userId, requestId, SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority(), SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority(), SharedPreferencesUtil.getInstance().getUserType(),checkCode,type,checkCode==-1?content:"");
            else if(type == 2){//历史审批记录
                mPersenter.getSearchApprovalHistoryList(userId,requestId,checkCode,type,checkCode==-1?content:"");
            }
        }
    }

    /**
     * 设置requestId
     * @param type 页面类型0.历史请假记录 1.待处理的请假记录 2.历史审批记录 3.销假
     * @return requestId
     */
    private long setRequestId(int type) {
        if ((type == 0||type==1||type==3)&&submitLeaveInfoList!=null&&submitLeaveInfoList.size()>0)
           return submitLeaveInfoList.get(submitLeaveInfoList.size() - 1).getId();
        else if(type==2&&approvalRecordList!=null&&approvalRecordList.size()>0)
            return approvalRecordList.get(approvalRecordList.size() - 1).getApprovalTime().getTime();
        return -1;
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
                smartRefreshLayout.finishRefresh(false);
                smartRefreshLayout.finishLoadMore(false);
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
            @Tag(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO_LIST)
    }, thread = EventThread.MAIN_THREAD)
    public void getLeaveInfoList(BaseResponse<List<LeaveInfo>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(requestId <0 ) {//下拉刷新
                smartRefreshLayout.finishRefresh(true);
                smartRefreshLayout.finishLoadMore(true);
            }
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&submitLeaveInfoList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                smartRefreshLayout.finishLoadMoreWithNoMoreData();
            }
            else {//有数据
                int currentSize = submitLeaveInfoList.size();
                submitLeaveInfoList.addAll(data.getData());
                setNoLayout(0);
                if(leaveInfoListAdapter==null)
                    initList();
                else
                    leaveInfoListAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
                if(data.getData().size()<Constants.requestSize)
                    new Handler().postDelayed(()-> smartRefreshLayout.setNoMoreData(true),Constants.clickdelay*2);
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
    public void getApprovalInfoList(BaseResponse<List<ApprovalRecord>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(requestId<0) {//下拉刷新
                smartRefreshLayout.finishRefresh(true);
                smartRefreshLayout.finishLoadMore(true);
            }
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&approvalRecordList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                smartRefreshLayout.finishLoadMoreWithNoMoreData();
            }
            else {//有数据
                int currentSize = approvalRecordList.size();
                approvalRecordList.addAll(data.getData());
                setNoLayout(0);
                if(leaveInfoListAdapter==null)
                    initList();
                else
                    leaveInfoListAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
                if(data.getData().size()<Constants.requestSize) {
                    //延时设置防止加载动画错误
                    new Handler().postDelayed(() -> smartRefreshLayout.setNoMoreData(true), Constants.clickdelay * 2);
                }

            }
        }
    }

    /**
     * 初始化列表
     */
    private void initList() {
        leaveInfoListAdapter = new LeaveInfoListAdapter(this,submitLeaveInfoList,approvalRecordList,ARouterPath,type,R.layout.item_leave_info);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(leaveInfoListAdapter);
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
        else if(showType==2&&(((type==1||type==0||type==3)&&submitLeaveInfoList.size()==0)||(type==2&&approvalRecordList.size()==0))){
            lg_no.setVisibility(View.VISIBLE);
            img_no.setImageResource(R.mipmap.nointernet);
            tv_no.setText(R.string.no_net_tip);
        }
    }

    /**
     * 更新审批状态成功
     * @param leaveId l
     */
    @Subscribe(tags = {
            @Tag("updateSuccess")
    }, thread = EventThread.MAIN_THREAD)
    public void updateSuccess(Long leaveId) {
        if(submitLeaveInfoList!=null&&leaveInfoListAdapter!=null) {
            for (int i =  submitLeaveInfoList.size()-1; i >= 0 ; i--) {
                if (submitLeaveInfoList.get(i).getId()==(leaveId)) {
                    submitLeaveInfoList.remove(i);
                }
            }
            leaveInfoListAdapter.notifyDataSetChanged();
            if(submitLeaveInfoList.size()==0)
                setNoLayout(1);
        }
    }


    /**
     * 销假成功
     * @param leaveId l
     */
    @Subscribe(tags = {
            @Tag("cancelLeaveSuccess")
    }, thread = EventThread.MAIN_THREAD)
    public void cancelSuccess(Long leaveId) {
        if(submitLeaveInfoList!=null&&leaveInfoListAdapter!=null) {
            for (int i =  submitLeaveInfoList.size()-1; i >= 0 ; i--) {
                if (submitLeaveInfoList.get(i).getId()==leaveId) {
                    if(type!=3&&isAll)//非销假列表 显示全部请假信息的列表
                        submitLeaveInfoList.get(i).setApprovalStatus((short)120);
                    else//销假列表
                        submitLeaveInfoList.remove(i);
                }
            }
            leaveInfoListAdapter.notifyDataSetChanged();
            if(submitLeaveInfoList.size()==0)
                setNoLayout(1);
        }
    }
    @Override
    public GetLeaveInfoHistoryPresenter getPersenter() {
        return new GetLeaveInfoHistoryPresenter(this);
    }
}
