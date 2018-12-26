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

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.TextViewUniversalToast;
import com.definesys.dmportal.main.bean.Message;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MsgRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Message> messages;
    private Context context;
    //  点击事件的预留变量
    private OnClickListener onClickListener;


    public MsgRecycleViewAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MsgHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_time_icon_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MsgHolder) {
            MsgHolder viewHolder = (MsgHolder) holder;
            viewHolder.time.setTextDisplayed(messages.get(position).getMsgDate());
            viewHolder.img.setImageResource(messages.get(position).getMsgIcon());
            viewHolder.content.setText(messages.get(position).getMsgTitle());
            if (onClickListener != null)
                viewHolder.layout.setOnClickListener((view) -> onClickListener.OnClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
//        holder.itemView.clearAnimation();
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.onClickListener = clickListener;
    }

    class MsgHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time_item_tit)
        TextViewUniversalToast time;
        @BindView(R.id.icon_item_tit)
        ImageView img;
        @BindView(R.id.content_item_tit)
        TextView content;
        @BindView(R.id.main_layout_item_tit)
        LinearLayout layout;

        public MsgHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickListener {
        void OnClick(int position);
    }

}
