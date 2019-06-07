package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by 羽翎 on 2019/1/20.
 */

public class GetCurrentLeaveInfoPresenter extends BasePresenter {
    public GetCurrentLeaveInfoPresenter(Context context) {
        super(context);
    }
    public void getCurrentLeaveInfo(Number userId){
        Map<String,Number> map = new HashMap<>();
        map.put("userId",userId);
        Log.d("myMap", new Gson().toJson(map));
        ViseHttp.POST(HttpConst.getCurrentLeaveInfoById)
                .tag(HttpConst.getCurrentLeaveInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<LeaveInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<LeaveInfo> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_CURRENT_LEAVE_INFO,  data);
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
        ViseHttp.cancelTag(HttpConst.getCurrentLeaveInfoById);
        super.unsubscribe();
    }
}
