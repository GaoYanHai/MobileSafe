package com.example.mobilesafe.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/4.
 */

public class VirusDao {
    public static String path = "data/data/com.example.mobilesafe/files/antivirus.db";

    //开启数据库 传递一个电话号码  返回一个归属地
    public static List<String> getVirusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<>();
        while (cursor.moveToNext()) {
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }
}
