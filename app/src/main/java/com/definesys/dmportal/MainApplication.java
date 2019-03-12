package com.definesys.dmportal;

import android.app.Application;
import android.util.Log;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.util.SSLSocketClient;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.vise.xsnow.event.Subscribe;
import com.vise.xsnow.http.ViseHttp;
import java.nio.charset.Charset;
import java.util.HashMap;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;



public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());

        SharedPreferencesUtil.setContext(this);
        ViseHttp.init(this);
        ViseHttp.CONFIG()
                //配置请求主机地址
                .baseUrl(SharedPreferencesUtil.getInstance().getHttpUrl())
                //配置全局请求头
                .globalHeaders(new HashMap<>())
                //配置全局请求参数
                .globalParams(new HashMap<>())
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5)
                //配置请求失败重试次数
//                .retryCount(0)
                //配置请求失败重试间隔时间，单位毫秒
//                 .retryDelayMillis(1000)
                //配置HTTPS协议
                .SSLSocketFactory(SSLSocketClient.getSSLSocketFactory())
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())
                //配置tooken
                .interceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("userId", String.valueOf(SharedPreferencesUtil.getInstance().getUserId()))
                            .addHeader("token", SharedPreferencesUtil.getInstance().getToken())
                            .build();
                    Response response = chain.proceed(request);
                    ResponseBody responseBody = response.body();
                    assert responseBody != null;
                    long contentLength = responseBody.contentLength();

                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = Charset.forName("UTF-8");
//                    MediaType contentType = responseBody.contentType();

                    String string = "" ;
                    if (contentLength != 0) {
                        //获取Response的body的字符串 并打印
                        string = buffer.clone().readString(charset);
                    }

                    BaseResponse resultBean = (new Gson()).fromJson(string, BaseResponse.class);
                    Log.d("mydemo",resultBean.toString());
                    if("600".equals(resultBean.getCode())){
                        MyCongfig.showMyDialog(R.string.no_one_tip);
                    }else if("601".equals(resultBean.getCode())) {
                        MyCongfig.showMyDialog(R.string.no_use_tip);
                    }
                    return response;
                });
        if (BuildConfig.DEBUG) { //如果在debug模式下
            // 打印日志,默认关闭
            ARouter.openLog();
            // 开启调试模式，默认关闭(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
            // 打印日志的时候打印线程堆栈
            ARouter.printStackTrace();

        }

        // 尽可能早，推荐在Application中初始化
        ARouter.init(this);

    }


    @Override
    @Subscribe
    public void onLowMemory() {
        super.onLowMemory();

    }


}