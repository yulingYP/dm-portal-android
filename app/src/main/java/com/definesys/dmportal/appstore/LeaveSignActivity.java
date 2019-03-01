package com.definesys.dmportal.appstore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;

import com.definesys.dmportal.appstore.customViews.BottomDialog;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.appstore.utils.ImageUntil;
import com.definesys.dmportal.appstore.utils.PermissionsUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.ChangeUserImagePresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.definesys.dmportal.appstore.utils.Constants.SIGN_CODE;


@Route(path = ARouterConstants.LeaveSignActivity)
public class LeaveSignActivity extends BaseActivity<ChangeUserImagePresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.no_sign_des)
    TextView tv_noSign;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.edit_sign)
    ImageView iv_edit;

    @BindView(R.id.select_des_text)
    TextView tv_des;

    @BindView(R.id.sign_img)
    ImageView iv_sign;

    @BindView(R.id.type_layout)
    LinearLayout lg_type;
    @BindView(R.id.show_img)
    ImageView iv_show;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    getTypeList(msg.obj.toString());
                    break;
            }
        }
    };
    private int selectPosition;//选择的样式的位置
    private ImageView iv_selected;//选择位置的imageview控件
    private TextView tv_selected;//选择位置的文本控件

    private RequestOptions option = new RequestOptions()
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);
    private Dialog dialog;//编辑艺术字提示框
    private EditText ed_sign;//签名输入框
    private List<String> typeList;//艺术字种类列表
    private BottomDialog bottomDialog;//签名生成模式选择提示框
    private int updateMode;//更新签名的模式  0.拍照 1.手写 2.艺术字 3.从相册选择
    private String pathName;//新签名的本地路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_sign);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.my_sign));
        titleBar.setBackgroundDividerEnabled(false);

        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    finish();
                });
        //编辑
        RxView.clicks(iv_edit)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    List<String> list = new ArrayList<>();
                    list.add(getString(R.string.sign_take_photo));
                    list.add(getString(R.string.sign_write));
                    list.add(getString(R.string.sign_design));
                    list.add(getString(R.string.sign_pictures));

                    showBottomDialog(list);
                    //showMyDialog();
                });
        //点击签名 展示签名
        RxView.clicks(iv_sign)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Bitmap image = ((BitmapDrawable)iv_sign.getDrawable()).getBitmap();
                    String path=ImageUntil.saveBitmapFromView(image,UUID.randomUUID().toString(),this,4);
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(path);
                    localMedia.setPosition(0);
                    List<LocalMedia> localMedias = new ArrayList<>();
                    localMedias.add(localMedia);
                    PictureSelector.create(this).openGallery(PictureMimeType.ofImage())
                            .openExternalPreview(0, localMedias);
                });
        //点击新建签名 展示新建签名
        RxView.clicks(iv_show)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Bitmap image = ((BitmapDrawable)iv_show.getDrawable()).getBitmap();
                    String path=ImageUntil.saveBitmapFromView(image,UUID.randomUUID().toString(),this,4);
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(path);
                    localMedia.setPosition(0);
                    List<LocalMedia> localMedias = new ArrayList<>();
                    localMedias.add(localMedia);
                    PictureSelector.create(this).openGallery(PictureMimeType.ofImage())
                            .openExternalPreview(0, localMedias);
