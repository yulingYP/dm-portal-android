package com.definesys.dmportal.appstore;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.customViews.ApplyDialog;
import com.definesys.dmportal.appstore.presenter.LeaveAuthorityPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;


//更新请假权限
@Route(path = ARouterConstants.UpdateLeAutActivity)
public class UpdateLeAutActivity extends BaseActivity<LeaveAuthorityPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @BindView(R.id.tea_layout)
    RelativeLayout lg_tea;
    @BindView(R.id.stu_layout)
    RelativeLayout lg_stu;
    @BindView(R.id.stu_authority_layout)
    LinearLayout lg_stuAut;
    @BindView(R.id.tea_authority_layout)
    LinearLayout lg_teaAut;
    @BindView(R.id.apply_text)
    TextView tv_show;
    @BindView(R.id.count_word_text)
    TextView tv_count;

    @BindView(R.id.ed_reason)
    EditText ed_reason;

    @BindView(R.id.des_layout)
    RelativeLayout lg_des;

    @BindView(R.id.down_icon)
    ImageView iv_down;

    @BindView(R.id.tea_down_icon)
    ImageView iv_tea;

    @BindView(R.id.stu_down_icon)
    ImageView iv_stu;

    @BindView(R.id.scorll_view)
    ScrollView lg_sc;

    private ImageView iv_selected;//选择位置的imageview控件
    private List<ApplyInfo>applyList;//申请的权限列表
    private String content="";//保存成员内容
    private ApplyDialog tempDialog;//
    private boolean isSingleLine = false;//单行显示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_le_aut);
        ButterKnife.bind(this);
        content=getString(R.string.no_des);
        initView();
        initEdit();//具体原因编辑框
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.apply_title));
        titleBar.setBackgroundDividerEnabled(false);
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    finish()
                );
        Button button = titleBar.addRightTextButton(getString(R.string.submit), R.layout.activity_update_le_aut);
        button.setTextSize(14);
        //提交
        RxView.clicks(button)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                    checkSelect()
                );
        //点击申请内容
        RxView.clicks(lg_des)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> {
                    if(!isSingleLine){//显示一行
                        tv_show.setSingleLine(true);
                        iv_down.setRotation(0);
                        isSingleLine = true;
                    }else {//全部显示
                        tv_show.setSingleLine(false);
                        iv_down.setRotation(180);
                        isSingleLine = false;
                    }
                });

        //点击审批学生权限
        RxView.clicks(lg_stu)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> {
                    if(lg_stuAut.getVisibility()==View.GONE){//显示全部可选权限
                        lg_stuAut.setVisibility(View.VISIBLE);
                        iv_stu.setRotation(180);
                    }else {//隐藏
                        lg_stuAut.setVisibility(View.GONE);
                        iv_stu.setRotation(0);
                    }
                });

        iv_stu.setRotation(180);
        iv_down.setRotation(180);
        //审批学生权限
        initAuthorityList(getResources().getStringArray(R.array.approverType),lg_stuAut,0, SharedPreferencesUtil.getInstance().getUserType()==0?0:2,SharedPreferencesUtil.getInstance().getUserType()==0?8:2);
        if(SharedPreferencesUtil.getInstance().getUserType()!=1){//不是教师
            lg_tea.setVisibility(View.GONE);
            lg_teaAut.setVisibility(View.GONE);
        }else {
            //审批教师权限
            initAuthorityList(getResources().getStringArray(R.array.approverType_2),lg_teaAut,1,0,2);
            //点击审批教师权限
            RxView.clicks(lg_tea)
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        if(lg_teaAut.getVisibility()==View.GONE){//显示全部可选权限
                            lg_teaAut.setVisibility(View.VISIBLE);
                            iv_tea.setRotation(180);
                        }else {//隐藏
                            lg_teaAut.setVisibility(View.GONE);
                            iv_tea.setRotation(0);
                        }
                    });
            iv_tea.setRotation(180);
        }
    }
    //具体原因编辑框设置
    private void initEdit() {
        tv_count.setText(getString(R.string.word_count, 0));
        RxView.clicks(ed_reason)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    ed_reason.setCursorVisible(true);
                    sendScrollMessage(ScrollView.FOCUS_DOWN);
                });
        //获取焦点
        ed_reason.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                ed_reason.setCursorVisible(true);
                sendScrollMessage(ScrollView.FOCUS_DOWN);
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
    /**
     * 延时发送页面滑动消息
     * @param position 活动到的位置
     */
    private void sendScrollMessage(int position) {
        new Handler().postDelayed(() -> {
            Log.d("mydemo","Height=="+lg_sc.getMeasuredHeight());
            lg_sc.fullScroll(position);
        }, Constants.scrollDelay);
    }
    //合法性检测
    private void checkSelect() {
        if(applyList==null||applyList.size()==0) {
            Toast.makeText(this, R.string.apply_error_tip_8, Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(ed_reason.getText().toString())) {
            Toast.makeText(this, R.string.apply_error_tip_9, Toast.LENGTH_SHORT).show();
            lg_sc.fullScroll(ScrollView.FOCUS_DOWN);
            ed_reason.setFocusable(true);
            ed_reason.setFocusableInTouchMode(true);
            ed_reason.requestFocus();
            ed_reason.findFocus();
            return;
        }
        List<String> list = new ArrayList<>();
        for(int i = 0 ;i <applyList.size();i++){
            list.add(applyList.get(i).getApplyDetailContent());
        }
        initDialog(list,100);
//       showReasonDialog();
    }
    /**
     * 初始化学生和教师权限列表
     * @param approvalers 审批权限类型数组
     * @param lg_aut 初始化的列表
     * @param type 0.审批学生权限 1.审批教师权限
     *@param start approvalers 数组偏移值
     * @param end approvalers 数组偏移值
     */
    private void initAuthorityList(String[] approvalers,LinearLayout lg_aut,int type,int start,int end) {
        for(int i = start; i<approvalers.length-end;i++)
            lg_aut.addView(addItemView(lg_aut,approvalers[i],i,type));

    }
    private View addItemView(LinearLayout parent, String content, int authority,int type) {
        View view= LayoutInflater.from(this).inflate(R.layout.item_sign_type_view,parent,false);
        TextView textView=view.findViewById(R.id.name_text);//权限名称
        ImageView imageView=view.findViewById(R.id.select_img);//是否选择
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        textView.setText(content);
        RxView.clicks(view)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if(iv_selected!=null)
                        iv_selected.setImageResource(R.drawable.no_select);
                    iv_selected = imageView;
                    imageView.setImageResource(R.drawable.right_icon);
                    showMyDialog(authority,type);
                });
        return view;
    }

    /**
     * 展示dialog
     * @param authority 权限
     * @param type 0.审批学生权限 1.审批教师权限
     */
    private void showMyDialog(int authority, int type) {
        if(type==0){//审批学生
            if(SharedPreferencesUtil.getInstance().getUserType()==1&&(authority==0||authority==1)){//教师
                Toast.makeText(this, R.string.apply_error_tip_2,Toast.LENGTH_SHORT).show();
                return;
            }
            if(authority==0||authority==1){//寝室长或班长
                    //获取班级名称
                    if(SharedPreferencesUtil.getInstance().getFaculty()==null||"".equals(SharedPreferencesUtil.getInstance().getFaculty())) {
                        Toast.makeText(this, R.string.apply_error_tip_3, Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        mPersenter.getApplyList(SharedPreferencesUtil.getInstance().getUserId(), SharedPreferencesUtil.getInstance().getFaculty(), authority==0?0:2);
                    }
            }else if(authority==2||authority==3){//班主任、毕设老师
                mPersenter.getApplyList(0, "", authority==2?3:5);
            }else if(authority==4){//辅导员
                mPersenter.getApplyList(0, "", 8);
            }else if(authority==5||authority==6||authority==7){//实习、学生、教学工作负责人
                mPersenter.getApplyList(0, "", authority+5);
            }
        }else if(type==1){//审批教师
            if(SharedPreferencesUtil.getInstance().getUserType()==0){//学生
                Toast.makeText(this, R.string.apply_error_tip_6,Toast.LENGTH_SHORT).show();
                return;
            }
            mPersenter.getApplyList(0, "", authority==0?20:21);

        }
        progressHUD.show();
    }

    /**
     * 获取申请权限的信息列表失败
     * @param msg 失败消息
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            progressHUD.dismiss();
            Toast.makeText(this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取申请权限的信息列表成功
     * @param data BaseResponse
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_APPLY_LIST_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getDetailInfo(BaseResponse<List<String>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this) {
            progressHUD.dismiss();
            if(data.getExtendInfo()==100) {
                finish();
//                data.getData(),leaveInfo.getUserId(), (short) 2, content, (short)(isAgree?1:0) ,leaveInfo.getId(),null,new Date() )
                SmecRxBus.get().post("addMessage",new MyMessage(String.valueOf(new Date().getTime()),SharedPreferencesUtil.getInstance().getUserId(),(short)11,"",null,"",new Date()));
                Toast.makeText(this, R.string.submit_success, Toast.LENGTH_SHORT).show();
            }
            else
                initDialog(data.getData(),data.getExtendInfo());
        }
    }

    /**
     *
     * @param data data
     * @param type 0.寝室长权限根据facultyId获取班级名称 1.根据班级id获取班级名单 2.班长权限 根据facultyId获取班级名称
     */
    private void initDialog(List<String> data, int type) {
        ApplyDialog applyDialog = new ApplyDialog(this,data,type);
        applyDialog.setOnItemClickListener(position -> {
            if(type==0){//获取该院系所有班级的id
                tempDialog = applyDialog;
                progressHUD.show();
                mPersenter.getApplyList(Integer.valueOf(data.get(position))*100,""+SharedPreferencesUtil.getInstance().getUserSex(),1);
            }else if(type==3){//获取所有院系的名称
                mPersenter.getApplyList(0,data.get(position),4);
                progressHUD.show();
                applyDialog.dismiss();
            }else if(type==5){//获取该院系所有班级的id
                mPersenter.getApplyList(0,data.get(position),6);
                progressHUD.show();
                applyDialog.dismiss();
            }else if(type==6){//获取班级全部成员
                tempDialog = applyDialog;
                progressHUD.show();
                mPersenter.getApplyList(0,data.get(position),7);
            }else if(type==8){//获取该院系所有班级的id
                mPersenter.getApplyList(0,data.get(position),9);
                progressHUD.show();
                applyDialog.dismiss();
            }
        });

        //点击确定
        applyDialog.setOnConfirmClickListener(() -> {
            if (type == 0||type==6) {//寝室长权限
                if(getString(R.string.no_des).equals(content)){//未填写内容
                    Toast.makeText(UpdateLeAutActivity.this, type == 0?R.string.apply_error_tip_4:R.string.apply_error_tip_10,Toast.LENGTH_SHORT).show();
                }else{
                    tv_show.setText(checkApplyAuthority(type,content.substring(0,content.length()-2)));
                    applyDialog.dismiss();
                    content = getString(R.string.no_des);
                }
            } else if(type==1||type==4||type==7||type==9) {//班级成员选择 选择班级
//                    if (initText(data, applyDialog.getApplyAuthorityAdapter().getSelectList(), type))
                if(checkContent(data, applyDialog.getApplyAuthorityAdapter().getSelectList(),type)) {
                    applyDialog.dismiss();
                    if(type==1||type==7)
                        tempDialog.setContent(content);
                    else  {
                        tv_show.setText(checkApplyAuthority(type, content.substring(0,content.length()-2)));
                        content = getString(R.string.no_des);
                    }

                }
            } else if(type==2||type==10||type==11||type==12||type==20||type==21) {//班长、实习、学生、教学工作负责人、部门请假负责人、部门教学管理人权限
                if(applyDialog.getApplyAuthorityAdapter().getSelectPosition()!=-1) {
                    tv_show.setText(checkApplyAuthority(type,data.get(applyDialog.getApplyAuthorityAdapter().getSelectPosition())));
                    applyDialog.dismiss();
                }else {
                    if(type==2)
                        Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_5, Toast.LENGTH_SHORT).show();
                    else if(type==10||type==11||type==12)
                        Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_12, Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_7, Toast.LENGTH_SHORT).show();
                }
            }else if(type==100){//权限提交
                applyDialog.dismiss();
                progressHUD.show();
                mPersenter.submitAuthoritiesApply(applyList,ed_reason.getText().toString().trim());
            }
        });
        //点击取消
        applyDialog.setOnCancelClickListener(() -> {
            applyDialog.dismiss();
            if(type==0||type==5)
                content = getString(R.string.no_des);
        });
        applyDialog.show();
        if(type==0||type==6)
            applyDialog.setContent(content);
    }

    /**
     * 检查学号是否重复
     * @param data 数据
     * @param selectList 是否选择的记录list
     * @param type 提示框类型
     */
    private boolean checkContent(List<String> data, List<Boolean> selectList, int type) {
        boolean flag = false;//是否有选中内容
//        if(type==4) {
//            HashSet<String> hashSet = new HashSet<>();
//            for (int i = 0;i<selectList.size();i++){
//                if(selectList.get(i)){//被选中
//                    hashSet.add(data.get(i).length() > 2 ? data.get(i).substring(0, 2) : data.get(i));
//                }
//            }
//            if(hashSet.size()>1){//选择了多个年级的班级
//                Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_5, Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        }
        for (int i = 0;i<selectList.size();i++){
            if(selectList.get(i)){//被选中
                if(content.equals(getString(R.string.no_des)))//第一次添加
                    content="";
                if(!content.contains(data.get(i)))//未被选择过
                    content = content+data.get(i)+", ";
                flag = true;
            }
        }
        if(!flag){
            if(type==4)
                Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_5, Toast.LENGTH_SHORT).show();
            else if(type==7)  Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_11, Toast.LENGTH_SHORT).show();
            else if(type==9)  Toast.makeText(UpdateLeAutActivity.this, R.string.apply_error_tip_5, Toast.LENGTH_SHORT).show();
            else  Toast.makeText(this, R.string.apply_error_tip_1,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 检查该权限是否已经加入列表
     * 并返回结果
     */
    private String checkApplyAuthority(int type ,String content) {
        if(applyList==null){//第一次写入
            tv_show.setGravity(Gravity.START);
            tv_show.setText("");
            applyList = new ArrayList<>();
        }

        String checkStr="";
//        boolean flag = false;//是否已经重写
        if(type==0) checkStr = getString(R.string.apply_tag_1);//寝室长权限 寝室成员:
        else if(type==2)checkStr = getString(R.string.apply_tag_2);//班长权限
        else if(type==4)checkStr = getString(R.string.apply_tag_3);//班主任权限
        else if(type==6)checkStr = getString(R.string.apply_tag_4);//毕设老师权限
        else if(type==9)checkStr = getString(R.string.apply_tag_5);//辅导员权限
        else if(type==10)checkStr = getString(R.string.apply_tag_6);//辅导员权限
        else if(type==11)checkStr = getString(R.string.apply_tag_7);//辅导员权限
        else if(type==12)checkStr = getString(R.string.apply_tag_8);//辅导员权限
        else if(type==20)checkStr = getString(R.string.apply_tag_9);//部门请假负责人权限
        else if(type==21)checkStr = getString(R.string.apply_tag_10);//部门教学负责人权限
        addItem(type,content,checkStr);
//        for(int i=0;i<applyList.size();i++ ){
//            if(applyList.get(i).getType()==type) {//已加入过，则重写这部分内容
//                applyList.get(i).setContent(checkStr + content);
//                flag = true;
//                break;
//            }
//        }
//        if(!flag){//没有重写过
//            applyList.get(applyList.size()-1).setContent(checkStr + content);
//        }
        StringBuilder result = new StringBuilder();
        for(int i=0;i<applyList.size();i++ ){
            result.append(applyList.get(i).getApplyDetailContent());
            if(i!=applyList.size()-1)
                result.append(" \n");
        }
       return result.toString();
    }

    /**
     * 获取新的对象
     * @param type  type
     * @param  title 标题
     *@param content  @return
     */
    private void addItem(int type, String content,String title) {
        ApplyInfo  applyInfo=null;
        boolean flag = false;//是否已经添加过
        int position ;
        for(position = 0;position<applyList.size();position++){
            if(applyList.get(position).getType()==type) {//已加入过，则重写这部分内容
                applyList.get(position).setApplyDetailContent( title + content);
//                if(type==0||type==4||type==6||type==9)
                    applyList.get(position).setApplyReason(content);
//                else
//                    applyList.get(position).setSignId(content);
                flag = true;
                break;
            }
        }
        if(!flag) {//没有添加过
            String applyId = ""+SharedPreferencesUtil.getInstance().getUserId()+ new Date().getTime();
//            String applyId, Integer applyUserId, Integer applyAuthorityType, Integer applyAuthority,
//            String applyRegion, Short applyStatus, int type
            if (type == 0)
                applyInfo = new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,0,content,type);//寝室长权限 寝室成员:
            else if (type == 2)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,1,content,type);//班长权限
            else if (type == 4)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,2,content,type);//班主任权限
            else if (type == 6)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,3,content,type);//毕设老师权限
            else if (type == 9)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,4,content,type);//辅导员权限
            else if (type == 10)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,5,content,type);//实习负责人权限
            else if (type == 11)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,6,content,type);//学生工作负责人权限
            else if (type == 12)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),0,7,content,type);//教学院长权限
            else if (type == 20)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),1,0,content,type);//部门请假负责人权限
            else if (type == 21)
                applyInfo =  new ApplyInfo(applyId,SharedPreferencesUtil.getInstance().getUserId().intValue(),1,1,content,type);//部门教学负责人权限
            if(applyInfo!=null) {
                applyInfo.setApplyDetailContent(title + content);
                applyInfo.setApplyUserName(SharedPreferencesUtil.getInstance().getUserName());
                applyList.add(applyInfo);
            }
        }

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if(inputMethodManager!=null&&inputMethodManager.isActive()&&this.getCurrentFocus()!=null){
                inputMethodManager.hideSoftInputFromWindow(UpdateLeAutActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            ed_reason.setCursorVisible(false);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public LeaveAuthorityPresenter getPersenter() {
        return new LeaveAuthorityPresenter(this);
    }
}
