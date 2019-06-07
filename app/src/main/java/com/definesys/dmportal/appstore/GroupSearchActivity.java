package com.definesys.dmportal.appstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.TypeListAdapter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.adapter.GruopInfoRecycleViewAdapter;
import com.definesys.dmportal.main.bean.GroupInfo;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.GroupSearchActivity)
public class GroupSearchActivity extends BaseActivity {
    //搜索框
    @BindView(R.id.et_search)
    EditText ed_search;

    @BindView(R.id.cancel_text)
    TextView tv_cancel;

    //种类列表
    @BindView(R.id.type_view)
    RecyclerView tyepListView;

    private List<String> typeList;
    private TypeListAdapter typeListAdapter;

    //社团列表
    @BindView(R.id.group_view)
    RecyclerView groupListView;

    private List<GroupInfo> groupList;
    private GruopInfoRecycleViewAdapter gruopInfoRecycleViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);
        ButterKnife.bind(this);
        initSerachView();//搜索框相关
        initTpyeList();//种类标签
        initGroupList();//社团标签
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

    private void initSerachView() {
        //获取搜索框焦点
        ed_search.setFocusable(true);
        ed_search.setFocusableInTouchMode(true);
        ed_search.requestFocus();
        RxView.clicks(tv_cancel)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->finish());
    }

    private void initTpyeList() {
        typeList = new ArrayList<>();
        typeList.add("全部");
        typeList.add("音乐");
        typeList.add("摄影");
        typeList.add("舞蹈");
        typeList.add("运动");
        typeList.add("动漫");
        typeList.add("实验室");
        GridLayoutManager staggeredGridLayoutManager = new GridLayoutManager(this,4);
        typeListAdapter = new TypeListAdapter(typeList,R.layout.item_type_list);
        tyepListView.setLayoutManager(staggeredGridLayoutManager);
        tyepListView.setAdapter(typeListAdapter);
        ((SimpleItemAnimator) tyepListView.getItemAnimator()).setSupportsChangeAnimations(false);
    }
    private void initGroupList() {
        groupList = new ArrayList<>();
        groupList.add(new GroupInfo("篮球爱好者协会","运动","描述.....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("影像协会","摄影","描述....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("威软实验室","实验室、编程","描述....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("冰峰实验室","实验室、编程、Android\\IOS","描述....",String.valueOf(R.drawable.x11)));
        gruopInfoRecycleViewAdapter = new GruopInfoRecycleViewAdapter(groupList,this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        groupListView.setLayoutManager(layoutManager);
        groupListView.setAdapter(gruopInfoRecycleViewAdapter);
    }

}
