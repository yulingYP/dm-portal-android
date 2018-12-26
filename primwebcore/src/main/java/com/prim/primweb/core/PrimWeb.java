package com.prim.primweb.core;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.prim.primweb.core.file.FileValueCallbackMiddleActivity;
import com.prim.primweb.core.handler.IKeyEvent;
import com.prim.primweb.core.handler.IKeyEventInterceptor;
import com.prim.primweb.core.handler.KeyEventHandler;
import com.prim.primweb.core.jsloader.CommonJSListener;
import com.prim.primweb.core.jsloader.CommonJavaObject;
import com.prim.primweb.core.uicontroller.AbsWebUIController;
import com.prim.primweb.core.uicontroller.BaseIndicatorView;
import com.prim.primweb.core.uicontroller.DefaultWebUIController;
import com.prim.primweb.core.uicontroller.IndicatorController;
import com.prim.primweb.core.uicontroller.IndicatorHandler;
import com.prim.primweb.core.uicontroller.WebViewManager;
import com.prim.primweb.core.jsinterface.IJsInterface;
import com.prim.primweb.core.jsinterface.SafeJsInterface;
import com.prim.primweb.core.jsloader.ICallJsLoader;
import com.prim.primweb.core.jsloader.SafeCallJsLoaderImpl;
import com.prim.primweb.core.webclient.PrimChromeClient;
import com.prim.primweb.core.webclient.PrimWebClient;
import com.prim.primweb.core.webclient.webchromeclient.AgentChromeClient;
import com.prim.primweb.core.webclient.webviewclient.AgentWebViewClient;
import com.prim.primweb.core.weblife.IWebLifeCycle;
import com.prim.primweb.core.weblife.WebLifeCycle;
import com.prim.primweb.core.websetting.DefaultWebSetting;
import com.prim.primweb.core.websetting.IAgentWebSetting;
import com.prim.primweb.core.websetting.X5DefaultWebSetting;
import com.prim.primweb.core.urlloader.IUrlLoader;
import com.prim.primweb.core.urlloader.UrlLoader;
import com.prim.primweb.core.webview.IAgentWebView;
import com.prim.primweb.core.webview.AndroidAgentWebView;
import com.prim.primweb.core.webview.X5AgentWebView;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 作    者：linksus
 * 版    本：1.0
 * 创建日期：5/11 0011
 * 描    述：代理webview的总入口
 * 修订历史：
 * ================================================
 */
public class PrimWeb {

    public static final String BRIDGECHECK = "checkJsBridge";

    private ICallJsLoader callJsLoader;

    public static boolean DEBUG = false;

    private IAgentWebView webView;
    private ViewGroup mViewGroup;
    private ViewGroup.LayoutParams mLayoutParams;
    private WeakReference<Activity> context;
    private int mIndex = 0;
    private IAgentWebSetting setting;
    private Map<String, String> headers;
    private IUrlLoader urlLoader;
    private View mView;

    /**
     * 设置webview的模式:
     * Strict - 严格模式
     * Normal - 正常模式
     */
    public enum ModeType {
        Strict, Normal
    }

    public enum WebViewType {
        Android, X5
    }

    private WebViewType webViewType = WebViewType.Android;

    private ModeType modeType = ModeType.Normal;

    private static final String TAG = "PrimWeb";

    private IJsInterface mJsInterface;

    private AgentWebViewClient agentWebViewClient;

    private AgentChromeClient agentWebChromeClient;

    //初始化个数 30 * 0.75 多个左右,考虑到哈希表默认大小只有 4 * 0.75 个
    //而哈希表的缺点是:扩容性能会下降 初始化时提前计算好上限.
    private HashMap<String, Object> mJavaObject = new HashMap<>(30);
    private WebViewClient webViewClient;
    private WebChromeClient webChromeClient;
    private com.tencent.smtt.sdk.WebViewClient x5WebViewClient;
    private com.tencent.smtt.sdk.WebChromeClient x5WebChromeClient;

    private CommonJSListener commonJSListener;

