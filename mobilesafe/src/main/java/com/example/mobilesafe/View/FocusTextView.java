package com.example.mobilesafe.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by GaoYanHai on 2018/4/17.
 */

public class FocusTextView extends TextView {
//通过java创建Text View
    public FocusTextView(Context context) {
        super(context);
    }
//上下文环境  属性   xml转换的时候由系统调用
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
//上下文环境  属性  布局文件中定义样式的构造方法  xml转换的时候由系统调用
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //重写获取焦点的方法 由系统调用的时候执行
    public boolean isFocused() {
        return true;
    }
}
