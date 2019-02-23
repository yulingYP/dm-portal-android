package com.definesys.dmportal;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.util.SSLSocketClient;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.qihoo360.replugin.RePlugin;
import com.vise.xsnow.event.Subscribe;
import com.vise.xsnow.http.ViseHttp;

import java.nio.charset.Charset;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by mobile on 2018/8/2.
 */
public class MainApplication extends Application {

//    private DaoMaster.DevOpenHelper mHelper;
//    private SQLiteDatabase db;
//    private DaoMaster mDaoMaster;
//    private DaoSession mDaoSession;
    public static float scale;

    private AlertDialog alert;//单机登陆提示框
    private boolean hasNewMessage;
    public static MainApplication instances;


    public boolean isHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        RePlugin.App.attachBaseContext(this);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        scale = getResources().getDisplayMetrics().density;

        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());

        RePlugin.App.onCreate();

        ViseHttp.init(this);
        ViseHttp.CONFIG()
                //配置请求主机地址
                .baseUrl(HttpConst.url)
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
                    long contentLength = responseBody.contentLength();

                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();

                    Charset charset = Charset.forName("UTF-8");
                    MediaType contentType = responseBody.contentType();

                    String string = "" ;
                    if (contentLength != 0) {
                        //获取Response的body的字符串 并打印
                        string = buffer.clone().readString(charset);
                    }

                    BaseResponse resultBean = (new Gson()).fromJson(string, BaseResponse.class);
                    Log.d("mydemo",resultBean.toString());
                    if("600".equals(resultBean.getCode())){
                        showDialog(R.string.no_one_tip);
                    }else if("601".equals(resultBean.getCode())) {
                        showDialog(R.string.no_use_tip);
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

        instances = this;
        SharedPreferencesUtil.setContext(this);
        //消除相机url异常
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

    }

    public synchronized void showDialog(int msgId) {
        if(alert!=null&&alert.isShowing())
            return;
//        SharedPreferencesUtil.getInstance().setToken("");

        //信鸽解绑
//        XGPushManager.unregisterPush(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivityManager.getInstance().getCurrentActivity());
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ARouter.getInstance().build(ARouterConstants.MainActivity).withBoolean(getString(R.string.exit_en), true).navigation(MyActivityManager.getInstance().getCurrentActivity());
                        dialog.cancel();
                    }
                });
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                alert = builder.create();
                alert.setCancelable(false);
                alert.show();
            }
        }.execute("");
    }

    public void disMissDialog(){
        if(alert!=null&&alert.isShowing())
            alert.dismiss();
    }

    public static MainApplication getInstances() {
        return instances;
    }
    /**
     * 设置greenDao
     */
//    private void setDatabase() {
//        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
//        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
//        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
//        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
//        // 此处sport-db表示数据库名称 可以任意填写
//        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "face-db", null);
//        db = mHelper.getWritableDatabase();
//        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
//        mDaoMaster = new DaoMaster(db);
//        mDaoSession = mDaoMaster.newSession();
//    }
//    public DaoSession getDaoSession() {
//        return mDaoSession;
//    }
//    public SQLiteDatabase getDb() {
//        return db;
//    }

    @Override
    @Subscribe
    public void onLowMemory() {
        super.onLowMemory();

        /* Not need to be called if your application's minSdkVersion > = 14 */
        RePlugin.App.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

        /* Not need to be called if your application's minSdkVersion > = 14 */
        RePlugin.App.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        /* Not need to be called if your application's minSdkVersion > = 14 */
        RePlugin.App.onConfigurationChanged(config);
    }
}