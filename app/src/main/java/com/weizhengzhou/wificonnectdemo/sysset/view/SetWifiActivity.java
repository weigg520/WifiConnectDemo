package com.weizhengzhou.wificonnectdemo.sysset.view;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.weizhengzhou.wificonnectdemo.R;
import com.weizhengzhou.wificonnectdemo.base.IRecyclerListener;
import com.weizhengzhou.wificonnectdemo.sysset.manager.LinkWifi;
import com.weizhengzhou.wificonnectdemo.sysset.view.adapter.WifiAdapter;

import java.util.List;

public class SetWifiActivity extends AppCompatActivity implements IRecyclerListener {
    private static final String TAG = SetWifiActivity.class.getSimpleName();
    private LinkWifi mLinkWifi; //Wifi连接类
    private List<ScanResult> mScanResult = null;
    private WifiAdapter mAdpter = null;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_wifi);

        init();
        initView();

        testLog();

    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.wifi_rv);
    }

    //显示wifi列表
    private void testLog() {
        mScanResult = mLinkWifi.getWifiList();
        if (mLinkWifi != null) {
            mAdpter = new WifiAdapter(this, mScanResult ,this);
            Log.e(TAG, "获取列表成功");
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(mAdpter);
        }
    }

    private void init() {
        //注册监听广播
        //获取工具栏实例
        mLinkWifi = LinkWifi.getInstance(this);
        //扫描wifi
        mLinkWifi.startScan();
    }

    @Override
    public void onItemClick(View view, int pos) {
        //ToastUtil.showToast(this , "连接：" + mScanResult.get(pos).SSID);
        //startActivity(new Intent(AppContextUtil.getInstance() , ActivationActivity.class));
    }

    @Override
    public void onItemLongClick(View view, int pos) {

    }
}
