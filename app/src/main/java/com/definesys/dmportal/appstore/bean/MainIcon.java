package com.definesys.dmportal.appstore.bean;

/**
 * Created by 羽翎 on 2019/1/4.
 */

import java.net.URI;

/**
 * 主页功能图标
 */
public class MainIcon {
    private String name;//功能名称
    private String iconUrl;//图标下载路径
    private int tempURL;//本地路径
    private String aRounterPath;//跳转路径

    public MainIcon(String name, int tempURL, String aRounterPath) {
        this.name = name;
        this.tempURL = tempURL;
        this.aRounterPath = aRounterPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getTempURL() {
        return tempURL;
    }

    public void setTempURL(int tempURL) {
        this.tempURL = tempURL;
    }

    public String getaRounterPath() {
        return aRounterPath;
    }

    public void setaRounterPath(String aRounterPath) {
        this.aRounterPath = aRounterPath;
    }
}
