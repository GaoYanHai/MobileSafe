package com.example.mobilesafe.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/6.
 */

public class ServiceUtils {
    //判断服务是否正在运行的工具类
    public static boolean isRunning(Context ctx, String serviceName){
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取服务的数量
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(100);
        //循环服务信息 找到自己的服务
        for (ActivityManager.RunningServiceInfo runningServiceinfo:runningServices) {
            //找到了返回true
            if (serviceName.equals(runningServiceinfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }
}
