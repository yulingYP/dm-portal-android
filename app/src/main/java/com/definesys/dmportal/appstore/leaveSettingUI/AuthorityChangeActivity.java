package com.definesys.dmportal.appstore.leaveSettingUI;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.presenter.LeaveAuthorityPresenter;
import com.definesys.dmportal.appstore.tempEntity.AuthorityDetail;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

@Route(path = ARouterConstants.AuthorityChangeActivity)
public class AuthorityChangeActivity extends BaseActivity<LeaveAuthorityPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    private HashMap<Integer,String> autMap;//用户已有的全部权限
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_change);
        initView();
    }
    private void initView() {
        titleBar.setTitle(getString(R.string.delete_authority));
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                        finish()
                );
        Button button = titleBar.addRightTextButton(getString(R.string.submit), R.layout.activity_update_le_aut);
        button.setTextSize(14);
        //提交
        RxView.clicks(button)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                        checkSelect()
                );
        getMyAuthorityDetail();
    }

    /** 获取权限详细信息成功
     * @param data BaseResponse
    */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getAllDetailInfoByBean(BaseResponse<List<AuthorityDetail>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            List<AuthorityDetail> list=data.getData();//详细信息的list
            int authority;
            if(list!=null&&list.size()>0) {//有数据
                for (int i = 0; i < list.size(); i++) {
                    authority = list.get(i).getUserAuthority();
                    if (autMap.get(authority) != null && !"".equals(autMap.get(authority)))
                        autMap.put(authority, autMap.get(authority) + list.get(i).getRegion() + ", ");
                    else
                        autMap.put(authority, list.get(i).getRegion() + ", ");
                }
            }
        }
    }

    /**
     * 获取申请信息列表失败
     * @param msg 失败消息
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            progressHUD.dismiss();
            Toast.makeText(this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
        }
    }
    //获取我的详细权限
    @SuppressLint("UseSparseArrays")
    private void getMyAuthorityDetail() {
        autMap = new HashMap<>();
        List<String> autList = new ArrayList<>();
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()>=0){//有审批学生的权限
            String authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();//审批学生权限
            for(int i = 0 ; i <8;i++){
                if(authorityStr.contains(""+i)){
                    autList.add(""+i);
                }
            }
        }
        if(SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()>=0){//有审批教师的权限
            String authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();//审批教师权限
            for(int i = 0 ; i <8;i++){
                if(authorityStr.contains(""+i)){
                    autList.add(""+(i+10));
                }
            }
        }
        mPersenter.getUserAuthorityDetail(SharedPreferencesUtil.getInstance().getUserId(),3,autList,true);
    }
    private void checkSelect() {
    }


    @Override
    public LeaveAuthorityPresenter getPersenter() {
        return new LeaveAuthorityPresenter(this);
    }


}
