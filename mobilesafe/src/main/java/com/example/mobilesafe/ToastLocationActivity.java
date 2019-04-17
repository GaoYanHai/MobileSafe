package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;

/**
 * Created by GaoYanHai on 2018/5/7.
 */

//土司位置的活动
public class ToastLocationActivity extends Activity {

    private ImageView iv_drag;
    private Button bt_top;
    private Button bt_bottom;
    private int mWidth;
    private int mHeight;

    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);
        initUI();
    }

    private void initUI() {
        iv_drag = (ImageView) findViewById(R.id.iv_drag);
        bt_top = (Button) findViewById(R.id.bt_top);
        bt_bottom = (Button) findViewById(R.id.bt_bottom);
        //获取屏幕的宽高
        WindowManager mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWidth = mWM.getDefaultDisplay().getWidth();
        mHeight = mWM.getDefaultDisplay().getHeight();

        int locationX = SharedPreferencesUtils.getInt(this, ConstantValue.LOCATION_TOUCH_X, 0);
        int locationY = SharedPreferencesUtils.getInt(this, ConstantValue.LOCATION_TOUCH_Y, 0);

        //设置图片的初始位置  回显
        //制定宽高 WRAP_CONTENT
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //将上次的坐标设置给图片
        params.leftMargin = locationX;
        params.topMargin = locationY;
        //将规则设置给图片
        iv_drag.setLayoutParams(params);
        if (locationY > mHeight / 2) {
            bt_top.setVisibility(View.VISIBLE);
            bt_bottom.setVisibility(View.INVISIBLE);
        } else {
            bt_top.setVisibility(View.INVISIBLE);
            bt_bottom.setVisibility(View.VISIBLE);
        }

        //双击事件的处理
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);

                mHits[mHits.length - 1] = SystemClock.uptimeMillis();

                if (mHits[mHits.length - 1] - mHits[0] < 500) {
                    int left = mWidth / 2 - iv_drag.getWidth() / 2;
                    int top = mHeight / 2 - iv_drag.getHeight() / 2;
                    int right = mWidth / 2 + iv_drag.getWidth() / 2;
                    int bottom = mHeight / 2 + iv_drag.getHeight() / 2;
                    iv_drag.layout(left, top, right, bottom);
                    SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_TOUCH_X, iv_drag.getLeft());
                    SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_TOUCH_Y, iv_drag.getTop());
                }
            }
        });


        iv_drag.setOnTouchListener(new View.OnTouchListener() {

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
                        //每条边距原位置移动了的位置
                        int Left = iv_drag.getLeft() + disX;
                        int Top = iv_drag.getTop() + disY;
                        int Right = iv_drag.getRight() + disX;
                        int Bottom = iv_drag.getBottom() + disY;

                        //容错处理 不能超过屏幕的宽高
                        if (Left < 0) {
                            return true;
                        }
                        if (Top < 0) {
                            return true;
                        }
                        if (Right > mWidth) {
                            return true;
                        }
                        //减掉通知栏的高度  没有API
                        if (Bottom > mHeight - 22) {
                            return true;
                        }
                        //文字的隐藏
                        if (Top > mHeight / 2) {
                            bt_top.setVisibility(View.VISIBLE);
                            bt_bottom.setVisibility(View.INVISIBLE);
                        } else {
                            bt_top.setVisibility(View.INVISIBLE);
                            bt_bottom.setVisibility(View.VISIBLE);
                        }


                        //将新的位置设置给图片
                        iv_drag.layout(Left, Top, Right, Bottom);
                        //再一次获取位置  重复
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_TOUCH_X, iv_drag.getLeft());
                        SharedPreferencesUtils.putInt(getApplicationContext(), ConstantValue.LOCATION_TOUCH_Y, iv_drag.getTop());
                        break;
                }
                //在只存在触摸事件的时候 返回 false 意为不响应移动事件   如果既要响应点击事件又要响应触摸事件则要将返回值改为false
                return false;
            }
        });
    }
}
