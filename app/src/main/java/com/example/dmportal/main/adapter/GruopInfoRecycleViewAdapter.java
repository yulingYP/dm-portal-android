package com.example.dmportal.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dmportal.R;
import com.example.dmportal.appstore.utils.Constants;
import com.example.dmportal.main.bean.GroupInfo;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2018/11/22.
 */

public class GruopInfoRecycleViewAdapter extends RecyclerView.Adapter<GruopInfoRecycleViewAdapter.ViewHolder> {
    private List<GroupInfo> list;
    private Context mContext;

    public GruopInfoRecycleViewAdapter(List<GroupInfo> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RequestOptions options = new RequestOptions()
                .dontAnimate()
                .error(R.drawable.x11);
        Glide.with(mContext).load(list.get(position).getUri()).apply(options).into(holder.iv_group);
        holder.tv_name.setText(list.get(position).getGroupName());
        holder.tv_type.setText(list.get(position).getGruopType());
        holder.tv_des.setText(list.get(position).getGroupDes());
        RxView.clicks(holder.item_layout)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //跳转页面展示社团详情
                });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_item_layout)
        LinearLayout item_layout;
        @BindView(R.id.group_img)
        ImageView iv_group;
        @BindView(R.id.group_name)
        TextView tv_name;
        @BindView(R.id.group_type)
        TextView tv_type;
        @BindView(R.id.group_des)
        TextView tv_des;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
