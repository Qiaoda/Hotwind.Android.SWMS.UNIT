/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils;

import android.app.Activity;
import android.content.Context;

import com.ex.lib.core.utils.mgr.MgrActivity;
import com.ex.lib.core.utils.mgr.MgrAndroid;
import com.ex.lib.core.utils.mgr.MgrApp;
import com.ex.lib.core.utils.mgr.MgrCache;
import com.ex.lib.core.utils.mgr.MgrCrashHandler;
import com.ex.lib.core.utils.mgr.MgrDB;
import com.ex.lib.core.utils.mgr.MgrDevice;
import com.ex.lib.core.utils.mgr.MgrDialog;
import com.ex.lib.core.utils.mgr.MgrDownload;
import com.ex.lib.core.utils.mgr.MgrFile;
import com.ex.lib.core.utils.mgr.MgrImage;
import com.ex.lib.core.utils.mgr.MgrLog;
import com.ex.lib.core.utils.mgr.MgrMD5;
import com.ex.lib.core.utils.mgr.MgrNet;
import com.ex.lib.core.utils.mgr.MgrNotification;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.core.utils.mgr.MgrQuer;
import com.ex.lib.core.utils.mgr.MgrString;
import com.ex.lib.core.utils.mgr.MgrT;
import com.ex.lib.core.utils.mgr.MgrThread;
import com.ex.lib.core.utils.mgr.MgrToast;
import com.ex.lib.core.utils.mgr.MgrUpdate;
import com.ex.lib.core.utils.mgr.MgrZip;
import com.ex.lib.ext.analytics.MgrAnalytics;
import com.ex.lib.ext.push.MgrPush;
import com.ex.lib.ext.share.MgrSharer;

/**
 * @ClassName: Ex
 * @Description: Ex 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class Ex {

	/**
	 * Ex 设备管理
	 */
	public static MgrDevice Device(Context context) {

		return MgrDevice.getInstance(context);
	}

	/**
	 * Ex 图片管理
	 */
	public static MgrImage Image(Context context) {

		return MgrImage.getInstance(context);
	}

	/**
	 * Ex 数据库管理
	 */
	public static MgrDB DB() {

		return MgrDB.getInstance();
	}
	
	/**
	 * Ex 应用更新管理
	 */
	public static MgrUpdate Update(Context context) {

		return MgrUpdate.getInstance(context);
	}

	/**
	 * Ex 缓存管理
	 */
	public static MgrCache Cache(Context context) {

		return MgrCache.getInstance(context);
	}

	/**
	 * Ex 配置管理
	 */
	public static MgrPerference Perference(Context context) {

		return MgrPerference.getInstance(context);
	}

	/**
	 * Ex 网络管理
	 */
	public static MgrNet Net(Context context) {

		return MgrNet.getInstance(context);

	}

	/**
	 * Ex 字符处理
	 */
	public static MgrString String() {

		return MgrString.getInstance();
	}

	/**
	 * Ex 日志管理
	 */
	public static MgrLog Log() {

		return MgrLog.getInstance();

	}

	/**
	 * Ex 文件管理
	 */
	public static MgrFile File() {

		return MgrFile.getInstance();

	}

	/**
	 * Ex 转换管理
	 */
	public static MgrT T() {

		return MgrT.getInstance();
	}

	/**
	 * Ex 线程管理
	 */
	public static MgrThread Thread() {

		return MgrThread.getInstance();

	}

	/**
	 * Ex MD5 管理
	 */
	public static MgrMD5 MD5() {

		return MgrMD5.getInstance();
	}

	/**
	 * Ex Activity 管理
	 */
	public static MgrActivity Activity(Context context) {

		return MgrActivity.getInstance(context);
	}

	/**
	 * Ex Android 管理
	 */
	public static MgrAndroid Android(Context context) {

		return MgrAndroid.getInstance(context);
	}

	/**
	 * Ex App 管理
	 */
	public static MgrApp App(Context context) {

		return MgrApp.getInstance(context);
	}

	/**
	 * Ex Download 管理
	 */
	public static MgrDownload Download(Context context) {

		return MgrDownload.getInstance(context);
	}

	/**
	 * Ex Notification 管理
	 */
	public static MgrNotification Notification(Context context) {

		return MgrNotification.getInstance(context);
	}

	/**
	 * Ex Toast 管理
	 */
	public static MgrToast Toast(Context context) {

		return MgrToast.getInstance(context);
	}

	/**
	 * Ex Zip 管理
	 */
	public static MgrZip Zip() {

		return MgrZip.getInstance();
	}

	/**
	 * Ex Dialog 管理
	 */
	public static MgrDialog Dialog(Context context) {

		return MgrDialog.getInstance(context);
	}

	/**
	 * Ex 分享管理
	 */
	public static MgrSharer Share(Activity activity) {

		return MgrSharer.getInstance(activity);
	}

	/**
	 * Ex 推送管理
	 */
	public static MgrPush Push(Activity activity) {

		return MgrPush.getInstance(activity);
	}

	/**
	 * Ex 统计管理
	 */
	public static MgrAnalytics Analytics(Activity activity) {

		return MgrAnalytics.getInstance(activity);
	}

	/**
	 * Ex 二维码管理
	 */
	public static MgrQuer Quer() {

		return MgrQuer.getInstance();
	}
	
	/**
	 * Ex 应用异常管理
	 */
	public static MgrCrashHandler CrashHandler(Context context){
		
		return MgrCrashHandler.getInstance(context);
	}

}
