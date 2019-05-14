package com.definesys.dmportal.appstore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.definesys.dmportal.appstore.presenter.ApplyInfoPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@Route(path = ARouterConstants.ApplyInfoActivity)
public class ApplyInfoActivity extends BaseActivity<ApplyInfoPresenter>{
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.name_text)
    TextView tv_name;

    @BindView(R.id.no_layout)
    LinearLayout lg_no;
    @BindView(R.id.layout_scroll)
    ScrollView lg_scoll;
    @BindView(R.id.aut_type)
    TextView tv_type;
    @BindView(R.id.aut_region)
    TextView tv_region;
    @BindView(R.id.reason_text)
    TextView tv_reason;
    @BindView(R.id.submit_time)
    TextView tv_date;
    @BindView(R.id.status_text)
    TextView tv_status;
    @BindView(R.id.check_approval_layout)
    LinearLayout lg_check;
    @BindView(R.id.atu_change_tip)
    TextView tv_tip;
    @Autowired(name = "applyInfo")
    ApplyInfo applyInfo;
    @Autowired(name = "applyId")
    long applyId;
    @Autowired(name = "isMsg")
    boolean isMsg;//是不是从消息页或点击推送进入的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_info);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        setNoLayout(true);
        if(applyInfo!=null)
            initView();
        else if(applyId>0){
            progressHUD.show();
            mPersenter.getApplyInfoById(applyId);
        }

    }

    private void initView() {
        setNoLayout(false);
        titleBar.setTitle(getString(R.string.apply_detail_info));
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    finish()
                );
        //姓名
        tv_name.setText(getString(R.string.name_tip,applyInfo.getApplyUserName()));
        //权限类型
        tv_type.setText(getString(R.string.apply_authority,DensityUtil.getAuthorityName(this,applyInfo.getApplyAuthorityType()>0?(short)(applyInfo.getApplyAuthority()+applyInfo.getApplyAuthorityType()*10):applyInfo.getApplyAuthority().shortValue())));
        //权限范围
        tv_region.setText(applyInfo.getApplyStatus()>=0?getString(R.string.authority_region,applyInfo.getApplyRegion()):getString(R.string.authority_delete,applyInfo.getApplyRegion()));
        //申请原因
        tv_reason.setText(getString(R.string.authority_reason,applyInfo.getApplyReason()));
        //申请时间
        tv_date.setText(getString(R.string.submit_time, DensityUtil.dateTypeToString(getString(R.string.date_type_2),applyInfo.getApplyDate())));
        //审批状态
        tv_status.setText(DensityUtil.getApplyStatus(this,applyInfo.getApplyStatus()));
        DensityUtil.setTVcolor(tv_status.getText().toString(),tv_status,this);

        //查看审批记录
        RxView.clicks(lg_check)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj-> {
                    progressHUD.show();
                    mPersenter.getApplyRecordById(applyInfo.getApplyId());
                });
        tv_reason.setVisibility("".equals(applyInfo.getApplyReason())?GONE:VISIBLE);
        lg_check.setVisibility(applyInfo.getApplyStatus()>=0?VISIBLE:GONE);
        if(isMsg||applyInfo.getApplyStatus()<0){
            initTip();
        }
    }

    private void initTip() {

        boolean flag = applyInfo.getApplyUserId() == SharedPreferencesUtil.getInstance().getUserId().intValue();
        String content=null;
        int startPosition = -1;
        int colorId = 0;
        if(flag){//本人查看
            if(applyInfo.getApplyStatus() == 100){//添加权限成功
                content=getString(R.string.change_authority_des_2,DensityUtil.getAuthorityName(this,(short)(applyInfo.getApplyAuthority()+applyInfo.getApplyAuthorityType()*10)),applyInfo.getApplyRegion());
                startPosition = content.indexOf(getString(R.string.add));
                colorId= getResources().getColor(R.color.green);
            }else if(applyInfo.getApplyStatus() == -100){//修改成功
                content=getString(R.string.change_authority_des_1,DensityUtil.getAuthorityName(this,applyInfo.getApplyAuthority().shortValue()),applyInfo.getApplyRegion());
                startPosition = content.indexOf(getString(R.string.delete));
                colorId= getResources().getColor(R.color.red_error);
            }else if(applyInfo.getApplyStatus() == -110){//删除成功
                content=getString(R.string.change_authority_des_3,DensityUtil.getAuthorityName(this,applyInfo.getApplyAuthority().shortValue()));
                startPosition = content.indexOf(getString(R.string.delete));
                colorId = getResources().getColor(R.color.red_error);
            }
        }else {//不是本人查看
            if(applyInfo.getApplyStatus() == 100 && applyInfo.getApplyAuthorityType() >= 0){//申请人添加权限 则另一个人删除权限
                content=getString(R.string.change_authority_des_1,DensityUtil.getAuthorityName(this,(short)(applyInfo.getApplyAuthority()+applyInfo.getApplyAuthorityType()*10)),applyInfo.getApplyRegion());
                startPosition = content.indexOf(getString(R.string.delete));
                colorId= getResources().getColor(R.color.red_error);
            }else if(applyInfo.getApplyAuthorityType() == -1){//申请人删除权限 则另外一个人添加权限
                content=getString(R.string.change_authority_des_2,DensityUtil.getAuthorityName(this,applyInfo.getApplyAuthority().shortValue()),applyInfo.getApplyRegion());
                startPosition = content.indexOf(getString(R.string.add));
                colorId= getResources().getColor(R.color.green);
            }
        }
        if(content==null)
            return;
        tv_tip.setVisibility(VISIBLE);
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(content);

        //设置部分文字点击事件
        style.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ARouter.getInstance().build(ARouterConstants.AuthoritySettingActivity).navigation();
            }
        }, style.length() - 7, style.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_tip.setText(style);

        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.blue));
        style.setSpan(foregroundColorSpan, style.length() - 7, style.length() - 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //给添加||删除设置字体颜色
        if(startPosition>=0){
            ForegroundColorSpan foregroundColorSpan_ = new ForegroundColorSpan(colorId);
            style.setSpan(foregroundColorSpan_, startPosition, startPosition + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        //配置给TextView
        tv_tip.setMovementMethod(LinkMovementMethod.getInstance());
        tv_tip.setText(style);
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
            if(applyInfo!=null)
                initView();
            else
                setNoLayout(false);
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
            progressHUD.dismiss();
            if(data.getData()!=null&&data.getData().size()>0)
                showMyDialog(data.getData());//显示提示框
            else
                Toast.makeText(this, ("".equals(data.getMsg())?getString(R.string.net_work_error):data.getMsg()),Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("InflateParams")
    private void showMyDialog(List<ApplyRecord> datas) {
         Dialog dialog = new Dialog(this);
         View view=LayoutInflater.from(this).inflate(R.layout.dialog_apply_approval_info_view,null);
         LinearLayout parent = view.findViewById(R.id.parent_layout);
         for(ApplyRecord data:datas) {
             View itemView=LayoutInflater.from(this).inflate(R.layout.item_approval_record,parent,false);
             //审批人
             ((TextView) itemView.findViewById(R.id.name_text)).setText(getString(R.string.approver_text, getApprovalerName(data)));
             //审批内容
             ((TextView) itemView.findViewById(R.id.content_text)).setText(getString(R.string.approval_content_text, data.getApplyContent()));
             //审批结果
             ((TextView) itemView.findViewById(R.id.result_text)).setText(Html.fromHtml(getString(R.string.approval_result_text, data.getApplyStatus() == 0 ? "<font color='#ff4444'>不同意</font>" : "<font color='#7cb342'>同意</font>")));
             //审批时间
             ((TextView) itemView.findViewById(R.id.time_text)).setText(getString(R.string.approval_time_text, DensityUtil.dateTypeToString(getString(R.string.date_type_2), data.getApprovalDate())));
             parent.addView(itemView);
         }
        //点击确定
        RxView.clicks(view.findViewById(R.id.confirm_text))
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o ->
                    dialog.dismiss()
                );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        if(dialog.getWindow()!=null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }
    private String getApprovalerName(ApplyRecord data){

//        if(applyInfo.getApplyAuthorityType()==0){//审批学生权限
//            String[]  arr = getResources().getStringArray(R.array.approverType);
//            if(applyInfo.getApplyAuthority()==0)//寝室长
//                return arr[1];//班长
//            else if(applyInfo.getApplyAuthority()==1)//班长
//                return arr[2];//班主任
//            else if(applyInfo.getApplyAuthority()==2||applyInfo.getApplyAuthority()==3)//班主任 毕设老师权限
//                return arr[4];//辅导员
//            else if(applyInfo.getApplyAuthority()==4||applyInfo.getApplyAuthority()==5||applyInfo.getApplyAuthority()==6||applyInfo.getApplyAuthority()==7)//辅导员/实习/学生/教学负责人
//                return arr[8];//权限审批负责人
//
//        }else if(applyInfo.getApplyAuthorityType()==1) {//审批教师权限
//            return getResources().getStringArray(R.array.approverType_2)[2];
//        }
       return "".equals(data.getApproverName())?data.getApproverId().toString():data.getApproverName();
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
    public ApplyInfoPresenter getPersenter() {
        return new ApplyInfoPresenter(this);
    }
}
