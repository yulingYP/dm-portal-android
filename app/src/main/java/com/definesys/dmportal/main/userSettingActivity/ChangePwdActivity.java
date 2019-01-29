package com.definesys.dmportal.main.userSettingActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.EditDeleteText;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.ChangePswPresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
@Route(path = ARouterConstants.ChangePwdActivity)
public class ChangePwdActivity extends BaseActivity<ChangePswPresenter> {
    @BindView(R.id.title_bar_att_cp)
    CustomTitleBar titleBar;

    @BindView(R.id.origin_att_cp)
    EditDeleteText e_origin;

    @BindView(R.id.new_att_cp)
    EditDeleteText e_new;

    @BindView(R.id.repeat_att_cp)
    EditDeleteText e_repeat;

    @BindView(R.id.forget_att_cp)
    TextView forget;
    @BindView(R.id.mainview)
    LinearLayout mainview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
        ButterKnife.bind(this);
        initView();
    }
    @SuppressLint("CheckResult")
    private void initView() {
        Resources resource = getResources();

        //设置输入方式
        e_origin.setInputPasswordWithLength(resource.getInteger(R.integer.max_psw_length));
        e_repeat.setInputPasswordWithLength(resource.getInteger(R.integer.max_psw_length));
        e_new.setInputPasswordWithLength(resource.getInteger(R.integer.max_psw_length));

        e_origin.setHint(getString(R.string.psw_input_hint_origin));
        e_repeat.setHint(getString(R.string.psw_input_hint_new_confirm));
        e_new.setHint(getString(R.string.psw_input_hint_new));

        //字体颜色
        e_origin.setEditTextColor(Color.BLACK);
        e_repeat.setEditTextColor(Color.BLACK);
        e_new.setEditTextColor(Color.BLACK);

        //下滑线颜色
        e_origin.setLineColor(Color.BLACK);
        e_repeat.setLineColor(Color.BLACK);
        e_new.setLineColor(Color.BLACK);

        //设置图标
        e_origin.setIcon(resource.getDrawable(R.drawable.my_pwd));
        e_repeat.setIcon(resource.getDrawable(R.drawable.my_pwd));
        e_new.setIcon(resource.getDrawable(R.drawable.my_pwd));
        e_origin.setIcon_delete(resource.getDrawable(R.mipmap.my_delete));
        e_repeat.setIcon_delete(resource.getDrawable(R.mipmap.my_delete));
        e_new.setIcon_delete(resource.getDrawable(R.mipmap.my_delete));
        titleBar.setTitle(R.string.change_pwd);
        titleBar.setBackgroundColor(resource.getColor(R.color.customer_title));

        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> finish());
        /*
        “提交”按钮的点击事件
         */
        RxView.clicks(titleBar.addRightTextButton(R.string.submit, R.id.topbar_right_button))
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    String input_origin = e_origin.getText();
                    String input_new = e_new.getText();
                    String input_repeat = e_repeat.getText();
                    // 有未填项
                    if (input_origin.equals("") || input_new.equals("") || input_repeat.equals("")) {
                        Toast.makeText(ChangePwdActivity.this, R.string.msg_pls_input_required, Toast.LENGTH_LONG).show();
                    }
                    // 密码输入不一致
                    else if (!input_new.equals(input_repeat)) {
                        Toast.makeText(ChangePwdActivity.this, R.string.msg_err_input_psw, Toast.LENGTH_SHORT).show();
                    } else if (input_new.length() < 6) {
                        Toast.makeText(ChangePwdActivity.this, R.string.psw_input_hint_new, Toast.LENGTH_LONG).show();
                    } else {
                        progressHUD.show();
                        mPersenter.changePwd(SharedPreferencesUtil.getInstance().getUserId(),null, input_origin, input_new, null,2);
                    }
                });

        //忘记密码
        RxView.clicks(forget)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                        if("".equals(SharedPreferencesUtil.getInstance().getUserPhone())){
                            Toast.makeText(ChangePwdActivity.this, R.string.bind_phone_tip, Toast.LENGTH_LONG).show();
                            return;
                        }
                        ARouter.getInstance()
                        .build(ARouterConstants.ForgetPwdActivity)
                        .withInt("type",2)
                        .navigation();
                });

    }
    /*
       修改密码发送失败
    */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD
    )
    public void error(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this) {
            progressHUD.dismiss();
            Toast.makeText(ChangePwdActivity.this, "".equals(msg) ? getString(R.string.net_work_error) : msg, Toast.LENGTH_SHORT).show();
        }
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
        Toast.makeText(ChangePwdActivity.this, R.string.change_pwd_success, Toast.LENGTH_SHORT).show();
        finish();
    }
    @Override
    public ChangePswPresenter getPersenter() {
        return new ChangePswPresenter(this);
    }
}
