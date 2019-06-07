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

public class EditDeleteText extends ConstraintLayout {
    @BindView(R.id.icon_head_view_delete)
    ImageView icon_head;
    @BindView(R.id.et_view_delete)
    EditText editText;
    @BindView(R.id.icon_del_view_delete)
    ImageView icon_delete;
    @BindView(R.id.bottom_line_view_delete)
    View line;

    public EditDeleteText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_edittext_delete, this);
        ButterKnife.bind(this);
        initView();
    }

    @SuppressLint("CheckResult")
    private void initView() {

        RxTextView.textChangeEvents(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textViewTextChangeEvent -> {
            if (textViewTextChangeEvent.text().length() > 0 && editText.isEnabled()) {
                icon_delete.setVisibility(View.VISIBLE);
            } else {
                icon_delete.setVisibility(View.GONE);
            }
        });

        RxView.clicks(icon_delete).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> editText.setText(""));

        RxView.focusChanges(editText)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hasFocus -> {
                    if (hasFocus && editText.getText().toString().length() > 0 && editText.isEnabled()) {
                        icon_delete.setVisibility(VISIBLE);
                    } else {
                        icon_delete.setVisibility(GONE);
                    }
                });
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String str) {
        editText.setText(str);
    }

    public void setHint(String str) {
        editText.setHint(str);
    }
    public void setHint(int resId) {
        editText.setHint(resId);
    }

    //设置输入类型
    public void setInputType(int type) {
        editText.setInputType(type);
    }

    //设置输入类型及长度

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
    //设置过滤条件（输入长度限制）
    public void setFilters(InputFilter[] inputFilters) {
        editText.setFilters(inputFilters);
    }
    //设置图标
    public void setIcon(Drawable drawable) {
        icon_head.setImageDrawable(drawable);
    }

    public void setEditDisabled(boolean disabled){
        if(disabled) {
            icon_delete.setVisibility(GONE);
        }else{
            icon_delete.setVisibility(VISIBLE);
        }
        editText.setEnabled(!disabled);
    }
    public void setEditTextColor(int color) {
        editText.setTextColor(color);
    }
    /**
     * 设置底部线条颜色
     */
    public void setLineColor(int color){
        line.setBackgroundColor(color);
    }

    public void setIcon_delete(Drawable icon_delete) {
        this.icon_delete.setImageDrawable(icon_delete);
    }
}
