/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import android.content.Context;
import android.widget.Toast;

/** 
* @ClassName: MgrToast 
* @Description: MgrToast 管理类
* @author Aloneter
* @date 2014-10-24 下午5:50:44 
* @Version 1.0
* 
*/
public class MgrToast {

	public static Context mContext; // 上下文
	public static Toast mToast; // 提示框

	/**
	 * 创建者
	 */
	private static class ToastHolder {

		private static final MgrToast mgr = new MgrToast();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrToast getInstance(Context context) {

		mContext = context;

		return ToastHolder.mgr;
	}

	public void show(String content) {

		show(content, -1);
	}

	public void show(int resId) {

		show(mContext.getString(resId), -1);
	}

	public void show(String content, int time) {

		if (mToast == null) {
			mToast = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(content);
		}

		if (time == -1) {
			time = Toast.LENGTH_SHORT;
		} else if (time == 0) {
			time = Toast.LENGTH_LONG;
		}

		mToast.setDuration(time);

		mToast.show();
	}

	public void show(int resId, int time) {

		show(mContext.getString(resId), time);
	}

	public void showShort(String content) {

		show(content, -1);
	}

	public void showShort(int resId) {

		show(mContext.getString(resId), -1);
	}

	public void showLong(String content) {

		show(content, 0);
	}

	public void showLong(int resId) {

		show(mContext.getString(resId), 0);
	}

}