//                    iv_show.setImageBitmap(null);
                    iv_show.setImageBitmap(BitmapFactory.decodeFile(path));

                });
        setSign();//设置签名

    }

    //添加提交按钮
    private void addSubmitButtom(){
        titleBar.removeAllRightViews();
        Button button = titleBar.addRightTextButton(getString(R.string.submit), R.layout.activity_leave_sign);
        button.setTextSize(14);
        //提交
        RxView.clicks(button)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> {
                    checkSelect();
                });
    }
    private void showBottomDialog(List<String> list) {
        if(dialog==null) {
            bottomDialog = new BottomDialog(this, list);
            bottomDialog.setOnOptionClickListener(position -> {
                if (position == 0) {//拍照
                    PictureSelector.create(this)
                            .openCamera(PictureMimeType.ofImage())
                            .maxSelectNum(1)
                            .enableCrop(true)
                            .rotateEnabled(false)
                            .compress(true)
                            .withAspectRatio(36, 17)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else if (position == 1) {//手写
                    ARouter.getInstance().build(ARouterConstants.SignatureActivity).navigation(this, SIGN_CODE);
                } else if (position == 2) {//艺术字
                    showMyDialog();
                } else if (position == 3) {//从相册查看
                    PictureSelector.create(this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1).enableCrop(true).compress(true)
                            .rotateEnabled(false)
                            .withAspectRatio(36, 17)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }
                if(position!=2)
                    addSubmitButtom();
                updateMode = position;
                bottomDialog.dismiss();
            });
        }

        bottomDialog.setOnCancelButtonClickListener(view -> bottomDialog.dismiss()).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST://拍照或从相册选择
                    if(!PermissionsUtil.isNetworkConnected(this)){//网络检测
                        Toast.makeText(this, R.string.no_net_tip_2,Toast.LENGTH_SHORT).show();
                        //updateImg(path);
                        return;
                    }
                    // 图片选择结果回调
                    List<LocalMedia> selectImages;
                    selectImages = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种 path
                    // 1.media.getPath(); 为原图 path
                    // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
                    // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    lg_type.removeAllViews();
                    if(tv_des.getVisibility()==View.GONE)
                        tv_des.setVisibility(View.VISIBLE);
                    tv_des.setText(R.string.pre_show);
                    iv_show.setVisibility(View.VISIBLE);
                    //设置图片
                    iv_show.setImageBitmap(BitmapFactory.decodeFile(selectImages.get(0).getCompressPath()));
//                    handler.sendEmptyMessageDelayed(1,Constants.scrollDelay);
                    break;
                case SIGN_CODE://手写签名
                    lg_type.removeAllViews();
                    if(tv_des.getVisibility()==View.GONE)
                        tv_des.setVisibility(View.VISIBLE);
                    tv_des.setText(R.string.pre_show);
                    iv_show.setVisibility(View.VISIBLE);
                    iv_show.setImageBitmap(BitmapFactory.decodeFile(data.getStringExtra("path")));
                    break;

            }
        }
    }
    //签名选择合法性检测
    private void checkSelect() {
        if(updateMode==2&&(tv_selected==null||typeList==null||typeList.size()==0)){
            Toast.makeText(this, R.string.sign_select_error,Toast.LENGTH_SHORT).show();
            return;
        }
        progressHUD.show();
        Bitmap bitmap;
        if(updateMode!=2){//拍照、相册选择、手写
            bitmap =ImageUntil.convertViewToBitmap(iv_show);
//            pathName = ImageUntil.saveBitmapFromView(bitmap, String.valueOf(SharedPreferencesUtil.getInstance().getUserId().intValue()), LeaveSignActivity.this, 4);
//            iv_show.setImageBitmap(BitmapFactory.decodeFile(pathName));
        }
        else  {//艺术字
            //上传签名图片
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.item_sign_text_view, null);
            tv.setText(tv_selected.getText().toString());
            if(selectPosition>0)
                tv.setTypeface(Typeface.createFromAsset(getAssets(), "ttf/"+typeList.get(selectPosition-1)));
            tv.setDrawingCacheEnabled(true);
            tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
            bitmap = Bitmap.createBitmap(tv.getDrawingCache());
            //千万别忘最后一步
            tv.destroyDrawingCache();
//            pathName = ImageUntil.saveBitmapFromView(bitmap, String.valueOf(SharedPreferencesUtil.getInstance().getUserId().intValue()), LeaveSignActivity.this, 4);
        }
        pathName = ImageUntil.saveBitmapFromView(bitmap,UUID.randomUUID().toString(), LeaveSignActivity.this, 4);
        File file = new File(pathName);

        mPersenter.uploadUserImage(String.valueOf(SharedPreferencesUtil.getInstance().getUserId()), file,"1");

    }
    /*
         修改头像成功
         */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_UPLOAD_USER_IMAGE)
    }, thread = EventThread.MAIN_THREAD)
    public void successfulUploadUserImage(String newUrl) {
        Toast.makeText(this, R.string.sign_update_success, Toast.LENGTH_SHORT).show();
        //更新本地签名信息
        SharedPreferencesUtil.getInstance().setUserSign(newUrl);
        PictureFileUtils.deleteCacheDirFile(this);
        progressHUD.dismiss();
        finish();
    }
    /**
     * 上传失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this,("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }


    /**
     * 获取签名种类并生成相应text
     * @param content 签名
     */
    private void getTypeList(String content) {
        if(tv_des.getVisibility()==View.GONE) {
            tv_des.setVisibility(View.VISIBLE);
        }
        if(lg_type.getChildCount()!=0)
            lg_type.removeAllViews();
        selectPosition = 0;
        Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> emitter) throws Exception {
                if(typeList ==null||typeList.size()==0) {//获取艺术字种类列表
                    AssetManager assetManager = getAssets();
                    String[] typeNames = null;
                    try {
                        typeNames = assetManager.list("ttf");
                        typeList = new ArrayList<>();
                        String temp;
                        for (int i = typeNames.length - 1; i >= 0; i--) {
                            temp = typeNames[i];
                            //检测文件类型是否正确
                            if (temp != null && temp.length() > 4 && ".ttf".equals(temp.substring(temp.length() - 4, temp.length()).toLowerCase()))
                                typeList.add(typeNames[i]);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        emitter.onError(e);
                    }
                }
                //添加系统文字类型
                emitter.onNext(addItemView(0,content,""));
                for(int i = 0 ; i <typeList.size();i++){
                    //添加艺术字文字类型
                    emitter.onNext(addItemView(i+1,content,typeList.get(i)));
                }
                emitter.onComplete();
            }
        }).observeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<View>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(View view) {
                lg_type.addView(view);
            }

            @Override
            public void onError(Throwable e) {
                progressHUD.dismiss();
                Toast.makeText(LeaveSignActivity.this, R.string.edit_error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                progressHUD.dismiss();
                addSubmitButtom();
                tv_des.setText(R.string.edit_select_des);
                iv_show.setVisibility(View.GONE);
                lg_type.setVisibility(View.VISIBLE);
            }
        });
    }

    //显示签名
    private void showMyDialog() {
        if(dialog==null){
            dialog = new Dialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.view_edit_sign,null);
            //编辑框
            EditText editText = (EditText)view.findViewById(R.id.edit_text);
            ed_sign = editText;
            //取消
            RxView.clicks(view.findViewById(R.id.cancel_text))
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        dialog.dismiss();
                    });
            //生成
            RxView.clicks(view.findViewById(R.id.yes_text))
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        checkEdit();//输入合法性检查
                    });
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(view);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    editText.setText("");
                }
            });
            dialog.show();

        }else {
            dialog.show();
        }
        //设置编辑框内容----用户姓名
        ed_sign.setText(SharedPreferencesUtil.getInstance().getUserName());
    }

    //检查输入框内容是否合法
    private void checkEdit() {
        String content = ed_sign!=null?ed_sign.getText().toString():"";
        if("".equals(content)){
            Toast.makeText(this, R.string.edit_fail_des_1,Toast.LENGTH_SHORT).show();
        }else {
            progressHUD.show();
            Message message = new Message();
            message.what=0;
            message.obj=content;
            lg_type.setVisibility(View.GONE);
            handler.sendMessageDelayed(message,Constants.scrollDelay);
            //getTypeList(content);
        }
        if(dialog!=null)
            dialog.dismiss();
    }

    /**
     * 添加单条样式
     * @param positon 点击位置
     * @param content 艺术字内容
     * @param type 艺术字类型
     * @return
     */
    private View addItemView(int positon,String content,String type) {
        View view= LayoutInflater.from(this).inflate(R.layout.item_sign_type_view,null);
        TextView textView=(TextView) view.findViewById(R.id.name_text);//签名
        ImageView imageView=(ImageView) view.findViewById(R.id.select_img);//是否选择
        if(positon==selectPosition) {//选中
            iv_selected = imageView;
            tv_selected = textView;
            imageView.setImageResource(R.drawable.right_icon);
        }else
            imageView.setImageResource(R.drawable.no_select);
        if(positon!=0)
            textView.setTypeface(Typeface.createFromAsset(getAssets(), "ttf/"+type));
        textView.setText(content);
        RxView.clicks(view)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if(iv_selected!=null)
                        iv_selected.setImageResource(R.drawable.no_select);
                    selectPosition = positon;
                    iv_selected = imageView;
                    tv_selected = textView;
                    imageView.setImageResource(R.drawable.right_icon);
                });
        return view;
    }

    /**
     * 设置签名
    */
    private void setSign() {
        Glide.with(this).asBitmap().load(getString(R.string.get_image,String.valueOf(SharedPreferencesUtil.getInstance().getUserId().intValue())+".png",2)).apply(option).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                iv_sign.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                tv_noSign.setText(R.string.no_sign_des);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                progressBar.setVisibility(View.GONE);
                iv_sign.setImageBitmap(resource);
            }
        });
    }
    @Override
    public ChangeUserImagePresenter getPersenter() {
        return new ChangeUserImagePresenter(this);
    }
}
