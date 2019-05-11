package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagePresenter extends BasePresenter {

    public MessagePresenter(Context context) {
        super(context);
    }

    //获取信息列表
    public void getMsgList(Number userId,Number requestId){
        Map<String,Number> msgMap = new HashMap<>();
        msgMap.put("userId",userId);
        msgMap.put("requestId",requestId);
        ViseHttp.CONFIG()
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5);
        ViseHttp.POST(HttpConst.getStaticMessage)
                .setJson(new Gson().toJson(msgMap))
                .tag(HttpConst.getStaticMessage)
        .request(new ACallback<BaseResponse<List<MyMessage>>>() {
            @Override
            public void onSuccess(BaseResponse<List<MyMessage>> data) {
            switch (data.getCode()){
                case "200":
                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_MESSAGE,data);
                    break;
                default:
                    SmecRxBus.get().post(MainPresenter.ERROR_GET_MESSAGE,data.getMsg());
                    break;
            }
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                SmecRxBus.get().post(MainPresenter.ERROR_GET_MESSAGE,"");
            }
        });
    }
    //更新信息的状态
    public void updateMsgStatus(Number userId,Long messageId){
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId.intValue());
        map.put("messageId",messageId);
        Log.d("myMap",new Gson().toJson(map));
        ViseHttp.POST(HttpConst.updateMsgStatus)
                .tag(HttpConst.getStaticMessage)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<Integer>>() {
                    @Override
                    public void onSuccess(BaseResponse<Integer> data) {
                        Log.d("mydemo",data.getMsg());
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("mydemo",errMsg);
                    }
                });

    }
    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getStaticMessage);
    }
}
