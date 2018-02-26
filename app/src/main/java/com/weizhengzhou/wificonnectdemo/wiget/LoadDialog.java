package com.weizhengzhou.wificonnectdemo.wiget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.TextView;

import com.weizhengzhou.wificonnectdemo.R;
import com.weizhengzhou.wificonnectdemo.sysset.utils.UIUtils;


/*
 *  @文件名:   LoadDialog
 *  @创建者:   ww
 *  @创建时间:  2017/9/21 17:33
 *  @描述：    TODO
 */
public class LoadDialog
		extends Dialog implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener {
	private static final String TAG = "LoadDialog";
	private final Context mContext;
	private TextView mTvContent;
	private String[] mWifiStatus;

	public LoadDialog(Context context) {
		this(context, R.style.blend_theme_dialog);
	}

	public LoadDialog(Context context, int themeResId) {
		super(context, themeResId);
		mContext = context;
		init();
	}

	private void init() {
		setCanceledOnTouchOutside(false);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = UIUtils.dip2Px(100);
		params.height = params.width;
		getWindow().setAttributes(params);
		mWifiStatus = mContext.getResources().getStringArray(R.array.wifi_status);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading_black);

		mTvContent = (TextView) findViewById(R.id.dlg_content_text);

		setOnDismissListener(this);
		setOnShowListener(this);
	}

	public void updateTips(String str) {
		if (mTvContent != null) {
			mTvContent.setText(str);
		}
	}

	public void updateTips(int state) {
		if (state >= mWifiStatus.length) {
			return;
		}
		if (mTvContent != null) {
			mTvContent.setText(mWifiStatus[state]);
		}
	}

	public void resetTips() {
		if (mTvContent != null) {
			mTvContent.setText(mWifiStatus[2]);
		}
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		resetTips();
		mHandler.removeCallbacksAndMessages(null);
	}

	@Override
	public void onShow(DialogInterface dialogInterface) {
		mHandler.removeMessages(0);
		resetTips();
		mHandler.sendEmptyMessageDelayed(0, TIME_DELAY);
	}

	private static final long TIME_DELAY = 35000;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mTvContent != null) {
					mTvContent.setText("连接超时,请重试或更换wifi");
				}
				sendEmptyMessageDelayed(1, 1500);
				break;
			case 1:
				dismiss();
				break;
			default:
				break;
			}
		}
	};

}
