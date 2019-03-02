package com.definesys.dmportal.appstore.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 羽翎 on 2019/3/2.
 */

public class LeaveAuthorityPresenter extends BasePresenter {
    public LeaveAuthorityPresenter(Context context) {
        super(context);
    }
    //获取用户对应权限的详细信息
    public void getUserAuthorityDetail(Number userId, int authorityType, int authority){
        HashMap<String,Number> map = new HashMap<>();
        map.put("userId",userId);
        map.put("authority",authorityType==1?authority+10:authority); //审批老师权限的时候+10；方便以后整理textView
        map.put("authorityType",authorityType);
        ViseHttp.POST(HttpConst.getAuthorityDetailInfo)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> data) {
                        switch (data.getCode()){
                            case "200":
                                data.setExtendInfo(authorityType);
                                data.setMsg(""+authority);
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO,  data);
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
        ViseHttp.cancelTag(HttpConst.getAuthorityDetailInfo);
    }
}
