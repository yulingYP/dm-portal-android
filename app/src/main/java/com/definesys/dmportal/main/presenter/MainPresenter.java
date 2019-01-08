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
    //获取课表信息成功
    public static final String SUCCESSFUL_GET_TABLE_INFO="SUCCESSFUL_GET_TABLE_INFO";
    //获取消息成功
    public static final String SUCCESSFUL_GET_MESSAGE = "SUCCESSFUL_GET_MESSAGE";
    //获取消息失败
    public static final String ERROR_GET_MESSAGE="ERROR_GET_MESSAGE";
    //获取新闻成功
    public static final String SUCCESSFUL_GET_NEWS = "SUCCESSFUL_GET_NEWS";
    //获取新闻失败
    public static final String ERROR_GET_NEWS="ERROR_GET_NEWS";

    public MainPresenter(Context context) {
        super(context);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag("getVerificationCode");
    }
}
