package com.definesys.dmportal.appstore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.LeaveInfoListAdapter;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.presenter.ApplyInfoPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.AppLyListActivity)
public class AppLyListActivity extends BaseActivity<ApplyInfoPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.no_layout)
    LinearLayout lg_no;//暂无记录或网络

    @BindView(R.id.no_img)
    ImageView img_no;//暂无记录或网络

    @BindView(R.id.no_text)
    TextView tv_no;//暂无记录或网络

    @BindView(R.id.history_list_view)
    RecyclerView recyclerView;

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout smartRefreshLayout;

    @Autowired(name = "type")
    int type;//页面类型 0.权限审批 1.历史申请记录 2.历史审批记录

    @Autowired(name = "ARouterPath")
    String ARouterPath;

    private int requestPage;//请求的页码
    private List<ApplyInfo> applyInfoList;//请假记录列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_ly_list);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();

    }



    private void initView() {
//        if(type==0||type==1||type==3)
//            submitLeaveInfoList = new ArrayList<>();
//        else
//            approvalRecordList = new ArrayList<>();

        titleBar.setTitle(setMyTitle());
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    finish();
                });
        //下拉刷新监听
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestPage = 1;
                httpPost();
            }
        });
        //加载更多
        smartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                ++requestPage;
                httpPost();
            }
        });

        //隐藏暂无页面
        lg_no.setVisibility(View.GONE);
        smartRefreshLayout.autoRefresh();
    }

    /**
     * 设置该页面标题
     * @return
     */
    private int setMyTitle() {

        if(type==0) {
            return R.string.approval_list;
        }
        else if(type == 1) {

            return R.string.apply_history_list;
        }
        else if(type==2) {
            return R.string.approval_history_list;
        }
        return R.string.no_title;
    }

    private void httpPost() {
    }

    @Override
    public ApplyInfoPresenter getPersenter() {
        return new ApplyInfoPresenter(this);
    }
}
