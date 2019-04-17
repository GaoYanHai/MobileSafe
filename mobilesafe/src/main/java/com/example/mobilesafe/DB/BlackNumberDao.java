package com.example.mobilesafe.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobilesafe.DB.Domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/8.
 */

public class BlackNumberDao {
    private final BlackNumberOpenHelper blackNumberOpenHelper;
    // BlackNumberDao 的单例模式
    //私有化构造方法
    //声明一个当前类对象
    //提供一个静态方法  如果为空 就创建一个新的

    private BlackNumberDao(Context context) {
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    private static BlackNumberDao blackNumberDao = null;

    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }


    public void insert(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phone);
        values.put("mode", mode);
        db.insert("BlackNumber", null, values);
        db.close();
    }

    public void delete(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        db.delete("BlackNumber", "phone = ?", new String[]{phone});
        db.close();
    }

    public void update(String phone, String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        db.update("BlackNumber", values, "phone = ?", new String[]{phone});
        db.close();
    }

    public List<BlackNumberInfo> queryAll() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.query("BlackNumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberList = new ArrayList<>();

        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);

        }
        cursor.close();
        db.close();
        return blackNumberList;
    }


    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select phone,mode from BlackNumber order by _id desc limit ?,20", new String[]{index + ""});
        List<BlackNumberInfo> blackNumberList = new ArrayList<>();

        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);

        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    //获取条目的总数
    public int getCount() {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from BlackNumber", null);

        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    //
    public int getMode(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int mode = 0;
        Cursor cursor = db.query("BlackNumber", new String[]{"mode"}, "phone=?", new String[]{phone}, null, null, null);

        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return mode;
    }

}
