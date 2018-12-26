package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.google.gson.Gson;
import com.vise.xsnow.http.ViseHttp;

/**
 * Created by mobile on 2018/8/20.
 */

public class MainPresenter extends BasePresenter {

    //登录页发送验证码成功
    public static final String SUCCESSFUL_SEND_CODE_LOGIN = "SUCCESSFUL_SEND_CODE_LOGIN";
    //注册页发送验证码成功
    public static final String SUCCESSFUL_SEND_CODE_REGISTER = "SUCCESSFUL_SEND_CODE_REGISTER";
    //忘记密码页发送验证码成功
    public static final String SUCCESSFUL_SEND_CODE_FORGET_PSW = "SUCCESSFUL_SEND_CODE_FORGET_PSW";
    //注册成功
    public static final String SUCCESSFUL_REGISTER_USER = "SUCCESSFUL_REGISTER_USER";
    //原密码修改密码成功
    public static final String SUCCESSFUL_CHANGE_PASSWORD_BY_PSW = "SUCCESSFUL_CHANGE_PASSWORD_BY_PSW";
    //验证码修改密码成功
    public static final String SUCCESSFUL_CHANGE_PASSWORD_BY_CODE = "SUCCESSFUL_CHANGE_PASSWORD_BY_CODE";
    //登录页获取个人信息成功
    public static final String SUCCESSFUL_GET_USER_INFORMATION_FOR_LOGIN = "SUCCESSFUL_GET_USER_INFORMATION_FOR_LOGIN";
    //注册页获取个人信息成功
    public static final String SUCCESSFUL_GET_USER_INFORMATION_BY_REGISTER = "SUCCESSFUL_GET_USER_INFORMATION_BY_REGISTER";
    //获取成员列表信息成功
    public static final String SUCCESSFUL_GET_GROUPS_INFORMATION= "SUCCESSFUL_GET_GROUPS_INFORMATION";
    //获取成员个人信息成功
    public static final String SUCCESSFUL_GET_GROUP_DETAIL_INFORMATION= "SUCCESSFUL_GET_GROUP_DETAIL_INFORMATION";
    //修改个人信息成功
    public static final String SUCCESSFUL_CHANGE_USER_INFORMATION = "SUCCESSFUL_CHANGE_USER_INFORMATION";
    //根据短信获取姓名成功
    public static final String SUCCESSFUL_GET_USERNAME = "SUCCESSFUL_GET_USERNAME";
    //获取消息成功
    public static final String SUCCESSFUL_GET_MESSAGE = "SUCCESSFUL_GET_MESSAGE";
    //获取新闻成功
    public static final String SUCCESSFUL_GET_NEWS = "SUCCESSFUL_GET_NEWS";
    //上传头像成功
    public static final String SUCCESSFUL_UPLOAD_USER_IMAGE = "SUCCESSFUL_UPLOAD_USER_IMAGE";
    //上传反馈成功
    public static final String SUCCESSFUL_UPLOAD_FEEDBACK = "SUCCESSFUL_UPLOAD_FEEDBACK";
    //获取版本更新成功
    public static final String SUCCESSFUL_DETECTED_NEW_VERSION = "SUCCESSFUL_DETECTED_NEW_VERSION";
    //获取用户所有楼层权限成功
    public static final String SUCCESSFUL_GET_MY_AUTH = "SUCCESSFUL_GET_MY_AUTH";
    //用户登陆成功
    public static final String SUCCESSFUL_LOGIN_USER = "SUCCESSFUL_LOGIN_USER";



    //登录页发送验证码失败
    public static final String ERROR_SEND_CODE_LOGIN = "ERROR_SEND_CODE_LOGIN";
    //注册页发送验证码失败
    public static final String ERROR_SEND_CODE_REGISTER = "ERROR_SEND_CODE_REGISTER";
    //忘记密码页发送验证码失败
    public static final String ERROR_SEND_CODE_FORGET_PSW = "ERROR_SEND_CODE_FORGET_PSW";
    //原密码修改密码失败
    public static final String ERROR_CHANGE_PASSWORD_BY_PSW  = "ERROR_CHANGE_PASSWORD_BY_PSW";
    //验证码修改密码失败
    public static final String ERROR_CHANGE_PASSWORD_BY_CODE  = "ERROR_CHANGE_PASSWORD_BY_CODE";
    //注册失败
    public static final String ERROR_REGISTER_USER = "ERROR_REGISTER_USER";
    //用户登录失败
    public static final String ERROR_LOGIN_USER = "ERROR_LOGIN_USER";

    //修改个人信息失败
    public static final String ERROR_CHANGE_USER_INFORMATION = "ERROR_CHANGE_USER_INFORMATION";
    //获取成员列表信息失败
    public static final String ERROR_GET_GROUPS_INFORMATION= "ERROR_GET_GROUPS_INFORMATION";
    //获取成员个人信息失败
    public static final String ERROR_GET_GROUP_DETAIL_INFORMATION= "ERROR_GET_GROUP_DETAIL_INFORMATION";
    //根据短信获取姓名失败
    public static final String ERROR_GET_USERNAME = "ERROR_GET_USERNAME";
    //获取消息失败
    public static final String ERROR_GET_MESSAGE = "ERROR_GET_MESSAGE";
    //获取新闻失败
    public static final String ERROR_GET_NEWS = "ERROR_GET_NEWS";
    //上传头像失败
    public static final String ERROR_UPLOAD_USER_IMAGE = "ERROR_UPLOAD_USER_IMAGE";
    //上传反馈失败
    public static final String ERROR_UPLOAD_FEEDBACK = "ERROR_UPLOAD_FEEDBACK";
    //获取版本更新失败
    public static final String ERROR_DETECTED_NEW_VERSION = "ERROR_DETECTED_NEW_VERSION";
    //获取用户所有楼层权限失败
    public static final String ERROR_GET_MY_AUTH = "ERROR_GET_MY_AUTH";

    //获取卡片信息成功
    public static final String SUCCESSFUL_GET_CARD_INFO = "SUCCESSFUL_GET_CARD_INFO";
    //获取卡片信息失败
    public static final String ERROR_GET_CARD_INFO = "ERROR_GET_CARD_INFO";


    //提交成员申请成功
    public static final String SUCCESSFUL_GET_ADDMember = "SUCCESSFUL_GET_ADDMember";
    //提交成员申请失败
    public static final String ERROR_GET_GET_ADDMember = "ERROR_GET_GET_ADDMember";


    //提交成员申请成功
    public static final String SUCCESSFUL_GET_DelMemberAuth = "SUCCESSFUL_GET_DelMemberAuth";
    //提交成员申请失败
    public static final String ERROR_GET_GET_DelMemberAuth = "ERROR_GET_GET_DelMemberAuth";


    //退出MainActivity
    public static final String Qiut_MainActivity = "Qiut_MainActivity";

    private Gson gson = new Gson();

    public MainPresenter(Context context) {
        super(context);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag("getVerificationCode");
    }
}
