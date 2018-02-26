package com.weizhengzhou.wificonnectdemo.sysset.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.weizhengzhou.wificonnectdemo.R;
import com.weizhengzhou.wificonnectdemo.sysset.model.WifiBean;

/**
 * Created by 75213 on 2018/2/26.
 */

public class WifiConnectDialog extends Dialog{
    private TextView wifiname;
    private EditText wifipassword;
    private TextView wificonnect;
    private OnWifiConnectListener mWifiConnectListener;
    private WifiBean mWifiBean= null;

    public interface OnWifiConnectListener{
        void onWifiConnect(WifiBean bean , String wPassWord);
    }

    public WifiConnectDialog(Context context , OnWifiConnectListener listener , WifiBean bean) {
        super(context , R.style.MyDialogStyle);
        mWifiConnectListener = listener;
        this.mWifiBean = bean;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_dialog);
        this.wificonnect = (TextView) findViewById(R.id.wifi_connect);
        this.wifipassword = (EditText) findViewById(R.id.wifi_password);
        this.wifiname = (TextView) findViewById(R.id.wifi_name);
        wifiname.setText(mWifiBean.getWifiInfo().SSID);
        wificonnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wPassword = wifipassword.getText().toString();
                mWifiConnectListener.onWifiConnect(mWifiBean ,wPassword);
            }
        });
    }
}
