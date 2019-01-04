package com.definesys.dmportal.appstore.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.MainIconAdapter;
import com.definesys.dmportal.appstore.bean.MainIcon;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;
@Route(path = ARouterConstants.LeaveMainActivity)
public class LeaveMainActivity extends AppCompatActivity {
    @BindView(R.id.title_bar)
     CustomTitleBar titleBar;

    @BindView(R.id.leave_layout)
    LinearLayout lg_leave;

    @BindView(R.id.confirm_layout)
    LinearLayout lg_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_main);
        ButterKnife.bind(this);
        initView();
    }
    private void initView() {
        titleBar.setTitle(getString(R.string.leave_off));
        titleBar.setBackgroundDividerEnabled(false);
        //titleBar.setBackground(null);
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED,intent);
                        finish();
                    }
                });

        RxView.clicks(lg_leave)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ARouter.getInstance()
                                .build(ARouterConstants.LeaveActivity)
                                .navigation();
                    }
                });
    }

}
