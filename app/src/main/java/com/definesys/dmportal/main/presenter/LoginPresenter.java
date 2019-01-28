package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.EditSendText;
import com.definesys.dmportal.main.util.MD5Util;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.request.PostRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPresenter extends BasePresenter {

    public LoginPresenter(Context context) {
        super(context);
    }

    /*
    登录接口
    */
    public void userLogin(Number userId,String phone, String psw, int loginType) {
        Map loginMap = new HashMap<>();
        //登录所需参数
        loginMap.put("userId", userId);
        if (loginType == EditSendText.PASSWORD) {
            loginMap.put("password", MD5Util.string2MD5(psw));
            loginMap.put("loginType", 0);
        } else {
            loginMap.put("password", psw);
            loginMap.put("loginType", 1);
            loginMap.put("phone", phone);
        }
        PostRequest postRequest = ViseHttp.POST(HttpConst.userLogin)
                .setJson(new Gson().toJson(loginMap))
                .tag(HttpConst.userLogin);

        postRequest.request(new ACallback<BaseResponse<HashMap<String,Object>>>() {
            @Override
            public void onSuccess(BaseResponse<HashMap<String,Object>> data) {
                switch (data.getCode()) {
                    case "200":
                        SharedPreferencesUtil.getInstance().setToken(data.getData().get("token").toString());
                        SharedPreferencesUtil.getInstance().setUserType((Number) data.getData().get("userType"));
                        if(loginType==0) {//密码登录
                            SharedPreferencesUtil.getInstance().setUserId(userId);
                            SmecRxBus.get().post(MainPresenter.SUCCESSFUL_LOGIN_USER, data.getMsg());
                        }else if(loginType==1){//短信登录
                            SharedPreferencesUtil.getInstance().setUserPhone(phone);
                            SharedPreferencesUtil.getInstance().setUserId((Number) data.getData().get("userId"));
                            SmecRxBus.get().post(MainPresenter.SUCCESSFUL_LOGIN_USER, data.getMsg());
                        }
                        break;
                    default:
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                        break;
                }
            }
            @Override
            public void onFail(int errCode, String errMsg) {
                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
            }
        });
    }


    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.userLogin);
    }
}
