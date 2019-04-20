package com.definesys.dmportal.appstore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.ApplyRecord;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.presenter.ApplyInfoPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.HddLayoutHeight;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@Route(path = ARouterConstants.ApprovalApplyInfoActivity)
public class ApprovalApplyInfoActivity extends BaseActivity<ApplyInfoPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @BindView(R.id.layout_scroll)
    ScrollView lg_scroll;
    @BindView(R.id.count_word_text)
    TextView tv_count;
    @BindView(R.id.ed_reason)
    EditText ed_reason;
    @BindView(R.id.approval_content_text)
    TextView tv_approvalContent;
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
    @BindView(R.id.name_text)
    TextView tv_name;
    @BindView(R.id.apply_type)
    TextView tv_type;
    @BindView(R.id.apply_region)
    TextView tv_region;
    @BindView(R.id.apply_resson)
    TextView tv_reason;
    @BindView(R.id.submit_time)
    TextView tv_date;
    @BindView(R.id.userId_text)
    TextView tv_id;
    @BindView(R.id.mainview)
    LinearLayout main;
    @Autowired(name = "applyInfo")
    ApplyInfo applyInfo;

    @Autowired(name = "applyId")
    String applyId;
    @Autowired(name = "approverId")
    int approverId;//审批人id
    @Autowired(name = "type")
    int type;//0.不同意 1.同意 4.未审批
    @Autowired(name = "content")
    String approvalContent;//审批内容
    @Autowired(name = "date")
    Date approvalDate;//审批内容

    private boolean isAgree = true;//是否同意
    private ApplyRecord applyRecord;
    private int requestCount = 0;//请求数量
    private boolean isInit=false;//是否初始化界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_apply_info);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        if(type==4&&applyInfo!=null){//未审批
            initEdit();
            initView();
        }else if(type==4){//未审批
            progressHUD.show();
            ++requestCount;
            //申请记录
            mPersenter.getApplyInfoById(applyId);

            ++requestCount;
            //查看是否已有审批记录
            mPersenter.getApplyRecordById(applyId);
        }else if(type==0||type==1){//已审批
         applyRecord = new ApplyRecord();
         applyRecord.setApplyContent(approvalContent);
         applyRecord.setApprovalDate(approvalDate==null?new Date():approvalDate);
         applyRecord.setApproverId(approverId);
         isAgree= type==1;
         setAgreeText();
         initEditUnable();
         if(applyInfo==null) {
             ++requestCount;
             mPersenter.getApplyInfoById(applyId);
         }
         else
             initView();
         if(approvalDate==null){
                Toast.makeText(this, R.string.approval_addvise_tip_5,Toast.LENGTH_SHORT).show();
         }
        }

    }
    /**
     * 获取权限详细信息失败
     * @param msg 失败消息
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            if(--requestCount<=0)
                progressHUD.dismiss();
        }
    }
    /**
     * 获取权限详细信息成功
     * @param data BaseResponse
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPLY_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getApplyInfo(BaseResponse<ApplyInfo> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            applyInfo=data.getData();
            if(--requestCount<=0){
                progressHUD.dismiss();
            }
            if(applyInfo ==null){//申请信息为空
                Toast.makeText(this, data.getMsg(),Toast.LENGTH_SHORT).show();
            }else {
                if(applyRecord!=null&type!=4) {//审批信息也不为空
                    initView();
                    initEditUnable();
                }
                else if(type==4&&isInit){//可以初始化界面
                    initView();
                    initEdit();
                }
            }
        }
    }
    /**
     * 获取审批信息成功
     * @param data BaseResponse
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPLY_RECORD_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getApplyRecord(BaseResponse<List<ApplyRecord>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            if(--requestCount<=0){
                progressHUD.dismiss();
            }
            if(data.getData()==null||data.getData().size()==0){//没有审批信息
                if(applyInfo!=null){//申请信息不为空
                    initView();
                    initEdit();
                }else//申请信息还未获取到
                    isInit = true;//置可以初始化
            }else {//有审批记录
                applyRecord = data.getData().get(0);
                type = applyRecord.getApplyStatus();
                isAgree = type==1;
                initEditUnable();
                if (applyInfo != null)//申请信息不为空
                    initView();//初始化界面
            }

        }
    }
    /**
     * 提交结果成功
     * @param data BaseResponse
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_SUBMIT_APPLY_RESULT)
    }, thread = EventThread.MAIN_THREAD)
    public void submitSuccess(BaseResponse<String> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this, R.string.submit_alyapr_success,Toast.LENGTH_SHORT).show();
            SmecRxBus.get().post("addMessage",new MyMessage(String.valueOf(new Date().getTime()),SharedPreferencesUtil.getInstance().getUserId(),(short)5,approvalContent,(short)(isAgree?1:0),applyInfo.getApplyId(),new Date()));
            SmecRxBus.get().post("updateList", applyInfo.getApplyId());
            if(--requestCount<=0){
                progressHUD.dismiss();
            }
            finish();
        }
    }
    private void initView() {
        titleBar.setTitle(type==4?getString(R.string.approval_apply_title):getString(R.string.check_approval_apply_title));
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
        //编号
        tv_id.setText(String.valueOf(applyInfo.getApplyUserId()));
        //姓名
        tv_name.setText(getString(R.string.name_tip,applyInfo.getApplyUserName()));
        //权限类型
        tv_type.setText(getString(R.string.apply_authority,DensityUtil.getAuthorityName(this,(short)(applyInfo.getApplyAuthority()+applyInfo.getApplyAuthorityType()*10))));
        //管理范围
        tv_region.setText(getString(R.string.authority_region,applyInfo.getApplyRegion()));
        //申请原因
        tv_reason.setText(getString(R.string.authority_reason,applyInfo.getApplyReason()));
        //申请时间
        tv_date.setText(getString(R.string.submit_time, DensityUtil.dateTypeToString(getString(R.string.date_type_2),applyInfo.getApplyDate())));

        if(type==4) {
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
            Button button = titleBar.addRightTextButton(getString(R.string.submit), R.layout.activity_approval_leave_info);
            button.setTextSize(14);
            //提交
            RxView.clicks(button)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> checkContent());
        }
        setAgreeText();
    }
    //规范性检查
    private void checkContent() {
        if("".equals(ed_reason.getText().toString())&&!isAgree) {//不同意且未输入审批意见
            Toast.makeText(this, R.string.approval_addvise_tip_2, Toast.LENGTH_SHORT).show();
            lg_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            ed_reason.setFocusable(true);
            ed_reason.setFocusableInTouchMode(true);
            ed_reason.requestFocus();
            ed_reason.findFocus();
            ed_reason.setCursorVisible(true);
//            InputMethodManager inputMethodManager = ( InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//            if(inputMethodManager!=null){
//                inputMethodManager.showSoftInput(ed_reason,0);
//            }
            return;
        }

        if(!checkAuthority()) {
            Toast.makeText(this, R.string.approval_addvise_tip_4, Toast.LENGTH_SHORT).show();
            return;
        }
        initResultDialog();

    }
    //检查用户是否有审批权限
    private boolean checkAuthority() {
        int authority = applyInfo.getApplyAuthority();//申请的权限
        //用户权限
        int userAuthority = applyInfo.getApplyAuthorityType()==0?SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority():SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();
        if(userAuthority<=0){
          return false;
        }
        if(applyInfo.getApplyAuthorityType()==0) {//审批学生权限
            if (authority == 0) {//寝室长
                return String.valueOf(userAuthority).contains("" + 1);//班长权限
            } else if (authority == 1) {//班长
                return String.valueOf(userAuthority).contains("" + 2);//班主任权限
            } else if (authority == 2 || authority == 3) {//班主任/毕设老师
                return String.valueOf(userAuthority).contains("" + 4);//辅导员权限
            } else if (authority == 4 || authority == 5 || authority == 6 || authority == 7) {//辅导员/实习/教学/学生工作负责人
                return String.valueOf(userAuthority).contains("" + 8);//院系权限审批负责人
            }
        }else if(applyInfo.getApplyAuthorityType()==1) {//审批老师权限
            return String.valueOf(userAuthority).contains("" + 2);//部门权限审批负责人
        }
        return false;
    }

    @SuppressLint("SetTextI18n")
    private void initResultDialog() {
        Dialog dialog = new Dialog(this);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.view_approval_result_confirm,null);

        //审批结果
        TextView tv_result = view.findViewById(R.id.approval_result_des);
        tv_result.setText(isAgree?R.string.agree_tip:R.string.refuse_tip);
        tv_result.setTextColor(isAgree?getResources().getColor(R.color.green):getResources().getColor(R.color.red_error));

        //审批意见
        approvalContent = ed_reason.getText().toString();
        if("".equals(approvalContent)&&isAgree)
            approvalContent= getString(R.string.agree_tip);
        ((TextView) view.findViewById(R.id.approval_content_text)).setText(getString(R.string.approval_addvise)+":\n "+approvalContent);

        //确定
        RxView.clicks(view.findViewById(R.id.confirm_text))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ApplyRecord applyRecord = new ApplyRecord(applyInfo.getApplyId(), applyInfo.getApplyUserId(),applyInfo.getApplyUserName(),SharedPreferencesUtil.getInstance().getUserId().intValue(),
                            (short)(isAgree?1:0),approvalContent.trim());
                    ++requestCount;
                    mPersenter.submitApplyResult(applyRecord);
                    progressHUD.show();
                    dialog.dismiss();
                });
        //取消
        RxView.clicks(view.findViewById(R.id.cancel_text))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    dialog.dismiss()
                );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        if(dialog.getWindow()!=null)
         dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    //具体原因编辑框设置
    private void initEdit() {
        tv_count.setText(getString(R.string.word_count, 0));
        tv_approvalContent.setVisibility(GONE);
        //点击事件
        RxView.clicks(ed_reason)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ed_reason.setCursorVisible(true)
                );
        //获取焦点
        ed_reason.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                ed_reason.setCursorVisible(true);
            }
        });
        /*
        监听输入框内容 《==》 获取输入长度显示到界面
         */
        RxTextView.textChanges(ed_reason)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence ->
                        tv_count.setText(getString(R.string.word_count, ed_reason.getText().toString().length())));
        // 防遮挡
        new HddLayoutHeight().addLayoutListener(this,main, tv_count,1);
    }
    //设置编辑框内容
    private void initEditUnable(){
        ed_reason.setVisibility(GONE);
        tv_approvalContent.setVisibility(VISIBLE);
        tv_approvalContent.setText(applyRecord.getApplyContent());
        if(applyRecord.getApproverId().equals(SharedPreferencesUtil.getInstance().getUserId()))//本人审批
            tv_count.setText(getString(R.string.approval_time, DensityUtil.dateTypeToString(getString(R.string.date_type_2),applyRecord.getApprovalDate())));
        else
            tv_count.setText(getString(R.string.approval_time_2,applyRecord.getApproverId(), DensityUtil.dateTypeToString(getString(R.string.date_type_2),applyRecord.getApprovalDate())));
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if(inputMethodManager!=null&&inputMethodManager.isActive()&&this.getCurrentFocus()!=null){
                inputMethodManager.hideSoftInputFromWindow(ApprovalApplyInfoActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            ed_reason.setCursorVisible(false);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public ApplyInfoPresenter getPersenter() {
        return new ApplyInfoPresenter(this);
    }
}
