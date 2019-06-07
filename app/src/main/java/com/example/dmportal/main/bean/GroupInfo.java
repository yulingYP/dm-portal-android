package com.example.dmportal.main.bean;

/**
 * Created by 羽翎 on 2018/11/22.
 */

public class GroupInfo {
    private String groupName;
    private String gruopType;
    private String groupDes;
    private String uri;


    public GroupInfo(String groupName, String gruopType, String groupDes, String uri) {
        this.groupName = groupName;
        this.gruopType = gruopType;
        this.groupDes = groupDes;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGruopType() {
        return gruopType;
    }

    public void setGruopType(String gruopType) {
        this.gruopType = gruopType;
    }

    public String getGroupDes() {
        return groupDes;
    }

    public void setGroupDes(String groupDes) {
        this.groupDes = groupDes;
    }
}
