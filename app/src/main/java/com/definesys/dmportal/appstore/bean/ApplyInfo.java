package com.definesys.dmportal.appstore.bean;

import java.io.Serializable;
import java.util.Date;

public class ApplyInfo implements Serializable {
    private String applyId;//信息id

    private Integer applyUserId;//申请人id

    private Integer applyAuthorityType;//权限类型 0.审批学生 1.审批教师

    private Integer applyAuthority;//权限
                                    //0.宿舍长权限 1.班长 2.班主任 3.毕设老师 4.辅导员
                                    // 5.学院实习工作负责人 6.教学工作领导 7.教学工作院长
                                    // 0.部门请假负责人 1.部门教学负责人
    private String applyReason;//申请原因

    private String applyRegion;//申请范围 班级id，学生id，院系名...

    private Date applyDate;//申请日期

    private Short applyStatus;//审批状态 0.未审批 100.已通过 110.已拒绝 -100:修改但不删除权限 -110：删除权限

    private int type;//提示框种类

    private String applyUserName;//申请人姓名

    private Date applyUpdateDate;//更新日期

    private String applyDetailContent;//申请的具体内容

    private Integer afterDeleteAut;//删除权限后用户剩余的权限

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private static final long serialVersionUID = 1L;
    public ApplyInfo(){

    }
    public ApplyInfo(String applyId, Integer applyUserId, Integer applyAuthorityType, Integer applyAuthority, String applyRegion, int type) {
        this.applyId = applyId;
        this.applyUserId = applyUserId;
        this.applyAuthorityType = applyAuthorityType;
        this.applyAuthority = applyAuthority;
        this.applyRegion = applyRegion;
        this.applyStatus = 0;
        this.type = type;
    }

    public ApplyInfo(String applyId, Integer applyUserId, Integer applyAuthorityType, Integer applyAuthority, String applyRegion, Short applyStatus, String applyUserName) {
        this.applyId = applyId;
        this.applyUserId = applyUserId;
        this.applyAuthorityType = applyAuthorityType;
        this.applyAuthority = applyAuthority;
        this.applyRegion = applyRegion;
        this.applyStatus = applyStatus;
        this.applyUserName = applyUserName;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId == null ? null : applyId.trim();
    }

    public Integer getApplyUserId() {
        return applyUserId;
    }

    public void setApplyUserId(Integer applyUserId) {
        this.applyUserId = applyUserId;
    }

    public Integer getApplyAuthorityType() {
        return applyAuthorityType;
    }

    public void setApplyAuthorityType(Integer applyAuthorityType) {
        this.applyAuthorityType = applyAuthorityType;
    }

    public Integer getApplyAuthority() {
        return applyAuthority;
    }

    public void setApplyAuthority(Integer applyAuthority) {
        this.applyAuthority = applyAuthority;
    }

    public String getApplyReason() {
        return applyReason;
    }

    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason == null ? null : applyReason.trim();
    }

    public String getApplyDetailContent() {
        return applyDetailContent;
    }

    public void setApplyDetailContent(String applyDetailContent) {
        this.applyDetailContent = applyDetailContent == null ? null : applyDetailContent.trim();
    }

    public String getApplyRegion() {
        return applyRegion;
    }

    public void setApplyRegion(String applyRegion) {
        this.applyRegion = applyRegion == null ? null : applyRegion.trim();
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public Short getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Short applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getApplyUserName() {
        return applyUserName;
    }

    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    public Date getApplyUpdateDate() {
        return applyUpdateDate;
    }

    public void setApplyUpdateDate(Date applyUpdateDate) {
        this.applyUpdateDate = applyUpdateDate;
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

    public Integer getAfterDeleteAut() {
        return afterDeleteAut;
    }

    public void setAfterDeleteAut(Integer afterDeleteAut) {
        this.afterDeleteAut = afterDeleteAut;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}