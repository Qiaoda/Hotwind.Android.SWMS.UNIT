/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.ex.lib.R;
import com.ex.lib.core.utils.Ex;

/**
 * @ClassName: MgrApp
 * @Description: MgrApp 管理类
 * @author Aloneter
 * @date 2014-10-24 下午5:53:09
 * @Version 1.0
 * 
 */
public class MgrApp {

	public static Context mContext; // 上下文

	/**
	 * 创建者
	 */
	private static class AppHolder {

		private static final MgrApp mgr = new MgrApp();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrApp getInstance(Context context) {

		mContext = context;

		return AppHolder.mgr;
	}

	/**
	 * Method_显示系统键盘
	 * 
	 * @param v_操作对象
	 */
	public void showSysKeyBord(View v) {

		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(v, 0);
	}

	/**
	 * Method_隐藏系统键盘
	 * 
	 * @param v_操作对象
	 */
	public void hideSysKeyBord(View v) {

		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(v.getWindowToken(), 0);
	}

	/**
	 * Method_打电话
	 * 
	 * @param phoneNumber_电话号码
	 */
	public void callPhone(String phoneNumber) {

		mContext.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
	}

	/**
	 * Method_发送邮件
	 * 
	 * @param emailAddr_邮件地址
	 */
	public void sendEmail(String[] emailAddr) {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, emailAddr);
		intent.setType("plain/text");

		mContext.startActivity(Intent.createChooser(intent, MgrAndroid.getInstance(mContext).string(R.string.ex_str_content_please_install_email)));
	}

	/**
	 * Method_发送短信通过系统
	 * 
	 * @param phoneNumber_电话号码
	 * @param content_内容
	 */
	public void sendSMSMgr(String phoneNumber, String content) {

		SmsManager smsManager = SmsManager.getDefault();

		PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(), 0);

		if (content.length() > 70) {
			List<String> divideContents = smsManager.divideMessage(content);

			for (String text : divideContents) {
				smsManager.sendTextMessage(phoneNumber, null, text, sentIntent, null);
			}
		} else {
			smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
		}
	}

	/**
	 * Method_发送短信
	 * 
	 * @param phoneNumber_电话号码
	 * @param content_内容
	 */
	public void sendSMS(String phoneNumber, String content) {

		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
		intent.putExtra("sms_body", content);

		mContext.startActivity(intent);
	}

	/**
	 * Method_在地图上面显示位置
	 */
	public void showMapByWeb() {

	}

	/**
	 * Method_显示 WEB 页面
	 * 
	 * @param url
	 */
	public void showPageByWeb(String url) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setData(Uri.parse(url));

		mContext.startActivity(intent);
	}

	/**
	 * Method_打开 Excel
	 * 
	 * @param path_路径
	 */
	public void openExcel(String path) {

		openFile(path, "application/vnd.ms-excel");
	}

	/**
	 * Method_打开 PPT
	 * 
	 * @param path_路径
	 */
	public void openPPT(String path) {

		openFile(path, "application/vnd.ms-powerpoint");
	}

	/**
	 * Method_打开 Word
	 * 
	 * @param path_路径
	 */
	public void openWord(String path) {

		openFile(path, "application/msword");
	}

	/**
	 * Method_打开文件
	 * 
	 * @param path_路径
	 * @param type_文件路径
	 */
	public void openFile(String path, String type) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(path)), type);

		try {
			mContext.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method_应用安装
	 * 
	 * @param filename_名
	 * @return 结果
	 */
	@SuppressLint("WorldReadableFiles")
	@SuppressWarnings("deprecation")
	public boolean install(String filename) {

		if (Ex.String().isEmpty(filename)) {

			return false;
		}

		int result = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);

		if (result == 0) {
			Toast.makeText(mContext, MgrAndroid.getInstance(mContext).string(R.string.ex_str_content_please_not_mark_app), Toast.LENGTH_SHORT).show();

			Intent intent = new Intent();

			intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			mContext.startActivity(intent);
		} else {

			if (!filename.contains("//sdcard")) {

				String oldname = filename;
				filename = "sharetemp_" + System.currentTimeMillis();

				try {
					FileInputStream fis = new FileInputStream(oldname);
					FileOutputStream fos = mContext.openFileOutput(filename, Context.MODE_WORLD_READABLE);

					filename = mContext.getFilesDir() + "/" + filename;
					byte[] buffer = new byte[1024];
					int len = 0;

					while ((len = fis.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}

					buffer = null;
					fis.close();
					fis = null;
					fos.flush();
					fos.close();
					fos = null;
				} catch (IOException e) {
					e.printStackTrace();

					return false;
				}
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);

			intent.setDataAndType(Uri.parse("file://" + filename), "application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

			mContext.startActivity(intent);

			return true;
		}

		return false;
	}

	/**
	 * Method_卸载应用
	 * 
	 * @param packageName_包名
	 * @return 结果
	 */
	public boolean uninstall(String packageName) {

		if (Ex.String().isEmpty(packageName)) {

			return false;
		}

		Uri packageURI = Uri.parse("package:" + packageName);
		Intent i = new Intent(Intent.ACTION_DELETE, packageURI);

		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		((Activity) mContext).startActivityForResult(i, 10);

		return true;

	}

	/**
	 * Method_运行应用
	 * 
	 * @param packageName_包名
	 * @return 结果
	 */
	public boolean run(String packageName) {

		try {
			mContext.getPackageManager().getPackageInfo(packageName, PackageManager.SIGNATURE_MATCH);

			try {
				Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(packageName);

				mContext.startActivity(intent);
			} catch (Exception e) {

				try {
					Toast.makeText(mContext, MgrAndroid.getInstance(mContext).string(R.string.ex_str_content_please_check_app_type), Toast.LENGTH_SHORT).show();
				} catch (Exception ex) {
				}

				e.printStackTrace();

				return false;
			}

			return true;
		} catch (NameNotFoundException e) {
			e.printStackTrace();

			return false;
		}
	}

}
