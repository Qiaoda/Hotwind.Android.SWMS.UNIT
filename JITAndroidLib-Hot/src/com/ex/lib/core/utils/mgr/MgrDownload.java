/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import android.content.Context;

import com.ex.lib.core.callback.ExDownloadCallback;

/** 
* @ClassName: MgrDownload 
* @Description: MgrDownload 管理类 
* @author Aloneter
* @date 2014-10-24 下午5:52:28 
* @Version 1.0
* 
*/
public class MgrDownload {

	private static Context mContext; // 上下文

	/**
	 * 创建者
	 */
	private static class DownloadHolder {

		private static final MgrDownload mgr = new MgrDownload();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrDownload getInstance(Context context) {

		mContext = context;

		return DownloadHolder.mgr;
	}

	public void start(String urlString, ExDownloadCallback callback) {

	}


}
