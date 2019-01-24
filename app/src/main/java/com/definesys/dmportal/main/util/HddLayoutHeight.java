package com.definesys.dmportal.main.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import com.definesys.dmportal.MainApplication;

public class HddLayoutHeight {

    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                float minDp = MainApplication.scale * 20 + 0.5f;

                main.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;

                if(mainInvisibleHeight > MainApplication.scale*60+0.5f){
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    if ( srollHeight <= minDp ) {

                    } else {
                        main.scrollTo(0, (int) (srollHeight +  minDp));
                    }

                }else {
                    main.scrollTo(0, 0);
                }

            }
        });
    }

}
