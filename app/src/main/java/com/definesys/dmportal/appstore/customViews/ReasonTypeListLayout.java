package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by 羽翎 on 2018/12/14.
 */

public class ReasonTypeListLayout extends LinearLayout {
    private Context mContext;
    @BindView(R.id.reason_view)
    RecyclerView recyclerView;
    private List<String> reasonlist;
    private MyClickListener myClickListener;
    public ReasonTypeListLayout(Context context) {
        super(context);
        initView(context);
    }

    public ReasonTypeListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ReasonTypeListLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext =context;
        LayoutInflater.from(mContext).inflate(R.layout.customer_reson_type_layout,this);
        ButterKnife.bind(this);
    }

    public class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder>{

        public ReasonAdapter() {
        }
        @NonNull
        @Override
        public ReasonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_reason_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ReasonAdapter.ViewHolder holder, int position) {
            holder.tv_reason.setText(reasonlist.get(position));
            RxView.clicks(holder.lg_item)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if(myClickListener!=null)
                                myClickListener.onClick(reasonlist.get(position));
                        }
                    });

        }

        @Override
        public int getItemCount() {
            return reasonlist==null?0:reasonlist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_layout)
            LinearLayout lg_item;
            @BindView(R.id.reason_text)
            TextView tv_reason;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }

    public void setReasonlist(List<String> reasonlist) {
        this.reasonlist = reasonlist;
        ReasonAdapter reasonAdapter = new ReasonAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(reasonAdapter);
    }

    public interface MyClickListener{
        public void onClick(String type);
    }

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
}
