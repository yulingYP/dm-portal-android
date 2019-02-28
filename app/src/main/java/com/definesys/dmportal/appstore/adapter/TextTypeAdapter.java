package com.definesys.dmportal.appstore.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 羽翎 on 2019/2/28.
 */

public class TextTypeAdapter extends RecyclerView.Adapter<TextTypeAdapter.ViewHolder>{
    private List<String> typeList;
    private String content;
    private Context mContext;
    private int selectPosition;//选择的样式的位置
    private ImageView iv_selected;//选择的图标

    public TextTypeAdapter(List<String> typeList, String content, Context mContext) {
        this.typeList = typeList;
        this.content = content;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sign_type_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(content);
        if(position!=0)
            holder.tv_name.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "ttf/"+typeList.get(position-1)));
        if(selectPosition==position){
            holder.iv_select.setImageResource(R.drawable.right_icon);
            iv_selected = holder.iv_select;
        }else {
            holder.iv_select.setImageResource(R.drawable.no_select);
        }
        RxView.clicks(holder.itemView)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if(iv_selected!=null)
                        iv_selected.setImageResource(R.drawable.no_select);
                    selectPosition = position;
                    holder.iv_select.setImageResource(R.drawable.right_icon);
                    iv_selected = holder.iv_select;
                });
    }

    @Override
    public int getItemCount() {
        return typeList==null?0:typeList.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_text)
        TextView tv_name;
        @BindView(R.id.select_img)
        ImageView iv_select;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }
}
