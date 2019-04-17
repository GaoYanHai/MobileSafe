package com.example.mobilesafe.DB.Domain;

/**
 * Created by GaoYanHai on 2018/5/8.
 */

public class BlackNumberInfo {
    public String phone;
    public String mode;

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "mode='" + mode + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
