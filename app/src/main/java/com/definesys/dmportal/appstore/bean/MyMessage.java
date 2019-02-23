package com.definesys.dmportal.appstore.bean;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by 羽翎 on 2019/2/20.
 */

public class MyMessage implements Comparable<MyMessage>{
    private Number userId;// 用户id

    private Short messageType;//消息类型 1.请假人的请假结果 2.审批人的审批结果

    private String messageContent;//消息内容

    private String messageExtend;//扩展字段 String型 可用户保存请假信息id等

    private Short messageExtend2;//扩展字段 number型 可用于保存审批结果、请假结果  0.不同意 1.同意  2.正在审核 3.销假

    private Date messageExtend3;//扩展字段 date型 可用户保存审批提交的时间等

    private Date sendTime;//发送时间

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;

    public MyMessage(){

    }
    public MyMessage(Number userId, Short messageType, String messageContent, Short messageExtend2, String messageExtend,Date messageExtend3, Date sendTime) {
        this.userId = userId;
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.messageExtend2 = messageExtend2;
        this.messageExtend = messageExtend;
        this.sendTime = sendTime;
        this.messageExtend3 = messageExtend3;
    }

    public Number getUserId() {
        return userId;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }

    public Short getMessageType() {
        return messageType;
    }

    public void setMessageType(Short messageType) {
        this.messageType = messageType;
    }

    public String getMessageContent() {
        return messageContent==null?"":messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent == null ? null : messageContent.trim();
    }

    public Short getMessageExtend2() {
        return messageExtend2;
    }

    public void setMessageExtend2(Short messageExtend2) {
        this.messageExtend2 = messageExtend2;
    }

    public String getMessageExtend() {
        return messageExtend;
    }

    public void setMessageExtend(String messageExtend) {
        this.messageExtend = messageExtend == null ? null : messageExtend.trim();
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1 == null ? null : attribute1.trim();
    }

    public String getAttribute2() {
        return attribute2;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2 == null ? null : attribute2.trim();
    }

    public String getAttribute3() {
        return attribute3;
    }

    public void setAttribute3(String attribute3) {
        this.attribute3 = attribute3 == null ? null : attribute3.trim();
    }

    public String getAttribute4() {
        return attribute4;
    }

    public void setAttribute4(String attribute4) {
        this.attribute4 = attribute4 == null ? null : attribute4.trim();
    }

    public String getAttribute5() {
        return attribute5;
    }

    public void setAttribute5(String attribute5) {
        this.attribute5 = attribute5 == null ? null : attribute5.trim();
    }

    @Override
    public int compareTo(@NonNull MyMessage o) {
        return  o.getSendTime().compareTo(this.getSendTime());
    }

    public Date getMessageExtend3() {
        return messageExtend3;
    }

    public void setMessageExtend3(Date messageExtend3) {
        this.messageExtend3 = messageExtend3;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "userId=" + userId +
                ", messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                ", messageExtend='" + messageExtend + '\'' +
                ", messageExtend2=" + messageExtend2 +
                ", messageExtend3=" + messageExtend3 +
                ", sendTime=" + sendTime +
                ", attribute1='" + attribute1 + '\'' +
                ", attribute2='" + attribute2 + '\'' +
                ", attribute3='" + attribute3 + '\'' +
                ", attribute4='" + attribute4 + '\'' +
                ", attribute5='" + attribute5 + '\'' +
                '}';
    }
}
