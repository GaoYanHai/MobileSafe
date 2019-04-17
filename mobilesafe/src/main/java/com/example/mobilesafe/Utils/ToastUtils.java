package com.example.mobilesafe.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by GaoYanHai on 2018/4/11.
 */

public class ToastUtils {
    public static void show(Context ctx, String text){
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }
}
