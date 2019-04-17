package com.example.mobilesafe.Receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.ToastUtils;

import static android.os.Looper.getMainLooper;

/**
 * Created by GaoYanHai on 2018/4/28.
 */

//注册广播接收者  接收广播消息
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        ToastUtils.show(context,"开机启动广播");
        Log.i(TAG, "onReceive: 开机启动广播");

        //一旦接收到开启手机的广播就比较sim卡的序列号 否则就发送报警短信
        String string = SharedPreferencesUtils.getString(context, ConstantValue.SIM_NUMBER, "");
        // 先获取   再   存储sim卡的序列号
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = manager.getSimSerialNumber();

        if (!string.equals(simSerialNumber)){

            //不一致发送短信  需要权限
            SmsManager smsManager = SmsManager.getDefault();
            String s = SharedPreferencesUtils.getString(context, ConstantValue.CONTACT_PHONE, "");
            smsManager.sendTextMessage(s,null,"sim chang!!!",null,null);
        }
    }
}
