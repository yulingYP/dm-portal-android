package com.definesys.dmportal.appstore.utils;

import android.content.Context;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.CursorArg;
import com.definesys.dmportal.appstore.bean.LeaveInfo;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.definesys.dmportal.appstore.utils.Constants.oneDay;

/**
 *
 * Created by 羽翎 on 2018/11/24.
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
     * @param source s
     * @param replacement r
     * @return r
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
     * @param pxValue p
     * @param context c
     * @return r
     */
    public static float px2sp( Context context,float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return  (pxValue / scale + 0.5f);
    }

    /**
     * 获取当前数的2进制形式
     * @param item 16进制
     * @return r
     */
    public static String getPitchString(char item){
        String [] allresults = {"0000","0001","0010","0011","0100","0101",
                "0110","0111","1000","1001","1010","1011","1100","1101","1110","1111"};
        return allresults [Integer.parseInt(String.valueOf(item),16)].trim();
    }

    /**
     * 设置某天的开始或结束时间
     * @param date d
     * @param isStart 是不是设置开始时间
     * @return r
     */
    public static Date setDate(Date date,boolean isStart){
        if(date==null)
            return new Date();
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
     * @return r
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
     * @return r
     */
    public static String dateTypeToString(String type,Date date){
        if(date==null)
            return "";
        SimpleDateFormat sDateFormat=new SimpleDateFormat(type, Locale.getDefault());
        return sDateFormat.format(date);
    }

    /**
     * 设置请假类型
     * @param type t
     */
    public static String setTypeText(String type) {
        int hasPostion = type.indexOf("(");//是否包含括号
        return hasPostion>=0?type.substring(0,hasPostion):type;
    }

    /**
     * 返回排序后的list
     * @param hashMap 待排序map
     * @return r
     */
    public static List<Map.Entry<Integer,String>> sort(HashMap<Integer,String> hashMap) {
        List<Map.Entry<Integer,String>> list = new ArrayList<>(hashMap.entrySet());
        //然后通过比较器来实现排序
        //升序排序
        Collections.sort(list, (o1, o2) -> o1.getKey().compareTo(o2.getKey()));
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
    /**
     * 返回该请假信息的审批状态
     * @param leaveInfo l
     * @param isMain 是不是请假主页
     * @return r
     */
    public static String getApprovalStatus(LeaveInfo leaveInfo,Context context,boolean isMain){
        if(leaveInfo==null)//没有请假记录
            return context.getString(R.string.status_tip_6);

        Date date=null;//请假结束日期
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.date_type), Locale.getDefault());
            date = simpleDateFormat.parse(leaveInfo.getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(leaveInfo.getApprovalStatus()<100)//正在审批
            return context.getString(R.string.status_tip_1);
        else if(leaveInfo.getApprovalStatus()==110&&(System.currentTimeMillis()-leaveInfo.getUpdateDate().getTime())/(24*60*60*1000)<1)//审批未通过且时间未超过一天
            return context.getString(R.string.status_tip_3);
        else if(leaveInfo.getApprovalStatus()==110&&(System.currentTimeMillis()-leaveInfo.getUpdateDate().getTime())/(24*60*60*1000)>=1)//审批未通过且时间超过一天
            return isMain?context.getString(R.string.status_tip_6):context.getString(R.string.status_tip_3);
        else if(leaveInfo.getApprovalStatus()==120&&(System.currentTimeMillis()-leaveInfo.getUpdateDate().getTime())/(24*60*60*1000)<1)//已销假且时间未超过一天
            return context.getString(R.string.status_tip_5);
        else if(leaveInfo.getApprovalStatus()==120&&(System.currentTimeMillis()-leaveInfo.getUpdateDate().getTime())/(24*60*60*1000)>=1)//已销假通过且时间超过一天
            return isMain?context.getString(R.string.status_tip_6):context.getString(R.string.status_tip_5);
        if(date!=null) {//长短假
            if (leaveInfo.getApprovalStatus() == 100 && date.getTime() - System.currentTimeMillis() < 0&&leaveInfo.getType()==2)//长假 已批准假期且已到请假结束日期
                return context.getString(R.string.status_tip_4);
            else if (leaveInfo.getApprovalStatus() == 100)//长假除外 已批准
                return context.getString(R.string.status_tip_2);

        }
        return context.getString(R.string.status_tip_7);
    }
    public static String getApplyStatus(Context context,int status){
        if(status==0)
            return context.getString(R.string.status_tip_1);
        else if(status==100)
            return context.getString(R.string.status_tip_2);
        else if(status==110)
            return context.getString(R.string.status_tip_3);
        return context.getString(R.string.status_tip_7);
    }
    /**
     * 根据内容设置textView的颜色
     * @param data d
     * @param tv t
     * @param context c
     */
    public static void setTVcolor(String data, TextView tv, Context context) {
        if(context.getString(R.string.status_tip_1).equals(data))
            tv.setTextColor(context.getResources().getColor(R.color.blue));
        else if(context.getString(R.string.status_tip_2).equals(data))
            tv.setTextColor(context.getResources().getColor(R.color.green));
        else if(context.getString(R.string.status_tip_3).equals(data))
            tv.setTextColor(context.getResources().getColor(R.color.red_error));
        else if(context.getString(R.string.status_tip_4).equals(data))
            tv.setTextColor(context.getResources().getColor(R.color.customOrange));
        else if(context.getString(R.string.status_tip_5).equals(data))
            tv.setTextColor(context.getResources().getColor(R.color.customGreen));
        else
            tv.setTextColor(context.getResources().getColor(R.color.buttonBlue));
    }

    /**
     * 获取上课的所有班级的id
     * @param cursorArg c
     * @return r
     */
    public static String getClassLisId(CursorArg cursorArg) {
        if(cursorArg==null)
            return "";
        List<String> classList = cursorArg.getClassId();//上课的班级id
        if(classList==null)
            return "";
        StringBuilder classId = new StringBuilder();
        for (int m = 0; m < classList.size(); m++) {
            if (m > 0) classId.append("、").append(classList.get(m));
            else classId.append(classList.get(m));
        }
        return classId.toString();
    }

    /**
     * 获取周数大小比较后的字符串
     * @param endWeek 结束周
     * @param startWeek 开始周
     * @param sourceStr 已经存好的周数1-12或1-12,14-18
     * @return sourceStr为1-12 待插入的字符串为13-18 则最终返回1-18 若带插入的字符串为14-18 则返回1-12,14-18
     */
    public static String getResultWeek(int endWeek,int startWeek,String sourceStr){
        String[] arr1=sourceStr.split(",");

        for(int i =0 ;i<arr1.length;i++ ){
            String[] arr2=arr1[i].split("-");
            int sorcueStart = Integer.valueOf(arr2[0]);
            int sorcueEnd =Integer.valueOf(arr2[1]);
            if(startWeek-sorcueEnd==1)//待填如周的开始与已填入周的结束相邻
                arr1[i]=""+sorcueStart+"-"+endWeek;
        }
        StringBuilder sourceStrBuilder = new StringBuilder(sourceStr);
        for(int i = 0; i<arr1.length; i++ ){
            if(i==0) sourceStrBuilder = new StringBuilder(arr1[i]);
            else sourceStrBuilder.append(",").append(arr1[i]);
        }
        sourceStr = sourceStrBuilder.toString();
        return sourceStr;
    }

    /**
     * 获取请假时长
     * @param startDateStr 开始时间的字符串 yyyy年MM月dd日 HH时
     * @param endDateStr 结束时间的字符串 yyyy年MM月dd日 HH时
     * @param isHour 是否需要显示小时
     * @return r
     */
    public  static String getSumTime(String startDateStr,String endDateStr,Context context,boolean isHour){
        SimpleDateFormat df = new SimpleDateFormat(context.getString(R.string.date_type), Locale.getDefault());
        Date startDate ;
        Date endDate ;
        try {
            startDate = df.parse(startDateStr);
            endDate = df.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            startDate = new Date();
            endDate = new Date();
        }
        long time = endDate.getTime() - startDate.getTime();
        int day = (int)(time/oneDay );
        int hour = (int)(time/(oneDay /24))-day*24;
        if(isHour)
        return (day>0?context.getString(R.string.off_day,day):"")+(day>0&&hour==0?"":context.getString(R.string.off_hour,hour));
        else return context.getString(R.string.off_day,day);
    }
}
