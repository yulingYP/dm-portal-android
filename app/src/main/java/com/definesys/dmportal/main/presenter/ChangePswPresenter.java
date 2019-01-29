package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.main.util.MD5Util;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.request.PostRequest;

import java.util.HashMap;
import java.util.Map;

public class ChangePswPresenter extends BasePresenter {
    public ChangePswPresenter(Context context) {
        super(context);
    }

    public void changePswByPsw(String phoneNumber, String oldPsw, String newPsw, String verifyCode) {
        Map<String, String> changePswMap = new HashMap<>();
        changePswMap.put("userCode", phoneNumber);
        changePswMap.put("verificationCode", verifyCode);
        changePswMap.put("oldPassword", MD5Util.string2MD5(oldPsw));
        changePswMap.put("newPassword", MD5Util.string2MD5(newPsw));
        changePswMap.put("changeType", "mpl");

//        PostRequest postRequest = ViseHttp.POST(HttpConst.changePassword)
//                .setJson(new Gson().toJson(changePswMap)).tag(HttpConst.changePassword);
//        postRequest.request(new ACallback<BaseResponse>() {
//            @Override
//            public void onSuccess(BaseResponse data) {
//                switch (data.getCode()) {
//                    case "200":
//                        SmecRxBus.get().post(MainPresenter.SUCCESSFUL_CHANGE_PASSWORD_BY_PSW, data.getMsg());
//                        break;
//                    default:
//                        SmecRxBus.get().post(MainPresenter.ERROR_CHANGE_PASSWORD_BY_PSW, data.getMsg());
//                        break;
//                }
//            }
//
//            @Override
//            public void onFail(int errCode, String errMsg) {
//                SmecRxBus.get().post(MainPresenter.ERROR_CHANGE_PASSWORD_BY_PSW, mContext.getString(R.string.msg_err_network));
//            }
//        });
    }

    public void changePswByCode(String phoneNumber, String oldPsw, String newPsw, String verifyCode) {
        Map<String, String> changePswMap = new HashMap<>();
        changePswMap.put("userCode", phoneNumber);
        changePswMap.put("verificationCode", verifyCode);
        changePswMap.put("oldPassword", MD5Util.string2MD5(oldPsw));
        changePswMap.put("newPassword", MD5Util.string2MD5(newPsw));
        changePswMap.put("changeType", "mcl");

//        PostRequest postRequest = ViseHttp.POST(HttpConst.changePassword)
//                .setJson(new Gson().toJson(changePswMap)).tag(HttpConst.changePassword);
//        postRequest.request(new ACallback<ResultBean>() {
//            @Override
//            public void onSuccess(ResultBean data) {
//                switch (data.getCode()) {
//                    case "200":
//                        SmecRxBus.get().post(MainPresenter.SUCCESSFUL_CHANGE_PASSWORD_BY_CODE, data.getMsg());
//                        break;
//                    default:
//                        SmecRxBus.get().post(MainPresenter.ERROR_CHANGE_PASSWORD_BY_CODE, data.getMsg());
//                        break;
//                }
//            }
//
//            @Override
//            public void onFail(int errCode, String errMsg) {
//                SmecRxBus.get().post(MainPresenter.ERROR_CHANGE_PASSWORD_BY_CODE, mContext.getString(R.string.msg_err_network));
//            }
//        });
//        ViseHttp.BASE(postRequest);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
//        ViseHttp.cancelTag(HttpConst.changePassword);
    }
}
