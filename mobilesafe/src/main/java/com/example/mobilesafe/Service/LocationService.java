package com.example.mobilesafe.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ServiceCompat;
import android.telephony.SmsManager;

import com.example.mobilesafe.Utils.ConstantValue;
import com.example.mobilesafe.Utils.SharedPreferencesUtils;

/**
 * Created by GaoYanHai on 2018/4/30.
 */

public class LocationService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //只需要获取一次  放到create  反复获取放到start方法中
        //获取经纬度坐标
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //以最优的方式获取坐标
        Criteria criteria = new Criteria();
        //允许花费流量进行定位
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE); //获取坐标的精确度的程度
        String bestProvider = lm.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        lm.requestLocationUpdates(bestProvider, 0, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                //获取经度和纬度
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                //发送短信
                SmsManager smsManager = SmsManager.getDefault();
                String s = SharedPreferencesUtils.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
                smsManager.sendTextMessage(s,null,"longitude"+longitude+"，"+"latitude"+latitude,null,null);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
