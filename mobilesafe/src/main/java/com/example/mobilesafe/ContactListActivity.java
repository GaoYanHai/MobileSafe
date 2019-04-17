package com.example.mobilesafe;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/4/26.
 */
public class ContactListActivity extends Activity {

    private static final String TAG ="ContactListActivity" ;
    private ListView lv_contact;
    private List<HashMap<String,String>> contactList = new ArrayList<HashMap<String,String>>();
    private MyAdapter mAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);

        initUI();
        //获取数据
        initData();

    }
    //数据适配器
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return contactList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contactList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getApplicationContext(),R.layout.listview_contact_item,null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);

            tv_name.setText(getItem(position).get("name"));
            tv_phone.setText(getItem(position).get("phone"));

            return view;
        }
    }

    //获取系统联系人的方法
    private void initData() {
        //可能联系人较多 所以可能是耗时操作 放到子线程中
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取内容解析器的对象
                ContentResolver contentResolver = getContentResolver();

                //查询系统联系人的数据库表
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), new String[]{"contact_id"}, null, null, null);
                //使用集合之前要清空数据
                contactList.clear();
                //循环游标
                while (cursor.moveToNext()){
                    String id = cursor.getString(0);
                    //Log.i(TAG, "id: "+id);
                    Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"},
                            "raw_contact_id=?", new String[]{id}, null);
                    HashMap<String, String> hashMap = new HashMap<>();
                    while (indexCursor.moveToNext()){
                        //循环获取每一个联系人的姓名与电话
                        String data = indexCursor.getString(0);
                        String type = indexCursor.getString(1);
                        //区分数据的类型
                        if (type.equals("vnd.android.cursor.item/phone_v2")){
                            if (!TextUtils.isEmpty(data)) {
                                hashMap.put("phone", data);
                            }

                        }else if (type.equals("vnd.android.cursor.item/name")){
                            if (!TextUtils.isEmpty(data)){
                                hashMap.put("name",data);
                            }

                        }
                    }
                    indexCursor.close();
                    contactList.add(hashMap);
                }
                cursor.close();
                //消息机制  目的是告诉主线程子线程数据填充已经完成
                mHandler.sendEmptyMessage(0);
            }
        }).start();


    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    HashMap<String, String> hashMap = mAdapter.getItem(position);
                    String phone = hashMap.get("phone");
                    //需要将数据返回给上一个活动
                    Intent intent = new Intent();
                    intent.putExtra("phone",phone);
                    setResult(0,intent);
                    finish();
                }

            }
        });
    }
}
