package com.definesys.dmportal.main.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.User;
import com.definesys.dmportal.main.interfaces.SharedPreferencesParams;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vise.xsnow.http.ViseHttp;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

@SuppressLint("StaticFieldLeak")
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
    private SharedPreferences.Editor getSpWithEdit() {
        return sp.edit();
    }

    //-----------------------GET-------------------

    public User getUserInfo(){
        User user = new User();
        user.setName(getUserName());//姓名
        user.setUserId(getUserId().intValue());//用户id
        user.setUserType((short)getUserType());//用户类型
        user.setUserSex((short)getUserSex());//用户性别
        user.setPhone(getUserPhone());//用户手机
        user.setLeaveTeacherAuthority(getApprpvalTeacherAuthority());//审批教师请假
        user.setLeaveAuthority(getApprpvalStudentAuthority());//审批学生请假
        user.setUserImage(getUserImageUrl());//头像
        user.setUserSign(getUserSign());//签名
        user.setBranchId(getUserBranchId());//部门id
        user.setBranchName(getUserBranchName());//部门名称
        user.setFacultyId(getFaculty());//院系id
        user.setFacultyName(getFacultyName());//院系名称
        user.setClassId(getClassId());//班级id
        user.setClassName(getClassName());//班级名称
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

    public long getLastLeaveTime(){return sp.getLong(getUserId().intValue()+spLeaveTime,System.currentTimeMillis());}

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

    public String getClassId() {   return sp.getString(spClassId,"");}

    public String getClassName() {   return sp.getString(spClassName,"");}

    public String getUserImageUrl() {   return sp.getString(spUserHead,"");}

    public String getUserSign() {   return sp.getString(spUserSign,"");}

    public String getUserLocal() {   return sp.getString(spUserLocalimg,"");}

    public String getUserPhone() {   return sp.getString(spUserPhone,"");}

    public String getUserBranchId() {   return sp.getString(spBranchId,"");}

    public String getUserBranchName() {   return sp.getString(spBranchName,"");}

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
                    .putInt(spUserSex, user.getUserSex())//性别
                    .putString(spFaculty,user.getFacultyId())//院系id
                    .putString(spFacultyName,user.getFacultyName())//院系名称
                    .putString(spClassId,user.getClassId())//班级id
                    .putString(spClassName,user.getClassName())//班级名称
                    .putString(spUserHead,user.getUserImage())//用户头像
                    .putString(spUserSign,user.getUserSign())//用户签名
                    .putString(spBranchId,user.getBranchId())//请假部门id
                    .putString(spBranchName,user.getBranchName())//请假部门名称
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
                .putString(spClassId,"")//班级id
                .putString(spClassName,"")//班级名称
                .putString(spUserHead,"")//用户头像
                .putString(spUserSign,"")//用户签名
                .putInt(spUserType,-1)//用户类型
                .putString(spToken,"")//Token
                .putString(spBranchId,"")//教师请假部门id
                .putString(spBranchName,"")//教师请假部门名称
                .putInt(spApprovalStuAut,-1)//请假权限
                .putInt(spApprovalTeaAut,-1)//请假权限
                .putString(spUserLocalimg,"");//本地头像路径
        editor.apply();
        getSpWithEdit();
    }
    public void disableFirstOpen() {
        getSpWithEdit().putBoolean(isFirstOpen, false).apply();
    }
//    public SharedPreferences.Editor setUserName(String userName) {
//        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserName, userName);
//        editor.apply();
//        return editor;
//    }

    public void setUserPhone(String userPhone) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserPhone, userPhone);
        editor.apply();
    }

    public void setApprovalStuAut(Integer authority){
        getSpWithEdit().putInt(spApprovalStuAut,authority).apply();
    }
    public void setApprovalTeaAut(Integer authority){
        getSpWithEdit().putInt(spApprovalTeaAut,authority).apply();
    }

    public SharedPreferences.Editor setUserSex(String userSex) {
        return getSpWithEdit().putString(spUserSex, userSex);
    }
    public void setUserType(Number userType) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserType, userType.intValue());
        editor.apply();
    }

    public void setToken(String sptoken) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spToken, sptoken);
        editor.apply();
    }

    public void setUserLocal(String userurl) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spUserLocalimg, userurl);
        editor.apply();
    }
    public void setBranchId(String branchId) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spBranchId, branchId);
        editor.apply();
    }

    public void setBranchName(String branchName) {
        SharedPreferences.Editor editor = getSpWithEdit().putString(spBranchName, branchName);
        editor.apply();
    }
    public void setUserSetting(int setCode) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserSetting+getUserId(), setCode);
        editor.apply();
    }

    public void setUserId(Number userId) {
        SharedPreferences.Editor editor = getSpWithEdit().putInt(spUserId, userId.intValue());
        editor.apply();
    }
    public void setLastLeaveTime(long leaveTime) {
        SharedPreferences.Editor editor = getSpWithEdit().putLong(getUserId().intValue()+spLeaveTime, leaveTime);
        editor.apply();
    }
    /**
     * 保存List和当前歌曲号
     *
     * @param datalist d
     */
    public void setHistoryData(int type, List<String> datalist) {
        if (!(null == datalist || datalist.size() <= 0)) {
            Gson gson = new Gson();
            String Json = gson.toJson(datalist);
            SharedPreferences.Editor editor = getSpWithEdit().putString(spSearchHistory + type + getUserId(), Json);
            editor.apply();
        }else {
            SharedPreferences.Editor editor = getSpWithEdit().putString(spSearchHistory + type + getUserId(), null);
            editor.apply();
        }
    }

    private void setHttpUrl(String url) {
        SharedPreferences.Editor editor = getSpWithEdit().putString("url", url);
        editor.apply();
    }

    public String getHttpUrl() {
        return sp.getString("url", HttpConst.url);
    }
    //设置url
    public void setUrl(String newUrl) {
        String url =context.getString(R.string.httpUrl,newUrl);
        setHttpUrl(url);
        ViseHttp.CONFIG().baseUrl(url);
    }

}
