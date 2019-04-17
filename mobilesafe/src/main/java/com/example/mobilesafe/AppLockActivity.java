package com.example.mobilesafe;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.DB.AppLockDao;
import com.example.mobilesafe.DB.Domain.AppInfo;
import com.example.mobilesafe.Engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/28.
 */
public class AppLockActivity extends Activity {

    private Button bt_lock;
    private Button bt_unlock;
    private TextView tv_lock;
    private TextView tv_unlock;
    private ListView lv_unlock;
    private ListView lv_lock;
    private ArrayList<AppInfo> mLockList;
    private ArrayList<AppInfo> munLockList;
    private AppLockDao mDao;
    private LinearLayout ll_lock;
    private LinearLayout ll_unlock;
    private MyAdapter mlockAdapter;
    private MyAdapter munlockAdapter;
    private TranslateAnimation mTranslateAnimation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            munlockAdapter = new MyAdapter(false);
            lv_unlock.setAdapter(munlockAdapter);

            mlockAdapter = new MyAdapter(true);
            lv_lock.setAdapter(mlockAdapter);



        }
    };

    class MyAdapter extends BaseAdapter {

        private final Boolean islock;

        //用于区分已加锁还是未加锁 已加锁 true 未加锁 false
        public MyAdapter(Boolean islock) {
            this.islock = islock;
        }

        @Override
        public int getCount() {
            if (islock) {
                tv_lock.setText("已加锁应用：" + mLockList.size());
                return mLockList.size();
            } else {
                tv_unlock.setText("未加锁应用：" + munLockList.size());
                return munLockList.size();
            }

        }

        @Override
        public AppInfo getItem(int position) {
            if (islock) {
                return mLockList.get(position);
            } else {
                return munLockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_islock_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo = getItem(position);
            //将converview付给临时变量规避编译错误 final 不能被赋值
            final View animationView = convertView;

            String name = appInfo.getName();
            Drawable icon = appInfo.getIcon();

            holder.tv_name.setText(name);
            holder.iv_icon.setBackground(icon);
            if (islock) {

                holder.iv_lock.setBackgroundResource(R.drawable.lock);
            } else {

                holder.iv_lock.setBackgroundResource(R.drawable.unlock);
            }
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加动画效果
                    animationView.startAnimation(mTranslateAnimation);
                    mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if (islock){
                                //已加锁   ----  未加锁
                                mLockList.remove(appInfo);
                                munLockList.add(appInfo);
                                mDao.delete(appInfo.getPackageName());
                                mlockAdapter.notifyDataSetChanged();
                            }else {
                                //未加锁  ---  已加锁
                                munLockList.remove(appInfo);
                                mLockList.add(appInfo);
                                mDao.insert(appInfo.getPackageName());
                                munlockAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            });

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        ImageView iv_lock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        initUI();

        initData();
        
        initAnimation();
    }

    private void initAnimation() {
        mTranslateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        mTranslateAnimation.setDuration(500);

    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppInfo> mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mLockList = new ArrayList<>();
                munLockList = new ArrayList<>();
                mDao = AppLockDao.getInstance(getApplicationContext());
                List<String> lockPackageList = mDao.findAll();
                for (AppInfo appInfo : mAppInfoList) {
                    if (lockPackageList.contains(appInfo.getPackageName())) {
                        //已加锁
                        mLockList.add(appInfo);
                    } else {
                        //未加锁
                        munLockList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initUI() {

        bt_lock = (Button) findViewById(R.id.bt_lock);
        bt_unlock = (Button) findViewById(R.id.bt_unlock);

        tv_lock = (TextView) findViewById(R.id.tv_lock);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);

        lv_unlock = (ListView) findViewById(R.id.lv_unlock);
        lv_lock = (ListView) findViewById(R.id.lv_lock);

        ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
        ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);

        bt_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_unlock.setVisibility(View.GONE);
                ll_lock.setVisibility(View.VISIBLE);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
                bt_lock.setBackgroundResource(R.drawable.tab_left_pressed);
            }
        });

        bt_unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_unlock.setVisibility(View.VISIBLE);
                ll_lock.setVisibility(View.GONE);
                bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                bt_lock.setBackgroundResource(R.drawable.tab_left_default);
            }
        });
    }
}
