/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.ex.lib.R;
import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.ible.ExNetIble;
import com.ex.lib.core.ible.ExReceiverIble;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrNet;
import com.ex.lib.core.utils.mgr.MgrString;
import com.ex.lib.ext.widget.swipbacklayout.app.SwipeBackActivity;
import com.ex.lib.ext.xutils.ViewUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @ClassName: ExBaseAcvitiy
 * @Description: Ex Base Activity
 * @author Aloneter
 * @date 2014-10-24 下午5:07:37
 * @Version 1.0
 * 
 */
@SuppressLint("HandlerLeak")
public abstract class ExBaseSwipeBackAcvitiy extends SwipeBackActivity {

	/**
	 * FINAL_显示加载框
	 */
	public static final int LOADING_DIALOG_SHOW = 100;
	/**
	 * FINAL_隐藏加载框
	 */
	public static final int LOADING_DIALOG_DISS = 101;
	/**
	 * FINAL_GET 数据请求
	 */
	public static final int NET_METHOD_GET = 102;
	/**
	 * FINAL_POST 数据请求
	 */
	public static final int NET_METHOD_POST = 103;
	/**
	 * FINAL_POST_OBJECT 数据请求
	 */
	public static final int NET_METHOD_POST_OBJECT = 104;

	private ExNetIble mNetIble; // 网络请求接口
	private ExReceiverIble mReceiverIble; // 广播处理接口

	protected ExBaseSwipeBackAcvitiy mActivity;
	protected Context mContext;
	protected Gson mGson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
		super.onCreate(savedInstanceState);

