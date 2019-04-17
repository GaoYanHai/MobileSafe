package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.mobilesafe.Service.AddressService;
import com.example.mobilesafe.Service.BlackNumberService;
import com.example.mobilesafe.Service.WatchDogService;
import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.ServiceUtils;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.View.SettingClickView;
import com.example.mobilesafe.View.SettingItemView;

/**
 * Created by GaoYanHai on 2018/4/21.
 */
public class SettingActivity extends Activity {
    private static final String TAG = "SettingActivity";
    private SettingClickView setting_click;
    private String[] mToastStyle;
    private int mToast_style;
    private SettingClickView mSetting_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        //是否开启自动更新的方法
        initUpData();

        //窗体的权限
        if (!Settings.canDrawOverlays(getApplicationContext())) {
            //启动Activity让用户授权
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        }

        //是否开启来电归属地的方法
        initAddress();
        //来电土司样式的选择
        initToastStyle();
        //来电土司的位置选择 双击居中
        initLocation();
        //来电黑名单
        initBlackNumber();
        //程序锁
        initAppLock();
    }

    private void initAppLock() {
        final SettingItemView siv_applock = (SettingItemView) findViewById(R.id.siv_applock);
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.mobilesafe.Service.WatchDogService");
        siv_applock.setCheck(isRunning);

        siv_applock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_applock.isCheck();
                siv_applock.setCheck(!check);
                if (!check){
                    startService(new Intent(getApplicationContext(), WatchDogService.class));
                }else {
                    stopService(new Intent(getApplicationContext(), WatchDogService.class));
                }
            }
        });
    }

    private void initBlackNumber() {
        final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknumber);
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.mobilesafe.Service.BlackNumberService");
        siv_blacknumber.setCheck(isRunning);

        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_blacknumber.isCheck();
                siv_blacknumber.setCheck(!check);
                if (!check){
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                }else {
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });

    }

    private void initLocation() {
        SettingClickView scv_toast_location = (SettingClickView) findViewById(R.id.scv_toast_location);
        scv_toast_location.SettingTitle("归属地提示框的位置");
        scv_toast_location.SettingDes("设置归属地提示框的位置");
        scv_toast_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
            }
        });
    }

    private void initToastStyle() {
        mSetting_click = (SettingClickView) findViewById(R.id.scv_toast_style);
        mSetting_click.SettingTitle("设置归属地显示风格");
        mToastStyle = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        //存储已经选择的样式
        mToast_style = SharedPreferencesUtils.getInt(this, ConstantValue.TOAST_STYLE, 0);
        mSetting_click.SettingDes(mToastStyle[mToast_style]);
        mSetting_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToast_style = SharedPreferencesUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
                ShowToastDialog();
            }
        });

    }

    private void ShowToastDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle("请选择归属的样式");
        builder.setSingleChoiceItems(mToastStyle, mToast_style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.i(TAG, "onClick: " + mToast_style);
                mSetting_click.SettingDes(mToastStyle[which]);
                dialog.dismiss();
                SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
            }

        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //显示对话框
        builder.show();
    }

    private void initAddress() {
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
        //对服务的运行做动态的显示
        boolean isRunning = ServiceUtils.isRunning(this, "com.example.mobilesafe.Service.AddressService");
        siv_address.setCheck(isRunning);

        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_address.isCheck();
                siv_address.setCheck(!check);
                if (!check) {

                    startService(new Intent(getApplicationContext(), AddressService.class));

                } else {

                    stopService(new Intent(getApplicationContext(), AddressService.class));

                }
            }
        });
    }

    private void initUpData() {
        final SettingItemView siv_updata = (SettingItemView) findViewById(R.id.siv_updata);

        boolean open = SharedPreferencesUtils.getBoolean(this, ConstantValue.OPEN, false);
        siv_updata.setCheck(open);
        siv_updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取条目的当前的状态
                //点击之前选中改为未选中  未选中改为选中
                boolean check = siv_updata.isCheck();
                siv_updata.setCheck(!check);
                //保存当前的状态
                SharedPreferencesUtils.putBoolean(getApplicationContext(), ConstantValue.OPEN, !check);

            }
        });
    }
}
