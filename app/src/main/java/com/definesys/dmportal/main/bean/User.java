package com.definesys.dmportal.main.bean;

public class User{
    private String userName;//姓名
    private Number userId;//编号
    private String userPhone;//用户手机
    private String faculty;//院系编号
    private String userSex;//性别
    private String userPassword;//密码
    private String url;//头像url
    private String localimg;//本地头像url

    public User() {
        userName = "小白";
        userId = 151110401;
        userSex = "男";
        faculty="111";
        userPassword = "";
        userPhone="17863136613";
        url = "";
        localimg = "";
    }

    public User(String userName, Number userId, String factyId, String userSex, String userPassword, String url, String localimg) {
        this.userName = userName;
        this.userId = userId;
        this.faculty = factyId;
        this.userSex = userSex;
        this.userPassword = userPassword;
        this.url = url;
        this.localimg = localimg;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Number getUserId() {
        return userId;
    }

    public void setUserId(Number userId) {
        this.userId = userId;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String factyId) {
        this.faculty = factyId;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalimg() {
        return localimg;
    }

    public void setLocalimg(String localimg) {
        this.localimg = localimg;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
