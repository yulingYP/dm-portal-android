package com.definesys.dmportal.appstore.bean;

import java.util.Date;

public class User{
    private int userId;//用户id

    private String password;//密码

    private String token;//token

    private String phone;//电话

    private Short userType;//用户类型

    private String userImage;//用户头像

    private Date lastLoginDate;//上次登陆时间

    private Date lastLogoutDate;//上次登出时间

    private Short leaveAuthority;//请假审批权限

    private String name;//姓名


    //《-----学生----》
    private String classId; //班级Id

    private String facultId;//院系id

    private Date enrolDate;//入学时间

    private Short tutorId;//毕设导师id

    private Short dormitorLeaderId; //寝室长id

    private String facultName;//院系名称


    //《-----教师----》
    private String teacherLevel;//等级

    private String attribute2;

    private String attribute3;

    private String attribute4;

    private String attribute5;




    public String getTeacherLevel() {
        return teacherLevel;
    }

    public void setTeacherLevel(String teacherLevel) {
        this.teacherLevel = teacherLevel == null ? null : teacherLevel.trim();
    }
    public String getName() {
        return name;
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
        return facultId;
    }

    public void setFacultId(String facultId) {
        this.facultId = facultId == null ? null : facultId.trim();
    }

    public Date getEnrolDate() {
        return enrolDate;
    }

    public void setEnrolDate(Date enrolDate) {
        this.enrolDate = enrolDate;
    }

    public Short getTutorId() {
        return tutorId;
    }

    public void setTutorId(Short tutorId) {
        this.tutorId = tutorId;
    }

    public Short getDormitorLeaderId() {
        return dormitorLeaderId;
    }

    public void setDormitorLeaderId(Short dormitorLeaderId) {
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
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Short getUserType() {
        return userType;
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

    public Short getLeaveAuthority() {
        return leaveAuthority;
    }

    public void setLeaveAuthority(Short leaveAuthority) {
        this.leaveAuthority = leaveAuthority;
    }

    public String getFacultName() {
        return facultName==null?"":facultName;
    }

    public void setFacultName(String facultName) {
        this.facultName = facultName==null?"":facultName;
    }
}
