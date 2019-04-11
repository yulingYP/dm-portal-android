package com.definesys.dmportal.main.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public class HddLayoutHeight {

    public void addLayoutListener(Context context,final View main, final View scroll,float mindp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        main.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            float minDp = scale  * mindp + 0.5f;//最小滑动范围
            //可见范围
            main.getWindowVisibleDisplayFrame(rect);
            //不可见高度
            int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;

            if(mainInvisibleHeight > scale *60+0.5f){//不可见范围超过60px
                int[] location = new int[2];
                scroll.getLocationInWindow(location);//位置左上角的x，y
                //滑动高度 当前位置y+控件高度-可见范围的底部坐标
                int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                if ( srollHeight >minDp ) {//滑动高度>最小滑动范围
                    main.scrollTo(0, (int) (srollHeight +  minDp));
                }

            }else {
                main.scrollTo(0, 0);
            }

        });
    }

}
