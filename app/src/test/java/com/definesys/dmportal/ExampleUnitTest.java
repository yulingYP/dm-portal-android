package com.definesys.dmportal;

import com.definesys.dmportal.appstore.utils.DensityUtil;

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
        String aa = "0809";
        String result = DensityUtil.getPitchString(aa.charAt(3))+DensityUtil.getPitchString(aa.charAt(2));
        System.out.println(result);
        assertEquals(4, 2 + 2);
    }
}