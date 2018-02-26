package com.weizhengzhou.wificonnectdemo.sysset.adapter;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ww on 2017/7/6.
 */
public class SpaceItemDecoration
        extends RecyclerView.ItemDecoration {

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        if (parent.getChildPosition(view) != 0) {
            outRect.top = space;
        }
        if (parent.getChildPosition(view) == layoutManager.getItemCount()-1) {
            outRect.bottom = space;
        }
    }
}
