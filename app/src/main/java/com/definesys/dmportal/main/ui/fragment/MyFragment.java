package com.definesys.dmportal.main.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.definesys.dmportal.config.MyCongfig;
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

    @BindView(R.id.person_layout)
    LinearLayout lg_person;

    @BindView(R.id.setting_layout)
    LinearLayout lg_setting;

    RequestOptions option = new RequestOptions()
            .centerCrop()
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);
    private int requestCount=0;//重新获取尝试次数
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
//        if (getArguments() != null) {
//            String mParam1 = getArguments().getString(ARG_PARAM1);
//            String mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        refreshUserImage();
    }


    //初始化界面
    private void initView() {
       updateShowInfo();

        //点击头像 显示图片
        RxView.clicks(userImage).throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    String path = SharedPreferencesUtil.getInstance().getUserLocal();
                    boolean isEmpty = "".equals(path);
                    if(isEmpty){
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
                    if(isEmpty)
                        userImage.setImageBitmap(BitmapFactory.decodeFile(path));
                });
        //个人信息
        RxView.clicks(lg_person)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->
                        ARouter.getInstance()
                                .build(ARouterConstants.UserInfoActivity)
                                .withObject("userId",SharedPreferencesUtil.getInstance().getUserId())
                                .navigation()
                );
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
        String des = SharedPreferencesUtil.getInstance().getUserType()==0?SharedPreferencesUtil.getInstance().getFacultyName():"";
        welcomeText.setText("".equals(des)?getString(R.string.scool_name):des);
    }

    /**
     * 更新完成刷新显示用户头像
     */
    public void refreshUserImage() {
        //提示单机登陆或账号冻结
        if(MyCongfig.isShowing) return;
        String str = SharedPreferencesUtil.getInstance().getUserLocal();
        if("".equals(str)){
            str = getString(R.string.get_image, SharedPreferencesUtil.getInstance().getHttpUrl(),SharedPreferencesUtil.getInstance().getUserImageUrl(),1);
        }

        Glide.with(this)
                .asBitmap()
                .load(str)
                .apply(option)
                .into(new SimpleTarget<Bitmap>() {
                    //得到图片
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        String path = ImageUntil.saveBitmapFromView(resource,UUID.randomUUID().toString(),getContext(),4);
                        SharedPreferencesUtil.getInstance().setUserLocal(path);
                        userImage.setImageBitmap(BitmapFactory.decodeFile(path));

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        SharedPreferencesUtil.getInstance().setUserLocal("");
//                        userImage.setImageResource(R.drawable.my);
                        if(++requestCount<5)
                            refreshUserImage();
                        else
                            userImage.setImageResource(R.drawable.my);
                    }
                });

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
        if(MyActivityManager.getInstance().getCurrentActivity()==this.getActivity()){
            Toast.makeText(getContext(), R.string.msg_success_upload_image, Toast.LENGTH_SHORT).show();
            //更新本地头像信息
            SharedPreferencesUtil.getInstance().setUserLocal(newUrl);
        }
        Glide.with(this).load(newUrl).apply(option).into(userImage);

//        refreshUserImage();
    }
    /**
     * 上传失败
     * @param msg msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity()==this.getActivity()) {
            Toast.makeText(getContext(), ("".equals(msg) ? getString(R.string.net_work_error) : msg), Toast.LENGTH_SHORT).show();
        }
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
                    List<LocalMedia> selectImages = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种 path
                    // 1.media.getPath(); 为原图 path
                    // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
                    // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                    String pathName = selectImages.get(0).getCompressPath();
                    File file = new File(pathName);
                    new ChangeUserImagePresenter(getContext()).uploadUserImage(String.valueOf(SharedPreferencesUtil.getInstance().getUserId()), file,"0");
                    break;

            }
        }
    }
}
