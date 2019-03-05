package com.definesys.dmportal.appstore.bean;

import java.io.Serializable;
import java.util.Date;

public class ApplyRecord implements Serializable {
    private String applyId;//信息id

    private Integer applyerId;//审批人id

    private Short applyStatus;//审批状态  0.不同意 1.同意

    private String applyContent;//审批内容

    private Date applyDate;//审批日期

    private String attribute;

    private String attribute1;

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private static final long serialVersionUID = 1L;

    public String getApplyId() {
        return applyId;
    }
    public ApplyRecord(){}
    public ApplyRecord(String applyId, Integer applyerId, Short applyStatus, String applyContent) {
        this.applyId = applyId;
        this.applyerId = applyerId;
        this.applyStatus = applyStatus;
        this.applyContent = applyContent;
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

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
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
}