package com.example.mobilesafe.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.mobilesafe.DB.BlackNumberDao;
import com.example.mobilesafe.R;
import com.example.mobilesafe.Utils.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by GaoYanHai on 2018/5/13.
 */

public class BlackNumberService extends Service {
    private static final String TAG = "BlackNumberService";
    private InnerSmsReceiver innerSmsReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener myPhoneStateListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provide.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);

        innerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(innerSmsReceiver,intentFilter);
        mDao = BlackNumberDao.getInstance(this);
        //对电话状态的监听
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

    }
    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        //重写电话状态的方法
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态  正在拨打电话或正在通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态
                    //挂断电话
                    endCall(incomingNumber);

                    break;

            }
            super.onCallStateChanged(state, incomingNumber);

        }
    }

    private void endCall(String phone) {
        int mode = mDao.getMode(phone);

        if (mode==2&&mode==3){
            //由于一些类对android开发者隐藏不能直接去调用  所以要反射调用
            //获取ServiceManager的字节码文件
            try {
                Class<?> aClass = Class.forName("android.os.ServiceManager");
                Method method = aClass.getMethod("getSystemService", String.class);
                IBinder invoke = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
//                ITelephony iTelephony  =  ITelephony.Stub.asInterface(invoke);
//                iTelephony.endCall();
                ToastUtils.show(getApplicationContext(),"电话拦截");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信的内容 电话号码  如果在黑名单中  拦截模式

            //获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //循环遍历短信的内容
            for (Object object : objects) {
                //获取短信对象
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                //获取短信对象的基本信息
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                //包含号码

                int mode = mDao.getMode(originatingAddress);
                if (mode==1&&mode==3){
                    //中断广播
                    abortBroadcast();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        //反注册  销毁动态的注册
        if (innerSmsReceiver!=null){
            unregisterReceiver(innerSmsReceiver);
        }
        super.onDestroy();
    }
}
