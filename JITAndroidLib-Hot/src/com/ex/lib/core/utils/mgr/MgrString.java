/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * @ClassName: MgrString
 * @Description: MgrString 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrString {

	/**
	 * 创建者
	 */
	private static class StringHolder {

		private static final MgrString mgr = new MgrString();
	}

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static MgrString getInstance() {

		return StringHolder.mgr;
	}

	/**
	 * 是否为空
	 * 
	 * @param str
	 * @return
	 */
	public boolean isEmpty(String str) {

		if (str == null || "".equals(str)) {

			return true;
		}

		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);

			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {

				return false;
			}
		}

		return true;
	}

	/**
	 * 是否为数字
	 * 
	 * @param str
	 * @return
	 */
	public boolean isNumber(String str) {

		try {
			Integer.parseInt(str.toString());
		} catch (Exception e) {

			return false;
		}

		return true;
	}

	/**
	 * 是否是今天
	 * 
	 * @param todayStr
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("deprecation")
	public boolean isToday(String todayStr) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date toDayDate = sdf.parse(todayStr);
			Date nowDate = new Date();

			if (nowDate.getYear() == toDayDate.getYear() && nowDate.getMonth() == toDayDate.getMonth() && nowDate.getDate() == toDayDate.getDate()) {

				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 是否匹配正则表达式
	 * 
	 * @param patternStr
	 * @param regStr
	 * @return
	 */
	public boolean isPattern(String patternStr, String regStr) {

		Pattern pattern = Pattern.compile(regStr);
		Matcher matcher = pattern.matcher(patternStr);

		if (matcher.matches()) {

			return true;
		}

		return false;
	}

	/**
	 * 处理为空的字符串
	 * 
	 * @param valStr
	 * @return
	 */
	public String dealEmpty(String valStr) {

		if (isEmpty(valStr)) {

			return "";
		}

		return valStr;
	}

	/**
	 * 获取文件格式大小
	 * 
	 * @param sizeStr
	 * @return
	 */
	public String getFileSize(String sizeStr) {

		long size = Long.parseLong(sizeStr);

		if (size < 0) {

			return "0";
		}
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };

		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	/**
	 * 获取星期内容
	 * 
	 * @param weekInt
	 * @return
	 */
	public String getWeek(int weekInt) {

		switch (weekInt) {
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		case 7:
			return "日";
		}

		return weekInt + "";
	}

	/**
	 * 获取地址域名
	 * 
	 * @param hostStr
	 * @return
	 */
	public String getHost(String hostStr) {

		String result = hostStr;

		int index = hostStr.indexOf('/', 7);

		if (index > 0) {
			result = result.substring(0, index);
		}

		result += "/";

		return result;
	}

	/**
	 * 转换 <br/>
	 * 为 \n
	 * 
	 * @param brStr
	 * @return
	 */
	public String getReplaceBR(String brStr) {

		String[] content = brStr.split("<br/>");
		StringBuffer result = new StringBuffer();

		int n = content.length;

		for (int i = 0; i < n; i++) {
			result.append(content[i]);

			if (i != n - 1) {
				result.append("\n");
			}
		}

		return result.toString();
	}

	/**
	 * 替换 \r \n 为空
	 * 
	 * @param rnStr
	 * @return
	 */
	public String getReplaceRN(String rnStr) {

		String result = rnStr.replace("\n", "").replace("\r", "");

		return result;
	}

	/**
	 * 获取请求参数
	 * 
	 * @param urlStr
	 * @param params
	 * @return
	 */
	public String getUrlParam(String urlStr, Map<String, String> params) {

		Set<String> keys = params.keySet();

		String result = urlStr;

		for (String key : keys) {

			String value = params.get(key);
			result = getUrlParam(result, key, value);
		}

		return result;
	}

	/**
	 * 获取请求参数集合
	 * 
	 * @param urlStr
	 * @param key
	 * @param value
	 * @return
	 */
	public String getUrlParam(String urlStr, String key, String value) {

		if (!urlStr.contains("&" + key + "=") && !urlStr.contains("?" + key + "=")) {

			if (urlStr.indexOf('?') > 0) {
				urlStr += "&" + key + "=" + value;
			} else {
				urlStr += "?" + key + "=" + value;
			}
		}

		return urlStr;
	}

	public String getGenerateUrl(String url, Map<String, String> params) {

		StringBuilder urlBuilder = new StringBuilder(url);

		if (null != params) {
			urlBuilder.append("?");
			Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				String key = param.getKey();
				String value = removeSpecialCharacter(param.getValue());
				urlBuilder.append(key).append('=').append(value);

				if (iterator.hasNext()) {
					urlBuilder.append('&');
				}
			}
		}

		String realUrl = urlEncode(urlBuilder.toString());
		Log.e("url:", realUrl);

		// 去除url编码对JsonArray的影响
		if (realUrl.contains("%22[")) {
			realUrl = realUrl.replace("%22[", "[");
		}
		if (realUrl.contains("]%22")) {
			realUrl = realUrl.replace("]%22", "]");
		}

		return realUrl;
	}

	private String urlEncode(String url) {
		return url.replace("\"", "%22").replace("{", "%7B").replace("}", "%7D").replace(" ", "%20");
	}

	private String removeSpecialCharacter(String content) {
		return content.replace("\"{", "{").replace("}\"", "}").replace("\"[", "[").replace("]\"", "]").replace("]\\\"", "]").replace("\\", "");
	}

}
