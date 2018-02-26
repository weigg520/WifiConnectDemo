package com.weizhengzhou.wificonnectdemo.sysset.manager;

import android.os.Handler;
import android.os.Message;
/**
 * Created by Administrator on 2017/11/1.
 * @des 更新时间、定时任务的消息
 */

public class TimerHandler extends Handler {

	public static final int MSG_UPDATE_TIME = 0;
	public static final int MSG_FORECAST_MORNING = 1;
	public static final int MSG_FORECAST_NOON = 2;
	public static final int MSG_FORECAST_AFTEERNOON = 3;
	public static final int MSG_CHECK_UPDATE = 4;
	private String TAG = "TimerHandler";

	private TimerHandler() {

	}

	public static TimerHandler init() {
		return THandler.sHandler;
	}

	public static class THandler {
		private static TimerHandler sHandler = new TimerHandler();
	}

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_UPDATE_TIME:
		case MSG_CHECK_UPDATE:
			if (mListener!=null)
				mListener.checkUpdate();
		default:
			break;
		}
	}

	public void onDestroy() {
		removeCallbacksAndMessages(null);

	}

	private IHandlerListener mListener;

	public void addListener(IHandlerListener l) {

		mListener = l;

	}

	public interface IHandlerListener {
		/**
		 * 启动延时1分钟检查更新
		 */
		void checkUpdate();

	}

}
