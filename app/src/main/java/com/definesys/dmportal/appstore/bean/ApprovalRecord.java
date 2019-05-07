package com.definesys.dmportal.appstore.bean;

import java.util.Date;

/**
 * 审批记录
 * Created by 羽翎 on 2019/1/17.
 */

public class ApprovalRecord {
    private String leaveInfoId;//请假消息id

    private int approverId;//审批人id

    private String approverName;//审批人姓名

    private String approvalContent;//审批意见

    private Short approvalResult;//审批结果0.不同意 1.同意

    private Date approvalTime;//审批时间

    private int approverType;//审批人类型
    //0.寝室长 1.班长 2.班主任 3.毕设导师 4.辅导员 5.学院实习工作负责人 6.学生工作领导 7.教学工作院长 -10.用户销假

    private String leaverName;//请假人姓名

    private int leaverId;//请假人id

    public ApprovalRecord() {

    }

    public ApprovalRecord(String approvalContent, Date approvalTime) {
        this.approvalContent = approvalContent;
        this.approvalTime = approvalTime;
    }
    public ApprovalRecord(String leaveInfoId, int approverId,String approvalContent, Short approvalResult, int approverType,int leaverId,String approverName) {
        this.leaveInfoId = leaveInfoId;
        this.approverId = approverId;
        this.approvalContent = approvalContent;
        this.approvalResult = approvalResult;
        this.approverType = approverType;
        this.leaverId = leaverId;
        this.approverName = approverName;
    }

    public String getLeaveInfoId() {
        return leaveInfoId==null?"":leaveInfoId;
    }

    public void setLeaveInfoId(String leaveInfoId) {
        this.leaveInfoId = leaveInfoId;
    }

    public int getApproverId() {
        return approverId;
    }

    public void setApproverId(int approverId) {
        this.approverId = approverId;
    }

    public String getApprovalContent() {
        return approvalContent==null?"":approvalContent;
    }

    public void setApprovalContent(String approvalContent) {
        this.approvalContent = approvalContent;
    }

    public Short getApprovalResult() {
        return approvalResult;
    }

    public void setApprovalResult(Short approvalResult) {
        this.approvalResult = approvalResult;
    }

    public Date getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(Date approvalTime) {
        this.approvalTime = approvalTime;
    }

    public int getApproverType() {
        return approverType;
    }

    public void setApproverType(int approverType) {
        this.approverType = approverType;
    }

    public String getLeaverName() {
        return leaverName;
    }

    public int getLeaverId() {
        return leaverId;
    }

    public void setLeaverId(int leaverId) {
        this.leaverId = leaverId;
    }

    public void setLeaverName(String leaverName) {
        this.leaverName = leaverName;
    }

    public String getApproverName() {
        return approverName==null?"":approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
}
