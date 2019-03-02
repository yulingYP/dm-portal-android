package com.definesys.dmportal.appstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;

//更新请假权限
@Route(path = ARouterConstants.UpdateLeAutActivity)
public class UpdateLeAutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_le_aut);
    }
}
