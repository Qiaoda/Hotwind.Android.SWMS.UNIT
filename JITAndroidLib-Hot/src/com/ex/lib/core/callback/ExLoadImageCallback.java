/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.callback;

import android.view.View;

/**
 * @ClassName: ExLoadImageCallback
 * @Description: ExLoadImageCallback 回调接口
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public abstract class ExLoadImageCallback {

	/**
	 * FINAL_图片加载成功
	 */
	public static final int LOAD_IMAGE_SUCCESS = 0;
	/**
	 * FINAL_图片加载事变
	 */
	public static final int LOAD_IMAGE_FAIL = -1;

	/**
	 * Method_请求执行结果回调
	 * 
	 * @param code_执行结果码
	 * @param view_传递图像
	 * @param data_数据对象
	 */
	public abstract void onPostExecute(int code, View view, Object data);

}
