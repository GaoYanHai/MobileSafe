package com.example.mobilesafe.Service;


import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mobilesafe.DB.Domain.ProcessInfo;
import com.example.mobilesafe.Engine.ProcessInfoProvider;
import com.example.mobilesafe.R;
import com.example.mobilesafe.Receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by GaoYanHai on 2018/5/27.
 */
public class WidgetService extends Service{

    private Timer mTimer;
    private InnerReceiver innerReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startTimer();
        //注册广播接收者
        IntentFilter intentFilter = new IntentFilter();
        //关屏广播
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        //开屏幕广播
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);

        innerReceiver = new InnerReceiver();

        registerReceiver(innerReceiver,intentFilter);

    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //定时更新UI
                updataAppWidget();
//                Log.i("startTimer", "run: ");
            }
        }, 0, 5000);
    }

    class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                startTimer();
            }else {
                if (mTimer!=null){
                    mTimer.cancel();
                    mTimer = null;
                }
            }
        }
    }

    private void updataAppWidget() {
        //获取对象
        AppWidgetManager instance = AppWidgetManager.getInstance(this);
        //获取窗口小部件生成的view对象
        RemoteViews aWm = new RemoteViews(getPackageName(), R.layout.appwidget);
        aWm.setTextViewText(R.id.tv_process_count,"进程总数："+ ProcessInfoProvider.getProcessCount(this));
        String s = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        aWm.setTextViewText(R.id.tv_process_memory,"剩余内存：："+ s);

        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");

        PendingIntent getActivity = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        aWm.setOnClickPendingIntent(R.id.ll_root,getActivity);
        ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
        instance.updateAppWidget(componentName,aWm);

    }

    @Override
    public void onDestroy() {
        if (innerReceiver!=null){
            unregisterReceiver(innerReceiver);
        }
        //停止刷新任务
        if (mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
        super.onDestroy();
    }
}
