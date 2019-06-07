package com.example.dmportal.main.userSettingActivity;

import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.base.BaseActivity;
import com.example.dmportal.R;
import com.example.dmportal.appstore.utils.ARouterConstants;
import com.example.dmportal.commontitlebar.CustomTitleBar;
import com.example.dmportal.main.presenter.NewsPresenter;
import com.example.dmportal.main.presenter.ViseUpdateHttpManager;
import com.makeramen.roundedimageview.RoundedImageView;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.vector.update_app.utils.AppUpdateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.AboutActivity)
public class AboutActivity extends BaseActivity<NewsPresenter> {

    @BindView(R.id.head_att_about)
    RoundedImageView head;
    @BindView(R.id.version_att_about)
    TextView version;
    @BindView(R.id.check_update_att_about)
    View check_update;
    @BindView(R.id.user_protocol)
    View use_protocol;
    @BindView(R.id.title_bar_att_about)
    CustomTitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initView();
    }



    private void initView() {
        titleBar.setTitle(getResources().getString(R.string.about_our));
        titleBar.addLeftBackImageButton().setOnClickListener((view) -> finish());
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        ((TextView) check_update.findViewById(R.id.title_item_itr)).setText(getResources().getString(R.string.about_check));
        ((ImageView) check_update.findViewById(R.id.img_item_itr)).setImageResource(R.mipmap.ic_check_update);

        ((TextView) use_protocol.findViewById(R.id.title_item_itr)).setText(getResources().getString(R.string.use_protocol));
        ((ImageView) use_protocol.findViewById(R.id.img_item_itr)).setImageResource(R.mipmap.user_protocol);
        check_update.setOnClickListener(view ->
            updateDiy()
        );

        //用户协议
        use_protocol.setOnClickListener(view -> ARouter.getInstance()
                .build(ARouterConstants.WebViewActivity)
                .withString("url", getString(R.string.us_protocol_url)).navigation());
        //设置版本信息
        String appVersionName;
        try {
            appVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            appVersionName = "unknown";
        }
        version.setText(appVersionName);
    }


    /**
     * 自定义接口协议  检查更新
     *
     */
    public void updateDiy() {

        String mUpdateUrl = "http://wcpublic.smec-cn.com:7777/simp/simp/TEISService/updateVersion";

        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(this)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new ViseUpdateHttpManager())
                //必须设置，更新地址
                .setUpdateUrl(mUpdateUrl)
                //全局异常捕获
                .handleException(Throwable::printStackTrace)
                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(true)
                //不显示通知栏进度条
//                .dismissNotificationProgress()
                //是否忽略版本
//                .showIgnoreVersion()
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
//                .setParams(params)
                //设置点击升级后，消失对话框，默认点击升级后，对话框显示下载进度，如果是强制更新，则设置无效
//               .hideDialogOnDownloading()
                //设置头部，不设置显示默认的图片，设置图片后自动识别主色调，然后为按钮，进度条设置颜色
                .setTopPic(R.mipmap.app_icon)
                //为按钮，进度条设置颜色。
                .setThemeColor(0xffffac5d)
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
//                .setTargetPath(path)
//  设置appKey，默认从AndroidManifest.xml获取，如果，使用自定义参数，则此项无效
//                .setAppKey("ab55ce55Ac4bcP408cPb8c1Aaeac179c5f6f")
                .setUpdateDialogFragmentListener(updateApp -> {
                    //用户点击关闭按钮，取消了更新，如果是下载完，用户取消了安装，则可以在 onActivityResult 监听到。

                })
                //不自动，获取
//                .setIgnoreDefParams(true)
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            //获取当前版本号
                            String version =  AppUpdateUtils.getVersionName(AboutActivity.this);
                            //更新逻辑版本判断，填YES or No,还未编写

                            final JSONObject data = jsonObject.getJSONObject("data");
                            final String newVersion = data.optString("versionNumber");

                            String status ;
                            if(ComparisonString(newVersion,version)){
                                status = "Yes";
                            }else {
                                status = "No";
                            }

                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(status)
                                    //（必须）新版本号，
                                    .setNewVersion(newVersion)
                                    //（必须）下载地址
                                    .setApkFileUrl(data.optString("appDownloadUrl"))
                                    //标题
                                    .setUpdateDefDialogTitle(String.format("快捷校园 是否升级到%s版本？", newVersion))
                                    //（必须）更新内容
                                    .setUpdateLog(data.optString("versionDetail"))
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize("5M")
                                    //是否强制更新，可以不设置
                                    .setConstraint(false);
                            //设置md5，可以不设置
                            //  .setNewMd5(jsonObject.optString("newmd5"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return updateAppBean;
                    }

                    @Override
                    protected void hasNewApp(UpdateAppBean updateApp, UpdateAppManager updateAppManager) {
                        updateAppManager.showDialogFragment();
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                        //                      CProgressDialogUtils.showProgressDialog(AboutActivity.this);
                        progressHUD.show();
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                        //                   CProgressDialogUtils.cancelProgressDialog(AboutActivity.this);
                        progressHUD.dismiss();
                    }

                    /**
                     * 没有新版本
                     */
                    @Override
                    public void noNewApp(String error) {
                        Toast.makeText(AboutActivity.this, "您的应用已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private boolean ComparisonString(String newVersion,String version){

        if((int)newVersion.charAt(0) > (int)version.charAt(0)) {
            return true;
        }
        else
        if((int)newVersion.charAt(0) < (int)version.charAt(0)){
            return  false;
        }

        if((int)newVersion.charAt(2) > (int)version.charAt(2)) {
            return true;
        }
        else
        if((int)newVersion.charAt(2) < (int)version.charAt(2)){
            return  false;
        }

        if((int)newVersion.charAt(4) > (int)version.charAt(4)) {
            return true;
        }
        else
        if((int)newVersion.charAt(4) < (int)version.charAt(4)){
            return  false;
        }
        return false;
    }

    @Override
    public NewsPresenter getPersenter() {
        return new NewsPresenter(this);
    }
}
