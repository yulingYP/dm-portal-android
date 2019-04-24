package com.definesys.dmportal.appstore.utils;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 *
 * Created by 羽翎 on 2019/4/17.
 */
@SuppressLint("StaticFieldLeak")
public class AnimUtils {
    private int mHeight;//伸展高度

    private View hideView,down;//需要展开隐藏的布局，开关控件

    private static AnimUtils instance;

    /**
     * 构造器(可根据自己需要修改传参)
     * @param hideView 需要隐藏或显示的布局view
     * @param down 按钮开关的view
     * @param height 布局展开的高度(根据实际需要传)
     */
    public static AnimUtils setInstance(View hideView,View down,int height){
        if(instance==null) {
            synchronized (AnimUtils.class) {
                if(instance==null)
                    instance = new AnimUtils(hideView,down,height);
            }
        }else {
            instance.setDown(down);
            instance.setHideView(hideView);
            instance.setmHeight(height);
        }
        return instance;
//        return new AnimUtils(hideView,down,height);
    }

    private AnimUtils(View hideView,View down,int height){
        this.hideView = hideView;
        this.down = down;
        mHeight=height;
    }


    /**
     * 布局伸展缩放开关
     * @param isVisible 是否初始化时设置为VISIBLE
     */
    public void toggle(boolean isVisible){
        rotationView();
        if (View.VISIBLE == hideView.getVisibility()) {
            closeAnimate(hideView);//布局隐藏
        } else {
            openAnim(hideView,isVisible);//布局铺开
        }
    }

    /**
     * 文本动画开关
     */
    public void textToggle(boolean isSingle){
        TextView textView = (TextView)hideView;
        final ValueAnimator[] animator = new ValueAnimator[1];
        if(!isSingle){//收缩
            down.setRotation(0);
            int origHeight = hideView.getHeight();
            animator[0] = createDropAnimator(hideView, origHeight, mHeight);
            animator[0].addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    textView.setSingleLine();
                }
            });
            animator[0].start();
        }else {//伸展
            down.setRotation(180);
            textView.setSingleLine(false);
            textView.post(()->{
                if(textView.getLayout()!=null) {
                    //单行高度----》多行高度
                    animator[0] = createDropAnimator(hideView, mHeight, mHeight + (textView.getLayout().getLineCount() - 1) * textView.getLineHeight());
                    animator[0].start();
                }
            });
        }
    }

    /**
     * 开关旋转
     */
    private void rotationView() {
        if (View.VISIBLE == hideView.getVisibility()) {
            down.setRotation(0);
        } else {
            down.setRotation(180);
        }
    }

    private void openAnim(View v,boolean isVisible) {
        v.setVisibility(View.VISIBLE);
        if(!isVisible){
            v.measure(0,0);
            mHeight = v.getMeasuredHeight();
        }
        ValueAnimator animator = createDropAnimator(v, 0, mHeight);
        animator.start();
    }

    private void closeAnimate(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(Constants.loadAnim);//动画时长
        animator.addUpdateListener(arg0 -> {
            int value = (int) arg0.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
            layoutParams.height = value;
            v.setLayoutParams(layoutParams);
        });
        return animator;
    }

    private void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    private void setHideView(View hideView) {
        this.hideView = hideView;
    }

    private void setDown(View down) {
        this.down = down;
    }
}

