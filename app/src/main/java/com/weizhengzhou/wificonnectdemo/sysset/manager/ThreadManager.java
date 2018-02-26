package com.weizhengzhou.wificonnectdemo.sysset.manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 *  @文件名:   ThreadManager
 *  @创建者:   ww
 *  @创建时间:  2015/11/23 11:27
 *  @描述：    线程管理类，管理线程池，一个应用中有多个线程池，每个线程池做自己相关的业务
 */
public class ThreadManager {

	private static ThreadPoolProxy mNormalPool = new ThreadPoolProxy(5 * 1000);
	private static ThreadPoolProxy mDownloadPool = new ThreadPoolProxy(5 * 1000);

	public static ThreadPoolProxy getNormalPool() {
		return mNormalPool;
	}

	public static ThreadPoolProxy getDownloadPool() {
		return mDownloadPool;
	}

	public static class ThreadPoolProxy {
		private final int mCorePoolSize;
		private final int mMaximumPoolSize;
		private final long mKeepAliveTime;
		private ThreadPoolExecutor mPool;

		public ThreadPoolProxy(long keepAliveTime) {
			int i = Runtime.getRuntime().availableProcessors();
			this.mCorePoolSize = i + 1;
			this.mMaximumPoolSize = i * 2 + 1;
			this.mKeepAliveTime = keepAliveTime;
			//			Executors.newSingleThreadExecutor();
		}

		private void initPool() {
			if (mPool == null || mPool.isShutdown()) {
				//                int corePoolSize = 1;//核心线程池大小
				//                int maximumPoolSize = 3;//最大线程池大小
				//                long keepAliveTime = 5 * 1000;//保持存活的时间
				TimeUnit unit = TimeUnit.MILLISECONDS;//单位
				BlockingQueue<Runnable> workQueue = null;
				//                        workQueue = new ArrayBlockingQueue<Runnable>(3);//阻塞队列FIFO,大小有限制
				workQueue = new LinkedBlockingQueue();//
				//				workQueue = new SynchronousQueue<>(true);//
				//                workQueue = new PriorityBlockingQueue();

				ThreadFactory threadFactory = Executors.defaultThreadFactory();//线程工厂

				RejectedExecutionHandler handler = null;//异常捕获器

				//				handler = new ThreadPoolExecutor.DiscardOldestPolicy();//插队效果，去掉队列中首个任务，将新加入的放到队列中去
				handler = new ThreadPoolExecutor.AbortPolicy();//触发异常
				//				handler = new ThreadPoolExecutor.DiscardPolicy();//不做任何处理
				//                                handler = new ThreadPoolExecutor.CallerRunsPolicy();//直接执行，不归线程池控制,在调用线程中执行

				//                new Thread(task).start();
				mPool = new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, unit, workQueue, threadFactory, handler);
			}
		}

		/**
		 * 执行任务
		 * @param task
		 */
		public void execute(Runnable task) {
			initPool();

			//执行任务
			mPool.execute(task);
		}

		public Future<?> submit(Runnable task) {
			initPool();
			return mPool.submit(task);
		}

		public void shutdownNow() {
			if (mPool != null && !mPool.isShutdown()) {
				mPool.shutdownNow();
			}
		}

		public void remove(Runnable task) {
			if (mPool != null && !mPool.isShutdown()) {
				//				mPool.getQueue().remove(task);
				mPool.remove(task);
			}
		}

		public void remove() {
			if (mPool != null && !mPool.isShutdown()) {
				mPool.shutdown();
				BlockingQueue<Runnable> queue = mPool.getQueue();
				for (int i = 0; i < queue.size(); i++) {
					queue.remove();
				}
			}
		}
	}
}
