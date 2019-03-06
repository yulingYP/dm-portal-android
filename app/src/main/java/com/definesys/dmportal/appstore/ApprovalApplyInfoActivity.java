package com.definesys.dmportal.appstore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.ApplyRecord;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.presenter.ApplyInfoPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

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
    @BindView(R.id.apply_conent)
    TextView tv_content;
    @BindView(R.id.apply_resson)
    TextView tv_reason;
    @BindView(R.id.submit_time)
    TextView tv_date;
    @BindView(R.id.userId_text)
    TextView tv_id;
    @Autowired(name = "applyInfo")
    ApplyInfo applyInfo;

    @Autowired(name = "applyId")
    String applyId;
    @Autowired(name = "type")
    int type;//0.不同意 1.同意 4.未审批
    @Autowired(name = "content")
    String approvalContent;//审批内容
    @Autowired(name = "date")
    Date approvalDate;//审批内容

    private boolean isAgree = true;//是否同意
    ApplyRecord applyRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_apply_info);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        if(type==4&&applyInfo==null){
            progressHUD.show();
            mPersenter.getApplyInfoById(applyId);
            initEdit();
        }else if(type==4){
            initEdit();
            initView();
        }
        else if(type==0||type==1){
         applyRecord = new ApplyRecord();
         applyRecord.setApplyContent(approvalContent);
         applyRecord.setApplyDate(approvalDate==null?new Date():approvalDate);
         isAgree= type==1;
         setAgreeText();
         initEditUnable();
         if(applyInfo==null)
             mPersenter.getApplyInfoById(applyId);
         else
             initView();
        }

//        initView();
//        initEdit();
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
            progressHUD.dismiss();
            initView();
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
            SmecRxBus.get().post("addMessage",new MyMessage(String.valueOf(new Date().getTime()),SharedPreferencesUtil.getInstance().getUserId(),(short)5,approvalContent,(short)(isAgree?1:0),applyInfo.getApplyId(),null,new Date()));
            progressHUD.dismiss();
            finish();
        }
    }
    private void initView() {
        titleBar.setTitle(type==4?getString(R.string.approval_apply_title):getString(R.string.check_approval_apply_title));
        titleBar.setBackgroundDividerEnabled(false);

        //退出
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
        //编号
        tv_id.setText(String.valueOf(applyInfo.getApplyUserId()));
        //姓名
        tv_name.setText(getString(R.string.name_tip,applyInfo.getApplyUserName()));
        //申请内容
        tv_content.setText(getString(R.string.apply_authority,applyInfo.getApplyDetailContent()));
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
                return String.valueOf(userAuthority).contains("" + 2);//辅导员权限
            } else if (authority == 4 || authority == 5 || authority == 6 || authority == 7) {//辅导员/实习/教学/学生工作负责人
                return String.valueOf(userAuthority).contains("" + 8);//院系权限审批负责人
            }
        }else if(applyInfo.getApplyAuthorityType()==1) {//审批老师权限
            return String.valueOf(userAuthority).contains("" + 2);//部门权限审批负责人
        }
        return false;
    }

    private void initResultDialog() {
        Dialog dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.view_approval_result_confirm,null);

        //审批结果
        TextView tv_reult = (TextView) view.findViewById(R.id.approval_result_des);
        tv_reult.setText(isAgree?R.string.agree_tip:R.string.refuse_tip);
        tv_reult.setTextColor(isAgree?getResources().getColor(R.color.green):getResources().getColor(R.color.red_error));

        //审批意见
        approvalContent = ed_reason.getText().toString();
        if("".equals(approvalContent)&&isAgree)
            approvalContent= getString(R.string.agree_tip);
        ((TextView) view.findViewById(R.id.approval_content_text)).setText(getString(R.string.approval_addvise)+":\n "+approvalContent);

        //确定
        RxView.clicks(view.findViewById(R.id.confirm_text))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ApplyRecord applyRecord = new ApplyRecord(applyInfo.getApplyId(), SharedPreferencesUtil.getInstance().getUserId().intValue(),
                            (short)(isAgree?1:0),approvalContent.trim());
                    mPersenter.submitApplyResult(applyRecord);
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

    //具体原因编辑框设置
    private void initEdit() {
        tv_count.setText(getString(R.string.word_count, 0));
        tv_approvalContent.setVisibility(GONE);
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
        tv_approvalContent.setText(applyRecord.getApplyContent());
        tv_count.setText(getString(R.string.approval_time, DensityUtil.dateTypeToString(getString(R.string.date_type_2),applyRecord.getApplyDate())));
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
    /**
     * 延时发送页面滑动消息
     * @param position 活动到的位置
     */
    private void sendScrollMessage(int position) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("mydemo","Height=="+lg_scroll.getMeasuredHeight());
                lg_scroll.fullScroll(position);
            }
        }, Constants.scrollDelay);
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if(inputMethodManager!=null&&inputMethodManager.isActive()){
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
