package com.weizhengzhou.wificonnectdemo.sysset.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;


import java.lang.ref.WeakReference;

/*
 *  @文件名:   WifiReceiver
 *  @创建者:   ww
 *  @创建时间:  2017.10.31 
 *  @描述：    监听wifi状态
 */
public class WifiReceiver
        extends BroadcastReceiver
{
    private static final String TAG                                = "WifiReceiver";
    /**
     * Broadcast intent action indicating that the configured networks changed.
     * This can be as a result of adding/updating/deleting a network.
     *
     * @hide
     */
    public static final  String CONFIGURED_NETWORKS_CHANGED_ACTION = "android.net.wifi.CONFIGURED_NETWORKS_CHANGE";

    /**
     * Broadcast intent action indicating that the link configuration
     * changed on wifi.
     *
     * @hide
     */
    public static final String LINK_CONFIGURATION_CHANGED_ACTION = "android.net.wifi.LINK_CONFIGURATION_CHANGED";
    /**
     * The lookup key for a (@link android.net.wifi.WifiConfiguration} object representing
     * the changed Wi-Fi configuration when the {@link #CONFIGURED_NETWORKS_CHANGED_ACTION}
     * broadcast is sent.
     * @hide
     */
    public static final String EXTRA_WIFI_CONFIGURATION          = "wifiConfiguration";

    public interface OnWifiChangeListener {
        /**
         * 网络可用
         */
        void onWifiEnable();

        /**
         * 网络不可用
         */
        void onWifiDisable();

        /**
         * 监听网络状态
         * @param info
         */
        void onNetState(NetworkInfo info);

        /**
         * 监听wifi状态
         * @param o
         */
        void onWifiState(Object o);
    }

    private OnWifiChangeListener mListener;

    public WifiReceiver(OnWifiChangeListener l) {
        WeakReference reference = new WeakReference(l);
        Object        o         = reference.get();
        if (o != null) {
            mListener = (OnWifiChangeListener) o;
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            //状态发生改变
            if (mListener != null) {
                Parcelable p1 = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (p1 != null && p1 instanceof NetworkInfo) {
                    mListener.onNetState((NetworkInfo) p1);
                }
                Parcelable p2 = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                if (p2 != null && p2 instanceof WifiInfo) {
                    mListener.onWifiState((WifiInfo) p2);
                }
            }

        } else if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            //差量比较，排序
            if (mListener != null) {
                mListener.onWifiEnable();
            }
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {//正在获得IP地址
            SupplicantState state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            //密码错误
            int code = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
            if (code == WifiManager.ERROR_AUTHENTICATING) {
                if (mListener != null) { mListener.onWifiDisable(); }
            }
        } else if (action.equals(WifiManager.EXTRA_RESULTS_UPDATED)) {

        } else if (CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)) {
            WifiConfiguration config = intent.getParcelableExtra(EXTRA_WIFI_CONFIGURATION);
            if (config != null) {
                if (mListener != null) { mListener.onWifiState(config); }
            }

        } else if (LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {


        } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int    wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            String tip       = "";
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLING:
                    tip = "正在打开WLAN...";
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    //正在搜索附近的WLAN...
                    tip = "正在搜索附近的WLAN...";
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    //正在关闭WLAN...
                    tip = "正在关闭WLAN...";
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    //要查看可用网络，请打开WLAN
                    tip = "要查看可用网络，请打开WLAN...";
                    break;
            }
            if (mListener != null) {
                mListener.onWifiState(tip);
            }
        }
    }
}
