package com.example.mobilesafe.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by GaoYanHai on 2018/5/4.
 */

public class AddressDao {
    public static String path = "data/data/com.example.mobilesafe/files/address.db";
    private static String mAddress = "未知号码";

    //开启数据库 传递一个电话号码  返回一个归属地
    public static String getAddress(String phone) {
        mAddress = "未知号码";
        //手机号码的正则表达式
        String regularExpression = "^1[3-8]\\d{9}";

        //开启数据库   只读的方式
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regularExpression)) {
            //截取前七位数字
            phone = phone.substring(0, 7);

            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);
                Cursor indexcursor = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                if (indexcursor.moveToNext()) {
                    mAddress = indexcursor.getString(0);

                }
            } else {
                mAddress = "未知号码";
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务电话";
                    break;
                case 7:
                    mAddress = "固定电话";
                    break;
                case 8:
                    mAddress = "固定电话";
                    break;
                case 11:
                    //区号加座机号
                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, "area=?", new String[]{area}, null, null, null);
                    if (cursor.moveToNext()) {
                        mAddress = cursor.getString(0);
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
                case 12:
                    //区号加座机号
                    String area1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()) {
                        mAddress = cursor1.getString(0);
                    } else {
                        mAddress = "未知号码";
                    }

                    break;
            }
        }
        return mAddress;

    }
}
