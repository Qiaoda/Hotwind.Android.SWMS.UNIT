package com.example.scandemo;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.jiebao.barcode.BarcodeManager;
import android.jiebao.barcode.BarcodeManager.Callback;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class ScanService extends Service implements Callback {
	public static final String CODE_RESULT = "com.jiebao.scan.code.RESULT";
	public static final String CODE = "code";
	public static final String CODE_TYPE = "codeType";
	private long scanLastTime;

//	private OneDBeepManager oneDBeepManager;
	private Handler mHandler = new Handler();
	private boolean isClick = false;
	private KeyguardManager km;
	public static boolean isScan = false;
	private BarcodeManager mBarcodeManager;

	@Override
	public void onCreate() {
		super.onCreate();
		km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mBarcodeManager = BarcodeManager.getInstance();
		mBarcodeManager.Barcode_Open(getApplicationContext(), this);
//		oneDBeepManager = new OneDBeepManager(this, true, true);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.jb.action.F4key");
		intentFilter.addAction("ComBackToScan");
		registerReceiver(f4Receiver, intentFilter);
	}

	public void scan(String trig) {
		// 新版
		if ("1".equals(trig)) {
			mBarcodeManager.Barcode_Stop();
		} else if ("0".equals(trig)) {
			mBarcodeManager.Barcode_Start();
		}

	}

	private Runnable countdown = new Runnable() {
		@Override
		public void run() {
			scan("1");
			isClick = false;
			isScan = false;
		}
	};

	long nowTime = 0;
	long lastTime = 0;
	/**
	 * 捕获扫描物理按键广播
	 */
	private BroadcastReceiver f4Receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			long timeMillis = System.currentTimeMillis();
			if (timeMillis - scanLastTime < 100) {
				return;
			}
			scanLastTime = timeMillis;
			if ("ComBackToScan".equals(intent.getAction())) {
				return;
			}
			if (km.inKeyguardRestrictedInputMode()) {// 屏已�?
				return;
			}
			Bundle bundle = intent.getExtras();
			String extra = (String) bundle.get("F4key");
			if (extra.equals("down")) {
				nowTime = System.currentTimeMillis();
				if (nowTime - lastTime < 120) {// 扫描间隔时间控制
					return;
				}
				lastTime = nowTime;
				if (isClick) {
					mHandler.removeCallbacks(countdown);
					scan("1");// 关闭扫描
					isClick = false;
					return;
				}
				scan("0");// 打开扫描
				isClick = true;
			} else if (extra.equals("up")) {
				mHandler.removeCallbacks(countdown);
				isClick = false;
				scan("1");// 关闭扫描
				isScan = false;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(f4Receiver);
		scan("1");
		sendBroadcast(new Intent("ScanServiceDestroy"));
	}


	public static void backToScanCom(Context context) {
		context.sendBroadcast(new Intent("ComBackToScan"));
	}


	@Override
	public void Barcode_Read(byte[] arg0, String arg1, int arg2) {
		String code = new String(arg0);
//		oneDBeepManager.setPlayBeep(true);
//		oneDBeepManager.play();
		Intent intent = new Intent();
		intent.setAction(CODE_RESULT);
		intent.putExtra(CODE, code);
		intent.putExtra(CODE_TYPE, arg1);
		sendBroadcast(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
