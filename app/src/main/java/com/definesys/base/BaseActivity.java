package com.definesys.base;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.definesys.dmportal.MyActivityManager;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kaopiz.kprogresshud.KProgressHUD;

import static android.os.Build.VERSION.SDK;

/**
 * Created by mobile on 2018/8/20.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {

    public static final String ACTIVITY_FINISH = "BaseActivity.finishActivity";
    public KProgressHUD progressHUD;
    protected T mPersenter ;

//    protected KeyboardWatcher keyboardWatcher ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // 强制竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPersenter = getPersenter();
        SmecRxBus.get().register(this);
        progressHUD = KProgressHUD.create(this).setLabel("loading...")
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
//        keyboardWatcher = new KeyboardWatcher(this);
//        keyboardWatcher.setListener(this);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SmecRxBus.get().unregister(this);
//        keyboardWatcher.destroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPersenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPersenter.unsubscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyActivityManager.getInstance().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public abstract T getPersenter();

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {
                    @Tag(BaseActivity.ACTIVITY_FINISH)
            }
    )
    public void finishActivity(String activityClass){
        if (this.getClass().getName().equals(activityClass) || "ALL".equals(activityClass)) {
            this.finish();
        }
    }

}
