package com.definesys.dmportal.appstore.customViews;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.CursorArg;
import com.definesys.dmportal.appstore.utils.DensityUtil;

import java.util.List;

/**
 * Created by 羽翎 on 2019/1/27.
 */

public class SubjectDialog extends Dialog {

    private TextView tv_confirm;
    private LinearLayout lg_item;
    private ScrollView lg_main;

    private List<CursorArg> subjectList;
    private int weekDay;
    private int pitch;
    private Context mContext;

    public SubjectDialog(@NonNull Context context ) {
        super(context);
        mContext=context;
        View view=LayoutInflater.from(mContext).inflate(R.layout.dialog_subject_teacher_table, null);

        tv_confirm=(TextView)view.findViewById(R.id.confirm_text);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        lg_item=(LinearLayout) view.findViewById(R.id.subject_layout);
        lg_main=(ScrollView) view.findViewById(R.id.main_layout);
    }

    public void setData(List<CursorArg> subjectList, int week, int pitch){
        this.subjectList = subjectList;
        this.weekDay = week;
        this.pitch = pitch;
        initView();
    }

    private void initView() {
        lg_item.removeAllViews();
        if(subjectList==null)
            return;
        for(CursorArg cursorArg: subjectList){
            View view =  LayoutInflater.from(mContext).inflate(R.layout.item_teacher_table_view, null);
            ((TextView)view.findViewById(R.id.cusor_name_text)).setText(mContext.getString(R.string.cursor_name,cursorArg.getCursorName(),cursorArg.getCursorType()));
            ((TextView)view.findViewById(R.id.credit_text)).setText(mContext.getString(R.string.credit_tip,cursorArg.getCredit()));
            ((TextView)view.findViewById(R.id.cusor_hour_text)).setText(mContext.getString(R.string.cursor_hour_tip,2));
            ((TextView)view.findViewById(R.id.location_text)).setText(mContext.getString(R.string.location,checkString(DensityUtil.checkClassRoom(weekDay+1,cursorArg.getClassroom()))));
            ((TextView)view.findViewById(R.id.week_text)).setText(mContext.getString(R.string.week_number_2,cursorArg.getStartWeek(),cursorArg.getEndWeek()));
            ((TextView)view.findViewById(R.id.pitch_text)).setText(mContext.getString(R.string.pitch_number,mContext.getResources().getStringArray(R.array.week)[weekDay],pitch));
            ((TextView)view.findViewById(R.id.class_id_text)).setText(checkString(DensityUtil.getClassLisId(cursorArg)));
            lg_item.addView(view);
        }
        setContentView(lg_main);
        //获取当前Activity所在的窗体
        if (getWindow() == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        //getWindow().setGravity(Gravity.BOTTOM);
        //设置铺满屏幕
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }
    /**
     * 检查字符串是否为空
     * @param str
     * @return
     */
    private String checkString(String str) {
        if(str==null||"".equals(str))
            return getContext().getString(R.string.unknow);
        return str;
    }
   public void scrollToBootom(){
       if(lg_main!=null)
           lg_main.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
   }

    @Override
    protected void onStart() {
        super.onStart();
        scrollToBootom();
    }
}