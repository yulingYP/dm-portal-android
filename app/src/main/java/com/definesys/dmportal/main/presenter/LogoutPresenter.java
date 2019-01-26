package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.google.gson.Gson;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

public class LogoutPresenter extends BasePresenter {
    public LogoutPresenter(Context context) {
        super(context);
    }

    public void logout(Number userId) {
        Map map = new HashMap<>();
        map.put("userId", userId);
        ViseHttp.POST(HttpConst.userLogout)
                .setJson(new Gson().toJson(map)).tag(HttpConst.userLogout)
                .request(new ACallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse data) {
                switch (data.getCode()) {
                    case "200":
                        Log.d("myMap", "onSuccess: LogOut success");
                        break;
                    default:
                        Log.d("myMap", "onSuccess: LogOut failed");
                        break;
                }
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                Log.d("myMap", "onError: LogOut failed");
            }
        });
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.userLogout);
    }
}
