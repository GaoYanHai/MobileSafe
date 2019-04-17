package com.example.mobilesafe;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.service.wallpaper.WallpaperService;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobilesafe.Engine.AddressDao;

/**
 * Created by GaoYanHai on 2018/5/4.
 */

public class QueryAddressActivity extends Activity {

    private EditText et_phone;
    private Button bt_query;
    private TextView tv_query_result;
    private String mAddress;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_query_result.setText(mAddress);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);
        initUI();
    }

    private void initUI() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        bt_query = (Button) findViewById(R.id.bt_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);


        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    //耗时操作  开启子线程
                    query(phone);
                }else {
                    //实现抖动效果
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    et_phone.startAnimation(shake);
                    //实现手机震动
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(1000);
                    //震动的规则  震动2秒 停止5秒  不重复
                   // vibrator.vibrate(new long[]{2000,5000,2000,5000},-1);
                }

            }

        });

        //实时查询
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                query(et_phone.getText().toString().trim());
            }
        });
    }

    private void query(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
}
