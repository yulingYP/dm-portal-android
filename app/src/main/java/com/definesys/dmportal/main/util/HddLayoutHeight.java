package com.definesys.dmportal.main.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public class HddLayoutHeight {

    public void addLayoutListener(Context context,final View main, final View scroll) {
        final float scale = context.getResources().getDisplayMetrics().density;
        main.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            float minDp = scale  * 20 + 0.5f;

            main.getWindowVisibleDisplayFrame(rect);
            int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;

            if(mainInvisibleHeight > scale *60+0.5f){
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

        });
    }

}
