package com.ex.lib.ext.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.telephony.TelephonyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;

public class OpenUDID {

	private static String _openUdid;

	public final static String TAG = "OpenUDID";
	public final static String PREF_KEY = "openudid";
	public final static String PREFS_NAME = "openudid_prefs";

	private final static boolean _UseImeiFailback = false;
	private final static boolean _UseBlueToothFailback = false;
	private final static boolean LOG = false;

	private static void _debugLog(String lmsg) {
		if (!LOG) {

			return;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("WorldReadableFiles")
	public static void syncContext(Context mContext) {

		if (_openUdid == null) {
			Context openContext = null;

			try {
				openContext = mContext.createPackageContext("net.openudid.android", Context.CONTEXT_IGNORE_SECURITY);
				mContext = openContext;
			} catch (NameNotFoundException e1) {

			}

			SharedPreferences mPreferences = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_WORLD_READABLE);
			String _keyInPref = mPreferences.getString(PREF_KEY, null);

			if (null == _keyInPref) {
				generateOpenUDIDInContext(mContext);
				Editor e = mPreferences.edit();
				e.putString(PREF_KEY, _openUdid);
				e.commit();
			} else {
				_openUdid = _keyInPref;
			}
		}
	}

	public static String getOpenUDIDInContext() {

		return _openUdid;
	}

	public static String getCorpUDID(String corpIdentifier) {

		return Md5(String.format("%s.%s", corpIdentifier, getOpenUDIDInContext()));
	}

	@SuppressLint("DefaultLocale")
	private static void generateOpenUDIDInContext(Context mContext) {

		if (LOG) {
			_debugLog("Generating openUDID");
		}
		generateWifiId(mContext);

		if (null != _openUdid) {

			return;
		}
		String _androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID).toLowerCase();

		if (null != _androidId && _androidId.length() > 14 && !_androidId.equals("9774d56d682e549c")) {
			_openUdid = "ANDROID:" + _androidId;

			return;
		}

		if (_UseImeiFailback) {
			_openUdid = null;
			generateImeiId(mContext);

			if (_openUdid != null) {

				return;
			}
		}

		if (_UseBlueToothFailback) {
			_openUdid = null;
			generateBlueToothId();

			if (_openUdid == null) {
				generateRandomNumber();
			}
		} else {
			generateRandomNumber();
		}

		_debugLog(_openUdid);
		_debugLog("done");
	}

	private static void generateImeiId(Context mContext) {

		try {
			TelephonyManager TelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			String szImei = TelephonyMgr.getDeviceId();

			if (null != szImei && !szImei.substring(0, 3).equals("000")) {
				_openUdid = "IMEI:" + szImei;
			}
		} catch (Exception ex) {

		}
	}

	private static void generateBlueToothId() {

		try {
			BluetoothAdapter m_BluetoothAdapter = null;
			m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			String m_szBTMAC = m_BluetoothAdapter.getAddress();

			if (null != m_szBTMAC) {
				_openUdid = "BTMAC:" + m_szBTMAC;
			}
		} catch (Exception ex) {

		}
	}

	private static void generateWifiId(Context mContext) {

		try {
			WifiManager wifiMan = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInf = wifiMan.getConnectionInfo();

			_debugLog(String.format("%s", wifiInf.getMacAddress()));

			String macAddr = wifiInf.getMacAddress();

			if (macAddr != null) {
				_openUdid = "WIFIMAC:" + macAddr;
			}
		} catch (Exception ex) {

		}
	}

	@SuppressLint("DefaultLocale")
	private static String Md5(String input) {

		MessageDigest m = null;

		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.update(input.getBytes(), 0, input.length());
		byte p_md5Data[] = m.digest();
		String mOutput = new String();

		for (int i = 0; i < p_md5Data.length; i++) {
			int b = (0xFF & p_md5Data[i]);

			if (b <= 0xF) {
				mOutput += "0";
			}
			mOutput += Integer.toHexString(b);
		}

		return mOutput.toUpperCase();
	}

	private static void generateRandomNumber() {

		_openUdid = Md5(UUID.randomUUID().toString());
	}

	@SuppressLint("DefaultLocale")
	private static void generateSystemId() {

		String fp = String.format("%s/%s/%s/%s:%s/%s/%s:%s/%s/%d-%s-%s-%s-%s", Build.BRAND, Build.PRODUCT, Build.DEVICE, Build.BOARD, Build.VERSION.RELEASE, Build.ID,
				Build.VERSION.INCREMENTAL, Build.TYPE, Build.TAGS, Build.TIME, Build.DISPLAY, Build.HOST, Build.MANUFACTURER, Build.MODEL);

		_debugLog(fp);

		if (null != fp) {
			_openUdid = Md5(fp);
		}
	}

}
