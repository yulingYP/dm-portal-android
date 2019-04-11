package com.definesys.dmportal.main.userSettingActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.EditDeleteText;
import com.definesys.dmportal.appstore.customViews.EditSendText;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.ChangePswPresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.SendCodePresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ARouterConstants.ForgetPwdActivity)
public class ForgetPwdActivity extends BaseActivity<ChangePswPresenter> {

    @BindView(R.id.title_bar_att_forget)
    CustomTitleBar titleBar;

    @BindView(R.id.phone_att_forget)
    EditDeleteText phone;

    @BindView(R.id.code_att_forget)
    EditSendText code;

    @BindView(R.id.pwd_att_forget)
    EditDeleteText pwd;

    @BindView(R.id.pwd2_att_forget)
    EditDeleteText pwd2;

    @Autowired(name = "type")
    int type;//1.登陆前忘记密码 2.登陆后修改密码

    private String phoneNumber;//手机号
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }


    @SuppressLint("CheckResult")
    private void initView() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Resources resources = getResources();
        //设置图标
        code.setIcon_head(resources.getDrawable(R.drawable.mail));
        pwd.setIcon(resources.getDrawable(R.drawable.my_pwd));
        pwd2.setIcon(resources.getDrawable(R.drawable.my_pwd));
        phone.setIcon(resources.getDrawable(R.drawable.my_phone));
        code.setIcon_delete(resources.getDrawable(R.mipmap.my_delete));
        pwd.setIcon_delete(resources.getDrawable(R.mipmap.my_delete));
        pwd2.setIcon_delete(resources.getDrawable(R.mipmap.my_delete));
        phone.setIcon_delete(resources.getDrawable(R.mipmap.my_delete));

        code.setHint(R.string.msg_pls_input_code);
        pwd.setHint(getString(R.string.psw_input_hint_new));
        pwd2.setHint(getString(R.string.psw_input_hint_confirm));
        code.setEditTextColor(Color.BLACK);
        code.setBottomLineBackColor(Color.BLACK);
        code.setSeparateLineBackColor(Color.BLACK);
        pwd.setEditTextColor(Color.BLACK);
        pwd2.setEditTextColor(Color.BLACK);
        phone.setEditTextColor(Color.BLACK);
        pwd.setLineColor(Color.BLACK);
        pwd2.setLineColor(Color.BLACK);
        phone.setLineColor(Color.BLACK);
        //输入方式
        code.setLoginType(EditSendText.VERIFY_CODE);
        pwd.setInputPasswordWithLength(resources.getInteger(R.integer.max_psw_length));
        pwd2.setInputPasswordWithLength(resources.getInteger(R.integer.max_psw_length));

        titleBar.setTitle(R.string.change_pwd);
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));

        if(type==2) {
            //自动填入手机号并禁用该组件
            phone.setText(SharedPreferencesUtil.getInstance().getUserPhone());
            phone.setEditDisabled(true);
        }else {
            phone.setHint(R.string.msg_pls_input_phone);

        }

        //获取验证码控件样式
        code.getSendVerifyCodeButton().setTextColorBlack();
        code.getSendVerifyCodeButton().setTextBackNull();
        code.setToastTextSize(14);

        /*
        "返回"箭头的点击事件
         */
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> finish());

         /*
        ”发送验证码“按钮的点击事件
         */
        RxView.clicks(code.getSendVerifyCodeButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                    String phoneNum = phone.getText();
                    if (code.checkPhoneNum(phoneNum, ForgetPwdActivity.this)) {
                        progressHUD.show();
                        new SendCodePresenter(ForgetPwdActivity.this).sendVerifyCode(phoneNum,2,null);
                    } else {
                        Toast.makeText(code.getContext(), R.string.msg_err_phone, Toast.LENGTH_SHORT).show();
                    }
                });
        /*
        "提交“的点击事件
         */
        RxView.clicks(titleBar.addRightTextButton(R.string.submit, R.id.topbar_right_button))
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

                    String temp_phone = phone.getText();
                    String temp_code = code.getText();
                    String temp_pwd = pwd.getText();
                    String temp_pwd2 = pwd2.getText();
                    if (temp_code.equals("") || temp_pwd.equals("") || temp_pwd2.equals("")||temp_phone.equals("")) {
                        Toast.makeText(ForgetPwdActivity.this, R.string.msg_pls_input_required, Toast.LENGTH_LONG).show();
                    } else if(phoneNumber==null){
                        Toast.makeText(code.getContext(), R.string.send_code_err, Toast.LENGTH_SHORT).show();
                    } else if(!code.checkPhoneNum(temp_phone, ForgetPwdActivity.this)){
                        Toast.makeText(code.getContext(), R.string.msg_err_phone, Toast.LENGTH_SHORT).show();
                    } else if (!temp_pwd.equals(temp_pwd2)) {
                        Toast.makeText(ForgetPwdActivity.this, R.string.msg_err_input_psw, Toast.LENGTH_LONG).show();
                    } else if (temp_pwd.length() < 6) {
                        Toast.makeText(ForgetPwdActivity.this, R.string.psw_input_hint_new, Toast.LENGTH_LONG).show();
                    } else if(!phoneNumber.equals(temp_phone)) {
                        Toast.makeText(code.getContext(), R.string.msg_err_phone_2, Toast.LENGTH_SHORT).show();
                    } else {
                        progressHUD.show();
                        mPersenter.changePwd(null,temp_phone, "", temp_pwd, temp_code,1);
                    }
                });
    }

    /*
     修改密码成功
  */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_CHANGE_PASSWORD)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulChangePsw(String msg) {
        progressHUD.dismiss();
        Toast.makeText(ForgetPwdActivity.this, R.string.change_pwd_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    /*
        验证码发送成功
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_SEND_CODE)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulSendCode(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this) {
            phoneNumber = phone.getText();
            code.startCount();
            progressHUD.dismiss();
        }
    }

    /*
        修改密码或验证码发送失败
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD
    )
    public void error(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this) {
            progressHUD.dismiss();
            Toast.makeText(ForgetPwdActivity.this, "".equals(msg) ? getString(R.string.net_work_error) : msg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public ChangePswPresenter getPersenter() {
        return new ChangePswPresenter(this);
    }
}
