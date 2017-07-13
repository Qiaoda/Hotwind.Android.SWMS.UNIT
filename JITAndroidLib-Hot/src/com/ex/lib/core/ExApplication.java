/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */
package com.ex.lib.core;

import android.app.Application;

import com.baidu.frontia.FrontiaApplication;
import com.ex.lib.core.utils.mgr.MgrCrashHandler;

/**
 * @ClassName: ExApplication
 * @Description: Ex Application
 * @author Aloneter
 * @date 2014-10-24 下午5:07:37
 * @Version 1.0
 * 
 */
public class ExApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// 初始化本地异常处理
		MgrCrashHandler.getInstance(getApplicationContext()).init();
		// 初始化百度推送信息
		FrontiaApplication.initFrontiaApplication(getApplicationContext());
	}

}
