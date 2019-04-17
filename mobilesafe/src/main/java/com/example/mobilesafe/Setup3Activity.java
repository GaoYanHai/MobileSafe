package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.ToastUtils;

/**
 * Created by GaoYanHai on 2018/4/24.
 */
public class Setup3Activity extends Activity {

    private EditText et_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initUI();


    }

    private void initUI() {
        et_number = (EditText) findViewById(R.id.et_number);
        String contact_phone = SharedPreferencesUtils.getString(this, ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(contact_phone)){
            et_number.setText(contact_phone);
        }

        Button bt_select_number = (Button) findViewById(R.id.bt_select_number);
        bt_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    //启动活动返回的方法  接受返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String phone = data.getStringExtra("phone");
            //将特殊字符进行过滤
            phone = phone.replace("-", "").replace(" ", "").trim();
            et_number.setText(phone);
            //存储联系人
            SharedPreferencesUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);
        }


    }

    public void NextPage(View view) {
        String phone = et_number.getText().toString().trim();

        //String contact_phone = SharedPreferencesUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
        if (!TextUtils.isEmpty(phone)) {
            Intent intent = new Intent(getApplicationContext(), Setup4Activity.class);
            startActivity(intent);
            finish();
            //如果是输入的号码也要存储一下
            SharedPreferencesUtils.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, phone);

            overridePendingTransition(R.anim.next_in_animator,R.anim.next_out_animator);
        } else {
            ToastUtils.show(this, "安全号码不能为空！");
        }

    }


    public void LastPage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_animator,R.anim.pre_out_animator);
    }
}
