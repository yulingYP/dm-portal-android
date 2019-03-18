package com.definesys.dmportal.appstore.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MainIcon;
import com.definesys.dmportal.appstore.customViews.GroupMenuView;
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

@Route(path = ARouterConstants.GroupMainActivity)
public class GroupMainActivity extends AppCompatActivity {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.img_menu)
    ImageView img_list;

    @BindView(R.id.srf_view)
    SmartRefreshLayout srf_view;

    @BindView(R.id.search_layout)
    LinearLayout lg_search;

    @BindView(R.id.temp_view)
    View v_temp;

    //种类列表
    @BindView(R.id.group_view)
    RecyclerView groupListView;
    private List<GroupInfo> groupList;
    private GruopInfoRecycleViewAdapter gruopInfoRecycleViewAdapter;
    private PopupWindow popupWindow;//弹出菜单框
    private GroupMenuView groupMenuView;//菜单视图
    private List<MainIcon> mainIconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);
        ButterKnife.bind(this);
        initView();
        initGroupList();//社团列表

    }

    private void initView() {
        titleBar.setBackgroundDividerEnabled(false);
        //titleBar.setBackground(null);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
        //搜索
        RxView.clicks(lg_search)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                        ARouter.getInstance().build(ARouterConstants.GroupSearchActivity).navigation()
                    );
        //菜单
        RxView.clicks(img_list)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj-> {
                    v_temp.setVisibility(View.VISIBLE);
                    v_temp.setAnimation(AnimationUtils.loadAnimation(GroupMainActivity.this,R.anim.layout_show));
                    popupWindow.showAsDropDown(img_list);
                });
        img_list.post(() -> initMenuList(img_list.getMeasuredWidth()));
    }

    /**
     * 菜单列表
     */
    private void initMenuList(int width) {
           mainIconList = new ArrayList<>();
           mainIconList.add(new MainIcon("请假",R.drawable.leave_icon,ARouterConstants.LeaveMainActivity));
           mainIconList.add(new MainIcon("课表",R.drawable.table_icon,ARouterConstants.SubjectTableActivity));
           mainIconList.add(new MainIcon("社团",R.drawable.group_icon,ARouterConstants.GroupMainActivity));
           groupMenuView= new GroupMenuView(this);
           groupMenuView.setData(mainIconList,width);
           popupWindow = new PopupWindow(groupMenuView,
                   LinearLayout.LayoutParams.WRAP_CONTENT,
                   LinearLayout.LayoutParams.WRAP_CONTENT, true);


           popupWindow.setAnimationStyle(R.style.PopupAnimation);
           popupWindow.setOnDismissListener(() -> {
               //消失动画
               v_temp.setAnimation(AnimationUtils.loadAnimation(GroupMainActivity.this,R.anim.layout_hide));
               v_temp.setVisibility(View.GONE);
           });

    }

    /**
     * 社团列表
     */
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

    @Override
    protected void onStop() {
        super.onStop();
        if(popupWindow!=null)
            popupWindow.dismiss();
    }
}
