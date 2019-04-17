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

public class SettingItemView extends RelativeLayout {

    public static final String NAMESPACE = "http://schemas.android.com/apk/res/com.example.mobilesafe";
    private CheckBox cb_box;
    private TextView tv_setdes;
    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_item, this);

        TextView tv_settitle = (TextView) findViewById(R.id.tv_settitle);
        tv_setdes = (TextView) findViewById(R.id.tv_setdes);
        cb_box = (CheckBox) findViewById(R.id.cb_box);
        //获取自定义控件的属性值
        initAttrs(attrs);
        tv_settitle.setText(mDestitle);
    }

    private void initAttrs(AttributeSet attrs) {
        mDestitle = attrs.getAttributeValue(NAMESPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //当前控件的状态
    public boolean isCheck() {
        return cb_box.isChecked();
    }

    //是否开启的变量
    public void setCheck(boolean isCheck) {
        cb_box.setChecked(isCheck);
        if (isCheck) {
            tv_setdes.setText(mDeson);
        } else {
            tv_setdes.setText(mDesoff);
        }
    }
}
