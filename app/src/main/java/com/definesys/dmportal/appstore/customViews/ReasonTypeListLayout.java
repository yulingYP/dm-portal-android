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
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
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
    @BindView(R.id.title_text)
    TextView tv_title;
    @BindView(R.id.confirm_text)
    TextView tv_confirm;
    private String[] reasonlist;//请假类型或原因数组
    private MyClickListener myClickListener;//请假类型或原因点击监听
    private MyOnConfirmClickListener myOnConfirmClickListener;//去顶按钮点击监听
    private ReasonAdapter reasonAdapter;//请假类型或原因适配器
    private SubjectSelectedAdapter subjectSelectedAdapter;//选择的请假课程适配器
    private String[] selectedList;////选择课程数组
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
        RxView.clicks(tv_confirm)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(myOnConfirmClickListener!=null)
                        myOnConfirmClickListener.onClick();
                });
    }

    /**
     * 请假类型适配器
     */
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
            holder.tv_reason.setText(reasonlist[position]);
            RxView.clicks(holder.lg_item)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if(myClickListener!=null)
                                myClickListener.onClick(reasonlist[position],position);
                        }
                    });

        }

        @Override
        public int getItemCount() {
            return reasonlist==null?0:reasonlist.length;
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

    /**
     * 选择课程
     */
    public class SubjectSelectedAdapter extends RecyclerView.Adapter<SubjectSelectedAdapter.ViewHolder>{


        @NonNull
        @Override
        public SubjectSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_selected_subject,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectSelectedAdapter.ViewHolder holder, int position) {
            String[]arr = selectedList[position].split("\\+");//时间+课程名
            //第position+1节课
            holder.tv_count.setText(mContext.getString(R.string.subject_count_tip,position+1));
            if(arr.length>1) {
                holder.tv_time.setVisibility(VISIBLE);
                holder.tv_name.setText(arr[1]);
                holder.tv_time.setText(arr[0]);
            }else if(arr.length==1){
                holder.tv_time.setVisibility(GONE);
                holder.tv_name.setText(arr[0]);
            }
            //下分割线
            if(position==selectedList.length-1)
                holder.v_bottomLine.setVisibility(VISIBLE);
            else
                holder.v_bottomLine.setVisibility(GONE);
        }

        @Override
        public int getItemCount() {
            return selectedList==null?0:selectedList.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.cursor_text)
            TextView tv_name;
            @BindView(R.id.time_text)
            TextView tv_time;
            @BindView(R.id.count_text)
            TextView tv_count;
            @BindView(R.id.bottom_line)
            View v_bottomLine;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
    public void setReasonlist(String[] reasonlist) {
        tv_confirm.setVisibility(GONE);
        this.reasonlist = reasonlist;
        if(reasonAdapter==null) {
            reasonAdapter = new ReasonAdapter();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(reasonAdapter);
        }else
            reasonAdapter.notifyDataSetChanged();
    }
    public void setSelectSubject(String[] selectedList) {
        tv_confirm.setVisibility(VISIBLE);
        this.selectedList = selectedList;
        if(subjectSelectedAdapter==null) {
            subjectSelectedAdapter = new SubjectSelectedAdapter();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(subjectSelectedAdapter);
        }else
            subjectSelectedAdapter.notifyDataSetChanged();
    }
    public interface MyClickListener{
        public void onClick(String type,int position);
    }
    public interface MyOnConfirmClickListener{
        public void onClick();
    }
    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void setMyOnConfirmClickListener(MyOnConfirmClickListener myOnConfirmClickListener) {
        this.myOnConfirmClickListener = myOnConfirmClickListener;
    }

    public TextView getTitleText() {
        return tv_title;
    }
}
