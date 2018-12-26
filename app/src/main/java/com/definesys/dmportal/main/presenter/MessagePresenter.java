package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.main.bean.DataContent;
import com.definesys.dmportal.main.bean.Message;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

public class MessagePresenter extends BasePresenter {

    public MessagePresenter(Context context) {
        super(context);
    }

    public void getMsg(String phoneNumber,int requestPage,int i){
        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("userCode","17863136613");
        msgMap.put("page",String.valueOf(requestPage));
        msgMap.put("size","10");
        ViseHttp.POST(HttpConst.getStaticMessage).setJson(new Gson().toJson(msgMap)).tag(HttpConst.getStaticMessage)
        .request(new ACallback<BaseResponse<DataContent<Message>>>() {
            @Override
            public void onSuccess(BaseResponse data) {
            switch (data.getCode()){
                case "200":
                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_MESSAGE,data.getData());break;
                default:
                    SmecRxBus.get().post(MainPresenter.ERROR_GET_MESSAGE,data.getMsg());break;
            }
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                SmecRxBus.get().post(MainPresenter.ERROR_GET_MESSAGE,i);
            }
        });
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getStaticMessage);
    }
}
