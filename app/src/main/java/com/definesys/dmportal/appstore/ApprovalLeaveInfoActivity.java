package com.definesys.dmportal.appstore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.presenter.GetApprovalRecordPresent;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.appstore.utils.ImageUntil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
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
import io.reactivex.functions.Consumer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.definesys.dmportal.appstore.utils.Constants.oneDay;
import static com.definesys.dmportal.appstore.utils.Constants.scrollDelay;

@Route(path = ARouterConstants.ApprovalLeaveInfoActivity)
public class ApprovalLeaveInfoActivity extends  BaseActivity<GetApprovalRecordPresent> {

    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.name_text)
    TextView tv_name;
    @BindView(R.id.userId_text)
    TextView tv_userId;
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
    @BindView(R.id.submit_time)
    TextView tv_submitTime;
//    @BindView(R.id.check_approval_layout)
//    LinearLayout lg_check;
    @BindView(R.id.layout_scroll)
    ScrollView lg_scoll;
    @BindView(R.id.count_word_text)
    TextView tv_count;
    @BindView(R.id.ed_reason)
    EditText ed_reason;
    @BindView(R.id.down_icon)
    ImageView iv_down;
    @BindView(R.id.info_layout)
    RelativeLayout lg_info;
    @BindView(R.id.more_layout)
    LinearLayout lg_more;
    @BindView(R.id.subject_layout)
    LinearLayout lg_subject;
    @BindView(R.id.yes_text)
    TextView tv_yes;
    @BindView(R.id.yes_icon)
    ImageView iv_yes;
    @BindView(R.id.yes_layout)
    LinearLayout lg_yes;
    @BindView(R.id.no_text)
    TextView tv_no;
    @BindView(R.id.no_layout)
    LinearLayout lg_no;
    @BindView(R.id.no_icon)
    ImageView iv_no;

    @BindView(R.id.approval_content_text)
    TextView tv_approvalContent;

    @Autowired(name = "leaveInfo")
    LeaveInfo submitLeaveInfo;

    @Autowired(name = "approvalRecord")
    ApprovalRecord approvalRecord;

    @Autowired(name = "leaveId")
    String leaveId;//请假id
    @Autowired(name = "type")
    int type;//审批的类型 0.拒绝 1.同意 4.未审批
    @Autowired(name = "approvalDate")
    Date approvalDate;//审批时间
    @Autowired(name = "approvalContent")
    String approvalContent;//审批内容


    private List<LocalMedia> localMediaList;
    private boolean isAgree = true;//是否同意
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_leave_info);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        boolean flag = false;
        if(submitLeaveInfo!=null) {
            initView();
            initEdit();
            flag = true;
        }else if(leaveId!=null&&!"".equals(leaveId)){//根据leaveId获取请假信息
            progressHUD.show();
            mPersenter.getLeaveInfoById(leaveId);
            if(type!=4&&approvalDate==null&&approvalContent!=null&&!"".equals(approvalContent)){//已审批但找不到审批记录
                isAgree = type==1;
                approvalRecord = new ApprovalRecord(leaveId,0,approvalContent,(short)type,null,0,0);
                if(submitLeaveInfo!=null){//已获取到请假信息
                    initView();
                    initEditUnable();
                }
            }else if(type!=4&&approvalDate!=null){
                mPersenter.getApprovalRecordByDate(leaveId,approvalDate,this);
            }
        }
        else {
            progressHUD.show();
            mPersenter.getLeaveInfoById(approvalRecord.getLeaveInfoId());
        }
        initTitle(flag);
    }

    private void initTitle(boolean isSow) {
        titleBar.setTitle(approvalRecord==null?getString(R.string.approval_leave_info):getString(R.string.approval_leave_info_2));
        titleBar.setBackgroundDividerEnabled(false);
        //titleBar.setBackground(null);
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED,intent);
                        finish();
                    }
                });
        if(approvalRecord==null&&isSow) {
            titleBar.removeAllRightViews();
            Button button = titleBar.addRightTextButton(getString(R.string.submit), R.layout.activity_approval_leave_info);
            button.setTextSize(14);

            //提交
            RxView.clicks(button)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> checkContent());
        }else {
            titleBar.removeAllRightViews();
        }
        tv_approvalContent.setVisibility(GONE);

    }

    private void initView() {
        initTitle(true);
        //点击更多
        RxView.clicks(lg_info)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    //获取焦点
                    lg_info.setFocusableInTouchMode(true);
                    lg_info.setFocusable(true);
                    if(lg_more.getVisibility()== View.VISIBLE){//已显示更多信息
                        lg_more.setVisibility(GONE);
                        iv_down.setRotation(0);
                    }else {//未显示更多信息
                        lg_more.setVisibility(VISIBLE);
                        iv_down.setRotation(180);
                        sendScrollMessage(ScrollView.FOCUS_UP);
                    }
                });
        //点击查看课表
        RxView.clicks(lg_subject)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                  ARouter.getInstance()
                          .build(ARouterConstants.SubjectTableActivity)
                          .withInt("checkId",submitLeaveInfo.getUserId().intValue())
                          .withInt("userType",submitLeaveInfo.getUserType())
                          .navigation();
                });
        if(approvalRecord==null) {
            //点击同意
            RxView.clicks(lg_yes)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        isAgree = true;
                        setAgreeText();
                    });
            //点击拒绝
            RxView.clicks(lg_no)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        isAgree = false;
                        setAgreeText();
                    });
        }

        //设置同意或拒绝
        setAgreeText();

        //姓名
        tv_name.setText(getString(R.string.name_tip,submitLeaveInfo.getName()));

        //学号
        tv_userId.setText(""+submitLeaveInfo.getUserId().longValue());

        //请假类型
        tv_type.setText(getString(R.string.type_tip, DensityUtil.setTypeText(getResources().getStringArray(R.array.leave_type)[submitLeaveInfo.getType()])));

        //请假原因
        tv_title.setText(getString(R.string.leave_title,submitLeaveInfo.getLeaveTitle()));

        //具体原因
        tv_content.setText(getString(R.string.content_tip,submitLeaveInfo.getLeaveReason()));

        //请假时间
        tv_submitTime.setText(getString(R.string.submit_time,DensityUtil.dateTypeToString(getString(R.string.date_type_2),submitLeaveInfo.getSubmitDate())));

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
     * 规范性检查
     */
    private void checkContent() {
        if("".equals(ed_reason.getText().toString())&&!isAgree) {//不同意且未输入审批意见
            Toast.makeText(this, R.string.approval_addvise_tip_2, Toast.LENGTH_SHORT).show();
            return;
        }
        initResultDialog();
    }

    private void initResultDialog() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.view_approval_result_confirm,null);

        //审批结果
        TextView tv_reult = (TextView) view.findViewById(R.id.approval_result_des);
        tv_reult.setText(isAgree?R.string.agree_tip:R.string.refuse_tip);
        tv_reult.setTextColor(isAgree?getResources().getColor(R.color.green):getResources().getColor(R.color.red_error));

        //审批意见
        String content = ed_reason.getText().toString();
        if("".equals(content)&&isAgree)
            content = getString(R.string.agree_tip);
        ((TextView) view.findViewById(R.id.approval_content_text)).setText(getString(R.string.approval_addvise)+":\n "+content);

        //确定
        String finalContent = content;
        RxView.clicks(view.findViewById(R.id.confirm_text))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ApprovalRecord approvalRecord = new ApprovalRecord(submitLeaveInfo.getId(),SharedPreferencesUtil.getInstance().getUserId().intValue(),
                            finalContent,(short)(isAgree?1:0),null,
                            SharedPreferencesUtil.getInstance().getUserAuthority(),submitLeaveInfo.getUserId().intValue());
                    mPersenter.updateApprovalStatusById(approvalRecord);
                    progressHUD.show();
                    dialog.dismiss();
                });
        //取消
        RxView.clicks(view.findViewById(R.id.cancel_text))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    dialog.dismiss();
                });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * 设置同意或拒绝显示效果
     */
    private void setAgreeText() {
        if (isAgree){//同意
            iv_no.setVisibility(View.INVISIBLE);
            iv_yes.setVisibility(VISIBLE);
            tv_yes.setTextColor(getResources().getColor(R.color.green));
            tv_no.setTextColor(getResources().getColor(R.color.black));
        }else {//拒绝
            iv_no.setVisibility(View.VISIBLE);
            iv_yes.setVisibility(View.INVISIBLE);
            tv_yes.setTextColor(getResources().getColor(R.color.black));
            tv_no.setTextColor(getResources().getColor(R.color.red_error));
        }
    }

    //具体原因编辑框设置
    private void initEdit() {
        tv_count.setText(getString(R.string.word_count, 0));
        //点击事件
        RxView.clicks(ed_reason)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ed_reason.setCursorVisible(true);
                    sendScrollMessage(ScrollView.FOCUS_DOWN);
                });
        //获取焦点
        ed_reason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ed_reason.setCursorVisible(true);
                    sendScrollMessage(ScrollView.FOCUS_DOWN);
                }
            }
        });
        /*
        监听输入框内容 《==》 获取输入长度显示到界面
         */
        ed_reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_count.setText(getString(R.string.word_count, ed_reason.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //设置编辑框内容
    private void initEditUnable(){
        ed_reason.setVisibility(GONE);
        tv_approvalContent.setVisibility(VISIBLE);
        tv_approvalContent.setText(approvalRecord.getApprovalContent());
        tv_count.setText(getString(R.string.word_count, tv_approvalContent.getText().toString().length()));
    }

    /**
     * 延时发送页面滑动消息
     * @param position 活动到的位置
     */
    private void sendScrollMessage(int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("mydemo","Height=="+lg_scoll.getMeasuredHeight());
                lg_scoll.fullScroll(position);
            }
        }, Constants.scrollDelay);
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
                        path= ImageUntil.saveBitmapFromView(resource,picUrl,ApprovalLeaveInfoActivity.this,0);
                        localMediaList.get(position).setPosition(position);
                        localMediaList.get(position).setPath(path);
                        //  LocalMedia localMedia1 = new LocalMedia(resource.);
                        RxView.clicks(img)
                                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                                .subscribe(obj->{
                                    //放大图片
                                    PictureSelector.create(ApprovalLeaveInfoActivity.this)
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
            Toast.makeText(ApprovalLeaveInfoActivity.this,("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }

    /**
     * 更新审批状态成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_UPDATE_APPRVAL_RECORD)
    }, thread = EventThread.MAIN_THREAD)
    public void getApprovalStatus(BaseResponse<String> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            progressHUD.dismiss();
            SmecRxBus.get().post("updateSuccess",submitLeaveInfo.getId());
            //审批人提交审批成功
            String content = ed_reason.getText().toString();  //审批意见
            if("".equals(content)&&isAgree)
                content = getString(R.string.agree_tip);
            SmecRxBus.get().post("addMessage",new MyMessage(data.getData(),submitLeaveInfo.getUserId(), (short) 2, content, (short)(isAgree?1:0) ,submitLeaveInfo.getId(),null,new Date() ));
            Toast.makeText(ApprovalLeaveInfoActivity.this, data.getMsg(),Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    /**
     * 获取审批记录成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPRVAL_RECORD_BY_DATE)
    }, thread = EventThread.MAIN_THREAD)
    public void getApprovalInfo(BaseResponse<ApprovalRecord> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(data.getData()==null){
                Toast.makeText(ApprovalLeaveInfoActivity.this, data.getMsg(),Toast.LENGTH_SHORT).show();
            }else {
                approvalRecord = data.getData();
                if(submitLeaveInfo!=null){
                    progressHUD.dismiss();
                    initView();
                    initEditUnable();
                }
            }
        }

    }

    /**
     * 获取请假信息成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_LEAVE_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getLeaveInfById(BaseResponse<LeaveInfo> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            submitLeaveInfo = data.getData();
            progressHUD.dismiss();
            if(submitLeaveInfo==null){
                Toast.makeText(ApprovalLeaveInfoActivity.this, data.getMsg(),Toast.LENGTH_SHORT).show();
            }else {
                if(approvalRecord!=null&type!=4) {
                    isAgree = approvalRecord.getApprovalResult() != 0;
                    initView();
                    initEditUnable();
                }
//                }else if(!checkAuthority()){
//
//                }
                else if(type==4){
                    initView();
                    initEdit();
                }
            }
        }
    }

    /**
     * 权限检测，检测该请假记录是否有权限审批
     * @return ture 有审批 false无权限
     */
    private boolean checkAuthority() {
        boolean flag = true;
        if(type==4){
            if(submitLeaveInfo.getApprovalStatus()>=100){//已经审批
                flag = false;
            }else {
                int userAuthority = SharedPreferencesUtil.getInstance().getUserAuthority();
                if(userAuthority==0&&userAuthority<submitLeaveInfo.getApprovalStatus()){//已经审批
                    flag = false;
                }else {
                    int max=0;
                    while (userAuthority%10>=0&&userAuthority>0){
                        if(max<userAuthority%10)
                            max=userAuthority%10;
                        userAuthority/=10;
                    }
                    if(max<submitLeaveInfo.getApprovalStatus())
                        flag = false;
                }
            }

        }
        return flag;
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
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if(inputMethodManager!=null&&inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(ApprovalLeaveInfoActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            ed_reason.setCursorVisible(false);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public GetApprovalRecordPresent getPersenter() {
        return new GetApprovalRecordPresent(this);
    }

}
