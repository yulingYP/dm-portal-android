package com.definesys.dmportal.main.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.definesys.dmportal.main.bean.User;

import static android.content.Context.MODE_PRIVATE;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spFaculty;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spFileName;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spToken;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserId;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserImage;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserLocalimg;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserName;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserPhone;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserSex;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserUrl;

public class SharedPreferencesUtil {
    private static SharedPreferencesUtil instance;
    private static SharedPreferences sp;
    private static Context context;

    public SharedPreferencesUtil() {
        if(sp==null) {
            sp = context.getSharedPreferences(spFileName, MODE_PRIVATE);
        }
    }

    public static SharedPreferencesUtil getInstance(){
        if(instance==null) {
            instance = new SharedPreferencesUtil();
        }
        return instance;
    }

    public static void setContext(Context context) {
        SharedPreferencesUtil.context = context;
    }
    public SharedPreferences.Editor getSpWithEdit() {
        return sp.edit();
    }

    //-----------------------SET-------------------
    public User getUser(){
        User user = new User();
        user.setUserName(getUserName());
        user.setUserId(getUserId());
        user.setUserSex(getUserSex());

        user.setUrl(getUserUrl());
        user.setLocalimg(getUserLocal());

        return user;
    }
    public String getUserName() {
        return sp.getString(spUserName, "");
    }

    public Number getUserId() {
        return sp.getInt(spUserId, 0);
    }

    public String getUserSex() {
        return sp.getString(spUserSex, "");
    }

    public String getUserImage() {
        return sp.getString(spUserImage, "");
    }

    public String getToken() {   return sp.getString(spToken,"");}

    public String getFaculty() {   return sp.getString(spFaculty,"");}

    public String getUserUrl() {   return sp.getString(spUserUrl,"");}

    public String getUserLocal() {   return sp.getString(spUserLocalimg,"");}


    //-----------------------SET-------------------
    public SharedPreferences.Editor setUser(User user) {
        if(user!=null) {
            SharedPreferences.Editor editor = getSpWithEdit()
                    .putInt(spUserId, (Integer) user.getUserId())
                    .putString(spUserName, user.getUserName())
                    .putString(spUserPhone, user.getUserPhone())
                    .putString(spUserSex, user.getUserSex())
                    .putString(spFaculty,user.getFaculty())
                    .putString(spUserUrl,user.getUrl())
                    .putString(spUserLocalimg,user.getLocalimg());
            editor.apply();
            return editor;
        }
        return getSpWithEdit();
    }
    public SharedPreferences.Editor setUserName(String userName) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserName, userName);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserCode(String userPhone) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserPhone, userPhone);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserSex(String userSex) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserSex, userSex);
        return editor;
    }


    public SharedPreferences.Editor setUserImage(String userImage) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserImage, userImage);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setToken(String sptoken) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spToken, sptoken);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserUrl(String userurl) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserUrl, userurl);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserLocal(String userlocalimg) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserLocalimg, userlocalimg);
        editor.apply();
        return editor;
    }
}
