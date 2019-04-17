package com.example.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.example.mobilesafe.Engine.SmsBackUp;
import com.example.mobilesafe.Utils.ToastUtils;

import java.io.File;

/**
 * Created by GaoYanHai on 2018/5/4.
 */


public class AToolActivity extends Activity {
    private Handler mhandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ToastUtils.show(getApplicationContext(),"短信备份已完成");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
        //号码归属地查询
        initPhoneAddress();
        //短信备份
        initSmsBackUp();
        //常用号码查询
        initCommonNumberQuery();
        //程序锁
        initAppLock();

    }

    private void initAppLock() {
        TextView tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        TextView tv_common_number_query = (TextView) findViewById(R.id.tv_common_number_query);
        tv_common_number_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQuery.class));
            }
        });

    }

    private void initSmsBackUp() {
        TextView tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBackUpDialog();
            }
        });
    }

    private void showBackUpDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.warning);
        progressDialog.setTitle("短信备份");
        //指定进度条的样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        //短信的获取  耗时操作 子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms.xml";
                //回调方法的应用
                SmsBackUp.backup(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        progressDialog.setProgress(index);
                    }
                });

                progressDialog.dismiss();

                mhandler.sendEmptyMessage(0);

            }
        }).start();

    }

    private void initPhoneAddress() {
        TextView tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QueryAddressActivity.class);
                startActivity(intent);
            }
        });
    }
}
