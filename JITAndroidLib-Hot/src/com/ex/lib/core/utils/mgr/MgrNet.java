/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;

/**
 * @ClassName: MgrNet
 * @Description: MgrNet 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */

public class MgrNet {

	private int mRequestTime = 10 * 1000; // 请求时间
	private int mOutTime = 5*60 * 1000; // 请求超时时间

	private static Context mContext; // 上下文

	/**
	 * 创建者
	 */
	private static class NetHolder {

		private static final MgrNet mgr = new MgrNet();
	}

	/**
	 * 获取当前实例对象
	 * 
	 * @param context
	 * @return
	 */
	public static MgrNet getInstance(Context context) {

		mContext = context;

		return NetHolder.mgr;
	}

	/**
	 * Method_设置请求时长 （默认：10 * 1000）
	 * 
	 * @param requestTime_时间
	 */
	public void setRequestTime(int requestTime) {

		mRequestTime = requestTime;
	}

	/**
	 * Method_设置超时时长 （默认：30 * 1000）
	 * 
	 * @param outTime_时间
	 */
	public void setOutTime(int outTime) {

		mOutTime = outTime;
	}

	/**
	 * Method_网络是否连接
	 * 
	 * @return 结果
	 */
	public boolean isConnected() {

		if (mContext == null) {
			return false;
		}
		ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity == null) {

			return false;
		} else {
			NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Method_判断WIFI网络是否可用
	 * 
	 * @return 结果
	 */
	public boolean isConnectedByWifi() {

		return isConnecteByType(ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * Method_判断MOBILE网络是否可用
	 * 
	 * @return 结果
	 */
	public boolean isConnectedByMobile() {

		return isConnecteByType(ConnectivityManager.TYPE_MOBILE);
	}

	/**
	 * Method_根据网络类型判断是否可用
	 * 
	 * @param networkType_网络类型
	 * @return 结果
	 */
	private boolean isConnecteByType(int networkType) {

		if (mContext != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(networkType);

			if (mWiFiNetworkInfo != null) {

				return mWiFiNetworkInfo.isConnected();
			}
		}

		return false;
	}

	/**
	 * Method_发送异步数据请求 GET
	 * 
	 * @param url_请求地址
	 * @param callback_回调函数
	 */
	public void sendAsyncGet(final String url, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendGet(url, callback);
			}
		});
	}

	/**
	 * Method_发送同步数据请求 GET
	 * 
	 * @param url_请求地址
	 * @param callback_回调函数
	 */
	public void sendSyncGet(String url, ExRequestCallback callback) {

		sendGet(url, callback);
	}

