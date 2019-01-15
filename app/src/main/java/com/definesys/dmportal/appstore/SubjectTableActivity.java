package com.definesys.dmportal.appstore;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.widget.PopupWindowCompat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.CursorArg;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.customViews.SelectWeekView;
import com.definesys.dmportal.appstore.customViews.SubjectDetailView;
import com.definesys.dmportal.appstore.presenter.GetTableInfoPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

@Route(path = ARouterConstants.SubjectTableActivity)
public class SubjectTableActivity extends BaseActivity<GetTableInfoPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;
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
    @BindView(R.id.current_week_text)
    TextView tv_current_week;
    @BindView(R.id.current_show_layout)
    LinearLayout lg_current_week;

    private List<TextView> textViewList;//周一到周日每节课的textView
    private SubjectTable subjectTable;//课表信息
    private int currentShowWeek=1;//当前显示的周数
    private Dialog dialog;//课程详细信息提示框
    private SubjectDetailView subjectDetailDialog;//提示框里的详细内容
    private SelectWeekView selectWeekView;//选择周数界面
    private PopupWindow popupWindow;//选择周数的提示框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_table);
        ButterKnife.bind(this);
        initView();
        initDialog(null,0,0,null);//初始化课程详情对话框
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.subject_table));
        titleBar.setBackgroundDividerEnabled(false);
        //titleBar.setBackground(null);
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj->finish());
        //上一周
        RxView.clicks(tv_pre)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        onClickWeek(tv_pre);
                        if(subjectTable==null) {
                            httpPost();
                            return;
                        }
                        if(currentShowWeek>1) {
                            --currentShowWeek;
                            initTable();
                        }
                        else
                            Toast.makeText(SubjectTableActivity.this, R.string.alread_first_week,Toast.LENGTH_SHORT).show();
                    }
                });
        //下一周
        RxView.clicks(tv_next)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        onClickWeek(tv_next);
                        if(subjectTable==null) {
                            httpPost();
                            return;
                        }
                        if(currentShowWeek<subjectTable.getSumWeek()) {
                            ++currentShowWeek;
                            initTable();
                        }
                        else
                            Toast.makeText(SubjectTableActivity.this, R.string.already_last_week,Toast.LENGTH_SHORT).show();
                    }
                });
        //选择要显示的周数
        RxView.clicks(lg_current_week)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->{
                    initSelectWeek();
                });
        tv_hello.setText(getString(R.string.table_hello_tip,SharedPreferencesUtil.getInstance().getUserName(),SharedPreferencesUtil.getInstance().getUserId()));
        tv_current_week.setText(getString(R.string.current_week,0));
        tv_show.setText(getString(R.string.current_show_week,0));
        textViewList = new ArrayList<>();
        getTextView(lg_first,1);
        getTextView(lg_second,2);
        getTextView(lg_third,3);
        getTextView(lg_fourth,4);
        getTextView(lg_fifth,5);
        getTextView(lg_sixth,6);
