package com.definesys.dmportal.appstore.bean;

import java.util.List;

/**
 * Created by 羽翎 on 2019/3/3.
 */

public class ApplyAuthority {
    private List<String> listId;//多选的id
    private String signId;//单选的id
    private String content;//展示的内容
    private int autType;//权限类型 0.审批学生 1.审批老师
    private int authority;//权限
    private int type;//提示框的类型

    public ApplyAuthority(List<String> listId, String signId, int autType, int authority, int type) {
        this.listId = listId;
        this.signId = signId;
        this.content = content;
        this.autType = autType;
        this.authority = authority;
        this.type = type;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getListId() {
        return listId;
    }

    public void setListId(List<String> listId) {
        this.listId = listId;
    }

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getAutType() {
        return autType;
    }

    public void setAutType(int autType) {
        this.autType = autType;
    }

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }
}
