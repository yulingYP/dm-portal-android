package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by 羽翎 on 2018/9/19.
 */

public class IFrameLayout extends FrameLayout {
    ViewPager viewPager;
    public IFrameLayout(@NonNull Context context) {
        super(context);
    }

    public IFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return viewPager.onTouchEvent(event);
    }


    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }


}