//        for(int i = 0 ; i<textViewList.size();i++){
//            textViewList.get(i).setText(""+i);
//        }
        httpPost();//网络请求
    }

    /**
     * 手动选择第几周
     */
    private void initSelectWeek() {
        if(subjectTable==null){
            httpPost();
            return;
        }
        if(popupWindow==null) {
            selectWeekView = new SelectWeekView(this);
            selectWeekView.setReasonlist(subjectTable.getSumWeek(),currentShowWeek);
            popupWindow = new PopupWindow(selectWeekView,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);
            selectWeekView.setMyClickListener(new SelectWeekView.MyClickListener() {
                @Override
                public void onClick(int week) {
                    popupWindow.dismiss();
                    currentShowWeek = week;
                    initTable();
                }
            });
            popupWindow.getContentView().measure(0,0);
        }else {
            int offsetX = lg_current_week.getWidth() - popupWindow.getContentView().getMeasuredWidth();
            int offsetY = 0;
            selectWeekView.setCurrentWeek(currentShowWeek);
            PopupWindowCompat.showAsDropDown(popupWindow, lg_current_week, offsetX, offsetY, Gravity.START);

        }

    }

    /**
     * 点击了上一周、下一周
     * @param tv_temp
     */
    private void onClickWeek(TextView tv_temp) {
        tv_temp.setBackgroundColor(getResources().getColor(R.color.bg_gray));
        tv_temp.setTextColor(getResources().getColor(R.color.buttonBlue));
        Observable//5秒未获取数据则加载失败
                .timer(Constants.clickdelay/2, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        tv_temp.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_temp.setTextColor(getResources().getColor(R.color.blue));
                    }
                });
    }

    /**
     * 获取课表信息失败
     * @param msg
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(SubjectTableActivity.this, R.string.net_work_error,Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }

    /**
     * 获取课表信息成功
     * @param data
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_TABLE_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getTableInfo(BaseResponse<SubjectTable> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            subjectTable = data.getData();
            //设置开始时间为00时00分00秒
            subjectTable.setStartDate(DensityUtil.setDate(subjectTable.getStartDate(),true));
            //设置开始时间为23时59分59秒
            subjectTable.setEndDate(DensityUtil.setDate(subjectTable.getEndDate(),false));
            if(System.currentTimeMillis()<=subjectTable.getEndDate().getTime()) {//当前时间《=本学期结课时间
                currentShowWeek = (int) ((System.currentTimeMillis() - subjectTable.getStartDate().getTime()) / (7 * Constants.oneDay));
            }
            //当前周数
            tv_current_week.setText(getString(R.string.current_week,currentShowWeek));
            //本学期总周数
            subjectTable.setSumWeek((int)Math.ceil((float)(subjectTable.getEndDate().getTime()-subjectTable.getStartDate().getTime())/(7*Constants.oneDay)));
            initTable();
            initSelectWeek();
            progressHUD.dismiss();
        }
    }
    //网络请求
    private void httpPost() {
        progressHUD.show();
        mPersenter.getTableInfo(SharedPreferencesUtil.getInstance().getUserId(),SharedPreferencesUtil.getInstance().getFaculty());
    }

    /**
     * 初始化课表
     */
    private void initTable() {
        tv_show.setText(getString(R.string.current_show_week,currentShowWeek));
        clearTable();
        if(subjectTable.getCursorArgList()==null||subjectTable.getCursorArgList().size()==0)
            return;
        else {
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
                                    int finalI = i;
                                    int finalJ = j;
                                    int finalK = k;
                                    RxView.clicks(textViewList.get(6-j+(7-k)*7))
                                            .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                                            .subscribe(obj->{
                                                initDialog(list.get(finalI),6- finalJ,8- finalK,textViewList.get(6-finalJ+(7-finalK)*7));
                                            });
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     *点击课表中的某节课，弹出弹窗
     * @param cursorArg 当前课的所有信息
     * @param weekDay 星期几
     * @param pitch 第几节
     */
    private void initDialog(CursorArg cursorArg,int weekDay,int pitch,TextView tv_temp) {
        if(dialog==null||tv_temp==null) {
            dialog = new Dialog(this);
            subjectDetailDialog = new SubjectDetailView(this);
            subjectDetailDialog.setOnClickConfirmListener(new SubjectDetailView.OnClickConfirmListener() {
                @Override
                public void onClickConfirm() {
                    dialog.dismiss();
                }
            });
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(subjectDetailDialog);
            dialog.setCancelable(true);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else {
            tv_temp.setBackgroundColor(getResources().getColor(R.color.bg_gray));
            tv_temp.setTextColor(getResources().getColor(R.color.buttonBlue));
            subjectDetailDialog.updateData(cursorArg,currentShowWeek,weekDay,pitch,DensityUtil.checkClassRoom(weekDay,cursorArg.getClassroom()));
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(tv_temp!=null) {
                        tv_temp.setBackgroundColor(getResources().getColor(R.color.white));
                        tv_temp.setTextColor(getResources().getColor(R.color.black));
                    }
                }
            });
            dialog.show();
        }
    }

    /**
     * 获取每个布局中显示课程的textView
     * @param linearLayout
     * @param pitch 第pitch节课
     */
    private void getTextView(LinearLayout linearLayout,int pitch){
        ((TextView) linearLayout.getChildAt(0)).setText(""+pitch);
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
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPersenter().unsubscribe();

    }

    @Override
    public  GetTableInfoPresenter getPersenter() {

        return new GetTableInfoPresenter(this);
    }
}
