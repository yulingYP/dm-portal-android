package com.example.dmportal.appstore.customViews;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by 羽翎 on 2018/9/19.
 */

public class FixedSpeedScroller extends Scroller {
    private int mDuration = 1500;
    private boolean isAuto = false;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        int temp;
        if(isAuto)
            temp=mDuration;
        else
            temp = duration;
        super.startScroll(startX, startY, dx, dy,temp);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int time) {
        mDuration = time;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public int getmDuration() {
        return mDuration;
    }
}