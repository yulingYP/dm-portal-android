package com.definesys.dmportal.appstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

@Route(path = ARouterConstants.SubjectTableActivity)
public class SubjectTableActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_table);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.subject_table));
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
    }

    @Override
    public BasePresenter getPersenter() {
        return new BasePresenter(this) {
            @Override
            public void subscribe() {
                super.subscribe();
            }
        };
    }
}
