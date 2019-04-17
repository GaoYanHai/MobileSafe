package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.mobilesafe.DB.Domain.ProcessInfo;
import com.example.mobilesafe.Engine.ProcessInfoProvider;
import com.example.mobilesafe.Utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by GaoYanHai on 2018/5/24.
 */
public class ProcessManagerActivity extends Activity implements View.OnClickListener {

    private TextView tv_des;
    private Button bt_select_all;
    private Button bt_select_reverse;
    private Button bt_clear;
    private Button bt_setting;
    private ListView lv_process_list;
    private TextView tv_process_count;
    private TextView tv_memory;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemList;
    private ArrayList<ProcessInfo> mCustomerList;

    private ProcessInfo mProcessInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyAdapter myAdapter = new MyAdapter();
            lv_process_list.setAdapter(myAdapter);
            if (tv_des != null && mCustomerList != null) {
                tv_des.setText("用户进程（" + mCustomerList.size() + "）");
            }
        }
    };

    private class MyAdapter extends BaseAdapter {

        //获取数据适配器中条目类型的总数  修改成两种
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                //纯文本条目
                return 0;
            } else {
                //图片+文本条目
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mSystemList.size() + mCustomerList.size() + 2;
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return null;
            } else {

                if (position < mCustomerList.size() + 1) {
                    return mCustomerList.get(position - 1);
                } else {
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }


        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {

                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.app_list_item_title, null);
                    holder = new ViewTitleHolder();

                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户进程（" + mCustomerList.size() + "）");
                } else {
                    holder.tv_title.setText("系统进程（" + mSystemList.size() + "）");
                }

                return convertView;

            } else {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.process_list_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_memory = (TextView) convertView.findViewById(R.id.tv_memory);
                    holder.cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackground(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                holder.tv_memory.setText(Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize));
                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.cb_box.setVisibility(View.GONE);
                } else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck);
                return convertView;
            }
        }


    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory;
        CheckBox cb_box;
    }

    static class ViewTitleHolder {

        TextView tv_title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        initUI();
        initTitleDate();
        initListDate();
    }

    private void initListDate() {
        getData();
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcess(getApplicationContext());
                mSystemList = new ArrayList<>();
                mCustomerList = new ArrayList<>();
                for (ProcessInfo info : mProcessInfoList) {
                    if (info.isSystem) {
                        mSystemList.add(info);
                    } else {
                        mCustomerList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initTitleDate() {
        int processCount = ProcessInfoProvider.getProcessCount(this);
        tv_process_count.setText("进程总数" + processCount);
        //获取内存大小   并且进行格式化
        long availSpace = ProcessInfoProvider.getAvailSpace(this);
        String availspace = Formatter.formatFileSize(this, availSpace);
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        String totalspace = Formatter.formatFileSize(this, totalSpace);
        tv_memory.setText("剩余内存/总内存" + availspace + "/" + totalspace);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory = (TextView) findViewById(R.id.tv_memory);
        tv_des = (TextView) findViewById(R.id.tv_process_des);
        bt_select_all = (Button) findViewById(R.id.bt_all);
        bt_select_reverse = (Button) findViewById(R.id.bt_reverse);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_setting = (Button) findViewById(R.id.bt_setting);
        lv_process_list = (ListView) findViewById(R.id.lv_process_list);

        bt_select_all.setOnClickListener(this);
        bt_select_reverse.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_setting.setOnClickListener(this);

        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动过程中调用方法
                //AbsListView中view就是listView对象
                //firstVisibleItem第一个可见条目索引值
                //visibleItemCount当前一个屏幕的可见条目数
                //总共条目总数
                if(mCustomerList!=null && mSystemList!=null){
                    if(firstVisibleItem>=mCustomerList.size()+1){
                        //滚动到了系统条目
                        tv_des.setText("系统进程("+mSystemList.size()+")");
                    }else{
                        //滚动到了用户应用条目
                        tv_des.setText("用户进程("+mCustomerList.size()+")");
                    }
                }

            }
        });

        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0 || position == mCustomerList.size()+1){
                    return;
                }else{
                    if(position<mCustomerList.size()+1){
                        mProcessInfo = mCustomerList.get(position-1);
                    }else{
                        //返回系统应用对应条目的对象
                        mProcessInfo = mSystemList.get(position - mCustomerList.size()-2);
                    }
                    if(mProcessInfo!=null){
                        if(!mProcessInfo.packageName.equals(getPackageName())){
                            //选中条目指向的对象和本应用的包名不一致,才需要去状态取反和设置单选框状态
                            //状态取反
                            mProcessInfo.isCheck = !mProcessInfo.isCheck;
                            //checkbox显示状态切换
                            //通过选中条目的view对象,findViewById找到此条目指向的cb_box,然后切换其状态
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_all:
                ToastUtils.show(getApplicationContext(),"1234567");
                break;
            case R.id.bt_reverse:
                ToastUtils.show(getApplicationContext(),"1234567");
                break;
            case R.id.bt_clear:
                ToastUtils.show(getApplicationContext(),"1234567");
                break;
            case R.id.bt_setting:
                ToastUtils.show(getApplicationContext(),"1234567");
                break;
        }
    }
}
