package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.bean.SubmitLeaveInfo;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取请假历史
 * Created by 羽翎 on 2019/1/15.
 */

public class GetLeaveInfoHistoryPresenter extends BasePresenter {
    public GetLeaveInfoHistoryPresenter(Context context) {
        super(context);
    }

    public void getTableInfo (Number userId,Number page){
        Map map = new HashMap();
        map.put("userId",userId);
        map.put("page",page);
        Log.d("myMap",new Gson().toJson(map).toString());

        ViseHttp.CONFIG()
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5);

        ViseHttp.POST(HttpConst.getLeaveInfoById)
                .tag(HttpConst.getLeaveInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<SubmitLeaveInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<SubmitLeaveInfo>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_HISTORY,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, errMsg);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        ViseHttp.cancelTag(HttpConst.getLeaveInfoById);
        super.unsubscribe();
    }
}
