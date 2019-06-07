package com.definesys.dmportal.appstore.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.interfaces.OnItemClickListener;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2018/8/24.
 */
//社团类型
public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.ViewHolder> {
    private List<String> typeList;
    private OnItemClickListener onItemClickListener;
    private int layoutId;

    public TypeListAdapter(List<String> typeList,int layoutId) {
        this.typeList = typeList;
        this.layoutId =layoutId;
    }

    @NonNull
    @Override
    public TypeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull TypeListAdapter.ViewHolder holder, int position) {

        holder.typeText.setText(typeList.get(position));
        RxView.clicks( holder.typeText)
                .throttleFirst(Constants.clickdelay , TimeUnit.MILLISECONDS)   //两秒钟之内只取一个点击事件，防抖操作
                .subscribe(obj ->{
                    if(onItemClickListener!=null)
                        onItemClickListener.onClick(position);

                });
    }

    @Override
    public int getItemCount() {
        return typeList==null?0:typeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.type_text)
        TextView typeText;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }



    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

