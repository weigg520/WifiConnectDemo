package com.weizhengzhou.wificonnectdemo;

import android.app.Application;
import android.os.Handler;

import com.weizhengzhou.wificonnectdemo.sysset.utils.AppContextUtil;

/**
 * Created by 75213 on 2018/2/9.
 */

public class MyApplication extends Application {
    private static Handler mHandler;
    @Override
    public void onCreate() {
        super.onCreate();
        AppContextUtil.init(getApplicationContext());
        mHandler = new Handler(getMainLooper());
    }

    /**
     * 子线程-->主线程
     * @param r
     */
    public static void post(Runnable r) {
        mHandler.post(r);
    }


    /**
     * 延时执行
     * @param r
     * @param delay
     */
    public static void postDelay(Runnable r, long delay) {
        mHandler.postDelayed(r, delay);
    }

    /**
     * 移除任务
     * @param r
     */
    public static void remove(Runnable r) {
        mHandler.removeCallbacks(r);
    }

    /**
     * If <var>token</var> is null,
     * all callbacks and messages will be removed.
     */
    public static void remove() {
        mHandler.removeCallbacksAndMessages(null);
    }
}
