package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.ToastUtils;

/**
 * Created by GaoYanHai on 2018/4/24.
 */
public class Setup4Activity extends Activity{

    private CheckBox cb_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        initUI();


    }

    private void initUI() {
        cb_box = (CheckBox) findViewById(R.id.cb_box1);
        boolean open_security = SharedPreferencesUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        cb_box.setChecked(open_security);
        if (open_security){
            cb_box.setText("防盗保护已开启！");
        }else {
            cb_box.setText("防盗保护已关闭！");
        }
        //设置点击后的状态
        cb_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_box.setText("防盗保护已开启！");
                }else {
                    cb_box.setText("防盗保护已关闭！");
                }
                //存储点击后的状态
                SharedPreferencesUtils.putBoolean(getApplicationContext(),ConstantValue.OPEN_SECURITY,isChecked);
            }
        });


    }

    public void NextPage(View view){
        boolean open_security = SharedPreferencesUtils.getBoolean(this, ConstantValue.OPEN_SECURITY, false);
        if (open_security){
            Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
            startActivity(intent);
            SharedPreferencesUtils.putBoolean(this, ConstantValue.SET_OVER,true);
            finish();
            overridePendingTransition(R.anim.next_in_animator,R.anim.next_out_animator);
        }else {
            ToastUtils.show(this,"请开启防盗保护！");
        }

    }


    public void LastPage(View view){
        Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_animator,R.anim.pre_out_animator);
    }
}
