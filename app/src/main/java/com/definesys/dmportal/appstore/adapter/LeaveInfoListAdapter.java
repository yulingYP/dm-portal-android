package com.definesys.dmportal.appstore.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2019/1/15.
 */

public class LeaveInfoListAdapter extends RecyclerView.Adapter<LeaveInfoListAdapter.ViewHolder> {

    private Context mContext;
    private List<LeaveInfo> submitLeaveInfoList;
    private List<ApprovalRecord> approvalRecordList;
    private String ARouterPath;//跳转路径
    private int type;//适配器类型 0.历史请假记录 1.待处理的审批记录 2.历史审批记录 3.销假
    private int layoutId;//layoutId

    public LeaveInfoListAdapter(Context mContext, List<LeaveInfo> submitLeaveInfoList,List<ApprovalRecord> approvalRecordList, String ARouterPath, int type, int layoutId) {
        this.mContext = mContext;
        this.submitLeaveInfoList = submitLeaveInfoList;
        this.ARouterPath = ARouterPath;
        this.type = type;
        this.layoutId = layoutId;
        this.approvalRecordList = approvalRecordList;
    }

    @NonNull
    @Override
    public LeaveInfoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveInfoListAdapter.ViewHolder holder, int position) {
//        submitLeaveInfoList.get(position)

        if(type==0||type==1||type==3) {//请假信息

            //点击事件
            RxView.clicks(holder.itemView)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj ->
                            ARouter.getInstance()
                                    .build(ARouterPath)//跳转页面
                                    .withObject("leaveInfo", submitLeaveInfoList.get(position))//请假信息
                                    .withInt("type",4)
                                    .withInt("title", 0)//页面标题
                                    .navigation()
                    );
            if (type == 0||type==3) {
                holder.tv_name.setVisibility(View.GONE);
                //审批状态
                holder.tv_status.setText(DensityUtil.getApprovalStatus(submitLeaveInfoList.get(position), mContext, false));
                DensityUtil.setTVcolor(holder.tv_status.getText().toString(), holder.tv_status, mContext);
            } else {
                holder.tv_name.setText(submitLeaveInfoList.get(position).getName());
                holder.tv_status.setText(String.valueOf(submitLeaveInfoList.get(position).getUserId()) );
            }
            if (submitLeaveInfoList.get(position).getType() == 3)//实习
                submitLeaveInfoList.get(position).setType(2);
            //请假类型
            submitLeaveInfoList.get(position).setLeaveType(DensityUtil.setTypeText(mContext.getResources().getStringArray(R.array.leave_type)[submitLeaveInfoList.get(position).getType()%3]));
            holder.tv_type.setText(mContext.getString(R.string.type_tip, submitLeaveInfoList.get(position).getLeaveType()));
            //请假原因
            holder.tv_title.setText(mContext.getString(R.string.leave_title, submitLeaveInfoList.get(position).getLeaveTitle()));
            //请假时间
            holder.tv_time.setText(DensityUtil.dateTypeToString(mContext.getString(R.string.date_type_2), submitLeaveInfoList.get(position).getSubmitDate()));
        }
        else {//历史审批记录

            //点击事件
            RxView.clicks(holder.itemView)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj ->
                            ARouter.getInstance()
                                    .build(ARouterPath)//跳转页面
                                    .withObject("date",approvalRecordList.get(position).getApprovalTime())//审批时间
                                    .withLong("leaveId",approvalRecordList.get(position).getLeaveInfoId())//请假id
                                    .withInt("type",approvalRecordList.get(position).getApprovalResult())//审批结果
                                    .withString("approvalContent",approvalRecordList.get(position).getApprovalContent())//审批内容
                                    .navigation()
                    );

            holder.tv_type.setVisibility(View.GONE);
            //请假人姓名
            holder.tv_name.setText(approvalRecordList.get(position).getLeaverName());
            //学号
            holder.tv_status.setText(String.valueOf(approvalRecordList.get(position).getLeaverId()));
            //审批结果
            String result=mContext.getString(R.string.approval_result_text, approvalRecordList.get(position).getApprovalResult()==1?mContext.getString(R.string.green_agree):mContext.getString(R.string.red_refuse));
            holder.tv_title.setText(Html.fromHtml(result));
            //审批时间
            holder.tv_time.setText(DensityUtil.dateTypeToString(mContext.getString(R.string.date_type_2), approvalRecordList.get(position).getApprovalTime()));
        }



    }


    @Override
    public int getItemCount() {
        if(type == 0 ||type == 1||type==3)
         return submitLeaveInfoList==null?0:submitLeaveInfoList.size();
        return approvalRecordList==null?0:approvalRecordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

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
