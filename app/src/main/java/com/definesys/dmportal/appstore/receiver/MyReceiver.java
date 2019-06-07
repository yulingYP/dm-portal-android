package com.definesys.dmportal.appstore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.definesys.dmportal.MainApplication;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.MainActivity;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.definesys.dmportal.welcomeActivity.SplashActivity;
import com.example.jpushdemo.Logger;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwangjr.rxbus.SmecRxBus;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JIGUANG-Example";
	private static String extra = "";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				assert bundle != null;
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				assert bundle != null;
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);


			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				assert bundle != null;
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");

				//打开自定义的Activity
				Intent i = new Intent(context, SplashActivity.class);
				i.putExtra("message",extra);
				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
				context.startActivity(i);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				assert bundle != null;
				Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Logger.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception ignored){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			switch (key) {
				case JPushInterface.EXTRA_NOTIFICATION_ID:
					sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
					break;
				case JPushInterface.EXTRA_CONNECTION_CHANGE:
					sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
					break;
				case JPushInterface.EXTRA_EXTRA:
					if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
						Logger.i(TAG, "This message has no Extra data");
						continue;
					}

					try {
						JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
						Iterator<String> it = json.keys();

						while (it.hasNext()) {
							String myKey = it.next();
							sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
						}
					} catch (JSONException e) {
						Logger.e(TAG, "Get message extra JSON error!");
					}

					break;
				default:
					sb.append("\nkey:").append(key).append(", value:").append(bundle.get(key));
					break;
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		MyMessage myMessage = null;
//		JPushInterface.addLocalNotification(context, createNotification(context, myMessage, message));
		if("message".equals(bundle.get(JPushInterface.EXTRA_TITLE))) {//请假或审批消息
			if (message != null && !"".equals(message) && message.contains("{")) {
				myMessage = new Gson().fromJson(message, new TypeToken<MyMessage>() {
				}.getType());
			}
			if (MainActivity.notiManager != null){//app已初始化
				if(myMessage!=null)
					SmecRxBus.get().post("hasNotify", myMessage);
			}
			else {//app未初始化
				extra = message;
				JPushInterface.addLocalNotification(context, createNotification(context, myMessage));
			}
		}else if("token".equals(bundle.get(JPushInterface.EXTRA_TITLE))){//单机登陆验证
			if(!SharedPreferencesUtil.getInstance().getToken().equals(message)){
				MyCongfig.showMyDialog(R.string.no_one_tip);
			}
		}
		assert myMessage != null;
		Logger.d(TAG, message+"\n"+myMessage.toString());
	}
	//app未初始化时尝试建立本地消息
	public JPushLocalNotification createNotification(Context context, MyMessage myMessage){

		JPushLocalNotification jPushLocalNotification = new JPushLocalNotification();
		String title="aa";//标题
		String content="bb";//内容
		if(myMessage!=null){
			if(myMessage.getMessageType()==1) {//请假人审批结果
				title = context.getString(R.string.approval_result);
				content = context.getString(R.string.approval_result_tip_1);
			}else if(myMessage.getMessageType()==2) {//新的请假请求，跳转到详细页面
				title = context.getString(R.string.leave_request);
				content = context.getString(R.string.leave_request_tip_1);
			}if(myMessage.getMessageType()==4) {//申请权限结果
				title = context.getString(R.string.approval_result_3);
				content = myMessage.getMessageExtend2()==0?context.getString(R.string.approval_result_tip_3):context.getString(R.string.approval_result_tip_2);
			}else if(myMessage.getMessageType()==5) {//新的权限申请请求，跳转到详细页面
				title = context.getString(R.string.approval_result_4);
				content = context.getString(R.string.leave_request_tip_3);
			}else if(myMessage.getMessageType()==10){//新的请假请求，跳转到列表页面
				title = context.getString(R.string.leave_request);
				content = context.getString(R.string.leave_request_tip_2);
			}else if(myMessage.getMessageType()==11){//权限申请请求，跳转到列表页面
				title = context.getString(R.string.approval_result_4);
				content = context.getString(R.string.approval_result_tip_7);
			}
		}

		jPushLocalNotification.setTitle(title);
		jPushLocalNotification.setContent(content);
		jPushLocalNotification.setNotificationId(System.currentTimeMillis());
		return jPushLocalNotification;
	}
}
