package com.definesys.dmportal.appstore.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.SubjectTableActivity;
import com.definesys.dmportal.appstore.bean.CursorArg;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 *
 * Created by 羽翎 on 2019/1/9.
 */

public class SubjectTableView extends LinearLayout {
    private Context mContext;

    @BindView(R.id.pre_week)
    TextView tv_pre;
    @BindView(R.id.next_week)
    TextView tv_next;
    @BindView(R.id.cur_show)
    TextView tv_show;
    @BindView(R.id.hello_text)
    TextView tv_hello;
    @BindView(R.id.first_layout)
    LinearLayout lg_first;
    @BindView(R.id.second_layout)
    LinearLayout lg_second;
    @BindView(R.id.third_layout)
    LinearLayout lg_third;
    @BindView(R.id.fourth_layout)
    LinearLayout lg_fourth;
    @BindView(R.id.fifth_layout)
    LinearLayout lg_fifth;
    @BindView(R.id.sixth_layout)
    LinearLayout lg_sixth;

    @BindView(R.id.current_show_layout)
    LinearLayout lg_current_week;

    @BindView(R.id.current_week_text)
    TextView tv_selectCount;

    @BindView(R.id.confirm_text)
    TextView tv_confirm;
    @BindView(R.id.cancel_text)
    TextView tv_cancel;
    private List<TextView> textViewList;//周一到周日每节课的textView
    private SubjectTable subjectTable;
    private int currentWeek;//本周
    private int currentDay;//今天是星期几
    private int currentShowWeek;//当前显示的周
    private HashMap<Integer,String> hashMap;//Integer xxik 第xx周,星期i，第k节课 String:课程名
    private MyOnClickListener myOnClickListener;//点击事件接口

    public SubjectTableView(Context context) {
        super(context);
        initView(context);
    }

