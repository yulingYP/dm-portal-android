package com.definesys.dmportal.appstore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MainIcon;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.ui.MainActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by 羽翎 on 2019/1/4.
 */

public class MainIconAdapter extends RecyclerView.Adapter<MainIconAdapter.ViewHolder> {
    private Context mContext;
    private List<MainIcon> mainIconList;

    public MainIconAdapter(Context mContext, List<MainIcon> mainIconList) {
        this.mContext = mContext;
        this.mainIconList = mainIconList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_main_icon,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img_icon.setImageResource(mainIconList.get(position).getTempURL());
        holder.tv_name.setText(mainIconList.get(position).getName());
        RxView.clicks(holder.itemView)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ARouter.getInstance().build(mainIconList.get(position).getaRounterPath()).navigation();
                    }
                });
        ViewGroup.LayoutParams layoutParams=holder.itemView.getLayoutParams();
        layoutParams.height= (MainActivity.screenWith-100)/3;
        holder.itemView.setLayoutParams(layoutParams);
    }

    @Override
    public int getItemCount() {
        return mainIconList==null?0:mainIconList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_layout)
        RelativeLayout lg_item;
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
