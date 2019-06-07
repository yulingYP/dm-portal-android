package com.example.dmportal.main.presenter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.vector.update_app.HttpManager;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.mode.DownProgress;
import com.vise.xsnow.http.request.GetRequest;
import com.vise.xsnow.http.request.PostRequest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 羽翎 on 2019/2/26.
 */
public class ViseUpdateHttpManager implements HttpManager {

    @Override
    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {

        GetRequest getRequest = ViseHttp.GET("WVector/AppUpdateDemo/master/json/json.txt").baseUrl("https://raw.githubusercontent.com/")
                .params(params);
        getRequest.request(new ACallback<String>() {
            @Override
            public void onSuccess(String data)
            {
                callBack.onResponse(data);
            }
            @Override
            public void onFail(int errCode, String errMsg) {
                callBack.onError("异常");
            }
        });
    }

    @Override
    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {
        Map<String, String> map = new HashMap<>();
        map.put("version", "2.1.4");
        map.put("platform","Android");
        PostRequest postRequest = ViseHttp.POST("updateVersion")
                .baseUrl("http://wcpublic.smec-cn.com:7777/simp/simp/TEISService/")
                .setJson(new Gson().toJson(map));

        Log.e( "onSuccess: data", url);

        postRequest.request(new ACallback<String>() {
            @Override
            public void onSuccess(String data) {
                callBack.onResponse(data);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                callBack.onError("异常");
            }
        });
    }


    @Override
    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull FileCallback callback) {

        callback.onBefore();
        ViseHttp.DOWNLOAD(url)
                .setFileName(fileName)
                .request(new ACallback<DownProgress>() {
                    @Override
                    public void onSuccess(DownProgress data) {
                        callback.onProgress(data.getDownloadSize() * 1.0f / data.getTotalSize(),data.getTotalSize());
                        if (data.isDownComplete()) {
                            callback.onResponse(new File("/storage/emulated/0/Android/data/com.definesys.DmPortal/cache/download",fileName));
                        }
                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        callback.onError("异常");
                    }
                });

    }


}