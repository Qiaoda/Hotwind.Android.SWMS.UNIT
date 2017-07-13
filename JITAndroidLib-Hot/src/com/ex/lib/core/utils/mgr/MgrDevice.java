/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.ex.lib.R;
import com.ex.lib.ext.utils.OpenUDID;

/**
 * @ClassName: MgrDevice
 * @Description: MgrDevice 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
@SuppressLint("InlinedApi")
public class MgrDevice {

	private static Context mContext; // 上下文

	private TelephonyManager tm; // 手机管理
	private WifiManager wm; // 网络管理
	private PackageManager pm; // 包管理
	private DisplayMetrics dm; // 显示管理
	private ActivityManager am; // 界面管理

	/**
	 * 创建者
	 */
	private static class DeviceHolder {

		private static final MgrDevice mgr = new MgrDevice();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrDevice getInstance(Context context) {

		mContext = context;

		return DeviceHolder.mgr;
	}

	/**
	 * Method_获取手机管理对象
	 * 
	 * @return
	 */
	public TelephonyManager getTM() {

		if (tm == null) {
			tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		}
		return tm;
	}

	/**
	 * Method_获取网络管理对象
	 * 
	 * @return
	 */
	public WifiManager getWM() {

		if (wm == null) {
			wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		}
		return wm;
	}

	/**
	 * Method_获取包管理对象
	 * 
	 * @return
	 */
	public PackageManager getPM() {

		if (pm == null) {
			pm = mContext.getPackageManager();
		}
		return pm;
	}

	/**
	 * Method_获取显示管理对象
	 * 
	 * @return
	 */
	public DisplayMetrics getDM() {

		if (dm == null) {
			dm = new DisplayMetrics();
		}
		return dm;
	}

	/**
	 * Method_获取界面管理对象
	 * 
	 * @return
	 */
	public ActivityManager getAM() {

		if (am == null) {
			am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		}
		return am;
	}

	/**
	 * Method_获取应用名
	 * 
	 * @return
	 */
	public String getContextName() {

		try {
			ApplicationInfo appInfo = getPM().getApplicationInfo(getPackageName(), 0);
			String appName = (String) getPM().getApplicationLabel(appInfo);

			return MgrString.getInstance().dealEmpty(appName);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取语言
	 * 
	 * @return
	 */
	public String getLanguage() {

		Locale l = Locale.getDefault();

		return String.format("%s-%s", l.getLanguage(), l.getCountry());
	}

	/**
	 * Method_获取设备Id
	 * 
	 * @return
	 */
	public String getDeviceId() {

		String deviceId = "";

		try {
			OpenUDID.syncContext(mContext);
			deviceId = OpenUDID.getCorpUDID(mContext.getPackageName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MgrString.getInstance().dealEmpty(deviceId);
	}

	/**
	 * Method_获取设备Id
	 * 
	 * @return
	 */
	public String getDeviceId(String pkg) {

		String deviceId = "";

		try {
			OpenUDID.syncContext(mContext);
			deviceId = OpenUDID.getCorpUDID(pkg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MgrString.getInstance().dealEmpty(deviceId);
	}

	/**
	 * Method_获取 IMEI
	 * 
	 * @return
	 */
	public String getIMEI() {

		try {
			String IMEI = getTM().getDeviceId();

			return MgrString.getInstance().dealEmpty(IMEI);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取 IMSI
	 * 
	 * @return
	 */
	public String getIMSI() {

		try {
			String IMSI = getTM().getSubscriberId();

			return MgrString.getInstance().dealEmpty(IMSI);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取手机号码
	 * 
	 * @return
	 */
	public String getPhoneNumber() {

		try {
			String phoneNo = getTM().getLine1Number();

			return MgrString.getInstance().dealEmpty(phoneNo);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取网络运营商
	 * 
	 * @return
	 */
	public String getNetProvider() {

		try {
			String netWorkOperator = getTM().getNetworkOperator();

			return MgrString.getInstance().dealEmpty(netWorkOperator);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取 SIM
	 * 
	 * @return
	 */
	public String getSIM() {

		try {
			String SIM = getTM().getSimSerialNumber();

			return MgrString.getInstance().dealEmpty(SIM);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取网络类型
	 * 
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public String getNetWorkType() {

		try {
			int netWorkId = -1;

			netWorkId = getTM().getNetworkType();

			HashMap<Integer, String> ntMap = new HashMap<Integer, String>();

			ntMap.put(TelephonyManager.NETWORK_TYPE_UNKNOWN, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_unknown));
			ntMap.put(TelephonyManager.NETWORK_TYPE_GPRS, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_gprs));
			ntMap.put(TelephonyManager.NETWORK_TYPE_EDGE, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_edge));
			ntMap.put(TelephonyManager.NETWORK_TYPE_UMTS, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_umts));
			ntMap.put(TelephonyManager.NETWORK_TYPE_CDMA, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_cdma));
			ntMap.put(TelephonyManager.NETWORK_TYPE_EVDO_0, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_evdo0));
			ntMap.put(TelephonyManager.NETWORK_TYPE_EVDO_A, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_evdoa));
			ntMap.put(TelephonyManager.NETWORK_TYPE_1xRTT, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_1xrtt));
			ntMap.put(TelephonyManager.NETWORK_TYPE_HSDPA, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_hsdpa));
			ntMap.put(TelephonyManager.NETWORK_TYPE_HSUPA, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_hsupa));
			ntMap.put(TelephonyManager.NETWORK_TYPE_HSPA, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_hspa));
			ntMap.put(TelephonyManager.NETWORK_TYPE_IDEN, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_iden));
			ntMap.put(TelephonyManager.NETWORK_TYPE_EVDO_B, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_evdob));
			ntMap.put(TelephonyManager.NETWORK_TYPE_LTE, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_lte));
			ntMap.put(TelephonyManager.NETWORK_TYPE_EHRPD, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_ehrpd));
			ntMap.put(TelephonyManager.NETWORK_TYPE_HSPAP, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_net_type_hspap));

			String networkType = ntMap.get(netWorkId);

			return MgrString.getInstance().dealEmpty(networkType);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取手机类型
	 * 
	 * @return
	 */
	@SuppressLint("UseSparseArrays")
	public String getPhoneType() {

		try {
			int phoneTypeId = -1;
			phoneTypeId = getTM().getPhoneType();

			Map<Integer, String> ptMap = new HashMap<Integer, String>();

			ptMap.put(TelephonyManager.PHONE_TYPE_NONE, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_phone_type_none));
			ptMap.put(TelephonyManager.PHONE_TYPE_GSM, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_phone_type_gsm));
			ptMap.put(TelephonyManager.PHONE_TYPE_CDMA, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_phone_type_cmda));
			ptMap.put(TelephonyManager.PHONE_TYPE_SIP, MgrAndroid.getInstance(mContext).string(R.string.ex_str_device_phone_type_sip));

			String phoneType = ptMap.get(phoneTypeId);

			return MgrString.getInstance().dealEmpty(phoneType);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取手机模式
	 * 
	 * @return
	 */
	public String getProductModel() {

		try {
			String productModel = android.os.Build.MODEL;

			return MgrString.getInstance().dealEmpty(productModel);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取系统版本
	 * 
	 * @return
	 */
	public String getOSVersion() {

		try {
			String osVer = android.os.Build.VERSION.RELEASE;

			return MgrString.getInstance().dealEmpty(osVer);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取 SDK 版本
	 * 
	 * @return
	 */
	public int getSDKVersion() {

		try {
			int sdkVersion = android.os.Build.VERSION.SDK_INT;

			return sdkVersion;
		} catch (Exception e) {
			e.printStackTrace();

			return -1;
		}

	}

	/**
	 * Method_获取 Mac 地址
	 * 
	 * @return
	 */
	public String getMacAddress() {

		try {
			String macAddress = getWM().getConnectionInfo().getMacAddress();

			return MgrString.getInstance().dealEmpty(macAddress);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取 IP 地址
	 * 
	 * @return
	 */
	public int getIpAddress() {

		try {
			int ipAddress = getWM().getConnectionInfo().getIpAddress();

			return ipAddress;
		} catch (Exception e) {
			e.printStackTrace();

			return -1;
		}
	}

	/**
	 * Method_获取版本名
	 * 
	 * @return
	 */
	public String getVersionName() {

		try {
			PackageInfo info = getPM().getPackageInfo(getPackageName(), 0);
			String verName = info.versionName;

			return MgrString.getInstance().dealEmpty(verName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取版本号
	 * 
	 * @return
	 */
	public String getVersionCode() {

		try {
			PackageInfo info = getPM().getPackageInfo(getPackageName(), 0);
			String verCode = info.versionCode + "";

			return MgrString.getInstance().dealEmpty(verCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();

			return "-1";
		}
	}

	/**
	 * Method_获取包名
	 * 
	 * @return
	 */
	public String getPackageName() {

		try {
			String pkgName = mContext.getPackageName();

			return MgrString.getInstance().dealEmpty(pkgName);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取分辨率内容
	 * 
	 * @return
	 */
	public String getResolution() {

		try {
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(getDM());

			String resolution = getDM().widthPixels + "" + getDM().heightPixels;

			return MgrString.getInstance().dealEmpty(resolution);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_获取宽度
	 * 
	 * @return
	 */
	public int getResolutionWidth() {

		try {
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(getDM());

			int width = getDM().widthPixels;

			return width;
		} catch (Exception e) {
			e.printStackTrace();

			return -1;
		}
	}

	/**
	 * Method_获取高度
	 * 
	 * @return
	 */
	public int getResolutionHeight() {

		try {
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(getDM());

			int height = getDM().heightPixels;

			return height;
		} catch (Exception e) {
			e.printStackTrace();

			return -1;
		}
	}

	/**
	 * Method_获取 Meta 值
	 * 
	 * @param metakey
	 * @return
	 */
	public String getMetaValue(String metakey) {

		Bundle meta = null;
		String value = null;

		if (mContext == null || MgrString.getInstance().isEmpty(metakey)) {

			return "";
		}

		try {
			ApplicationInfo ai = getPM().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);

			if (ai != null) {
				meta = ai.metaData;
			}
			if (meta != null) {
				value = meta.getString(metakey);
			}

			return MgrString.getInstance().dealEmpty(value);
		} catch (Exception e) {
			e.printStackTrace();

			return "";
		}
	}

	/**
	 * Method_是否在后台运行
	 * 
	 * @return
	 */
	public boolean isBackground() {

		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();

		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(mContext.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					// 后台运行
					return true;
				} else {
					// 前台运行
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * Method_是否处在休眠状态
	 * 
	 * @return
	 */
	public boolean isSleeping() {

		KeyguardManager kgMgr = (KeyguardManager) mContext.getSystemService(Context.KEYGUARD_SERVICE);

		boolean isSleeping = kgMgr.inKeyguardRestrictedInputMode();

		return isSleeping;
	}

	/**
	 * Method_是否存在 App
	 * 
	 * @param pkgName
	 * @return
	 */
	public boolean isExitsApp(String pkgName) {

		if (MgrString.getInstance().isEmpty(pkgName)) {

			return false;
		}

		try {
			getPM().getPackageInfo(pkgName, 0);

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Method_是否是系统 App
	 * 
	 * @param pkgName
	 * @return
	 */
	public boolean isSysApp(String pkgName) {

		if (MgrString.getInstance().isEmpty(pkgName)) {

			return false;
		}

		try {
			ApplicationInfo appInfo = getPM().getApplicationInfo(pkgName, 0);

			if ((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {

				return false;
			} else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
	}

	/**
	 * Method_检查是否存在 SD 卡
	 * 
	 * @return
	 */
	public boolean checkSDcard() {

		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * Method_获取签名字符串
	 * 
	 * @param pkgName
	 * @return
	 */
	public String getSign(String pkgName) {

		try {
			PackageInfo pis = mContext.getPackageManager().getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);

			return hexdigest(pis.signatures[0].toByteArray());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Method_转换签名字符串
	 * 
	 * @param paramArrayOfByte
	 * @return
	 */
	private String hexdigest(byte[] paramArrayOfByte) {

		final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };

		try {

			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(paramArrayOfByte);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];

			for (int i = 0, j = 0;; i++, j++) {
				if (i >= 16) {

					return new String(arrayOfChar);
				}

				int k = arrayOfByte[i];
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				arrayOfChar[++j] = hexDigits[(k & 0xF)];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

}
