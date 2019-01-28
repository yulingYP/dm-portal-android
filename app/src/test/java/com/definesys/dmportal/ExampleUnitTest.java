package com.definesys.dmportal;

import android.animation.IntArrayEvaluator;
import android.util.Log;

import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.util.MD5Util;
import com.definesys.dmportal.main.util.SSLSocketClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("MMDDHHmmss");
        String date = sDateFormat.format(new Date());
        Map map = new HashMap();
        map.put("userid","E102BD");
        map.put("pwd", MD5Util.string2MD5("E102BD"+"00000000"+"tJmB40"+date));
        map.put("mobile","17562619116");
        map.put("content","%d1%e9%d6%a4%c2%eb%a3%ba6666%a3%ac%b4%f2%cb%c0%b6%bc%b2%bb%d2%aa%b8%e6%cb%df%b1%f0%c8%cb%c5%b6%a3%a1");
        map.put("timestamp",date);
        map.put("custid","1511104");
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid","E102BD")
                .add("pwd", MD5Util.string2MD5("E102BD"+"00000000"+"tJmB40"+date))
                .add("mobile","17562619116")
                .add("content","%d1%e9%d6%a4%c2%eb%a3%ba6666%a3%ac%b4%f2%cb%c0%b6%bc%b2%bb%d2%aa%b8%e6%cb%df%b1%f0%c8%cb%c5%b6%a3%a1")
                .add("timestamp",date)
                .add("custid","1511104")
                .build();
        Request request = new Request.Builder()
                .url("http://api02.monyun.cn:7901/sms/v2/std/single_send")
                .post(requestBody)
                .build();
        Response response =okHttpClient.newCall(request).execute();
        String responseDate = response.body().string();

        assertEquals(4, 2 + 2);
    }


}