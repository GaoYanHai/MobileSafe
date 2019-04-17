package com.example.mobilesafe;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by GaoYanHai on 2018/6/11.
 */
public class TrafficActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);

        initUI();

    }

    private void initUI() {

        //手机下载流量
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //手机总流量
        long mobileTxBytes = TrafficStats.getMobileTxBytes();
        //下载流量 数据加wifi
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        //总流量  上传加下载  数据加wifi
        long totalTxBytes = TrafficStats.getTotalTxBytes();

        TextView tv_traffic1 = (TextView) findViewById(R.id.tv_traffic1);
        TextView tv_traffic2 = (TextView) findViewById(R.id.tv_traffic2);
        TextView tv_traffic3 = (TextView) findViewById(R.id.tv_traffic3);
        TextView tv_traffic4 = (TextView) findViewById(R.id.tv_traffic4);

        tv_traffic1.setText("手机下载流量: "+mobileRxBytes);
        tv_traffic2.setText("手机总流量: "+mobileTxBytes);
        tv_traffic3.setText("下载流量 数据加wifi: "+totalRxBytes);
        tv_traffic4.setText("总流量 上传加下载 数据加wifi: "+totalTxBytes);

    }
}