    //webview的生命周期管理
    private IWebLifeCycle webLifeCycle;

    //返回键的处理类
    private IKeyEvent keyEvent;

    // 返回键拦截，有特殊处理的可以实现此接口
    private IKeyEventInterceptor keyEventInterceptor;

    //是否允许网页打开其他应用
    private boolean alwaysOpenOtherPage;

    private WebViewManager webViewManager;

    private AbsWebUIController absWebUIController;

    private boolean isGeolocation;

    private boolean allowUploadFile;

    private boolean invokingThird;

    public static PrimBuilder with(Activity context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }
        return new PrimBuilder(context);
    }

    public static void init(Application application) {
        // X5浏览器初始化
        QbSdk.initX5Environment(application, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean b) {

            }
        });
    }


    /**
     * 飞船建造阶段 -- 初始化各种配置设定
     *
     * @param builder
     *         飞船设定系统
     */
    PrimWeb(PrimBuilder builder) {
        doCheckSafe(builder);

        createLayout(builder);

        webLifeCycle = new WebLifeCycle(webView);
        if (builder.mJavaObject != null && !builder.mJavaObject.isEmpty()) {
            this.mJavaObject.putAll(builder.mJavaObject);
        }
    }


    /** webView 安全检查 --- 检查飞船的各种设定是否安全 */
    private void doCheckSafe(PrimBuilder builder) {
        this.webView = builder.webView;
        this.mView = builder.mView;
        this.mViewGroup = builder.mViewGroup;
        this.mLayoutParams = builder.mLayoutParams;
        this.context = builder.context;
        this.mIndex = builder.mIndex;
        this.setting = builder.setting;
        this.headers = builder.headers;
        this.urlLoader = builder.urlLoader;
        this.callJsLoader = builder.callJsLoader;
        this.modeType = builder.modeType;
        this.agentWebViewClient = builder.agentWebViewClient;
        this.webViewClient = builder.webViewClient;
        this.x5WebViewClient = builder.x5WebViewClient;
        this.webChromeClient = builder.webChromeClient;
        this.x5WebChromeClient = builder.x5WebChromeClient;
        this.webViewType = builder.webViewType;
        this.agentWebChromeClient = builder.agentWebChromeClient;
        this.commonJSListener = builder.commonJSListener;
        this.alwaysOpenOtherPage = builder.alwaysOpenOtherPage;
        this.absWebUIController = builder.absWebUIController;
        this.isGeolocation = builder.isGeolocation;
        this.allowUploadFile = builder.allowUploadFile;
        this.invokingThird = builder.invokingThird;
        if (null == webView) {//webview 不能为空
            webView = new AndroidAgentWebView(context.get());
            mView = webView.getAgentWebView();
        }
        webView.removeRiskJavascriptInterface();
        if (this.absWebUIController == null) {
            this.absWebUIController = new DefaultWebUIController(context.get());
        }
    }

    /**
     * 创建WebView的layout --- 创建飞船整体,飞船建造完毕
     *
     * @param builder
     *         PrimBuilder
     */
    private void createLayout(PrimBuilder builder) {
        webViewManager = WebViewManager.createWebView()
                .setActivity(context.get())
                .setViewGroup(mViewGroup)
                .setAgentWebView(webView)
                .setLp(mLayoutParams)
                .setCustomTopIndicator(builder.customTopIndicator)
                .setNeedTopProgress(builder.needTopIndicator)
                .setColor(builder.mColor)
                .setIndex(mIndex)
                .setColorPaser(builder.colorPaser)
                .setErrorClickId(builder.errorClickId)
                .setErrorLayout(builder.errorLayout)
                .setErrorView(builder.errorView)
                .setLoadView(builder.loadView)
                .setHeight(builder.height)
                .setIndicatorView(builder.mIndicatorView)
                .setLoadLayout(builder.loadLayout)
                .setAbsWebUIController(absWebUIController)
                .build();
    }

    /** 飞船建造完毕进入 -- 准备阶段,检查所有引擎是否正常工作 */
    void ready() {
        // 加载 webView设置
        createSetting();

        // 加载 url加载器
        if (null == urlLoader) {
            urlLoader = new UrlLoader(webView);
        }

        // 加载 webViewClient
        createWebViewClient();

        //加载webChromeClient 系统设置的优先
        createWebChromeClient();

        // 加载js脚本注入
        createJsInterface();
    }

    private void createJsInterface() {
        if (null == mJsInterface) {
            mJsInterface = SafeJsInterface.getInstance(webView, modeType);
        }

        // 注入通用的js脚本
        mJavaObject.put(BRIDGECHECK, new CommonJavaObject(commonJSListener));

        if (mJavaObject != null && !mJavaObject.isEmpty()) {
            mJsInterface.addJavaObjects(mJavaObject);
        }
    }

    private void createSetting() {
        if (null == setting) {
            if (webViewType == WebViewType.Android) {
                setting = new DefaultWebSetting(context.get());
            } else {
                setting = new X5DefaultWebSetting(context.get());
            }
        }
        setting.setSetting(webView);
    }

    private void createWebChromeClient() {
        IndicatorController indicatorController = IndicatorHandler.getInstance().setIndicator(webViewManager.getIndicator());
        PrimChromeClient.createChromeBuilder()
                .setActivity(context.get())
                .setType(webViewType)
                .setWebView(webView)
                .setWebChromeClient(x5WebChromeClient)
                .setWebChromeClient(webChromeClient)
                .setWebChromeClient(agentWebChromeClient)
                .setAbsWebUIController(absWebUIController)
                .setAllowUploadFile(allowUploadFile)
                .setGeolocation(isGeolocation)
                .setIndicatorController(indicatorController)
                .setInvokingThird(invokingThird)
                .build();
    }

    private void createWebViewClient() {
        PrimWebClient.createClientBuilder()
                .setActivity(context.get())
                .setType(webViewType)
                .setWebView(webView)
                .setWebViewClient(x5WebViewClient)
                .setWebViewClient(webViewClient)
                .setWebViewClient(agentWebViewClient)
                .setAlwaysOpenOtherPage(alwaysOpenOtherPage)
                .setAbsWebUIController(absWebUIController)
                .build();
    }

    /** 准备完毕 发起最终阶段 加载url -------> 飞船发射 */
    PrimWeb launch(String url) {
        if (null == headers || headers.isEmpty()) {
            urlLoader.loadUrl(url);
        } else {
            urlLoader.loadUrl(url, headers);
        }
        return this;
    }

    public static class PrimBuilder {
        private IAgentWebView webView;
        private View mView;
        private WeakReference<Activity> context;
        private ViewGroup mViewGroup;
        private ViewGroup.LayoutParams mLayoutParams;
        private int mIndex;
        private IAgentWebSetting setting;
        private Map<String, String> headers;
        private IUrlLoader urlLoader;
        private ICallJsLoader callJsLoader;
        private ModeType modeType = ModeType.Normal;
        private HashMap<String, Object> mJavaObject;
        private AgentWebViewClient agentWebViewClient;
        private WebViewType webViewType = WebViewType.Android;
        private WebViewClient webViewClient;
        private com.tencent.smtt.sdk.WebViewClient x5WebViewClient;
        private WebChromeClient webChromeClient;
        private com.tencent.smtt.sdk.WebChromeClient x5WebChromeClient;
        private AgentChromeClient agentWebChromeClient;
        private CommonJSListener commonJSListener;
        private boolean alwaysOpenOtherPage;
        private AbsWebUIController absWebUIController;
        private boolean isGeolocation = true;
        private boolean allowUploadFile = true;
        private boolean invokingThird = false;
        //UI Controller
        private boolean needTopIndicator;
        private boolean customTopIndicator;
        private View errorView;
        private View loadView;
        private BaseIndicatorView mIndicatorView;
        private @ColorInt
        int mColor = -1;
        private String colorPaser;
        private int height = 0;
        private @LayoutRes
        int errorLayout = 0;
        private @IdRes
        int errorClickId = 0;
        private @LayoutRes
        int loadLayout = 0;

        PrimBuilder(Activity context) {
            this.context = new WeakReference<>(context);
        }

        /** 设置webview的父类 */
        public UIControllerBuilder setWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            return new UIControllerBuilder(this);
        }

        public UIControllerBuilder setWebParent(@NonNull ViewGroup v, @NonNull ViewGroup.LayoutParams lp, int index) {
            this.mViewGroup = v;
            this.mLayoutParams = lp;
            this.mIndex = index;
            return new UIControllerBuilder(this);
        }

        /** 所有设置完成 */
        public PerBuilder build() {
            if (mViewGroup == null) {
                throw new NullPointerException("ViewGroup not null,please check your code!");
            }
            return new PerBuilder(new PrimWeb(this));
        }

        private void addJavaObject(String key, Object o) {
            if (mJavaObject == null) {
                mJavaObject = new HashMap<>(30);//初始化个数 30 * 0.75 多个左右,考虑到哈希表 默认大小只有 4 * 0.75 个
                // 而哈希表的缺点是:扩容性能会下降 初始化时提前计算好上限.
            }
            mJavaObject.put(key, o);
        }

        private void setWebViewType(WebViewType webViewType) {
            this.webViewType = webViewType;
            if (null == this.webView) {
                if (webViewType == WebViewType.X5) {
                    this.webView = new X5AgentWebView(context.get());
                    this.mView = this.webView.getAgentWebView();
                } else {
                    this.webView = new AndroidAgentWebView(context.get());
                    this.mView = this.webView.getAgentWebView();
                }
            }
        }
    }

    /**
     * webview的UI控制器设置
     * 包括错误页面的设置和加载页面的设置
     */
    public static class UIControllerBuilder {
        private PrimBuilder primBuilder;

        public UIControllerBuilder(PrimBuilder primBuilder) {
            this.primBuilder = primBuilder;
        }

        public IndicatorBuilder useDefaultUI() {
            return new IndicatorBuilder(primBuilder);
        }

        public IndicatorBuilder useCustomUI(@LayoutRes int errorLayout, @LayoutRes int loadLayout, @IdRes int errorClickId) {
            this.primBuilder.errorLayout = errorLayout;
            this.primBuilder.loadLayout = loadLayout;
            this.primBuilder.errorClickId = errorClickId;
            return new IndicatorBuilder(primBuilder);
        }

        public IndicatorBuilder useCustomUI(@LayoutRes int errorLayout, @IdRes int errorClickId) {
            this.primBuilder.errorLayout = errorLayout;
            this.primBuilder.errorClickId = errorClickId;
            return new IndicatorBuilder(primBuilder);
        }

        public IndicatorBuilder useCustomUI(@LayoutRes int errorLayout) {
            this.primBuilder.errorLayout = errorLayout;
            return new IndicatorBuilder(primBuilder);
        }

        public IndicatorBuilder useCustomUI(@NonNull View errorView, @NonNull View loadView) {
            this.primBuilder.errorView = errorView;
            this.primBuilder.loadView = loadView;
            return new IndicatorBuilder(primBuilder);
        }

        public IndicatorBuilder useCustomUI(@NonNull View errorView) {
            this.primBuilder.errorView = errorView;
            return new IndicatorBuilder(primBuilder);
        }
    }

    /**
     * webView 指示器设置
     */
    public static class IndicatorBuilder {
        private PrimBuilder primBuilder;

        public IndicatorBuilder(PrimBuilder primBuilder) {
            this.primBuilder = primBuilder;
        }

        public CommonBuilder useDefaultTopIndicator() {
            this.primBuilder.needTopIndicator = true;
            return new CommonBuilder(primBuilder);
        }

        public CommonBuilder useDefaultTopIndicator(@ColorInt int color) {
            this.primBuilder.needTopIndicator = true;
            this.primBuilder.mColor = color;
            return new CommonBuilder(primBuilder);
        }

        public CommonBuilder useDefaultTopIndicator(@ColorInt int color, int height) {
            this.primBuilder.needTopIndicator = true;
            this.primBuilder.mColor = color;
            this.primBuilder.height = height;
            return new CommonBuilder(primBuilder);
        }

        public CommonBuilder useDefaultTopIndicator(@NonNull String color) {
            this.primBuilder.needTopIndicator = true;
            this.primBuilder.colorPaser = color;
            return new CommonBuilder(primBuilder);
        }

        public CommonBuilder useDefaultTopIndicator(@NonNull String color, int height) {
            this.primBuilder.height = height;
            this.primBuilder.needTopIndicator = true;
            this.primBuilder.colorPaser = color;
            return new CommonBuilder(primBuilder);
        }

        /**
         * 使用自定义的进度指示器 注意需要继承{@link BaseIndicatorView}
         *
         * @param indicatorView
         *         自定义的指示器view
         *
         * @return CommonBuilder
         */
        public CommonBuilder useCustomTopIndicator(@NonNull BaseIndicatorView indicatorView) {
            this.primBuilder.mIndicatorView = indicatorView;
            this.primBuilder.needTopIndicator = true;
            this.primBuilder.customTopIndicator = true;
            return new CommonBuilder(primBuilder);
        }

        /**
         * 关闭进度指示器
         *
         * @return CommonBuilder
         */
        public CommonBuilder colseTopIndicator() {
            this.primBuilder.needTopIndicator = false;
            this.primBuilder.customTopIndicator = false;
            this.primBuilder.height = 0;
            return new CommonBuilder(primBuilder);
        }
    }

    /**
     * webView的通用设置
     */
    public static class CommonBuilder {
        private PrimBuilder primBuilder;

        public CommonBuilder(PrimBuilder primBuilder) {
            this.primBuilder = primBuilder;
        }

        /** 设置代理的webview 若不设置使用默认的 */
        public CommonBuilder setAgentWebView(IAgentWebView webView) {
            primBuilder.webView = webView;
            primBuilder.mView = webView.getAgentWebView();
            if (primBuilder.mView instanceof WebView) {
                primBuilder.setWebViewType(WebViewType.X5);
            } else {
                primBuilder.setWebViewType(WebViewType.Android);
            }
            return this;
        }

        /** web的代理设置 */
        public CommonBuilder setAgentWebSetting(IAgentWebSetting agentWebSetting) {
            primBuilder.setting = agentWebSetting;
            return this;
        }

        /** 设置自定义的url加载器 */
        public CommonBuilder setUrlLoader(IUrlLoader urlLoader) {
            primBuilder.urlLoader = urlLoader;
            return this;
        }

        /** 设置自定义js 方法加载器 */
        public CommonBuilder setCallJsLoader(ICallJsLoader callJsLoader) {
            primBuilder.callJsLoader = callJsLoader;
            return this;
        }

        /** 设置模式 js脚本的注入模式 */
        public CommonBuilder setModeType(ModeType modeType) {
            primBuilder.modeType = modeType;
            return this;
        }

        /** 设置WebView的类型 如果设置了setAgentWebView 此方法最好不要调用setAgentWebView 会默认判断webview的类型 */
        public CommonBuilder setWebViewType(WebViewType webViewType) {
            primBuilder.setWebViewType(webViewType);
            return this;
        }

        /** 设置代理的WebViewClient 兼容android webview 和 x5 webview */
        public CommonBuilder setWebViewClient(AgentWebViewClient agentWebViewClient) {
            primBuilder.agentWebViewClient = agentWebViewClient;
            return this;
        }

        public CommonBuilder setWebViewClient(WebViewClient webViewClient) {
            primBuilder.webViewClient = webViewClient;
            return this;
        }

        public CommonBuilder setWebViewClient(com.tencent.smtt.sdk.WebViewClient webViewClient) {
            primBuilder.x5WebViewClient = webViewClient;
            return this;
        }

        /** 设置代理的WebChromeClient 兼容android webview 和 x5 webview */
        public CommonBuilder setWebChromeClient(AgentChromeClient agentWebChromeClient) {
            primBuilder.agentWebChromeClient = agentWebChromeClient;
            return this;
        }

        public CommonBuilder setWebChromeClient(WebChromeClient webChromeClient) {
            primBuilder.webChromeClient = webChromeClient;
            return this;
        }

        public CommonBuilder setWebChromeClient(com.tencent.smtt.sdk.WebChromeClient webChromeClient) {
            primBuilder.x5WebChromeClient = webChromeClient;
            return this;
        }

        /** 检查js方法是否存在 */
        public CommonBuilder setListenerCheckJsFunction(CommonJSListener commonJSListener) {
            primBuilder.commonJSListener = commonJSListener;
            return this;
        }

        /** 注入js脚本 */
        public CommonBuilder addJavascriptInterface(@NonNull String name, @NonNull Object o) {
            primBuilder.addJavaObject(name, o);
            return this;
        }

        /** 是否允许打开其他应用 */
        public CommonBuilder alwaysOpenOtherPage(boolean alwaysOpenOtherPage) {
            primBuilder.alwaysOpenOtherPage = alwaysOpenOtherPage;
            return this;
        }

        /** 设置自定义的UI控制器 */
        public CommonBuilder setWebUIController(AbsWebUIController absWebUIController) {
            primBuilder.absWebUIController = absWebUIController;
            return this;
        }

        /** 设置是否允许上传文件 默认为允许 */
        public CommonBuilder setAllowUploadFile(boolean flag) {
            primBuilder.allowUploadFile = flag;
            return this;
        }

        /** 设置是否允许定位 默认为允许 */
        public CommonBuilder setGeolocation(boolean flag) {
            primBuilder.isGeolocation = flag;
            return this;
        }

        /** 上传文件 false 调用系统文件  true 调用自定义的文件库 */
        public CommonBuilder setUpdateInvokThrid(boolean flag) {
            primBuilder.invokingThird = flag;
            return this;
        }

        /** 设置完成开始建造 */
        public PerBuilder buildWeb() {
            return primBuilder.build();
        }
    }

