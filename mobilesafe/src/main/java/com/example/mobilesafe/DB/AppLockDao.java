package com.example.mobilesafe.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mobilesafe.DB.Domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/8.
 */

public class AppLockDao {
    private final AppLockOpenHelper appLockOpenHelper;
    private final Context context;
    // BlackNumberDao 的单例模式
    //私有化构造方法
    //声明一个当前类对象
    //提供一个静态方法  如果为空 就创建一个新的

    private AppLockDao(Context context) {
        appLockOpenHelper = new AppLockOpenHelper(context);
        this.context = context;
    }

    private static AppLockDao appLockDao = null;

    public static AppLockDao getInstance(Context context) {
        if (appLockDao == null) {
            appLockDao = new AppLockDao(context);
        }
        return appLockDao;
    }
    //插入方法
    public void insert(String packagename){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("packagename",packagename);
        db.insert("AppLock",null,contentValues);

        db.close();
        //内容观察者
        context.getContentResolver().notifyChange(Uri.parse("content//applock/change"),null);
    }

    public void delete(String packagename){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();

        db.delete("AppLock","packagename=?",new String[]{packagename});

        db.close();
        context.getContentResolver().notifyChange(Uri.parse("content//applock/change"),null);
    }

    public List<String> findAll(){
        SQLiteDatabase db = appLockOpenHelper.getReadableDatabase();

        Cursor cursor = db.query("AppLock", new String[]{"packagename"}, null, null, null, null, null);
        List<String> lockPackageList = new ArrayList<>();
        while (cursor.moveToNext()){
            lockPackageList.add(cursor.getString(0));
        }
        cursor.close();

        db.close();

        return lockPackageList;
    }

}
