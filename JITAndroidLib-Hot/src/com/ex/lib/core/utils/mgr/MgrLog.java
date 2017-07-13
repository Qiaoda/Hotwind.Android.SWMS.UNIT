/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import android.util.Log;

/**
 * @ClassName: MgrLog
 * @Description: MgrLog 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrLog {

	public String mTag = "Demo"; // 便签
	public boolean mDebug = true; // 是否开启调试

	/**
	 * 创建者
	 */
	private static class LogHolder {

		private static final MgrLog mgr = new MgrLog();
	}

	/**
	 * 获取当前实例
	 * 
	 * @return
	 */
	public static MgrLog getInstance() {

		return LogHolder.mgr;
	}

	/**
	 * Method_设置是否开启调试
	 * 
	 * @param debug
	 */
	public void setDebug(boolean debug) {

		mDebug = debug;
	}

	/**
	 * Method_这是便签
	 * 
	 * @param tag
	 */
	public void setTag(String tag) {

		mTag = tag;
	}

	/**
	 * Method_d
	 * 
	 * @param msg_消息
	 */
	public void d(String msg) {

		if (mDebug) {
			Log.d(mTag, msg);
		}
	}

	/**
	 * Method_w
	 * 
	 * @param msg_消息
	 */
	public void w(String msg) {

		if (mDebug) {
			Log.w(mTag, msg);
		}
	}

	/**
	 * Method_i
	 * 
	 * @param msg_消息
	 */
	public void i(String msg) {

		if (mDebug) {
			Log.i(mTag, msg);
		}
	}

	/**
	 * Method_e
	 * 
	 * @param msg_消息
	 */
	public void e(String msg) {

		if (mDebug) {
			Log.e(mTag, msg);
		}
	}

	/**
	 * Method_e
	 * 
	 * @param msg_消息
	 */
	public void e(String msg, Throwable e) {

		if (mDebug) {
			Log.e(mTag, msg, e);
		}
	}

	/**
	 * Method_d
	 * 
	 * @param msg_消息
	 */
	public void d(String tag, String msg) {

		if (mDebug) {
			Log.d(tag, msg);
		}
	}

	/**
	 * Method_w
	 * 
	 * @param msg_消息
	 */
	public void w(String tag, String msg) {

		if (mDebug) {
			Log.w(tag, msg);
		}
	}

	/**
	 * Method_i
	 * 
	 * @param msg_消息
	 */
	public void i(String tag, String msg) {

		if (mDebug) {
			Log.i(tag, msg);
		}
	}

	/**
	 * Method_e
	 * 
	 * @param msg_消息
	 */
	public void e(String tag, String msg) {

		if (mDebug) {
			Log.e(tag, msg);
		}
	}

	/**
	 * Method_e
	 * 
	 * @param msg_消息
	 */
	public void e(String tag, String msg, Throwable e) {

		if (mDebug) {
			Log.e(tag, msg, e);
		}
	}

}
