package com.weizhengzhou.wificonnectdemo.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * @author ww
 * @date 2017.11.17
 * @des  RecyclerView的适配器，封装一些常用的方法、点击事件
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = "RecyclerAdapter";
	protected IRecyclerListener mIRecyclerListener;
	protected List<T> mList;
	protected int index = -1;
	protected Context mContext;
	protected RecyclerView recyclerView;


	public RecyclerAdapter(Context context) {
		mContext = context;
	}

	public RecyclerAdapter(Context context, List<T> list) {
		mContext = context;
		mList = list;
	}

	public RecyclerAdapter(List<T> list) {
		mList = list;

	}

	public RecyclerAdapter(List<T> list, IRecyclerListener l) {
		mList = list;
		mIRecyclerListener = l;
	}

	public RecyclerAdapter(Context context, List<T> list, IRecyclerListener l) {
		mContext = context;
		mList = list;
		mIRecyclerListener = l;
	}

	public RecyclerAdapter(Context context, IRecyclerListener l) {
		mContext = context;
		mIRecyclerListener = l;
	}


	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
		initItemEvent(holder);

		if (payloads.isEmpty()) {
			bindData(holder, position, mList.get(position));

		} else {
			bindData(holder, position, mList.get(position), payloads);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		initItemEvent(holder);

		bindData(holder, position, mList.get(position));
	}

	public void initItemEvent(final RecyclerView.ViewHolder holder) {
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setIndex(holder.getAdapterPosition());
				if (mIRecyclerListener != null)
					mIRecyclerListener.onItemClick(holder.itemView, holder.getAdapterPosition());
			}
		});

		holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				setIndex(holder.getAdapterPosition());
				if (mIRecyclerListener != null)
					mIRecyclerListener.onItemLongClick(holder.itemView, holder.getAdapterPosition());
				return false;
			}
		});
	}

	/**
	 * 填充数据
	 * @param holder
	 * @param position
	 */
	public abstract void bindData(RecyclerView.ViewHolder holder, int position, T t);

	/**
	 * 填充数据
	 * @param holder
	 * @param position
	 */
	protected void bindData(RecyclerView.ViewHolder holder, int position, T t, List<Object> payloads) {

	}

	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;

	}

	/**
	 * 监听点击事件
	 * @param l
	 */
	public void addAdapterListener(IRecyclerListener l) {
		mIRecyclerListener = l;
	}

	/**
	 * 刷新数据
	 */
	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	public List<T> getList() {

		return mList;
	}

	/**
	 * 获取item的实体数据
	 * @return
	 */
	public T getEntity() {
		if (index == -1 || mList == null || mList.size() == 0)
			return null;
		return mList.get(index);
	}

	/**
	 * 获取item的实体数据
	 * @param pos 某个item位置
	 * @return
	 */
	public T getEntity(int pos) {
		if (pos == -1 || mList == null || mList.size() == 0)
			return null;
		return mList.get(pos);
	}

	/**
	 * @return 当前点击事件位置
	 */
	public int getIndex() {

		return index;

	}

	/**
	 * 设置当前点击事件位置
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * 在某个item位置 批量添加数据
	 *
	 * @param start 起始item
	 * @param list  
	 *              
	 */
	public void insertRange(int start, List<T> list) {

		mList.addAll(start, list);
		notifyItemRangeInserted(start, list.size());
	}

	/**
	 * 在某个item位置 添加单个数据
	 *
	 * @param position 单个数据的位置
	 * @param t         
	 *                 例如，添加到第2个item的位置，position就是2，而list.add(2,object)）
	 */
	public void insert(int position, T t) {
		// TODO Auto-generated method stub
		mList.add(position, t);
		notifyItemInserted(position);
	}

	/**
	 * 删除单个数据
	 *
	 * @param position 单个数据的位置
	 *                 例如，删除第2个item的位置，position就是2，而list.removed(2)）
	 */
	public void remove(int position) {
		this.mList.remove(position);
		notifyItemRemoved(position);
	}

}
