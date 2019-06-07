package com.example.dmportal.appstore.customViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dmportal.R;
import com.example.dmportal.appstore.LeaveActivity;
import com.example.dmportal.appstore.bean.LeaveInfo;
import com.example.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2019/1/10.
 */

public class SubmitLeaveInfoView extends LinearLayout {

    @BindView(R.id.cancel_text)
    TextView tv_cancel;
    @BindView(R.id.confirm_text)
    TextView tv_confirm;
    @BindView(R.id.name_text)
    TextView tv_name;
    @BindView(R.id.type_text)
    TextView tv_type;
    @BindView(R.id.title_text)
    TextView tv_title;
    @BindView(R.id.start_time_text)
    TextView tv_startTime;
    @BindView(R.id.end_time_text)
    TextView tv_endTime;
    @BindView(R.id.sum_time_text)
    TextView tv_sumTime;
    @BindView(R.id.selected_subject_text)
    TextView tv_selectedSubject;
    @BindView(R.id.content_text)
    TextView tv_content;
    @BindView(R.id.submit_tip)
    TextView tv_tip;
    @BindView(R.id.img_layout)
    RelativeLayout lg_img;
    @BindView(R.id.img_1)
    ImageView img_1;
    @BindView(R.id.img_2)
    ImageView img_2;
    @BindView(R.id.img_3)
    ImageView img_3;
//    @BindView(R.id.layout_scroll)
//    ScrollView lg_scroll;
    private Context mContext;
    private OnClickListener onClickConfirmListener;
    public SubmitLeaveInfoView(Context context) {
        super(context);
        initView(context);
    }

    public SubmitLeaveInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SubmitLeaveInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context mContext) {
        this.mContext = mContext;
        LayoutInflater.from(mContext).inflate(R.layout.view_submit_leave_confirm,this);
        ButterKnife.bind(this);
        RxView.clicks(tv_confirm)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obg->{
                    if(onClickConfirmListener!=null)
                        onClickConfirmListener.onClickConfirm();
                });
        RxView.clicks(tv_cancel)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(obg->{
                    if(onClickConfirmListener!=null)
                        onClickConfirmListener.onCancelClick();
                });
    }

    /**
     * 设置数据
     * @param submitLeaveInfo 各种信息
     * @param selectImages s
     *
     */
    public void setDate(LeaveInfo submitLeaveInfo, List<LocalMedia> selectImages){
        tv_tip.setText(mContext.getString(R.string.confirm_info_tip,Constants.leaveInterval));
        tv_name.setText(mContext.getString(R.string.name_tip,submitLeaveInfo.getName()));
        tv_type.setText(mContext.getString(R.string.type_tip,submitLeaveInfo.getLeaveType()));
        tv_title.setText(mContext.getString(R.string.leave_title,submitLeaveInfo.getLeaveTitle()));
        tv_sumTime.setText(mContext.getString(R.string.sum_time_tip,submitLeaveInfo.getSubTime()));
        tv_content.setText(mContext.getString(R.string.content_tip,submitLeaveInfo.getLeaveReason()));
        tv_startTime.setText(mContext.getString(R.string.start_time_tip,submitLeaveInfo.getStartTime()));
        tv_endTime.setText(mContext.getString(R.string.end_time_tip,submitLeaveInfo.getEndTime()));
        if(!"".equals(submitLeaveInfo.getSelectedSubject())){
            tv_selectedSubject.setText(Html.fromHtml(mContext.getString(R.string.selected_subject_tip,submitLeaveInfo.getSelectedSubject())));
        }else {
            tv_selectedSubject.setVisibility(GONE);
        }
        if(selectImages==null||selectImages.size()==0)
            lg_img.setVisibility(GONE);
        else {
            lg_img.setVisibility(VISIBLE);
            String path;
            for(int i = 0 ;i<selectImages.size();i++){
                path = selectImages.get(i).getCompressPath()==null?selectImages.get(i).getPath():selectImages.get(i).getCompressPath();
                if(i==0) {
                  initImg(img_1,path,0,selectImages);
                }
                else if(i==1) {
                    initImg(img_2,path,1,selectImages);
                }
                else {
                    initImg(img_3,path,2,selectImages);
                }
            }
        }
//        //滑动页面到最低端
//        tv_confirm.post(() -> lg_scroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    /**
     * 设置图片及点击事件
     * @param img imgaeView
     * @param path 图片路径
     * @param position 点击位置
     * @param selectImages 图片数组
     */
    private void initImg(ImageView img, String path, int position, List<LocalMedia> selectImages) {
        Glide.with(mContext).load(path).into(img);
        RxView.clicks(img)
                .subscribe(obj->
                        PictureSelector.create((LeaveActivity)mContext).
                                externalPicturePreview(position, selectImages)
                );
    }

    public void setOnClickListener(OnClickListener onClickConfirmListener) {
        this.onClickConfirmListener = onClickConfirmListener;
    }

    public interface OnClickListener{
        void onClickConfirm();
        void onCancelClick();
    }
}
