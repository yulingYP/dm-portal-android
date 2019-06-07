package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

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

    /**
     * 修改密码
     * @param userId 用户id
     * @param phoneNumber 电话
     * @param oldPwd 旧密码
     * @param newPwd 新密码
     * @param code 短信验证码
     * @param changeType 1.短信修改 2.旧密码修改
     */
    public void changePwd(Number userId,String phoneNumber, String oldPwd, String newPwd, String code,int changeType) {
        Map<String, Object> changePswMap = new HashMap<>();
        if(changeType==1){
            changePswMap.put("phone", phoneNumber);
            changePswMap.put("code", code);
            changePswMap.put("newPassword", MD5Util.string2MD5(newPwd));
            changePswMap.put("changeType", changeType);
        }else if(changeType==2){
            changePswMap.put("userId", userId);
            changePswMap.put("oldPassword", MD5Util.string2MD5(oldPwd));
            changePswMap.put("newPassword", MD5Util.string2MD5(newPwd));
            changePswMap.put("changeType", changeType);
        }
        Log.d("myMap",new Gson().toJson(changePswMap));
        PostRequest postRequest = ViseHttp.POST(HttpConst.changePassword)
                .setJson(new Gson().toJson(changePswMap)).tag(HttpConst.changePassword);
        postRequest.request(new ACallback<BaseResponse<String>>() {
            @Override
            public void onSuccess(BaseResponse<String> data) {
                switch (data.getCode()) {
                    case "200":
                        SmecRxBus.get().post(MainPresenter.SUCCESSFUL_CHANGE_PASSWORD, "");
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
        ViseHttp.cancelTag(HttpConst.changePassword);
    }
}
