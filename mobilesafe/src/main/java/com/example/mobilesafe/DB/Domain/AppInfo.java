package com.example.mobilesafe.DB.Domain;

import android.graphics.drawable.Drawable;

/**
 * Created by GaoYanHai on 2018/5/24.
 */

public class AppInfo {
    public String name;
    public String packageName;
    public Drawable icon;
    public boolean isSdCard;
    public boolean isSystem;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSdCard() {
        return isSdCard;
    }

    public void setSdCard(boolean sdCard) {
        isSdCard = sdCard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


}
