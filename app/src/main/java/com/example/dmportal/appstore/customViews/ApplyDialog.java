package com.example.dmportal.appstore.customViews;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import com.example.dmportal.R;
import com.example.dmportal.appstore.adapter.ApplyAuthorityAdapter;
import com.example.dmportal.appstore.utils.Constants;
import com.example.dmportal.main.interfaces.OnConfirmClickListener;
import com.example.dmportal.main.interfaces.OnItemClickListener;
import com.jakewharton.rxbinding2.view.RxView;


import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

/**
 *
 * Created by 羽翎 on 2019/3/3.
 */

public class ApplyDialog extends Dialog {
    private Context context;
    private List<String> stringList;
    private int type;
    private OnItemClickListener onItemClickListener;
    private OnConfirmClickListener onCancelClickListener;
    private OnConfirmClickListener onConfirmClickListener;
    private ApplyAuthorityAdapter applyAuthorityAdapter;
    private TextView tv_select;
    public ApplyDialog(Context context, @NonNull List<String> stringList,int type) {
        super(context);
        this.context = context;
        this.stringList = stringList;
        this.type =type;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
    }

    /** type
     *   0.寝室长权限根据facultyId获取班级名称
     *   1.根据班级id获取班级名单
     *   2.班长权限 根据facultyId获取班级名称
     *   3.班主任权限 获取院系列表
     *   4.获取该院系所有班级的id
     *   5.毕设老师权限 获取所有院系的名称
     *   6.毕设老师权限 获取该院系所有班级的id
     *   7.获取班级全部成员
     *   8.辅导员权限 获取院系列表
     *   9.获取所有班级id
     *   10.学院实习工作负责人权限 获取院系列表
     *   11.学生工作负责人权限 获取院系列表
     *   12.教学院长权限 获取院系列表
     *   20.部门请假负责人权限 获取所有部门的id
     *   21.部门教学院长权限 获取所有部门的id
     *   100，101.提交提示框
     */
    @SuppressLint("InflateParams")
    private void initView() {
         View view = LayoutInflater.from(context).inflate(R.layout.dialog_apply_authority_view, null);
         tv_select  = view.findViewById(R.id.select_text);
        if(type==0){
            tv_select.setVisibility(View.VISIBLE);
//            tv_select.setText(context.getString(R.string.select_des,"暂无"));
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_1);
        }else  if(type==1){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_2);
        }else  if(type==2){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_3);
        }else  if(type==3){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_4);
        }else  if(type==4){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_5);
        }else  if(type==5){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_6);
        }else  if(type==6){
            tv_select.setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_7);
        }else  if(type==7){
            tv_select.setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_8);
        }else  if(type==8){
            tv_select.setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_9);
        }else  if(type==9){
            tv_select.setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_10);
        }else  if(type==10||type==11||type==12){
            tv_select.setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_11);
        } else  if(type==20||type==21){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_12);
        } else  if(type==100){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(context.getString(R.string.apply_dialog_des_13,Constants.applyInterval));
            ((TextView)view.findViewById(R.id.title_text)).setTextColor(context.getResources().getColor(R.color.blue));
        }else if(type==101){
            tv_select.setVisibility(GONE);
            ((TextView)view.findViewById(R.id.title_text)).setText(R.string.apply_dialog_des_14);
            ((TextView)view.findViewById(R.id.title_text)).setTextColor(context.getResources().getColor(R.color.blue));
        }
        if(type==3||type==5||type==8){
            view.findViewById(R.id.confirm_layout).setVisibility(View.GONE);
        } else if(type!=3) {
            view.findViewById(R.id.confirm_layout).setVisibility(View.VISIBLE);
            //点击确定
            RxView.clicks(view.findViewById(R.id.confirm_text))
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {
                        if (onConfirmClickListener != null)
                            onConfirmClickListener.onClick();
                    });
            //点击取消
            RxView.clicks(view.findViewById(R.id.cancel_text))
                    .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {
                        if (onCancelClickListener != null)
                            onCancelClickListener.onClick();
                    });
        }
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        applyAuthorityAdapter = new ApplyAuthorityAdapter(stringList,context,type);
        //点击事件
        applyAuthorityAdapter.setMyOnClickListener(position -> {
            if(onItemClickListener!=null)
                onItemClickListener.onClick(position);
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(applyAuthorityAdapter);
        this.setContentView(view);
        //获取当前Activity所在的窗体
        if (getWindow() == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        getWindow().setGravity(Gravity.CENTER);
        //设置铺满屏幕
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



    public void setOnCancelClickListener(OnConfirmClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
    }



    public ApplyAuthorityAdapter getApplyAuthorityAdapter() {
        return applyAuthorityAdapter;
    }

    public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.onConfirmClickListener = onConfirmClickListener;
    }

    public void setContent(String content) {
        tv_select.setText(context.getString(R.string.select_des,content));
    }
}
