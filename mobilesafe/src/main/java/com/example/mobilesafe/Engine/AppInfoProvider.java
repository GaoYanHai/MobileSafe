package com.example.mobilesafe.Engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.example.mobilesafe.DB.Domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/24.
 */

public class AppInfoProvider {
    //返回手机中的应用信息 内存 SD卡 名称 包名 图标 系统 用户
    public static List<AppInfo> getAppInfoList(Context ctx){
        //获取包的管理者对象
        PackageManager pm = ctx.getPackageManager();
        //获取手机应用的集合
        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        List<AppInfo> appInfolist = new ArrayList<>();
        //获取应用的信息
        for (PackageInfo packageInfo:packageInfoList) {
            AppInfo appInfo = new AppInfo();
            //包名
            appInfo.packageName = packageInfo.packageName;
            //名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.name = applicationInfo.loadLabel(pm).toString()+applicationInfo.uid;
            //图标
            appInfo.icon = applicationInfo.loadIcon(pm);
            //判断是系统应用还是手机应用
            if ((applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.isSystem = true;
            }else {
                //手机应用
                appInfo.isSystem = false;
            }
            //是否为sd卡中安装的应用
            if ((applicationInfo.flags& ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){

                appInfo.isSdCard = true;
            }else {

                appInfo.isSdCard = false;
            }
            appInfolist.add(appInfo);
        }
        return appInfolist;
    }
}
