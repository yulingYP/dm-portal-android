package com.definesys.dmportal.appstore.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.SubjectTableActivity;
import com.definesys.dmportal.appstore.adapter.MainIconAdapter;
import com.definesys.dmportal.appstore.bean.MainIcon;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.presenter.GetCurrentApprovalStatusPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
@Route(path = ARouterConstants.LeaveMainActivity)
public class LeaveMainActivity extends BaseActivity<GetCurrentApprovalStatusPresenter> {
    @BindView(R.id.title_bar)
     CustomTitleBar titleBar;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.leave_layout)
    LinearLayout lg_leave;//请假

    @BindView(R.id.approval_layout)
    LinearLayout lg_approval;//审批处理

    @BindView(R.id.my_leave_history_layout)
    LinearLayout lg_leaveHistory;//请假记录

    @BindView(R.id.progress_layout)
    LinearLayout lg_progress;//进度查询

    @BindView(R.id.approval_history_layout)
    LinearLayout lg_approvalHistory;//审批历史记录

    @BindView(R.id.current_status)
    TextView tv_currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.leave_off));
        titleBar.setBackgroundDividerEnabled(false);
        //titleBar.setBackground(null);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });

        //请假
        RxView.clicks(lg_leave)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                        ARouter.getInstance()
                        .build(ARouterConstants.LeaveActivity)
                        .navigation());
        //请假记录
        RxView.clicks(lg_leaveHistory)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                        ARouter.getInstance()
                                .build(ARouterConstants.LeaveListActivity)
                                .withInt("userId",(int) SharedPreferencesUtil.getInstance().getUserId())
                                .withInt("type", 0)//列表类型
                                .withBoolean("isAll",true)
                                .withBoolean("isSearch",false)
                                .withString("ARouterPath",ARouterConstants.LeaveInFoDetailActivity)//点击列表跳转的页面
                                .navigation());
        //审批处理
        RxView.clicks(lg_approval)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                        ARouter.getInstance()
                                .build(ARouterConstants.LeaveListActivity)
                                .withInt("userId",(int) SharedPreferencesUtil.getInstance().getUserId())
                                .withInt("type", 1)//列表类型
                                .withBoolean("isAll",true)
                                .withBoolean("isSearch",false)
                                .withString("ARouterPath",ARouterConstants.ApprovalLeaveInfoActivity)//点击列表跳转的页面
                                .navigation());
        //审批记录
        RxView.clicks(lg_approvalHistory)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                        ARouter.getInstance()
                                .build(ARouterConstants.LeaveListActivity)
                                .withInt("userId",(int) SharedPreferencesUtil.getInstance().getUserId())
                                .withInt("type", 2)//列表类型
                                .withBoolean("isAll",true)
                                .withBoolean("isSearch",false)
                                .withString("ARouterPath",ARouterConstants.ApprovalLeaveInfoActivity)//点击列表跳转的页面
                                .navigation());

        //进度查询
        RxView.clicks(lg_progress)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                        ARouter.getInstance()
                                .build(ARouterConstants.LeaveInFoDetailActivity)
                                .withObject("leaveInfo",null)
                                .withInt("title",1)
                                .navigation());
        if(SharedPreferencesUtil.getInstance().getUserAuthority()<0){
            lg_approval.setVisibility(View.GONE);
            lg_approvalHistory.setVisibility(View.GONE);
        }else {
            lg_approval.setVisibility(View.VISIBLE);
            lg_approvalHistory.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取当前请假状态失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(LeaveMainActivity.this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            tv_currentStatus.setText(R.string.status_tip_7);
            DensityUtil.setTVcolor(getString(R.string.status_tip_7),tv_currentStatus,this);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 获取当前请假状态成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_CURRENT_STATUS)
    }, thread = EventThread.MAIN_THREAD)
    public void getTableInfo(BaseResponse<String> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            tv_currentStatus.setText(data.getData());
            DensityUtil.setTVcolor(data.getData(),tv_currentStatus,this);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        tv_currentStatus.setText("");
        mPersenter.getCurrentStatus(SharedPreferencesUtil.getInstance().getUserId());
    }

    @Override
    public GetCurrentApprovalStatusPresenter getPersenter() {
        return new GetCurrentApprovalStatusPresenter(this);
    }

}
