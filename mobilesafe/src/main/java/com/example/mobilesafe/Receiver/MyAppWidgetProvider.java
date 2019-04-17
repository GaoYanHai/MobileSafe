package com.example.mobilesafe.Receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mobilesafe.Service.WidgetService;

/**
 * Created by GaoYanHai on 2018/5/27.
 */

public class MyAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        context.startService(new Intent(context,WidgetService.class));

    }

    //调用第一个小部件所用的方法 只有第一个小部件调用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context,WidgetService.class));
    }

    //创建另一个小部件调用的方法 第二个小部件直接调用这个方法
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context,WidgetService.class));
    }

    //宽高发生改变 第一个创建也要调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        context.startService(new Intent(context,WidgetService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

    }

    //最后一个小部件调用
    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context,WidgetService.class));
        super.onDisabled(context);

    }
}
