package com.example.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

import com.example.base.BasePresenter;
import com.example.base.BaseResponse;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by 羽翎 on 2019/1/29.
 */

public class PhoneBindPresent extends BasePresenter {
    public PhoneBindPresent(Context context) {
        super(context);
    }

    public void phoneBind(String phone,String code,String isBind){
        Map<String,String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
        map.put("isBind",isBind);
        Log.d("myMap",new Gson().toJson(map));
        ViseHttp.POST(HttpConst.bindPhone)
                .tag(HttpConst.bindPhone)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        switch (data.getCode()) {
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_BIND_PHONE, "");
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
}
