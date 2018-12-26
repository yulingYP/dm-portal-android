package com.definesys.dmportal.appstore.utils;

import android.content.Context;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 羽翎 on 2018/8/31.
 */

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp==dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 交换两个元素的位置的方法
     * @param strSort    需要交换元素的数组
     * @param i    索引i
     * @param j 索引j
     */
    private static void swap(String[] strSort, int i, int j) {
        String t = strSort[i];
        strSort[i] = strSort[j];
        strSort[j] = t;
    }
    /***
     * 把中文替换为指定字符<br>
     * 注意:一次只匹配一个中文字符
     * @param source
     * @param replacement
     * @return
     */
    public static String replaceChinese(String source, String replacement){

        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat=pat.matcher(source);
        String repickStr = mat.replaceAll(replacement);
        StringBuilder sb = new StringBuilder(repickStr);
        sb.replace(10, 11, "");
        return sb.toString();
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param
     * @return
     */
    public static float px2sp( Context context,float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return  (pxValue / scale + 0.5f);
    }
}
