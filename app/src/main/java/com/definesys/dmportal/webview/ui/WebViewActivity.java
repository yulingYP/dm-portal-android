package com.definesys.dmportal.webview.ui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.widget.LinearLayout;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.webview.jsinterface.JsInterface;
import com.jakewharton.rxbinding2.view.RxView;
import com.prim.primweb.core.PrimWeb;
import com.prim.primweb.core.jsloader.CommonJSListener;
import com.prim.primweb.core.webclient.webchromeclient.AgentChromeClient;
import com.prim.primweb.core.webclient.webviewclient.AgentWebViewClient;
import com.prim.primweb.core.webview.IAgentWebView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.WebViewActivity)
public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.mTitlebar)
    CustomTitleBar mTitlebar;

    @BindView(R.id.container)
    ViewGroup mLinearLayout;

    @Autowired(name = "url")
    String url;

    PrimWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);

        mTitlebar.setTitle("");

        RxView.clicks(   mTitlebar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(object ->{
                    if (!mAgentWeb.handlerBack()) {
                        WebViewActivity.this.finish();
                    }
                });


        mAgentWeb = PrimWeb.with(this)//传入Activity
                .setWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams
                .useDefaultUI()// 使用默认进度条
                .useDefaultTopIndicator()
                .setWebViewType(PrimWeb.WebViewType.Android)
                .setListenerCheckJsFunction(new CommonJSListener() {
                    @Override
                    public void jsFunExit(Object data) {

                    }

                    @Override
                    public void jsFunNoExit(Object data) {

                    }
                })
                .setWebViewClient(agentWebViewClient)
                .setWebChromeClient(agentChromeClient)
                .buildWeb()
                .lastGo()
                .launch(url);


        mAgentWeb.getJsInterface().addJavaObject(new JsInterface(mAgentWeb.getCallJsLoader()), "androidBridge");
    }

    AgentWebViewClient agentWebViewClient = new AgentWebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(IAgentWebView view, String url) {
            Log.e("", "shouldOverrideUrlLoading: " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(IAgentWebView view, WebResourceRequest request) {
            Log.e("", "shouldOverrideUrlLoading: WebResourceRequest -->　" + request.getUrl());
            return super.shouldOverrideUrlLoading(view, request);
        }
    };

    AgentChromeClient agentChromeClient = new AgentChromeClient() {
        @Override
        public void onReceivedTitle(View webView, String s) {
            super.onReceivedTitle(webView, s);
            if (mTitlebar != null) {
                mTitlebar.setTitle(s);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAgentWeb.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mAgentWeb.handlerKeyEvent(keyCode, event) || super.onKeyDown(keyCode, event);

    }


}
