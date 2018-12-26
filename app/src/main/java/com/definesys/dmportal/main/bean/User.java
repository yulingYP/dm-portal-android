package com.definesys.dmportal.main.bean;

public class User{
    private String userName;
    private String userCode;
    private String userSex;
    private String userAddress;
    private String userDetailAddress;
    private String userPassword;
    private String attachmentId;
    private String agreementId;
    private String groupRegion;
    private String useroption;
    private String url;
    private String localimg;

    public User() {
        userName = "";
        userCode = "";
        userSex = "";
        userAddress = "";
        userDetailAddress = "";
        userPassword = "";
        attachmentId = "";
        agreementId = "";
        groupRegion = "";
        url = "";
        localimg = "";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserDetailAddress() {
        return userDetailAddress;
    }

    public void setUserDetailAddress(String userDetailAddress) {
        this.userDetailAddress = userDetailAddress;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getAgreementId() {
        return agreementId;
    }

    public void setAgreementId(String agreementId) {
        this.agreementId = agreementId;
    }

    public String getGroupRegion() {
        return groupRegion;
    }

    public void setGroupRegion(String groupRegion) {
        this.groupRegion = groupRegion;
    }

    public String getUseroption() {
        return useroption;
    }

    public void setUseroption(String useroption) {
        this.useroption = useroption;
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

    public User(String userName, String userCode, String userSex,
                String userAddress, String userDetailAddress, String userPassword,
                String attachmentId, String agreementId, String groupRegion, String useroption,
                String url,String localimg) {
        this.userName = userName;
        this.userCode = userCode;
        this.userSex = userSex;
        this.userAddress = userAddress;
        this.userDetailAddress = userDetailAddress;
        this.userPassword = userPassword;
        this.attachmentId = attachmentId;
        this.agreementId = agreementId;
        this.groupRegion = groupRegion;
        this.useroption = useroption;
        this.url = url;
        this.localimg = localimg;
    }
}
