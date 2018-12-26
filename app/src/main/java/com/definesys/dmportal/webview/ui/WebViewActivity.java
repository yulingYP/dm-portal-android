package com.definesys.dmportal.webview.ui;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.definesys.dmportal.R;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.webview.jsinterface.JsInterface;
import com.prim.primweb.core.PrimWeb;
import com.prim.primweb.core.jsloader.CommonJSListener;
import com.prim.primweb.core.webclient.webchromeclient.AgentChromeClient;
import com.prim.primweb.core.webclient.webviewclient.AgentWebViewClient;
import com.prim.primweb.core.webview.IAgentWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.mTitlebar)
    CustomTitleBar mTitlebar ;

    @BindView(R.id.container)
    ViewGroup mLinearLayout ;

    PrimWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ButterKnife.bind(this);

        mTitlebar.setTitle("浏览器");
        mTitlebar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAgentWeb.handlerBack()) {
                    WebViewActivity.this.finish();
                }

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
                .launch("http://192.168.43.19:53274/dm-portal-js-api/index.html");


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
        if (mAgentWeb.handlerKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
