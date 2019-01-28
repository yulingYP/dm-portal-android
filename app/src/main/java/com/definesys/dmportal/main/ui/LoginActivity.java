package com.definesys.dmportal.main.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.definesys.dmportal.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.MainApplication;
import com.definesys.dmportal.appstore.customViews.EditDeleteText;
import com.definesys.dmportal.appstore.customViews.EditSendText;
import com.definesys.dmportal.appstore.customViews.ReasonTypeListLayout;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.LoginPresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.SendCodePresenter;
import com.definesys.dmportal.main.util.HddLayoutHeight;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.vise.xsnow.http.mode.ApiCode;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ARouterConstants.LoginAcitvity)
public class LoginActivity extends BaseActivity<LoginPresenter> {

    @BindView(R.id.tel_edit_att_login)
    EditDeleteText inputTel;
    @BindView(R.id.pwd_edit_att_login)
    EditSendText inputPwd;
    @BindView(R.id.text_reg_att_log)
    TextView textReg;
    @BindView(R.id.text_log_att_log)
    TextView textLogin;
    @BindView(R.id.login_btn_att_log)
    Button btn;

    String userPhoneNumber;
    Number userId;

    @BindView(R.id.mainview)
    View main;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStatusBarFullTransparent();
        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
        initView();
    }

    /*
        验证码发送成功
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_SEND_CODE_LOGIN)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulSendCode(String msg) {
        inputPwd.startCount();
        progressHUD.dismiss();
    }

    /*
        验证码发送失败
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD
    )
    public void errorSendCode(String msg) {
        progressHUD.dismiss();
        inputPwd.stopCount(false);
        Toast.makeText(LoginActivity.this, "".equals(msg)?getString(R.string.net_work_error):msg, Toast.LENGTH_SHORT).show();
    }
    /*
        登录成功
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_LOGIN_USER)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulLogin(String msg) {
        progressHUD.dismiss();
        //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
        ARouter.getInstance().build(ARouterConstants.MainActivity)
                .navigation(this, new NavCallback() {
                    @Override
                    public void onArrival(Postcard postcard) {
                        LoginActivity.this.finish();
                    }
                });

    }

    @SuppressLint("CheckResult")
    private void initView() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //设置输入类型
        Resources resources = this.getResources();
        inputTel.setInputNumberWithLength(resources.getInteger(R.integer.max_phone_length));
        inputTel.setHint(R.string.msg_pls_input_userId);
        inputTel.setIcon(resources.getDrawable(R.drawable.phone));

        inputPwd.setLoginType(EditSendText.PASSWORD);
        inputPwd.setHint(R.string.msg_pls_input_psw);
        inputPwd.setToastTextSize(14);

        // 点击切换登陆方式
        textLogin.setOnClickListener(view -> {

            if (textLogin.getText().toString().equals(getString(R.string.login_text_code))) {
                textLogin.setText(R.string.login_text_pwd);
                inputTel.setHint(R.string.msg_pls_input_phone);
                inputTel.setText(userPhoneNumber);
                inputPwd.setLoginType(EditSendText.VERIFY_CODE);
                inputPwd.setHint(R.string.msg_pls_input_code);
            } else {
                textLogin.setText(R.string.login_text_code);
                inputTel.setText(userId.intValue()>0?""+userId:"");
                inputTel.setHint(R.string.msg_pls_input_userId);
                inputPwd.setLoginType(EditSendText.PASSWORD);
                inputPwd.setHint(R.string.msg_pls_input_psw);
            }
        });

//      ”注册“点击事件
//        RxView.clicks(textReg).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(o -> ARouter.getInstance().build(ARouterConstants.RegisterActivity).navigation());

        /*
        ”发送验证码“按钮的点击事件
         */
        RxView.clicks(inputPwd.getSendVerifyCodeButton()).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    String phoneNum = inputTel.getText();
                    if (inputPwd.checkPhoneNum(phoneNum, LoginActivity.this)) {
                        progressHUD.show();
                        new SendCodePresenter(LoginActivity.this).sendVerifyCodeForLogin(phoneNum,1);
                    } else {
                        Toast.makeText(inputPwd.getContext(), R.string.msg_err_phone, Toast.LENGTH_SHORT).show();
                    }
                });

        /*
        “登录”按钮的点击事件
         */
        RxView.clicks(btn)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    int type = inputPwd.getLoginType();
                    if (type==EditSendText.VERIFY_CODE&&"".equals(inputTel.getText())) {
                        Toast.makeText(LoginActivity.this, getString(R.string.msg_pls_input_phone), Toast.LENGTH_SHORT).show();
                    } else if (type==EditSendText.VERIFY_CODE&&!inputPwd.checkPhoneNum(inputTel.getText(), LoginActivity.this)) {
                        Toast.makeText(LoginActivity.this, getString(R.string.msg_err_phone), Toast.LENGTH_SHORT).show();
                    } else {
                        String pwd = inputPwd.getText();

                        if (pwd.equals("")) {
                            if (type == EditSendText.PASSWORD) {
                                Toast.makeText(LoginActivity.this, getString(R.string.msg_pls_input_psw), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.msg_pls_input_code), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if(type==EditSendText.PASSWORD)userId= Integer.valueOf(inputTel.getText());
                            else userPhoneNumber = inputTel.getText();
                            progressHUD.show();
                            mPersenter.userLogin(userId,userPhoneNumber, pwd, type);
                        }
                    }
                });

        userPhoneNumber = SharedPreferencesUtil.getInstance().getUserPhone();
        userId = SharedPreferencesUtil.getInstance().getUserId();
        inputTel.setText(userId.intValue()>0?""+userId:"");
        inputPwd.getSendVerifyCodeButton().setTextBackNull();
        // 防遮挡
        new HddLayoutHeight().addLayoutListener(main, textReg);
    }

    //TODO 为方便调试设置的点击自动登录
    @OnClick(R.id.copyrt_att_log)
    public void onClick() {
        this.inputTel.setText("151110401");
        this.inputPwd.setText("123456");
        this.btn.callOnClick();
    }

    @OnLongClick(R.id.copyrt_att_log)
    public boolean onLongClick() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(new long[]{0,30},-1);
        }
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText url = new EditText(LoginActivity.this);
        layout.addView(url);
        //MainApplication application = (MainApplication) getApplication();
        url.setText(HttpConst.url);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("设置请求网址").setView(layout)
                .setNegativeButton("少废话,直接进", (dialogInterface, i) -> ARouter.getInstance().build(ARouterConstants.MainActivity).navigation())
                .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                    //application.setUrl(url.getText().toString());
                }).show();
        return true;

    }

    @Override
    protected void onDestroy() {
        inputPwd.stopCount(false);
        super.onDestroy();
    }

    @Override
    public LoginPresenter getPersenter() {
        return new LoginPresenter(this);
    }


//    /*
//     注册成功
//     */
//    @Subscribe(tags = {
//            @Tag(MainPresenter.SUCCESSFUL_REGISTER_USER)
//    }, thread = EventThread.MAIN_THREAD)
//    public void successfulRegister(ResultBean<LoginBean> data) {
//        finish();
//    }
}
