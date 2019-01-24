package com.definesys.dmportal.appstore.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.definesys.dmportal.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TextViewUniversalToast extends ConstraintLayout {

    @BindView(R.id.text_view_tut)
    TextView textView;
    @BindView(R.id.toast_layout)
    LinearLayout toastLayout;

    private int msgTime;
    private Boolean threadFlag;
    private String textDisplayed;

    public TextViewUniversalToast(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_text_universal_toast, this);
        ButterKnife.bind(this);
        msgTime = getResources().getInteger(R.integer.code_send_time);
        textDisplayed = getResources().getString(R.string.text_after_send_code);
    }

    /*
        文本倒计时功能
     */
    @SuppressLint("CheckResult")
    public void startCount() {

        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            int msgTime_bk = msgTime;       //复制当前设置的秒数
            threadFlag = true;
            //满足条件  循环 onNext
            while (msgTime_bk > 0 && threadFlag) {
                emitter.onNext(msgTime_bk--);
                Thread.sleep(1000);
            }
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> textView.setEnabled(false))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        textView.setText(getResources().getString(R.string.tut_toast_text_time,integer));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        textView.setText(textDisplayed);
                        textView.setEnabled(true);
                    }
                });
    }

    /*
        设置文字大小
     */
    public void setTextSize(float size) {
        textView.setTextSize(size);
    }

    /*
        设置倒计时时长
     */
    public void setMsgTime(int Seconds) {
        this.msgTime = Seconds;
    }

    /*
        手机号码校验
     */
    public boolean checkPhoneNum(String phoneNum, Context context) {
        if (phoneNum.matches(context.getString(R.string.phone_regex)))
            return true;
        return false;
    }

    /*
        点击事件
     */
    public void setOnClickListener(OnClickListener listener) {
        textView.setOnClickListener(listener);
    }

    /*
        设置FLag
     */
    public void stopCount(Boolean stop) {
        this.threadFlag = !stop;
    }

    /*
        设置背景
     */
    public void setBackGround(int resId) {
        textView.setBackgroundResource(resId);
    }

    /*
        设置显示文本
     */
    public void setTextDisplayed(String textDisplayed) {
        this.textDisplayed = textDisplayed;
        textView.setText(textDisplayed);
    }

    public void setTextColorBlack(){
        textView.setTextColor(Color.BLACK);
        toastLayout.setBackgroundResource(R.drawable.toast_back);
    }
    public void setTextBackNull(){
        toastLayout.setBackground(null);
    }

}
