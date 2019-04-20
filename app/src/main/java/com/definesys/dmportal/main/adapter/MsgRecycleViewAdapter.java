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
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.customViews.TextViewUniversalToast;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MsgRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MyMessage> messages;
    private Context context;

    private SimpleDateFormat sdf;


    public MsgRecycleViewAdapter(Context context, List<MyMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.sdf=new SimpleDateFormat(context.getString(R.string.date_type_2), Locale.getDefault());
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
            viewHolder.time.setTextDisplayed(sdf.format(messages.get(position).getSendTime()));
            if(messages.get(position).getMessageType()==1){//请假人消息
                if(messages.get(position).getMessageExtend2()==0) {//拒绝
                    viewHolder.img.setImageResource(R.drawable.refuse);
                    viewHolder.content.setText(R.string.message_tip_1);
                }else if(messages.get(position).getMessageExtend2()==1) {//同意
                    viewHolder.img.setImageResource(R.drawable.pass);
                    viewHolder.content.setText(R.string.message_tip_2);
                }
                else if(messages.get(position).getMessageExtend2()==2) {//已提交
                    viewHolder.img.setImageResource(R.drawable.review);
                    viewHolder.content.setText(R.string.message_tip_3);
                }else if(messages.get(position).getMessageExtend2()==3) {//已销假
                    viewHolder.img.setImageResource(R.drawable.ic_leave_return);
                    viewHolder.content.setText(R.string.message_tip_4);
                }

            }else if(messages.get(position).getMessageType()==2){//审批人消息
                String id = messages.get(position).getMessageExtend();
                id=id.length()>9?id.substring(0,9):id;
                if(messages.get(position).getMessageExtend2()==0) {//拒绝
                    viewHolder.img.setImageResource(R.drawable.ic_leave_refuse);
                    viewHolder.content.setText(context.getString(R.string.message_tip_5,id));
                }else if(messages.get(position).getMessageExtend2()==1) {//同意
                    viewHolder.img.setImageResource(R.drawable.ic_leave_accept);
                    viewHolder.content.setText(context.getString(R.string.message_tip_6,id));
                }else if(messages.get(position).getMessageExtend2()==4) {//未审批
                    viewHolder.img.setImageResource(R.drawable.ic_leave_approving);
                    viewHolder.content.setText(context.getString(R.string.message_tip_7,id));
                }
            }else if(messages.get(position).getMessageType()==4){//申请人申请权限
                if(messages.get(position).getMessageExtend2()==0) {//拒绝
                    viewHolder.img.setImageResource(R.drawable.refuse);
                    viewHolder.content.setText(R.string.message_tip_8);
                }else if(messages.get(position).getMessageExtend2()==1) {//同意
                    viewHolder.img.setImageResource(R.drawable.pass);
                    viewHolder.content.setText(R.string.message_tip_9);
                } else if(messages.get(position).getMessageExtend2()==4) {//已提交
                    viewHolder.img.setImageResource(R.drawable.review);
                    viewHolder.content.setText(R.string.message_tip_10);
                }
            }else if(messages.get(position).getMessageType()==5){//审批人审批权限
                String id = messages.get(position).getMessageExtend();
                id=id.length()>9?id.substring(0,9):id;
                if(messages.get(position).getMessageExtend2()==0) {//拒绝
                    viewHolder.img.setImageResource(R.drawable.ic_leave_refuse);
                    viewHolder.content.setText(context.getString(R.string.message_tip_11,id));
                }else if(messages.get(position).getMessageExtend2()==1) {//同意
                    viewHolder.img.setImageResource(R.drawable.ic_leave_accept);
                    viewHolder.content.setText(context.getString(R.string.message_tip_12,id));
                }else if(messages.get(position).getMessageExtend2()==4) {//未审批
                    viewHolder.img.setImageResource(R.drawable.ic_leave_approving);
                    viewHolder.content.setText(context.getString(R.string.message_tip_13,id));
                }
            }else if(messages.get(position).getMessageType()==6){//查看权限申请记录
               if("delete".equals(messages.get(position).getMessageContent().toLowerCase())){//权限删除
                   if("".equals(messages.get(position).getMessageExtend())) {//被动删除
                       viewHolder.img.setImageResource(R.drawable.ic_ma_delete);
                       viewHolder.content.setText(context.getString(R.string.message_tip_15, DensityUtil.getAuthorityName(context, messages.get(position).getMessageExtend2())));
                   }else {//主动删除
                       viewHolder.img.setImageResource(R.drawable.ic_aut_delete);
                       viewHolder.content.setText(context.getString(R.string.message_tip_17, DensityUtil.getAuthorityName(context, messages.get(position).getMessageExtend2())));
                   }
               } else  {//仍保留权限但范围发生变化
                   if("".equals(messages.get(position).getMessageExtend())) {//被动修改
                       viewHolder.img.setImageResource(R.drawable.ic_ma_change);
                       viewHolder.content.setText(context.getString(R.string.message_tip_16, DensityUtil.getAuthorityName(context, messages.get(position).getMessageExtend2())));
                   }else {//主动修改
                       viewHolder.img.setImageResource(R.drawable.ic_aut_change);
                       viewHolder.content.setText(context.getString(R.string.message_tip_18, DensityUtil.getAuthorityName(context, messages.get(position).getMessageExtend2())));
                   }
               }
            }
            else if(messages.get(position).getMessageType()==11){//查看权限申请记录
                viewHolder.img.setImageResource(R.drawable.review);
                viewHolder.content.setText(R.string.message_tip_14);
            }
            RxView.clicks(viewHolder.layout)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj->{
                        if(messages.get(position).getMessageType()==1){//请假人消息
                            ARouter.getInstance()
                                    .build(ARouterConstants.LeaveInFoDetailActivity)
                                    .withString("leaveId",messages.get(position).getMessageExtend())
                                    .navigation();
                        }else if(messages.get(position).getMessageType()==2){//审批人消息
                            MyMessage myMessage = messages.get(position);
                            if(messages.get(position).getMessageExtend2()==4){//未审批
                                myMessage= checkDate(position);
                            }
                            if(myMessage!=null) {
                                ARouter.getInstance()
                                        .build(ARouterConstants.ApprovalLeaveInfoActivity)
                                        .withString("leaveId", myMessage.getMessageExtend())
                                        .withInt("type", myMessage.getMessageExtend2().intValue())
                                        .withObject("date", myMessage.getSendTime())
                                        .withString("approvalContent", myMessage.getMessageContent())
                                        .navigation();
                            }
                        }else if(messages.get(position).getMessageType()==4){//申请人消息
                            ARouter.getInstance()
                                    .build(ARouterConstants.ApplyInfoActivity)
                                    .withString("applyId",messages.get(position).getMessageExtend())
                                    .navigation();
                        }else if(messages.get(position).getMessageType()==5){//审批人消息
                            MyMessage myMessage = messages.get(position);
                            if(messages.get(position).getMessageExtend2()==4){
                                myMessage= checkDate(position);
                            }
                            if(myMessage!=null) {
                                ARouter.getInstance()
                                        .build(ARouterConstants.ApprovalApplyInfoActivity)
                                        .withString("applyId", myMessage.getMessageExtend())
                                        .withInt("type", myMessage.getMessageExtend2().intValue())
                                        .withString("content",myMessage.getMessageContent())
                                        .withInt("approverId",myMessage.getUserId().intValue())
                                        .withObject("date",myMessage.getSendTime())
                                        .navigation();
                            }else {
                                ARouter.getInstance()
                                        .build(ARouterConstants.ApprovalApplyInfoActivity)
                                        .withString("applyId",  messages.get(position).getMessageExtend())
                                        .withInt("type", 4)
                                        .navigation();
                            }
                        }else if(messages.get(position).getMessageType()==6){//权限发生改变
                            if("".equals(messages.get(position).getMessageExtend())) {
                                ARouter.getInstance()
                                        .build(ARouterConstants.AuthoritySettingActivity)
                                        .navigation();
                            }else {
                                ARouter.getInstance()
                                        .build(ARouterConstants.ApplyInfoActivity)
                                        .withString("applyId",messages.get(position).getMessageExtend())
                                        .withBoolean("isMsg",true)
                                        .navigation();
                            }
                        }
                        else if(messages.get(position).getMessageType()==11){//查看权限申请记录
                            ARouter.getInstance()
                                    .build(ARouterConstants.AppLyListActivity)
                                    .withObject("ARouterPath",ARouterConstants.ApplyInfoActivity)
                                    .withInt("type",1)//我的申请记录
                                    .navigation();
                        }

                    });
        }
    }

    /**
     * 检查该记录是否已经审批
     * @param position 检测的位置
     * @return r
     */
    private MyMessage checkDate(int position) {
        if(position<0)
            return  null;
        MyMessage myMessage = messages.get(position);
        int i;
        for( i= position-1 ; i >=0;i--){
            if(messages.get(i).getMessageExtend().equals(myMessage.getMessageExtend())&&//是不是同一条请假信息
                    messages.get(i).getMessageExtend2()!=4&&//是不是已经给出请假结果
                    (messages.get(i).getMessageType()==5|| messages.get(i).getMessageType()==2)&&//是不是审批人消息
                    messages.get(i).getSendTime().after(myMessage.getSendTime())) {//是不是在该条信息之后
                break;
            }
        }
        return messages.get((i>=0?i:position));
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


    class MsgHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time_item_tit)
        TextViewUniversalToast time;
        @BindView(R.id.icon_item_tit)
        ImageView img;
        @BindView(R.id.content_item_tit)
        TextView content;
        @BindView(R.id.main_layout_item_tit)
        LinearLayout layout;

        private MsgHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
