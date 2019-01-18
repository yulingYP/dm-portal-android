package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * 选择周数
 * Created by 羽翎 on 2019/1/6.
 */

public class SelectWeekView extends LinearLayout {
    private Context mContext;
    @BindView(R.id.reason_view)
    RecyclerView recyclerView;
    @BindView(R.id.view_layout)
    LinearLayout lg_view;
    @BindView(R.id.title_text)
    TextView tv_title;
    @BindView(R.id.confirm_text)
    TextView tv_confirm;
    private int maxWeek;//最大周
    private int currentWeek;//当前周
    private MyClickListener myClickListener;
    private TextView tv_temp;
    private SelectWeekAdapter selectWeekAdapter;
    public SelectWeekView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        setFocusable(true);
        LayoutInflater.from(context).inflate(R.layout.customer_reson_type_layout,this);
        ButterKnife.bind(this);
        tv_title.setVisibility(GONE);
        tv_confirm.setVisibility(GONE);
        lg_view.setBackground(mContext.getResources().getDrawable(R.drawable.week_select_view_back_ground));

    }
    public void setReasonlist(int maxWeek,int currentWeek) {
        this.maxWeek = maxWeek;
        this.currentWeek =currentWeek;
        selectWeekAdapter= new SelectWeekAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(selectWeekAdapter);
    }

    public void setCurrentWeek(int currentWeek) {
        this.currentWeek = currentWeek;
        selectWeekAdapter.notifyDataSetChanged();

    }

    public class SelectWeekAdapter extends RecyclerView.Adapter<SelectWeekAdapter.ViewHolder>{

        public SelectWeekAdapter() {
        }
        @NonNull
        @Override
        public SelectWeekAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_reason_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SelectWeekAdapter.ViewHolder holder, int position) {
            holder.tv_week.setText(""+(position+1));
            holder.tv_week.setGravity(Gravity.END|Gravity.CENTER);
            if(position==currentWeek-1) {
                holder.tv_week.setTextColor(mContext.getResources().getColor(R.color.blue));
                tv_temp = holder.tv_week;
            }
            else
                holder.tv_week.setTextColor(mContext.getResources().getColor(R.color.black));
            //item长度
            LayoutParams layoutParams =new LayoutParams(DensityUtil.dip2px(mContext,120), ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.CENTER;
            holder.itemView.setLayoutParams(layoutParams);
            RxView.clicks(holder.lg_item)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj-> {
                            if(myClickListener!=null)
                                myClickListener.onClick(position+1);
                            currentWeek = position+1;
                            if(tv_temp!=null)
                                tv_temp.setTextColor(mContext.getResources().getColor(R.color.black));
                            holder.tv_week.setTextColor(mContext.getResources().getColor(R.color.blue));
                            tv_temp = holder.tv_week;
                    });

        }

        @Override
        public int getItemCount() {
            return maxWeek;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_layout)
            LinearLayout lg_item;
            @BindView(R.id.reason_text)
            TextView tv_week;
            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);
            }
        }
    }
    public interface MyClickListener{
         void onClick(int week);
    }

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }
}
