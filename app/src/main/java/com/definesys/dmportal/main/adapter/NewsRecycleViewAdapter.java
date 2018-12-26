package com.definesys.dmportal.main.adapter;

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
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.TextViewUniversalToast;
import com.definesys.dmportal.main.bean.News;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<News> newsList;
    private Context context;
    private OnItemClickListener onClickListener;
    private RequestOptions options;

    public NewsRecycleViewAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
        options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_news_image_load_failed)
                .error(R.drawable.ic_news_image_load_failed);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsHolder(LayoutInflater.from(context).inflate( R.layout.item_time_img_title_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NewsHolder viewHolder = (NewsHolder) holder;
        viewHolder.date.setTextDisplayed(newsList.get(position).getNewsDate());
        Glide.with(context).load(newsList.get(position).getImageUrl()).apply(options).into(viewHolder.img);
        viewHolder.title.setText(newsList.get(position).getNewsTitle());
        viewHolder.content.setText(newsList.get(position).getNewsContent());
        if (onClickListener != null)
            viewHolder.layout.setOnClickListener((view) -> onClickListener.OnClick(position));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return newsList == null ? 0 : newsList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    class NewsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time_item_titc)
        TextViewUniversalToast date;
        @BindView(R.id.img_item_titc)
        ImageView img;
        @BindView(R.id.titie_item_titc)
        TextView title;
        @BindView(R.id.content_item_titc)
        TextView content;
        @BindView(R.id.main_layout_item_titc)
        LinearLayout layout;

        NewsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void OnClick(int position);
    }
}
