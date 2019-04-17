package com.example.mobilesafe.Utils;


import java.io.IOException;
import java.io.InputStream;

/**
 * Created by GaoYanHai on 2018/4/3.
 */
public class StreamUtils {
    public static String StreamToString(InputStream is) {
        try {

            String data = "";
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = is.read(buffer)) != -1) {
                data = data + new String(buffer, 0, len);

            }
                return data;
            } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
