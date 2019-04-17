package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;

/**
 * Created by GaoYanHai on 2018/4/22.
 */

public class SetupOverActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        判断是否设置完成  完成就不要再进入设置界面了
        boolean set_over = SharedPreferencesUtils.getBoolean(this, ConstantValue.SET_OVER, false);
        if (set_over) {
//            设置完成
            setContentView(R.layout.activity_set_over);
            TextView tv_safe_number = (TextView) findViewById(R.id.tv_safe_number);
            String phone = SharedPreferencesUtils.getString(this, ConstantValue.CONTACT_PHONE, "");
                tv_safe_number.setText(phone);
            TextView tv_setup_again = (TextView) findViewById(R.id.tv_setup_again);
            tv_setup_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                    startActivity(intent);
                    //关闭当前的界面
                    finish();
                }
            });

        }else{
            //设置未完成
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //关闭当前的界面
            finish();
        }



    }
}
