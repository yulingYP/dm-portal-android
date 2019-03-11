package com.definesys.dmportal.main.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.definesys.dmportal.appstore.bean.User;
import com.definesys.dmportal.main.interfaces.SharedPreferencesParams;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class SharedPreferencesUtil implements SharedPreferencesParams {
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
        user.setFacultyId(getFaculty());
        user.setFacultyName(getFacultyName());
        user.setClassId(getClassId());
        user.setUserSex((short)getUserSex());
        user.setLeaveAuthority(getApprpvalStudentAuthority());
        user.setLeaveTeacherAuthority(getApprpvalTeacherAuthority());
        user.setUserImage(getUserImageUrl());
        user.setUserSign(getUserSign());
        user.setPhone(getUserPhone());
        user.setBranchId(getUserBranchId());
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

    public int getUserSex() {
        return sp.getInt(spUserSex, -1);
    }

    public List<String> getHistortyData(int type){
        List<String> datalist;
        String Json = sp.getString(spSearchHistory+type+getUserId(), null);
        if (null == Json) {
            return null;
        }

        Gson gson = new Gson();
        datalist = gson.fromJson(Json, new TypeToken<List<String>>() {

        }.getType());

        return datalist;
    }

    public String getToken() {   return sp.getString(spToken,"");}

    public String getFaculty() {   return sp.getString(spFaculty,"");}

    public String getFacultyName() {   return sp.getString(spFacultyName,"");}

    public String getClassId() {   return sp.getString(spClass,"");}

    public String getUserImageUrl() {   return sp.getString(spUserHead,"");}

    public String getUserSign() {   return sp.getString(spUserSign,"");}

    public String getUserLocal() {   return sp.getString(spUserLocalimg,"");}

    public String getUserPhone() {   return sp.getString(spUserPhone,"");}

    public String getUserBranchId() {   return sp.getString(spBranchId,"");}

    public int getUserType() {   return sp.getInt(spUserType,0);}
    public int getApprpvalTeacherAuthority() {   return sp.getInt(spApprovalTeaAut,-1);}
    public int getApprpvalStudentAuthority() {   return sp.getInt(spApprovalStuAut,-1);}
    public int getUserSetting() {   return sp.getInt(spUserSetting+getUserId(),1);}
    //-----------------------SET-------------------
    public void setUser(User user) {
        if(user!=null) {
            SharedPreferences.Editor editor = getSpWithEdit()
                    .putString(spUserName, user.getName())//姓名
                    .putString(spUserPhone, user.getPhone())//电话
                    .putInt(spUserSex, user.getUserSex())
                    .putString(spFaculty,user.getFacultyId())//院系id
                    .putString(spFacultyName,user.getFacultyName())//院系名称
                    .putString(spClass,user.getClassId())//班级
                    .putString(spUserHead,user.getUserImage())//用户头像
                    .putString(spUserSign,user.getUserSign())//用户头像
                    .putString(spBranchId,user.getBranchId())//请假部门id
                    .putInt(spApprovalStuAut,user.getLeaveAuthority())//学生请假方面的权限
                    .putInt(spApprovalTeaAut,user.getLeaveTeacherAuthority())//教师请假方面的权限
                    .putString(spUserLocalimg,"");//本地头像
            editor.apply();
            return;
        }
        getSpWithEdit();
    }
    public void clearUser() {
        SharedPreferences.Editor editor = getSpWithEdit()
                .putString(spUserName, "")//姓名
                .putString(spUserPhone, getUserPhone())//电话
                .putInt(spUserSex, -1)
                .putString(spFaculty,"")//院系id
                .putString(spFacultyName,"")//院系名称
                .putString(spClass,"")//班级
                .putString(spUserHead,"")//用户头像
                .putString(spUserSign,"")//用户签名
                .putInt(spUserType,0)//用户类型
                .putString(spToken,"")//Token
                .putString(spBranchId,"")//教师请假部门id
                .putInt(spApprovalStuAut,-1)//请假权限
                .putInt(spApprovalTeaAut,-1)//请假权限
                .putString(spUserLocalimg,"");//本地头像路径
        editor.apply();
        getSpWithEdit();
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
    public SharedPreferences.Editor setBranchId(String branchId) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spBranchId, branchId);
        editor.apply();
        return editor;
    }
    public SharedPreferences.Editor setUserSetting(int setCode) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserSetting+getUserId(), setCode);
        editor.apply();
        return editor;
    }
    public SharedPreferences.Editor setUserImageUrl(String userurl) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserHead, userurl);
        editor.apply();
        return editor;
    }
    public SharedPreferences.Editor setUserSign(String userurl) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserSign, userurl);
        editor.apply();
        return editor;
    }
    public SharedPreferences.Editor setUserId(Number userId) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserId, userId.intValue());
        editor.apply();
        return editor;
    }

    /**
     * 保存List和当前歌曲号
     *
     * @param datalist d
     */
    public SharedPreferences.Editor setHistoryData(int type, List<String> datalist) {
        if (!(null == datalist || datalist.size() <= 0)) {
            Gson gson = new Gson();
            String Json = gson.toJson(datalist);
            SharedPreferences.Editor editor = getSpWithEdit().putString(spSearchHistory + type + getUserId(), Json);
            editor.apply();
            return editor;
        }else {
            SharedPreferences.Editor editor = getSpWithEdit().putString(spSearchHistory + type + getUserId(), null);
            editor.apply();
            return editor;
        }
    }

    public SharedPreferences.Editor setHttpUrl(String url) {
        SharedPreferences.Editor editor = getSpWithEdit().putString("url", url);
        editor.apply();
        return editor;
    }
    public String getHttpUrl() {
        return sp.getString("url", HttpConst.url);
    }

}
