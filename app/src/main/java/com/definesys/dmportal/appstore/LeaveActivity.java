package com.definesys.dmportal.appstore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.ReasonImageAdapter;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.customViews.MyDatePicker;
import com.definesys.dmportal.appstore.customViews.ReasonTypeListLayout;
import com.definesys.dmportal.appstore.customViews.SubjectTableView;
import com.definesys.dmportal.appstore.customViews.SubmitLeaveInfoView;
import com.definesys.dmportal.appstore.presenter.GetTableInfoPresenter;
import com.definesys.dmportal.appstore.presenter.LeaveRequestPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.HddLayoutHeight;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.vise.xsnow.http.ViseHttp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.definesys.dmportal.appstore.utils.Constants.oneDay;

@Route(path = ARouterConstants.LeaveActivity)
public class LeaveActivity extends BaseActivity<LeaveRequestPresenter> {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.subject_select_info)
    ImageView img_subIcon;

    @BindView(R.id.type_text)
    TextView tv_type;

    @BindView(R.id.type_reason_text)
    TextView tv_typeReason;

    @BindView(R.id.text_name)
    TextView tv_name;

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;

    @BindView(R.id.img_count)
    TextView tv_imgCount;

    @BindView(R.id.leave_start_layout)
    LinearLayout lg_leaveStart;

    @BindView(R.id.leave_end_layout)
    LinearLayout lg_leaveEnd;

    @BindView(R.id.type_layout)
    LinearLayout lg_type;

    @BindView(R.id.reason_layout)
    LinearLayout lg_reason;

    @BindView(R.id.type_reason_layout)
    LinearLayout lg_typeReason;

    @BindView(R.id.subject_table_layout)
    LinearLayout lg_table;

    @BindView(R.id.leave_day_count_layout)
    LinearLayout lg_timeCount;

   @BindView(R.id.scorll_view)
    ScrollView sc_scroll;

    @BindView(R.id.end_time_text)
    TextView tv_timeEnd;

    @BindView(R.id.start_time_text)
    TextView tv_timeStart;

    @BindView(R.id.leave_day_count_text)
    TextView tv_dayOffCount;

    @BindView(R.id.count_word_text)
    TextView tv_count;

    @BindView(R.id.ed_reason)
    EditText ed_reason;

    @BindView(R.id.content_layout)
    LinearLayout main;

    private ReasonImageAdapter leaveImgAdapter;//图片适配器
    private List<LocalMedia> selectImages;//选择的图片

    private Date startDate;
    private Date endDate;
    private SimpleDateFormat df;
    private boolean isStart;//用户点击的是开始日期还是结束日期
    private Dialog dateDialog;//日期选择提示框
    private MyDatePicker datePick;//日期选择Picker
    private Dialog typeDialog;//请假类型提示框
    private Dialog reasonDialog;//请假原因提示框
    private Dialog subjectDialog;//课程选择提示框
    private Dialog subjectelectDialog;//查看已选择请假的课程列表提示框
    private int selectTypePosition=1;//请假类型 0：课假 1.短假 2.长假
    private ReasonTypeListLayout reasonListView;//请假原因视图
    private ReasonTypeListLayout selectedSubjectView;//查看已选择请假的课程列表视图
    private SubjectTableView subjectTableView;//课表视图
    private SubjectTable subjectTableInfo;//课表信息
    private HashMap<Integer,String> hashMap;//选择的课程 xxik 第xx周,星期i，第k节课

    //班长、班主任、导员、导师、教务处、任课老师
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_off);
        ButterKnife.bind(this);

        df = new SimpleDateFormat(getString(R.string.date_type), Locale.getDefault());
        initView();
        initEdit();//编辑框
        initPictureList();//添加图片的列表
        initTypeDialog();//初始化请假类型提示框
        initDateDialog(false);//初始化日期选择提示框
        setLatoutVisibility();//根据请假类型设定显示的内容

    }

    private void initView() {
        tv_name.setText(SharedPreferencesUtil.getInstance().getUserName());
        titleBar.setTitle(getString(R.string.leave_off));
        titleBar.setBackgroundDividerEnabled(false);
        titleBar.setBackground(getResources().getDrawable(R.drawable.title_bg));
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED,intent);
                    finish();
                });
        Button button = titleBar.addRightTextButton(getString(R.string.submit),R.layout.activity_leave_off);
        button.setTextSize(14);
        //提交
        RxView.clicks(button)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj-> checkSelect());
        //请假类型
        RxView.clicks(lg_type)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj-> typeDialog.show());
        //请假原因
        RxView.clicks(lg_typeReason)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj-> {
                    reasonListView.setReasonlist(getResources().getStringArray(selectTypePosition <=1 ? R.array.leave_short_reason : R.array.leave_long_reason));
                    reasonDialog.show();
                });
        //课程选择
        RxView.clicks(lg_table)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj-> initSubjectDialog(true));
        //请假结束时间
        RxView.clicks(lg_leaveEnd)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> initDateDialog(false));
        //请假开始时间
        RxView.clicks(lg_leaveStart)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> initDateDialog(true));
        //时长
        RxView.clicks(lg_timeCount)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obj -> {
                    if(img_subIcon.getVisibility()==View.VISIBLE&&subjectelectDialog!=null){
                        subjectelectDialog.show();
                    }
                });
        //默认请假类型 短假
        tv_type.setText(DensityUtil.setTypeText(getResources().getStringArray(R.array.leave_type)[selectTypePosition]));
        //默认请假原因 身体欠佳
        tv_typeReason.setText(getResources().getStringArray(R.array.leave_short_reason)[0]);
    }

    //具体原因编辑框设置
    private void initEdit() {
        tv_count.setText(getString(R.string.word_count, 0));
        RxView.clicks(ed_reason)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(obj->
                    ed_reason.setCursorVisible(true)
                );
        //获取焦点
        ed_reason.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                ed_reason.setCursorVisible(true);
            }
        });
        /*
        监听输入框内容 《==》 获取输入长度显示到界面
         */
        ed_reason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tv_count.setText(getString(R.string.word_count, ed_reason.getText().toString().length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // 防遮挡
        new HddLayoutHeight().addLayoutListener(this,main, tv_count,1);
    }
    //照片列表
    private void initPictureList() {
        selectImages = new ArrayList<>();
        tv_imgCount.setText(getString(R.string.img_count, 0));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));

        leaveImgAdapter = new ReasonImageAdapter(this, selectImages);
        recyclerView.setAdapter(leaveImgAdapter);

        // 自定义图片控件的点击事件
        leaveImgAdapter.setOnClickListener(new ReasonImageAdapter.OnClickListener() {
            @Override
            public void onBackgroundClick(int position) {
                if(position == 0){
                    //打开相册·拍摄照片
                    PictureSelector.create(LeaveActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .maxSelectNum(3)
                            .compress(true)
                            .selectionMedia(selectImages)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                }else {
                    //放大已拍摄图片
                    PictureSelector.create(LeaveActivity.this).
                            externalPicturePreview(position-1, selectImages);
                }
            }

            @Override
            public void onForegroundClick(int position) {
                // 删除选中的图片
                selectImages.remove(position-1);
                leaveImgAdapter.notifyDataSetChanged();
                tv_imgCount.setText(getString(R.string.img_count, leaveImgAdapter.getItemCount()-1));
            }
        });
    }

    /**
     * 初始化开始日期、结束日期
     */
    private void initDate() {
        startDate = new Date(System.currentTimeMillis());
        endDate = new Date(System.currentTimeMillis()+Constants.oneDay*7);
        tv_timeEnd.setText(df.format(endDate));
        tv_timeStart.setText(df.format(startDate));
    }
    //请假类型选择
    private void initTypeDialog() {
        typeDialog = new Dialog(this);
        ReasonTypeListLayout reasonTypeListLayout = new ReasonTypeListLayout(this);
        reasonTypeListLayout.getTitleText().setText(R.string.reason_des);
        reasonTypeListLayout.setReasonlist(getResources().getStringArray(R.array.leave_type));
        reasonTypeListLayout.setMyClickListener((type, position) -> {
            tv_type.setText(DensityUtil.setTypeText(type));
            if(selectTypePosition!=position) {
                selectTypePosition = position;
                setLatoutVisibility();
            }
            typeDialog.dismiss();
        });
        typeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        typeDialog.setContentView(reasonTypeListLayout);
        typeDialog.setCancelable(true);
        if(typeDialog.getWindow()!=null)
        typeDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }



    //日期选择提示框
    private void initDateDialog(boolean flag) {
        isStart = flag;
        if(dateDialog==null) {
            datePick = new MyDatePicker(this, null);
            dateDialog = new Dialog(this, R.style.BottomDialog);
            datePick.setMyListener(new MyDatePicker.onClickEventListener() {
                @Override
                public void onCancel() {
                    dateDialog.dismiss();
                }

                @Override
                public void onConfirm(String date) {
                    dateDialog.dismiss();
                    int checkCode = checkDate(isStart, date);
                    if(checkCode==0){
                        Toast.makeText(LeaveActivity.this, R.string.time_fail_tip, Toast.LENGTH_SHORT).show();
                        return;
                    }else if(checkCode==2){
                        Toast.makeText(LeaveActivity.this, R.string.time_fail_tip_2, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(selectTypePosition==1&&(endDate.getTime()-startDate.getTime())/(float)Constants.oneDay>7){//短假不在1-7天以内
                        Toast.makeText(LeaveActivity.this, R.string.time_fail_tip_3, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(selectTypePosition==2&&(endDate.getTime()-startDate.getTime())/(float)Constants.oneDay<=7){
                        Toast.makeText(LeaveActivity.this, R.string.time_fail_tip_4, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isStart)
                        tv_timeStart.setText(date);
                    else
                        tv_timeEnd.setText(date);
                    initTime();//更新开始、结束时间

                }
            });
            dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dateDialog.getWindow();
            if(window!=null)
                window.setGravity(Gravity.BOTTOM);
            dateDialog.setContentView(datePick);
            dateDialog.setCancelable(true);
            dateDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else {
            datePick.setDate(isStart ? tv_timeStart.getText().toString() : tv_timeEnd.getText().toString());
            dateDialog.show();
        }

    }

    /**
     * 计算并显示开始时间与结束时间之间的差值
     */
    @SuppressLint("SetTextI18n")
    private void initTime() {
        startDate = null;
        endDate = null;
        try {
            startDate = df.parse(tv_timeStart.getText().toString());
            endDate = df.parse(tv_timeEnd.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = endDate.getTime() - startDate.getTime();
        int day = (int)(time/oneDay );
        int hour = (int)(time/(oneDay /24))-day*24;
        tv_dayOffCount.setText((day>0?getString(R.string.off_day,day):"")+(day>0&&hour==0?"":getString(R.string.off_hour,hour)));
    }

    /**
     * 检测选择的时间是否比另一个时间小
     * @param isStart 选择的是否是开始时间
     * @param date 选择的日期
     * @return 0.结束时间小于当前时间 1.正常 2.结束时间小于开始时间
     */
    private int checkDate(boolean isStart, String date) {
        startDate = null;
        endDate = null;
        try {
            startDate = isStart?df.parse(date):df.parse(tv_timeStart.getText().toString());
            endDate = isStart?df.parse(tv_timeEnd.getText().toString()):df.parse(date);
            if(endDate.before(new Date()))
                return 0;
            return startDate.before(endDate)?1:2;
        } catch (ParseException e) {
            e.printStackTrace();
            return 2;
        }
    }

    /**
     * 提交合法性检查
     */
    private void checkSelect() {
        if(selectTypePosition==0){//课假
            if(hashMap==null||hashMap.size()==0) {//未选择课程
                Toast.makeText(this, R.string.no_subject_select,Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if("".equals(ed_reason.getText().toString())){
            Toast.makeText(this, R.string.no_reason_des,Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {
                Log.d("mydemo","Height=="+sc_scroll.getMeasuredHeight());
                sc_scroll.scrollTo(0,(int)lg_reason.getY());
            }, Constants.scrollDelay);
            ed_reason.setFocusable(true);
            ed_reason.setFocusableInTouchMode(true);
            ed_reason.requestFocus();
            ed_reason.findFocus();
            return;
        }
        if(getString(R.string.shixi).equals(tv_typeReason.getText().toString())){//如果是实习，进行规范性检测
            String content = ed_reason.getText().toString().trim();
            if(!(content.contains("+")&&content.indexOf("+")<content.length()-1&&content.indexOf("+")>0&&content.split("\\+").length==2)) {
                Toast.makeText(this, R.string.no_reason_des_2, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(selectTypePosition==2&&(Math.ceil(endDate.getTime()-startDate.getTime())/Constants.oneDay)<=7){
            Toast.makeText(this, R.string.time_fail_tip_4,Toast.LENGTH_SHORT).show();
            return;
        }
        initSubmitDialog();
    }

    /**
     * 提交的确认提示框
     */
    private void initSubmitDialog() {
        String name = SharedPreferencesUtil.getInstance().getUserName();
        Number id = SharedPreferencesUtil.getInstance().getUserId();
        String title = tv_typeReason.getText().toString();
        String startTime = selectTypePosition==0?"":tv_timeStart.getText().toString();
        String endTime = selectTypePosition==0?"":tv_timeEnd.getText().toString();
        String sumTime = tv_dayOffCount.getText().toString();
        String content = "\n  "+ed_reason.getText().toString().trim();
        StringBuilder selectedSubject= new StringBuilder();
        int type = selectTypePosition;
//        if(selectTypePosition>1&&getString(R.string.shixi).equals(tv_typeReason.getText().toString()))
//            type=3;
        if(type==0) {//课假
            //对hashMap排序
            //这里将map.entrySet()转换成list
            List<Map.Entry<Integer,String>> list = DensityUtil.sort(hashMap);
            int i = 1;
            for (Map.Entry<Integer, String> entry :list) {
                int week = entry.getKey() / 100;//第几周
                int day = entry.getKey() % 100 / 10;//星期几
                int pitch = entry.getKey() % 10;//第几节课
                selectedSubject.append("<br />" + "<font color='#37a0d2'>").append(getString(R.string.subject_count_tip, i)).append("</font>").append(entry.getValue()).append("<br />").append("&nbsp;&nbsp;&nbsp;").append(getString(R.string.selected_subject_info, week, day, pitch));
                ++i;
            }
            //结束时间
            endTime=df.format(DensityUtil.initSujectTime(subjectTableInfo.getStartDate(),list.get(list.size()-1).getKey(),true));
            //开始时间
            startTime=df.format(DensityUtil.initSujectTime(subjectTableInfo.getStartDate(),list.get(0).getKey(),false));
        }

        //(Number id,String name, String content, String startTime, String endTime, String leaveType, String leaveTitle, String subTime, String selectedSubject)
        LeaveInfo submitLeaveInfo = new LeaveInfo(id,name,content,startTime,endTime,title,sumTime, selectedSubject.toString(),type,SharedPreferencesUtil.getInstance().getUserType());

        Dialog dialog = new Dialog(this);
        SubmitLeaveInfoView submitLeaveInfoView = new SubmitLeaveInfoView(this);
        submitLeaveInfoView.setDate(submitLeaveInfo,selectImages);
        //设置确定、取消点击事件
        submitLeaveInfoView .setOnClickListener(new SubmitLeaveInfoView.OnClickListener() {
            @Override
            public void onClickConfirm() {
                dialog.dismiss();
                mPersenter.getRequestResult(submitLeaveInfo,selectImages);
                progressHUD.show();
            }

            @Override
            public void onCancelClick() {
                dialog.dismiss();
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(submitLeaveInfoView);
        dialog.setCancelable(true);
        if(dialog.getWindow()!=null)
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    /**
     * 根据请假类型设置接下来要进行设置的选项
     */
    private void setLatoutVisibility(){
//        isScroll = false;
        ViseHttp.cancelTag(HttpConst.getTable);//停止申请课表的网络请求
        if(selectTypePosition==0){//课假
            lg_table.setVisibility(View.VISIBLE);
            lg_leaveEnd.setVisibility(View.GONE);
            lg_leaveStart.setVisibility(View.GONE);
            tv_dayOffCount.setText(getString(R.string.off_suject,0));
            img_subIcon.setVisibility(View.GONE);
            if(subjectTableView!=null)
                subjectTableView.clearHashSet();
            initSubjectDialog(false);
        }else {//短假或长假
            lg_table.setVisibility(View.GONE);
            img_subIcon.setVisibility(View.GONE);
            lg_leaveEnd.setVisibility(View.VISIBLE);
            lg_leaveStart.setVisibility(View.VISIBLE);
            tv_dayOffCount.setText(getString(R.string.off_day,7));
            initDate();//初始化日期
        }
        tv_typeReason.setText(getResources().getStringArray(selectTypePosition <=1 ? R.array.leave_short_reason : R.array.leave_long_reason)[0]);
        //具体原因的hint替换
        if(getString(R.string.shixi).equals(tv_typeReason.getText().toString())){//实习
            ed_reason.setHint(R.string.reson_detail_tip_2);
        }else {//不是实习
            ed_reason.setHint(R.string.reson_detail_tip);
        }
        initTypeReasonDialog();
    }

    /**
     * 获取课表信息失败
     * @param msg m
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_NETWORK)
    }, thread = EventThread.MAIN_THREAD)
    public void netWorkError(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(LeaveActivity.this, ("".equals(msg)?getString(R.string.net_work_error):msg),Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
        }
    }

    /**
     * 获取课表信息成功
     * @param data d
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_TABLE_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getTableInfo(BaseResponse<SubjectTable> data) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            subjectTableInfo = data.getData();
            //设置开始时间为00时00分00秒
            subjectTableInfo.setStartDate(DensityUtil.setDate(subjectTableInfo.getStartDate(),true));
            //设置结束时间为23时59分59秒
            subjectTableInfo.setEndDate(DensityUtil.setDate(subjectTableInfo.getEndDate(),false));
            //本学期总周数
            subjectTableInfo.setSumWeek((int)Math.ceil((float)(subjectTableInfo.getEndDate().getTime()-subjectTableInfo.getStartDate().getTime())/(7*Constants.oneDay)));
            initSubjectDialog(progressHUD.isShowing());
            progressHUD.dismiss();

        }
    }

    /**
     * 提交请假申请成功
     * @param msg m
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_LEAVE_REQUEST)
    }, thread = EventThread.MAIN_THREAD)
    public void getLeaveRequest(String msg) {
        if(MyActivityManager.getInstance().getCurrentActivity() == this){
            Toast.makeText(this, R.string.submit_success,Toast.LENGTH_SHORT).show();
            progressHUD.dismiss();
            PictureFileUtils.deleteCacheDirFile(this);
            //请假人提交请假申请成功
            SmecRxBus.get().post("addMessage",new MyMessage(msg,SharedPreferencesUtil.getInstance().getUserId(), (short) 1, "", (short)2, msg,new Date()));
            finish();

        }
    }
    /**
     * 课表提示框
     * @param isShow 是否显示加载动画
     */
    private void initSubjectDialog(boolean isShow) {
        if(subjectTableInfo==null){//获取课表信息
            if(isShow)
                progressHUD.show();
            new GetTableInfoPresenter(this).getTableInfo(SharedPreferencesUtil.getInstance().getUserId(),SharedPreferencesUtil.getInstance().getUserType(),SharedPreferencesUtil.getInstance().getFaculty());
            return;
        }
        if(subjectDialog==null) {
            subjectDialog = new Dialog(this);
            subjectTableView = new SubjectTableView(this);
            subjectTableView.setData(subjectTableInfo,(int) Math.ceil((float)(System.currentTimeMillis() - subjectTableInfo.getStartDate().getTime()) / (7 * Constants.oneDay)));
            //设置确定、取消点击事件
            subjectTableView.setMyOnClickListener(new SubjectTableView.MyOnClickListener() {
                @Override
                public void onConfirmClick(HashMap<Integer,String> hashMap) {
                    tv_dayOffCount.setText(getString(R.string.off_suject,hashMap.size()*2));
                    LeaveActivity.this.hashMap=hashMap;
                    if(hashMap.size()>0) {
                        img_subIcon.setVisibility(View.VISIBLE);
                        initSelectedSubjectDialog();
                    }
                    else
                        img_subIcon.setVisibility(View.GONE);
                    subjectDialog.dismiss();
                }

                @Override
                public void onCancelClick() {
                    subjectDialog.dismiss();
                }
            });
            subjectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            subjectDialog.setContentView(subjectTableView);
            subjectDialog.setCancelable(true);
            if(subjectDialog.getWindow()!=null)
            subjectDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if(isShow)
            subjectDialog.show();
    }

    /**
     * 请假原因提示框
     *
     */
    private void initTypeReasonDialog() {
        if(reasonDialog==null) {
            reasonDialog = new Dialog(this);
            reasonListView = new ReasonTypeListLayout(this);
            reasonListView.getTitleText().setText(R.string.type_reason);
            reasonListView.setReasonlist(getResources().getStringArray(selectTypePosition == 1 ? R.array.leave_short_reason : R.array.leave_long_reason));
            reasonListView.setMyClickListener((type, position) -> {
                tv_typeReason.setText(type);
                if(getString(R.string.shixi).equals(type)){//实习
                    ed_reason.setHint(R.string.reson_detail_tip_2);
                }else {//不是实习
                    ed_reason.setHint(R.string.reson_detail_tip);
                }
                reasonDialog.dismiss();
            });
            reasonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            reasonDialog.setContentView(reasonListView);
            reasonDialog.setCancelable(true);
            if(reasonDialog.getWindow()!=null)
                reasonDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * 查看选择的请假的课程
     */
    private void initSelectedSubjectDialog() {
        String [] arr = new String[hashMap.size()];
        //对hashmap排序
        List<Map.Entry<Integer,String>> list=DensityUtil.sort(hashMap);
        int i=0;
        for(Map.Entry<Integer, String> entry : list){
            int week= entry.getKey()/100;//第几周
            int day = entry.getKey()%100/10;//星期几
            int pitch = entry.getKey()%10;//第几节课
            arr[i]=getString(R.string.selected_subject_info,week,day,pitch)+"+"+entry.getValue();
            ++i;
        }
        if(subjectelectDialog==null) {
            subjectelectDialog = new Dialog(this);
            selectedSubjectView = new ReasonTypeListLayout(this);
            selectedSubjectView.getTitleText().setText(R.string.select_subject);
            selectedSubjectView.setSelectSubject(arr);
            selectedSubjectView.setMyOnConfirmClickListener(() -> subjectelectDialog.dismiss());
            subjectelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            subjectelectDialog.setContentView(selectedSubjectView);
            subjectelectDialog.setCancelable(true);
            if(subjectelectDialog.getWindow()!=null)
                subjectelectDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else {
            selectedSubjectView.setSelectSubject(arr);
        }

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if(inputMethodManager!=null&&inputMethodManager.isActive()&&this.getCurrentFocus()!=null){
                inputMethodManager.hideSoftInputFromWindow(LeaveActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            ed_reason.setCursorVisible(false);
//            isVisible = false;
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectImages.clear();
                    // 图片选择结果回调
                    selectImages.addAll(PictureSelector.obtainMultipleResult(data));
                    // 例如 LocalMedia 里面返回三种 path
                    // 1.media.getPath(); 为原图 path
                    // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
                    // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    // 更新图片数量
                    tv_imgCount.setText(getString(R.string.img_count, selectImages.size()));
                    // 更新显示图片
                    leaveImgAdapter.setImages(selectImages);
                    leaveImgAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        isVisible = ed_reason.isFocused();
        ed_reason.setCursorVisible(ed_reason.isFocused());
    }


    @Override
    public LeaveRequestPresenter getPersenter() {
        return new LeaveRequestPresenter(this);
    }
//    int count=0;
//    private void buttonBeyondKeyboardLayout(final View root, final View button) {
//        // 监听根布局的视图变化
//        root.getViewTreeObserver().addOnGlobalLayoutListener(
//                () -> {
//                    Log.d("mydemo","entry"+isScroll);
//                    if (isScroll) {
//                        if (ed_reason.isCursorVisible() && isVisible) {
//                            sc_scroll.scrollTo(0, (int) button.getY());
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    sc_scroll.scrollTo(0, (int) lg_reason.getY());
//                                    Log.d("mydemo","scroll_delay");
//                                }
//                            }, Constants.scrollDelay);
//
//                        } else {
//                            // 键盘隐藏
//                            root.scrollTo(0, 0);
//                            if (!isVisible && ++count > 1) {
//                                isVisible = true;
//                            }
//                        }
//                    }
//                });
//    }


}
