package com.example.dmportal.main.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static String string2MD5(String plainText) {
        byte[] secretBytes = null;
        try {
            //创建具有指定算法名称的信息摘要, 使用指定的字节数组对摘要进行最后更新，然后完成摘要计算
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("get encryption style instance failed");
        }
        //轮换字节数组为十六进制字符串,BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

}