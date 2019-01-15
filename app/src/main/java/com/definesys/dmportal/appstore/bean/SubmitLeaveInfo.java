package com.definesys.dmportal.appstore.bean;

import java.util.Date;

/**
 * Created by 羽翎 on 2019/1/10.
 */

public class SubmitLeaveInfo {
    private String id;//请假编号
    private Number userId;//用户id
    private String userName;//姓名
    private String leaveReason;//具体原因
    private String startTime;//开始时间
    private String endTime;//结束时间
    private String leaveType;//请假类型
    private String leaveTitle;//请假标题（原因）
    private String subTime;//时长
    private String selectedSubject;//选择课表
    private int type;//0.课假 1.短假 2.长假
    private Date submitDate;//提交日期
    private String picUrl;//图片url用*号隔开

    public SubmitLeaveInfo(Number id,String name, String content, String startTime, String endTime, String leaveType, String leaveTitle, String subTime, String selectedSubject,int type) {
        this.userId = id;
        this.userName = name;
        this.leaveReason = content;
        this.startTime = startTime;
        this.endTime = endTime;
        this.leaveType = leaveType;
        this.leaveTitle = leaveTitle;
        this.subTime = subTime;
        this.selectedSubject = selectedSubject;
        this.type = type;
    }


    public String getLeaveReason() {
        return leaveReason;
    }

    public void setLeaveReason(String content) {
        this.leaveReason = content;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveTitle() {
        return leaveTitle;
    }

    public void setLeaveTitle(String leaveTitle) {
        this.leaveTitle = leaveTitle;
    }

    public String getSubTime() {
        return subTime;
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
    }

    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public Number getUserId() {
        return userId;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String d) {
        this.id = d;
    }
}
