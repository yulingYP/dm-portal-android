package com.definesys.dmportal.appstore.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.TypeListAdapter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.adapter.GruopInfoRecycleViewAdapter;
import com.definesys.dmportal.main.bean.GroupInfo;
import com.jakewharton.rxbinding2.view.RxView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

@Route(path = ARouterConstants.GroupMainActivity)
public class GroupMainActivity extends AppCompatActivity {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.srf_view)
    SmartRefreshLayout srf_view;

    //种类列表
    @BindView(R.id.group_view)
    RecyclerView groupListView;
    private List<GroupInfo> groupList;
    private GruopInfoRecycleViewAdapter gruopInfoRecycleViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);
        ButterKnife.bind(this);
        initView();
        initGroupList();//社团列表

    }

    private void initView() {
        titleBar.setTitle(getString(R.string.group_des));
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
        RxView.clicks(titleBar.addRightImageButton(R.drawable.search_icon,R.layout.activity_group_main))
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ARouter.getInstance().build(ARouterConstants.GroupSearchActivity).navigation();
                    }
                });
    }

    private void initGroupList() {
        groupList = new ArrayList<>();
        groupList.add(new GroupInfo("篮球爱好者协会","运动","描述.....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("影像协会","摄影","描述....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("威软实验室","实验室、编程","描述....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("冰峰实验室","实验室、编程、Android\\IOS","描述....",String.valueOf(R.drawable.x11)));
        gruopInfoRecycleViewAdapter = new GruopInfoRecycleViewAdapter(groupList,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        groupListView.setLayoutManager(layoutManager);
        groupListView.setAdapter(gruopInfoRecycleViewAdapter);
    }

}
