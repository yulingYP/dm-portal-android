package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;
import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
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

    public void getApprovalRecordList (String leaveId,String isNeed){
        Map<String,String> map = new HashMap<>();
        map.put("leaveId",leaveId);
        map.put("isNeed",isNeed);//是否需要排除销假记录
        Log.d("myMap",new Gson().toJson(map));
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
    public void getApprovalRecordByMsgId(String msgId,String leaveId){
        Map<String,String> map =new HashMap<>();
        map.put("msgId",msgId);
        map.put("leaveId",leaveId);
        map.put("userId",String.valueOf(SharedPreferencesUtil.getInstance().getUserId()));
        map.put("type","msg");
        ViseHttp.POST(HttpConst.getApprovalRecordByDate)
                .tag(HttpConst.getApprovalRecordById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<MyMessage>>() {
                    @Override
                    public void onSuccess(BaseResponse<MyMessage> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPRVAL_RECORD_BY_DATE,  data);
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

//    public void getApprovalRecordByDate(String leaveId, Date approvalDate, Context context){
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(approvalDate);
//        //yyyy-MM-dd HH:mm:ss
//        String date = context.getString(R.string.request_date_type,calendar.get(Calendar.YEAR),
//                calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DATE),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND));
//
//        Map<String,String> map =new HashMap<>();
//        map.put("leaveId",leaveId);
//        map.put("approvalDate",date);
//        map.put("type",date);
//
//
//        Log.d("myMap",new Gson().toJson(map));
//        ViseHttp.POST(HttpConst.getApprovalRecordByDate)
//                .tag(HttpConst.getApprovalRecordById)
//                .setJson(new Gson().toJson(map))
//                .request(new ACallback<BaseResponse<ApprovalRecord>>() {
//                    @Override
//                    public void onSuccess(BaseResponse<ApprovalRecord> data) {
//                        switch (data.getCode()){
//                            case "200":
//                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPRVAL_RECORD_BY_DATE,  data);
//                                break;
//                            default:
//                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
//                                break;
//                        }
//
//                    }
//
//                    @Override
//                    public void onFail(int errCode, String errMsg) {
//                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
//                    }
//                });
//    }
    /**
     * 更新审批状态并插入审批记录
     * @param approvalRecord)
     */
    public void updateApprovalStatusById (ApprovalRecord approvalRecord){
        Log.d("myMap",new Gson().toJson(approvalRecord));
        ViseHttp.POST(HttpConst.updateApprovalStatusById)
                .tag(HttpConst.updateApprovalStatusById)
                .setJson(new Gson().toJson(approvalRecord))
                .request(new ACallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_UPDATE_APPRVAL_RECORD,  data);
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

    public void getLeaveInfoById (String leaveId){
        Map<String,String> map = new HashMap<>();
        map.put("leaveId",leaveId);
        Log.d("myMap",new Gson().toJson(map));
        ViseHttp.POST(HttpConst.getLeaveInfoByLeaveId)
                .tag(HttpConst.getLeaveInfoByLeaveId)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<LeaveInfo>>() {
                    @Override
                    public void onSuccess(BaseResponse<LeaveInfo> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO,  data);
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
        ViseHttp.cancelTag(HttpConst.getApprovalRecordById);
        ViseHttp.cancelTag(HttpConst.getLeaveInfoByLeaveId);
        ViseHttp.cancelTag(HttpConst.updateApprovalStatusById);
        super.unsubscribe();
    }
}
