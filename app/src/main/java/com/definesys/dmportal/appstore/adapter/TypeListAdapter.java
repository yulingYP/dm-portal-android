package com.definesys.dmportal.appstore.adapter;

/**
 * Created by 羽翎 on 2018/11/22.
 */

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

import static com.vise.xsnow.http.ViseHttp.getContext;

/**
 * Created by 羽翎 on 2018/8/24.
 */

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.ViewHolder> {
    private List<String> typeList;
    private int oldPosition;

    public TypeListAdapter(List<String> typeList) {
        this.typeList = typeList;
        oldPosition=0;
    }

    @NonNull
    @Override
    public TypeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull TypeListAdapter.ViewHolder holder, int position) {
        if(position==oldPosition){
            holder.typeText.setTextColor(Color.WHITE);
            holder.typeText.setBackgroundResource(R.drawable.type_text_is_select);
        }else {
            holder.typeText.setTextColor(R.color.color_deep_gray);
            holder.typeText.setBackgroundResource(R.drawable.type_text_no_select);
        }
        holder.typeText.setText(typeList.get(position));
        RxView.clicks( holder.typeText)
                .throttleFirst(Constants.clickdelay , TimeUnit.MILLISECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(oldPosition!=-1){
                            notifyItemChanged(oldPosition);
                        }
                        holder.typeText.setTextColor(Color.WHITE);
                        holder.typeText.setBackgroundResource(R.drawable.type_text_is_select);
                        oldPosition=position;
                       // SmecRxBus.get().post(getContext().getString(R.string.rxbus_tag_type),holder.typeText.getText().toString());
                        Toast.makeText(getContext(),typeList.get(position),Toast.LENGTH_SHORT).show();              }
                });
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.type_text)
        TextView typeText;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public int getPosition(){
        return oldPosition;
    }
}

