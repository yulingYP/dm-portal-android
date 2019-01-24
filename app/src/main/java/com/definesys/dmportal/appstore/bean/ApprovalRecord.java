package com.definesys.dmportal.appstore.bean;

import java.util.Date;

/**
 * 审批记录
 * Created by 羽翎 on 2019/1/17.
 */

public class ApprovalRecord {
    private String leaveInfoId;//请假消息id

    private int approverId;//审批人id

    private String approvalContent;//审批意见

    private Short approvalResult;//审批结果0.不同意 1.同意

    private Date approvalTime;//审批时间

    private Short approverType;//审批人类型
    //0.寝室长 1.班长 2.班主任 3.辅导员

    private String leaverName;//请假人姓名

    public ApprovalRecord() {

    }

    public ApprovalRecord(String leaveInfoId, int approverId, String approvalContent, Short approvalResult, Date approvalTime, Short approverType) {
        this.leaveInfoId = leaveInfoId;
        this.approverId = approverId;
        this.approvalContent = approvalContent;
        this.approvalResult = approvalResult;
        this.approvalTime = approvalTime;
        this.approverType = approverType;
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

    public Short getApproverType() {
        return approverType;
    }

    public void setApproverType(Short approverType) {
        this.approverType = approverType;
    }

    public String getLeaverName() {
        return leaverName;
    }

    public void setLeaverName(String leaverName) {
        this.leaverName = leaverName;
    }
}
