package com.example.mobilesafe;

import android.Manifest;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;
import com.example.mobilesafe.Utils.StreamUtils;
import com.example.mobilesafe.Utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.factory.BitmapFactory;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends AppCompatActivity {

    private TextView tv_version_name;
    private int mLocalVersionCode;
    private static final String TAG = "SplashActivity";

    private static final int UPDATA_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URLERROR = 102;
    private static final int IOERROR = 103;
    private static final int JSONERROR = 104;

    private String mVersionDes;
    private String mVersionUrl;

    private RelativeLayout rl_root;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATA_VERSION:
                    upDataDialog();
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case URLERROR:
                    ToastUtils.show(SplashActivity.this, "URLERROR");
                    break;
                case IOERROR:
                    ToastUtils.show(SplashActivity.this, "IOERROR");
                    enterHome();
                    break;
                case JSONERROR:
                    ToastUtils.show(SplashActivity.this, "JSONERROR");
                    break;
            }
        }
    };


    //升级提醒的对话框
    private void upDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("升级提醒");
        builder.setIcon(R.drawable.warning);
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadAPK();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        //点击返回键的操作
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });

        builder.show();
    }

    private void downloadAPK() {
        //判断sdk的状态  是否存在   获取路径
        //动态获取权限问题  工具类
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final String s = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MobileSafe.apk";
            Log.i(TAG, "s: " + s);

            //权限已经获取 直接下一步
            //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行
            if (ContextCompat.checkSelfPermission(SplashActivity.this, "Manifest.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
                //注意第二个参数没有双引号
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }

            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mVersionUrl, s, new RequestCallBack<File>() {
                //成功
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    File file = responseInfo.result;
                    //提示用户安装
                    installApk(file);
                    Log.i(TAG, "onSuccess: ");
                }

                //失败
                public void onFailure(HttpException error, String msg) {
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();

                    Log.i(TAG, "onFailure: msg:" + msg);
                }

                //开始
                public void onStart() {
                    Log.i(TAG, "onStart: ");
                }

                //正在现在
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.i(TAG, "onLoading: ");
                    Log.i(TAG, "total: " + total);
                    Log.i(TAG, "current: " + current);

                }
            });


        }
    }

    //开始安装
    private void installApk(File file) {

        Intent intent = new Intent("android.intent.action.View");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.package-archive");
        //startActivity(intent);
        startActivityForResult(intent, 0);


    }

    //处理安装过程中点击取消的处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }


    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //设置UI界面
        initUI();
        //获取版本名称并设置
        initData();
        //动画效果
        initAnimation();
        //初始化数据库
        initDB();

        //生成快捷方式
        if (SharedPreferencesUtils.getBoolean(this, ConstantValue.SHORTCUT, true)){
        initShortCut();
        }


    }

    private void initShortCut() {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, android.graphics.BitmapFactory.decodeResource(getResources(),R.drawable.icon));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"安全卫士");
        Intent shortcut = new Intent("android.intent.action.HOME");
        shortcut.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcut);
        sendBroadcast(intent);
        SharedPreferencesUtils.putBoolean(this,ConstantValue.SHORTCUT,false);
    }

    private void initDB() {
        //电话号码地址
        initAddressDB("address.db");
        //常用号码查询
        initAddressDB("commonnum.db");
        //病毒库
        initAddressDB("antivirus.db");
    }

    private void initAddressDB(String dbName) {
        //在file 创建同名的数据库文件
        File file = getFilesDir();
        File file1 = new File(file, dbName);
        //如果存在就返回 不继续执行下边的代码
        if (file1.exists()) {
            return;
        }
        InputStream inputStream=null;
        FileOutputStream fileOutputStream  =null;
        try {
            inputStream = getAssets().open(dbName);
            fileOutputStream = new FileOutputStream(file1);
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = inputStream.read(bs)) != -1) {
                fileOutputStream.write(bs, 0, temp);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (inputStream!=null&&fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //加入动画效果
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        rl_root.startAnimation(alphaAnimation);
    }

    private void initData() {
        // 1
        tv_version_name.setText("版本名称" + getVersionName());
        // 2 检查更新
        mLocalVersionCode = getVersionCode();
        //检查更新是否开启
        if (SharedPreferencesUtils.getBoolean(this, ConstantValue.OPEN, false)) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
        }
    }


    private void checkVersion() {
        new Thread() {
            public void run() {
                long startTime = System.currentTimeMillis();

                Message message = Message.obtain();

                String ip = "http://10.134.104.93:8080/updata.json";
                try {
                    URL url = new URL(ip);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(2000);

                    if (urlConnection.getResponseCode() == 200) {
                        InputStream is = urlConnection.getInputStream();

                        String s = StreamUtils.StreamToString(is);

                        JSONObject jsonObject = new JSONObject(s);
                        String versionName = jsonObject.getString("VersionName");
                        String versionCode = jsonObject.getString("VersionCode");
                        mVersionDes = jsonObject.getString("VersionDes");
                        mVersionUrl = jsonObject.getString("VersionUrl");

                        Log.i(TAG, "run: s:" + versionName);
                        Log.i(TAG, "run: s:" + versionCode);
                        Log.i(TAG, "run: s:" + mVersionDes);
                        Log.i(TAG, "run: s:" + mVersionUrl);

                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            message.what = UPDATA_VERSION;
                        } else {
                            message.what = ENTER_HOME;
                        }

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    message.what = URLERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = IOERROR;
                } catch (JSONException e) {
                    message.what = JSONERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 3000) {
                        try {
                            Thread.sleep(3000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }

    public String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(this.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
