package com.definesys.dmportal.appstore.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.definesys.dmportal.appstore.bean.MyMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwangjr.rxbus.SmecRxBus;

/**
 *
 * Created by 羽翎 on 2019/3/6.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String TYPE = "type"; //这个type是为了Notification更新信息的，这个不明白的朋友可以去搜搜，很多

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);

        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }
        if(action!=null) {

            if (action.equals("notification_clicked")) {
                //处理点击事件
                String message = intent.getStringExtra("message");
                MyMessage myMessage=null;
                if (message != null && !"".equals(message) && message.contains("{")) {
                    myMessage = new Gson().fromJson(message, new TypeToken<MyMessage>() {
                    }.getType());
                }
                //启动activity
                SmecRxBus.get().post("startActivity",myMessage);
            }

            else if (action.equals("notification_cancelled")) {
                //处理滑动清除和点击删除事件
            }
        }
    }
}