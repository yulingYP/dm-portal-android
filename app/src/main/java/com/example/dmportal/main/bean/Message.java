package com.example.dmportal.main.bean;

public class Message {
    private String msgId;
    private String userCode;
    private String msgDate;
    private String msgStatus;
    private String msgTitle;
    private String msgContent;
    private int msgIcon;

    public Message() {
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgId() {
        return msgId==null?"":msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getUserCode() {
        return userCode==null?"":userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getMsgDate() {
        return msgDate ==null?"": msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgStatus() {
        return msgStatus==null?msgStatus:msgStatus;
    }

    public void setMsgStatus(String msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getMsgContent() {
        return msgContent==null?msgContent:msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public int getMsgIcon() {
        return msgIcon;
    }

    public void setMsgIcon(int msgIcon) {
        this.msgIcon = msgIcon;
    }
}
