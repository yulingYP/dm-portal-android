package com.example.dmportal.appstore.bean;

import java.util.Date;

public class User{
    //《-----用户----》
    private int userId;//用户id

    private String password;//密码

    private String token;//token

    private String phone;//电话

    private Short userType;//用户类型 0.学生 1.教师

    private Short userSex;//用户性别 1.男 2.女

    private String userImage;//用户头像URL

    private String userSign;//用户签名URL

    private Date lastLoginDate;//上次登陆时间

    private Date lastLogoutDate;//上次登出时间

    private Integer leaveAuthority;//学生请假审批权限

    private Integer leaveTeacherAuthority;//教师请假审批权限

    private String name;//姓名


    //《-----学生----》
    private String classId; //班级Id

    private String facultyId;//院系id

    private Date enrolDate;//入学时间

    private Integer tutorId;//毕设导师id

    private Integer dormitorLeaderId; //寝室长id

    private String className;//班级名称

    private String facultyName;//院系名称


    //《-----教师----》
    private String branchId;//请假部门编号

    private String branchName;//部门名称

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;


    public String getBranchId() {
        return branchId==null?"": branchId;
    }

    public void setBranchId(String brachId) {
        this.branchId = brachId==null?"": brachId;
    }

    public String getName() {
        return name==null?"":name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId == null ? null : classId.trim();
    }

    public String getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(String facultyId) {
        this.facultyId = facultyId == null ? null : facultyId.trim();
    }

    public Date getEnrolDate() {
        return enrolDate;
    }

    public void setEnrolDate(Date enrolDate) {
        this.enrolDate = enrolDate;
    }

    public Integer getTutorId() {
        return tutorId;
    }

    public void setTutorId(Integer tutorId) {
        this.tutorId = tutorId;
    }

    public Integer getDormitorLeaderId() {
        return dormitorLeaderId;
    }

    public void setDormitorLeaderId(Integer dormitorLeaderId) {
        this.dormitorLeaderId = dormitorLeaderId;
    }

    public String getAttribute5() {
        return attribute5;
    }

    public void setAttribute5(String attribute5) {
        this.attribute5 = attribute5 == null ? null : attribute5.trim();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getPhone() {
        return phone==null?"":phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Short getUserType() {
        return userType==null?-1:userType;
    }

    public void setUserType(Short userType) {
        this.userType = userType;
    }

    public String getUserImage() {
        return userImage==null?"":userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage == null ? null : userImage.trim();
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastLogoutDate() {
        return lastLogoutDate;
    }

    public void setLastLogoutDate(Date lastLogoutDate) {
        this.lastLogoutDate = lastLogoutDate;
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

    public Integer getLeaveAuthority() {
        return leaveAuthority==null?-1:leaveAuthority;
    }

    public void setLeaveAuthority(Integer leaveAuthority) {
        this.leaveAuthority = leaveAuthority;
    }

    public Integer getLeaveTeacherAuthority() {
        return leaveTeacherAuthority==null?-1:leaveTeacherAuthority;
    }

    public void setLeaveTeacherAuthority(Integer leaveTeacherAuthority) {
        this.leaveTeacherAuthority = leaveTeacherAuthority;
    }

    public String getFacultyName() {
        return facultyName==null?"":facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName==null?"":facultyName;
    }

    public Short getUserSex() {
        return userSex==null?-1:userSex;
    }

    public void setUserSex(Short userSex) {
        this.userSex = userSex;
    }

    public String getUserSign() {
        return userSign==null?"":userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getClassName() {
        return this.className == null?"":className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
