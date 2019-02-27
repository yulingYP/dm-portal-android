package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
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

    //请假的全部历史记录
    public void getAllLeaveInfoList (Number userId,Number page){
        HashMap<String,Number> map = new HashMap<>();
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
                .request(new ACallback<BaseResponse<List<LeaveInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<LeaveInfo>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO_LIST,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }

    //待审批处理的全部请假记录
    public void getAllApprovalList (Number userId,Number page,Number approvalStudentAuthority,Number approvalTeacherAuthority,Number userType){
        HashMap<String,Number> map = new HashMap<>();
        map.put("userId",userId);
        map.put("page",page);
        map.put("approvalStuAut",approvalStudentAuthority);
        map.put("approvalTeaAut",approvalTeacherAuthority);
        map.put("userType",userType);
        Log.d("myMap",new Gson().toJson(map).toString());

        ViseHttp.CONFIG()
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5);

        ViseHttp.POST(HttpConst.getDealApprovalListById)
                .tag(HttpConst.getDealApprovalListById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<LeaveInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<LeaveInfo>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO_LIST,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }

    //审批的全部历史记录
    public void getAllApprovalHistoryList (Number userId,Number page){
        HashMap<String,Number> map = new HashMap<>();
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

        ViseHttp.POST(HttpConst.getApprovalHistoryListById)
                .tag(HttpConst.getApprovalHistoryListById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<ApprovalRecord>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ApprovalRecord>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPROVAL_HISTORY_LIST,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }


    //根据关键字搜索请假信息或审批记录
    public void getSearchLeaveInfoList (Number userId,Number page,Number checkCode,Number type,String content){
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("page",page);
        map.put("checkCode",checkCode);
        map.put("type",type);
        map.put("content",content);
        Log.d("myMap",new Gson().toJson(map).toString());

        ViseHttp.CONFIG()
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5);

        ViseHttp.POST(HttpConst.getLeaveSearchList)
                .tag(HttpConst.getLeaveInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<LeaveInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<LeaveInfo>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO_LIST,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }
    @Override
    public void unsubscribe() {
        ViseHttp.cancelTag(HttpConst.getLeaveInfoById);
        ViseHttp.cancelTag(HttpConst.getDealApprovalListById);
        ViseHttp.cancelTag(HttpConst.getApprovalHistoryListById);
        super.unsubscribe();
    }
//    Number userId,Number page,Number approvalStudentAuthority,Number approvalTeacherAuthority,Number userType
    public void getSearchApprovalList(Number userId,Number page,Number approvalStudentAuthority,Number approvalTeacherAuthority,Number userType, Number checkCode, Number type, String content) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("page", page);
        map.put("approvalStuAut",approvalStudentAuthority);
        map.put("approvalTeaAut",approvalTeacherAuthority);
        map.put("userType",userType);
        map.put("checkCode",checkCode);
        map.put("type",type);
        map.put("content",content);
        Log.d("myMap",new Gson().toJson(map).toString());

        ViseHttp.CONFIG()
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5);

        ViseHttp.POST(HttpConst.getLeaveSearchList)
                .tag(HttpConst.getDealApprovalListById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<LeaveInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<LeaveInfo>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO_LIST,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }

    //条件查询已审批的历史记录
    public void getSearchApprovalHistoryList (Number userId,Number page,Number checkCode ,Number type,String content){
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("page",page);
        map.put("checkCode",checkCode);
        map.put("type",type);
        map.put("content",content);
        Log.d("myMap",new Gson().toJson(map).toString());

        ViseHttp.CONFIG()
                //配置读取超时时间，单位秒
                .readTimeout(5)
                //配置写入超时时间，单位秒
                .writeTimeout(5)
                //配置连接超时时间，单位秒
                .connectTimeout(5);

        ViseHttp.POST(HttpConst.getLeaveSearchList)
                .tag(HttpConst.getApprovalHistoryListById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<ApprovalRecord>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ApprovalRecord>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPROVAL_HISTORY_LIST,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }

                    }
                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("myMap","fail");
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }
}
