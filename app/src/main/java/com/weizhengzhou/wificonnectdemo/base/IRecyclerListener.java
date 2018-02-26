package com.weizhengzhou.wificonnectdemo.base;

import android.view.View;

/**
 * @author ww
 * @date 2017.11.17
 * @des RecyclerView 点击事件监听器
 */
public interface IRecyclerListener {
    /**
     *
     * @param view
     * @param pos
     */
	void onItemClick(View view, int pos);

    /**
     *
     * @param view
     * @param pos
     */
	void onItemLongClick(View view, int pos);

}
