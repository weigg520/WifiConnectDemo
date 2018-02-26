package com.weizhengzhou.wificonnectdemo.sysset.manager;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @描述 wifi连接
 */
public class LinkWifi {

	private static final String TAG = "LinkWifi";
	private WifiManager wifimanager;
	private static LinkWifi mLinkWifi = null;

	public interface WifiCipherType {
		int WIFI_CIPHER_WEP = 100;
		int WIFI_CIPHER_WPA_EAP = 101;
		int WIFI_CIPHER_WPA_PSK = 102;
		int WIFI_CIPHER_WPA2_PSK = 103;
		int WIFI_CIPHER_NOPASS = 104;
	}

	public LinkWifi(Context context) {
		wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	public static LinkWifi getInstance(Context context) {
		if (mLinkWifi == null) {
			mLinkWifi = new LinkWifi(context);
		}
		return mLinkWifi;
	}

	public boolean checkWifiState() {
		boolean isOpen = true;
		int wifiState = wifimanager.getWifiState();

		if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING
				|| wifiState == WifiManager.WIFI_STATE_UNKNOWN || wifiState == WifiManager.WIFI_STATE_ENABLING) {
			isOpen = false;
		}

		return isOpen;
	}

	/**
	 * 判断该wifi是否已连接
	 *
	 * @param result
	 * @return
	 */
	public boolean isConnected(ScanResult result) {
		WifiInfo info = wifimanager.getConnectionInfo();
		if (TextUtils.isEmpty(info.getBSSID()) || TextUtils.isEmpty(result.BSSID)) {
			return false;
		}
		if (TextUtils.equals(info.getBSSID(), result.BSSID)) {
			return true;
		}

		return false;
	}

	public boolean ConnectToNetID(int netID) {

		return wifimanager.enableNetwork(netID, true);
	}

	/**
	 * 连接新配置的wifi
	 * @param wifiId
	 * @return
	 */
	public boolean ConnectWifi(int wifiId) {
		List<WifiConfiguration> wifiConfigList = wifimanager.getConfiguredNetworks();
		for (int i = 0; i < wifiConfigList.size(); i++) {
			WifiConfiguration wifi = wifiConfigList.get(i);
			if (wifi.networkId == wifiId) {
				while (!(wifimanager.enableNetwork(wifiId, true))) {//激活该Id，建立连接
					//status:0--已经连接，1--不可连接，2--可以连接
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否已经配置该wifi
	 *
	 * @param SSID
	 * @return
	 */
	public WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> configs = wifimanager.getConfiguredNetworks();
		if (configs == null || configs.size() == 0)
			return null;

		for (WifiConfiguration config : configs) {

			if (config.SSID.equals("\"" + SSID + "\"")) {
				return config;
			}
		}
		return null;
	}

	/**
	 * 配置wifi信息获取networkID
	 * @param wifiInfo
	 * @param pwd
	 * @return
	 */
	public int CreateWifiInfo2(ScanResult wifiInfo, String pwd) {

		int type = getWifiCipherType(wifiInfo);

		WifiConfiguration config = CreateWifiInfo(wifiInfo.SSID, pwd, type);

		return wifimanager.addNetwork(config);
	}

	/**
	 * 获取wifi加密类型
	 *
	 * @param wifiInfo wifi信息
	 * @return 加密类型
	 */
	@NonNull
	private int getWifiCipherType(ScanResult wifiInfo) {
		int type;
		if (wifiInfo.capabilities.contains("WPA2-PSK")) {

			type = WifiCipherType.WIFI_CIPHER_WPA2_PSK;
		} else if (wifiInfo.capabilities.contains("WPA-PSK")) {

			type = WifiCipherType.WIFI_CIPHER_WPA_PSK;
		} else if (wifiInfo.capabilities.contains("WPA-EAP")) {

			type = WifiCipherType.WIFI_CIPHER_WPA_EAP;
		} else if (wifiInfo.capabilities.contains("WEP")) {

			type = WifiCipherType.WIFI_CIPHER_WEP;
		} else {

			type = WifiCipherType.WIFI_CIPHER_NOPASS;
		}
		return type;
	}

	public WifiConfiguration setMaxPriority(WifiConfiguration config) {
		int priority = getMaxPriority() + 1;
		if (priority > 99999) {
			priority = shiftPriorityAndSave();
		}

		config.priority = priority; // 2147483647;
		Log.e(TAG, "priority=" + priority);

		wifimanager.updateNetwork(config);

		return config;
	}

	/**
	 * 创建wifi的配置信息
	 *
	 * @param SSID
	 * @param password
	 * @param type
	 * @return
	 */
	public WifiConfiguration CreateWifiInfo(String SSID, String password, int type) {

		int priority;

		WifiConfiguration config = this.IsExsits(SSID);
		if (config != null) {
			//ww 2017.9.25 多次输入密码尝试连接
			if (!config.preSharedKey.replace("\"", "").equals(password)) {
				config.preSharedKey = "\"" + password + "\"";
			}
			return setMaxPriority(config);
		}

		config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		config.status = WifiConfiguration.Status.ENABLED;
		// config.BSSID = BSSID;
		// config.hiddenSSID = true;

		priority = getMaxPriority() + 1;
		if (priority > 99999) {
			priority = shiftPriorityAndSave();
		}

		config.priority = priority; // 2147483647;
		if (type == WifiCipherType.WIFI_CIPHER_NOPASS) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == WifiCipherType.WIFI_CIPHER_WEP) {
			config.preSharedKey = "\"" + password + "\"";

			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		} else if (type == WifiCipherType.WIFI_CIPHER_WPA_EAP) {

			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);

		} else if (type == WifiCipherType.WIFI_CIPHER_WPA_PSK) {

			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN | WifiConfiguration.Protocol.WPA);

		} else if (type == WifiCipherType.WIFI_CIPHER_WPA2_PSK) {

			config.preSharedKey = "\"" + password + "\"";
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);

			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

		} else {
			return null;
		}

