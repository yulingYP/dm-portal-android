package com.definesys.dmportal.appstore.utils;

import android.content.Context;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /**
     * 获取当前数的2进制形式
     * @param item 16进制
     * @return
     */
    public static String getPitchString(char item){
        String [] allresults = {"0000","0001","0010","0011","0100","0101",
                "0110","0111","1000","1001","1010","1011","1100","1101","1110","1111"};
        return allresults [Integer.parseInt(String.valueOf(item),16)].trim();
    }

    /**
     * 设置某天的开始或结束时间
     * @param date
     * @param isStart 是不是设置开始时间
     * @return
     */
    public static Date setDate(Date date,boolean isStart){
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
        calendar.setTime(date);
        if(isStart) {
            //一天的开始时间 yyyy:MM:dd 00:00:00
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime();
        }

        //一天的结束时间 yyyy:MM:dd 23:59:59
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar.getTime();
    }

    /**
     * 检查要显示的教室格式
     * @param weekDay 星期几
     * @param classroom 教室 可能为null，MMM或（xMMM,xMMM）
     * @return
     */
    public static String checkClassRoom(int weekDay, String classroom) {
        if(classroom==null)
            return "";
        if(!classroom.contains(","))
            return classroom;
        String[] classRooms = classroom.split(",");
        int i;
        for(i =0;i<classRooms.length;i++){
            if(classRooms[i].charAt(0)-48==weekDay){
                break;
            }
        }
        if(i>=classRooms.length)
            --i;
        return classRooms[i].substring(1);
    }

    /**
     * 返回指定格式的日期
     * @param type 格式
     * @param date 日期
     * @return
     */
    public static String dateTypeToString(String type,Date date){
        if(date==null)
            return "";
        SimpleDateFormat sDateFormat=new SimpleDateFormat(type);
        return sDateFormat.format(date);
    }

    /**
     * 设置请假类型
     * @param type
     */
    public static String setTypeText(String type) {
        int hasPostion = type.indexOf("(");//是否包含括号
        return hasPostion>=0?type.substring(0,hasPostion):type;
    }

    /**
     * 返回排序后的list
     * @param hashMap 待排序map
     * @return
     */
    public static List<Map.Entry<Integer,String>> sort(HashMap<Integer,String> hashMap) {
        List<Map.Entry<Integer,String>> list = new ArrayList<Map.Entry<Integer,String>>(hashMap.entrySet());
        //然后通过比较器来实现排序
        Collections.sort(list,new Comparator<Map.Entry<Integer,String>>() {
            //升序排序
            public int compare(Map.Entry<Integer, String> o1,
                               Map.Entry<Integer, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return list;
    }

    /**
     * 设置课假开始时间和结束时间
     * @param startDate 学期开始时间
     * @param time xxik 第xx周,星期i，第k节课
     * @param isEnd 是不是设置结束日期
     * @return 选择的这节课的时间
     */
    public static Date initSujectTime(Date startDate, int time,boolean isEnd) {
        int week = time / 100;//第几周
        int day = time % 100 / 10;//星期几
        int pitch = time % 10;//第几节课
        long pitchTime=isEnd?Constants.oneDay/3:Constants.oneDay/4;//isEnd 初试时间为8点 否则初试时间为6点
        if(pitch==1||pitch==2){//第一二大节
            pitchTime+=pitch*2*3600*1000;//每节2小时
        }else if(pitch==3||pitch==4){//第三四大节
            pitch/=2;
            pitchTime+=6*3600*1000;//初试时间为14点
            pitchTime+=pitch*2*3600*1000;//每节2小时
        }else {//第五六大节
            pitchTime+=9*3600*1000;//初试时间为17点
            pitch/=3;
            pitchTime+=pitch*15*3600*100;//每节1.5小时
        }
        Date date = new Date();
        date.setTime(startDate.getTime()+(long)(week-1)*Constants.oneDay*7+(long)(day-1)*Constants.oneDay+pitchTime);
        return date;
    }
}
