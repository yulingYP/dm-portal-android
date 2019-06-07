package com.example.dmportal.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.dmportal.R;
import com.example.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackRecycleViewImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LocalMedia> bgds;
    private Context context;
    private int itemLayout;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FeedbackRecycleViewImageAdapter(Context context, int itemLayout, List<LocalMedia> bgds) {
        this.bgds = bgds;
        this.context = context;
        this.itemLayout = itemLayout;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(itemLayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if(position == 0){
            ((ViewHolder) holder).bgd.setImageResource(R.mipmap.addimage);
            ((ViewHolder) holder).frd.setVisibility(View.GONE);
        }else {
            if (bgds.get(position).isCut()) {
                Glide.with(context).load(bgds.get(position).getCutPath()).into(viewHolder.bgd);
                ((ViewHolder) holder).frd.setVisibility(View.VISIBLE);
            } else {
                Glide.with(context).load(bgds.get(position).getPath()).into(viewHolder.bgd);
                ((ViewHolder) holder).frd.setVisibility(View.VISIBLE);
            }
        }

        if (onClickListener != null) {

            //  背景图点击事件《==》对应显示部分
            RxView.clicks(viewHolder.bgd)
                    .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                    .subscribe(object ->{
                        onClickListener.onBackgroundClick(position);
                    });


            //  前景图点击事件《==》对应小x
            RxView.clicks(viewHolder.frd)
                    .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                    .subscribe(object ->{
                        onClickListener.onForegroundClick(position);
                    });
        }
    }

    public void setImages(List<LocalMedia> images) {
        this.bgds = images;
    }

    @Override
    public int getItemCount() {
        return bgds == null ? 1 : bgds.size();
    }

    public void removeItem(int position) {
        bgds.remove(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bgd_view_fi)
        ImageView bgd;
        @BindView(R.id.frd_view_fi)
        ImageView frd;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickListener {
        public void onBackgroundClick(int position);

        public void onForegroundClick(int position);
    }
}