		return config;
	}

	private int getMaxPriority() {
		List<WifiConfiguration>     localList     = this.wifimanager.getConfiguredNetworks();
		int                         i             = 0;
		Iterator<WifiConfiguration> localIterator = localList.iterator();
		while (true) {
			if (!localIterator.hasNext())
				return i;
			WifiConfiguration localWifiConfiguration = localIterator.next();
			if (localWifiConfiguration.priority <= i)
				continue;
			i = localWifiConfiguration.priority;
		}
	}

	private int shiftPriorityAndSave() {
		List<WifiConfiguration> localList = this.wifimanager.getConfiguredNetworks();
		sortByPriority(localList);
		int i = localList.size();
		for (int j = 0;; ++j) {
			if (j >= i) {
				this.wifimanager.saveConfiguration();
				return i;
			}
			WifiConfiguration localWifiConfiguration = localList.get(j);
			localWifiConfiguration.priority = j;
			this.wifimanager.updateNetwork(localWifiConfiguration);
		}
	}

	private void sortByPriority(List<WifiConfiguration> paramList) {
		Collections.sort(paramList, new SjrsWifiManagerCompare());
	}

	class SjrsWifiManagerCompare implements Comparator<WifiConfiguration> {
		public int compare(WifiConfiguration paramWifiConfiguration1, WifiConfiguration paramWifiConfiguration2) {
			return paramWifiConfiguration1.priority - paramWifiConfiguration2.priority;
		}
	}

	private List<ScanResult> results;
	/**
	 * 扫描wifi列表
	 */
	public void startScan() {
		wifimanager.startScan();
		//得到扫描结果
		List<ScanResult> results = wifimanager.getScanResults();
		// 得到配置好的网络连接
		if (results == null) {
          /*  if(wifimanager.getWifiState()==3){
                Toast.makeText(mContext,"当前区域没有无线网络",Toast.LENGTH_SHORT).show();
            }else if(wifimanager.getWifiState()==2){
                Toast.makeText(mContext,"wifi正在开启，请稍后扫描", Toast.LENGTH_SHORT).show();
            }else{Toast.makeText(mContext,"WiFi没有开启", Toast.LENGTH_SHORT).show();
            }*/
		} else {
			results = new ArrayList();
			for(ScanResult result : results){
				if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
					continue;
				}
				boolean found = false;
				for(ScanResult item: results){
					if(item.SSID.equals(result.SSID)&&item.capabilities.equals(result.capabilities)){
						found = true;break;
					}
				}
				if(!found){
					results.add(result);
				}
			}
		}
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return results;
	}

}
