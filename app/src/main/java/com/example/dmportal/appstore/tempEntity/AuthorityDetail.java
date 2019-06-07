package com.example.dmportal.appstore.tempEntity;

/**
 *
 * Created by 羽翎 on 2019/3/8.
 */

public class AuthorityDetail {
    private String region; //范围
    private Integer userAuthority; //用户权限

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getUserAuthority() {
        return userAuthority;
    }

    public void setUserAuthority(Integer userAuthority) {
        this.userAuthority = userAuthority;
    }
}
