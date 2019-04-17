package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.mobilesafe.DB.Domain.AppInfo;
import com.example.mobilesafe.Engine.AppInfoProvider;
import com.example.mobilesafe.Utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GaoYanHai on 2018/5/24.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {

    private ListView lv_app_list;
    private List<AppInfo> mAppInfoList;
    private ArrayList<AppInfo> mSystemList;
    private ArrayList<AppInfo> mCustomerList;
    private TextView tv_des;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MyAdapter myAdapter = new MyAdapter();
            lv_app_list.setAdapter(myAdapter);
            if (tv_des != null && mCustomerList != null) {
                tv_des.setText("用户应用（" + mCustomerList.size() + "）");
            }
        }
    };
    private AppInfo mAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        initTitle();

        initList();


    }

    private void initList() {
        tv_des = (TextView) findViewById(R.id.tv_des);
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mSystemList = new ArrayList<>();
                mCustomerList = new ArrayList<>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem) {
                        //系统应用
                        mSystemList.add(appInfo);
                    } else {
                        //用户应用
                        mCustomerList.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }).start();

        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //滚动条目调用的方法   firstVisibleItem第一个可见条目索引值   visibleItemCount   当前一个屏幕可见条目数
                if (mCustomerList != null && mSystemList != null) {
                    if (firstVisibleItem >= mCustomerList.size() + 1) {
                        tv_des.setText("系统应用（" + mSystemList.size() + "）");
                    } else {
                        tv_des.setText("用户应用（" + mCustomerList.size() + "）");
                    }
                }


            }
        });

        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerList.size() + 1) {
                    return;
                } else {

                    if (position < mCustomerList.size() + 1) {
                        mAppInfo = mCustomerList.get(position - 1);
                    } else {
                        mAppInfo = mSystemList.get(position - mCustomerList.size() - 2);
                    }
                    showPopuWindow(view);
                }
            }
        });
    }

    private void showPopuWindow(View view) {


        View popuview = View.inflate(this, R.layout.popuwindow_layout, null);
        TextView tv_uninstall = (TextView) popuview.findViewById(R.id.tv_uninstall);
        TextView tv_share = (TextView) popuview.findViewById(R.id.tv_share);
        TextView tv_start = (TextView) popuview.findViewById(R.id.tv_start);

        tv_share.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_uninstall.setOnClickListener(this);
        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        //保持原有位置
        alphaAnimation.setFillAfter(true);
        //缩放动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);

        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);


        PopupWindow popupWindow = new PopupWindow(popuview, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置背景颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        //指定窗体的位置
        popupWindow.showAsDropDown(view, 150, -view.getHeight());

        popuview.startAnimation(animationSet);

    }

    private void initTitle() {
        String path = Environment.getDataDirectory().getAbsolutePath();
        String sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //获取内存和sd的可用空间
        String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
        String sdmemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdpath));

        TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
        tv_memory.setText("手机内存可用：" + memoryAvailSpace);
        tv_sd_memory.setText("SD卡可用：" + sdmemoryAvailSpace);
    }

    //int  是 2g  long 是16g

    private long getAvailSpace(String path) {
        //获取可用磁盘大小的类
        StatFs statFs = new StatFs(path);
        //可用区块的个数
        long count = statFs.getAvailableBlocks();
        //区块的大小
        long size = statFs.getBlockSize();
        //可用区块个数 * 区块大小 = 可用空间
        return count * size;

    }

    //实现点击事件的方法
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_uninstall:
                if (mAppInfo.isSystem){
                    ToastUtils.show(this,"系统应用无法卸载");
                }else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package"+mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_share:
                //真正的分享是到第三方平台
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用"+mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case R.id.tv_start:
                //通过桌面启动指定包名的应用
                PackageManager pm = getPackageManager();
                Intent launchIntentForPackage = pm.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage!=null){
                    startActivity(launchIntentForPackage);
                }else {
                    ToastUtils.show(this,"应用开启失败");
                }
                break;
        }

    }


    private class MyAdapter extends BaseAdapter {

        //获取数据适配器中条目类型的总数  修改成两种
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                //纯文本条目
                return 0;
            } else {
                //图片+文本条目
                return 1;
            }
        }

        @Override
        public int getCount() {
            return mSystemList.size() + mCustomerList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mCustomerList.size() + 1) {
                return null;
            } else {

                if (position < mCustomerList.size() + 1) {
                    return mCustomerList.get(position - 1);
                } else {
                    return mSystemList.get(position - mCustomerList.size() - 2);
                }
            }


        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int type = getItemViewType(position);
            if (type == 0) {

                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.app_list_item_title, null);
                    holder = new ViewTitleHolder();

                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewTitleHolder) convertView.getTag();
                }
                if (position == 0) {
                    holder.tv_title.setText("用户应用（" + mCustomerList.size() + "）");
                } else {
                    holder.tv_title.setText("系统应用（" + mSystemList.size() + "）");
                }

                return convertView;

            } else {
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.app_list_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                    holder.tv_path = (TextView) convertView.findViewById(R.id.tv_path);
                    convertView.setTag(holder);

                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.iv_icon.setBackground(getItem(position).icon);
                holder.tv_name.setText(getItem(position).name);
                if (getItem(position).isSdCard) {
                    holder.tv_path.setText("SD卡应用");
                } else {
                    holder.tv_path.setText("手机内存应用");
                }
                return convertView;
            }
        }


    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    static class ViewTitleHolder {

        TextView tv_title;
    }
}
