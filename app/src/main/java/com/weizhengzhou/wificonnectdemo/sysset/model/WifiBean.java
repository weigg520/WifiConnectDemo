package com.weizhengzhou.wificonnectdemo.sysset.model;

import android.net.wifi.ScanResult;

/**
 * Created by 75213 on 2018/2/9.
 */

public class WifiBean {
    private static final String TAG = "WifiBean";

    private ScanResult wifiInfo;
    private String[] safeTypeArr;
    private int resId;

    public WifiBean(ScanResult wifiInfo, String[] safeTypeArr, int resId) {

        this.wifiInfo = wifiInfo;
        this.safeTypeArr = safeTypeArr;
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public ScanResult getWifiInfo() {
        return wifiInfo;
    }

    public void setWifiInfo(ScanResult wifiInfo) {
        this.wifiInfo = wifiInfo;
    }

    public String[] getSafeType() {
        return safeTypeArr;
    }

    public void setSafeType(int position, String safeType) {
        safeTypeArr[position] = safeType;
    }
}
