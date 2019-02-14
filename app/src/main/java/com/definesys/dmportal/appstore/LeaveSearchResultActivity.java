package com.definesys.dmportal.appstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.presenter.GetLeaveInfoHistoryPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.LeaveSearchResultActivity)
public class LeaveSearchResultActivity extends BaseActivity<GetLeaveInfoHistoryPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @Autowired(name = "type")
    int type;//页面类型 0.历史请假记录 1.待处理的审批记录 2.历史审批记录

    @Autowired(name = "isAll")
    Boolean isAll;

    @Autowired(name = "checkCode")
    int checkCode;

    @Autowired(name = "content")
    String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_search_result);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.search_result));
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
    }
    @Override
    public GetLeaveInfoHistoryPresenter getPersenter() {
        return new GetLeaveInfoHistoryPresenter(this);
    }
}
