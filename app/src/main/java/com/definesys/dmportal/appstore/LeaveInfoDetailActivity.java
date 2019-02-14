package com.definesys.dmportal.appstore;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Html;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.customViews.ReasonTypeListLayout;
import com.definesys.dmportal.appstore.presenter.GetApprovalRecordPresent;
import com.definesys.dmportal.appstore.presenter.GetCurrentLeaveInfoPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.appstore.utils.ImageUntil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.definesys.dmportal.appstore.utils.Constants.oneDay;

@Route(path = ARouterConstants.LeaveInFoDetailActivity)
public class LeaveInfoDetailActivity extends BaseActivity<GetApprovalRecordPresent> {

    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.name_text)
    TextView tv_name;
    @BindView(R.id.type_text)
    TextView tv_type;
    @BindView(R.id.title_text)
    TextView tv_title;
    @BindView(R.id.start_time_text)
    TextView tv_startTime;
    @BindView(R.id.end_time_text)
    TextView tv_endTime;
    @BindView(R.id.sum_time_text)
    TextView tv_sumTime;
    @BindView(R.id.selected_subject_text)
    TextView tv_selectedSubject;
    @BindView(R.id.content_text)
    TextView tv_content;
    @BindView(R.id.img_layout)
    RelativeLayout lg_img;
    @BindView(R.id.img_1)
    ImageView img_1;
    @BindView(R.id.img_2)
    ImageView img_2;
    @BindView(R.id.img_3)
    ImageView img_3;
    @BindView(R.id.status_text)
    TextView tv_status;
    @BindView(R.id.submit_time)
    TextView tv_submitTime;
    @BindView(R.id.check_approval_layout)
    LinearLayout lg_check;

    @BindView(R.id.no_layout)
    LinearLayout lg_no;

    @BindView(R.id.layout_scroll)
    ScrollView lg_scoll;

    @Autowired(name = "leaveInfo")
    LeaveInfo submitLeaveInfo;
    @Autowired(name = "title")
    int title;
    private List<LocalMedia> localMediaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_info_detail);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        setNoLayout(true);
        if(submitLeaveInfo!=null)
            initView();
        else {
            progressHUD.show();
            (new GetCurrentLeaveInfoPresenter(this)).getCurrentLeaveInfo(SharedPreferencesUtil.getInstance().getUserId());
        }
    }

    private void initView() {
        setNoLayout(false);
        titleBar.setTitle(title==0?R.string.leave_detail:R.string.leave_progress);
        titleBar.setBackgroundDividerEnabled(false);

        if(submitLeaveInfo.getType()==3)//实习
            submitLeaveInfo.setType(2);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
        
        //查看审批记录
        RxView.clicks(lg_check)
              .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
              .subscribe(obj->{
                  progressHUD.show();
                  mPersenter.getApprovalRecord(submitLeaveInfo.getId());
                 
              });  

        //姓名
        tv_name.setText(getString(R.string.name_tip,submitLeaveInfo.getName()));

        //请假类型
        tv_type.setText(getString(R.string.type_tip, DensityUtil.setTypeText(getResources().getStringArray(R.array.leave_type)[submitLeaveInfo.getType()%3])));

        //请假原因
        tv_title.setText(getString(R.string.leave_title,submitLeaveInfo.getLeaveTitle()));

        //具体原因
        tv_content.setText(getString(R.string.content_tip,submitLeaveInfo.getLeaveReason()));

        //请假时间
        tv_submitTime.setText(getString(R.string.submit_time,DensityUtil.dateTypeToString(getString(R.string.date_type_2),submitLeaveInfo.getSubmitDate())));

        //审批状态
        tv_status.setText(DensityUtil.getApprovalStatus(submitLeaveInfo,this,false));
        DensityUtil.setTVcolor(tv_status.getText().toString(),tv_status,this);

        //开始时间
        tv_startTime.setText(getString(R.string.start_time_tip,submitLeaveInfo.getStartTime()));

        //结束时间
        tv_endTime.setText(getString(R.string.end_time_tip,submitLeaveInfo.getEndTime()));

        if(submitLeaveInfo.getType()==0){//课假

            //课程选择
            tv_selectedSubject.setText(Html.fromHtml(getString(R.string.selected_subject_tip,submitLeaveInfo.getSelectedSubject())));

            //时长 2*‘#’的个数
            tv_sumTime.setText(getString(R.string.sum_time_tip,getString(R.string.off_suject,(2*(submitLeaveInfo.getSelectedSubject().split("\\#").length-1)))));
        }else {//长假或短假
           
            tv_selectedSubject.setVisibility(GONE);

            //时长
            tv_sumTime.setText(getString(R.string.sum_time_tip,getSumTime(submitLeaveInfo.getStartTime(),submitLeaveInfo.getEndTime())));
        }


        if(submitLeaveInfo.getPicUrl()==null||"".equals(submitLeaveInfo.getPicUrl().trim())) {//没有图片
            lg_img.setVisibility(GONE);
            return;
        }
        localMediaList = new ArrayList<>();
        String[] picUrls=submitLeaveInfo.getPicUrl().split("\\*");

        if(picUrls.length==0)
            lg_img.setVisibility(GONE);
        else {
            //相关图片
            lg_img.setVisibility(VISIBLE);
            for(int i = 0 ;i<picUrls.length;i++){
                localMediaList.add(new LocalMedia());
                if(i==0) {
                    initImg(img_1,picUrls[i],i);
                }
                else if(i==1) {
                    initImg(img_2,picUrls[i],i);
                }
                else {
                    initImg(img_3,picUrls[i],i);
                }
            }
        }
    }

    /**
     * 审批记录提示框
     * @param list
     */
    private void initDialog(List<ApprovalRecord> list) {
        Dialog dialog = new Dialog(this);
        ReasonTypeListLayout checkApprovalView = new ReasonTypeListLayout(this);
        checkApprovalView .getTitleText().setText(R.string.approval_record);
        checkApprovalView.setApprovalRecord(list);
        checkApprovalView .setMyOnConfirmClickListener(new ReasonTypeListLayout.MyOnConfirmClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(checkApprovalView);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    /**
     * 加载图片
     * @param img
     * @param picUrl
     */
    private void initImg(ImageView img, String picUrl,int position) {
        Glide.with(this)
             .asBitmap()
             .load(getString(R.string.get_image,picUrl,0))
             .into(new SimpleTarget<Bitmap>() {
                 //得到图片
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                     img.setImageBitmap(resource);
                     String path="";
                     try {
                         path=ImageUntil.saveBitmapFromView(resource,picUrl,LeaveInfoDetailActivity.this,0);
                     } catch (ParseException e) {
                         e.printStackTrace();
                     }
                     localMediaList.get(position).setPosition(position);
                     localMediaList.get(position).setPath(path);
                   //  LocalMedia localMedia1 = new LocalMedia(resource.);
                     RxView.clicks(img)
                             .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                             .subscribe(obj->{
                                 //放大图片
                                 PictureSelector.create(LeaveInfoDetailActivity.this)
                                         .openGallery(PictureMimeType.ofImage())
                                         .openExternalPreview(position,localMediaList);

                             });
                 }


                 @Override
                 public void onLoadFailed(@Nullable Drawable errorDrawable) {
                     img.setImageResource(R.drawable.error);
                 }
             });
    }

    /**
     * 获取审批记录失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(LeaveInfoDetailActivity.this,("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }

    /**
     * 获取审批记录成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPRVAL_RECORD)
    }, thread = EventThread.MAIN_THREAD)
    public void getApprovalStatus(BaseResponse<List<ApprovalRecord>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            progressHUD.dismiss();
            if(data.getData()==null||data.getData().size()==0){
                Toast.makeText(LeaveInfoDetailActivity.this, data.getMsg(),Toast.LENGTH_SHORT).show();
                return;
            }
            initDialog(data.getData());

        }
    }
    /**
     * 获取最近一次请假信息成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_CURRENT_LEAVE_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getCurrentLeaveInfo(BaseResponse<LeaveInfo> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            progressHUD.dismiss();
            if(data.getData()==null){
                setNoLayout(true);
                Toast.makeText(LeaveInfoDetailActivity.this, data.getMsg(),Toast.LENGTH_SHORT).show();
                return;
            }
            submitLeaveInfo = data.getData();
            initView();
        }
    }
    /**
     * 获取请假时长
     * @param startDateStr 开始时间的字符串 yyyy年MM月dd日 HH时
     * @param endDateStr 结束时间的字符串 yyyy年MM月dd日 HH时
     * @return
     */
    private String getSumTime(String startDateStr,String endDateStr){
        SimpleDateFormat df = new SimpleDateFormat(getString(R.string.date_type));
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = df.parse(startDateStr);
            endDate = df.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            startDate = new Date();
            endDate = new Date();
        }
        long time = endDate.getTime() - startDate.getTime();
        int day = (int)(time/oneDay );
        int hour = (int)(time/(oneDay /24))-day*24;
        return (day>0?getString(R.string.off_day,day):"")+(day>0&&hour==0?"":getString(R.string.off_hour,hour));
    }

    /**
     * 设置暂无页
     * @param isShowNoLayout 是否显示暂无页
     */
    private void setNoLayout(boolean isShowNoLayout){
        if(isShowNoLayout){
            lg_no.setVisibility(VISIBLE);
            lg_scoll.setVisibility(GONE);
        }else {
            lg_no.setVisibility(GONE);
            lg_scoll.setVisibility(VISIBLE);
        }
    }
    @Override
    public GetApprovalRecordPresent getPersenter() {
        return new GetApprovalRecordPresent(this);
    }
}
