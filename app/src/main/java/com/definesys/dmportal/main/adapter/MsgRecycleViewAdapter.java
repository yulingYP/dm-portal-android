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
import com.definesys.dmportal.main.bean.Message;
import com.jakewharton.rxbinding2.view.RxView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MsgRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MyMessage> messages;
    private Context context;
    //  点击事件的预留变量
    private OnClickListener onClickListener;
    private SimpleDateFormat sdf;


    public MsgRecycleViewAdapter(Context context, List<MyMessage> messages) {
        this.context = context;
        this.messages = messages;
        this.sdf=new SimpleDateFormat(context.getString(R.string.date_type_2));
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
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_refuse);
                    viewHolder.content.setText(R.string.message_tip_1);
                }else if(messages.get(position).getMessageExtend2()==1) {//同意
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_accept);
                    viewHolder.content.setText(R.string.message_tip_2);
                }
                else if(messages.get(position).getMessageExtend2()==2) {//已提交
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_await);
                    viewHolder.content.setText(R.string.message_tip_3);
                }else if(messages.get(position).getMessageExtend2()==3) {//已销假
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_await);
                    viewHolder.content.setText(R.string.message_tip_4);
                }

            }else if(messages.get(position).getMessageType()==2){//审批人消息
                String id = messages.get(position).getMessageExtend();
                id=id.length()>9?id.substring(0,9):id;
                if(messages.get(position).getMessageExtend2()==0) {//拒绝
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_refuse);
                    viewHolder.content.setText(context.getString(R.string.message_tip_5,id));
                }else if(messages.get(position).getMessageExtend2()==1) {//同意
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_accept);
                    viewHolder.content.setText(context.getString(R.string.message_tip_6,id));
                }else if(messages.get(position).getMessageExtend2()==4) {//未审批
                    viewHolder.img.setImageResource(R.drawable.ic_msg_status_await);
                    viewHolder.content.setText(context.getString(R.string.message_tip_7,id));
                }
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
                            if(messages.get(position).getMessageExtend2()==4){
                                myMessage= checkDate(position);
                            }
                            ARouter.getInstance()
                                    .build(ARouterConstants.ApprovalLeaveInfoActivity)
                                    .withString("leaveId",myMessage.getMessageExtend())
                                    .withInt("type",myMessage.getMessageExtend2())
                                    .withObject("approvalDate",myMessage.getMessageExtend3())
                                    .withString("approvalContent",myMessage.getMessageContent())
                                    .navigation();
                        }

                    });
        }
    }

    /**
     * 检查该记录是否已经审批
     * @param position 检测的位置
     * @return
     */
    private MyMessage checkDate(int position) {
        if(position<0)
            return null;
        MyMessage myMessage = messages.get(position);
        int i;
        for( i= position-1 ; i >=0;i--){
            if(messages.get(i).getMessageExtend().equals(myMessage.getMessageExtend())&&//是不是同一条请假信息
                    messages.get(i).getMessageExtend2()!=4&&//是不是已经给出请假结果
                    messages.get(i).getSendTime().after(myMessage.getSendTime())) {//是不是在该条信息之后
                break;
            }
        }
        return messages.get((i>=0?i:position));
    }
    /**
     * 获取结果消息
     * @return
     */
    public MyMessage getMessage(MyMessage myMessage) {
        MyMessage result = null;
        if(messages!=null&&messages.contains(myMessage)){
            result = checkDate(messages.indexOf(myMessage));
        }

        return result;
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

    public void setOnClickListener(OnClickListener clickListener) {
        this.onClickListener = clickListener;
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

        public MsgHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnClickListener {
        void OnClick(int position);
    }

}
