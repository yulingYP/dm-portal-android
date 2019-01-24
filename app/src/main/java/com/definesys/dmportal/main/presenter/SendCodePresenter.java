package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.request.PostRequest;

import java.util.HashMap;
import java.util.Map;

public class SendCodePresenter extends BasePresenter {

    public SendCodePresenter(Context context) {
        super(context);
    }

    /*
        发送验证码登录账号
     */
    public void sendVerifyCodeForLogin(String phoneNumber) {
        Map<String,String> map = new HashMap<>();
        map.put("userCode", phoneNumber);
        map.put("smsType", "Login");
        PostRequest postRequest = ViseHttp.POST(HttpConst.getVerificationCode)
                .setJson(new Gson().toJson(map))
                .tag(HttpConst.getVerificationCode);
        postRequest.request(new ACallback<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse data) {
                switch (data.getCode()) {
                    case "200":
                            SmecRxBus.get().post(MainPresenter.SUCCESSFUL_SEND_CODE_LOGIN, data.getMsg());
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

    /*
        发送验证码注册账号
     */
    public void sendVerifyCodeForRegister(String phoneNumber) {
        Map<String,String> map = new HashMap<>();
        map.put("userCode", phoneNumber);
        map.put("smsType", "Register");
        PostRequest postRequest = ViseHttp.POST(HttpConst.getVerificationCode)
                .setJson(new Gson().toJson(map))
                .tag(HttpConst.getVerificationCode);
        postRequest.request(new ACallback<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse data) {
                switch (data.getCode()) {
                    case "200":
                            SmecRxBus.get().post(MainPresenter.SUCCESSFUL_SEND_CODE_REGISTER, data.getMsg());
                        break;
                    default:
                            SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                        break;
                }
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,"");
            }
        });
        ViseHttp.BASE(postRequest);
    }
    /*
     发送验证码修改密码(忘记密码)
    */
    public void sendVerifyCodeForChangePsw(String phoneNumber) {
        Map<String,String> map = new HashMap<>();
        map.put("userCode", phoneNumber);
        map.put("smsType", "Passwd");
        PostRequest postRequest = ViseHttp.POST(HttpConst.getVerificationCode)
                .setJson(new Gson().toJson(map))
                .tag(HttpConst.getVerificationCode);
        postRequest.request(new ACallback<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse data) {
                switch (data.getCode()) {
                    case "200":
                        SmecRxBus.get().post(MainPresenter.SUCCESSFUL_SEND_CODE_FORGET_PSW, data.getMsg());
                        break;

                    default:
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                        break;
                }
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,"");
            }
        });
        ViseHttp.BASE(postRequest);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getVerificationCode);
    }
}
