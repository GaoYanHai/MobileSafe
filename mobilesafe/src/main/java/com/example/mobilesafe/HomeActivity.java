package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.MD5Utils;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.ToastUtils;

/**
 * Created by GaoYanHai on 2018/4/11.
 */
public class HomeActivity extends Activity {

    private GridView gv_home;
    private String[] mTiteStr;
    private int[] mDrawableIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();
        //初始化数据
        initData();


        //动态获取权限
        //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行
        if (ContextCompat.checkSelfPermission(HomeActivity.this, "Manifest.permission.SYSTEM_ALERT_WINDOW") != PackageManager.PERMISSION_GRANTED) {
            //注意第二个参数没有双引号
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{
                    Manifest.permission.READ_PHONE_STATE
                    //获取读取联系人的权限
                    , Manifest.permission.READ_CONTACTS
                    //发送短信
                    , Manifest.permission.SEND_SMS
                    //定位
                    , Manifest.permission.ACCESS_FINE_LOCATION
                    //接收短信
                    , Manifest.permission.RECEIVE_SMS
                    //读写SD卡的权限
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    //挂载view 的权限
                    , Manifest.permission.SYSTEM_ALERT_WINDOW
                    , Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                    //打电话
                    , Manifest.permission.CALL_PHONE}, 1);
        }


    }

    private void initData() {
        mTiteStr = new String[]{
                "手机防盗",
                "通信卫士",
                "软件管理",
                "进程管理",
                "流量统计",
                "手机杀毒",
                "缓存清理",
                "高级工具",
                "设置中心"
        };
        mDrawableIds = new int[]{
                R.drawable.home_safe,
                R.drawable.home_callmsgsafe,
                R.drawable.home_apps,
                R.drawable.home_taskmanager,
                R.drawable.home_netmanager,
                R.drawable.home_trojan,
                R.drawable.home_sysoptimize,
                R.drawable.home_tools,
                R.drawable.home_settings
        };
        gv_home.setAdapter(new MyAdapter());
        //设置点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(), BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(), AppManagerActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(getApplicationContext(), ProcessManagerActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), TrafficActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(getApplicationContext(), AnitVirusActivity.class));
                        break;
                    case 6:
//                        startActivity(new Intent(getApplicationContext(), CacheClearActivity.class));
                        startActivity(new Intent(getApplicationContext(), BaseCacheClearActivity.class));
                        break;
                    case 7:
                        Intent intent1 = new Intent(getApplicationContext(), AToolActivity.class);
                        startActivity(intent1);
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    //显示密码对话框
    private void showDialog() {

        String psd = SharedPreferencesUtils.getString(this, ConstantValue.SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            showSetPsdDialog();
        } else {
            showConfirmPsdDialog();
        }
    }

    //确认密码框
    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);

        dialog.setView(view);
        dialog.show();
        //要加上view  否则会报空指针  因为控件都是在view中
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        final Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);

                String confirmpsd = et_confirm_psd.getText().toString();
                String psd = SharedPreferencesUtils.getString(getApplicationContext(), ConstantValue.SAFE_PSD, "");
                if (!TextUtils.isEmpty(confirmpsd)) {
                    if (psd.equals(MD5Utils.encoder(confirmpsd))) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        //结束这个对话框  在开启活动之后
                        dialog.dismiss();

                    } else {
                        ToastUtils.show(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtils.show(getApplicationContext(), "密码不能为空");

                }
            }
        });
        //取消按钮
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    //设置对话框密码
    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        final View view = View.inflate(this, R.layout.dialog_psd, null);

        dialog.setView(view);
        dialog.show();
        //要加上view  否则会报空指针  因为控件都是在view中
        Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
        final Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);

                String psd = et_set_psd.getText().toString();
                String confirmpsd = et_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmpsd)) {
                    if (psd.equals(confirmpsd)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        //结束这个对话框  在开启活动之后
                        dialog.dismiss();

                        SharedPreferencesUtils.putString(getApplicationContext(), ConstantValue.SAFE_PSD, MD5Utils.encoder(psd));

                    } else {
                        ToastUtils.show(getApplicationContext(), "两次密码不一致");
                    }
                } else {
                    ToastUtils.show(getApplicationContext(), "密码不能为空");

                }
            }
        });
        //取消按钮
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void initUI() {
        gv_home = (GridView) findViewById(R.id.gv_home);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTiteStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTiteStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            ImageView iv_image = (ImageView) view.findViewById(R.id.iv_image);
            tv_title.setText(mTiteStr[position]);
            iv_image.setBackgroundResource(mDrawableIds[position]);
            return view;
        }
    }
}
