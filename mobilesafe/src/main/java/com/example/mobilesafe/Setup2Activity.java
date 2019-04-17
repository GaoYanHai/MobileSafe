package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.ToastUtils;
import com.example.mobilesafe.View.SettingItemView;

/**
 * Created by GaoYanHai on 2018/4/24.
 */
public class Setup2Activity extends Activity {

    private SettingItemView siv_sim_bound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initUI();

    }

    private void initUI() {
        siv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        String sim_number = SharedPreferencesUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(sim_number)) {
            siv_sim_bound.setCheck(false);
        } else {
            siv_sim_bound.setCheck(true);
        }

        siv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    boolean ischeck = siv_sim_bound.isCheck();
                    //取反
                    siv_sim_bound.setCheck(!ischeck);
                    if (!ischeck) {
                            // 先获取   再   存储sim卡的序列号
                            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                            String simSerialNumber = manager.getSimSerialNumber();

                            SharedPreferencesUtils.putString(getApplicationContext(), ConstantValue.SIM_NUMBER, simSerialNumber);

                    } else {
                        SharedPreferencesUtils.remove(getApplicationContext(), ConstantValue.SIM_NUMBER);
                    }




            }
        });

    }


    public void NextPage(View view) {
        String string = SharedPreferencesUtils.getString(this, ConstantValue.SIM_NUMBER, "");
        if (TextUtils.isEmpty(string)){
            ToastUtils.show(this,"请绑定sim卡");
        }else {
            Intent intent = new Intent(getApplicationContext(), Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_animator,R.anim.next_out_animator);
        }


    }

    public void LastPage(View view) {
        Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_animator,R.anim.pre_out_animator);
    }


}
