/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @ClassName: MgrThread
 * @Description: MgrThread 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrThread {

	private static Executor executor = Executors.newCachedThreadPool(); // 获取线程池对象

	/**
	 * 创建者
	 */
	private static class ThreadHolder {

		private static final MgrThread mgr = new MgrThread();
	}

	/**
	 * 获取当前实例对象
	 * 
	 * @return
	 */
	public static MgrThread getInstance() {

		return ThreadHolder.mgr;
	}

	/**
	 * 线程执行
	 * 
	 * @param runnable
	 */
	public void execute(Runnable runnable) {

		executor.execute(runnable);
	}

}
