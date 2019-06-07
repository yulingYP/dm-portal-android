package com.definesys.dmportal.appstore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MainIcon;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2019/1/4.
 */

public class MainIconAdapter extends RecyclerView.Adapter<MainIconAdapter.ViewHolder> {
    private Context mContext;
    private List<MainIcon> mainIconList;
    private boolean isMain;//是不是主页图标
    private int layoutId;

    public MainIconAdapter(Context mContext, List<MainIcon> mainIconList, boolean isMain, int layoutId) {
        this.mContext = mContext;
        this.mainIconList = mainIconList;
        this.isMain = isMain;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img_icon.setImageResource(mainIconList.get(position).getTempURL());
        holder.tv_name.setText(mainIconList.get(position).getName());
        RxView.clicks(holder.itemView)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj-> {
                    if (ARouterConstants.NoPath.equals(mainIconList.get(position).getaRounterPath())){//占位图
                        Toast.makeText(mContext,mContext.getString(R.string.coming_soon),Toast.LENGTH_SHORT).show();
                    }else {//有跳转路径
                        ARouter.getInstance()
                                .build(mainIconList.get(position).getaRounterPath())
                                .navigation();
                    }
                });
        if(isMain) {//主页图标
            //宽高一致
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = (MainActivity.screenWith - 100) / 3;
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return mainIconList==null?0:mainIconList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_icon)
        ImageView img_icon;
        @BindView(R.id.text_icon_name)
        TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
