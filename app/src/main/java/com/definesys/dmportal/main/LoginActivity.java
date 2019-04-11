package com.definesys.dmportal.main;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.appstore.customViews.EditDeleteText;
import com.definesys.dmportal.appstore.customViews.EditSendText;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.SystemUtil;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.presenter.LoginPresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.SendCodePresenter;
import com.definesys.dmportal.main.util.HddLayoutHeight;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.vise.xsnow.permission.RxPermissions;
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
    @BindView(R.id.text_forget_att_log)
    TextView textForget;
    @BindView(R.id.text_log_att_log)
    TextView textLogin;
    @BindView(R.id.login_btn_att_log)
    Button btn;


    private String userPhoneNumber;//电话号码
    private Number userId;//用户id

    @BindView(R.id.mainview)
    View main;

    @Autowired(name = "isFirst")
    boolean isFirst;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
        SystemUtil.setStatusBarFullTransparent(this);
        if(isFirst)//第一次使用APP？
            requestPermissions();
        MyCongfig.isShowing = false;
        initView();
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
            inputPwd.startCount();
            progressHUD.dismiss();
        }
    }

    /*
        验证码发送失败
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD
    )
    public void errorSendCode(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this) {
            progressHUD.dismiss();
            inputPwd.stopCount(false);
            Toast.makeText(LoginActivity.this, "".equals(msg) ? getString(R.string.net_work_error) : msg, Toast.LENGTH_SHORT).show();
        }
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
                inputPwd.setIcon_head(resources.getDrawable(R.drawable.code));
            } else {
                textLogin.setText(R.string.login_text_code);
                inputTel.setText(userId.intValue()>0?""+userId:"");
                inputTel.setHint(R.string.msg_pls_input_userId);
                inputPwd.setLoginType(EditSendText.PASSWORD);
                inputPwd.setHint(R.string.msg_pls_input_psw);
                inputPwd.setIcon_head(resources.getDrawable(R.drawable.pwd));
            }
        });

//      ”忘记密码“点击事件
        RxView.clicks(textForget).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> ARouter.getInstance().build(ARouterConstants.ForgetPwdActivity).withInt("type",1).navigation());

        /*
        ”发送验证码“按钮的点击事件
         */
        RxView.clicks(inputPwd.getSendVerifyCodeButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    String phoneNum = inputTel.getText();
                    if (inputPwd.checkPhoneNum(phoneNum, LoginActivity.this)) {
                        progressHUD.show();
                        new SendCodePresenter(LoginActivity.this).sendVerifyCode(phoneNum,1,null);
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
        new HddLayoutHeight().addLayoutListener(this,main, textForget);
    }

    //TODO 为方便调试设置的点击自动登录
    @OnClick(R.id.copyrt_att_log)
    public void onClick() {
        this.inputTel.setText("151110401");
        this.inputPwd.setText("123456");
        this.btn.callOnClick();
    }
    //TODO 为方便调试设置的点击自动登录
    @OnLongClick(R.id.copyrt_att_log)
    public boolean onLongClick() {
        this.inputTel.setText("100000001");
        this.inputPwd.setText("123456");
        this.btn.callOnClick();
        return true;
    }
    //TODO 为方便调试设置的点击自动登录
    @OnClick(R.id.app_img_att_login)
    public void onIconClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText editText = new EditText(this);
        editText.setHint("请输入新的ip地址");
        builder.setView(editText)
                .setTitle("URL修改")
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    if("".equals(editText.getText().toString())){
                        Toast.makeText(this,"url不能为空",Toast.LENGTH_SHORT).show();
                    }else {
                        SharedPreferencesUtil.getInstance().setUrl(editText.getText().toString().trim());
                        Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel,null)
                .create()
                .show();

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
    //申请权限
    private void requestPermissions() {
//        JPushInterface.requestPermission(this);
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        // 用户已经同意该权限
                        Log.d("mydemo", permission.name + " is granted.");
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        Log.d("mydemo", permission.name + " is denied. More info should be provided.");
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        Log.d("mydemo", permission.name + " is denied.");
                    }
                });
    }

    /*
 点按两次退出
  */
    private boolean mIsExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                System.exit(0);
            } else {
                Toast.makeText(this, R.string.exit_2s,Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(() -> mIsExit = false, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
