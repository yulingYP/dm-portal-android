package com.definesys.dmportal.main.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.bean.User;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by 羽翎 on 2019/1/25.
 */

public class UserInfoPresent extends BasePresenter {
    private int requestCount;//请求次数

    public UserInfoPresent(Context context) {
        super(context);
        requestCount=0;
    }
    //获取用户信息
    public void getUserInfo(Number id){
        //提示单机登陆或账号冻结
        if(MyCongfig.isShowing)
            return;
        Map<String,Number> map = new HashMap<>();
        map.put("userId",id);
        Log.d("myMap",new Gson().toJson(map));
        ViseHttp.POST(HttpConst.getUserInfo)
                .tag(HttpConst.getUserInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<User>>() {
                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        switch (data.getCode()) {
                            case "200":
                                if(data.getData().getUserId()==SharedPreferencesUtil.getInstance().getUserId().intValue()) {//获取的是自己的信息
                                    SharedPreferencesUtil.getInstance().setUser(data.getData());
                                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_USER_INFO, data.getData().getUserImage());
                                }
                                //获取的是指定的信息
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_REQUEST_USER_INFO, data);
                                break;
                            default:
//                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                if(++requestCount<=5)
                                 getUserInfo(id);//重新获取
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        if(++requestCount<=5)
                         getUserInfo(id);//重新获取
//                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, errMsg);
                    }
                });


    }

    //获取用户姓名
    public void getUserName(Number id, int userType, TextView textView, ProgressBar progressBar){
        Map<String,Number> map = new HashMap<>();
        map.put("userId",id);
        map.put("userType",userType);
        Log.d("myMap",new Gson().toJson(map));
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("tv_name",textView);
        hashMap.put("progress",progressBar);
        ViseHttp.POST(HttpConst.getUserName)
                .tag(HttpConst.getUserInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        switch (data.getCode()) {
                            case "200":
                                hashMap.put("data",data.getData());
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_USER_NAME, hashMap);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK_NAME, hashMap);
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK_NAME, hashMap);
                    }
                });
    }
    //获取发送失败和未读的信息
    public void getPushErrorReadMsg(Number id){
        //提示单机登陆或账号冻结
        if(MyCongfig.isShowing) return;
        Map<String,Number> map = new HashMap<>();
        map.put("userId",id);
        Log.d("myMap",new Gson().toJson(map));
        ViseHttp.POST(HttpConst.getPushErrorMessage)
                .tag(HttpConst.getUserInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<ArrayList<MyMessage>>>() {
                    @Override
                    public void onSuccess(BaseResponse<ArrayList<MyMessage>> data) {
                        switch (data.getCode()) {
                            case "200":
                                if(data.getData()!=null&&data.getData().size()>0)
                                    SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_PUSH_ERROR_MSG, data.getData());
                                break;
                            default:
                                Log.d("mydemo",data.getMsg());
                                if(++MyCongfig.tryCount<=3)
                                    getPushErrorReadMsg(id);
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Log.d("mydemo",errMsg);
                        if(++MyCongfig.tryCount<=3)
                            getPushErrorReadMsg(id);
                    }
                });
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getUserInfo);
    }
}
