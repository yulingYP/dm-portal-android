package com.definesys.dmportal.appstore.bean;

import java.io.Serializable;
import java.util.Date;

public class ApplyRecord implements Serializable {
    private String applyId;//信息id

    private Integer applyerId;//申请人id

    private String applyerName;//申请人姓名

    private Integer approverId;//审批人id

    private String approverName;//审批人姓名

    private Short applyStatus;//审批状态  0.不同意 1.同意

    private String applyContent;//审批内容

    private Date approvalDate;//审批日期

    private String attribute;

    private String attribute1;

    private String attribute2;



    private static final long serialVersionUID = 1L;

    public String getApplyId() {
        return applyId;
    }
    public ApplyRecord(){}

    public ApplyRecord(String applyId,Integer applyerId, String applyerName, Integer approverId,  Short applyStatus, String applyContent,String approverName) {
        this.applyId = applyId;
        this.applyerName = applyerName;
        this.approverId = approverId;
        this.applyerId = applyerId;
        this.applyStatus = applyStatus;
        this.applyContent = applyContent;
        this.approverName = approverName;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId == null ? null : applyId.trim();
    }

    public Integer  getApplyerId() {
        return applyerId;
    }

    public void setApplyerId(Integer  applyerId) {
        this.applyerId = applyerId;
    }

    public Short getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Short applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getApplyContent() {
        return applyContent;
    }

    public void setApplyContent(String applyContent) {
        this.applyContent = applyContent == null ? null : applyContent.trim();
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute == null ? null : attribute.trim();
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

    public String getApplyerName() {
        return applyerName;
    }

    public void setApplyerName(String applyerName) {
        this.applyerName = applyerName;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName==null?"":approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }
}