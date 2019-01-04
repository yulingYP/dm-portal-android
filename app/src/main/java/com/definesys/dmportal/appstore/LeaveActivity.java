package com.definesys.dmportal.appstore;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.ReasonImageAdapter;
import com.definesys.dmportal.appstore.customViews.MyDatePick;
import com.definesys.dmportal.appstore.customViews.MyDatePicker;
import com.definesys.dmportal.appstore.customViews.ReasonTypeListLayout;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

@Route(path = ARouterConstants.LeaveActivity)
public class LeaveActivity extends BaseActivity {
    @BindView(R.id.title_bar)
    CustomTitleBar titleBar;

    @BindView(R.id.type_text)
    TextView tv_type;

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

    @BindView(R.id.time_table_layout)
    LinearLayout lg_table;
   @BindView(R.id.scoll_view)
    ScrollView sc_scoll;

    @BindView(R.id.ed_reason)
    EditText ed_reason;

    @BindView(R.id.end_time_text)
    TextView tv_timeEnd;

    @BindView(R.id.start_time_text)
    TextView tv_timeStart;

    @BindView(R.id.leave_day_count_text)
    TextView tv_dayOffCount;

    @BindView(R.id.count_word_text)
    TextView tv_count;

    private ReasonImageAdapter fedbkImgAdapter;
    private List<LocalMedia> selectImages;
    private List<LocalMedia> ViewImages;

