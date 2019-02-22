package com.definesys.dmportal.appstore.receiver;



/**
 * Created by 羽翎 on 2019/2/21.
 */

public class MyXGReceiver /*extends JPushMessageReceiver*/{
//    @Override
//    public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {
//        Log.d("myXG","onRegisterResult"+xgPushRegisterResult.toString());
//        MainActivity.XG_isBind = true;
//    }
//
//    @Override
//    public void onUnregisterResult(Context context, int i) {
//        Log.d("myXG","onUnregisterResult");
//        MainActivity.XG_isBind = false;
//        //登陆之后解绑的话，重新绑定
//        SmecRxBus.get().post("reBind","");
//    }
//
//    @Override
//    public void onSetTagResult(Context context, int i, String s) {
//        Log.d("myXG","onSetTagResult");
//    }
//
//    @Override
//    public void onDeleteTagResult(Context context, int i, String s) {
//        Log.d("myXG","onDeleteTagResult");
//    }
//
//    @Override
//    public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {
//        Log.d("myXG","onTextMessage:"+xgPushTextMessage.getCustomContent());
//        //消息
////        if(!"isToken".equals(xgPushTextMessage.getTitle())){
////            SmecRxBus.get().post("hasNotify",xgPushTextMessage);
////            return;
////        }
////        //单点登录
////        if(!xgPushTextMessage.getContent().equals(SharedPreferencesUtil.getInstance().getToken()))
////            MainApplication.getInstances().showDialog(R.string.no_one_tip);;
//
//    }
//
//    @Override
//    public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {
//        Log.d("myXG","onNotifactionClickedResult");
//        if (context == null || xgPushClickedResult == null) {
//            return;
//        }
//        if (xgPushClickedResult.getActionType() == XGPushClickedResult.NOTIFACTION_CLICKED_TYPE) {
//            // 通知在通知栏被点击啦。。。。。
//            // APP自己处理点击的相关动作
//            // 这个动作可以在activity的onResume也能监听，请看第3点相关内容
//            // Toast.makeText(context,"通知在通知栏被点击啦。。。。",Toast.LENGTH_SHORT).show();
////            ARouter.getInstance()
////                    .build(ARouterConstants.TransferRouterActivity)
////                    .withTransition(R.anim.x11,R.anim.x22)
////                    .navigation(context);
//        } else if (xgPushClickedResult.getActionType() == XGPushClickedResult.NOTIFACTION_DELETED_TYPE) {
//            // 通知被清除啦。。。。
//            // APP自己处理通知被清除后的相关动作
//        }
//    }
//
//    @Override
//    public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {
//        Log.d("myXG","onNotifactionShowedResult");
////        if (!"".equals(xgPushShowedResult.getContent())) {
////            MainApplication.getInstances().setHasNewMessage(true);
////            MainApplication.getInstances().getXgPushShowedResultList().add(xgPushShowedResult);
////            SmecRxBus.get().post("setRed",true);
////        }
//    }

}