//    public static class JsInterfaceBuilder {
//        private PrimBuilder primBuilder;
//
//        public JsInterfaceBuilder(PrimBuilder primBuilder) {
//            this.primBuilder = primBuilder;
//        }
//
//        /** 注入js脚本 */
//        public JsInterfaceBuilder addJavascriptInterface(@NonNull String name, @NonNull Object o) {
//            primBuilder.addJavaObject(name, o);
//            return this;
//        }
//
//        /** 设置完成开始建造 */
//        public PerBuilder buildWeb() {
//            return primBuilder.build();
//        }
//
//    }

    /** 设置完成准备发射 */
    public static class PerBuilder {
        private PrimWeb primWeb;
        private boolean isReady = false;

        public PerBuilder(PrimWeb primWeb) {
            this.primWeb = primWeb;
        }

        public PerBuilder lastGo() {
            if (!isReady) {
                primWeb.ready();
                isReady = true;
            }
            return this;
        }

        public PrimWeb launch(@NonNull String url) {
            if (!isReady) {
                lastGo();
            }
            return primWeb.launch(url);
        }
    }

    public WebViewType getWebViewType() {
        return webViewType;
    }

    /** 获取webview的父view */
    public FrameLayout getRootView() {
        if (null != webViewManager) {
            return webViewManager.getWebParentView();
        }
        return null;
    }

    /** 获取调用js方法 */
    public ICallJsLoader getCallJsLoader() {
        checkWebView();
        if (callJsLoader == null) {
            callJsLoader = SafeCallJsLoaderImpl.getInstance(webView);
        }
        return callJsLoader;
    }

    /** 获取注入js脚本方法 */
    public IJsInterface getJsInterface() {
        checkWebView();
        if (mJsInterface == null) {
            mJsInterface = SafeJsInterface.getInstance(webView, modeType);
        }
        return mJsInterface;
    }

    /** 获取websettings， Object具体的是android webSetting 还是x5 webSetting 自己判断强转 */
    public Object getWebSettings() {
        if (null == setting) {
            if (webViewType == WebViewType.Android) {
                setting = new DefaultWebSetting(context.get());
            } else {
                setting = new X5DefaultWebSetting(context.get());
            }
        }
        return setting.getWebSetting();
    }

    /** 长按图片等会用到 类型自己转换 */
    public Object getHitTestResult() {
        checkWebView();
        return webView.getAgentHitTestResult();
    }

    /** 获取url加载器 加载URL和刷新url操作 */
    public IUrlLoader getUrlLoader() {
        checkWebView();
        if (null == urlLoader) {
            urlLoader = new UrlLoader(webView);
        }
        return urlLoader;
    }

    /** 设置webview的生命周期 */
    public IWebLifeCycle webLifeCycle() {
        if (webLifeCycle == null) {
            if (webView != null) {
                webLifeCycle = new WebLifeCycle(webView);
            }
        }
        return webLifeCycle;
    }

    /** 获取webview */
    public IAgentWebView getWebView() {
        checkWebView();
        return webView;
    }

    /** 获取真实的webview 类型可以自己强转 */
    public View getRealWebView() {
        checkWebView();
        return webView.getAgentWebView();
    }

    /**
     * Check for the presence of a JS method
     *
     * @param checkJsFunction
     *         CommonJSListener
     */
    public void setListenerCheckJsFunction(CommonJSListener checkJsFunction) {
        if (null == commonJSListener) {
            this.commonJSListener = checkJsFunction;
        }
    }

    /**
     * handler back button
     *
     * @return true handler ;false no handler
     */
    public boolean handlerBack() {
        checkWebView();
        if (keyEvent == null) {
            keyEvent = KeyEventHandler.getInstance(webView, keyEventInterceptor);
        }
        return keyEvent.back();
    }

    /**
     * handler onKeyDown
     *
     * @param keyCode
     *         keyCode
     * @param event
     *         KeyEvent
     *
     * @return true handler;false no handler
     */
    public boolean handlerKeyEvent(int keyCode, KeyEvent event) {
        checkWebView();
        if (keyEvent == null) {
            keyEvent = KeyEventHandler.getInstance(webView, keyEventInterceptor);
        }
        return keyEvent.onKeyDown(keyCode, event);
    }

    public String getUrl() {
        checkWebView();
        return webView.getAgentUrl();
    }

    public void copyUrl() {
        ClipboardManager mClipboardManager = (ClipboardManager) context.get().getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, getUrl()));
    }

    public void openBrowser(String targetUrl) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            Toast.makeText(context.get(), targetUrl + "无效的链接无法使用浏览器打开", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri mUri = Uri.parse(targetUrl);
        intent.setData(mUri);
        context.get().startActivity(intent);
    }

    public void clearWebViewCache() {

    }

    public void setJsUploadChooserCallback(FileValueCallbackMiddleActivity.JsUploadChooserCallback jsUploadChooserCallback) {
        FileValueCallbackMiddleActivity.setJsUploadChooserCallback(jsUploadChooserCallback);
    }

    public static void removeJsUploadChooserCallback() {
        FileValueCallbackMiddleActivity.removeJsUploadChooserCallback();
    }

    /**
     * 用第三方库选择文件,回调逻辑需自己处理
     *
     * @param thriedChooserListener
     *         ThriedChooserListener
     */
    public void setThriedChooserListener(FileValueCallbackMiddleActivity.ThriedChooserListener thriedChooserListener) {
        FileValueCallbackMiddleActivity.setThriedChooserListener(thriedChooserListener);
    }

    public static void removeThriedChooserListener() {
        FileValueCallbackMiddleActivity.removeThriedChooserListener();
    }

    /**
     * check webView not null
     */
    private void checkWebView() {
        if (null == webView) {
            throw new NullPointerException("webView most not be null,please check your code!");
        }
    }
}
