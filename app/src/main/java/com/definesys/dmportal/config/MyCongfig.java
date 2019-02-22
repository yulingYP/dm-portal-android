package com.definesys.dmportal.config;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by 羽翎 on 2018/9/30.
 */

public  class MyCongfig {

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
}
