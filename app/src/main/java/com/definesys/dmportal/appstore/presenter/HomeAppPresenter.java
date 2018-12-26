package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.Collections;
import java.util.Map;


/**
 * Created by mobile on 2018/8/20.
 */

public class HomeAppPresenter extends BasePresenter {

    //此处传Activity的Context
    public HomeAppPresenter(Context context) {
        super(context);
    }

    public void getHomeAppData() {
        ViseHttp.GET("EimPPWI6b5613cd312c26066de5eca43ad5c311af54fe02?uri=/home/apps")
                .tag("getHomeAppData")
                .request(new ACallback<Map>() {

                    @Override
                    public void onSuccess(Map map) {
                        Log.e("TAG", map.toString());
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.e("TAG", errCode + "" + errMsg);

                    }
                });
    }

}
