package com.weizhengzhou.wificonnectdemo.sysset.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.weizhengzhou.wificonnectdemo.R;
import com.weizhengzhou.wificonnectdemo.sysset.model.WifiBean;

import java.util.List;

/*
 *  @文件名:   WifiAdapter
 *  @创建者:   ww
 *  @创建时间:  2017.10.31 
 *  @描述：    wifi列表适配器
 */
public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.MyViewHolder> {

	private Context        mContext;
	private List<WifiBean> mList;
	private WifiBean       result;
	/**
	 * 保存当前的位置
	 */
	private int pos = -1;
	private OnItemClickListener listener;//item点击事件接口

	public WifiAdapter(Context mContext) {
		this.mContext = mContext;

	}

	/**
	 * 设置数据源
	 *
	 * @param list
	 */
	public void setList(List<WifiBean> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wifi_list, parent, false);
		return new MyViewHolder(convertView);
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, int position, List<Object> payloads) {
		if (payloads.isEmpty()) {
			onBindViewHolder(holder, position);

		} else {
			result = mList.get(position);
			if (result != null) {
				holder.wifiState.setText(result.getSafeType()[0]);
			}
		}
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		result = mList.get(position);
		if (result != null) {
			String ssid = result.getWifiInfo().SSID;
			holder.wifiName.setText(TextUtils.isEmpty(ssid) ? "" : ssid);// wifi名称
			holder.wifiState.setText(result.getSafeType()[0]);
			holder.wifiState.setCompoundDrawablesWithIntrinsicBounds(0, 0, result.getResId(), 0);
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (listener != null) {
						pos = position;
						result = mList.get(position);
						String[] safeTypeArr = result.getSafeType();
						if (safeTypeArr[0].contains("已保存")) {
							//wifi已经配置过
							//TODO:
							listener.onConfig(result, position);
							return;
						}
						if (!result.getSafeType()[0].equals("开放网络")) {
							//非开放网络
							listener.onConnectByInput(result, position);
						} else {
							//开放网络
							listener.onConnectNoPassword(result, position);

						}
					}
				}
			});
		}
	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	/**
	 * 更新wifi连接状态
	 * @param bean
	 */
	public void updateWifiState(WifiBean bean) {
		if (pos != -1) {
			if (bean.getWifiInfo().SSID.equals(result.getWifiInfo().SSID)) {
				notifyItemChanged(pos, "update_wifi_state");
			}
		}
	}

	/**
	 * 更新wifi连接状态
	 * @param bssid
	 */
	public void updateWifiState(String bssid) {
		if (pos != -1) {
			for (int i = 0; i < mList.size(); i++) {
				if (bssid.equals(mList.get(i).getWifiInfo().BSSID)) {
					pos = i;
					notifyItemChanged(pos, "update_wifi_state");
				}
			}
		}
	}

	/**
	 * 移除已连接的wifi
	 * @param bean
	 */
	public void removeWifi(WifiBean bean) {
		if (pos != -1) {
			if (bean.getWifiInfo().SSID.equals(result.getWifiInfo().SSID)) {
				mList.remove(pos);
				notifyItemRemoved(pos);
			}
		}
	}

	/**
	 * 移除已连接的wifi
	 * @param bssid
	 */
	public void removeWifi(String bssid) {
		if (pos != -1) {
			for (int i = 0; i < mList.size(); i++) {
				if (bssid.equals(mList.get(i).getWifiInfo().BSSID)) {
					pos = i;
					notifyItemRemoved(i);
				}
			}
		}
	}

	public void clear() {
		if (mList == null || mList.isEmpty())
			return;
		mList.clear();
		notifyDataSetChanged();

	}

	static class MyViewHolder extends RecyclerView.ViewHolder {
		TextView wifiName;
		TextView wifiState;

		public MyViewHolder(View root) {
			super(root);
			wifiName = (TextView) root.findViewById(R.id.tv_wifi_name);
			wifiState = (TextView) root.findViewById(R.id.tv_wifi_state);
		}
	}

	/**
	 * item点击事件接口
	 */
	public interface OnItemClickListener {
		/**
		 * 输入密码连接wifi
		 * @param position
		 * @param bean
		 */
		void onConnectByInput(WifiBean bean, int position);

		/**
		 * 连接开放的wifi
		 * @param position
		 * @param bean
		 */
		void onConnectNoPassword(WifiBean bean, int position);

		/**
		 * 设置已保存的wifi
		 * @param position
		 * @param bean
		 */
		void onConfig(WifiBean bean, int position);
	}

	/**
	 * item点击事件
	 *
	 * @param listener
	 */
	public void setListener(OnItemClickListener listener) {
		this.listener = listener;
	}
}
