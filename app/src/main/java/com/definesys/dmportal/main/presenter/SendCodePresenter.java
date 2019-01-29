package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;

import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.main.util.MD5Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.request.PostRequest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendCodePresenter extends BasePresenter {

    public SendCodePresenter(Context context) {
        super(context);
    }

    /**
     * 获取验证码
     * @param phone 电话
     * @param emailType 验证码类型 1.登陆验证 2.修改密码 3.手机绑定验证 4.手机解绑解绑
     */
    public void sendVerifyCode(String phone,int emailType) {
        Map<String,Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("emailType", emailType);
        PostRequest postRequest = ViseHttp.POST(HttpConst.getEmailCode)
                .setJson(new Gson().toJson(map))
                .tag(HttpConst.getEmailCode);
        postRequest.request(new ACallback<BaseResponse<HashMap<String,String>>>() {
            @Override
            public void onSuccess(BaseResponse<HashMap<String,String>> data) {
                switch (data.getCode()) {
                    case "200":

                            sendEmail(data.getData().get("code"),phone,data.getData().get("data1"),data.getData().get("data2"));
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


    /*
     发送验证码修改密码(忘记密码)
    */
    public void sendVerifyCodeForChangePsw(String phoneNumber) {
        Map<String,String> map = new HashMap<>();
        map.put("userCode", phoneNumber);
        map.put("smsType", "Passwd");
        PostRequest postRequest = ViseHttp.POST(HttpConst.getEmailCode)
                .setJson(new Gson().toJson(map))
                .tag(HttpConst.getEmailCode);
        postRequest.request(new ACallback<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse data) {
                switch (data.getCode()) {
                    case "200":
                        SmecRxBus.get().post(MainPresenter.SUCCESSFUL_SEND_CODE_FORGET_PSW, data.getMsg());
                        break;

                    default:
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                        break;
                }
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,"");
            }
        });
        ViseHttp.BASE(postRequest);
    }
    public void sendEmail(String code,String phone,String data1,String data2)  {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(mContext.getString(R.string.date_type_3));
        String date = sDateFormat.format(new Date());
        String encode = null;
        try {
            encode = URLEncoder.encode(mContext.getString(R.string.email_model,code), "GBK").toLowerCase();
        } catch (UnsupportedEncodingException e) {
            SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail));
            e.printStackTrace();
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",data1)
                .add("pwd", MD5Util.string2MD5(data2+date))
                .add("mobile",phone)
                .addEncoded("content",encode)
                .add("timestamp",date)
                .add("custid","fast_school")
                .build();
        Request request = new Request.Builder()
                .url(HttpConst.eamilUrl)
                .post(requestBody)
                .build();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = null;
                    try {
                        response = okHttpClient.newCall(request).execute();
                        String responseDate = response.body().string();
                        Log.d("myMap", responseDate);
                        Gson gson = new Gson();
                        java.lang.reflect.Type type = new TypeToken<Map>() {
                        }.getType();
                        Map result = gson.fromJson(responseDate, type);
                        double resultCode = (double)result.get("result");
                        if(resultCode==0)//成功
                          SmecRxBus.get().post(MainPresenter.SUCCESSFUL_SEND_CODE, "");
                        else if(resultCode==-100003){
                          SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail_1));
                        }
                        else if(resultCode==-100012){
                          SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail_2));
                        }
                        else if(resultCode==-100014){
                          SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail_3));
                        }
                        else if(resultCode==-100029){
                            SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail_4));
                        }
                        else if(resultCode==-100999){
                          SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail_5));
                        }
                        else {
                          SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail_6));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK,mContext.getString(R.string.send_email_fail));
                    }

                }
            }).start();
    }
    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getEmailCode);
    }
}
