package com.definesys.dmportal.main.util;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

public class HddLayoutHeight {

    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                if (mainInvisibleHeight > 50) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    if ( srollHeight <= 40 ) {

                    } else {
                        main.scrollTo(0, srollHeight + 20);
                    }

                } else {
                    main.scrollTo(0, 0);
                }
            }
        });
    }

}
