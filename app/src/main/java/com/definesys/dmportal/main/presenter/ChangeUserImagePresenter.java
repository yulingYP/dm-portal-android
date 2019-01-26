package com.definesys.dmportal.main.presenter;

import android.content.Context;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
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

    public void uploadUserImage(String userId, File file){
        String fileName = file.getName();
        MediaType type = MediaType.parse("image/"+fileName.substring(fileName.lastIndexOf(".")+1));
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("userId",userId)
                .addFormDataPart("uuid",String.valueOf(SharedPreferencesUtil.getInstance().getUserId()))
                .addFormDataPart("file", fileName,RequestBody.create(type,file));

        ViseHttp.POST(HttpConst.uploadUserHeadPicture).setRequestBody(builder.build())
                .tag(HttpConst.uploadUserHeadPicture).request(new ACallback<BaseResponse>() {
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
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.update_head_fail));
                    }
                });
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.uploadUserHeadPicture);
    }
}