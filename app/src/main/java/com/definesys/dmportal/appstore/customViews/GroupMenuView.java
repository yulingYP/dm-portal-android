package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.MainIconAdapter;
import com.definesys.dmportal.appstore.bean.MainIcon;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2019/1/6.
 */

public class GroupMenuView extends LinearLayout{
    @BindView(R.id.menu_view)
    RecyclerView recyclerView;
    @BindView(R.id.img_up)
    ImageView img_up;
    @BindView(R.id.item_layout)
    LinearLayout lg_item;
    @BindView(R.id.list_layout)
    LinearLayout lg_list;

    private Context mContext;
    private List<MainIcon> mainIconList;
    private OnLayoutClickListener onClickListener;

    public GroupMenuView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.view_group_menu, this);
        ButterKnife.bind(this);
        RxView.clicks(lg_item)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                   if(onClickListener!=null)
                       onClickListener.onLayoutClick();
                });
    }

    /**
     * 数据设置
     * @param mainIconList 图标
     * @param iconWith 菜单图标的宽度
     */
    public void setData(List<MainIcon> mainIconList,int iconWith){
        this.mainIconList = mainIconList;

        //设置上箭头位置
        LayoutParams layoutParams = (LayoutParams) img_up.getLayoutParams();

        layoutParams.rightMargin= DensityUtil.dip2px(mContext,6) +iconWith/2-DensityUtil.dip2px(mContext,4);
//        layoutParams.rightMargin=0;
        img_up.setLayoutParams(layoutParams);

        //设置列表位置
        LayoutParams layoutParams1 = (LayoutParams) lg_list.getLayoutParams();
        layoutParams1.rightMargin = DensityUtil.dip2px(mContext,12);
//        layoutParams1.rightMargin =0;
        lg_list.setLayoutParams(layoutParams1);
        initList();
    }

    private void initList() {
        MainIconAdapter mainIconAdapter = new MainIconAdapter(mContext, mainIconList, false, R.layout.item_group_menu_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mainIconAdapter);
    }

    public void setOnLayoutClickListener(OnLayoutClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnLayoutClickListener{
        void onLayoutClick();
    }

}
