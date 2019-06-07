package com.example.dmportal.appstore.bean;

import java.util.Date;

/**
 *
 * Created by 羽翎 on 2019/2/20.
 */

public class MyMessage {
    private Long messageId;// 消息id

    private Number userId;// 用户id

    private Short messageType;//消息类型 1.请假人的请假结果 2.审批人的审批结果

    private String messageContent;//消息内容

    private String messageExtend;//扩展字段 String型 可用户保存请假信息id等

    private Short messageExtend2;//扩展字段 number型 可用于保存审批结果、请假结果  0.不同意 1.同意  2.正在审核 3.销假


    private Date sendTime;//发送时间

    private Short pushResult;//信息状态 0.未读 1.已读 2.推送失败

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;

    public MyMessage(){

    }
    public MyMessage(Long messageId,Number userId, Short messageType, String messageContent, Short messageExtend2, String messageExtend, Date sendTime) {
        this.messageId =messageId;
        this.userId = userId;
        this.messageType = messageType;
        this.messageContent = messageContent;
        this.messageExtend2 = messageExtend2;
        this.messageExtend = messageExtend;
        this.sendTime = sendTime;
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
        return messageExtend==null?"":messageExtend;
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
    public String toString() {
        return "MyMessage{" +
                "userId=" + userId +
                ", messageType=" + messageType +
                ", messageContent='" + messageContent + '\'' +
                ", messageExtend='" + messageExtend + '\'' +
                ", messageExtend2=" + messageExtend2 +
                ", sendTime=" + sendTime +
                ", attribute1='" + attribute1 + '\'' +
                ", attribute2='" + attribute2 + '\'' +
                ", attribute3='" + attribute3 + '\'' +
                ", attribute4='" + attribute4 + '\'' +
                ", attribute5='" + attribute5 + '\'' +
                '}';
    }

    public Long getMessageId() {
        return messageId==null?-1:messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Short getPushResult() {
        return pushResult;
    }

    public void setPushResult(Short pushResult) {
        this.pushResult = pushResult;
    }
}
