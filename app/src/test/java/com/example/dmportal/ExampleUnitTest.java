package com.example.dmportal;

import com.example.dmportal.appstore.utils.DensityUtil;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        String content = "12812小时";
//        String result = "";
////        boolean flag = content.contains("+")&&content.indexOf("+")<content.length()-1&&content.indexOf("+")>0;
//        try {
//            result =content.substring(0,content.indexOf("天"));
//        }catch (Exception e){
//            result = "error";
//        }
//        Date date = new Date();
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(date);
        String str = "sfsdf1111";
        System.out.println(DensityUtil.string2Long(str));
       assertEquals(4, 2 + 2);
    }


}