    private Date startDate;
    private Date endDate;
    private int tempDay = 1000*60*60*24;//天
    private boolean isVisible =false;//光标是否可见
    private boolean isStart;//用户点击的是开始日期还是结束日期
    private Dialog dateDialog;//日期选择提示框
    private MyDatePicker datePick;//日期选择Picker
    private Dialog reasonDialog;//请假类型提示框
    private SimpleDateFormat df;
    //班长、班主任、导员、导师、教务处、任课老师
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_off);
        ButterKnife.bind(this);
        buttonBeyondKeyboardLayout(lg_reason,ed_reason);
        df = new SimpleDateFormat(getString(R.string.date_type));

        initView();
        initEdit();//编辑框
        initList();//添加图片的列表
        initDate();//日期dialog
        initReasonDialog();//初始化请假类型提示框
        initDialog(false);//初始化日期选择提示框
    }

    private void initView() {
        titleBar.setTitle(getString(R.string.leave_off));
        titleBar.setBackgroundDividerEnabled(false);
        //titleBar.setBackground(null);
        RxView.clicks(titleBar.addLeftBackImageButton())
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        Intent intent = new Intent();
                        setResult(RESULT_CANCELED,intent);
                        finish();
                    }
                });
        Button button = titleBar.addRightTextButton(getString(R.string.submit),R.layout.activity_leave_off);
        button.setTextSize(14);
        RxView.clicks(button)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        checkSelect();
                    }
                });
        RxView.clicks(lg_type)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        initReasonDialog();
                    }
                });
        RxView.clicks(lg_table)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ARouter.getInstance()
                                .build(ARouterConstants.SubjectTableActivity)
                                .navigation(LeaveActivity.this);

                    }
                });
    }
    private void initEdit() {
        tv_count.setText(getString(R.string.word_count, 0));
        RxView.clicks(ed_reason)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        isVisible = true;
                        ed_reason.setCursorVisible(true);
                        // sc_scoll.scrollTo(0,ed_reason.getBottom()+100);
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
    }
    private void initList() {
        selectImages = new ArrayList<>();
        ViewImages = new ArrayList<>();
        ViewImages.add(0,new LocalMedia(" ",100,2,""));
        tv_imgCount.setText(getString(R.string.img_count, 0));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));

        fedbkImgAdapter = new ReasonImageAdapter(this, ViewImages);
        recyclerView.setAdapter(fedbkImgAdapter);

        // 自定义图片控件的点击事件
        fedbkImgAdapter.setOnClickListener(new ReasonImageAdapter.OnClickListener() {
            @Override
            public void onBackgroundClick(int position) {
                if(position == 0){
                    //打开相册·拍摄照片
                    PictureSelector.create(LeaveActivity.this)
                            .openGallery(PictureMimeType.ofImage()).maxSelectNum(3).compress(true)
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
                ViewImages.remove(position);
                fedbkImgAdapter.notifyDataSetChanged();
                tv_imgCount.setText(getString(R.string.img_count, fedbkImgAdapter.getItemCount()-1));
            }
        });
    }
    private void initDate() {
        startDate = new Date(System.currentTimeMillis());
        endDate = new Date(System.currentTimeMillis());
        tv_timeEnd.setText(df.format(endDate));
        tv_timeStart.setText(df.format(startDate));
        tv_dayOffCount.setText(getString(R.string.off_day,0)+getString(R.string.off_hour,0));
        RxView.clicks(lg_leaveEnd)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        initDialog(false);
                    }
                });
        RxView.clicks(lg_leaveStart)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        initDialog(true);
                    }
                });
    }
    //请假类型选择
    private void initReasonDialog() {
        if(reasonDialog==null) {
            reasonDialog = new Dialog(this);
            ReasonTypeListLayout reasonTypeListLayout = new ReasonTypeListLayout(this);
            reasonTypeListLayout.setReasonlist(getData());
            reasonTypeListLayout.setMyClickListener(new ReasonTypeListLayout.MyClickListener() {
                @Override
                public void onClick(String type) {
                    tv_type.setText(type);
                    reasonDialog.dismiss();
                }
            });
            reasonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            reasonDialog.setContentView(reasonTypeListLayout);
            reasonDialog.setCancelable(true);
            reasonDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else
            reasonDialog.show();
    }
    private List<String> getData() {
        // 数据源
        List<String> dataList = new ArrayList<String>();
        dataList.add("实习");
        dataList.add("出差");
        dataList.add("住院");
        dataList.add("身体欠佳");
        dataList.add("出国交流");
        dataList.add("家庭原因");
        dataList.add("其他");
        return dataList;
    }
    //日期选择提示框
    private void initDialog(boolean flag) {
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
                    boolean isBefore = checkDate(isStart, date);
                    if (isStart && isBefore)
                        tv_timeStart.setText(date);
                    else if (!isStart && isBefore)
                        tv_timeEnd.setText(date);
                    else
                        Toast.makeText(LeaveActivity.this, R.string.time_fail_tip, Toast.LENGTH_SHORT).show();
                    if ((isStart && isBefore) || (!isStart && isBefore)) {
                        initTime();
                    }
                }
            });
            dateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dateDialog.getWindow();
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
        int day = (int)(time/tempDay);
        int hour = (int)(time/(tempDay/24))-day*24;
        tv_dayOffCount.setText((day>0?getString(R.string.off_day,day):"")+(day>0&&hour==0?"":getString(R.string.off_hour,hour)));
        System.gc();
    }

    /**
     * 检测选择的时间是否比另一个时间小
     * @param isStart 选择的是否是开始时间
     * @param date 选择的日期
     * @return isBefore
     */
    private boolean checkDate(boolean isStart, String date) {
        startDate = null;
        endDate = null;
        try {
            startDate = isStart?df.parse(date):df.parse(tv_timeStart.getText().toString());
            endDate = isStart?df.parse(tv_timeEnd.getText().toString()):df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate.before(endDate);
    }

    private void checkSelect() {
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            if(inputMethodManager.isActive()){
                inputMethodManager.hideSoftInputFromWindow(LeaveActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
            ed_reason.setCursorVisible(false);
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
                    ViewImages.clear();
                    ViewImages.add(0,new LocalMedia(" ",100,2,""));
                    // 图片选择结果回调
                    selectImages.addAll(PictureSelector.obtainMultipleResult(data));
                    ViewImages.addAll(PictureSelector.obtainMultipleResult(data));
                    // 例如 LocalMedia 里面返回三种 path
                    // 1.media.getPath(); 为原图 path
                    // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
                    // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    // 更新图片数量
                    tv_imgCount.setText(getString(R.string.img_count, selectImages.size()));
                    // 更新显示图片
                    fedbkImgAdapter.setImages(ViewImages);
                    fedbkImgAdapter.notifyDataSetChanged();
                    break;
            }
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
    int count=0;
    private void buttonBeyondKeyboardLayout(final View root, final View button) {
        // 监听根布局的视图变化
        root.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (ed_reason.isCursorVisible()&&isVisible) {
                            sc_scoll.scrollTo(0,(int)lg_reason.getY());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sc_scoll.scrollTo(0,(int)lg_reason.getY());
                                }
                            }, 300);

                        } else {
                            // 键盘隐藏
                            root.scrollTo(0, 0);
                            if(!isVisible&&++count>1) {
                                isVisible = true;
                            }
                        }
                    }
                });
    }

}
