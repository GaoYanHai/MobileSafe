package com.example.mobilesafe;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilesafe.Engine.VirusDao;
import com.example.mobilesafe.Utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by GaoYanHai on 2018/5/30.
 */
public class AnitVirusActivity extends Activity{

    private static final int SCANING = 100;
    private static final int SCANING_FINSH = 101;
    private ImageView iv_scanning;
    private TextView tv_name;
    private ProgressBar pb_bar;
    private LinearLayout ll_add_text;
    int index=0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCANING:
                    ScanInfo info = (ScanInfo) msg.obj;
                    tv_name.setText(info.name);
                    TextView textView = new TextView(getApplicationContext());
                    if (info.isVirus){
                        textView.setTextColor(Color.RED);
                        textView.setText("发现病毒: "+info.name);
                    }else {
                        textView.setTextColor(Color.BLACK);
                        textView.setText("扫描安全: "+info.name);
                    }
                    ll_add_text.addView(textView,0);

                    break;
                case SCANING_FINSH:
                    tv_name.setText("扫描完成");
                    iv_scanning.clearAnimation();
                    //弹出有病毒的应用的 卸载框
                    unInstallVirus();
                    break;
            }
        }
    };
    private List<ScanInfo> mVirusScanInfoList;

    private void unInstallVirus() {
        for (ScanInfo scanInfo:mVirusScanInfoList){
            String packageName = scanInfo.packageName;
            Intent intent = new Intent("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package"+packageName));
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anitvirus);
        initUI();
        initAnimation();
        checkVirus();
    }

    private void checkVirus() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> virusList = VirusDao.getVirusList();
                //获取手机应用签名的md5码 与病毒库的md5码做对比
                PackageManager pm = getPackageManager();
                //获取安装应用的签名和卸载残留的文件
                List<PackageInfo> packageinfoList = pm.getInstalledPackages(PackageManager.GET_SIGNATURES + PackageManager.GET_UNINSTALLED_PACKAGES);
                //病毒的集合
                mVirusScanInfoList = new ArrayList<>();
                //所有应用的集合
                List<ScanInfo> ScanInfoList = new ArrayList<>();
                pb_bar.setMax(packageinfoList.size());
                //遍历集合
                for(PackageInfo packageInfo:packageinfoList){
                    ScanInfo scanInfo = new ScanInfo();
                    Signature[] signatures = packageInfo.signatures;
                    Signature signature = signatures[0];
                    String string = signature.toCharsString();
                    String encoder = MD5Utils.encoder(string);
                    //对比病毒库
                    if (virusList.contains(encoder)){
                        //记录病毒
                        scanInfo.isVirus = true;
                        mVirusScanInfoList.add(scanInfo);
                    }else {
                        scanInfo.isVirus = false;
                    }
                    scanInfo.packageName = packageInfo.packageName;
                    scanInfo.name = packageInfo.applicationInfo.loadLabel(pm).toString();
                    ScanInfoList.add(scanInfo);

                    index++;
                    pb_bar.setProgress(index);
                    try {
                        //随机睡眠时间50 到199
                        Thread.sleep(50+new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.what = SCANING;
                    msg.obj = scanInfo;
                    mHandler.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = SCANING_FINSH;
                mHandler.sendMessage(msg);

            }
        }).start();

    }
    class ScanInfo{
        public Boolean isVirus;
        public String packageName;
        public String name;
    }

    private void initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1500);
        //一直转
//        rotateAnimation.setRepeatMode(Animation.INFINITE);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        //保持动画结束后的状态
        rotateAnimation.setFillAfter(true);

        iv_scanning.setAnimation(rotateAnimation);
    }

    private void initUI() {
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
        tv_name = (TextView) findViewById(R.id.tv_name);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);

    }
}
