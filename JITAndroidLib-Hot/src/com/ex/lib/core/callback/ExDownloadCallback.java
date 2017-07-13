/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.callback;

import com.ex.lib.core.exception.ExException;

/**
 * @ClassName: ExDownloadCallback
 * @Description: ExDownloadCallback 回调接口
 * @author Aloneter
 * @date 2014-10-24 下午5:33:07
 * @Version 1.0
 * 
 */
public abstract class ExDownloadCallback {

	/**
	 * FINAL_下载成功
	 */
	public static final int DOWN_LOAD_SUCCESS = 0;
	/**
	 * FINAL_下载错误
	 */
	public static final int DOWN_LOAD_ERROR = -1; 
	/**
	 * FINAL_下载失败
	 */
	public static final int DOWN_LOAD_FAIL = 1;

	/**
	 * Method_下载成功回调
	 * 
	 * @param statusCode_请求状态码
	 * @param fileString_文件地址字符串
	 */
	public abstract void onSuccess(int statusCode, String fileString);

	/**
	 * Method_下载失败回调
	 * 
	 * @param statusCode_请求状态码
	 * @param e_异常信息
	 */
	public abstract void onError(int statusCode, ExException e);

}
