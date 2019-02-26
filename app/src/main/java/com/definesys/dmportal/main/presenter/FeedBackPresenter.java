package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.main.bean.Feedback;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.luck.picture.lib.entity.LocalMedia;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.io.File;
import java.util.List;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by 羽翎 on 2019/2/26.
 */

public class FeedBackPresenter extends BasePresenter {
    public FeedBackPresenter(Context context) {
        super(context);
    }

    /**
     * 提交请假请求
     * @param selectImages 选择的图片
     */
    public void getRequestResult (Feedback feedback, List<LocalMedia> selectImages){

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody requestBody=null;
        //上传图片文件
        if(selectImages!=null&&selectImages.size()>0) {
            for (int i=0;i<selectImages.size();i++) {
                File file = new File(selectImages.get(i).getCompressPath()==null?selectImages.get(i).getPath():selectImages.get(i).getCompressPath());
                String fileName = file.getName();
                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                MediaType type = MediaType.parse("image/" + fileType);
                builder.addFormDataPart("files", fileName, RequestBody.create(type, file));
                builder.addFormDataPart("uuids", UUID.randomUUID().toString());
            }
            builder.addFormDataPart("uploadId", feedback.getFeedbackId());
            builder.addFormDataPart("type","2");
            requestBody = builder.build();
        }


        //submitLeaveInfo.setSubmitDate(DensityUtil.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",new Date()));

        Log.d("myMap",new Gson().toJson(feedback).toString());
        RequestBody finalRequestBody = requestBody;
        ViseHttp.POST(HttpConst.submitFeedBack)
                .tag(HttpConst.submitFeedBack)
                .setJson(new Gson().toJson(feedback))
                .request(new ACallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        switch (data.getCode()) {
                            case "200":
                                if(finalRequestBody == null)//没有图片要上传
                                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_REQUEST, data.getData());
                                else //有图片要上传
                                    updatePictures(finalRequestBody,data.getData());
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, errMsg);
                    }
                });

    }

    /**
     * 上传请假图片
     * @param finalRequestBody
     * @param msgId 消息id
     */
    private void updatePictures(RequestBody finalRequestBody,String msgId) {
        ViseHttp.POST(HttpConst.uploadPictures)
                .setRequestBody(finalRequestBody)
                .tag(HttpConst.submitFeedBack)
                .request(new ACallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        switch (data.getCode()) {
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_LEAVE_REQUEST, msgId);
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
        ViseHttp.cancelTag(HttpConst.submitFeedBack);
    }
}
