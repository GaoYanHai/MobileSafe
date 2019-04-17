package com.example.mobilesafe.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.Engine.AddressDao;
import com.example.mobilesafe.R;
import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;

/**
 * Created by GaoYanHai on 2018/5/5.
 */

public class AddressService extends Service {

    private static final String TAG = "AddressService";
    private TelephonyManager mTM;
    private MyPhoneStateListener myPhoneStateListener;
    private WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWM;
    private View mViewToast;
    private String mAddress;
    private int mWidth;
    private int mHeight;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_toast.setText(mAddress);
        }
    };
    private TextView tv_toast;


    @Nullable

    @Override
    public void onCreate() {
        //对电话状态的监听
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        myPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        super.onCreate();


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        //重写电话状态的方法
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    Log.i(TAG, "onCallStateChanged: 空闲状态");
                    //防止空指针   再创建以后移除
                    if (mWM!=null&&mViewToast!=null){
                        mWM.removeView(mViewToast);
                    }

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态  正在拨打电话或正在通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态
                    Log.i(TAG, "onCallStateChanged: 响铃状态");
                    ShowToast(incomingNumber);
                    break;

            }
            super.onCallStateChanged(state, incomingNumber);

        }
    }

    private void ShowToast(String incomingNumber) {


        //获取屏幕的宽高
        mWidth = mWM.getDefaultDisplay().getWidth();
        mHeight = mWM.getDefaultDisplay().getHeight();

        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
//        |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        params.format = PixelFormat.TRANSLUCENT;
        // params.windowAnimations= com.example.mobilesafe.R.style.Animation_Toast   ;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        //指定默认的土司位置  中间加上部
        params.gravity = Gravity.FILL_HORIZONTAL + Gravity.CENTER_HORIZONTAL;
        //读取已经存储的吐司位置的设置  左上角
        params.x = SharedPreferencesUtils.getInt(this, ConstantValue.LOCATION_TOUCH_X, 0);
        params.y = SharedPreferencesUtils.getInt(this, ConstantValue.LOCATION_TOUCH_Y, 0);
        //土司布局 转换成view
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        //找到控件
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);
        //在窗体上挂载布局   需要权限
        mWM.addView(mViewToast, (ViewGroup.LayoutParams) mParams);
        //显示归属地
        query(incomingNumber);

        int[] DrawableId = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green,};
        int toastStyleindex = SharedPreferencesUtils.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(DrawableId[toastStyleindex]);

        //悬浮窗口可以被拖拽 在来电的时候
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            private int startY;
            private int startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        //移动的位置
                        int disX = moveX - startX;
                        int disY = moveY - startY;
                        //移动后的位置
                        params.x=params.x+disX;
                        params.y=params.y+disY;
                        //容错处理
//                        if (params.x>mWidth/2-mViewToast.getWidth()){
//                            params.x=mWidth/2;
//                        }
//                        if (params.y<mHeight/2-mViewToast.getHeight()-44){
//                            params.y=mHeight/2;
//                        }
                        if (params.x>mWidth-mViewToast.getWidth()){
                            params.x=mWidth-mViewToast.getWidth();
                        }
                        if (params.y>mHeight-mViewToast.getHeight()-44){
                            params.y=mHeight-mViewToast.getHeight();
                        }

                        //设置给控件新的位置
                          mWM.updateViewLayout(mViewToast,params);
                        //再一次获取位置  重复
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_TOUCH_X, params.x);
                        SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_TOUCH_Y, params.y);
                        break;
                }

                return true;
            }
        });


    }

    private void query(final String incomingNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        //服务销毁的时候取消对电话的监听状态
        if (mTM != null && myPhoneStateListener != null) {
            mTM.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        super.onDestroy();
    }
}