    public SubjectTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SubjectTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.view_subject_table,this);
        ButterKnife.bind(this);
        textViewList = new ArrayList<>();

        getTextView(lg_first,1);
        getTextView(lg_second,2);
        getTextView(lg_third,3);
        getTextView(lg_fourth,4);
        getTextView(lg_fifth,5);
        getTextView(lg_sixth,6);
        //上一周
        RxView.clicks(tv_pre)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    onClickWeek(tv_pre);
                    if(currentShowWeek>currentWeek) {
                        --currentShowWeek;
                        clearTable();
                        initTable();
                    }
                    else
                        Toast.makeText(mContext, R.string.select_fail_tip_3,Toast.LENGTH_SHORT).show();
                });
        //下一周
        RxView.clicks(tv_next)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    onClickWeek(tv_next);
                    if(currentShowWeek>=subjectTable.getSumWeek()) {//最后一周
                        Toast.makeText(mContext, R.string.already_last_week,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(currentShowWeek-currentWeek>=1) {//下一周
                        Toast.makeText(mContext, R.string.select_fail_tip_3, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ++currentShowWeek;
                    clearTable();
                    initTable();

                });
        RxView.clicks(tv_confirm)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(myOnClickListener!=null)
                        myOnClickListener.onConfirmClick(hashMap);
                });
        RxView.clicks(tv_cancel)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    if(myOnClickListener!=null)
                        myOnClickListener.onCancelClick();
                });
    }
    /**
     * 获取每个布局中显示课程的textView
     * @param linearLayout l
     * @param pitch 第pitch节课
     */
    private void getTextView(LinearLayout linearLayout,int pitch){
        ((TextView) linearLayout.getChildAt(0)).setText(String.valueOf(pitch));
        for(int i=1;i<linearLayout.getChildCount();i++){
            textViewList.add((TextView) linearLayout.getChildAt(i));
        }
    }

    /**
     * 课表置空
     * 清除点击事件
     */
    private void clearTable(){
        for(int i = 0 ; i<textViewList.size();i++){
            textViewList.get(i).setText("");
            textViewList.get(i).setOnClickListener(null);
            textViewList.get(i).setBackgroundColor(getResources().getColor(R.color.white));
            textViewList.get(i).setTextColor(getResources().getColor(R.color.black));
        }
    }

    /**
     *
     * @param subjectTable 课表信息
     * @param currentWeek 当前周数
     */
    public void setData(SubjectTable subjectTable, int currentWeek){
        this.subjectTable = subjectTable;
        this.currentWeek = currentWeek;
        this.currentShowWeek = currentWeek;
        Calendar c= Calendar.getInstance();
        c.setTime(new Date());
        currentDay = (c.get(Calendar.DAY_OF_WEEK)-1)>0?c.get(Calendar.DAY_OF_WEEK)-1:7;
        hashMap = new HashMap<>();
        initTable();
    }
    /**
     * 初始化课表
     */
    @SuppressLint("SetTextI18n")
    private void initTable() {
        tv_show.setText(mContext.getString(R.string.current_show_week,currentShowWeek));
        tv_selectCount.setText(mContext.getString(R.string.select_subject_count,hashMap.size()));
        if(subjectTable.getCursorArgList()!=null&&subjectTable.getCursorArgList().size()>0) {
            List<CursorArg> list=subjectTable.getCursorArgList();
            for(int i = 0 ; i <list.size();i++){
                if(currentShowWeek>=list.get(i).getStartWeek()&&currentShowWeek<=list.get(i).getEndWeek()&&list.get(i).getCursorName()!=null&&!"".equals(list.get(i).getCursorName())){//当前周有课
                    String weekDay=list.get(i).getWeekDay();//星期几上课
                    String pitch = list.get(i).getPitch();//这天第几节有课
                    int position = pitch.length()-1;//要取字符串位置
                    for(int j = weekDay.length()-1;j>=0;j--){
                        if(weekDay.charAt(j)=='1') {//这天有课
                            //16进制转2进制String
                            String result = DensityUtil.getPitchString(pitch.charAt(position-1))+DensityUtil.getPitchString(pitch.charAt(position));
                            position-=2;
                            for(int k = result.length()-1 ; k>=2;k--){
                                if(result.charAt(k)=='1'){
                                    //第currentShowWeek周的星期7-j的第8-k节有i课
                                    textViewList.get(6-j+(7-k)*7).setText(list.get(i).getCursorName()+"\n"+DensityUtil.checkClassRoom(7-j,list.get(i).getClassroom()));
                                    if(hashMap.get(currentShowWeek*100+(7-j)*10+8-k)!=null){//当前课被选中
                                        textViewList.get(6-j+(7-k)*7).setBackgroundColor(getResources().getColor(R.color.bg_gray));
                                        textViewList.get(6-j+(7-k)*7).setTextColor(getResources().getColor(R.color.buttonBlue));
                                    }else {
                                        textViewList.get(6-j+(7-k)*7).setBackgroundColor(getResources().getColor(R.color.white));
                                        textViewList.get(6-j+(7-k)*7).setTextColor(getResources().getColor(R.color.black));
                                    }
                                    int finalJ = j;
                                    int finalK = k;
                                    int finalI = i;
                                    RxView.clicks(textViewList.get(6-j+(7-k)*7))
                                            .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                                            .subscribe(obj->
                                                initEvent(list.get(finalI).getCursorName(),7- finalJ,8- finalK,textViewList.get(6-finalJ+(7-finalK)*7))
                                            );
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    /**
     *点击事件
     * @param cursorName 课程名
     * @param weekDay 星期几
     * @param pitch 第几节
     * @param textView t
     */
    private void initEvent(String cursorName, int weekDay, int pitch, TextView textView) {
        if(currentShowWeek==currentWeek&&weekDay<currentDay) {
            Toast.makeText(mContext, R.string.select_fail_tip_1, Toast.LENGTH_SHORT).show();
            return;
        }
        int position = currentShowWeek*100+weekDay*10+pitch; //xxik 第xx周星期i，第k节课
        //是否包含此元素
        if(hashMap.get(position)!=null)
            hashMap.remove(position);
        else {
            if(hashMap.size()>=6) {//是否已选择6节课
                Toast.makeText(mContext, R.string.select_fail_tip_2, Toast.LENGTH_SHORT).show();
                return;
            }
            Date date = new Date();
            //当前时间大于请假课程的结束时间
            if(date.getTime()> DensityUtil.initSujectTime(subjectTable.getStartDate(),position,true).getTime()){
                Toast.makeText(mContext, R.string.select_fail_tip_4, Toast.LENGTH_SHORT).show();
                return;
            }
            hashMap.put(position,cursorName);
        }
        tv_selectCount.setText(mContext.getString(R.string.select_subject_count,hashMap.size()));
        textView.setTextColor(hashMap.get(position)!=null?getResources().getColor(R.color.buttonBlue): Color.BLACK);
        textView.setBackgroundColor(hashMap.get(position)!=null?getResources().getColor(R.color.bg_gray):Color.WHITE);
    }

    /**
     * 点击了上一周、下一周
     * @param tv_temp t
     */
    private void onClickWeek(TextView tv_temp) {
        tv_temp.setBackgroundColor(getResources().getColor(R.color.bg_gray));
        tv_temp.setTextColor(getResources().getColor(R.color.buttonBlue));
        Observable//5秒未获取数据则加载失败
                .timer(Constants.clickdelay/2, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    tv_temp.setBackgroundColor(getResources().getColor(R.color.white));
                    tv_temp.setTextColor(getResources().getColor(R.color.blue));
                });
    }

    public interface MyOnClickListener{
        void onConfirmClick(HashMap<Integer,String> hashMap);//确定
        void onCancelClick();//取消
    }

    /**
     * 清空hasSet
     */
    public void clearHashSet() {
        hashMap.clear();
        currentShowWeek = currentWeek;
        initTable();
    }

    public void setMyOnClickListener(MyOnClickListener myOnClickListener) {
        this.myOnClickListener = myOnClickListener;
    }
}
