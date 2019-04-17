package com.example.mobilesafe.Receiver;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.media.MediaPlayer;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.mobilesafe.LockScreenActivity;
import com.example.mobilesafe.R;
import com.example.mobilesafe.Service.LocationService;
import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.ToastUtils;
import com.example.mobilesafe.WipeDataActivity;

import static android.os.Looper.getMainLooper;

/**
 * Created by GaoYanHai on 2018/4/29.
 */
//对接受的短信进行监听
public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        ToastUtils.show(context, "接受短信广播接收到了");

        //判断防盗保护是否开启
        boolean open_security = SharedPreferencesUtils.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            //获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //循环遍历短信的内容
            for (Object object : objects) {
                //获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                //判断是否包含关键字
                if (messageBody.contains("#*alarm*#")) {
                    //播放音乐 先准备音乐
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    //循环播放
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();

                }
                if (messageBody.contains("#*location*#")) {
                    //开启获取位置的服务
                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);

                }
                if (messageBody.contains("#*wipedata*#")) {
                    Intent intent1 = new Intent(context, WipeDataActivity.class);
                    context.startActivity(intent);

                }
                if (messageBody.contains("#*lockscreen*#")) {
                    Intent intent1 = new Intent(context, LockScreenActivity.class);
                    context.startActivity(intent);

                }
            }
        }
    }
}
