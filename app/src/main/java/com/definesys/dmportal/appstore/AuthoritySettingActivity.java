package com.definesys.dmportal.appstore;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.presenter.LeaveAuthorityPresenter;
import com.definesys.dmportal.appstore.ui.LeaveMainActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@Route(path = ARouterConstants.AuthoritySettingActivity)
public class AuthoritySettingActivity extends BaseActivity<LeaveAuthorityPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @BindView(R.id.approval_stu_text)
    TextView tv_stu;
    @BindView(R.id.approval_tea_text)
    TextView tv_tea;
    @BindView(R.id.tea_layout)
    RelativeLayout lg_tea;
    @BindView(R.id.stu_layout)
    RelativeLayout lg_stu;
    @BindView(R.id.stu_down_icon)
    ImageView iv_stu;
    @BindView(R.id.tea_down_icon)
    ImageView iv_tea;
    @BindView(R.id.apply_layout)
    LinearLayout lg_apply;
    @BindView(R.id.delete_layout)
    LinearLayout lg_delete;
    @BindView(R.id.approval_layout)
    LinearLayout lg_approval;
    @BindView(R.id.approval_history_layout)
    LinearLayout lg_appHis;
    @BindView(R.id.apply_history_layout)
    LinearLayout lg_alyHis;
    private volatile int requestCount=0;//请求的个数
    List<String> stuAutInfoList;//审批请假学生权限列表
    List<String> teaAutInfoList;//审批请假教师权限列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_authority);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.my_authority_title));
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    finish();
                });
        //点击审批学生权限箭头
        RxView.clicks(lg_stu)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(tv_stu.getVisibility()== View.VISIBLE){//已显权限
                        tv_stu.setVisibility(GONE);
                        iv_stu.setRotation(0);
                    }else {//未显示权限
                        if(stuAutInfoList==null&&"".equals(tv_stu.getText().toString())){//还没有获取到详细权限
                            httpPost(0);
                        }else
                            tv_stu.setVisibility(VISIBLE);
                        iv_stu.setRotation(180);
                    }
                });

        //点击审批教师权限箭头
        RxView.clicks(lg_tea)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(tv_tea.getVisibility()== View.VISIBLE){//已显权限
                        tv_tea.setVisibility(GONE);
                        iv_tea.setRotation(0);
                    }else {//未显示权限
                        if(teaAutInfoList==null&& "".equals(tv_tea.getText().toString())){//还没有获取到详细权限
                            httpPost(1);
                        }else
                            tv_tea.setVisibility(VISIBLE);
                        iv_tea.setRotation(180);
                    }
                });
        //申请权限
        RxView.clicks(lg_apply)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ARouter.getInstance().build(ARouterConstants.UpdateLeAutActivity)
                            .withInt("type",0)//申请权限
                            .navigation();
                });
        //删除权限
        RxView.clicks(lg_delete)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ARouter.getInstance().build(ARouterConstants.UpdateLeAutActivity)
                            .withObject("stuList",stuAutInfoList)
                            .withObject("teaList",teaAutInfoList)
                            .withInt("type",1)//删除权限
                            .navigation();
                });
        //权限审批
        RxView.clicks(lg_approval)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ARouter.getInstance().build(ARouterConstants.AppLyListActivity)
                            .withObject("ARouterPath",ARouterConstants.ApprovalApplyInfoActivity)
                            .withInt("type",0)//权限审批
                            .navigation();
                });
        //我的申请记录
        RxView.clicks(lg_alyHis)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ARouter.getInstance().build(ARouterConstants.AppLyListActivity)
                            .withObject("ARouterPath",ARouterConstants.ApprovalApplyInfoActivity)
                            .withInt("type",1)//我的申请记录
                            .navigation();
                });
        //历史审批记录
        RxView.clicks(lg_appHis)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ARouter.getInstance().build(ARouterConstants.AppLyListActivity)
                            .withObject("ARouterPath",ARouterConstants.ApprovalApplyInfoActivity)
                            .withInt("type",2)//历史审批记录
                            .navigation();
                });
        setShowLayout();

    }

    private void setShowLayout() {
        if(SharedPreferencesUtil.getInstance().getUserType()==0)//学生
            lg_tea.setVisibility(GONE);
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()<0&&SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()<0)//没有任何审批权限
            lg_delete.setVisibility(GONE);
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()<0){//审批请假学生权限
            tv_stu.setText(R.string.no_authority_des);
            tv_stu.setGravity(Gravity.CENTER);
        }

        //审批请假教师权限
        if(SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()<0){//审批请假学生权限
            tv_tea.setText(R.string.no_authority_des);
            tv_tea.setGravity(Gravity.CENTER);
        }
    }

    /**
     * 添加权限描述
     * @param type 0.审批学生权限 1.审批老师权限
     * @return 权限描述
     */
    private void httpPost(int type) {
        String authorityStr=null;//用户权限
        String [] approverTypes = null;
        int max=0;
//        int position=0;//元素在list中的位置
        if(type==0){//0.审批学生权限
            stuAutInfoList = new ArrayList<>();
            authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();
            max=9;
            approverTypes=getResources().getStringArray(R.array.approverType);
        }else if(type==1){//审批老师权限
            teaAutInfoList = new ArrayList<>();
            authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();
            max = 3;
            approverTypes=getResources().getStringArray(R.array.approverType_2);
        }

        progressHUD.show();
        for(int i = 0 ; i <max;i++){
            if(authorityStr.contains(""+i)){
                if(type==0){
                    stuAutInfoList.add(approverTypes[i]);
                }else if(type==1){
                    teaAutInfoList.add(approverTypes[i]);
                }
                ++requestCount;
                mPersenter.getUserAuthorityDetail(SharedPreferencesUtil.getInstance().getUserId(),type,i);
            }else {
                if (type == 0) {
                    stuAutInfoList.add("");
                } else if (type == 1) {
                    teaAutInfoList.add("");
                }
            }
        }
    }

    /**
     * 获取权限详细信息失败
     * @param msg 失败消息
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            if(--requestCount<=0)
                progressHUD.dismiss();
        }
    }

    /**
     * 获取权限详细信息成功
     * @param data BaseResponse
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getDetailInfo(BaseResponse<List<String>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            StringBuilder content = new StringBuilder();
            List<String> list=data.getData();//详细信息的list
            int type=data.getExtendInfo();//权限类型
            for(int i = 0 ; i<list.size();i++){
                content.append(list.get(i));
                if(i<list.size()-1)content.append(", ");
            }
            int authority = Integer.valueOf(data.getMsg());
            String result=content.toString()+(authority<=4&&type==0?getString(R.string.ban):" ");
            if(type==0) {//学生
                stuAutInfoList.set(Integer.valueOf(data.getMsg()), stuAutInfoList.get(authority)+" "+result);
            }else if(type==1){//教师
                teaAutInfoList.set(Integer.valueOf(data.getMsg()), teaAutInfoList.get(authority)+" "+result);
            }

            if(--requestCount<=0) {//请求全部完成
                showText(type);
                progressHUD.dismiss();
            }

        }
    }

    /**
     * 展示审批的详细结果
     * @param type 0.审批学生 1.审批教师
     */
    private void showText(int type) {
        StringBuilder content= new StringBuilder();
        if(type==0){//审批学生
            for(int i = 0;i<stuAutInfoList.size();i++){
                if(!"".equals(stuAutInfoList.get(i))){//有权限
                    content.append(stuAutInfoList.get(i)).append("\n");
                }
            }
            tv_stu.setText(content.toString().substring(0,content.length()-1));
            tv_stu.setVisibility(VISIBLE);
        }else if(type==1){//审批教师
            for(int i = 0;i<teaAutInfoList.size();i++){
                if(!"".equals(teaAutInfoList.get(i))){//有权限
                    content.append(teaAutInfoList.get(i)).append("\n");
                }
            }
            tv_tea.setText(content.toString().substring(0,content.length()-1));
            tv_tea.setVisibility(VISIBLE);
        }
    }

    @Override
    public LeaveAuthorityPresenter getPersenter() {
        return new LeaveAuthorityPresenter(this);
    }
}
