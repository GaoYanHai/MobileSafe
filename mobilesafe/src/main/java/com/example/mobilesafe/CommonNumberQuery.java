package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.mobilesafe.Engine.CommonNumberDao;

import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/26.
 */
public class CommonNumberQuery extends Activity{

    private ExpandableListView elv_common_number;
    private List<CommonNumberDao.Group> mGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commom_number_query);
        initUI();
        initData();
    }

    private void initData() {
        CommonNumberDao commonNumberDao = new CommonNumberDao();
        mGroup = commonNumberDao.getGroup();
        final MyAdapter myAdapter = new MyAdapter();
        elv_common_number.setAdapter(myAdapter);

        elv_common_number.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                startCall(myAdapter.getChild(groupPosition,childPosition).number);
                return false;
            }
        });

    }

    private void startCall(String number) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+number));
        if(ContextCompat.checkSelfPermission(CommonNumberQuery.this,"Manifest.permission.CALL_PHONE") != PackageManager.PERMISSION_GRANTED) {
            //注意第二个参数没有双引号
            ActivityCompat.requestPermissions(CommonNumberQuery.this, new String[]{
                    Manifest.permission.CALL_PHONE}, 1);
        }
        startActivity(intent);
    }

    private void initUI() {
        elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
    }

    class MyAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return mGroup.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroup.get(groupPosition).childlist.size();
        }

        @Override
        public CommonNumberDao.Group getGroup(int groupPosition) {
            return mGroup.get(groupPosition);
        }

        @Override
        public CommonNumberDao.Child getChild(int groupPosition, int childPosition) {
            return mGroup.get(groupPosition).childlist.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText("     "+getGroup(groupPosition).name);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,24);
            return textView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.elv_child_item, null);
            TextView tv_elv_name = (TextView) view.findViewById(R.id.tv_elv_name);
            TextView tv_elv_number = (TextView) view.findViewById(R.id.tv_elv_number);
            tv_elv_name.setText(getChild(groupPosition,childPosition).name);
            tv_elv_number.setText(getChild(groupPosition,childPosition).number);

            return view;
        }

        //是否让孩子响应点击事件
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
