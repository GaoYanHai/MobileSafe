package com.example.mobilesafe.Engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by GaoYanHai on 2018/5/4.
 */

public class CommonNumberDao {
    public String path = "data/data/com.example.mobilesafe/files/commonnum.db";


    //开启数据库 传递一个电话号码  返回一个归属地
    public List<Group> getGroup() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", new String[]{"name", "idx"}, null, null, null, null, null);
        List<Group> grouplist = new ArrayList<>();
        while (cursor.moveToNext()) {
            Group group = new Group();
            group.name =  cursor.getString(0);
            group.idx =  cursor.getString(1);
            group.childlist = getChild(group.idx);
            grouplist.add(group);
        }
        cursor.close();
        db.close();
        return grouplist;
    }
    public class Group {
            public String name;
            public String idx;
            public List<Child> childlist;
        }
    public List<Child> getChild(String idx){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
      //  Cursor cursor = db.query("table"+idx, new String[]{"name", "idx"}, null, null, null, null, null);
        Cursor cursor = db.rawQuery("select * from table" + idx + ";", null);
        List<Child> childlist = new ArrayList<>();
        while (cursor.moveToNext()) {
            Child child = new Child();
            child._id = cursor.getString(0);
            child.number = cursor.getString(1);
            child.name = cursor.getString(2);
            childlist.add(child);
        }
        cursor.close();
        db.close();
        return childlist;
    }
    public class Child {
        public String name;
        public String _id;
        public String number;
    }


}
