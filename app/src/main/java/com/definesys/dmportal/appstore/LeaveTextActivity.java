package com.definesys.dmportal.appstore;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.presenter.GetApprovalRecordPresent;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.appstore.utils.ImageUntil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.UserInfoPresent;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@Route(path = ARouterConstants.LeaveTextActivity)
public class LeaveTextActivity extends BaseActivity<GetApprovalRecordPresent> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.image_share)
    LinearLayout lg_img;

    @BindView(R.id.pdf_share)
    LinearLayout lg_pdf;

    @BindView(R.id.absence_layout)
    LinearLayout lg_absence;

    @Autowired(name = "leaveInfo")
    LeaveInfo leaveInfo;

    List<ApprovalRecord> approvalRecordList;//审批记录

    RequestOptions option = new RequestOptions()
            .signature(new ObjectKey(UUID.randomUUID().toString()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_text);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);

        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.leave_absence));
        titleBar.setBackgroundDividerEnabled(false);

        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });

        //生成图片并分享
        RxView.clicks(lg_img)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o->{
                    if(approvalRecordList.size()==0){//审批记录为空，重新获取
                       httpPost();
                    }else{
                        String leaveType = "".equals(leaveInfo.getLeaveType())?DensityUtil.setTypeText(getResources().getStringArray(R.array.leave_type)[leaveInfo.getType()%3]):leaveInfo.getLeaveType();
                        share(true,getString(R.string.file_name,leaveInfo.getName(),leaveType,leaveInfo.getLeaveTitle(),(new SimpleDateFormat(getString(R.string.date_type_7), Locale.getDefault())).format(new Date())));
                    }
                });

        //生成pdf并分享
        RxView.clicks(lg_pdf)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o->{
                    if(approvalRecordList.size()==0){////审批记录为空，重新获取
                        httpPost();
                    }else {
                        String leaveType = "".equals(leaveInfo.getLeaveType())?DensityUtil.setTypeText(getResources().getStringArray(R.array.leave_type)[leaveInfo.getType()%3]):leaveInfo.getLeaveType();
                        share(false, getString(R.string.file_name, leaveInfo.getName(), leaveType, leaveInfo.getLeaveTitle(),(new SimpleDateFormat(getString(R.string.date_type_7), Locale.getDefault())).format(new Date())));
                    }
                });

        approvalRecordList = new ArrayList<>();
        httpPost();

    }

    private void httpPost() {
        progressHUD.show();
        mPersenter.getApprovalRecordList(leaveInfo.getId(),null);
    }

    /**
     * 分享图片或pdf
     * @param isImage 是不是图片
     * @param fileName 用户名-请假类型-请假标题-yyyyMMdd-HH:mm:ss
     */
    private void share(boolean isImage,String fileName){
        Observable.create((ObservableOnSubscribe<File>) emitter -> {
            File file=isImage?ImageUntil.saveBitmap(ImageUntil.convertViewToBitmap(lg_absence),fileName):ImageUntil.viewToPdf(lg_absence,fileName);
            emitter.onNext(file);
            emitter.onComplete();
        }).subscribeOn(Schedulers.io())// 指定 subscribe() 发生在 IO 线程
                .doOnSubscribe(disposable -> progressHUD.show())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        Intent share_intent = new Intent();
                        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
                        share_intent.setType(isImage?"image/*":"application/pdf");//设置分享内容的类型
                        share_intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));//添加分享内容
                        startActivity(Intent.createChooser(share_intent, ""));
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressHUD.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        progressHUD.dismiss();
                    }
                });

    }

    /**
     * 获取审批记录失败
     * @param msg m
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(LeaveTextActivity.this,("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }

    /**
     * 获取审批记录成功
     * @param data d
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPRVAL_RECORD)
    }, thread = EventThread.MAIN_THREAD)
    public void getApprovalStatus(BaseResponse<List<ApprovalRecord>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(data.getData()==null||data.getData().size()==0){
                Toast.makeText(LeaveTextActivity.this, data.getMsg(),Toast.LENGTH_SHORT).show();
                return;
            }
            approvalRecordList.clear();
            approvalRecordList.addAll(data.getData());
            initText();
            progressHUD.dismiss();
        }
    }
    /**
     * 获取用户姓名成功
     * @param hash h
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_USER_NAME)
    }, thread = EventThread.MAIN_THREAD)
    public void getUserName(HashMap<String,Object> hash) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            progressHUD.dismiss();
           if(hash.get("data")!=null&&hash.get("tv_name")!=null) {
               ((TextView) hash.get("tv_name")).setText(hash.get("data").toString());
               if(hash.get("progress")!=null)
                   ((ProgressBar)hash.get("progress")).setVisibility(View.GONE);
           }
           else
               Toast.makeText(this, R.string.get_sign_fail,Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 获取用户姓名失败
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK_NAME)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkErroNamer(HashMap<String,Object> hash) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(hash.get("tv_name")!=null)
             ((TextView) hash.get("tv_name")).setText(R.string.get_name_fail);
            if(hash.get("progress")!=null)
                ((ProgressBar)hash.get("progress")).setVisibility(View.GONE);
            progressHUD.dismiss();
        }
    }
    /**
     * 生成请假条
     */
    private void initText() {
        lg_absence.removeAllViews();

        if(leaveInfo.getUserType()==0) {//学生
            //课假或短假
            if (leaveInfo.getType() == 0 || leaveInfo.getType() == 1) {
                lg_absence.addView(addShortView(0));
            }

            //长假且不是实习
            else if (leaveInfo.getType() == 2 && !getString(R.string.shixi).equals(leaveInfo.getLeaveTitle())) {
                lg_absence.addView(addLongView(""));
            }

            //长假且是实习
            else if (leaveInfo.getType() == 2 && getString(R.string.shixi).equals(leaveInfo.getLeaveTitle()) || leaveInfo.getType() == 3) {
                lg_absence.addView(addLongView(getString(R.string.practice_des)));
                lg_absence.addView(addPractice());
            }
        }else if(leaveInfo.getUserType()==1){//教师
            //课假或短假
            if (leaveInfo.getType() == 0 || leaveInfo.getType() == 1) {
                lg_absence.addView(addShortView(1));
            }
            else if (leaveInfo.getType() == 2 ) {//长假
                lg_absence.addView(addTeacherLongView());
            }
        }
        //        设置横屏
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    //添加学生实习请假条
    private View addPractice() {
        View view = LayoutInflater.from(this).inflate(R.layout.leave_practice_view,lg_absence,false);
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_type_4), Locale.getDefault());

        //编号
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(leaveInfo.getSubmitDate());
        ((TextView)view.findViewById(R.id.leave_number)).setText(getString(R.string.leave_practice_number,calendar.get(Calendar.YEAR),"         "));

        //学生签名
        ((TextView)view.findViewById(R.id.stu_sign)).setText(getString(R.string.stu_sign,leaveInfo.getName()));
        //学号
        ((TextView)view.findViewById(R.id.stuId_text)).setText(getString(R.string.stu_id,leaveInfo.getUserId().intValue()));
        //签名时间
        ((TextView)view.findViewById(R.id.sign_time)).setText(sdf.format(leaveInfo.getSubmitDate()));

        //各部门签字
        for (ApprovalRecord approvalRecord:approvalRecordList){
            int approvalAuthority  = approvalRecord.getApproverType();
            if(approvalAuthority==0){//宿舍长
                //签字
                setSign(view.findViewById(R.id.sign_img_0),view.findViewById(R.id.sign_text_0), view.findViewById(R.id.progressBar0),approvalRecord.getApproverId(),0);
            }else if(approvalAuthority<0){//销假签字
                //返校后签字
                ((TextView)view.findViewById(R.id.sign_text_8)).setText(leaveInfo.getName());
            }
            else {
                while (approvalAuthority%10>=0&&approvalAuthority>0){
                    int authority = approvalAuthority%10;
                    if(authority==0) {//宿舍长
                        //签字
                        setSign( view.findViewById(R.id.sign_img_0),  view.findViewById(R.id.sign_text_0),  view.findViewById(R.id.progressBar0), approvalRecord.getApproverId(), 0);
                    }else if(authority==1){//班长
                        //签字
                        setSign(view.findViewById(R.id.sign_img_1),view.findViewById(R.id.sign_text_1),view.findViewById(R.id.progressBar1),approvalRecord.getApproverId(),0);

                    }else if(authority==2){//班主任
                        //签字
                        setSign(view.findViewById(R.id.sign_img_2),view.findViewById(R.id.sign_text_2),view.findViewById(R.id.progressBar2),approvalRecord.getApproverId(),1);

                    }else if(authority==3){//毕设导师
                        //签字
                        setSign(view.findViewById(R.id.sign_img_3),view.findViewById(R.id.sign_text_3),view.findViewById(R.id.progressBar3),approvalRecord.getApproverId(),1);

                    }else if(authority==4){//辅导员
                        //签字
                        setSign(view.findViewById(R.id.sign_img_4),view.findViewById(R.id.sign_text_4),view.findViewById(R.id.progressBar4),approvalRecord.getApproverId(),1);

                    }else if(authority==5){//实习工作负责人
                        //签字
                        setSign(view.findViewById(R.id.sign_img_5),view.findViewById(R.id.sign_text_5),view.findViewById(R.id.progressBar5),approvalRecord.getApproverId(),1);

                    }else if(authority==6){//学习工作负责人
                        //签字
                        setSign(view.findViewById(R.id.sign_img_6),view.findViewById(R.id.sign_text_6),view.findViewById(R.id.progressBar6),approvalRecord.getApproverId(),1);

                    }else if(authority==7){//教学工作负责人
                        //签字
                        setSign(view.findViewById(R.id.sign_img_7),view.findViewById(R.id.sign_text_7),view.findViewById(R.id.progressBar7),approvalRecord.getApproverId(),1);
                    }
                    approvalAuthority/=10;
                }
            }
        }
        //《-----------------------存根-----------------------------》
        //编号
        ((TextView)view.findViewById(R.id.leave_copy_number)).setText(getString(R.string.leave_practice_number,calendar.get(Calendar.YEAR),"         "));
        //签名时间
        ((TextView)view.findViewById(R.id.copy_time)).setText(sdf.format(leaveInfo.getSubmitDate()));

        try {
            String[] arr = leaveInfo.getLeaveReason().split("\\+");
            //主文描述
            ((TextView)view.findViewById(R.id.des_text)).setText(Html.fromHtml(getString(R.string.leave_practice_content,"<u> "+arr[0]+" </u>","<u> "+arr[1]+" </u>","<u> "+leaveInfo.getStartTime().substring(0,11)+" </u>","<u> "+leaveInfo.getEndTime().substring(0,11)+" </u>")));
            //存根描述
            ((TextView)view.findViewById(R.id.copy_des)).setText(getString(R.string.leave_practice_copy_content,leaveInfo.getUserId().intValue()/100,leaveInfo.getUserName(),leaveInfo.getEndTime().substring(0,11)));
        }catch (Exception e){
            String[] arr = leaveInfo.getLeaveReason().split("\\+");
            String startTime=leaveInfo.getStartTime().length()>=11?leaveInfo.getStartTime().substring(0,11):leaveInfo.getStartTime();
            String endTime = leaveInfo.getEndTime().length()>=11?leaveInfo.getEndTime().substring(0,11):leaveInfo.getEndTime();
            if(arr.length>1) {
                //主文描述
                ((TextView) view.findViewById(R.id.des_text)).setText(Html.fromHtml(getString(R.string.leave_practice_content, "<u> "+arr[0]+" </u>", "<u> "+arr[1]+" </u>", "<u> "+startTime+" </u>", "<u> "+endTime+" </u>")));
                //存根描述
                ((TextView) view.findViewById(R.id.copy_des)).setText(getString(R.string.leave_practice_copy_content, leaveInfo.getUserId().intValue() / 100, leaveInfo.getUserName(), endTime));
            }
            else {
                //主文描述
                ((TextView) view.findViewById(R.id.des_text)).setText(Html.fromHtml(getString(R.string.leave_practice_content, "<u> "+leaveInfo.getLeaveReason()+" </u>", "<u> "+leaveInfo.getLeaveReason()+" </u>", "<u> "+startTime+"</u> ", " <u>"+endTime+" </u>")));
                //存根描述
                ((TextView) view.findViewById(R.id.copy_des)).setText(getString(R.string.leave_practice_copy_content, leaveInfo.getUserId().intValue() / 100, leaveInfo.getUserName(), endTime));
            }
        }

       return view;
    }

    //添加学生长假请假条
    private View addLongView(String leaveReason) {
        View view = LayoutInflater.from(this).inflate(R.layout.leave_long_view,lg_absence,false);
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_type_4), Locale.getDefault());
        //姓名
        ((TextView)view.findViewById(R.id.leave_name)).setText(getString(R.string.name_tip,leaveInfo.getName()));
        //事由
        ((TextView)view.findViewById(R.id.leave_reason)).setText(getString(R.string.leave_title,"\n"+("".equals(leaveReason)?leaveInfo.getLeaveReason():leaveReason)));
        //时长
        ((TextView)view.findViewById(R.id.leave_sum)).setText(getString(R.string.sum_time, DensityUtil.getSumTime(leaveInfo.getStartTime(),leaveInfo.getEndTime(),this,false)));
        try {
            //时间
            ((TextView)view.findViewById(R.id.leave_time)).setText(getString(R.string.leave_time_start_to_end,leaveInfo.getStartTime().substring(0,11),leaveInfo.getEndTime().substring(0,11)));

        }catch (Exception e){
            //时间
            ((TextView)view.findViewById(R.id.leave_time)).setText(getString(R.string.leave_time_start_to_end,leaveInfo.getStartTime(),leaveInfo.getEndTime()));
        }
        //销假签字
        ((TextView)view.findViewById(R.id.return_sign)).setText(getString(R.string.stu_return_sign,""));
        //销假日期
        ((TextView)view.findViewById(R.id.return_time)).setText(getString(R.string.return_time,getString(R.string.return_date,"        ","     ","     ")));

        //各部门签字
        for (ApprovalRecord approvalRecord:approvalRecordList){
            int approvalAuthority  = approvalRecord.getApproverType();
            if(approvalAuthority<0){//销假
                //销假签字
                ((TextView)view.findViewById(R.id.return_sign)).setText(getString(R.string.stu_return_sign,leaveInfo.getUserName()));
                //销假日期
                ((TextView)view.findViewById(R.id.return_time)).setText(getString(R.string.return_time,sdf.format(leaveInfo.getSubmitDate())));
            }
            else if(approvalAuthority>0){//未销假
                while (approvalAuthority%10>=0&&approvalAuthority>0){
                    int authority = approvalAuthority%10;
                    if(authority==1){//班长
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_1)).setText(getString(R.string.agree_tip_1,"  "+approvalRecord.getApprovalContent()));
                        //签字
                        setSign(view.findViewById(R.id.sign_img_1),view.findViewById(R.id.sign_text_1),view.findViewById(R.id.progressBar1),approvalRecord.getApproverId(),0);
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_1)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }else if(authority==2){//班主任
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_2)).setText(getString(R.string.agree_tip_2,"  "+approvalRecord.getApprovalContent()));
                        //签字
                        setSign(view.findViewById(R.id.sign_img_2),view.findViewById(R.id.sign_text_2),view.findViewById(R.id.progressBar2),approvalRecord.getApproverId(),1);
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_2)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }else if(authority==4){//辅导员
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_3)).setText(getString(R.string.agree_tip_3,"  "+approvalRecord.getApprovalContent()));
                        //签字
                        setSign(view.findViewById(R.id.sign_img_3),view.findViewById(R.id.sign_text_3),view.findViewById(R.id.progressBar3),approvalRecord.getApproverId(),1);
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_3)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }else if(authority==6){//学生工作负责人
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_4)).setText(getString(R.string.agree_tip_4,"  "+approvalRecord.getApprovalContent()));
                        //签字
                        setSign(view.findViewById(R.id.sign_img_4),view.findViewById(R.id.sign_text_4),view.findViewById(R.id.progressBar4),approvalRecord.getApproverId(),1);
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_4)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }else if(authority==7){//教学工作负责人
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_5)).setText(getString(R.string.agree_tip_5,"  "+approvalRecord.getApprovalContent()));
                        //签字
                        setSign(view.findViewById(R.id.sign_img_5),view.findViewById(R.id.sign_text_5),view.findViewById(R.id.progressBar5),approvalRecord.getApproverId(),1);
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_5)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }
                    approvalAuthority/=10;
                }
            }
        }

       return view;
    }
    //添加教师长假请假条
    private View addTeacherLongView() {
         View view = LayoutInflater.from(this).inflate(R.layout.leave_teacher_long_view,lg_absence,false);
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.date_type_4), Locale.getDefault());
        //姓名
        ((TextView)view.findViewById(R.id.leave_name)).setText(getString(R.string.name_tip,leaveInfo.getName()));
        //事由
        ((TextView)view.findViewById(R.id.leave_reason)).setText(getString(R.string.leave_title,"\n"+leaveInfo.getLeaveReason()));
        //时长
        ((TextView)view.findViewById(R.id.leave_sum)).setText(getString(R.string.sum_time, DensityUtil.getSumTime(leaveInfo.getStartTime(),leaveInfo.getEndTime(),this,false)));
        try {
            //时间
            ((TextView)view.findViewById(R.id.leave_time)).setText(getString(R.string.leave_time_start_to_end,leaveInfo.getStartTime().substring(0,11),leaveInfo.getEndTime().substring(0,11)));

        }catch (Exception e){
            //时间
            ((TextView)view.findViewById(R.id.leave_time)).setText(getString(R.string.leave_time_start_to_end,leaveInfo.getStartTime(),leaveInfo.getEndTime()));
        }
        //销假签字
        ((TextView)view.findViewById(R.id.return_sign)).setText(getString(R.string.tea_return_sign,""));
        //销假日期
        ((TextView)view.findViewById(R.id.return_time)).setText(getString(R.string.return_time,getString(R.string.return_date,"        ","     ","     ")));

        //各部门签字
        for (ApprovalRecord approvalRecord:approvalRecordList){
            int approvalAuthority  = approvalRecord.getApproverType();
            if(approvalAuthority<0){//销假
                //销假签字
                ((TextView)view.findViewById(R.id.return_sign)).setText(getString(R.string.stu_return_sign,leaveInfo.getUserName()));
                //销假日期
                ((TextView)view.findViewById(R.id.return_time)).setText(getString(R.string.return_time,sdf.format(leaveInfo.getSubmitDate())));
            }else if(approvalAuthority==0){//请假负责人
                //签字
                setSign(view.findViewById(R.id.sign_img_0),view.findViewById(R.id.sign_text_0),view.findViewById(R.id.progressBar0),approvalRecord.getApproverId(),1);
                //意见
                ((TextView)view.findViewById(R.id.agree_text_0)).setText(getString(R.string.agree_tip_6,"  "+approvalRecord.getApprovalContent()));
                //签名日期
                ((TextView)view.findViewById(R.id.sign_time_0)).setText(sdf.format(approvalRecord.getApprovalTime()));
            }
            else if(approvalAuthority>0){//未销假
                while (approvalAuthority%10>=0&&approvalAuthority>0){
                    int authority = approvalAuthority%10;
                    if(authority==0){//请假负责人
                        //签字
                        setSign(view.findViewById(R.id.sign_img_0),view.findViewById(R.id.sign_text_0),view.findViewById(R.id.progressBar0),approvalRecord.getApproverId(),1);
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_0)).setText(getString(R.string.agree_tip_6,"  "+approvalRecord.getApprovalContent()));
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_0)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }else if(authority==1){//教学院长
                        //意见
                        ((TextView)view.findViewById(R.id.agree_text_1)).setText(getString(R.string.agree_tip_7,"  "+approvalRecord.getApprovalContent()));
                        //签字
                        setSign(view.findViewById(R.id.sign_img_1),view.findViewById(R.id.sign_text_1),view.findViewById(R.id.progressBar1),approvalRecord.getApproverId(),1);
                        //签名日期
                        ((TextView)view.findViewById(R.id.sign_time_1)).setText(sdf.format(approvalRecord.getApprovalTime()));
                    }
                    approvalAuthority/=10;
                }
            }
        }

        return view;
    }

    /**
     * 添加短假请假条
     * @param userType 用户类型 0.学生 1.教师
     */
    private View addShortView(int userType) {
        View view = LayoutInflater.from(this).inflate(userType==0?R.layout.leave_short_view:R.layout.leave_teacher_short_view,lg_absence,false);
        //请假标题
        ((TextView)view.findViewById(R.id.short_leave_des)).setText(getString(R.string.leave_short_des, SharedPreferencesUtil.getInstance().getFacultyName()));
        //姓名
        ((TextView)view.findViewById(R.id.name_text)).setText(leaveInfo.getName());
        //学号
        ((TextView)view.findViewById(R.id.stuId_text)).setText(String.valueOf(leaveInfo.getUserId().intValue()));
        //事由
        ((TextView)view.findViewById(R.id.leave_reason)).setText(leaveInfo.getLeaveReason());
        //时间
        ((TextView)view.findViewById(R.id.submit_time)).setText(new SimpleDateFormat(getString(R.string.date_type_5), Locale.getDefault()).format(leaveInfo.getSubmitDate()));
        //辅导员或请假管理员意见
        ((TextView)view.findViewById(R.id.approval_content)).setText(approvalRecordList.get(0).getApprovalContent());
        //签名
        setSign(view.findViewById(R.id.sign_img),view.findViewById(R.id.sign_text),view.findViewById(R.id.progressBar),approvalRecordList.get(0).getApproverId(),userType);
        //签名日期
        ((TextView)view.findViewById(R.id.sign_time)).setText(new SimpleDateFormat(getString(R.string.date_type_6), Locale.getDefault()).format(approvalRecordList.get(0).getApprovalTime()));

        //<--------------存根------------------>
        //姓名
        ((TextView)view.findViewById(R.id.name_copy)).setText(leaveInfo.getName());
        //学号
        ((TextView)view.findViewById(R.id.stuId_copy)).setText(String.valueOf(leaveInfo.getUserId().intValue()));
        //事由
        ((TextView)view.findViewById(R.id.reason_copy)).setText(leaveInfo.getLeaveReason());
        //时间
        ((TextView)view.findViewById(R.id.time_copy)).setText(new SimpleDateFormat(getString(R.string.date_type_5), Locale.getDefault()).format(leaveInfo.getSubmitDate()));

        return view;
    }

    /**
     * 设置签名
     * @param img 签名图片
     * @param tv_sign 签名text
     * @param progressBar 进度
     * @param signUrl 图片url
     * @param userType 用户类型 0.学生 1.教师
     */
    private void setSign(ImageView img, TextView tv_sign, ProgressBar progressBar,Number signUrl,int userType) {
        Glide.with(this).asBitmap().load(getString(R.string.get_image, SharedPreferencesUtil.getInstance().getHttpUrl(),String.valueOf(signUrl.intValue())+".png",2)).apply(option).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                new UserInfoPresent(LeaveTextActivity.this).getUserName(signUrl,userType,tv_sign,progressBar);
                img.setVisibility(View.GONE);
            }

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                progressBar.setVisibility(View.GONE);
                img.setImageBitmap(resource);
            }
        });
    }

    @Override
    public GetApprovalRecordPresent getPersenter() {
        return new GetApprovalRecordPresent(this);
    }
}
