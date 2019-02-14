package com.definesys.dmportal.appstore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
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
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.LeaveInfoListAdapter;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.presenter.GetLeaveInfoHistoryPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.PermissionsUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
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

    private List<LeaveInfo> submitLeaveInfoList;//请假记录列表
    private List<ApprovalRecord>approvalRecordList;//审批记录列表
    private LeaveInfoListAdapter leaveInfoListAdapter;//适配器

    @Autowired(name = "userId")
    int userId;//要查询的id

    @Autowired(name = "type")
    int type;//页面类型 0.历史请假记录 1.待处理的审批记录 2.历史审批记录

    @Autowired(name = "ARouterPath")
    String ARouterPath;

    private int requestPage;//请求的页码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_history);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    private void initView() {
        if(type==0||type==1)
            submitLeaveInfoList = new ArrayList<>();
        else
            approvalRecordList = new ArrayList<>();

        titleBar.setTitle(setMyTitle());
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
        //下拉刷新监听
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestPage = 1;
                if(submitLeaveInfoList!=null&&submitLeaveInfoList.size()>0) {
                    submitLeaveInfoList.clear();
                    if(leaveInfoListAdapter!=null)
                        leaveInfoListAdapter.notifyDataSetChanged();
                }else if(approvalRecordList!=null&&approvalRecordList.size()>0){
                    submitLeaveInfoList.clear();
                    leaveInfoListAdapter.notifyDataSetChanged();
                }
                httpPost();
            }
        });
        //加载更多
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++requestPage;
                httpPost();
            }
        });
        RxView.clicks(iv_search)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                   ARouter.getInstance()
                           .build(ARouterConstants.LeaveListSearchActivity)
                           .withInt("type",type)
                           .navigation();
                });
        //隐藏暂无页面
        lg_no.setVisibility(View.GONE);
        smartRefreshLayout.autoRefresh();
    }

    /**
     * 设置该页面标题
     * @return
     */
    private int setMyTitle() {
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
        if(type==0)//历史请假记录
            mPersenter.getLeaveInfoList(userId,requestPage);
        else if(type==1)//待审批记录
            mPersenter.getApprovalList(userId,requestPage, SharedPreferencesUtil.getInstance().getUserAuthority(),SharedPreferencesUtil.getInstance().getUserType());
        else if(type==2)//历史审批记录
            mPersenter.getApprovalHistoryList(userId,requestPage);
    }

    /**
     * 获取请假信息失败
     * @param msg
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
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO_LIST)
    }, thread = EventThread.MAIN_THREAD)
    public void getLeaveInfoList(BaseResponse<List<LeaveInfo>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(requestPage==1)//下拉刷新
                smartRefreshLayout.finishRefresh(true);
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&submitLeaveInfoList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                --requestPage;
            }
            else {//有数据
                int currentSize = submitLeaveInfoList.size();
                List<LeaveInfo> leaveInfos = data.getData();
                //排序
                Collections.sort(leaveInfos);
                submitLeaveInfoList.addAll(leaveInfos);
                setNoLayout(0);
                if(leaveInfoListAdapter==null)
                    initList();
                else
                    leaveInfoListAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
            }
        }
    }
    /**
     * 获取审批记录成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPROVAL_HISTORY_LIST)
    }, thread = EventThread.MAIN_THREAD)
    public void getApprovalInfoList(BaseResponse<List<ApprovalRecord>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(requestPage==1)//下拉刷新
                smartRefreshLayout.finishRefresh(true);
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&approvalRecordList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                --requestPage;
                return;
            }
            else {//有数据
                int currentSize = approvalRecordList.size();
                approvalRecordList.addAll(data.getData());
                setNoLayout(0);
                if(leaveInfoListAdapter==null)
                    initList();
                else
                    leaveInfoListAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
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
        else if(showType==2&&(((type==1||type==0)&&submitLeaveInfoList.size()==0)||(type==2&&approvalRecordList.size()==0))){
            lg_no.setVisibility(View.VISIBLE);
            img_no.setImageResource(R.mipmap.nointernet);
            tv_no.setText(R.string.no_net_tip);
        }
    }

    /**
     * 更新审批状态成功
     * @param leaveId
     */
    @Subscribe(tags = {
            @Tag("updateSuccess")
    }, thread = EventThread.MAIN_THREAD)
    public void updateSuccess(String leaveId) {
        if(submitLeaveInfoList!=null) {
            for (int i = 0; i < submitLeaveInfoList.size(); i++) {
                if (submitLeaveInfoList.get(i).getId().equals(leaveId)) {
                    submitLeaveInfoList.remove(i);
                    break;
                }
            }
            leaveInfoListAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public GetLeaveInfoHistoryPresenter getPersenter() {
        return new GetLeaveInfoHistoryPresenter(this);
    }
}
