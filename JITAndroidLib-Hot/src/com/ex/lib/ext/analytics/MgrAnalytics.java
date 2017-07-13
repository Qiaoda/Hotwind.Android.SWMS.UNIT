package com.ex.lib.ext.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName: MgrAnalytics
 * @Description: 友盟统计管理者
 * @author Aloneter
 * @date 2014-10-31 上午11:36:07
 * @Version 1.0
 * 
 */
public class MgrAnalytics {

	private static final String PREFERENCE_TAG = "EX_PRE_UMENG_ANALYZE"; // 缓存文件

	private static Activity mActivity; // 上下文
	private static Context mContext;

	/**
	 * 创建者
	 */
	private static class AnalyticsHolder {

		private static final MgrAnalytics mgr = new MgrAnalytics();

	}

	/**
	 * 获取当前对象实例
	 */
	public static MgrAnalytics getInstance(Activity activity) {

		if (mActivity == null) {
			mActivity = activity;
			mContext = activity.getApplicationContext();
		}

		return AnalyticsHolder.mgr;
	}

	/**
	 * Method_初始化
	 */
	public void init() {

		// 开启调试模式
		MobclickAgent.setDebugMode(true);

		// SDK在统计Fragment时，需要关闭Activity自带的页面统计，
		// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
		MobclickAgent.openActivityDurationTrack(false);

		// 注册在线更新
		MobclickAgent.updateOnlineConfig(mContext);
	}

	/**
	 * Method_自定义事件
	 * 
	 * @param eventName
	 * @param key
	 */
	public void event(String eventName, String key) {

		MobclickAgent.onEvent(mContext, eventName, key);
	}

	/**
	 * Method_自定义唯一事件
	 * 
	 * @param eventName
	 * @param key
	 */
	public void uniqueEvent(String eventName, String key) {

		// 检测是否统计过
		SharedPreferences sharedPre = mContext.getSharedPreferences(PREFERENCE_TAG, 0);
		Boolean hasStat = sharedPre.getBoolean(key, false);

		// 若未统计过
		if (!hasStat) {
			// 进行统计
			event(eventName, key);

			// 做标记表示已被统计
			SharedPreferences.Editor editor = sharedPre.edit();
			editor.putBoolean(key, true);
			editor.commit();
		}
	}

}
