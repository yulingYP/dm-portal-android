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
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.ApplyRecord;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2019/3/6.
 */

public class ApplyInfoAdapter extends RecyclerView.Adapter<ApplyInfoAdapter.ViewHolder> {
    private Context mContext;
    private List<ApplyInfo> applyInfoList;
    private List<ApplyRecord> applyRecordList;
    private String ARouterPath;//跳转路径
    private int type;//适配器类型 0.权限审批 1.历史申请记录 2.历史审批记录
    private int layoutId;//layoutId

    public ApplyInfoAdapter(Context mContext, List<ApplyInfo> applyInfoList, List<ApplyRecord> applyRecordList, String ARouterPath, int type, int layoutId) {
        this.mContext = mContext;
        this.applyInfoList = applyInfoList;
        this.applyRecordList = applyRecordList;
        this.ARouterPath = ARouterPath;
        this.type = type;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public ApplyInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(layoutId,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyInfoAdapter.ViewHolder holder, int position) {
        if(type==0||type==1){//0.权限审批 1.历史申请记录
            //点击事件
            RxView.clicks(holder.itemView)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj ->
                            ARouter.getInstance()
                                    .build(ARouterPath)//跳转页面
                                    .withObject("applyInfo", applyInfoList.get(position))//请假信息
                                    .withInt("type",4)//0.权限审批专用
                                    .withInt("title", 0)//页面标题
                                    .navigation()
                    );
            if(type==0){
                //姓名
                holder.tv_name.setText(mContext.getString(R.string.name_tip,applyInfoList.get(position).getApplyUserName()));
                //学号或工号
                holder.tv_status.setText(String.valueOf(applyInfoList.get(position).getApplyUserId()));
            }else {
                //姓名
                holder.tv_name.setVisibility(View.GONE);
                //状态
                holder.tv_status.setText(DensityUtil.getApplyStatus(mContext,applyInfoList.get(position).getApplyStatus()));
                DensityUtil.setTVcolor(holder.tv_status.getText().toString(),holder.tv_status,mContext);
            }

            //权限类型
            String type=applyInfoList.get(position).getApplyDetailContent().split(" ")[0];
            holder.tv_type.setText(mContext.getString(R.string.authority_tip,type.length()>2?type.substring(0,type.length()-2):type));
            //权限范围
            holder.tv_title.setText(mContext.getString(R.string.region_tip,applyInfoList.get(position).getApplyRegion()));
            //申请时间
            holder.tv_time.setText(DensityUtil.dateTypeToString(mContext.getString(R.string.date_type_2),applyInfoList.get(position).getApplyDate()));

        }else { //2.历史审批记录
            holder.tv_type.setVisibility(View.GONE);
            //申请人姓名
            holder.tv_name.setText(applyRecordList.get(position).getApplyerName());
            //学号
            holder.tv_status.setText(String.valueOf(applyInfoList.get(position).getApplyId()));
            //审批结果
            String result=mContext.getString(R.string.approval_result_text, applyRecordList.get(position).getApplyStatus()==1?mContext.getString(R.string.green_agree):mContext.getString(R.string.red_refuse));
            holder.tv_title.setText(Html.fromHtml(result));
            //审批时间
            holder.tv_time.setText(DensityUtil.dateTypeToString(mContext.getString(R.string.date_type_2), applyRecordList.get(position).getApprovalDate()));
            //点击事件
            RxView.clicks(holder.itemView)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj ->
                            ARouter.getInstance()
                                    .build(ARouterPath)//跳转页面
                                    .withObject("date",applyRecordList.get(position).getApprovalDate())//审批时间
                                    .withString("applyId",applyRecordList.get(position).getApplyId())//申请id
                                    .withInt("type",applyRecordList.get(position).getApplyStatus())//审批结果
                                    .withString("approvalContent",applyRecordList.get(position).getApplyContent())//审批内容
                                    .navigation()
                    );
        }

    }

    @Override
    public int getItemCount() {
        if(type==0||type==1)
            return applyInfoList==null?0:applyInfoList.size();
        return applyRecordList==null?0:applyRecordList.size();
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
