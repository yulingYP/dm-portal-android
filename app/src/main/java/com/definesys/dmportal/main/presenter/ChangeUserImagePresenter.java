package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ChangeUserImagePresenter extends BasePresenter {

    public ChangeUserImagePresenter(Context context) {
        super(context);
    }

    /**
     * 上传头像或签名
     * @param userId i
     * @param file f
     * @param updateType 0.头像 1.签名
     */
    public void uploadUserImage(String userId, File file,String updateType){
        String fileName = file.getName();
        MediaType type = MediaType.parse("image/"+fileName.substring(fileName.lastIndexOf(".") + 1));
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("userId",userId)
                .addFormDataPart("uuid",userId)
                .addFormDataPart("type",updateType)
                .addFormDataPart("file", fileName,RequestBody.create(type,file));

        ViseHttp.POST(HttpConst.uploadUserPicture).setRequestBody(builder.build())
                .tag(HttpConst.uploadUserPicture).request(new ACallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse data) {
                        switch (data.getCode()) {
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_UPLOAD_USER_IMAGE, file.getPath());
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,data.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,errMsg);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.uploadUserPicture);
    }
}