package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilesafe.Utils.ToastUtils;

/**
 * Created by GaoYanHai on 2018/5/30.
 */
public class EnterPasdActivity extends Activity{

    private TextView tv_app_name;
    private ImageView iv_app_icon;
    private EditText et_psd;
    private Button bt_submit;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpasd);
        packageName = getIntent().getStringExtra("packageName");
        initUI();
        initData();
    }

    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            iv_app_icon.setBackground(applicationInfo.loadIcon(pm));
            tv_app_name.setText(applicationInfo.loadLabel(pm).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd = et_psd.getText().toString();
                if (!psd.isEmpty()){
                    if (psd.equals("123")){
                        //进入应用
                        //解锁后  发送广播不要再次拦截
                        Intent intent = new Intent("android.intent.action.SKIP");
                        intent.putExtra("packageName",packageName);
                        sendBroadcast(intent);
                        finish();
                    }else {
                        ToastUtils.show(getApplicationContext(),"密码错误");
                    }
                }else {
                    ToastUtils.show(getApplicationContext(),"密码不能为空");
                }
            }
        });
    }

    private void initUI() {
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
        et_psd = (EditText) findViewById(R.id.et_psd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    //回退按钮的处理
    @Override
    public void onBackPressed() {
        //回到桌面  隐式意图
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        super.onBackPressed();
    }
}
