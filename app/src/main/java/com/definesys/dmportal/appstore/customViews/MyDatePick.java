package com.definesys.dmportal.appstore.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.definesys.dmportal.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * Created by 羽翎 on 2018/11/21.
 */

public class MyDatePick extends LinearLayout {

    @BindView(R.id.text_cancel)
    TextView cancelText;

    @BindView(R.id.text_confirm)
    TextView confirmText;
    @BindView(R.id.date_picker)
    DatePicker datePicker;

    @BindView(R.id.time_picker)
    TimePicker timePicker;

    @BindView(R.id.date_text)
    TextView tv_date;

    private onClickEventListener myListener;

    public MyDatePick(Context context) {
        super(context);
        initView(context);
    }
    public MyDatePick(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyDatePick(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.last_design_date_picker, this);
        ButterKnife.bind(this);
        timePicker.setIs24HourView(true);
        RxView.clicks(cancelText)
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(myListener!=null)
                            myListener.onCancel();
                    }
                });
        RxView.clicks(confirmText)
                .throttleFirst(600, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if(myListener!=null)
                            myListener.onConfirm(initDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour()));
                    }
                });
        tv_date.setText((new SimpleDateFormat(getContext().getString(R.string.date_type)).format(new Date())));
        datePicker.setMinDate(0);
        datePicker.setMinDate(System.currentTimeMillis());
        setNumberPickerTextSize(timePicker);
        setNumberPickerTextSize(datePicker);
    }

    private String initDate(int year,int month,int day,int hour) {
        return ""+year+"年"+String.format("%02d",(month+1))+"月"+String.format("%02d",(day))+"日 "+String.format("%02d",(hour))+"时";
    }

    public interface onClickEventListener{
       public void onCancel();
       public void onConfirm(String date);
    }

    public void setMyListener(onClickEventListener myListener) {
        this.myListener = myListener;
    }

    private void setNumberPickerTextSize(ViewGroup viewGroup) {
        List<NumberPicker> npList = findNumberPicker(viewGroup);
        if (null != npList) {
            for (NumberPicker mMinuteSpinner : npList) {
                System.out.println("mMinuteSpinner.toString()=" + mMinuteSpinner.toString());
                if (mMinuteSpinner.toString().contains("id/minute")) {
                    mMinuteSpinner.setVisibility(GONE);
                }
                mMinuteSpinner.setOnScrollListener(new NumberPicker.OnScrollListener() {
                    @Override
                    public void onScrollStateChange(NumberPicker view, int scrollState) {
                        tv_date.setText(initDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour()));
                    }
                });

            }
        }
    }
    private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
        List<NumberPicker> npList = new ArrayList<NumberPicker>();
        View child = null;
        if (null != viewGroup)
        {
            for (int i = 0; i < viewGroup.getChildCount(); i++)
            {
                child = viewGroup.getChildAt(i);
                if(child instanceof AppCompatTextView)
                    child.setVisibility(GONE);
                if (child instanceof NumberPicker)
                {
                    npList.add((NumberPicker)child);
                }
                else if (child instanceof LinearLayout)
                {
                    List<NumberPicker> result = findNumberPicker((ViewGroup)child);
                    if (result.size() > 0)
                    {
                        return result;
                    }
                }

            }
        }
        return npList;
    }
    public void setDate(String date){
        Date currentDate = null;
        try {
            currentDate = new SimpleDateFormat(getContext().getString(R.string.date_type)).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(currentDate);
        datePicker.updateDate(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        timePicker.setCurrentHour(currentDate.getHours());
    }
}
