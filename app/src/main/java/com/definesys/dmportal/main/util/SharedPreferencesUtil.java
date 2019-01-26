package com.definesys.dmportal.main.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.definesys.dmportal.appstore.bean.User;

import static android.content.Context.MODE_PRIVATE;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.isFirstOpen;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spClass;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spFaculty;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spFacultyName;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spFileName;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spToken;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserAuthority;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserId;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserLocalimg;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserName;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserPhone;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserSex;
import static com.definesys.dmportal.main.interfaces.SharedPreferencesParams.spUserType;
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
            synchronized (SharedPreferencesUtil.class) {
                if(instance==null)
                instance = new SharedPreferencesUtil();
            }
        }
        return instance;
    }

    public static void setContext(Context context) {
        SharedPreferencesUtil.context = context;
    }
    public SharedPreferences.Editor getSpWithEdit() {
        return sp.edit();
    }

    //-----------------------GET-------------------
    public User getUser(){
        User user = new User();
        user.setUserId(getUserId().intValue());
        user.setUserType((short) getUserType());
        user.setName(getUserName());
        user.setFacultId(getFaculty());
        user.setClassId(getClassId());
//        user.setUserSex(getUserSex());
        user.setLeaveAuthority((short)getUserAuthority());
        user.setUserImage(getUserImageUrl());
        user.setPhone(getUserPhone());
        return user;
    }
    public boolean isFirstOpen() {
        return sp.getBoolean(isFirstOpen, true);
    }
    public String getUserName() {
        return sp.getString(spUserName, "");
    }

    public Number getUserId() {
        return sp.getInt(spUserId, -1);
    }

    public String getUserSex() {
        return sp.getString(spUserSex, "");
    }

    public String getToken() {   return sp.getString(spToken,"");}

    public String getFaculty() {   return sp.getString(spFaculty,"");}

    public String getFacultyName() {   return sp.getString(spFacultyName,"");}

    public String getClassId() {   return sp.getString(spClass,"");}

    public String getUserImageUrl() {   return sp.getString(spUserUrl,"");}

    public String getUserLocal() {   return sp.getString(spUserLocalimg,"");}

    public String getUserPhone() {   return sp.getString(spUserPhone,"");}

    public int getUserType() {   return sp.getInt(spUserType,0);}
    public int getUserAuthority() {   return sp.getInt(spUserAuthority,-1);}
    //-----------------------SET-------------------
    public SharedPreferences.Editor setUser(User user) {
        if(user!=null) {
            SharedPreferences.Editor editor = getSpWithEdit()
                    .putString(spUserName, user.getName())//姓名
                    .putString(spUserPhone, user.getPhone())//电话
//                    .putString(spUserSex, user.getUserSex())
                    .putString(spFaculty,user.getFacultyId())//院系id
                    .putString(spFacultyName,user.getFacultName())//院系名称
                    .putString(spClass,user.getClassId())//班级
                    .putString(spUserUrl,user.getUserImage())//用户头像
//                    .putString(spUserType,user.getUserType())
                    .putInt(spUserAuthority,user.getLeaveAuthority())//请假权限
                    .putString(spUserLocalimg,"");//本地头像
            editor.apply();
            return editor;
        }
        return getSpWithEdit();
    }
    public SharedPreferences.Editor clearUser() {
        SharedPreferences.Editor editor = getSpWithEdit()
                .putString(spUserName, "")//姓名
                .putString(spUserPhone, getUserPhone())//电话
//                    .putString(spUserSex, user.getUserSex())
                .putString(spFaculty,"")//院系id
                .putString(spFacultyName,"")//院系名称
                .putString(spClass,"")//班级
                .putString(spUserUrl,"")//用户头像
                .putInt(spUserType,0)//用户类型
                .putString(spToken,"")//Token
//                    .putString(spUserType,user.getUserType())
                .putInt(spUserAuthority,-1)//请假权限
                .putString(spUserLocalimg,"");//本地头像路径
        editor.apply();
        return getSpWithEdit();
    }
    public void disableFirstOpen() {
        getSpWithEdit().putBoolean(isFirstOpen, false).apply();
    }
    public SharedPreferences.Editor setUserName(String userName) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserName, userName);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserPhone(String userPhone) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserPhone, userPhone);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserSex(String userSex) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserSex, userSex);
        return editor;
    }
    public SharedPreferences.Editor setUserType(Number userType) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserType, userType.intValue());
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setToken(String sptoken) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spToken, sptoken);
        editor.apply();
        return editor;
    }

    public SharedPreferences.Editor setUserLocal(String userurl) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserLocalimg, userurl);
        editor.apply();
        return editor;
    }
    public SharedPreferences.Editor setUserImageUrl(String userurl) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserUrl, userurl);
        editor.apply();
        return editor;
    }
    public SharedPreferences.Editor setUserId(Number userId) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserId, userId.intValue());
        editor.apply();
        return editor;
    }

}
