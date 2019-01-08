package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.widget.Toast;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.SubjectTableActivity;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
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

    public void getTableInfo (Number stuID,String facultyId){
        Map map = new HashMap();
        map.put("stuId",stuID);
        map.put("facultyId",facultyId);
        ViseHttp.POST(HttpConst.getTable)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<SubjectTable>>() {
                    @Override
                    public void onSuccess(BaseResponse<SubjectTable> data) {
                        SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_TABLE_INFO,  data);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, errMsg);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        ViseHttp.cancelTag(HttpConst.getTable);
        super.unsubscribe();
    }
}
