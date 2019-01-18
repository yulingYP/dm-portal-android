package com.definesys.dmportal.appstore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.SubmitLeaveInfo;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by 羽翎 on 2019/1/15.
 */

public class LeaveInfoListAdapter extends RecyclerView.Adapter<LeaveInfoListAdapter.ViewHolder> {

    private Context mContext;
    private List<SubmitLeaveInfo> submitLeaveInfoList;
    private String ARouterPath;//跳转路径
    private int type;//适配器类型 0.查看历史请假记录 1.查看历史审批记录 2.查看未审批记录
    private int layoutId;//layoutId

    public LeaveInfoListAdapter(Context mContext, List<SubmitLeaveInfo> submitLeaveInfoList, String ARouterPath,int type,int layoutId) {
        this.mContext = mContext;
        this.submitLeaveInfoList = submitLeaveInfoList;
        this.ARouterPath = ARouterPath;
        this.type = type;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public LeaveInfoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveInfoListAdapter.ViewHolder holder, int position) {
//        submitLeaveInfoList.get(position)
        RxView.clicks(holder.itemView)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                        ARouter.getInstance()
                                .build(ARouterPath)//跳转页面
                                .withObject("leaveInfo",submitLeaveInfoList.get(position))//参数
                                .navigation()
                );
        if(type==0){
            holder.tv_id.setVisibility(View.GONE);
            holder.tv_name.setVisibility(View.GONE);
        }
        //请假类型
        submitLeaveInfoList.get(position).setLeaveType(DensityUtil.setTypeText(mContext.getResources().getStringArray(R.array.leave_type)[submitLeaveInfoList.get(position).getType()]));
        holder.tv_type.setText(mContext.getString(R.string.type_tip, submitLeaveInfoList.get(position).getLeaveType()));
        //请假原因
        holder.tv_title.setText(mContext.getString(R.string.leave_title,submitLeaveInfoList.get(position).getLeaveTitle()));
        //请假时间
        holder.tv_time.setText(DensityUtil.dateTypeToString(mContext.getString(R.string.date_type_2),submitLeaveInfoList.get(position).getSubmitDate()));

        setStatus(position,holder.tv_status);
    }
    /*
     *审批状态
     */
    private void setStatus(int position,TextView tv_status) {
        if(submitLeaveInfoList.get(position).getApprovalStatus()<10){//正在审批
            tv_status.setText(R.string.status_tip_1);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.blue));
        }else if (submitLeaveInfoList.get(position).getApprovalStatus()==10){//已批准
            tv_status.setText(R.string.status_tip_2);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.green));
        }else if (submitLeaveInfoList.get(position).getApprovalStatus()==11){//已拒绝
            tv_status.setText(R.string.status_tip_3);
            tv_status.setTextColor(mContext.getResources().getColor(R.color.red_error));
        }
    }

    @Override
    public int getItemCount() {
        return submitLeaveInfoList==null?0:submitLeaveInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.id_text)
        TextView tv_id;

        @BindView(R.id.name_text)
        TextView tv_name;

        @BindView(R.id.type_text)
        TextView tv_type;

        @BindView(R.id.submit_time_text)
        TextView tv_time;

        @BindView(R.id.title_text)
        TextView tv_title;

        @BindView(R.id.status_text)
        TextView tv_status;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
