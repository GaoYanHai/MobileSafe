package com.example.mobilesafe.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.mobilesafe.DB.AppLockDao;
import com.example.mobilesafe.EnterPasdActivity;

import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/30.
 */
public class WatchDogService extends Service {

    private AppLockDao mDao;
    boolean isWatch = true;
    private String mSkippackageName;
    private MyContentObserver myContentObserver;
    private List<String> mPacknamelist;
    private InnerReceiver mInnerReceiver;

    @Override
    public void onCreate() {
        mDao = AppLockDao.getInstance(this);
        WatchDog();
        //广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver, intentFilter);
        myContentObserver = new MyContentObserver(new Handler());
        //注册内容观察者 一旦数据库发生添加  或删除操作 重新获取数据
        getContentResolver().registerContentObserver(Uri.parse("content//applock/change"), true, myContentObserver);
        super.onCreate();
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mSkippackageName = intent.getStringExtra("packageName");
        }
    }

    private void WatchDog() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPacknamelist = mDao.findAll();
                while (isWatch) {

                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //获取正在运行的任务栈   只获取最后开启的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    //获取任务栈的第一个任务  即 最后运行的程序
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //获取栈顶的包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    if (mPacknamelist.contains(packageName)) {
                        if (!packageName.equals(mSkippackageName)) {
                            //弹出拦截界面
                            Intent intent = new Intent(getApplicationContext(), EnterPasdActivity.class);
                            intent.putExtra("packageName", packageName);
                            //在服务里开启活动  要申请任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isWatch = false;
        if (mInnerReceiver!=null){
            unregisterReceiver(mInnerReceiver);
        }
        getContentResolver().unregisterContentObserver(myContentObserver);
        super.onDestroy();
    }

    class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        //数据库发生变化时候调用的方法   重新获取包名
        @Override
        public void onChange(boolean selfChange) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //重新获取包名
                    mPacknamelist = mDao.findAll();
                }
            }).start();
            super.onChange(selfChange);
        }
    }
}
