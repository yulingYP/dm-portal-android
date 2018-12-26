package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.definesys.dmportal.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomTitleIndicator extends LinearLayout {

    @BindView(R.id.title1_view_ti)
    TextView title1;
    @BindView(R.id.title2_view_ti)
    TextView title2;
    @BindView(R.id.layout_view_ti)
    ConstraintLayout layout;
    @BindView(R.id.indicator_view_ti)
    View indicator;

    private OnTitleClickListener onTitleClickListener;
    // TODO:
    final float scale = this.getContext().getResources ().getDisplayMetrics ().density;
    final float maxMargin = 80*scale+0.5f;

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem;

    public CustomTitleIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_textview_indicator, this);
        ButterKnife.bind(this);

        this.title1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTitleClickListener!=null){
                    onTitleClickListener.onClick(0);
                }
            }
        });
        this.title2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onTitleClickListener!=null){
                    onTitleClickListener.onClick(1);
                }
            }
        });
    }

    public void setTitle1Text(String title1) {
        this.title1.setText(title1);
    }

    public void setTitle1TextSize(float size) {
        this.title1.setTextSize(size);
    }

    public void setTitle1TextColor(int color) {
        this.title1.setTextColor(color);
    }

    public void setTitle2Text(String title2) {
        this.title2.setText(title2);
    }

    public void setTitle2TextSize(float size) {
        this.title2.setTextSize(size);
    }

    public void setTitle2TextColor(int color) {
        this.title2.setTextColor(color);
    }

    public void setIndicatorFeatures(int position, float offset) {
        LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
        //从0--》1   文本内容1变大，0变小，右边距变大，1到center
        // offset  0->1

        if (position == 0) {
            if(offset>0.5) {
                setTitle1TextColor(getResources().getColor(R.color.alert_grey_small));
                setTitle2TextColor(getResources().getColor(R.color.color_white));
            }else {
                setTitle1TextColor(getResources().getColor(R.color.color_white));
                setTitle2TextColor(getResources().getColor(R.color.alert_grey_small));
            }
            setTitle1TextSize(20 - 6 * offset);
            setTitle2TextSize(14 + 6 * offset);
            layoutParams.rightMargin = (int) (maxMargin * offset);
//            layoutParams.rightMargin = (int) (100*offset);

        }
        //从1--》0   文本内容0变大，1变小，右边距变小，0到center
        // offset  1->0
        else {
            if(offset>0.5) {
                setTitle1TextColor(getResources().getColor(R.color.color_white));
                setTitle2TextColor(getResources().getColor(R.color.alert_grey_small));
            }else {
                setTitle1TextColor(getResources().getColor(R.color.alert_grey_small));
                setTitle2TextColor(getResources().getColor(R.color.color_white));
            }
            setTitle1TextSize(14 + 6 * (1-offset));
            setTitle2TextSize(20 - 6 * (1-offset));
            layoutParams.rightMargin = (int)(maxMargin *(1-offset));
        }
        layout.setLayoutParams(layoutParams);
    }

    public void setFocus(int position) {
        LayoutParams layoutParams = (LayoutParams) layout.getLayoutParams();
        if (position == 0) {
            setTitle1TextSize(20);
            setTitle1TextColor(getResources().getColor(R.color.color_white));
            setTitle2TextSize(14);
            setTitle2TextColor(getResources().getColor(R.color.alert_grey_small));
            layoutParams.rightMargin = 0;
            layout.setLayoutParams(layoutParams);
        } else {
            setTitle1TextSize(14);
            setTitle1TextColor(getResources().getColor(R.color.alert_grey_small));
            setTitle2TextSize(20);
            setTitle2TextColor(getResources().getColor(R.color.color_white));
            layoutParams.rightMargin = (int) maxMargin;
            layout.setLayoutParams(layoutParams);
        }
    }

    public void setOnTitleClickListener(OnTitleClickListener onTitleClickListener) {
        this.onTitleClickListener = onTitleClickListener;
    }

    public interface OnTitleClickListener{
        public void onClick(int position);
    }

}
