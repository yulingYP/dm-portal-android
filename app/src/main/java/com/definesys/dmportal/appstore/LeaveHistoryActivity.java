package com.definesys.dmportal.appstore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.LeaveInfoListAdapter;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.bean.SubmitLeaveInfo;
import com.definesys.dmportal.appstore.presenter.GetLeaveInfoHistoryPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.appstore.utils.PermissionsUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.prim.primweb.core.permission.PermissionCheckUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.vise.xsnow.http.ViseHttp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

@Route(path = ARouterConstants.LeaveHistoryActivity)
public class LeaveHistoryActivity extends BaseActivity<GetLeaveInfoHistoryPresenter> {
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

    List<SubmitLeaveInfo> submitLeaveInfoList;//历史记录列表
    LeaveInfoListAdapter leaveInfoListAdapter;//适配器

    @Autowired(name = "userId")
    int userId;//要查询的id
    @Autowired(name = "type")
    int type;//适配器的类型

    private int requestPage;//请求的页码


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_history);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
    }

    private void initView() {
        submitLeaveInfoList = new ArrayList<>();
        titleBar.setTitle(getString(R.string.leave_history));
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
        //下拉刷新监听
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestPage = 1;
                if(submitLeaveInfoList.size()>0) {
                    submitLeaveInfoList.clear();
                    leaveInfoListAdapter.notifyDataSetChanged();
                }
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

    //清空记录，发送网络请求
    private void httpPost() {

        if(!PermissionsUtil.isNetworkConnected(this)){
            smartRefreshLayout.finishRefresh(false);
            setNoLayout(2);
            return;
        }
        mPersenter.getTableInfo(userId,requestPage);
    }

    /**
     *  设置暂无类页面
     * @param type 0.隐藏暂无页面 1.暂无记录 2.暂无网络
     */
    private void setNoLayout(int type) {
       if(type==0) {
           lg_no.setVisibility(View.GONE);
       }
       else if(type==1){
           lg_no.setVisibility(View.VISIBLE);
           img_no.setImageResource(R.mipmap.no_history);
           tv_no.setText(R.string.no_history_tip);
       }
       else if(type==2){
           lg_no.setVisibility(View.VISIBLE);
           img_no.setImageResource(R.mipmap.nointernet);
           tv_no.setText(R.string.no_net_tip);
       }
    }

    /**
     * 获取请假信息失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(smartRefreshLayout.getState().isOpening) {
                smartRefreshLayout.finishRefresh(false);
                smartRefreshLayout.finishLoadMore(false);
            }
            setNoLayout(1);
        }
    }

    /**
     * 获取请假信息成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_LEAVE_HISTORY)
    }, thread = EventThread.MAIN_THREAD)
    public void getTableInfo(BaseResponse<List<SubmitLeaveInfo>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            //Toast.makeText(LeaveHistoryActivity.this,"获取成功",Toast.LENGTH_SHORT).show();
            if(requestPage==1)//下拉刷新
                smartRefreshLayout.finishRefresh(true);
            else//加载更多
                smartRefreshLayout.finishLoadMore(true);
            if((data.getData()==null||data.getData().size()==0)&&submitLeaveInfoList.size()==0)//没有数据
                setNoLayout(1);
            else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
                Toast.makeText(this,data.getMsg(),Toast.LENGTH_SHORT).show();
                --requestPage;
                return;
            }
            else {//有数据
                int currentSize = submitLeaveInfoList.size();
                submitLeaveInfoList.addAll(data.getData());
                setNoLayout(0);
                if(leaveInfoListAdapter==null)
                    initList();
                else
                    leaveInfoListAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
            }
        }
    }

    /**
     * 初始化列表
     */
    private void initList() {
        leaveInfoListAdapter = new LeaveInfoListAdapter(this,submitLeaveInfoList,ARouterConstants.LeaveInFoDetailActivity,type,R.layout.item_leave_info);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(leaveInfoListAdapter);
    }

    @Override
    public GetLeaveInfoHistoryPresenter getPersenter() {
        return new GetLeaveInfoHistoryPresenter(this);
    }
}
