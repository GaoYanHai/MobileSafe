package com.example.mobilesafe.Global;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by GaoYanHai on 2018/6/11.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
                Log.i("111", "uncaughtException: "+e);

                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "logcat.log";
                File file = new File(path);

                try {
                    PrintWriter printWriter = new PrintWriter(file);
                    e.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //上传至服务器中

                //结束应用
                System.exit(0);
            }
        });
    }
}
