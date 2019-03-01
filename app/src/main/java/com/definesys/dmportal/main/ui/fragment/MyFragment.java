package com.definesys.dmportal.main.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.BottomDialog;
import com.definesys.dmportal.appstore.customViews.RCImageView;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.ImageUntil;
import com.definesys.dmportal.appstore.utils.PermissionsUtil;
import com.definesys.dmportal.main.presenter.ChangeUserImagePresenter;
import com.definesys.dmportal.main.presenter.LogoutPresenter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.vise.xsnow.permission.Permission;
import com.vise.xsnow.permission.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFragment} interface
 * to handle interaction events.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Unbinder unbinder;

    @BindView(R.id.head_item_uc)
    RCImageView userImage;
    @BindView(R.id.name_item_uc)
    TextView userName;
    @BindView(R.id.hello_item_uc)
    TextView welcomeText;

    @BindView(R.id.logout_layout)
    LinearLayout lg_logout;

    @BindView(R.id.feedback_layout)
    LinearLayout lg_fed;

//    @BindView(R.id.bind_phone_layout)
//    LinearLayout lg_phone;


    @BindView(R.id.setting_layout)
    LinearLayout lg_setting;

    // 用户已选择的图片
    private List<LocalMedia> selectImages;
    private String pathName = "";

    RequestOptions option = new RequestOptions()
            .centerCrop()
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.my);

    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_my, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SmecRxBus.get().register(this);
        initView();
        requestPermissions();
        refreshUserImage();
        refreshUserInformation();
    }


    //初始化界面
    private void initView() {
       updateShowInfo();
//        top.setOnClickListener((view) ->
//                ARouter.getInstance().build("/dmportal/usercenter/UserInformationActivity").navigation());

        RxView.clicks(userImage).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    List<String> photoStyles = new ArrayList<>();
                    photoStyles.add(getString(R.string.ui_photo));
                    photoStyles.add(getString(R.string.ui_galary));
                    photoStyles.add(getString(R.string.ui_check_head));
                    show(photoStyles, 2);
                });

