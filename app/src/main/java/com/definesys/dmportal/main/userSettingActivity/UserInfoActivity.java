package com.definesys.dmportal.main.userSettingActivity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.User;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.UserInfoPresent;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import java.util.Objects;

@Route(path = ARouterConstants.UserInfoActivity)
public class UserInfoActivity extends BaseActivity<UserInfoPresent> {

    @Autowired(name = "userId")
    Number userId;
    private User user;//用户信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        if(Objects.equals(userId, SharedPreferencesUtil.getInstance().getUserId())){//本人信息
            user = SharedPreferencesUtil.getInstance().getUserInfo();
            intView();
        }else {
            mPersenter.getUserInfo(userId);
        }

    }

    private void intView() {
    }

    /**
     * 获取用户信息成功
     * @param data 信息
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_REQUEST_USER_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getUserUrl(BaseResponse<User> data) {
       if(MyActivityManager.getInstance().getCurrentActivity()==this){
           user = data.getData();
           intView();
        }
    }
    @Override
    public UserInfoPresent getPersenter() {
        return new UserInfoPresent(this);
    }
}
