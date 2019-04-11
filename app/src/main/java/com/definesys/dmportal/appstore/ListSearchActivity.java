package com.definesys.dmportal.appstore;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.FlowLayout;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARouterConstants.ListSearchActivity)
public class ListSearchActivity extends BaseActivity {

    //搜索框
    @BindView(R.id.et_search)
    EditText ed_search;

    @BindView(R.id.cancel_text)
    TextView tv_cancel;

    @BindView(R.id.type_des)
    TextView tv_des;

     //种类列表
     @BindView(R.id.type_view)
     FlowLayout fl_type;

    //搜索列表
    @BindView(R.id.history_view)
    FlowLayout fl_history;
    private  List<String> historyList;

    @BindView(R.id.history_layout)
    LinearLayout lg_history;

    @BindView(R.id.delete_text)
    TextView tv_delete;//删除历史记录

    @Autowired(name = "type")
    int type;//页面类型 0.历史请假记录 1.待处理的请假记录 2.历史请假审批记录 3.销假  10.权限审批 11.历史权限申请记录 12.历史权限审批记录
    private String clickContent;//点击的标签
    private boolean clickCheck;//是否需要进行重复内容检测
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_list_search);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        initView();
        initTpyeList();//种类标签
        initHistoryList();//历史记录
    }



    /**
     * 种类列表
     */
    private void initTpyeList() {
        List<String> typeList = new ArrayList<>();
        typeList.add(getString(R.string.all));
        if(type==0) {//0.历史请假记录
            typeList.add(getString(R.string.tag_1));
            typeList.add(getString(R.string.tag_2));
            typeList.add(getString(R.string.tag_3));
            typeList.add(getString(R.string.tag_4));
            typeList.add(getString(R.string.tag_5));
            typeList.add(getString(R.string.tag_6));
            typeList.add(getString(R.string.tag_7));
            typeList.add(getString(R.string.tag_10));
        }
        else if(type==1) {//请假未审批记录
            typeList.add(getString(R.string.tag_1));
            typeList.add(getString(R.string.tag_2));
            typeList.add(getString(R.string.tag_3));
        }else if(type==2) {//请假已审批记录
            typeList.add(getString(R.string.tag_8));
            typeList.add(getString(R.string.tag_9));
        }else if(type==3) {//请假的销假列表
            typeList.add(getString(R.string.tag_4));
            typeList.add(getString(R.string.tag_7));
        }else if(type==10) {//权限审批
            //审批学生权限
            int stuAut=SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority();
            //审批教师权限
            int teaAut=SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();
            stuAut=stuAut<0?0:stuAut;
            teaAut=teaAut<0?0:teaAut;
            if(stuAut>0) {//审批学生
                String strStu = String.valueOf(stuAut);
                if ((strStu).contains("1"))//班长权限
                    typeList.add(getString(R.string.tag_11));
                if ((strStu).contains("2"))//班主任权限
                    typeList.add(getString(R.string.tag_12));
                if ((strStu).contains("4")) {//辅导员权限
                    typeList.add(getString(R.string.tag_12));
                    typeList.add(getString(R.string.tag_13));
                    typeList.add(getString(R.string.tag_14));
                }
                if ((strStu).contains("8")) {//权限管理人
                    typeList.add(getString(R.string.tag_15));
                    typeList.add(getString(R.string.tag_16));
                    typeList.add(getString(R.string.tag_17));
                    typeList.add(getString(R.string.tag_18));
                }
            }
            if(teaAut>0) {//审批教师
                if (("" + teaAut).contains("2")) {//权限管理人
                    typeList.add(getString(R.string.tag_19));
                    typeList.add(getString(R.string.tag_20));
                }
            }

        }else if(type==11) {//历史权限申请记录
            typeList.add(getString(R.string.tag_4));
            typeList.add(getString(R.string.tag_5));
            typeList.add(getString(R.string.tag_6));
        }else if(type==12) {//历史权限审批记录
            typeList.add(getString(R.string.tag_8));
            typeList.add(getString(R.string.tag_9));
        }
        if(typeList.size()>1){//初始化列表
            for (int i = 0; i < typeList.size(); i++) {
                fl_type.addView(addTagItem(typeList.get(i),true));//添加到父View
            }
        }else {
            fl_type.setVisibility(View.GONE);
            tv_des.setVisibility(View.GONE);
        }


    }
    /**
     * 历史记录列表
     */
    private void initHistoryList() {
        if(historyList==null)
            historyList = new ArrayList<>();
        //获取历史记录
        if(SharedPreferencesUtil.getInstance().getHistortyData(type)!=null) {
            historyList.clear();
            fl_history.removeAllViews();
            historyList.addAll(SharedPreferencesUtil.getInstance().getHistortyData(type));
        }
        if (historyList == null || historyList.size() <= 0) {//没有历史记录 隐藏界面
            lg_history.setVisibility(View.GONE);
            return;
        }
        lg_history.setVisibility(View.VISIBLE);
        for (int i = 0; i < historyList.size(); i++) {
           fl_history.addView(addTagItem(historyList.get(i),false));
        }

    }

    /**
     * 添加单个tag
     * @param content 内容
     * @param check 是否需要做重复内容检测
     * @return r
     */
    private TextView addTagItem(String content,boolean check) {
        TextView tv = (TextView) LayoutInflater.from(this).inflate(
                R.layout.item_history_list, fl_history, false);
        tv.setText(content);
        //点击事件
        tv.setOnClickListener(v -> goSearch(content,check));
        return  tv;
    }

    /**
     * 搜索的最终入口
     * @param check 是否需要做重复内容检查
     * @param content r
     */
    private void goSearch(String content,boolean check){
        this.clickCheck = check;
        this.clickContent = content;
        ARouter.getInstance()
                .build(type<10?ARouterConstants.LeaveListActivity:ARouterConstants.AppLyListActivity)//请假列表：权限列表
                .withInt("type",type<10?type:(type-10))
                .withBoolean("isAll",getString(R.string.all).equals(content.trim()))
                .withBoolean("isSearch",true)
                .withInt("checkCode",getCode(content.trim()))
                .withString("content",content.trim())
                .withInt("userId",SharedPreferencesUtil.getInstance().getUserId().intValue())
                .withString("ARouterPath",type<10?(type==0?ARouterConstants.LeaveInFoDetailActivity:ARouterConstants.ApprovalLeaveInfoActivity)
                        :(type==1?ARouterConstants.ApplyInfoActivity:ARouterConstants.ApprovalApplyInfoActivity))
                .navigation();
    }

    private void initView() {
        if(type==0||type==3)//历史请假记录||销假
            ed_search.setHint(R.string.history_hint_0);
        else if(type==1) //待处理的请假审批记录
            ed_search.setHint(R.string.history_hint_1);
        else if(type==2)//历史请假审批记录
            ed_search.setHint(R.string.history_hint_2);
        else if(type==10)//权限审批
            ed_search.setHint(R.string.history_hint_1);
        else if(type==11)//历史权限申请
            ed_search.setHint(R.string.history_hint_3);
        else if(type==12)//历史权限审批
            ed_search.setHint(R.string.history_hint_4);
        //获取搜索框焦点
        ed_search.setFocusable(true);
        ed_search.setFocusableInTouchMode(true);
        ed_search.requestFocus();

        //取消
        RxView.clicks(tv_cancel)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj ->finish());

        //清除历史记录
        RxView.clicks(tv_delete)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o ->
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.detele_history)
                            .setNegativeButton(R.string.cancel,null)
                            .setPositiveButton(R.string.confirm, (dialog, id) -> {
                                SharedPreferencesUtil.getInstance().setHistoryData(type,null);
                                historyList.clear();
                                fl_history.removeAllViews();
                                initHistoryList();
                                dialog.dismiss();
                            })
                            .create()
                            .show()

                );
    }

    /*
    *编辑框搜索
     */
    private void edSearch() {
        String content = ed_search.getText().toString();
        if(!"".equals(content)){
            ed_search.setText("");
            goSearch(content,true);
        }
    }

    /**
     * 检测是否与历史记录中的记录重复
     * @param item i
     * @return r
     */
    private boolean checkSearchItem(String item){
        if(item==null||"".equals(item))
            return false;
        if(null==historyList||historyList.size()<=0)
            return true;
        for(int i = 0 ; i <historyList.size(); i++){
            if(historyList.get(i).equals(item))
                return false;
        }
        return true;
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputMethodManager!=null&&inputMethodManager.isActive()&&this.getCurrentFocus()!=null){
                inputMethodManager.hideSoftInputFromWindow(ListSearchActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            edSearch();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }


    public int getCode(String trim) {
        if (type == 0) {
            if(getString(R.string.tag_6).equals(trim))//正在审批
                return 3;
            else if(getString(R.string.tag_5).equals(trim))//已拒绝
                return 11;
            else if(getString(R.string.tag_10).equals(trim))//已销假
                return 12;

        }
        if(type == 0||type==1){
            if(getString(R.string.tag_1).equals(trim))//课假
                return 0;
            else if(getString(R.string.tag_2).equals(trim))//短假
                return 1;
            else if(getString(R.string.tag_3).equals(trim))//长假
                return 2;
        }
        if(type == 0||type==3){
            if(getString(R.string.tag_7).equals(trim))//未销假
                return 4;
            else if(getString(R.string.tag_4).equals(trim))//已批准
                return 10;
        }
        else if(type==2||type==12) {
            if (getString(R.string.tag_8).equals(trim))//同意
                return 1;
            else if (getString(R.string.tag_9).equals(trim))//拒绝
                return 0;
        }
       else if(type==10){
            if(getString(R.string.tag_11).equals(trim))//寝室长权限
                return 0;
            else if(getString(R.string.tag_12).equals(trim))//班长
                return 1;
            else if(getString(R.string.tag_13).equals(trim))//班主任
                return 2;
            else if(getString(R.string.tag_14).equals(trim))//毕设老师
                return 3;
            else if(getString(R.string.tag_15).equals(trim))//辅导员
                return 4;
            else if(getString(R.string.tag_16).equals(trim))//实习负责人
                return 5;
            else if(getString(R.string.tag_17).equals(trim))//学生工作领导
                return 6;
            else if(getString(R.string.tag_18).equals(trim))//教学院长
                return 7;
            else if(getString(R.string.tag_19).equals(trim))//部门请假负责人
                return 10;
            else if(getString(R.string.tag_20).equals(trim))//部门教学负责人
                return 11;
        }
        else if(type==11){
            if(getString(R.string.tag_6).equals(trim))//正在审批
                return 3;
            else if(getString(R.string.tag_4).equals(trim))//已批准
                return 10;
            else if(getString(R.string.tag_5).equals(trim))//已拒绝
                return 11;
        }

        return -1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //添加新标签
        if(clickCheck&&checkSearchItem(clickContent)) {//重复内容检测
            historyList.add(0, clickContent);
            SharedPreferencesUtil.getInstance().setHistoryData(type, historyList);

            if(lg_history.getVisibility()==View.GONE)
                lg_history.setVisibility(View.VISIBLE);
            fl_history.addView(addTagItem(clickContent,true),0);
        }
    }

    @Override
    public BasePresenter getPersenter() {
        return new BasePresenter(this) {
            @Override
            public void subscribe() {
                super.subscribe();
            }
        };
    }
}
