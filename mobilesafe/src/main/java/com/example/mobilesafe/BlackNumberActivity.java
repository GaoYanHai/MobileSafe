package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.mobilesafe.DB.BlackNumberDao;
import com.example.mobilesafe.DB.Domain.BlackNumberInfo;
import com.example.mobilesafe.Utils.ToastUtils;

import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/8.
 */
public class BlackNumberActivity extends Activity{

    private BlackNumberDao mDao;
    private List<BlackNumberInfo> mBlackNumberInfoList;
    private ListView lv_blacknumber;
    private MyAdapter myAdapter;
    private int mode = 1;
    boolean isLoad =false;
    private int count;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (myAdapter==null){
                myAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(myAdapter);
            }else {
                myAdapter.notifyDataSetChanged();
            }


        }
    };



    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mBlackNumberInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //listview的优化  不要忘了

            //converView 的优化
            //findviewbyid 查找次数的优化
            // 将viewholder 定义成静态的 不去创建多个对象
            //listview 有多个条目时 做成分页的算法 每一次加载20条

            ViewHolder holder=null;
            if (convertView == null){
                convertView = View.inflate(getApplicationContext(),R.layout.blacknumber_list,null);
                holder = new ViewHolder();
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.lv_delete = (ImageView) convertView.findViewById(R.id.lv_delete);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.lv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //删除数据库
                    mDao.delete(mBlackNumberInfoList.get(position).phone);
                    //删除集合  更新UI
                    mBlackNumberInfoList.remove(position);
                    if (myAdapter!=null){
                        myAdapter.notifyDataSetChanged();
                    }
                }
            });

           holder.tv_phone.setText(mBlackNumberInfoList.get(position).phone);
            int mode =  Integer.parseInt(mBlackNumberInfoList.get(position).mode);
            switch ( mode){
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有类型");
                    break;
            }
            return convertView;
        }
    }

    static class ViewHolder{
        TextView tv_phone;
        TextView tv_mode;
        ImageView lv_delete;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        initUI();
        initData();
    }

    private void initData() {
        //查询数据库  耗时操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDao = BlackNumberDao.getInstance(getApplicationContext());
                //查询部分数据
                mBlackNumberInfoList = mDao.find(0);
                count = mDao.getCount();
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initUI() {
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        Button bt_add = (Button) findViewById(R.id.bt_add);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            //状态发生改变的方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumberInfoList!=null){

                    //1 处于空闲状态  2 最后一个的条目索引值（从零开始）大于集合的大小  3避免重复加载的标识 正在加载是true 加载完是false 下次加载必须等上次加载完成
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                            && lv_blacknumber.getLastVisiblePosition()>=mBlackNumberInfoList.size()-1
                            && !isLoad){
                        //只有总条目数大与一页的数量才去做加载下一页的操作
                        if (count>mBlackNumberInfoList.size()){
                            //加载下一页的数据
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mDao = BlackNumberDao.getInstance(getApplicationContext());
                                    //查询下一页的数据
                                    List<BlackNumberInfo> moredata = mDao.find(mBlackNumberInfoList.size());
                                    //添加到集合中
                                    mBlackNumberInfoList.addAll(moredata);
                                    //通知数据适配器更新
                                    mHandler.sendEmptyMessage(0);

                                }
                            }).start();
                        }

                    }
                }

            }

            //滚动过程中的方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(getApplicationContext(),R.layout.dialog_blacknumber,null);
        dialog.setView(view,0,0,0,0);

        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);

        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_phone:
                        mode = 2;
                        break;
                    case R.id.rb_all:
                        mode = 3;
                        break;

                }
            }
        });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)){
                    mDao.insert(phone,mode+"");
                    //将更新的数据添加到集合中去
                    BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
                    blackNumberInfo.mode=mode+"";
                    blackNumberInfo.phone=phone;
                    mBlackNumberInfoList.add(0,blackNumberInfo);
                    //通知数据适配器进行更新操作  更新UI
                    if (myAdapter!=null){
                        myAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }else {
                    ToastUtils.show(getApplicationContext(),"号码不能为空");
                }
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
}
