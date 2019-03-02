package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.google.gson.Gson;
import com.vise.xsnow.http.ViseHttp;

/**
 * Created by mobile on 2018/8/20.
 */

public class MainPresenter extends BasePresenter {

    //网络请求失败
    public static final String ERROR_NETWORK="ERROR_NETWORK";

    //获取用户姓名失败
    public static final String ERROR_NETWORK_NAME="ERROR_NETWORK_NAME";

    //登陆成功
    public static final String SUCCESSFUL_LOGIN_USER = "SUCCESSFUL_LOGIN_USER" ;

    //短信修改密码成功
    public static final String SUCCESSFUL_CHANGE_PASSWORD = "SUCCESSFUL_CHANGE_PASSWORD";

    //绑定或解绑手机成功
    public static final String SUCCESSFUL_BIND_PHONE = "SUCCESSFUL_BIND_PHONE";

    //获取用户信息成功
    public static final String SUCCESSFUL_GET_USER_INFO="SUCCESSFUL_GET_USER_INFO";

    //获取用户姓名成功
    public static final String SUCCESSFUL_GET_USER_NAME="SUCCESSFUL_GET_USER_NAME";

    //修改头像成功
    public static final String SUCCESSFUL_UPLOAD_USER_IMAGE="SUCCESSFUL_UPLOAD_USER_IMAGE";

    //获取课表信息成功
    public static final String SUCCESSFUL_GET_TABLE_INFO="SUCCESSFUL_GET_TABLE_INFO";

    //获取课程成绩成功
    public static final String SUCCESSFUL_GET_SCORE_INFO="SUCCESSFUL_GET_SCORE_INFO";

    //获取提交请假请求
    public static final String SUCCESSFUL_GET_LEAVE_REQUEST="SUCCESSFUL_GET_LEAVE_REQUEST";

    //获取请假记录成功
    public static final String SUCCESSFUL_GET_LEAVE_INFO_LIST="SUCCESSFUL_GET_LEAVE_INFO_LIST";

    //根据leaveId获取请假信息
    public static final String SUCCESSFUL_GET_LEAVE_INFO="SUCCESSFUL_GET_LEAVE_INFO";

    //获取历史审批记录成功
    public static final String SUCCESSFUL_GET_APPROVAL_HISTORY_LIST="SUCCESSFUL_GET_APPROVAL_HISTORY_LIST";

    //通过ID获取审批记录成功
    public static final String SUCCESSFUL_GET_APPRVAL_RECORD="SUCCESSFUL_GET_APPRVAL_RECORD";

    //通过时间获取审批记录成功
    public static final String SUCCESSFUL_GET_APPRVAL_RECORD_BY_DATE="SUCCESSFUL_GET_APPRVAL_RECORD_BY_DATE";

    //根据请假Id更新审批状态，并插入审批记录
    public static final String SUCCESSFUL_UPDATE_APPRVAL_RECORD="SUCCESSFUL_UPDATE_APPRVAL_RECORD";

    //获取当前请假状态成功
    public static final String SUCCESSFUL_GET_CURRENT_STATUS="SUCCESSFUL_GET_CURRENT_STATUS";

    //获取最近一次请假信息成功
    public static final String SUCCESSFUL_GET_CURRENT_LEAVE_INFO="SUCCESSFUL_GET_CURRENT_LEAVE_INFO";

    //获取请假记录成功
    public static final String SUCCESSFUL_GET_DEAL_APPROVAL_LIST="SUCCESSFUL_GET_DEAL_APPROVAL_LIST";

    //获取消息成功
    public static final String SUCCESSFUL_GET_MESSAGE = "SUCCESSFUL_GET_MESSAGE";

    //获取消息失败
    public static final String ERROR_GET_MESSAGE="ERROR_GET_MESSAGE";

    //获取新闻成功
    public static final String SUCCESSFUL_GET_NEWS = "SUCCESSFUL_GET_NEWS";

    //获取新闻失败
    public static final String ERROR_GET_NEWS="ERROR_GET_NEWS";

    //发送验证码成功
    public static final String SUCCESSFUL_SEND_CODE= "SUCCESSFUL_SEND_CODE";

    //发送验证码注册账号
    public static final String SUCCESSFUL_SEND_CODE_REGISTER = " String SUCCESSFUL_SEND_CODE_REGISTER";

    //发送验证码修改密码(忘记密码)
    public static final String SUCCESSFUL_SEND_CODE_FORGET_PSW = "SUCCESSFUL_SEND_CODE_FORGET_PSW";

    //获取用户权限的详细信息
    public static final String SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO = "SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO";

    //获取用户推送失败的消息列表
    public static final String SUCCESSFUL_GET_PUSH_ERROR_MSG="SUCCESSFUL_GET_PUSH_ERROR_MSG";

    public MainPresenter(Context context) {
        super(context);
    }


    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag("getVerificationCode");
    }

}
