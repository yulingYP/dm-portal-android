package com.definesys.dmportal.appstore.bean;

import java.util.Date;

/**
 * Created by 羽翎 on 2019/1/10.
 */

public class LeaveInfo {
    public String id;//请假编号
    public Number userId;//用户id
    public String userName;//姓名
    public String leaveReason;//具体原因
    public String startTime;//开始时间
    public String endTime;//结束时间
    public String leaveType;//请假类型
    public String leaveTitle;//请假标题（原因）
    public String subTime;//时长
    public String selectedSubject;//选择课表
    Short approvalStatus;//审批状态 1.正在审批 10.已批准 11.已拒绝
    int type;//0.课假 1.短假 2.长假 3.实习
    public Date submitDate;//提交日期
    public String picUrl;//图片url用*号隔开
    public Date updateDate;//更新日期
    public int userType;//用户类型

    public LeaveInfo() {
    }

    public LeaveInfo(Number id, String name, String content, String startTime, String endTime, String leaveType, String leaveTitle, String subTime, String selectedSubject, int type,int userType) {
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
        this.userType = userType;
    }


    public String getLeaveReason() {
        return leaveReason==null?"":leaveReason;
    }

    public void setLeaveReason(String content) {
        this.leaveReason = content;
    }

    public String getStartTime() {
        return startTime==null?"":startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime==null?"":endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLeaveType() {
        return leaveType==null?"":leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getLeaveTitle() {
        return leaveTitle==null?"":leaveTitle;
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
        return selectedSubject==null?"":selectedSubject;
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
        return userName==null?"":userName;
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
        return picUrl==null?"":picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getId() {
        return id==null?"":id;
    }

    public void setId(String d) {
        this.id = d;
    }

    public Short getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Short approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }
}
