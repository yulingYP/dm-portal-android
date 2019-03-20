package com.definesys.dmportal.appstore.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EditSendText extends ConstraintLayout {

    @BindView(R.id.icon_del_view_send)
    ImageView icon_delete;
    @BindView(R.id.icon_head_view_send)
    ImageView icon_head;
    @BindView(R.id.et_view_send)
    EditText editText;
    @BindView(R.id.tut_send_view_send)
    TextViewUniversalToast tut;
    @BindView(R.id.separate_line_view_send)
    View separateLine;
    @BindView(R.id.bottom_line_view_send)
    View line;
    /*登录类型
            1       密码登录《==》 按钮隐藏，输入类型为password
            其他  验证码登录《==》 按钮出现，输入类型为text
        */
    private int loginType;
    public static int PASSWORD = 0;
    public static int VERIFY_CODE = 1;


    public EditSendText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_edittext_send, this);
        ButterKnife.bind(this);
        loginType = VERIFY_CODE;
        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {
        RxView.clicks(icon_delete).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> editText.setText(""));

        RxTextView.textChangeEvents(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textViewTextChangeEvent -> {
                    if (textViewTextChangeEvent.text().length() > 0) {
                        icon_delete.setVisibility(View.VISIBLE);
                    } else {
                        icon_delete.setVisibility(View.GONE);
                    }
        });

        RxView.focusChanges(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hasFocus -> {
                    if (hasFocus && editText.getText().toString().length() > 1) {
                        icon_delete.setVisibility(View.VISIBLE);
                    } else {
                        icon_delete.setVisibility(View.GONE);
                    }
                });
    }
    /*
    设置登录类型
     */

    public void setLoginType(int loginType) {
        this.loginType = loginType;
        setText("");
        if (loginType == VERIFY_CODE) {
            setInputNumberWithLength(getResources().getInteger(R.integer.max_code_length));
            tut.setVisibility(VISIBLE);
            separateLine.setVisibility(VISIBLE);
        } else {
            setInputPasswordWithLength(getResources().getInteger(R.integer.max_psw_length));
            tut.setVisibility(GONE);
            separateLine.setVisibility(GONE);
        }
    }

    /*
    按钮文字倒计时的开关
     */
    public void stopCount(boolean flag) {
        tut.stopCount(flag);
    }
    /*
    返回登录类型
     */

    public int getLoginType() {
        return loginType;
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String text) {
        editText.setText(text);
    }


    public TextViewUniversalToast getSendVerifyCodeButton() {
        return this.tut;
    }

    public void setEditTextSize(float size) {
        editText.setTextSize(size);
    }


    public boolean checkPhoneNum(String phoneNum, Context context) {
        return tut.checkPhoneNum(phoneNum, context);
    }

    public void setEditTextColor(int color) {
        editText.setTextColor(color);
    }

    public void setToastTextSize(float size) {
        tut.setTextSize(size);
    }

    public void startCount() {
        tut.startCount();
    }

    //设置图片
    public void setIcon_head(Drawable drawable) {
        icon_head.setImageDrawable(drawable);
    }

    public void setInputType(int type) {
        editText.setInputType(type);
    }

    public void setFilters(InputFilter[] inputFilters) {
        editText.setFilters(inputFilters);
    }

    public void setHint(String str) {
        editText.setHint(str);
    }
    public void setHint(int resId) {
        editText.setHint(resId);
    }
    /**
     * 设置底部线条颜色
     */
    public void setLineColor(int color){
        line.setBackgroundColor(color);
    }

    //对应  验证码/手机号
    public void setInputNumberWithLength(int length) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        setInputLength(length);
    }
    //对应明文显示的任何东西
    public void setInputTextWithLength(int length) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        setInputLength(length);
    }
    //对应密码
    public void setInputPasswordWithLength(int length) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setInputLength(length);
    }

    public void setInputLength(int length){
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
    }

    public void setIcon_delete(Drawable icon_delete) {
        this.icon_delete.setImageDrawable(icon_delete);
    }

    public void setBottomLineBackColor(int color) {
        this.line.setBackgroundColor(color);
    }
    public void setSeparateLineBackColor(int color) {
        this.separateLine.setBackgroundColor(color);
    }
}
