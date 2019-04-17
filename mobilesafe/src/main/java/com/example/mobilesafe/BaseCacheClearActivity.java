package com.example.mobilesafe;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * Created by GaoYanHai on 2018/6/11.
 */
public class BaseCacheClearActivity extends TabActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_cache_clear);

        TabHost.TabSpec tabSpec1 = getTabHost().newTabSpec("clear_cache").setIndicator("缓存清理");
        TabHost.TabSpec tabSpec2 = getTabHost().newTabSpec("clear_cache_1").setIndicator("SD卡缓存清理");

        tabSpec1.setContent(new Intent(this,CacheClearActivity.class));
        tabSpec2.setContent(new Intent(this,SdCacheClearActivity.class));

        getTabHost().addTab(tabSpec1);
        getTabHost().addTab(tabSpec2);
    }
}
