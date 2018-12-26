package com.definesys.dmportal.webview.jsinterface;

import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.Gson;
import com.prim.primweb.core.jsloader.AgentValueCallback;
import com.prim.primweb.core.jsloader.ICallJsLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mobile on 2018/8/20.
 */

public class JsInterface {

    ICallJsLoader callJsLoader ;

    Gson gson = new Gson();

    public JsInterface(ICallJsLoader callJsLoader) {
        this.callJsLoader = callJsLoader ;
    }

    @JavascriptInterface
    public void DmBridgeJS2OC_Config(String data) {
        HashMap hashMap = gson.fromJson(data, HashMap.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            callJsLoader.callJs(hashMap.get("callbackUuId").toString(), value -> {

            },"{\"status\":\"ok\")");
        } else {
            callJsLoader.callJS(hashMap.get("callbackUuId").toString(),"{\"status\":\"ok\")");
        }

    }
}
