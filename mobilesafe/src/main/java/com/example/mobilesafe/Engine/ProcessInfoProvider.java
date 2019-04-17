package com.example.mobilesafe.Engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.util.Log;

import com.example.mobilesafe.DB.Domain.ProcessInfo;
import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/25.
 */

public class ProcessInfoProvider {
    //获取进程总数
    public static int getProcessCount(Context ctx){
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程合集
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //返回


        //return runningActivity;
        return runningAppProcesses.size();
    }

    public static long getAvailSpace(Context ctx){
        //获取剩余内存
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //构建可用的内存对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //给可用内存对象赋值
        am.getMemoryInfo(memoryInfo);
        //返回其中的大小
        return memoryInfo.availMem;

    }


    public static long getTotalSpace(Context ctx){
        //获取内存总数
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //构建可用的内存对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //给可用内存对象赋值
        am.getMemoryInfo(memoryInfo);
        //返回其中的大小
        return memoryInfo.totalMem;

    }

    public static List<ProcessInfo> getProcess(Context ctx){
        List<ProcessInfo> processInfosList = new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的进程合集
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info: runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //获取应用包名
            processInfo.packageName = info.processName;
            //获取占用内存的大小  pid 唯一进程标识
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //返回数组中索引位置为0 的对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //获取已使用的大小
            processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                //获取应用名称 图片
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                processInfo.icon = applicationInfo.loadIcon(pm);
                //判断是否为系统进程
                if ((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                    processInfo.isSystem = true;

                }else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //找不到应用名称的异常情况 要处理
                processInfo.name = info.processName;
                processInfo.icon = ctx.getResources().getDrawable(R.drawable.icon);
                processInfo.isSystem= true;

                e.printStackTrace();
            }
            processInfosList.add(processInfo);
        }
        return processInfosList;
    }
}
