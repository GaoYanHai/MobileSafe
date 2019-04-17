package com.example.mobilesafe.Engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by GaoYanHai on 2018/5/23.
 */

public class SmsBackUp {

    private static int index=0;


    //传递参数  上下文环境用来调用内容解析器     文件存储的路径    对话框进度条更新的进度
    public static void backup(Context ctx, String path, CallBack callBack) {

        Cursor cursor=null;
        FileOutputStream fileOutputStream=null;

        File file = new File(path);
        cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
        try {
            fileOutputStream = new FileOutputStream(file);
            //序列化数据放到xml 中
            XmlSerializer xmlSerializer = Xml.newSerializer();
            //设置xml的相应设置
            xmlSerializer.setOutput(fileOutputStream, "utf-8");
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");
            //设置进度条的最大值
            if (callBack != null) {
                callBack.setMax(cursor.getCount());
            }

            //读取数据库中的数据写到xml中
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");

                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");

                xmlSerializer.endTag(null, "sms");
                //更新进度条
                index++;
                Thread.sleep(200);
                if (callBack != null) {
                    callBack.setProgress(index);
                }



            }
            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor!=null&& fileOutputStream!=null){
                cursor.close();
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    //回调的实现
    public interface CallBack{
        public void setMax(int max);

        public void setProgress(int index);
    }
}
