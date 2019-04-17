package com.definesys.dmportal.appstore.leaveSettingUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.customViews.ApplyDialog;
import com.definesys.dmportal.appstore.presenter.LeaveAuthorityPresenter;
import com.definesys.dmportal.appstore.tempEntity.AuthorityDetail;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.AnimUtils;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.UserInfoPresent;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.AuthorityChangeActivity)
public class AuthorityChangeActivity extends BaseActivity<LeaveAuthorityPresenter> implements Constants{
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @BindView(R.id.parent_layout)
    LinearLayout lg_parent;
    @BindView(R.id.no_text)
    TextView tv_no;

    private HashMap<Integer,String> deleteMap;//用户要删除的权限<权限，范围>
    private HashMap<Integer, String> autMap;//用户的全部权限<权限，范围>
    private StringBuilder classIds;//同时包含辅导员权限和学生权限负责人权限时，不可删除的班级Id列表
    private short requestCount;//网络请求的个数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authority_change);
        ButterKnife.bind(this);
        initView();
    }
    private void initView() {
        titleBar.setTitle(getString(R.string.delete_authority));
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(clickdelay, TimeUnit.MILLISECONDS)
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
        //获取权限详情
        getMyAuthorityDetail();
    }

    //获取辅导员权限中不可删除的班级id列表
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_NOABLE_CLASS_IDS)
    }, thread = EventThread.MAIN_THREAD)
    public void getNoAbleClassIds(BaseResponse<List<String>> data){
        if(MyActivityManager.getInstance().getCurrentActivity()==this){
            if(data.getData()==null||data.getData().size()==0) {
                if(--requestCount<=0) progressHUD.dismiss();
                return;
            }
            List<String> list = data.getData();
            int size = list.size();
            for(int i = 0 ; i < size ; i++){
                classIds.append(list.get(i)).append(",");
            }
            if(--requestCount<=0){
                //根据map生成权限列表
                initSelectList();
                progressHUD.dismiss();
            }
        }
    }
    /** 获取权限详细信息成功
     * @param data BaseResponse
    */
    @SuppressLint("UseSparseArrays")
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getAllDetailInfoByBean(BaseResponse<List<AuthorityDetail>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            List<AuthorityDetail> list =new ArrayList<>();
            list.addAll(data.getData());//详细信息的list
            if(list.size()>0) {//有数据
                tv_no.setVisibility(View.GONE);
                deleteMap = new HashMap<>();
                autMap = new HashMap<>();
                //把数据赋给权限map
                int authority;
                for (int i = 0; i < list.size(); i++) {
                    authority = list.get(i).getUserAuthority();
                    if (autMap.get(authority) != null && !"".equals(autMap.get(authority)))
                        autMap.put(authority, autMap.get(authority) + list.get(i).getRegion() + ", ");
                    else
                        autMap.put(authority, list.get(i).getRegion() + ", ");
                }
            }else
                tv_no.setVisibility(View.VISIBLE);

            if(--requestCount<=0) {
                //根据map生成权限列表
                initSelectList();
                progressHUD.dismiss();
            }
        }
    }
    //初始化列表
    private void initSelectList(){
        boolean isShow = false;//是否有可显示权限
        for(int i = 0 ;i<12;i++){
            if(autMap.get(i)!=null&&!"".equals(autMap.get(i))&&i!=aut8) {
                initItemView(i, autMap.get(i).split(", "));
                isShow = true;
            }
        }
        if(!isShow)
            tv_no.setVisibility(View.VISIBLE);
    }
    //初始化列表中的单个权限
    private void initItemView(int authority, String[] region) {
        View view= LayoutInflater.from(this).inflate(R.layout.item_authority_view,lg_parent,false);
        //权限详细信息
        LinearLayout itemView= view.findViewById(R.id.item_layout);
        //下箭头
        ImageView downIcon = view.findViewById(R.id.down_icon);
        //权限名称
        ((TextView)view.findViewById(R.id.aut_des)).setText(authority<10?getResources().getStringArray(R.array.approverType)[authority%9]:getResources().getStringArray(R.array.approverType_2)[(authority-10)%3]);
        //获取高度
        final int[] itemViewHeight = new int[1];
        itemView.post(()->
            itemViewHeight[0] =itemView.getMeasuredHeight()
        );
        //点击权限标题 开始动画
        RxView.clicks(view.findViewById(R.id.des_layout))
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->
                    AnimUtils.setInstance(itemView,downIcon,itemViewHeight[0]).toggle(true)
                );
        downIcon.setRotation(180);
        //添加权限的详细信息
        int size = region.length;
        for(int i = 0; i<size ;i++){
            View autView= LayoutInflater.from(this).inflate(R.layout.item_sign_type_view, itemView,false);
            TextView textView=autView.findViewById(R.id.name_text);//权限名称
            ImageView imageView=autView.findViewById(R.id.select_img);//是否选择
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setText(region[i]);
            int upAut;//上级管理权限 0-》1 1-》2 2，3-》4 4，5，6，7-》8 10,11-》12
            if(authority==aut0)upAut=aut1;
            else if(authority==aut1)upAut=aut2;
            else if(authority==aut2||authority==aut3) upAut=aut4;
            else if(authority==aut4||authority==aut5||authority==aut6||authority==aut7) upAut=aut8;
            else if(authority==aut10||authority==aut11) upAut=aut12;
            else upAut=-1;
            String checkStr = authority==aut0||authority==aut3?region[i].substring(0,region[i].length()-2):region[i];
            if(upAut==-1||
               autMap.get(upAut)==null||
               autMap.get(upAut).equals("")||
               (authority!=aut4&&!autMap.get(upAut).contains(checkStr))||//不是辅导员权限时直接进行判断
               (authority==aut4&&!classIds.toString().contains(checkStr))//是辅导员时判断该班级是否可以被删除
                    ) {//没有管理authority的上级权限
                imageView.setImageResource(R.drawable.no_select);
                int finalI = i;
                //点击可选权限
                RxView.clicks(autView)
                        .subscribe(o -> {
                            if (deleteMap.get(authority) != null && deleteMap.get(authority).contains(region[finalI])) {//删除
                                imageView.setImageResource(R.drawable.no_select);
                                deleteMap.put(authority, deleteMap.get(authority).replace(region[finalI] + ", ", ""));
                            } else {//添加
                                imageView.setImageResource(R.drawable.right_icon);
                                deleteMap.put(authority, (deleteMap.get(authority) == null ? "" : deleteMap.get(authority)) + region[finalI] + ", ");
                            }
                        });
            }else {//不能被删除的权限范围
                RxView.clicks(autView)
                        .throttleFirst(clickdelay,TimeUnit.MILLISECONDS)
                        .subscribe(o ->
                            Toast.makeText(this,getString(R.string.delete_error_tip_2, DensityUtil.getAuthorityName(this,(short)upAut)),Toast.LENGTH_SHORT).show()
                        );
                textView.setTextColor(getResources().getColor(R.color.text_noable));
                imageView.setVisibility(View.GONE);
            }
            itemView.addView(autView);
        }
        lg_parent.addView(view);

    }

    /**
     * 获取申请信息列表失败
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
    //获取我的详细权限
    private void getMyAuthorityDetail() {
        progressHUD.show();
        classIds= new StringBuilder();
        List<String> autList = new ArrayList<>();
        boolean flag = false;//是否同时包含辅导员权限和学生权限负责人权限
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()>=0){//有审批学生的权限
            String authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();//审批学生权限
            for(int i = 0 ; i <=8;i++){
                if(authorityStr.contains(""+i)){
                    autList.add(""+i);
                }
            }
            if(authorityStr.contains(""+aut4)&&authorityStr.contains(""+aut8))//同时包含辅导员和学生权限负责人权限
                flag = true;
        }
        if(SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()>=0){//有审批教师的权限
            String authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();//审批教师权限
            for(int i = 0 ; i <=2;i++){
                if(authorityStr.contains(""+i)){
                    autList.add(""+(i+10));
                }
            }
        }
        //获取权限的所有管理范围
        ++requestCount;
        mPersenter.getUserAuthorityDetail(SharedPreferencesUtil.getInstance().getUserId(),3,autList,true);
        if(flag) {//获取该权限审批人所管理院系的所有班级id
            ++requestCount;
            mPersenter.getNoAbleDeleteClassId(SharedPreferencesUtil.getInstance().getUserId());
        }
    }
    private void checkSelect() {
        if(deleteMap==null||deleteMap.size()==0) {
            Toast.makeText(this, R.string.delete_error_tip_1, Toast.LENGTH_SHORT).show();
            return;
        }
        List<String> list = new ArrayList<>();
        String[] stuType = getResources().getStringArray(R.array.approverType);
        String[] teaType = getResources().getStringArray(R.array.approverType_2);
        for(int i = 0 ;i<12;i++){
            if(deleteMap.get(i)!=null&&!"".equals(deleteMap.get(i))){
                StringBuilder oldStr = new StringBuilder(deleteMap.get(i));
                String []arr = oldStr.toString().split(", ");
                Arrays.sort(arr);
                oldStr = new StringBuilder();
                for (String anArr : arr) {
                    oldStr.append(anArr).append(", ");
                }
                deleteMap.put(i,oldStr.toString());
                if(i<10) {
                    list.add(getString(R.string.delete_authority_des,stuType[i%9],deleteMap.get(i).substring(0,deleteMap.get(i).length()-2)));
                }else {
                    list.add(getString(R.string.delete_authority_des,teaType[(i-10)%3],deleteMap.get(i).substring(0,deleteMap.get(i).length()-2)));
                }
            }
        }
        ApplyDialog applyDialog = new ApplyDialog(this,list,101);
        //取消
        applyDialog.setOnCancelClickListener(applyDialog::dismiss);
        //确定
        applyDialog.setOnConfirmClickListener(() -> {
            progressHUD.show();
            List<ApplyInfo> applyInfoList = new ArrayList<>();
            Date date = new Date();
            for(int i=0;i<12;i++) {
                if(deleteMap.get(i)!=null&&!"".equals(deleteMap.get(i))){
//            String applyId, Integer applyUserId, Integer applyAuthorityType, Integer applyAuthority, String applyRegion
//            applyDate, Short applyStatus, String applyUserName
                    ApplyInfo applyInfo=new ApplyInfo(String.valueOf(SharedPreferencesUtil.getInstance().getUserId()) + String.valueOf(date.getTime()), SharedPreferencesUtil.getInstance().getUserId().intValue(),
                            -1, i,deleteMap.get(i).substring(0,deleteMap.get(i).length()-2),deleteMap.get(i).length()<autMap.get(i).length()?(short)-100:-110,SharedPreferencesUtil.getInstance().getUserName());
                    applyInfo.setApplyReason("");
                    //删除权限
                    if(applyInfo.getApplyStatus()==-110){
                        //剩余权限
                       int newAuthority = getChangeAuthority(i);
                       //更新本地信息
                       if(i<10)
                        SharedPreferencesUtil.getInstance().setApprovalStuAut(newAuthority);
                       else
                           SharedPreferencesUtil.getInstance().setApprovalTeaAut(newAuthority);
                       applyInfo.setAfterDeleteAut(newAuthority);
                   }
                    applyInfoList.add(applyInfo);
                    date.setTime(date.getTime()+100);
                }
            }
            mPersenter.deleteAuthorities(applyInfoList);
            applyDialog.dismiss();
        });
        applyDialog.show();
    }

    //删除权限成功
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_DELETE_AUTHORITIES)
    }, thread = EventThread.MAIN_THREAD)
    public void deleteAuthorities(BaseResponse<String> data){
        if(MyActivityManager.getInstance().getCurrentActivity()==this){
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            //重新获取用户权限
            new UserInfoPresent(this).getUserInfo(SharedPreferencesUtil.getInstance().getUserId(),SharedPreferencesUtil.getInstance().getUserType());
            finish();
        }
    }

    @Override
    public LeaveAuthorityPresenter getPersenter() {
        return new LeaveAuthorityPresenter(this);
    }


    //获取删除权限后用户剩余的权限
    public Integer getChangeAuthority(int deleteAut) {
        Integer oldAut;
        String newAut;
        if(deleteAut<10) {//审批学生
            oldAut = SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();
        } else{//审批教师
            oldAut =  SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();
            deleteAut -=10;
        }
        newAut = String.valueOf(oldAut).replace(String.valueOf(deleteAut),"");
        if("".equals(newAut)){
            oldAut = -1;
        }else if (newAut.charAt(0)=='0'){//权限0在下标为0的位置
            oldAut = Integer.valueOf(newAut)*10;
        }else {
            oldAut = Integer.valueOf(newAut);
        }
        return oldAut;
    }
}
