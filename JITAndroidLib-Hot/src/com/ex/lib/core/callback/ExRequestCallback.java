/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.callback;

import java.io.InputStream;
import java.util.HashMap;

import com.ex.lib.core.exception.ExException;

/**
 * @ClassName: ExRequestCallback
 * @Description: ExRequestCallback 回调接口
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public abstract class ExRequestCallback {

	/**
	 * FINAL_网络请求超时
	 */
	public static final int REQUEST_TIMEOUT = 408;
	/**
	 * FINAL_网络请求失败
	 */
	public static final int REQUEST_FAIL = -1;
	/**
	 * FINAL_网络请求错误
	 */
	public static final int REQUEST_ERROR = 1;
	/**
	 * FINAL_未联网
	 */
	public static final int REQUEST_UNAVAILABLE = 600;

	/**
	 * Method_请求成功回调
	 * 
	 * @param inStream_请求返回流对象
	 * @param cookies_Cookie 相应信息
	 */
	public abstract void onSuccess(InputStream inStream, HashMap<String, String> cookies);

	/**
	 * Method_请求失败回调
	 * @param statusCode_请求状态码
	 * @param e_异常信息
	 */
	public abstract void onError(int statusCode, ExException e);

}
