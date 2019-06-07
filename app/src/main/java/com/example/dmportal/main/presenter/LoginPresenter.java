package com.example.dmportal.main.presenter;

import android.content.Context;
import com.example.base.BasePresenter;
import com.example.base.BaseResponse;
import com.example.dmportal.appstore.customViews.EditSendText;
import com.example.dmportal.main.util.MD5Util;
import com.example.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import java.util.HashMap;
import java.util.Map;

public class LoginPresenter extends BasePresenter {

    public LoginPresenter(Context context) {
        super(context);
    }

    /*
    登录接口
    */
    public void userLogin(Number userId,String phone, String psw, int loginType) {
        Map<String,Object> loginMap = new HashMap<>();
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
        ViseHttp.POST(HttpConst.userLogin)
                .setJson(new Gson().toJson(loginMap))
                .tag(HttpConst.userLogin)
                .request(new ACallback<BaseResponse<HashMap<String,Object>>>() {
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
