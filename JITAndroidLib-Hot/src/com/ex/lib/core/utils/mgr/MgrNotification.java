/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import android.content.Context;

/** 
* @ClassName: MgrNotification 
* @Description: MgrNotification 管理类 
* @author Aloneter
* @date 2014-10-24 下午5:50:30 
* @Version 1.0
* 
*/
public class MgrNotification {

	private static Context mContext;

	/**
	 * 创建者
	 */
	private static class NotificationHolder {

		private static final MgrNotification mgr = new MgrNotification();
	}

	/**
	 * 获取当前实例对象
	 * 
	 * @param context
	 * @return
	 */
	public static MgrNotification getInstance(Context context) {

		mContext = context;

		return NotificationHolder.mgr;
	}

}
