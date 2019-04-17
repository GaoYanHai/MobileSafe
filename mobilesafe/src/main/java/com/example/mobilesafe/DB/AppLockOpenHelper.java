package com.example.mobilesafe.DB;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GaoYanHai on 2018/5/8.
 */

public class AppLockOpenHelper extends SQLiteOpenHelper {
    public AppLockOpenHelper(Context context) {
        super(context, "AppLock.db", null, 1);
    }

    public AppLockOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table AppLock (_id integer primary key autoincrement , packagename varchare(50)) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
