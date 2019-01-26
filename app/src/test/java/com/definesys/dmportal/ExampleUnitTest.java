package com.definesys.dmportal;

import android.animation.IntArrayEvaluator;

import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.main.util.MD5Util;

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

        System.out.println(MD5Util.string2MD5("123456"));
        assertEquals(4, 2 + 2);
    }
}