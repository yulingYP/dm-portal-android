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
import com.definesys.dmportal.appstore.utils.Constants;
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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.AuthorityChangeActivity)
public class AuthorityChangeActivity extends BaseActivity<LeaveAuthorityPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @BindView(R.id.parent_layout)
    LinearLayout lg_parent;
    @BindView(R.id.no_text)
    TextView tv_no;

    private HashMap<Integer,String> deleteMap;//用户要删除的权限<权限，范围>
    HashMap<Integer, String> autMap;//用户的全部权限<权限，范围>
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
        getMyAuthorityDetail();
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
            List<AuthorityDetail> list=data.getData();//详细信息的list
            if(list!=null&&list.size()>0) {//有数据
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

                //根据map生成权限列表
                for(int i = 0 ;i<12;i++){
                    if(autMap.get(i)!=null&&!"".equals(autMap.get(i)))
                        initSelectList(i, autMap.get(i).split(", "));
                }
            }else
                tv_no.setVisibility(View.VISIBLE);
            progressHUD.dismiss();
        }
    }
    //初始化列表
    private void initSelectList(int authority, String[] region) {
        View view= LayoutInflater.from(this).inflate(R.layout.item_authority_view,lg_parent,false);
        //权限详细信息
        LinearLayout itemView= view.findViewById(R.id.item_layout);
        //下箭头
        ImageView downIcon = view.findViewById(R.id.down_icon);
        //权限名称
        ((TextView)view.findViewById(R.id.aut_des)).setText(authority<10?getResources().getStringArray(R.array.approverType)[authority%9]:getResources().getStringArray(R.array.approverType_2)[(authority-10)%3]);
        //点击权限标题
        RxView.clicks(view.findViewById(R.id.des_layout))
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> {
                    if(itemView.getVisibility()==View.GONE){//显示全部可选权限
                        itemView.setVisibility(View.VISIBLE);
                        downIcon.setRotation(180);
                    }else {//隐藏
                        itemView.setVisibility(View.GONE);
                        downIcon.setRotation(0);
                    }
                });
        downIcon.setRotation(180);
        //添加权限的详细信息
        int size = region.length;
//        deleteMap.put(authority,"");
        for(int i = 0; i<size ;i++){
            View autView= LayoutInflater.from(this).inflate(R.layout.item_sign_type_view, itemView,false);
            TextView textView=autView.findViewById(R.id.name_text);//权限名称
            ImageView imageView=autView.findViewById(R.id.select_img);//是否选择
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
            textView.setText(region[i]);
            imageView.setImageResource(R.drawable.no_select);
            int finalI = i;
            //点击可选权限
            RxView.clicks(autView)
                    .subscribe(o -> {
                        if(deleteMap.get(authority)!=null&&deleteMap.get(authority).contains(region[finalI])) {//删除
                            imageView.setImageResource(R.drawable.no_select);
                            deleteMap.put(authority,deleteMap.get(authority).replace(region[finalI]+", ",""));
                        }else {//添加
                            imageView.setImageResource(R.drawable.right_icon);
                            deleteMap.put(authority, (deleteMap.get(authority)==null?"":deleteMap.get(authority)) + region[finalI] + ", ");
                        }
                    });
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
        List<String> autList = new ArrayList<>();
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()>=0){//有审批学生的权限
            String authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();//审批学生权限
            for(int i = 0 ; i <8;i++){
                if(authorityStr.contains(""+i)){
                    autList.add(""+i);
                }
            }
        }
        if(SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()>=0){//有审批教师的权限
            String authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();//审批教师权限
            for(int i = 0 ; i <2;i++){
                if(authorityStr.contains(""+i)){
                    autList.add(""+(i+10));
                }
            }
        }
        mPersenter.getUserAuthorityDetail(SharedPreferencesUtil.getInstance().getUserId(),3,autList,true);
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
            for(int i=0;i<12;i++) {
                if(deleteMap.get(i)!=null&&!"".equals(deleteMap.get(i))){
//            String applyId, Integer applyUserId, Integer applyAuthorityType, Integer applyAuthority, String applyRegion
//            applyDate, Short applyStatus, String applyUserName
                    applyInfoList.add(new ApplyInfo(String.valueOf(SharedPreferencesUtil.getInstance().getUserId()) + String.valueOf(System.currentTimeMillis()), SharedPreferencesUtil.getInstance().getUserId().intValue(),
                            -1, i,deleteMap.get(i),(short)100,SharedPreferencesUtil.getInstance().getUserName(),deleteMap.get(i).equals(autMap.get(i)) ));
                }
            }
            mPersenter.deleteAuthorities(applyInfoList);
            applyDialog.dismiss();
        });
        applyDialog.show();
    }
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void deleteAuthorities(BaseResponse<String> data){
        if(MyActivityManager.getInstance().getCurrentActivity()==this){
            Toast.makeText(this, R.string.delete_success, Toast.LENGTH_SHORT).show();
            new UserInfoPresent(this).getUserInfo(SharedPreferencesUtil.getInstance().getUserId(),SharedPreferencesUtil.getInstance().getUserType());
            finish();
        }
    }

    @Override
    public LeaveAuthorityPresenter getPersenter() {
        return new LeaveAuthorityPresenter(this);
    }


}
