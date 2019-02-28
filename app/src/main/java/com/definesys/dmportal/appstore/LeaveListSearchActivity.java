package com.definesys.dmportal.appstore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.TypeListAdapter;
import com.definesys.dmportal.appstore.customViews.FlowLayout;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.adapter.GruopInfoRecycleViewAdapter;
import com.definesys.dmportal.main.bean.GroupInfo;
import com.definesys.dmportal.main.interfaces.OnItemClickListener;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vise.xsnow.http.ViseHttp.getContext;

@Route(path = ARouterConstants.LeaveListSearchActivity)
public class LeaveListSearchActivity extends BaseActivity {

    //搜索框
    @BindView(R.id.et_search)
    EditText ed_search;

    @BindView(R.id.cancel_text)
    TextView tv_cancel;

     //种类列表
     @BindView(R.id.type_view)
     FlowLayout fl_type;
     private List<String> typeList;


    //搜索列表
    @BindView(R.id.history_view)
    FlowLayout fl_history;
    private  List<String> historyList;

    @BindView(R.id.history_layout)
    LinearLayout lg_history;

    @BindView(R.id.delete_text)
    TextView tv_delete;//删除历史记录

    @Autowired(name = "type")
    int type;//页面类型 0.历史请假记录 1.待处理的请假记录 2.历史审批记录
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
        typeList = new ArrayList<>();
        typeList.add(getString(R.string.all));
        if(type==0) {
            typeList.add(getString(R.string.tag_1));
            typeList.add(getString(R.string.tag_2));
            typeList.add(getString(R.string.tag_3));
            typeList.add(getString(R.string.tag_4));
            typeList.add(getString(R.string.tag_5));
            typeList.add(getString(R.string.tag_6));
            typeList.add(getString(R.string.tag_7));
            typeList.add(getString(R.string.tag_10));
        }
        else if(type==1) {
            typeList.add(getString(R.string.tag_1));
            typeList.add(getString(R.string.tag_2));
            typeList.add(getString(R.string.tag_3));
        }else if(type==2) {
            typeList.add(getString(R.string.tag_8));
            typeList.add(getString(R.string.tag_9));
        }else if(type==3) {
            typeList.add(getString(R.string.tag_4));
            typeList.add(getString(R.string.tag_7));
        }
        //初始化列表
        for (int i = 0; i < typeList.size(); i++) {
            fl_type.addView(addTagItem(typeList.get(i),true));//添加到父View
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
     * @return
     */
    private TextView addTagItem(String content,boolean check) {
        TextView tv = (TextView) LayoutInflater.from(this).inflate(
                R.layout.item_history_list, fl_history, false);
        tv.setText(content);
        //点击事件
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearch(content,check);
            }
        });
        return  tv;
    }

    /**
     * 搜索的最终入口
     * @param check 是否需要做重复内容检查
     * @param content
     */
    private void goSearch(String content,boolean check){
        this.clickCheck =check;
        this.clickContent =content;
        ARouter.getInstance().build(ARouterConstants.LeaveListActivity)
                .withInt("type",type)
                .withBoolean("isAll",getString(R.string.all).equals(content.trim()))
                .withBoolean("isSearch",true)
                .withInt("checkCode",getCode(content.trim()))
                .withString("content",content)
                .withInt("userId",SharedPreferencesUtil.getInstance().getUserId().intValue())
                .withString("ARouterPath",type==0?ARouterConstants.LeaveInFoDetailActivity:ARouterConstants.ApprovalLeaveInfoActivity)
                .navigation();
    }

    private void initView() {
        if(type==0||type==3)//历史请假记录||销假
            ed_search.setHint(R.string.history_hint_0);
        else if(type==1) //待处理的审批记录
            ed_search.setHint(R.string.history_hint_1);
        else if(type==2)//历史审批记录
            ed_search.setHint(R.string.history_hint_2);

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
                .subscribe(o -> {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.detele_history)
                            .setNegativeButton(R.string.cancel,null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferencesUtil.getInstance().setHistoryData(type,null);
                                    historyList.clear();
                                    fl_history.removeAllViews();
                                    initHistoryList();
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();

                });
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
     * @param item
     * @return
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
            if(inputMethodManager!=null&&inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(LeaveListSearchActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            edSearch();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }


    public int getCode(String trim) {
        if(getString(R.string.tag_1).equals(trim))//课假
            return 0;
        else if(getString(R.string.tag_2).equals(trim))//短假
             return 1;
        else if(getString(R.string.tag_3).equals(trim))//长假
            return 2;
        else if(getString(R.string.tag_4).equals(trim))//已批准
            return 10;
        else if(getString(R.string.tag_5).equals(trim))//已拒绝
            return 11;
        else if(getString(R.string.tag_6).equals(trim))//正在审批
            return 3;
        else if(getString(R.string.tag_7).equals(trim))//未销假
            return 4;
        else if(getString(R.string.tag_8).equals(trim))//同意
            return 1;
        else if(getString(R.string.tag_9).equals(trim))//拒绝
            return 0;
        else if(getString(R.string.tag_10).equals(trim))//已销假
            return 12;
        else
            return -1;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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
