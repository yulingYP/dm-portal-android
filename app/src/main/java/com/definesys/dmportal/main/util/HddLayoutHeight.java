package com.definesys.dmportal.main.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.MainActivity;

public class HddLayoutHeight {

    public void addLayoutListener(Context context,final View main, final View scroll,float mindp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        final boolean[] scrollable = {true};
        main.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            float minDp = scale  * mindp + 0.5f;//最小滑动范围
            //软件盘高度
            main.getWindowVisibleDisplayFrame(rect);
            // 当前窗口的高度 - 可见区域的底部 = 不可见窗体的大小[即软件盘的高度]
            int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;

            if(mainInvisibleHeight > scale * 60 + 0.5f){//件盘的高度超过60px
                int[] location = new int[2];
                scroll.getLocationInWindow(location);//一个控件在其父布局上的坐标位置x，y
                //滑动高度 当前位置y+控件高度-可见范围的底部坐标
                int srollHeight = location[1] + scroll.getHeight() - rect.bottom;
                if ( srollHeight > minDp && scrollable[0]) {//滑动高度>最小滑动范围
                    main.scrollTo(0, (int) (srollHeight +  minDp));
                    scrollable[0] = false;
                }
            }else {
                main.scrollTo(0, 0);
                scrollable[0] = true;
            }
        });
    }

    public void addLayoutListener(Context context, final View main, final View scroll, LinearLayout linearLayout, EditText editText, int bottomMargin) {
        final float scale = context.getResources().getDisplayMetrics().density;
        final boolean[] scrollable = {true};
        main.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect rect = new Rect();
            float minDp = scale  * 1 + 0.5f;//最小滑动范围
            //软件盘高度
            main.getWindowVisibleDisplayFrame(rect);
            // 当前窗口的高度 - 可见区域的底部 = 不可见窗体的大小[即软件盘的高度]
            int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;

            if(mainInvisibleHeight > scale * 60 + 0.5f){//件盘的高度超过60px
                int[] location = new int[2];
                scroll.getLocationInWindow(location);//一个控件在其父布局上的坐标位置x，y
                //滑动高度 当前位置y+控件高度-可见范围的底部坐标
                int srollHeight = location[1] + scroll.getHeight() - rect.bottom;
                if ( srollHeight > minDp && scrollable[0]) {//滑动高度>最小滑动范围
                    main.scrollTo(0, (int) (srollHeight +  minDp));
                    scrollable[0] = false;
                }
            }else {
                main.scrollTo(0, 0);
                scrollable[0] = true;
            }
        });
    }
}
