package com.definesys.dmportal.appstore.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.SubjectTable;
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
 * 获取审批记录
 * Created by 羽翎 on 2019/1/17.
 */

public class GetApprovalRecordPresent extends BasePresenter {
    public GetApprovalRecordPresent(Context context) {
        super(context);
    }

    public void getApprovalRecord (String leaveId){
        Map map = new HashMap();
        map.put("leaveId",leaveId);
        ViseHttp.POST(HttpConst.getApprovalRecordById)
                .tag(HttpConst.getApprovalRecordById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<ApprovalRecord>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ApprovalRecord>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPRVAL_RECORD,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                                break;

                        }

                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, errMsg);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        ViseHttp.cancelTag(HttpConst.getApprovalRecordById);
        super.unsubscribe();
    }
}
