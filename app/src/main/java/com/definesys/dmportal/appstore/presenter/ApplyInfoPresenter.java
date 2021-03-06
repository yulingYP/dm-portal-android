package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import android.util.Log;

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
import java.util.List;
import java.util.Map;

/**
 *
 * Created by 羽翎 on 2019/3/5.
 */

public class ApplyInfoPresenter extends BasePresenter {
    public ApplyInfoPresenter(Context context) {
        super(context);
    }

    //获取 applyId对应的信息实体
    public void getApplyInfoById(long applyId){
        Map<String,Object>map = new HashMap<>();
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
    public void getApplyRecordById(long applyId){
        Map<String,Object>map = new HashMap<>();
        map.put("applyId",applyId);
        map.put("type","record");
        ViseHttp.POST(HttpConst.getApplyInfoById)
                .tag(HttpConst.getApplyInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<ApplyRecord>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ApplyRecord>> data) {
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
    //提交审批结果
    public void submitApplyResult(ApplyRecord applyRecord){
        ViseHttp.POST(HttpConst.submitApplyResult)
                .tag(HttpConst.getApplyInfoById)
                .setJson(new Gson().toJson(applyRecord))
                .request(new ACallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_SUBMIT_APPLY_RESULT,  data);
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

    /**
     * 获取提交的权限申请
     * @param userId 用户id
     * @param stuAut 审批学生的权限
     * @param teaAut 审批教师的权限
     * @param type 1.未审批申请 2.已审批
     * @param requestId list中最后一个数据的id
     */
    public void getApplyInfoList(Number userId,Number stuAut,Number teaAut,Number type,Number requestId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("stuAut",stuAut);
        map.put("teaAut",teaAut);
        map.put("requestId", requestId);
        map.put("type",type);
        ViseHttp.POST(HttpConst.getRequestApplyList)
                .tag(HttpConst.getApplyInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<ApplyInfo>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ApplyInfo>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_REQUEST_APPLY_LIST,  data);
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

    /**
     * 获取历史审批记录
     * @param userId 用户id
     */
    public void getHistoryApprovalApplyList(Number userId,Number requestId){
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("requestId",requestId);
        map.put("type",2);
        ViseHttp.POST(HttpConst.getRequestApplyList)
                .tag(HttpConst.getApplyInfoById)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<ApplyRecord>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<ApplyRecord>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPROVAL_HISTORY_LIST , data);
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

    public void getSearchApplyList(int type, Number userId, int checkCode, String content, long requestId, Integer stuAut, Integer teaAut){
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("requestId", requestId);
        map.put("checkCode",checkCode);
        map.put("type",type);
        map.put("content",content);
        if(type==0&&checkCode==-1){//按内容查询待审批记录
            map.put("stuAut",stuAut);
            map.put("teaAut",teaAut);
        }
        Log.d("myMap",new Gson().toJson(map));
        if(type==0||type==1) {
            ViseHttp.POST(HttpConst.getApplySearchList)
                    .tag(HttpConst.getApplyInfoById)
                    .setJson(new Gson().toJson(map))
                    .request(new ACallback<BaseResponse<List<ApplyInfo>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<ApplyInfo>> data) {
                            switch (data.getCode()) {
                                case "200":
                                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_REQUEST_APPLY_LIST, data);
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
        }else if(type==2){
            ViseHttp.POST(HttpConst.getApplySearchList)
                    .tag(HttpConst.getApplyInfoById)
                    .setJson(new Gson().toJson(map))
                    .request(new ACallback<BaseResponse<List<ApplyRecord>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<ApplyRecord>> data) {
                            switch (data.getCode()){
                                case "200":
                                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPROVAL_HISTORY_LIST , data);
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
    }
    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getApplyInfoById);
    }
}
