package com.weizhengzhou.wificonnectdemo.sysset.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.weizhengzhou.wificonnectdemo.MyApplication;
import com.weizhengzhou.wificonnectdemo.R;
import com.weizhengzhou.wificonnectdemo.sysset.adapter.SpaceItemDecoration;
import com.weizhengzhou.wificonnectdemo.sysset.adapter.WifiAdapter;
import com.weizhengzhou.wificonnectdemo.sysset.manager.LinkWifi;
import com.weizhengzhou.wificonnectdemo.sysset.manager.ThreadManager;
import com.weizhengzhou.wificonnectdemo.sysset.model.WifiBean;
import com.weizhengzhou.wificonnectdemo.sysset.reveiver.WifiReceiver;
import com.weizhengzhou.wificonnectdemo.sysset.utils.UIUtils;
import com.weizhengzhou.wificonnectdemo.sysset.utils.WifiAdmin;
import com.weizhengzhou.wificonnectdemo.wiget.LoadDialog;

import java.util.ArrayList;
import java.util.List;

/*
 *  @创建者:   ww
 *  @创建时间:  2017.10.31
 *  @描述：    wifi设置
 */
public class WifiSetActivity
		extends AppCompatActivity
		implements WifiReceiver.OnWifiChangeListener, WifiAdapter.OnItemClickListener, View.OnClickListener,WifiConnectDialog.OnWifiConnectListener{

	private static final String TAG = WifiSetActivity.class.getSimpleName();
	private WifiReceiver mReceiver;
	private LoadDialog mDialog;
	private TextView     mTvSelectWifi;
	private WifiAdapter  mWifiAdapter;
	private WifiAdmin mWifiAdmin;
	private LinkWifi mLinkWifi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_set);

		initEvent();
		initView();

	}

	private void initView() {
		mDialog = new LoadDialog(this);
		mWifiAdmin = new WifiAdmin(this);
		mLinkWifi = LinkWifi.getInstance(this);
		initWifiList();

		mWifiAdmin.scan();
	}

	private void initWifiList() {
		mTvSelectWifi = (TextView) findViewById(R.id.tv_select_wifi);
		if (mWifiAdmin.isWifiEnable()) {
			WifiInfo info = mWifiAdmin.getConnectionInfo();
			if (info.getNetworkId() == -1) {
				mTvSelectWifi.setText(R.string.select_wifi);
			} else {
				mTvSelectWifi.setText(info.getSSID().replace("\"", ""));
			}
		}
		findViewById(R.id.tv_refresh_wifi).setOnClickListener(this);

		RecyclerView wifiRecyclerView = (RecyclerView) findViewById(R.id.rv_wifi_list);
		wifiRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		wifiRecyclerView.addItemDecoration(new SpaceItemDecoration(UIUtils.dip2Px(15)));
		mWifiAdapter = new WifiAdapter(this);
		mWifiAdapter.setListener(this);
		wifiRecyclerView.setAdapter(mWifiAdapter);

		WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = manager.getScanResults();
		Log.e("TAG", "result:" + results);
	}

	private void initEvent() {

		registerReceiver();

	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); // ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(Intent.ACTION_DATE_CHANGED);
		filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		filter.addAction(WifiManager.EXTRA_RESULTS_UPDATED);
		filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);//正在获得IP地址
		filter.addAction(WifiReceiver.CONFIGURED_NETWORKS_CHANGED_ACTION);
		filter.addAction(WifiReceiver.LINK_CONFIGURATION_CHANGED_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
		mReceiver = new WifiReceiver(this);
		registerReceiver(mReceiver, filter);
	}

	private List<ScanResult> mWifiList;
	private List<WifiBean> mWifiInfos = new ArrayList<>();
	/**
	 * 处理获取到的wifi数据集
	 */
	private Runnable mWifiTask  = new Runnable() {
		@Override
		public void run() {
			//数据初始化
			mWifiList = mWifiAdmin.getScanResults();
			for (int i = 0; i < mWifiList.size(); i++) {
			}
			//根据信号强度排序
			sortByLevel(mWifiList);
			final List<WifiBean> temp = initData();

			mWifiInfos.clear();
			mWifiInfos.addAll(temp);

			//			mHandler.sendEmptyMessage(0);
			MyApplication.post(new Runnable() {
				@Override
				public void run() {
					mWifiAdapter.setList(mWifiInfos);
				}
			});
		}
	};

	/**
	 * 将搜索到的wifi根据信号强度从强到弱进行排序,level属性即为强度，负数
	 * 冒泡排序
	 *
	 * @param resultList
	 */
	private void sortByLevel(List<ScanResult> resultList) {
		ScanResult temp;
		for (int i = resultList.size() - 1; i >= 0; i--) {
			for (int j = 0; j < i; j++) {
				if (resultList.get(j).level < resultList.get(j + 1).level) {
					temp = resultList.get(j + 1);
					resultList.set(j + 1, resultList.get(j));
					resultList.set(j, temp);
				}
			}
		}

	}

	/**
	 * 数据组装
	 *
	 * @return
	 */
	private List<WifiBean> initData() {
		List<WifiBean> refreshWifiInfos = new ArrayList<>();
		ScanResult     scanResult;

		WifiConfiguration wifiConfig;
		WifiBean          bean;
		ScanResult        temp;
		//去重
		for (int i = 0; i < mWifiList.size(); i++) {
			temp = mWifiList.get(i);
			for (int j = i + 1; j < mWifiList.size(); j++) {
				// ssid是wifi名称可能有相同的情况
				if (mWifiList.get(j).SSID.equals(temp.SSID)) {
					mWifiList.remove(j);
					j--;
				}
			}
		}
		for (int i = 0; i < mWifiList.size(); i++) {
			scanResult = mWifiList.get(i);

			bean = getWifiInfo(scanResult);

			wifiConfig = mLinkWifi.IsExsits(scanResult.SSID);
			if (wifiConfig != null) {
				if (wifiConfig.networkId == mWifiAdmin.getConnectionInfo().getNetworkId()) {
					if (mWifiAdmin.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED) {
						bean.setSafeType(0, "已连接");
						if (refreshWifiInfos.remove(bean))
						refreshWifiInfos.add(0, bean);
						//ww 2017.9.4
						//移除已连接的wifi，显示到顶部区域
						final ScanResult finalScanResult = scanResult;
						MyApplication.post(new Runnable() {
							@Override
							public void run() {
								mTvSelectWifi.setText(TextUtils.isEmpty(finalScanResult.SSID) ? "" : finalScanResult.SSID);
							}
						});
					}
				} else {
					bean.setSafeType(0, bean.getSafeType()[1].equals("开放网络") ? bean.getSafeType()[1] : bean.getSafeType()[2]);
					refreshWifiInfos.add(0, bean);
				}
			} else {
				bean.setSafeType(0, bean.getSafeType()[1]);
				refreshWifiInfos.add(bean);
			}
		}
		return refreshWifiInfos;
	}

	private WifiBean getWifiInfo(ScanResult scanResult) {
		String[] lockStrArr = getWifiSafeType(scanResult);

		int resId = getResId(scanResult, lockStrArr[1]);

		WifiBean bean = new WifiBean(scanResult, lockStrArr, resId);
		//		LogUtils.e(TAG, "getWifiInfo:" + scanResult.SSID);
		return bean;
	}

	/**
	 * 根据信号强度和是否加密定义wifi图标
	 *
	 * @param scanResult
	 * @param lockType   加密类型
	 * @return
	 */
	private int getResId(ScanResult scanResult, String lockType) {
		int resId;
		if (scanResult.level < -90) {
			resId = lockType.equals("开放网络") ? R.drawable.icon_unlock_wifi_0 : R.drawable.icon_lock_wifi_0;
		} else if (scanResult.level < -80) {
			resId = lockType.equals("开放网络") ? R.drawable.icon_unlock_wifi_1 : R.drawable.icon_lock_wifi_1;
		} else if (scanResult.level < -70) {
			resId = lockType.equals("开放网络") ? R.drawable.icon_unlock_wifi_2 : R.drawable.icon_lock_wifi_2;
		} else if (scanResult.level < -60) {
			resId = lockType.equals("开放网络") ? R.drawable.icon_unlock_wifi_3 : R.drawable.icon_lock_wifi_3;
		} else {
			resId = lockType.equals("开放网络") ? R.drawable.icon_unlock_wifi_4 : R.drawable.icon_lock_wifi_4;
		}
		return resId;
	}

	/**
	 * 获取wifi加密类型
	 *
	 * @param scanResult
	 * @return
	 */
	@NonNull
	private String[] getWifiSafeType(ScanResult scanResult) {
		//定义wifi描述信息的数组
		//0为当前状态，1为加密信息，2为已保存状态
		String[] lockStrArr = new String[3];
				if (scanResult.capabilities.contains("WPA2-PSK") && scanResult.capabilities.contains("WPA-PSK")) {
					// WPA-PSK加密
					lockStrArr[1] = "通过WPA-PSK/WPA2-PSK进行保护";
					//lockStrArr[1] = "加密";
				} else if (scanResult.capabilities.contains("WPA2-PSK")) {
					// WPA-PSK加密
					lockStrArr[1] = "通过WPA2-PSK进行保护";
					//lockStrArr[1] = "加密";
				} else if (scanResult.capabilities.contains("WPA-PSK")) {
					// WPA-PSK加密
					lockStrArr[1] = "通过WPA-PSK进行保护";
					//lockStrArr[1] = "加密";
				} else if (scanResult.capabilities.contains("WPA-EAP")) {
					// WPA-EAP加密
					lockStrArr[1] = "通过WPA-EAP进行保护";
					//lockStrArr[1] = "加密";
				} else if (scanResult.capabilities.contains("WEP")) {
					// WEP加密
					lockStrArr[1] = "通过WEP进行保护";
					//lockStrArr[1] = "加密";
				}
		if (scanResult.capabilities.contains("WPA") || scanResult.capabilities.contains("WEP")) {
			lockStrArr[1] = "加密";
		} else {
			// 无密码
			lockStrArr[1] = "开放网络";
		}
		lockStrArr[2] = "已保存";
		return lockStrArr;
	}

	@Override
	public void onWifiEnable() {
		ThreadManager.getNormalPool().execute(mWifiTask);
		Log.e(TAG , "网络可用");

	}

	@Override
	public void onWifiDisable() {
		Log.e(TAG , "网络不可用");
	}

	@Override
	public void onNetState(NetworkInfo info) {
		Log.e(TAG , "网咯连接信息");
		//Toast.makeText(WifiSetActivity.this,"info:" + info.getType() , Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onWifiState(Object o) {
		Log.e(TAG , "wifi状态:" + o.toString());
	}

	private WifiConnectDialog mWifiConnectDialog;

	@Override
	public void onConnectByInput(WifiBean bean, int position) {
		Log.e(TAG , "非开放网络");
		mWifiConnectDialog = new WifiConnectDialog(this , this , bean);
		mWifiConnectDialog.show();
	}

	@Override
	public void onConnectNoPassword(WifiBean bean, int position) {
		Log.e(TAG , "开放网咯");
	}

	@Override
	public void onConfig(WifiBean bean, int position) {
		Log.e(TAG , "连接已经配置好的网咯");
		mLinkWifi.ConnectToNetID(position);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_refresh_wifi:
			startScan();
			break;
		default:
			break;
		}
	}

	/**
	 * 清空wifi列表
	 */
	private void clearWifiList() {
		mWifiAdapter.clear();
	}

	/**
	 * 开始扫描wifi，通过广播监听结果
	 */
	private void startScan() {
		mWifiAdmin.scan();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onWifiConnect(WifiBean bean , String wPassWord) {
		mLinkWifi.CreateWifiInfo2(bean.getWifiInfo() , wPassWord);
		mWifiConnectDialog.cancel();
		mWifiConnectDialog.dismiss();
	}
}
