package com.definesys.dmportal.main.userSettingActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.LeaveActivity;
import com.definesys.dmportal.appstore.adapter.ReasonImageAdapter;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.bean.Feedback;
import com.definesys.dmportal.main.interfaces.OnItemClickListener;
import com.definesys.dmportal.main.presenter.FeedBackPresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ARouterConstants.FeedBackActivity)
public class FeedBackActivity extends BaseActivity<FeedBackPresenter> {
    @BindView(R.id.title_bar_att_feedback)
    CustomTitleBar titleBar;
    @BindView(R.id.et_att_feedback)
    EditText content;
    @BindView(R.id.count_word_att_feedback)
    TextView wordCount;
    @BindView(R.id.recycle_view_att_feedback)
    RecyclerView recyclerView;
    @BindView(R.id.count_img_att_feedback)
    TextView imgCount;
    private ReasonImageAdapter fedbkImgAdapter;
    private List<LocalMedia> selectImages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        selectImages = new ArrayList<>();
        //标题栏的设置一套
        titleBar.setTitle(getString(R.string.feedback));
        titleBar.setBackgroundDividerEnabled(false);

        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> finish());

        //提交
        RxView.clicks(titleBar.addRightTextButton(R.string.submit,R.id.topbar_right_button))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if(imm!=null)
                        imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
                    String content = this.content.getText().toString();
                    // 文字不为空
                    if ("".equals(content)) {
                        Toast.makeText(FeedBackActivity.this, R.string.feedback_word_hint, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    progressHUD.show();
                    mPersenter.getRequestResult(new Feedback(String.valueOf(System.currentTimeMillis()), SharedPreferencesUtil.getInstance().getUserId().intValue(),content), selectImages);
                });

        //设置输入指示器
        wordCount.setText(getString(R.string.word_count, 0));
        imgCount.setText(getString(R.string.img_count, 0));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));

        fedbkImgAdapter = new ReasonImageAdapter(this, selectImages);
        recyclerView.setAdapter(fedbkImgAdapter);


        fedbkImgAdapter.setOnClickListener(new ReasonImageAdapter.OnClickListener() {
            //点击背景
            @Override
            public void onBackgroundClick(int position) {
                if(imm!=null)
                    imm.hideSoftInputFromWindow(content.getWindowToken(), 0);

                if(position == 0){
                    //打开相册·拍摄照片
                    PictureSelector.create(FeedBackActivity.this)
                            .openGallery(PictureMimeType.ofImage()).maxSelectNum(3).compress(true)
                            .selectionMedia(selectImages)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }else {
                    //放大已拍摄图片
                    PictureSelector.create(FeedBackActivity.this).
                            externalPicturePreview(position-1, selectImages);
                }
            }
            //点击小红叉删除
            @Override
            public void onForegroundClick(int position) {
                // 删除选中的图片
                selectImages.remove(position-1);
                fedbkImgAdapter.notifyDataSetChanged();
                imgCount.setText(getString(R.string.img_count, selectImages.size()));
            }
        });
        /*
        监听输入框内容 《==》 获取输入长度显示到界面
         */
        RxTextView.textChanges(content)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence ->
                        wordCount.setText(getString(R.string.word_count, content.length())));
    }

    /**
     * 申请失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }
    /**
     * 提交反馈意见成功
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_LEAVE_REQUEST)
    }, thread = EventThread.MAIN_THREAD)
    public void getLeaveRequest(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this, R.string.submit_success,Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
            PictureFileUtils.deleteCacheDirFile(this);
            finish();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectImages.clear();
                    // 图片选择结果回调
                    selectImages.addAll(PictureSelector.obtainMultipleResult(data));
                    // 例如 LocalMedia 里面返回三种 path
                    // 1.media.getPath(); 为原图 path
                    // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
                    // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    // 更新图片数量
                    imgCount.setText(getString(R.string.img_count, selectImages.size()));
                    // 更新显示图片
                    fedbkImgAdapter.setImages(selectImages);
                    fedbkImgAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
    @Override
    public FeedBackPresenter getPersenter() {
        return new FeedBackPresenter(this);
    }
}