	/**
	 * Method_发送异步数据请求 GET
	 * 
	 * @param url_请求地址
	 * @param callback_回调函数
	 */
	public void sendAsyncGet(final String url, final Map<String, String> params, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendSyncGet(url, params, callback);
			}
		});
	}

	/**
	 * Method_发送同步数据请求 GET
	 * 
	 * @param url_请求地址
	 * @param callback_回调函数
	 */
	public void sendSyncGet(String url, Map<String, String> params, ExRequestCallback callback) {

		url = MgrString.getInstance().getGenerateUrl(url, params);

		sendGet(url, callback);
	}

	/**
	 * Method_发送异步请求 POST
	 * 
	 * @param url_请求地址
	 * @param params_参数信息
	 * @param callback_回调函数
	 */
	public void sendAsyncPost(final String url, final Map<String, String> params, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendPost(url, params, callback);
			}
		});
	}
	
	/**
	 * Method_发送异步请求 POST
	 * 
	 * @param url_请求地址
	 * @param params_参数信息
	 * @param callback_回调函数
	 */
	public void sendLongAsyncPost(final String url, final Map<String, String> params, final int mRequestTime, final int mOutTime, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendLongPost(url, params, mRequestTime, mOutTime, callback);
			}
		});
	}
	
	/**
	 * Method_发送异步请求 POST
	 * 
	 * @param url_请求地址
	 * @param post json格式字符串
	 * @param callback_回调函数
	 */
	public void sendJsonAsyncPost(final String url, final String postData, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendPost(url, postData, callback);
			}
		});
	}
	
	/**
	 * Method_发送异步请求 POST
	 * 
	 * @param url_请求地址
	 * @param post json格式字符串
	 * @param callback_回调函数
	 */
	public void sendJsonLongAsyncPost(final String url, final String postData, final int mRequestTime, final int mOutTime, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendPost(url, postData, mRequestTime, mOutTime, callback);
			}
		});
	}

	/**
	 * Method_发送同步请求 POST
	 * 
	 * @param url_请求地址
	 * @param params_请求参数
	 * @param callback_回调函数
	 */
	public void sendSyncPost(String url, Map<String, String> params, ExRequestCallback callback) {

		sendPost(url, params, callback);
	}

	/**
	 * Method_发送异步请求 PB
	 * 
	 * @param url_请求地址
	 * @param message_对象
	 * @param cookieString_cookie
	 *            信息
	 * @param callback_回调函数
	 */
	public void sendAsyncPostWithEnity(final String url, final Object message, final String cookieString, final ExRequestCallback callback) {

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				sendPostWithEnity(url, message, cookieString, callback);
			}
		});
	}

	/**
	 * Method_发送同步请求 PB
	 * 
	 * @param url_请求地址
	 * @param message_对象
	 * @param cookieString_cookie
	 *            信息
	 * @param callback_回调函数
	 */
	public void sendSyncPostWithEnity(String url, Object message, String cookieString, ExRequestCallback callback) {

		sendPostWithEnity(url, message, cookieString, callback);
	}

	/**
	 * Method_获取 COOKIES
	 * 
	 * @param httpResponse_响应参数
	 * @return 结果
	 */
	public HashMap<String, String> getCookies(HttpResponse httpResponse) {

		HashMap<String, String> responseCookies = new HashMap<String, String>();
		Header[] headers = httpResponse.getHeaders("Set-Cookie");

		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				String cookie = headers[i].getValue();
				String[] cookievalues = cookie.split(";");
				for (int j = 0; j < cookievalues.length; j++) {
					String[] keyPair = cookievalues[j].split("=");
					String key = keyPair[0].trim();
					String value = keyPair.length > 1 ? keyPair[1].trim() : "";
					responseCookies.put(key, value);
				}
			}
		}

		return responseCookies;
	}

	/**
	 * Method_获取 COOKIES 字符串
	 * 
	 * @param cookies_信息
	 * @return 结果
	 */
	@SuppressWarnings("rawtypes")
	public String getCookiesString(HashMap<String, String> cookies) {

		StringBuilder sb = new StringBuilder();

		if (cookies != null) {
			Iterator iter = cookies.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();

				String key = entry.getKey().toString();
				String val = entry.getValue().toString();
				sb.append(key);
				sb.append("=");
				sb.append(val);
				sb.append(";");
			}
		}

		return sb.toString();
	}

	/**
	 * Method_发送 GET 请求
	 * 
	 * @param url_请求地址
	 * @param callback_回调函数
	 */
	public void sendGet(String url, ExRequestCallback callback) {

		Ex.Log().e("test ex get====> url:{" + url + "}");

		HttpClient httpClient = newInstance();
		HttpGet httpGet = new HttpGet(url);
		int statusCode = 0;

		try {
			HttpResponse response = httpClient.execute(httpGet);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				callback.onSuccess(response.getEntity().getContent(), null);
			} else {
				callback.onError(statusCode, new ExException("Http响应不成功，StatusCode=[" + statusCode + "],URL=" + url));
			}
		} catch (ConnectTimeoutException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_TIMEOUT : statusCode;
			callback.onError(statusCode, new ExException("Http请求超时异常，StatusCode=[" + statusCode + "],URL=" + url + "/" + e.getMessage(), e));
		} catch (IOException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_FAIL : statusCode;
			callback.onError(statusCode, new ExException("Http请求IO异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} catch (Throwable e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_ERROR : statusCode;
			callback.onError(statusCode, new ExException("Http请求异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * Method_发送 POST 请求
	 * 
	 * @param url_请求地址
	 * @param params_参数
	 * @param callback_回调函数
	 */
	public void sendPost(String url, String postData, int mRequestTime, int mOutTime, ExRequestCallback callback) {

		Ex.Log().e("test ex post====> url:{" + url + "}");

		HttpClient httpClient = newInstance(mRequestTime, mOutTime);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", "application/json; charset=utf-8");
		httpPost.setHeader("Accept", "application/json; charset=utf-8");
		int statusCode = 0;
		try {
			httpPost.setEntity(new StringEntity(postData, HTTP.UTF_8));  
			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				callback.onSuccess(response.getEntity().getContent(), null);
			} else {
				callback.onError(statusCode, new ExException("Http响应不成功，StatusCode=[" + statusCode + "],URL=" + url));
			}
		} catch (ConnectTimeoutException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_TIMEOUT : statusCode;
			callback.onError(statusCode, new ExException("Http请求超时异常，StatusCode=[" + statusCode + "],URL=" + url + "/" + e.getMessage(), e));
		} catch (IOException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_FAIL : statusCode;
			callback.onError(statusCode, new ExException("Http请求IO异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} catch (Throwable e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_ERROR : statusCode;
			callback.onError(statusCode, new ExException("Http请求异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Method_发送 POST 请求
	 * 
	 * @param url_请求地址
	 * @param params_参数
	 * @param callback_回调函数
	 */
	public void sendPost(String url, String postData, ExRequestCallback callback) {

		Ex.Log().e("test ex post====> url:{" + url + "}");

		HttpClient httpClient = newInstance();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Content-type", "application/json; charset=utf-8");
		httpPost.setHeader("Accept", "application/json; charset=utf-8");
		int statusCode = 0;
		try {
			httpPost.setEntity(new StringEntity(postData, HTTP.UTF_8));  
			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				callback.onSuccess(response.getEntity().getContent(), null);
			} else {
				callback.onError(statusCode, new ExException("Http响应不成功，StatusCode=[" + statusCode + "],URL=" + url));
			}
		} catch (ConnectTimeoutException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_TIMEOUT : statusCode;
			callback.onError(statusCode, new ExException("Http请求超时异常，StatusCode=[" + statusCode + "],URL=" + url + "/" + e.getMessage(), e));
		} catch (IOException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_FAIL : statusCode;
			callback.onError(statusCode, new ExException("Http请求IO异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} catch (Throwable e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_ERROR : statusCode;
			callback.onError(statusCode, new ExException("Http请求异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * Method_发送 POST 请求
	 * 
	 * @param url_请求地址
	 * @param params_参数
	 * @param callback_回调函数
	 */
	public void sendPost(String url, Map<String, String> params, ExRequestCallback callback) {

		Ex.Log().e("test ex post====> url:{" + url + "}");

		HttpClient httpClient = newInstance();
		HttpPost httpPost = new HttpPost(url);
		int statusCode = 0;

		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>(params.size());

				for (Map.Entry<String, String> entry : params.entrySet()) {
					paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}

				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
				httpPost.setEntity(entity);
			}

			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				callback.onSuccess(response.getEntity().getContent(), null);
			} else {
				callback.onError(statusCode, new ExException("Http响应不成功，StatusCode=[" + statusCode + "],URL=" + url));
			}
		} catch (ConnectTimeoutException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_TIMEOUT : statusCode;
			callback.onError(statusCode, new ExException("Http请求超时异常，StatusCode=[" + statusCode + "],URL=" + url + "/" + e.getMessage(), e));
		} catch (IOException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_FAIL : statusCode;
			callback.onError(statusCode, new ExException("Http请求IO异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} catch (Throwable e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_ERROR : statusCode;
			callback.onError(statusCode, new ExException("Http请求异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * Method_发送 POST 请求
	 * 
	 * @param url_请求地址
	 * @param params_参数
	 * @param callback_回调函数
	 */
	public void sendLongPost(String url, Map<String, String> params, int mRequestTime, int mOutTime, ExRequestCallback callback) {

		Ex.Log().e("test ex post====> url:{" + url + "}");

		HttpClient httpClient = newInstance(mRequestTime, mOutTime);
		HttpPost httpPost = new HttpPost(url);
		int statusCode = 0;

		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>(params.size());

				for (Map.Entry<String, String> entry : params.entrySet()) {
					paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}

				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
				httpPost.setEntity(entity);
			}

			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == HttpStatus.SC_OK) {
				callback.onSuccess(response.getEntity().getContent(), null);
			} else {
				callback.onError(statusCode, new ExException("Http响应不成功，StatusCode=[" + statusCode + "],URL=" + url));
			}
		} catch (ConnectTimeoutException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_TIMEOUT : statusCode;
			callback.onError(statusCode, new ExException("Http请求超时异常，StatusCode=[" + statusCode + "],URL=" + url + "/" + e.getMessage(), e));
		} catch (IOException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_FAIL : statusCode;
			callback.onError(statusCode, new ExException("Http请求IO异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} catch (Throwable e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_ERROR : statusCode;
			callback.onError(statusCode, new ExException("Http请求异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Method_发送 Object 请求
	 * 
	 * @param url_请求地址
	 * @param message_对象
	 * @param cookieString_cookie
	 *            信息
	 * @param callback_回调函数
	 */
	public void sendPostWithEnity(String url, Object message, String cookieString, ExRequestCallback callback) {

		HttpClient httpClient = newInstance();
		HttpPost httpPost = new HttpPost(url);
		int statusCode = 0;

		try {
			ByteArrayEntity entity = new ByteArrayEntity(MgrT.getInstance().getObject2Byte(message));

			httpPost.setEntity(entity);

			httpPost.addHeader("cookie", cookieString);
			httpPost.addHeader("Content-Type", "application/octet-stream");

			HttpResponse response = httpClient.execute(httpPost);
			statusCode = response.getStatusLine().getStatusCode();

			HashMap<String, String> responseCookies = getCookies(response);

			if (statusCode == HttpStatus.SC_OK) {
				callback.onSuccess(response.getEntity().getContent(), responseCookies);
			} else {
				callback.onError(statusCode, new ExException("Http响应不成功，StatusCode=[" + statusCode + "],URL=" + url));
			}
		} catch (ConnectTimeoutException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_TIMEOUT : statusCode;
			callback.onError(statusCode, new ExException("Http请求超时异常，StatusCode=[" + statusCode + "],URL=" + url + "/" + e.getMessage(), e));
		} catch (IOException e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_FAIL : statusCode;
			callback.onError(statusCode, new ExException("Http请求IO异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} catch (Throwable e) {
			statusCode = (statusCode == 0) ? ExRequestCallback.REQUEST_ERROR : statusCode;
			callback.onError(statusCode, new ExException("Http请求异常，StatusCode=[" + statusCode + "],URL=" + url, e));
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Method_获取新 HttpClient 对象
	 * 
	 * @return 请求对象
	 */
	public HttpClient newInstance() {

		BasicHttpParams httpParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(httpParams, mRequestTime);
		HttpConnectionParams.setSoTimeout(httpParams, mOutTime);
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);

		defaultHttpClient.setRedirectHandler(new RedirectHandler() {

			@Override
			public boolean isRedirectRequested(HttpResponse response, HttpContext context) {

				return false;
			}

			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {

				return null;
			}
		});

		return defaultHttpClient;
	}
	
	public HttpClient newInstance(int mRequestTime, int mOutTime) {

		BasicHttpParams httpParams = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(httpParams, mRequestTime);
		HttpConnectionParams.setSoTimeout(httpParams, mOutTime);
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient(httpParams);

		defaultHttpClient.setRedirectHandler(new RedirectHandler() {

			@Override
			public boolean isRedirectRequested(HttpResponse response, HttpContext context) {

				return false;
			}

			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {

				return null;
			}
		});

		return defaultHttpClient;
	}

}
