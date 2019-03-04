package com.definesys.dmportal.appstore.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.ApplyRecord;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 羽翎 on 2019/3/5.
 */

public class ApplyInfoPresenter extends BasePresenter {
    public ApplyInfoPresenter(Context context) {
        super(context);
    }

    //获取 applyId对应的信息实体
    public void getApplyInfoById(String applyId){
        Map<String,String>map = new HashMap<>();
        map.put("applyId",applyId);
        map.put("type","info");
        ViseHttp.POST(HttpConst.getApplyInfoById)
                .tag(HttpConst.getApplyInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<ApplyInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<ApplyInfo> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPLY_INFO,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });

    }
    //获取 applyId对应的信息实体
    public void getApplyRecordById(String applyId){
        Map<String,String>map = new HashMap<>();
        map.put("applyId",applyId);
        map.put("type","record");
        ViseHttp.POST(HttpConst.getApplyInfoById)
                .tag(HttpConst.getApplyInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<ApplyRecord>>() {
                    @Override
                    public void onSuccess(BaseResponse<ApplyRecord> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPLY_RECORD_INFO,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });

    }
    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getApplyInfoById);
    }
}
