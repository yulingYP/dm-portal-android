package com.definesys.dmportal.appstore.ui.leaveSetting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.LinePathView;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.ImageUntil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.SignatureActivity)
public class SignatureActivity extends AppCompatActivity {
    @BindView(R.id.back_icon)
    ImageView iv_back;
    @BindView(R.id.sign_view)
    LinePathView lg_sign;
    @BindView(R.id.clear_layout)
    LinearLayout lg_clear;
    @BindView(R.id.save_layout)
    LinearLayout lg_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        //退出
        RxView.clicks(iv_back)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    setResult(RESULT_CANCELED);
                    finish();
                });
        //清除
        RxView.clicks(lg_clear)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o ->
                   lg_sign.clear()
                );
        //保存
        RxView.clicks(lg_save)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if(lg_sign.getTouched()) {
                        Intent intent = new Intent();
                        intent.putExtra("path", ImageUntil.saveBitmapFromView(lg_sign.clearBlank(lg_sign.reDraw(), 10), UUID.randomUUID().toString(), this, 4));
                        setResult(RESULT_OK, intent);
                        finish();
                    }else
                        Toast.makeText(this, R.string.sign_text,Toast.LENGTH_SHORT).show();

                });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //设置横屏
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


}
