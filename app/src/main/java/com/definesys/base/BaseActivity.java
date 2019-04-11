package com.definesys.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.appstore.utils.SystemUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.kaopiz.kprogresshud.KProgressHUD;


/**
 *
 * Created by mobile on 2018/8/20.
 */

public abstract class BaseActivity<T extends BasePresenter>extends AppCompatActivity {

    public static final String ACTIVITY_FINISH = "BaseActivity.finishActivity";
    public KProgressHUD progressHUD;
    protected T mPersenter ;

//    protected KeyboardWatcher keyboardWatcher ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 强制竖屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mPersenter = getPersenter();
        SmecRxBus.get().register(this);
        progressHUD = KProgressHUD.create(this).setLabel("loading...")
//                .setBackgroundColor(Color.parseColor("#00000000"))//透明背景
                .setAnimationSpeed(1)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f);
        //隐藏状态栏
        SystemUtil.setStatusBarFullTransparent(this);
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