//        //手机绑定
//        RxView.clicks(lg_phone)
//                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(o ->
//                        ARouter.getInstance()
//                                .build(ARouterConstants.PhoneBindActivity)
//                                .withBoolean("isBind",!"".equals(SharedPreferencesUtil.getInstance().getUserPhone()))
//                                .navigation()
//                );
//
        //反馈建议
        RxView.clicks(lg_fed)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                       ARouter.getInstance().build(ARouterConstants.FeedBackActivity).navigation()
                );
        //设置
        RxView.clicks(lg_setting)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                        ARouter.getInstance().build(ARouterConstants.UserSettingActivity).navigation()
                );
        //退出
        RxView.clicks(lg_logout).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                        new AlertDialog.Builder(getContext())
                        .setMessage(R.string.logout_msg)
                        .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                        .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                            new LogoutPresenter(getContext()).logout( SharedPreferencesUtil.getInstance().getUserId());
                            SmecRxBus.get().post("exitActivity",true);

                        }).create().show()
                );


    }
    //更新姓名和院系
    public void updateShowInfo() {
        userName.setText(SharedPreferencesUtil.getInstance().getUserName());
        String des = SharedPreferencesUtil.getInstance().getFacultyName();
        welcomeText.setText("".equals(des)?getString(R.string.scool_name):des);
    }

    /**
     * 更新完成刷新显示用户头像
     */
    public void refreshUserImage() {
        String str = SharedPreferencesUtil.getInstance().getUserLocal();
        if("".equals(str)){
            str = getString(R.string.get_image,SharedPreferencesUtil.getInstance().getUserImageUrl(),1);
        }

        Glide.with(this)
                .asBitmap()
                .load(str)
                .apply(option)
                .into(new SimpleTarget<Bitmap>() {
                    //得到图片
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        userImage.setImageBitmap(resource);
                        SharedPreferencesUtil.getInstance().setUserLocal(ImageUntil.saveBitmapFromView(resource,UUID.randomUUID().toString(),getContext(),0));

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        SharedPreferencesUtil.getInstance().setUserLocal("");
                        userImage.setImageResource(R.drawable.my);
                    }
                });

    }



    /**
     * 更新完成刷新显示用户其他信息（姓名、称呼）
     */
    public void refreshUserInformation() {
//        userName.setText(SharedPreferencesUtil.getInstance().getUserName());
//        // 判断性别设置欢迎语
//        String sex_temp = SharedPreferencesUtil.getInstance().getUserSex();
//        if (!sex_temp.equals("")) {
//            if (sex_temp.equals(getString(R.string.sex_male))) {
//                welcomeText.setText(getString(R.string.uc_hello, getString(R.string.sex_male_call)));
//            } else {
//                welcomeText.setText(getString(R.string.uc_hello, getString(R.string.sex_female_call)));
//            }
//        } else {
//            welcomeText.setText(getString(R.string.uc_hello, ""));
//        }

    }
    private void show(List<String> list, int type) {

        BottomDialog bottomDialog = new BottomDialog(getContext(),list);
        if (type == 2) {
            bottomDialog.setOnOptionClickListener(position -> {
                if(position==2){//查看头像
                    String path = SharedPreferencesUtil.getInstance().getUserLocal();
                    if("".equals(path)){
                        Bitmap image = ((BitmapDrawable)userImage.getDrawable()).getBitmap();
                        path=ImageUntil.saveBitmapFromView(image,UUID.randomUUID().toString(),getContext(),0);
                    }
                    LocalMedia localMedia = new LocalMedia();
                    localMedia.setPath(path);
                    localMedia.setPosition(0);
                    List<LocalMedia> localMedias = new ArrayList<>();
                    localMedias.add(localMedia);
                    PictureSelector.create(getActivity()).openGallery(PictureMimeType.ofImage())
                            .openExternalPreview(0, localMedias);
                }
                if (position == 1) {//从相册选择
                    PictureSelector.create(this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(1).enableCrop(true).compress(true)
                            .circleDimmedLayer(true)
                            .rotateEnabled(false)
                            .withAspectRatio(1, 1)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }
                if(position == 0) {//拍照
                    PictureSelector.create(this)
                            .openCamera(PictureMimeType.ofImage())
                            .maxSelectNum(1).enableCrop(true).compress(true)
                            .circleDimmedLayer(true)
                            .rotateEnabled(false)
                            .withAspectRatio(1, 1)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }
                bottomDialog.dismiss();
            });
        }

        bottomDialog.setOnCancelButtonClickListener(view -> bottomDialog.dismiss()).show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        SmecRxBus.get().unregister(this);
        super.onDestroyView();
    }

    /*
      修改头像成功
      */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_UPLOAD_USER_IMAGE)
    }, thread = EventThread.MAIN_THREAD)
    public void successfulUploadUserImage(String newUrl) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this.getActivity()) {
            Toast.makeText(getContext(), R.string.msg_success_upload_image, Toast.LENGTH_SHORT).show();
            Glide.with(this).load(newUrl).apply(option).into(userImage);
            //更新本地头像信息
            SharedPreferencesUtil.getInstance().setUserLocal(newUrl);
        }
    }
    /**
     * 上传失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this.getActivity()) {
            Toast.makeText(getContext(), ("".equals(msg) ? getString(R.string.net_work_error) : msg), Toast.LENGTH_SHORT).show();
        }
    }
    //申请权限
    private void requestPermissions() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            Log.d("mydemo", permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.d("mydemo", permission.name + " is denied. More info should be provided.");
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.d("mydemo", permission.name + " is denied.");
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    if(!PermissionsUtil.isNetworkConnected(getContext())){//网络检测
                        Toast.makeText(getContext(), R.string.no_net_tip_2,Toast.LENGTH_SHORT).show();
                        //updateImg(path);
                        return;
                    }
                    // 图片选择结果回调
                    selectImages = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种 path
                    // 1.media.getPath(); 为原图 path
                    // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
                    // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    pathName = selectImages.get(0).getCompressPath();
                    File file = new File(pathName);
                    new ChangeUserImagePresenter(getContext()).uploadUserImage(String.valueOf(SharedPreferencesUtil.getInstance().getUserId()), file,"0");
                    break;

            }
        }
    }
}
