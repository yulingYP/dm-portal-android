package com.definesys.dmportal.appstore.leaveSettingUI;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.presenter.LeaveAuthorityPresenter;
import com.definesys.dmportal.appstore.tempEntity.AuthorityDetail;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.AnimUtils;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import static android.view.View.GONE;


@Route(path = ARouterConstants.AuthoritySettingActivity)
public class AuthoritySettingActivity extends BaseActivity<LeaveAuthorityPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
    @BindView(R.id.approval_stu_text)
    TextView tv_stu;
    @BindView(R.id.approval_tea_text)
    TextView tv_tea;
    @BindView(R.id.tea_layout)
    RelativeLayout lg_tea;
    @BindView(R.id.stu_layout)
    RelativeLayout lg_stu;
    @BindView(R.id.stu_down_icon)
    ImageView iv_stu;
    @BindView(R.id.tea_down_icon)
    ImageView iv_tea;
    @BindView(R.id.apply_layout)
    LinearLayout lg_apply;
    @BindView(R.id.delete_layout)
    LinearLayout lg_delete;
    @BindView(R.id.approval_layout)
    LinearLayout lg_approval;
    @BindView(R.id.approval_history_layout)
    LinearLayout lg_appHis;
    @BindView(R.id.apply_history_layout)
    LinearLayout lg_alyHis;
    private HashMap<Integer,String> stuMap;//审批请假学生权限Map
    private HashMap<Integer,String> teaMap;//审批请假教师权限Map
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_authority);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.my_authority_title));
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        //退出
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    finish()
                );
        //点击审批学生权限箭头 开始动画
        RxView.clicks(lg_stu)
                .throttleFirst(Constants.loadAnim, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(tv_stu.getVisibility()== View.VISIBLE){//已显权限
                        AnimUtils.setInstance(tv_stu,iv_stu,tv_stu.getMeasuredHeight()).toggle(true);
                    }else {//未显示权限
                       if(stuMap==null&&"".equals(tv_stu.getText().toString())){//还没有获取到详细权限
                            httpPost(0);
                       }else if(stuMap!=null&&"".equals(tv_stu.getText().toString())) {//网络请求已发送但暂未获取到网络请求
                           showTextByMap(0);
                       }else {
                           AnimUtils.setInstance(tv_stu,iv_stu,Integer.valueOf(stuMap.get(-1))).toggle(true);
                       }
                    }
                });

        //点击审批教师权限箭头 开始动画
        RxView.clicks(lg_tea)
                .throttleFirst(Constants.loadAnim, TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(tv_tea.getVisibility()== View.VISIBLE){//已显权限
                        AnimUtils.setInstance(tv_tea,iv_tea,tv_tea.getMeasuredHeight()).toggle(true);
                    }else {//未显示权限
                        if(teaMap==null&& "".equals(tv_tea.getText().toString())){//还没有获取到详细权限
                                httpPost(1);
                        }else if(teaMap!=null&&"".equals(tv_tea.getText().toString())){//网络请求已发送但暂未获取到网络请求
                            showTextByMap(1);
                        }else {
                            AnimUtils.setInstance(tv_tea,iv_tea,Integer.valueOf(teaMap.get(-1))).toggle(true);
                        }
                    }
                });
        //申请权限
        RxView.clicks(lg_apply)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ARouter.getInstance().build(ARouterConstants.UpdateLeAutActivity)
                            .navigation()
                );
        //删除权限
        RxView.clicks(lg_delete)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ARouter.getInstance().build(ARouterConstants.AuthorityChangeActivity)
                            .navigation()
                );
        //权限审批
        RxView.clicks(lg_approval)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ARouter.getInstance().build(ARouterConstants.AppLyListActivity)
                            .withString("ARouterPath",ARouterConstants.ApprovalApplyInfoActivity)
                            .withInt("type",0)//权限审批
                            .withBoolean("isAll",true)
                            .navigation()
                );
        //我的申请记录
        RxView.clicks(lg_alyHis)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ARouter.getInstance().build(ARouterConstants.AppLyListActivity)
                            .withString("ARouterPath",ARouterConstants.ApplyInfoActivity)
                            .withInt("type",1)//我的申请记录
                            .withBoolean("isAll",true)
                            .navigation()
                );
        //历史审批记录
        RxView.clicks(lg_appHis)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ARouter.getInstance().build(ARouterConstants.AppLyListActivity)
                            .withString("ARouterPath",ARouterConstants.ApprovalApplyInfoActivity)
                            .withInt("type",2)//历史审批记录
                            .withBoolean("isAll",true)
                            .navigation()
                );
        setShowLayout();

    }

    private void setShowLayout() {
        if(SharedPreferencesUtil.getInstance().getUserType()==0)//学生
            lg_tea.setVisibility(GONE);
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()<=0&&SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()<0) {//没有任何审批权限
            lg_delete.setVisibility(GONE);
            lg_approval.setVisibility(GONE);
        }
        if(SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority()<0){//没有审批请假学生权限
            tv_stu.setText(R.string.no_authority_des);
            tv_stu.setGravity(Gravity.CENTER);
        }

        //审批请假教师权限
        if(SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority()<0){//没有审批请假学生权限
            tv_tea.setText(R.string.no_authority_des);
            tv_tea.setGravity(Gravity.CENTER);
        }
    }

    /**
     * 添加权限描述
     * @param type 0.审批学生权限 1.审批老师权限
     */
    @SuppressLint("UseSparseArrays")
    private void httpPost(int type) {
        progressHUD.show();
        String authorityStr=null;//用户权限
        String [] approverTypes = null;
        int max=0;
        if(type==0){//0.审批学生权限
            stuMap = new HashMap<>();
            authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();
            max=8;
            approverTypes=getResources().getStringArray(R.array.approverType);
        }else if(type==1){//审批老师权限
            teaMap = new HashMap<>();
            authorityStr=""+SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();
            max = 2;
            approverTypes=getResources().getStringArray(R.array.approverType_2);
        }

        List<String> autList = new ArrayList<>();
        for(int i = 0 ; i <max;i++){
            if(authorityStr.contains(""+i)){
                if(type==0){
                    stuMap.put(i,approverTypes[i]+" ");
                    autList.add(""+i);
                }else {
                    teaMap.put(i,approverTypes[i]+" ");
                    autList.add(""+(i+10));
                }
            }
        }
        mPersenter.getUserAuthorityDetail(SharedPreferencesUtil.getInstance().getUserId(),type,autList,true);
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


    /* * 获取权限详细信息成功
     * @param data BaseResponse
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getDetailInfoByBean(BaseResponse<List<AuthorityDetail>> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this&&(data.getExtendInfo()==0||data.getExtendInfo()==1)){
            List<AuthorityDetail> list=data.getData();//详细信息的list
            int type=data.getExtendInfo();//权限类型
            int authority;
            if(list!=null&&list.size()>0) {//有数据
                for (int i = 0; i < list.size(); i++) {
                    if (type == 0) {//审批学生学生
                        authority = list.get(i).getUserAuthority();
                        if (stuMap.get(authority) != null && !"".equals(stuMap.get(authority)))
                            stuMap.put(authority, stuMap.get(authority) + list.get(i).getRegion() + ", ");
                    } else {//审批教师
                        authority = list.get(i).getUserAuthority() - 10;
                        if (teaMap.get(authority) != null && !"".equals(teaMap.get(authority)))
                            teaMap.put(authority, teaMap.get(authority) + list.get(i).getRegion() + ", ");
                    }
                }
            }else {//没数据
                Toast.makeText(this, ("".equals(data.getMsg())?getString(R.string.net_work_error):data.getMsg()),Toast.LENGTH_SHORT).show();
            }
            showTextByMap(type);
            progressHUD.dismiss();

        }
    }
    /**
     * 展示审批的详细结果
     * @param type 0.审批学生 1.审批教师
     * stuMap.get(i).length()>4判断是为了确定用户获得的权限里还有没有成员
     */
    private void showTextByMap(int type) {
        StringBuilder content= new StringBuilder();
        if(type==0){//审批学生
            for(int i = 0;i<9;i++){
                if(stuMap.get(i)!=null&&!"".equals(stuMap.get(i))){//有权限
                    content.append(stuMap.get(i).length()>9?stuMap.get(i).substring(0,stuMap.get(i).length()-2):stuMap.get(i))
                            .append((i==1||i==2)&&stuMap.get(i).length()>4?getString(R.string.ban):" ")
                            .append("\n");
                }
            }
            tv_stu.setText(content.toString().substring(0,content.length()-1));
            //测量宽高
            tv_stu.measure(0,0);
            //获取文本高度
            tv_stu.post(()->{
                stuMap.put(-1,""+tv_stu.getMeasuredHeight());
                AnimUtils.setInstance(tv_stu,iv_stu,tv_stu.getMeasuredHeight()).toggle(true);
            });

        }else if(type==1){//审批教师
            for(int i = 0;i<3;i++){
                if(teaMap.get(i)!=null&&!"".equals(teaMap.get(i))){//有权限
                    content.append(teaMap.get(i).length()>4?teaMap.get(i).substring(0,teaMap.get(i).length()-2):teaMap.get(i))//去掉最后的“， ”
                            .append("\n");
                }
            }
            tv_tea.setText(content.toString().substring(0,content.length()-1));
            //测量宽高
            tv_tea.measure(0,0);
            //获取文本高度
            tv_tea.post(()->{
                teaMap.put(-1,""+tv_tea.getMeasuredHeight());
                AnimUtils.setInstance(tv_tea,iv_tea,tv_tea.getMeasuredHeight()).toggle(true);
            });

        }

    }


    @Override
    public LeaveAuthorityPresenter getPersenter() {
        return new LeaveAuthorityPresenter(this);
    }
}
