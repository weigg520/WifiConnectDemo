package com.weizhengzhou.wificonnectdemo.sysset.view.adapter;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weizhengzhou.wificonnectdemo.R;
import com.weizhengzhou.wificonnectdemo.base.IRecyclerListener;
import com.weizhengzhou.wificonnectdemo.base.RecyclerAdapter;

import java.util.List;

/**
 * WiFi界面适配器
 * Created by 75213 on 2018/1/26.
 */

public class WifiAdapter extends RecyclerAdapter<ScanResult> {

    public WifiAdapter(Context context, List<ScanResult> list , IRecyclerListener listener) {
        super(context, list , listener);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder holder, int position, ScanResult scanResult) {
        if (holder instanceof WifiHolder){
            WifiHolder holder1 = (WifiHolder)holder;
            holder1.tvSSID.setText(scanResult.SSID);
            holder1.tvLevel.setText("level" + scanResult.level);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WifiHolder(LayoutInflater.from(mContext).inflate(R.layout.wifi_item,parent, false));
    }

    static class WifiHolder extends RecyclerView.ViewHolder {
        TextView tvSSID;
        TextView tvLevel;
        public WifiHolder(View itemView) {
            super(itemView);
            tvSSID = itemView.findViewById(R.id.wifi_ssid);
            tvLevel = itemView.findViewById(R.id.wifi_level);
        }
    }
}
