package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.SubjectTableActivity;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 羽翎 on 2019/1/8.
 */

public class GetTableInfoPresenter extends BasePresenter {
    public GetTableInfoPresenter(Context context) {
        super(context);
    }

    public void getTableInfo (Number userId, int userType,String facultyId ){
        Map map = new HashMap();
        map.put("userId",userId);
        map.put("userType",userType);
        map.put("facultyId",facultyId);
        Log.d("myMap",new Gson().toJson(map).toString());
        ViseHttp.POST(HttpConst.getTable)
                .tag(HttpConst.getTable)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<SubjectTable>>() {
                    @Override
                    public void onSuccess(BaseResponse<SubjectTable> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_TABLE_INFO,  data);
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
        ViseHttp.cancelTag(HttpConst.getTable);
        super.unsubscribe();
    }
}
