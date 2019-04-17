package com.example.mobilesafe.Utils;

import android.content.Context;

/**
 * Created by GaoYanHai on 2018/4/21.
 */

public class SharedPreferencesUtils {

    private static android.content.SharedPreferences sp;

    //存储此节点的标识信息  写入sp中
    public static void putBoolean(Context context, String key, Boolean value) {
        if (sp == null) {
            //节点的文件名 读取的方式
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    //读取节点的默认值  读取sp的值
    public static boolean getBoolean(Context context, String key, Boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }


    public static void putString(Context context, String key, String value) {
        if (sp == null) {
            //节点的文件名 读取的方式
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }


    public static String getString(Context context, String key, String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    //删除节点
    public static void remove(Context context, String key) {
        if (sp == null) {
            //节点的文件名 读取的方式
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    public static void putInt(Context context, String key, int value) {
        if (sp == null) {
            //节点的文件名 读取的方式
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }


    public static int getInt(Context context, String key, int defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }
}
