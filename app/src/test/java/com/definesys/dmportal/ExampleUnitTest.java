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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String content = "adasd+asdasd";
        boolean flag = content.contains("+")&&content.indexOf("+")<content.length()-1&&content.indexOf("+")>0;
        System.out.println(flag);
       assertEquals(4, 2 + 2);
    }


}