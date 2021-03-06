package com.example.mobilesafe;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.example.mobilesafe.Receiver.SmsReceiver;


/**
 * Created by GaoYanHai on 2018/5/4.
 */
//一键清除数据
public class WipeDataActivity extends Activity {

    private static final String TAG ="WipeDataActivity" ;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(this, SmsReceiver.class);

        if (mDPM.isAdminActive(mDeviceAdminSample)){
            mDPM.wipeData(0);
        }else {
            Log.i(TAG, "onCreate: 没有激活设备管理器");
        }
    }
}
