package com.example.dmportal.appstore.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.dmportal.R;

import static com.luck.picture.lib.tools.ScreenUtils.getStatusBarHeight;

/**
 *
 * Created by 羽翎 on 2019/4/11.
 */

public class StatusUtil {
    //隐藏状态栏
    public static void setStatusBarFullTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21表示5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //设置状态栏字体颜色
    public static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            if (dark) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarBackGround(activity);
//            setStatusBarColor(activity,Color.BLACK);
        }
    }
    //设置状态栏的背景色
    private static void setStatusBarBackGround(Activity activity){
        ViewGroup parent=(ViewGroup)activity.getWindow().getDecorView();
        View view = new View(activity);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(parent.getContext().getResources().getColor(R.color.transparent_black_5));
        parent.addView(view);
    }
    private static void setStatusBarColor(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//21表示5.0
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(statusColor);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //让view不根据系统窗口来调整自己的布局
            ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            ViewCompat.requestApplyInsets(mChildView);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//19表示4.4
            Window window = activity.getWindow();
            //设置Window为全透明
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView =  window.findViewById(Window.ID_ANDROID_CONTENT);
            //获取父布局
            View mContentChild = mContentView.getChildAt(0);
            //获取状态栏高度
            int statusBarHeight = getStatusBarHeight(activity);
            //如果已经存在假状态栏则移除，防止重复添加
            removeFakeStatusBarViewIfExist(activity);

            //设置子控件到状态栏的间距
            addMarginTopToContentChild(mContentChild, statusBarHeight);
            //不预留系统栏位置
            if (mContentChild != null) {
                ViewCompat.setFitsSystemWindows(mContentChild, false);
            }
            //如果在Activity中使用了ActionBar则需要再将布局与状态栏的高度跳高一个ActionBar的高度，否则内容会被ActionBar遮挡
            int action_bar_id = activity.getResources().getIdentifier("action_bar", "id", activity.getPackageName());
            View view = activity.findViewById(action_bar_id);
            if (view != null) {
                TypedValue typedValue = new TypedValue();
                if (activity.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
                    int actionBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, activity.getResources().getDisplayMetrics());
                    setContentTopPadding(activity, actionBarHeight);
                }
            }
        }
    }
    private static void removeFakeStatusBarViewIfExist(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup mDecorView = (ViewGroup) window.getDecorView();

        View fakeView = mDecorView.findViewWithTag("TAG_FAKE_STATUS_BAR_VIEW");
        if (fakeView != null) {
            mDecorView.removeView(fakeView);
        }
    }


    private static void addMarginTopToContentChild(View mContentChild, int statusBarHeight) {
        if (mContentChild == null) {
            return;
        }
        if (!"TAG_MARGIN_ADDED".equals(mContentChild.getTag())) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
            lp.topMargin += statusBarHeight;
            mContentChild.setLayoutParams(lp);
            mContentChild.setTag("TAG_MARGIN_ADDED");
        }
    }
    private static void setContentTopPadding(Activity activity, int padding) {
        ViewGroup mContentView = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        mContentView.setPadding(0, padding, 0, 0);
    }

}
