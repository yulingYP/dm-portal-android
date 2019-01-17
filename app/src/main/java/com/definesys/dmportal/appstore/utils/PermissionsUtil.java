package com.definesys.dmportal.appstore.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



/**
 * Created by 羽翎 on 2018/11/5.
 */

public class PermissionsUtil {
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
        //mNetworkInfo.isAvailable();
            return true;//有网
            }
        }
        return false;//没有网
    }
}
