package com.example.mobilesafe.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;

/**
 * Created by GaoYanHai on 2018/4/21.
 */

public class SettingClickView extends RelativeLayout {



    private TextView tv_setdes;
    private TextView tv_settitle;


    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_click, this);

        tv_settitle = (TextView) findViewById(R.id.tv_settitle);
        tv_setdes = (TextView) findViewById(R.id.tv_setdes);

    }
    //设置标题
    public void SettingTitle(String title){
        tv_settitle.setText(title);
    }
    //设置标题描述
    public void SettingDes(String des){
        tv_setdes.setText(des);
    }

}
