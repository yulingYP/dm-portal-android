package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.CursorArg;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 羽翎 on 2019/1/6.
 */

public class SubjectDetailDialog extends LinearLayout {
    private Context mContext;
    @BindView(R.id.cusor_name_text)
    TextView tv_cursor_name;
    @BindView(R.id.teacher_name_text)
    TextView tv_teacher;
    @BindView(R.id.location_text)
    TextView tv_location;
    @BindView(R.id.week_text)
    TextView tv_week;
    @BindView(R.id.pitch_text)
    TextView tv_pitch;
    @BindView(R.id.confirm_text)
    TextView tv_confirm;
    private OnClickConfirmListener onClickConfirmListener;
    public SubjectDetailDialog(Context context) {
        super(context);
        initView(context);
    }


    public SubjectDetailDialog(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SubjectDetailDialog(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.dialog_subject_view,this);
        ButterKnife.bind(this);
        RxView.clicks(tv_confirm)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obg->{
                    if(onClickConfirmListener!=null)
                        onClickConfirmListener.onClickConfirm();
                });
    }

    /**
     * 更新提示框的内容
     * @param cursorArg 课程安排对象
     * @param currentWeek 第几周
     * @param weekDay 星期几
     * @param pitch 第几节
     */
    public void updateData(CursorArg cursorArg,int currentWeek,int weekDay,int pitch){
        tv_cursor_name.setText(mContext.getString(R.string.cursor_name,cursorArg.getCursorName()));
        tv_teacher.setText(mContext.getString(R.string.teacher_name,checkString(cursorArg.getTeacherName())));
        tv_location.setText(mContext.getString(R.string.location,checkString(cursorArg.getClassroom())));
        tv_week.setText(mContext.getString(R.string.week_number,currentWeek));
        tv_pitch.setText(mContext.getString(R.string.pitch_number,weekDay,pitch));
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

    public void setOnClickConfirmListener(OnClickConfirmListener onClickConfirmListener) {
        this.onClickConfirmListener = onClickConfirmListener;
    }

    public interface OnClickConfirmListener{
        void onClickConfirm();
    }
}
