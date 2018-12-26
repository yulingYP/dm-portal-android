package com.definesys.dmportal.appstore.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.definesys.dmportal.R;


/**
 * Created by 羽翎 on 2018/10/12.
 */

public class StrokeTextView extends android.support.v7.widget.AppCompatTextView {
    private boolean m_bDrawSideLine = true; // 默认采用描边
    private int strokeColor;//描边颜色
    private int textColor;//文本颜色
    private float stroke;// 描边宽度
    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public StrokeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }
    private void init(AttributeSet attrs) {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.Stroke_TextView);
        strokeColor = array.getColor(R.styleable.Stroke_TextView_text_stroke_color, Color.GREEN);
        m_bDrawSideLine = array.getBoolean(R.styleable.Stroke_TextView_isDraw_stroke,true);
        stroke=array.getDimension(R.styleable.Stroke_TextView_text_stroke_width,0);
        textColor = array.getColor(R.styleable.Stroke_TextView_text_solid_color, Color.WHITE);
        array.recycle();


    }
    @Override
    protected void onDraw(Canvas canvas) {
        if (m_bDrawSideLine) {
        this.setTextColor(strokeColor);
        TextPaint tp = this.getPaint();
        tp.setStrokeWidth(stroke);
        tp.setStyle(Paint.Style.FILL_AND_STROKE );
        tp.setColor(strokeColor);
        tp.setFakeBoldText(true); // 外层text采用粗体
        tp.setShadowLayer(1, 0, 0, 0); //字体的阴影效果，可以忽略
        super.onDraw(canvas);

        tp.setColor(textColor);
        tp.setStyle(Paint.Style.FILL);
        this.setTextColor(textColor);
        tp.setFakeBoldText(false); // 外层text采用粗体
        tp.setShadowLayer(0, 0, 0, 0); //字体的阴影效果，可以忽略
        }
        super.onDraw(canvas);

    }


}