		// 绑定布局
		setContentView(exInitLayout());
		// 初始化全局变量
		mActivity = this;
		mContext = this.getApplicationContext();
		mGson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		// 初始化 XUtils 注解绑定控件
		ViewUtils.inject(this);
		// 实现类处理传入数据
		exInitBundle();
		// 实现类处理控件数据绑定
		exInitView();
		// 实现处理初始化完成之后
		exInitAfter();
		// 注册本地广播
		regiesterReceiver();
		// 添加 Activity 管理
		Ex.Activity(mContext).add(mActivity);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 注销本地广播
		unregiesterReceiver();
		// 移除 Handler 所有回调和消息
		mHandler.removeCallbacksAndMessages(null);
		// 终止当前 Activity
		// Ex.Activity(mContext).finish();
	}

	/**
	 * Method_初始化操作接口
	 * 
	 * @param netIble_网络请求处理接口
	 * @param receiverIble_广播处理
	 * @param method_网络请求方式
	 */
	protected void initIble(ExNetIble netIble, ExReceiverIble receiverIble) {

		mNetIble = netIble;
		mReceiverIble = receiverIble;
	}

	/**
	 * Method_启动任务请求数据
	 * 
	 * @param url_请求主地址信息
	 * @param what_请求标识码
	 * @param method_网络请求方式
	 * @param isCache_是否使用缓存
	 */
	protected void startTask(String url, int what, int method, boolean isCache) {

		startTask(url, what, true, method, isCache);
	}

	/**
	 * Method_启动任务请求数据
	 * 
	 * @param url_请求主地址信息
	 * @param what_请求标识码
	 * @param isShow_是否显示加载框
	 * @param method_网络请求方式
	 * @param isCache_是否使用缓存
	 */
	protected void startTask(final String url, final int what, final boolean isShow, final int method, final boolean isCache) {

		// 判断请求地址是否为空
		if (Ex.String().isEmpty(url)) {
			Ex.Toast(mContext).show(R.string.ex_str_param_not_empty);

			return;
		}
		// 判断网络是否可以使用
		if (!MgrNet.getInstance(mContext).isConnected() && !MgrNet.getInstance(mContext).isConnected()) {
			Ex.Toast(mContext).show(R.string.ex_str_net_no);

			return;
		}
		// 判断当前是否显示加载框
		if (isShow && !isCache) {
			Ex.Dialog(mActivity).showProgressDialog("", Ex.Android(mContext).string(R.string.ex_str_loading));
		}
		// 接口回调回去操作参数
		Map<String, String> params = mNetIble.onStart(what);
		Map<String, Object> paramsNet = mNetIble.onStartNetParam(what);
		// 缓存使用的 key
		String caheKey = "";
		// 判断使用缓存的类型数据
		if (method == ExBaseSwipeBackAcvitiy.NET_METHOD_POST_OBJECT) {
			// 如果使用对象请求的时候，需要使用用户自定义 key
			String key = (String) paramsNet.get(ExNetIble.NET_PARAM_CACHE_KEY);

			caheKey = Ex.MD5().getMD5(key);
		} else {
			// 正常情况下使用请求 url 和请求参数组成的 key
			caheKey = Ex.MD5().getMD5(MgrString.getInstance().getGenerateUrl(url, params));
		}

		ExRequestCallback requestCallback = new ExRequestCallback() {

			@Override
			public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {

				// 请求结果
				String result = "";
				// 判断结果流是否为空
				if (inStream != null) {
					// 转换流对象
					result = Ex.T().getInStream2Str(inStream);
					Log.e("result--", result);
				}
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = inStream;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", cookies);
				data.putString("result", result);
				data.putBoolean("cache", false);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);
			}

			@Override
			public void onError(int statusCode, ExException e) {

				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = statusCode;
				// 请求结果
				Bundle data = new Bundle();
				data.putString("result", e.getMessage());
				data.putBoolean("isShow", isShow);

				msg.setData(data);
				// 发送消息
				mHandler.sendMessage(msg);
			}
		};

		if (isCache && !Ex.String().isEmpty(caheKey)) {
			String result = Ex.Cache(mContext).getAsString(caheKey);

			if (Ex.String().isEmpty(result)) {
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = null;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", null);
				data.putString("result", result);
				data.putBoolean("cache", true);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);

				return;
			}
		}

		// 启动网络请求
		if (method == ExBaseAcvitiy.NET_METHOD_GET) {
			Ex.Net(mContext).sendAsyncGet(url, params, requestCallback);
		} else if (method == ExBaseAcvitiy.NET_METHOD_POST) {
			Ex.Net(mContext).sendAsyncPost(url, params, requestCallback);
		} else {
			Object message = paramsNet.get(ExNetIble.NET_PARAM_OBJECT);
			String cookieString = (String) paramsNet.get(ExNetIble.NET_PARAM_COOKIE_STR);

			Ex.Net(mContext).sendAsyncPostWithEnity(url, message, cookieString, requestCallback);
		}
	}

	/**
	 * NEW_消息处理对象
	 */
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			// 获取结果值
			String result = msg.getData().getString("result");
			boolean isShow = msg.getData().getBoolean("isShow");

			// 获取操作码
			int what = msg.what;
			// 获取请求结果码
			int arg2 = msg.arg2;
			// 判断对象是否销毁
			if (mActivity == null || mContext == null) {

				return;
			}
			// 回调请求结果
			if (arg2 == 0) {
				boolean cache = msg.getData().getBoolean("cache");
				// 判断使用缓存处理结果
				if (cache) {
					mNetIble.onSuccess(what, result, cache);
					mNetIble.onSuccess(what, null, null, cache);
				} else {
					HashMap<String, String> cookies = (HashMap<String, String>) msg.getData().getSerializable("cookies");
					InputStream inStream = (InputStream) msg.obj;

					mNetIble.onSuccess(what, result, cache);
					mNetIble.onSuccess(what, inStream, cookies, cache);
				}
			} else {
				mNetIble.onError(what, arg2, result);
			}
			// 判断是否显示对话框
			if (isShow) {
				Ex.Dialog(mContext).dismissProgressDialog();
			}
		}
	};
	
	/**
	 * Method_启动任务请求数据
	 * 
	 * @param url_请求主地址信息
	 * @param what_请求标识码
	 * @param isShow_是否显示加载框
	 * @param method_网络请求方式
	 * @param isCache_是否使用缓存
	 */
	public void startLongTask(final String url, final int what, final boolean isShow, final int method, final boolean isCache, int mRequestTime, int mOutTime) {

		// 判断请求地址是否为空
		if (Ex.String().isEmpty(url)) {
			Ex.Toast(mContext).show(R.string.ex_str_param_not_empty);

			return;
		}
		// 判断网络是否可以使用
		if (!MgrNet.getInstance(mContext).isConnected() && !MgrNet.getInstance(mContext).isConnected()) {
			Ex.Toast(mContext).show(R.string.ex_str_net_no);

			return;
		}
		// 判断当前是否显示加载框
		if (isShow && !isCache) {
//			Ex.Dialog(mActivity).showProgressDialog("", Ex.Android(mContext).string(R.string.ex_str_loading));
			Ex.Dialog(mActivity).showProgressDialog("", "提交数据中，需等待1-2分钟，请勿做别的操作");
		}
		// 接口回调回去操作参数
		Map<String, String> params = mNetIble.onStart(what);
		Map<String, Object> paramsNet = mNetIble.onStartNetParam(what);
		// 缓存使用的 key
		String caheKey = "";
		// 判断使用缓存的类型数据
		if (method == ExBaseAcvitiy.NET_METHOD_POST_OBJECT) {
			// 如果使用对象请求的时候，需要使用用户自定义 key
			String key = (String) paramsNet.get(ExNetIble.NET_PARAM_CACHE_KEY);

			caheKey = Ex.MD5().getMD5(key);
		} else {
			// 正常情况下使用请求 url 和请求参数组成的 key
			caheKey = Ex.MD5().getMD5(MgrString.getInstance().getGenerateUrl(url, params));
		}

		ExRequestCallback requestCallback = new ExRequestCallback() {

			@Override
			public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {

				// 请求结果
				String result = "";
				// 判断结果流是否为空
				if (inStream != null) {
					// 转换流对象
					result = Ex.T().getInStream2Str(inStream);
					Log.e("result--", result);
				}
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = inStream;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", cookies);
				data.putString("result", result);
				data.putBoolean("cache", false);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);
			}

			@Override
			public void onError(int statusCode, ExException e) {

				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = statusCode;
				// 请求结果
				Bundle data = new Bundle();
				data.putString("result", e.getMessage());
				data.putBoolean("isShow", isShow);

				msg.setData(data);
				// 发送消息
				mHandler.sendMessage(msg);
			}
		};

		if (isCache && !Ex.String().isEmpty(caheKey)) {
			String result = Ex.Cache(mContext).getAsString(caheKey);

			if (Ex.String().isEmpty(result)) {
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = null;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", null);
				data.putString("result", result);
				data.putBoolean("cache", true);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);

				return;
			}
		}

		// 启动网络请求
		if (method == ExBaseAcvitiy.NET_METHOD_GET) {
			Ex.Net(mContext).sendAsyncGet(url, params, requestCallback);
		} else if (method == ExBaseAcvitiy.NET_METHOD_POST) {
			Ex.Net(mContext).sendLongAsyncPost(url, params, mRequestTime, mOutTime, requestCallback);
		} else {
			Object message = paramsNet.get(ExNetIble.NET_PARAM_OBJECT);
			String cookieString = (String) paramsNet.get(ExNetIble.NET_PARAM_COOKIE_STR);

			Ex.Net(mContext).sendAsyncPostWithEnity(url, message, cookieString, requestCallback);
		}
	}
	
	protected void startJsonLongTask(final String url, final int what, final boolean isShow, final int method, final String postData, final boolean isCache, int mRequestTime, int mOutTime) {
		// 判断请求地址是否为空
		if (Ex.String().isEmpty(url)) {
			Ex.Toast(mContext).show(R.string.ex_str_param_not_empty);

			return;
		}
		// 判断网络是否可以使用
		if (!MgrNet.getInstance(mContext).isConnected() && !MgrNet.getInstance(mContext).isConnected()) {
			Ex.Toast(mContext).show(R.string.ex_str_net_no);

			return;
		}
		// 判断当前是否显示加载框
		if (isShow && !isCache) {
			Ex.Dialog(mActivity).showProgressDialog("", "提交数据中，需等待1-2分钟，请勿做别的操作");
//			Ex.Dialog(mActivity).showProgressDialog("", Ex.Android(mContext).string(R.string.ex_str_loading));
		}
		
		// 接口回调回去操作参数
		Map<String, String> params = mNetIble.onStart(what);
		Map<String, Object> paramsNet = mNetIble.onStartNetParam(what);
		// 缓存使用的 key
		String caheKey = "";
		// 判断使用缓存的类型数据
		if (method == ExBaseAcvitiy.NET_METHOD_POST_OBJECT) {
			// 如果使用对象请求的时候，需要使用用户自定义 key
			String key = (String) paramsNet.get(ExNetIble.NET_PARAM_CACHE_KEY);

			caheKey = Ex.MD5().getMD5(key);
		} else {
			// 正常情况下使用请求 url 和请求参数组成的 key
			caheKey = Ex.MD5().getMD5(MgrString.getInstance().getGenerateUrl(url, params));
		}

		ExRequestCallback requestCallback = new ExRequestCallback() {

			@Override
			public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {

				// 请求结果
				String result = "";
				// 判断结果流是否为空
				if (inStream != null) {
					// 转换流对象
					result = Ex.T().getInStream2Str(inStream);
					Log.e("result--", result);
				}
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = inStream;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", cookies);
				data.putString("result", result);
				data.putBoolean("cache", false);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);
			}

			@Override
			public void onError(int statusCode, ExException e) {

				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = statusCode;
				// 请求结果
				Bundle data = new Bundle();
				data.putString("result", e.getMessage());
				data.putBoolean("isShow", isShow);

				msg.setData(data);
				// 发送消息
				mHandler.sendMessage(msg);
			}
		};

		if (isCache && !Ex.String().isEmpty(caheKey)) {
			String result = Ex.Cache(mContext).getAsString(caheKey);

			if (Ex.String().isEmpty(result)) {
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = null;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", null);
				data.putString("result", result);
				data.putBoolean("cache", true);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);

				return;
			}
		}

		// 启动网络请求
		if (method == ExBaseAcvitiy.NET_METHOD_GET) {
//			Ex.Net(mContext).sendJsonAsyncPost(url, postData, requestCallback);
			Ex.Net(mContext).sendJsonLongAsyncPost(url, postData, mRequestTime, mOutTime, requestCallback);
//			Ex.Net(mContext).sendLongAsyncPost(url, params, mRequestTime, mOutTime, requestCallback);
		} else if (method == ExBaseAcvitiy.NET_METHOD_POST) {
//			Ex.Net(mContext).sendJsonAsyncPost(url, postData, requestCallback);
//			Ex.Net(mContext).sendLongAsyncPost(url, params, mRequestTime, mOutTime, requestCallback);
			Ex.Net(mContext).sendJsonLongAsyncPost(url, postData, mRequestTime, mOutTime, requestCallback);
		} else {
			Object message = paramsNet.get(ExNetIble.NET_PARAM_OBJECT);
			String cookieString = (String) paramsNet.get(ExNetIble.NET_PARAM_COOKIE_STR);
			Ex.Net(mContext).sendAsyncPostWithEnity(url, message, cookieString, requestCallback);
		}
	}
	
	protected void startJsonTask(final String url, final int what, final boolean isShow, final int method, final String postData, final boolean isCache) {
		// 判断请求地址是否为空
		if (Ex.String().isEmpty(url)) {
			Ex.Toast(mContext).show(R.string.ex_str_param_not_empty);

			return;
		}
		// 判断网络是否可以使用
		if (!MgrNet.getInstance(mContext).isConnected() && !MgrNet.getInstance(mContext).isConnected()) {
			Ex.Toast(mContext).show(R.string.ex_str_net_no);

			return;
		}
		// 判断当前是否显示加载框
		if (isShow && !isCache) {
			Ex.Dialog(mActivity).showProgressDialog("", Ex.Android(mContext).string(R.string.ex_str_loading));
		}
		
		// 接口回调回去操作参数
		Map<String, String> params = mNetIble.onStart(what);
		Map<String, Object> paramsNet = mNetIble.onStartNetParam(what);
		// 缓存使用的 key
		String caheKey = "";
		// 判断使用缓存的类型数据
		if (method == ExBaseAcvitiy.NET_METHOD_POST_OBJECT) {
			// 如果使用对象请求的时候，需要使用用户自定义 key
			String key = (String) paramsNet.get(ExNetIble.NET_PARAM_CACHE_KEY);

			caheKey = Ex.MD5().getMD5(key);
		} else {
			// 正常情况下使用请求 url 和请求参数组成的 key
			caheKey = Ex.MD5().getMD5(MgrString.getInstance().getGenerateUrl(url, params));
		}

		ExRequestCallback requestCallback = new ExRequestCallback() {

			@Override
			public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {

				// 请求结果
				String result = "";
				// 判断结果流是否为空
				if (inStream != null) {
					// 转换流对象
					result = Ex.T().getInStream2Str(inStream);
					Log.e("result--", result);
				}
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = inStream;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", cookies);
				data.putString("result", result);
				data.putBoolean("cache", false);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);
			}

			@Override
			public void onError(int statusCode, ExException e) {

				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = statusCode;
				// 请求结果
				Bundle data = new Bundle();
				data.putString("result", e.getMessage());
				data.putBoolean("isShow", isShow);

				msg.setData(data);
				// 发送消息
				mHandler.sendMessage(msg);
			}
		};

		if (isCache && !Ex.String().isEmpty(caheKey)) {
			String result = Ex.Cache(mContext).getAsString(caheKey);

			if (Ex.String().isEmpty(result)) {
				// 创建消息对象
				Message msg = mHandler.obtainMessage();
				// 传入操作码
				msg.what = what;
				// 请求结果码
				msg.arg2 = 0;
				// 请求结果
				msg.obj = null;
				// 请求结果参数
				Bundle data = new Bundle();
				data.putSerializable("cookies", null);
				data.putString("result", result);
				data.putBoolean("cache", true);
				data.putBoolean("isShow", isShow);

				msg.setData(data);

				// 发送消息
				mHandler.sendMessage(msg);

				return;
			}
		}

		// 启动网络请求
		if (method == ExBaseAcvitiy.NET_METHOD_GET) {
			Ex.Net(mContext).sendJsonAsyncPost(url, postData, requestCallback);
		} else if (method == ExBaseAcvitiy.NET_METHOD_POST) {
			Ex.Net(mContext).sendJsonAsyncPost(url, postData, requestCallback);
		} else {
			Object message = paramsNet.get(ExNetIble.NET_PARAM_OBJECT);
			String cookieString = (String) paramsNet.get(ExNetIble.NET_PARAM_COOKIE_STR);
			Ex.Net(mContext).sendAsyncPostWithEnity(url, message, cookieString, requestCallback);
		}
	}

	/**
	 * NEW_广播处理对象
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			// 判断对象是否销毁
			if (mActivity == null) {

				return;
			}
			// 回调广播结果
			mReceiverIble.onReceiver(intent);
		}
	};

	/**
	 * Method_注册本地广播
	 */
	private void regiesterReceiver() {

		try {
			IntentFilter intentFilter = new IntentFilter();

			// 获取设置 Action 数据对Action进行绑定
			String[] actions = exInitReceiver();

			if (actions != null) {
				for (int i = 0; i < actions.length; i++) {
					intentFilter.addAction(actions[i]);
				}

				// 注册广播
				mContext.registerReceiver(mReceiver, intentFilter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method_注销本地广播
	 */
	private void unregiesterReceiver() {

		try {
			// 获取设置 Action 数据对Action进行绑定
			String[] actions = exInitReceiver();

			if (mReceiver != null && actions != null) {
				mContext.unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method_初始化布局 ：对展示布局进行设置
	 * 
	 * @return 布局资源 ID
	 */
	protected abstract int exInitLayout();

	/**
	 * Method_初始化传入参数 ：处理进入之前传入的数据
	 */
	protected abstract void exInitBundle();

	/**
	 * Method_初始化控件参数： 在该方法中，可以对已绑定的控件数据初始化
	 */
	protected abstract void exInitView();

	/**
	 * Method_初始化之后： 在基础数据初始化完成之后可以使用该方法
	 */
	protected abstract void exInitAfter();

	/**
	 * Method_初始化广播处理 Action： 注意传入数据使用全局常量管理
	 * 
	 * @return 处理 Action 数据
	 */
	protected abstract String[] exInitReceiver();

}
