package com.example.mobilesafe.Utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by GaoYanHai on 2018/4/23.
 */

public class MD5Utils {
    public static String encoder(String psd) {
        try {
            psd = psd + "mobilesafe";
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(psd.getBytes());

            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                stringBuffer.append(hexString);
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
