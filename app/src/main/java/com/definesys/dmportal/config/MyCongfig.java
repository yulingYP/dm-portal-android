package com.definesys.dmportal.config;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.util.Log;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.main.LoginActivity;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 *
 * Created by 羽翎 on 2018/9/30.
 */

public  class MyCongfig {
    public static boolean isShowing = false;//单机提示框是否已经显示
    public static int tryCount = 0;//开机获取信息失败时尝试次数 3次
    public static int remindMode = 1;// 0.静音 1.震动 2.铃声  3.震动+铃声
    public static void musicOpen(Context context, boolean isShow) {
        if(remindMode>=2||isShow) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (notification == null) return;
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
            Log.d("mydemo", "音乐");
        }
    }

    public static void vibratorOpen(Context context) {
        Log.d("mydemo", "震动");
        Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(500);
    }

    public static void checkMode(Context context){
        musicOpen(context,false);
        if(remindMode%2==1) {
            vibratorOpen(context);
        }
    }

    //单机登录提示框
    @SuppressLint("StaticFieldLeak")
    public static void showMyDialog(int msgId) {
        if(isShowing|| MyActivityManager.getInstance().getCurrentActivity() instanceof LoginActivity)
            return;
        isShowing= true;
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivityManager.getInstance().getCurrentActivity());
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, (dialog, id) -> {
                    ARouter.getInstance().build(ARouterConstants.MainActivity).withBoolean("exit", true).navigation(MyActivityManager.getInstance().getCurrentActivity());
                    dialog.dismiss();
                    isShowing = false;
                });

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(String aVoid) {
                builder.create();
                builder.show();
//                isShowing = true;
            }
        }.execute("");
    }
}
