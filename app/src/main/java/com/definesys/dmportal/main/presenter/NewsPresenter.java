package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.main.bean.DataContent;
import com.definesys.dmportal.main.bean.News;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

public class NewsPresenter extends BasePresenter {

    public NewsPresenter(Context context) {
        super(context);
    }

    public void getNews(int requestPage,int i){
        Map<String,Integer> newsMap = new HashMap<>();
        newsMap.put("page",requestPage);
        newsMap.put("size",4);
        ViseHttp.POST(HttpConst.getNews).setJson(new Gson().toJson(newsMap))
                .tag(HttpConst.getNews).request(new ACallback<BaseResponse<DataContent<News>>>() {
                    @Override
                    public void onSuccess(BaseResponse<DataContent<News>> data) {
                        switch (data.getCode()) {
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_NEWS, data.getData());
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_GET_NEWS, data.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_GET_NEWS,i);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getNews);
    }
}