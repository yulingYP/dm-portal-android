package com.definesys.dmportal;

import android.animation.IntArrayEvaluator;

import com.definesys.dmportal.appstore.utils.DensityUtil;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(1);
        hashSet.add(2);
        hashSet.add(11);
        System.out.println(hashSet.contains(1));
        hashSet.remove(1);
        String aa="01234";
        for(int i = 0 ; i <aa.length();i++){
            System.out.println(aa.substring(i));
        }
        ;
//        System.out.println(DensityUtil.getFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",new Date()).toString());
        assertEquals(4, 2 + 2);
    }
}