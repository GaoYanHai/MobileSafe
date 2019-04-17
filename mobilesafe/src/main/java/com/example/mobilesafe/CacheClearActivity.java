package com.example.mobilesafe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/6/10.
 */
public class CacheClearActivity extends Activity{

    private static final int UPDATA_CACHE_APP = 100;
    private static final int CHECK_CACHE_APP = 101;
    private static final int CHECK_CACHE_APP_FINISH = 102;
    private static final int CLEAR_CACHE = 103;
    private Button bt_clear;
    private ProgressBar pb_bar;
    private TextView tv_name1;
    private LinearLayout ll_add_text;
    private PackageManager mPm;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATA_CACHE_APP:
                    View view = View.inflate(getApplicationContext(), R.layout.cache_list_item, null);
                    ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_memory = (TextView) view.findViewById(R.id.tv_memory);
                    ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                    final CacheInfo cacheInfo = (CacheInfo) msg.obj;
                    iv_icon.setBackground(cacheInfo.icon);
                    tv_name.setText(cacheInfo.name);

                    tv_memory.setText(Formatter.formatFileSize(getApplicationContext(),cacheInfo.cacheSize));

                    ll_add_text.addView(view,0);

                    iv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent("android.setting.APPLICATION_DATEILS_SETTINGS");
                            intent.setData(Uri.parse("package:"+cacheInfo.packagename));
                            startActivity(intent);
                        }
                    });

                    break;
                case CHECK_CACHE_APP:
                    tv_name1.setText((String) msg.obj);
                    break;

                case CHECK_CACHE_APP_FINISH:
                    tv_name1.setText("扫描完成");
                    break;

                case CLEAR_CACHE:
                    ll_add_text.removeAllViews();
                    break;
            }

        }
    };
    private int mIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);
        initUI();
        initData();

    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPm = getPackageManager();
                List<PackageInfo> installedPackages = mPm.getInstalledPackages(0);
                pb_bar.setMax(installedPackages.size());
                for (PackageInfo packageInfo:installedPackages){
                    String packageName = packageInfo.packageName;
                    getPackageCache(packageName);

                    mIndex++;
                    pb_bar.setProgress(mIndex);
                    Message msg = Message.obtain();
                    msg.what = CHECK_CACHE_APP;
                    try {
                        msg.obj = mPm.getApplicationInfo(packageName,0).loadLabel(mPm).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(msg);

                }
                Message msg = Message.obtain();
                msg.what = CHECK_CACHE_APP_FINISH;
                mHandler.sendMessage(msg);

            }
        }).start();

    }
    class CacheInfo{
        public String name;
        public Drawable icon;
        public String packagename;
        public long cacheSize;
    }

    private void getPackageCache(String packageName) {
       IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub(){

           @Override
           public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
              long cacheSize = pStats.cacheSize;
               if (cacheSize>0){
                   Message msg = Message.obtain();
                   msg.what = UPDATA_CACHE_APP;
                   CacheInfo cacheInfo = new CacheInfo();
                   cacheInfo.cacheSize = cacheSize;
                   cacheInfo.packagename = pStats.packageName;
                   try {
                       cacheInfo.name = mPm.getApplicationInfo(pStats.packageName,0).loadLabel(mPm).toString();
                       cacheInfo.icon = mPm.getApplicationInfo(pStats.packageName,0).loadIcon(mPm);
                   } catch (PackageManager.NameNotFoundException e) {
                       e.printStackTrace();
                   }
                   msg.obj = cacheInfo;
                   mHandler.sendMessage(msg);
               }
              // String str = Formatter.formatFileSize(getApplicationContext(), pStats.cacheSize);
           }
       };

        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageManager");
            Method getPackageCache = clazz.getMethod("getPackageSizeInfo",String.class,IPackageStatsObserver.class);
            getPackageCache.invoke(mPm,packageName,mStatsObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        bt_clear = (Button) findViewById(R.id.bt_clear);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_name1 = (TextView) findViewById(R.id.tv_name);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_root);

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Class<?> clazz = Class.forName("android.content.pm.PackageManager");
                    Method getPackageCache = clazz.getMethod("freeStorageAndNotify",long.class,IPackageDataObserver.class);
                    getPackageCache.invoke(mPm,Long.MAX_VALUE, new IPackageDataObserver.Stub() {
                        @Override
                        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

                            Message msg = Message.obtain();
                            msg.what = CLEAR_CACHE;
                            mHandler.sendMessage(msg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
