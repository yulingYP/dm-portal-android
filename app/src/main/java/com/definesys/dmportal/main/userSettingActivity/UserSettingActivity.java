package com.definesys.dmportal.main.userSettingActivity;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.presenter.LogoutPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ARouterConstants.UserSettingActivity)
public class UserSettingActivity extends BaseActivity {


    @BindView(R.id.title_bar_att_us)
    CustomTitleBar titleBar;

    @BindView(R.id.switch_sound)
    Switch sw_sound;

    @BindView(R.id.switch_vibrate)
    Switch sw_vibrate;

    @BindView(R.id.logout_layout)
    LinearLayout lg_logout;

    @BindView(R.id.change_pwd_layout)
    LinearLayout lg_pwd;

    @BindView(R.id.bind_phone_layout)
    LinearLayout lg_phone;

    @BindView(R.id.about_layout)
    LinearLayout lg_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setTitle(getString(R.string.setting));
        //退出
        titleBar.addLeftBackImageButton().setOnClickListener((view) -> finish());

        //修改密码
        RxView.clicks(lg_pwd)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                        ARouter.getInstance().build(ARouterConstants.ChangePwdActivity).navigation()
                );
        //手机绑定
        RxView.clicks(lg_phone)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                        ARouter.getInstance()
                                .build(ARouterConstants.PhoneBindActivity)
                                .withBoolean("isBind",!"".equals(SharedPreferencesUtil.getInstance().getUserPhone()))
                                .navigation()
                );
        //关于我们
        RxView.clicks(lg_about)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
//                        ARouter.getInstance().build(ARouterConstants.UserSettingActivity).navigation()
                        }
                );
        //退出登录
        RxView.clicks(lg_logout).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                        new AlertDialog.Builder(this)
                                .setMessage(R.string.logout_msg)
                                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                                    new LogoutPresenter(this).logout( SharedPreferencesUtil.getInstance().getUserId());
                                    ARouter.getInstance().build(ARouterConstants.MainActivity).withBoolean(getString(R.string.exit_en), true).navigation(UserSettingActivity.this);

                                }).create().show()
                );

        getConfig();
        sw_sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyCongfig.musicOpen(UserSettingActivity.this,true);
            }
        });
        sw_vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyCongfig.vibratorOpen(UserSettingActivity.this);
            }
        });
    }

    /**
     * 退出时保存设置
     */
    public void setConfig(){
        //提醒模式
        int mode =0;
        if(sw_vibrate.isChecked())
            mode+=1;
        if(sw_sound.isChecked())
            mode+=2;
        MyCongfig.remindMode = mode;
        SharedPreferencesUtil.getInstance().setUserSetting(mode);
    }
    public void getConfig(){

        if(MyCongfig.remindMode==1) {
            sw_vibrate.setChecked(true);
            sw_sound.setChecked(false);
        }
        else if(MyCongfig.remindMode==2) {
            sw_vibrate.setChecked(false);
            sw_sound.setChecked(true);
        }
        else if(MyCongfig.remindMode==3){
            sw_vibrate.setChecked(true);
            sw_sound.setChecked(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        setConfig();
    }

    @Override
    public BasePresenter getPersenter() {
        return new BasePresenter(this) {
            @Override
            public void subscribe() {
                super.subscribe();
            }
        };
    }
}
