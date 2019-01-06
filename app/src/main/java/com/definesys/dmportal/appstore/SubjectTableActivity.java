package com.definesys.dmportal.appstore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.definesys.base.BaseActivity;
import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.CursorArg;
import com.definesys.dmportal.appstore.bean.SubjectTable;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

@Route(path = ARouterConstants.SubjectTableActivity)
public class SubjectTableActivity extends BaseActivity {
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

    private List<TextView> textViewList;
    private SubjectTable subjectTable;//课表信息
    private int currentShowWeek=1;//当前显示的周数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_table);
        ButterKnife.bind(this);
        initView();
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
                        if(subjectTable==null)
                            return;
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
                        if(subjectTable==null)
                            return;
                        if(currentShowWeek<subjectTable.getSumWeek()) {
                            ++currentShowWeek;
                            initTable();
                        }
                        else
                            Toast.makeText(SubjectTableActivity.this, R.string.already_last_week,Toast.LENGTH_SHORT).show();
                    }
                });
        tv_hello.setText(getString(R.string.table_hello_tip,"马一平","151110414"));
        textViewList = new ArrayList<>();
        getTextView(lg_first);
        getTextView(lg_second);
        getTextView(lg_third);
        getTextView(lg_fourth);
        getTextView(lg_fifth);
        getTextView(lg_sixth);
//        for(int i = 0 ; i<textViewList.size();i++){
//            textViewList.get(i).setText(""+i);
//        }
        httpPost();//网络请求
    }

    private void httpPost() {
        Map map = new HashMap();
        map.put("stuId",151110401);
        map.put("facultyId","111");
        ViseHttp.POST(HttpConst.getTable)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<SubjectTable>>() {
                    @Override
                    public void onSuccess(BaseResponse<SubjectTable> data) {
                        subjectTable = data.getData();
                        if(System.currentTimeMillis()<=subjectTable.getEndDate().getTime())
                            currentShowWeek = (int)Math.ceil((System.currentTimeMillis()-subjectTable.getStartDate().getTime())/(7*Constants.oneDay));
                        //本学期总周数
                        subjectTable.setSumWeek((int)Math.ceil((subjectTable.getEndDate().getTime()-subjectTable.getStartDate().getTime())/(7*Constants.oneDay)));
                        initTable();
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {

                    }
                });
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
                if(currentShowWeek>=list.get(i).getStartWeek()&&currentShowWeek<=list.get(i).getEndWeek()){//当前周有课
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
                                    textViewList.get(6-j+(7-k)*7).setText(list.get(i).getCursorName()+"\n"+(list.get(i).getClassroom()==null?"":list.get(i).getClassroom()));
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 获取每个布局中显示课程的textView
     * @param linearLayout
     */
    private void getTextView(LinearLayout linearLayout){
        for(int i=1;i<linearLayout.getChildCount();i++){
            textViewList.add((TextView) linearLayout.getChildAt(i));
        }
    }

    /**
     * 课表置空
     */
    private void clearTable(){
        for(int i = 0 ; i<textViewList.size();i++){
            textViewList.get(i).setText("");
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
