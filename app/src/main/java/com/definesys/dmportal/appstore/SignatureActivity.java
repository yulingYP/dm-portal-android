package com.definesys.dmportal.appstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;

@Route(path = ARouterConstants.SignatureActivity)
public class SignatureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
    }
}
