/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.ible;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ExNetIble
 * @Description: 网络处理接口
 * @author Aloneter
 * @date 2014-10-24 下午5:34:40
 * @Version 1.0
 * 
 */
public interface ExNetIble {

	/**
	 * FINAL_OBJECT 请求参数 OBJECT 类型
	 */
	public final String NET_PARAM_OBJECT = "param_object";
	/**
	 * FINAL_COOKIE_STR 请求参数 COOKIE_STR 类型
	 */
	public final String NET_PARAM_COOKIE_STR = "param_cookie_str";
	
	/**
	 * FINAL_OBJECT 请求参数 CacheKey 类型
	 */
	public final String NET_PARAM_CACHE_KEY = "param_cache_key";

	/**
	 * Method_启动处理回调
	 * 
	 * @param what_操作码
	 * @return 请求参数
	 */
	public Map<String, String> onStart(int what);

	/**
	 * Method_启动定义参数回调
	 * 
	 * @param what_操作码
	 * @return 请求参数
	 */
	public Map<String, Object> onStartNetParam(int what);

	/**
	 * Method_成功处理回调
	 * 
	 * @param what_操作码
	 * @param result_请求结果字符串
	 */
	public void onSuccess(int what, String result, boolean hashCache);

	/**
	 * Method_成功处理回调
	 * 
	 * @param what_操作码
	 * @param result_请求结果流
	 * @param cookies_请求结果
	 *            Cookie 信息
	 */
	public void onSuccess(int what, InputStream result, HashMap<String, String> cookies, boolean hashCache);

	/**
	 * Method_错误处理回调
	 * 
	 * @param what_操作码
	 * @param e_错误码
	 * @param message_错误信息
	 */
	public void onError(int what, int e, String message);

}
