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

public class MyDatePicker extends LinearLayout {

    @BindView(R.id.text_cancel)
    TextView cancelText;

    @BindView(R.id.text_confirm)
    TextView confirmText;
    @BindView(R.id.num_year)
    NumberPicker num_year;

    @BindView(R.id.num_month)
    NumberPicker num_month;

    @BindView(R.id.num_day)
    NumberPicker num_day;

    @BindView(R.id.num_hour)
    NumberPicker num_hour;

    @BindView(R.id.date_text)
    TextView tv_date;
    private int currentYear;
    private int currentMonth;
    private int currentDay;
    private Calendar cal = Calendar.getInstance();
    private onClickEventListener myListener;
    private Context mContent;

    public MyDatePicker(Context context) {
        super(context);
        initView(context);
    }
    public MyDatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyDatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context context) {
        mContent = context;
        LayoutInflater.from(mContent).inflate(R.layout.view_date_picker, this);
        ButterKnife.bind(this);
        currentYear = cal.get(Calendar.YEAR);
        currentMonth = cal.get(Calendar.MONTH)+1;
        currentDay = cal.get(Calendar.DAY_OF_MONTH);
        //设置不可编辑
        num_year.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        num_month.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        num_day.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        num_hour.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //这里设置为不循环显示，默认值为true
        num_year.setWrapSelectorWheel(false);
        initStatus();//年月日设置
        //设置小时
        num_hour.setMaxValue(23);
        tv_date.setText((new SimpleDateFormat(getContext().getString(R.string.date_type)).format(new Date())));
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
                            myListener.onConfirm(initDate(num_year.getValue(), num_month.getValue(), num_day.getValue(), num_hour.getValue()));
                    }
                });
        num_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    changeStatus();
            }
        });
        num_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                changeStatus();
            }
        });
        num_day.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tv_date.setText(initDate(num_year.getValue(), num_month.getValue(), num_day.getValue(), num_hour.getValue()));
            }
        });
        num_hour.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tv_date.setText(initDate(num_year.getValue(), num_month.getValue(), num_day.getValue(), num_hour.getValue()));
            }
        });
    }

    /**
     * 改变数值
     */
    private void changeStatus() {
        if(num_year.getValue()==currentYear||(num_year.getValue()==currentYear&&num_month.getValue()==currentMonth))
            initStatus();
        else {
            num_month.setMinValue(1);
            num_day.setMinValue(1);
            num_day.setMaxValue(getDays(num_year.getValue(), num_month.getValue()));
        }
        tv_date.setText(initDate(num_year.getValue(), num_month.getValue(), num_day.getValue(), num_hour.getValue()));
    }

    /**
     * 初始化年月日
     */
    private void initStatus() {
        //设置年份
        num_year.setMinValue(currentYear);
        num_year.setMaxValue(9999);
        num_year.setValue(currentYear);
        //设置月份
        num_month.setMinValue(currentMonth);
        num_month.setMaxValue(12);
        if(num_month.getValue()<currentMonth)
        num_month.setValue(currentMonth);
        //设置天数
        num_day.setMinValue(currentDay);
        num_day.setMaxValue(getDays(currentYear,num_month.getValue()));
    }

    private String initDate(int year,int month,int day,int hour) {
        return mContent.getString(R.string.date_des,year,month,day,hour);
//        ""+year+"年"+String.format("%02d",month)+"月"+String.format("%02d",(day))+"日 "+String.format("%02d",(hour))+"时";
    }

    public interface onClickEventListener{
        public void onCancel();
        public void onConfirm(String date);
    }

    public void setMyListener(onClickEventListener myListener) {
        this.myListener = myListener;
    }


    public void setDate(String date){
        Date currentDate = null;
        try {
            currentDate = new SimpleDateFormat(getContext().getString(R.string.date_type)).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cal.setTime(currentDate);
//        num_year.setValue(cal.get(Calendar.YEAR));
        num_month.setValue(cal.get(Calendar.MONTH)+1);
        num_day.setMaxValue(getDays(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1));
        num_day.setValue(cal.get(Calendar.DAY_OF_MONTH));
        num_hour.setValue(currentDate.getHours());
//        changeStatus();

    }
    /**
     * 计算当前月有多少天
     * @return
     */
    public int getDays(int year, int month) {
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;

            }
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
            else
                days = 28;
        }
        return days;
    }
}
