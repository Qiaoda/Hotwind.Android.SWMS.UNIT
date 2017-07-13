/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * @ClassName: MgrDialog
 * @Description: MgrDialog 管理类
 * @author Aloneter
 * @date 2014-10-24 下午5:52:42
 * @Version 1.0
 * 
 */
public class MgrDialog {

	private static Context mContext;

	private static ProgressDialog mDialog;
	private static Dialog mAlertDialog;

	/**
	 * 创建者
	 */
	private static class DialogHolder {
		private static final MgrDialog mgr = new MgrDialog();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrDialog getInstance(Context context) {

		mContext = context;

		return DialogHolder.mgr;
	}

	/**
	 * Method_显示加载对话框
	 * 
	 * @param title_标题
	 * @param msg_消息
	 */
	public void showProgressDialog(String title, String msg, boolean isCancle) {
		
		if (mDialog != null && mDialog.isShowing() == true) {
			
			return;
		}
		
		mDialog = ProgressDialog.show(mContext, title, msg, true, isCancle);
	}
	/**
	 * Method_显示加载对话框
	 * 
	 * @param title_标题
	 * @param msg_消息
	 */
	public void showProgressDialog(String title, String msg) {

		if (mDialog != null && mDialog.isShowing() == true) {

			return;
		}

		mDialog = ProgressDialog.show(mContext, title, msg, true, false);
	}

	/**
	 * Method_显示加载对话框
	 * 
	 * @param titleId_标题ID
	 * @param msgId_消息
	 *            ID
	 */
	public void showProgressDialog(int titleId, int msgId) {

		String title = titleId == -1 ? "" : mContext.getString(titleId);
		String content = msgId == -1 ? "" : mContext.getString(msgId);

		showProgressDialog(title, content);
	}

	/**
	 * Method_隐藏加载对话框
	 */
	public void dismissProgressDialog() {

		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	/**
	 * Method_判断是否对话框已显示
	 * 
	 * @return 结果
	 */
	public boolean isDialogShowing() {

		if (mDialog != null) {

			return mDialog.isShowing();
		}

		return false;
	}

}
