package com.definesys.dmportal.welcomeActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;

@Route(path = ARouterConstants.SplashActivity)
public class SplashActivity extends AppCompatActivity {
private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Main_AppTheme);
        super.onCreate(savedInstanceState);
        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_splash);
        if(getIntent()!=null)
            message = getIntent().getStringExtra("message");
        initView();
    }


    private void initView() {
        // 判断是否是第一次开启应用
        boolean isFirstOpen = SharedPreferencesUtil.getInstance().isFirstOpen();
        // 如果是第一次启动，则先进入功能引导页
        if (isFirstOpen) {
            SharedPreferencesUtil.getInstance().disableFirstOpen();
            ARouter.getInstance().build(ARouterConstants.LoginAcitvity).withBoolean("isFirst",true).navigation(this, new NavCallback() {
                @Override
                public void onArrival(Postcard postcard) {
                    SplashActivity.this.finish();
                }
            });
            return;
        }

        //已经登录
        if (checkExist()) {
            ARouter.getInstance().build(ARouterConstants.MainActivity).withString("message",message).navigation(this, new NavCallback() {
                @Override
                public void onArrival(Postcard postcard) {
                    SplashActivity.this.finish();
                }
            });
            return;
        }
        //没有登录
        ARouter.getInstance().build(ARouterConstants.LoginAcitvity).navigation(this, new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {
                SplashActivity.this.finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /*
      检查本地是否存在用户信息
       */
    private boolean checkExist() {
        return SharedPreferencesUtil.getInstance().getUserId().intValue() > 0 && SharedPreferencesUtil.getInstance().getToken().length()>0;
    }

}

