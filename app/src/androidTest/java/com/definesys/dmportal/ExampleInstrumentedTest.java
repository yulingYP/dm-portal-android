package com.definesys.dmportal;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.util.MD5Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();


//        System.out.println((double)result.get("result")==0);
//        assertEquals("com.definesys.dmportal", appContext.getPackageName());
    }

}
