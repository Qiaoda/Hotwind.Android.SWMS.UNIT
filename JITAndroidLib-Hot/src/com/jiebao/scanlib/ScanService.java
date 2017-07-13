package com.jiebao.scanlib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.jiebao.barcode.BarcodeManager;
import android.jiebao.barcode.BarcodeManager.Callback;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

public class ScanService extends Service implements Callback {
	private final byte[] pack_code_protocol = { 0x07, (byte) 0xC6, 0x04, 0x00,
			(byte) 0xFF, (byte) 0xEE, 0x01, (byte) 0xFD, 0x41 };
	private final byte[] wakeUp = { 0x00 };
	private final byte[] host_cmd_ack = { 0x04, (byte) 0xD0, 0x04, 0x00,
			(byte) 0xFF, 0x28 };
	private long scanLastTime;

	private int hardwareVersion;

	private OneDBeepManager oneDBeepManager;
	private static File versionFile = new File(
			"/sys/devices/platform/exynos4412-adc/ver");
	private ScanListener mScanListener;
	private File trigFile = new File("/sys/devices/platform/em3095/trig");// ??0"出光，写??”关??
	private File power = new File("/sys/devices/platform/em3095/dc_power");
	private static File openCom = new File("/sys/devices/platform/em3095/com");
	private Handler mHandler = new Handler();
	private boolean isClick = false;
	private KeyguardManager km;
	private DataReceived dataReceived;
	public static boolean isScan = false;
	private String trig = -1 + "";
	private boolean isInit = false;
	/**
	 * 讯宝????引擎se955
	 */
	public static boolean se955 = false;

	private WakeLockUtil mWakeLockUtil = null;
	private BarcodeManager mBarcodeManager;

	@Override
	public void onCreate() {
		super.onCreate();
		String line = getHardwareVersion();
		if (!TextUtils.isEmpty(line)) {
			String temp = line.substring(15, 19);
			temp = temp.replace(".", "");
			this.hardwareVersion = Integer.valueOf(temp).intValue();
		}
		km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		se955 = Preference.isSe955(this);
		dataReceived = new DataReceived();
		dataReceived.init();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mBarcodeManager = BarcodeManager.getInstance();
		mBarcodeManager.Barcode_Open(getApplicationContext(), this);
		mWakeLockUtil = new WakeLockUtil(this);
		oneDBeepManager = new OneDBeepManager(this, true, true);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.jb.action.F4key");
		intentFilter.addAction("ComBackToScan");
		registerReceiver(f4Receiver, intentFilter);
		if (!se955)
			init();
	}

	/**
	 * 扫描数据接收??
	 * 
	 * @author Administrator
	 * 
	 */
	private class DataReceived extends JBInterface {
		@Override
		protected void onDataReceived(byte[] buffer, int size) {
			isScan = false;
			if (buffer == null)
				return;
			if (buffer.length == 7 && buffer[0] == 5 && buffer[1] == -47
					&& buffer[2] == 0 && buffer[3] == 0 && buffer[4] == 1
					&& buffer[5] == -1 && buffer[6] == 41) {
				if (!se955) {
					ScanService.se955 = true;
					Preference.setSe955(ScanService.this, true);
				}
				return;
			}
			if (buffer.length == 6 && buffer[0] == 4 && buffer[1] == -48
					&& buffer[2] == 0 && buffer[3] == 0 && buffer[4] == -1
					&& buffer[5] == 44) {
				if (!se955) {
					ScanService.se955 = true;
					Preference.setSe955(ScanService.this, true);
				}
				if (!isInit) {
					isInit = true;
					scan(trig);
				}
				return;
			}
			if (se955 && dataReceived != null) {
				dataReceived.writeCommand(host_cmd_ack);
			}

			boolean isAllInvalib = true;
			int startIndex = -1;
			int len = -1;
			byte[] temp = null;
			for (int i = 0; i < buffer.length; i++) {
				if (buffer[i] > 32) {
					isAllInvalib = false;
				}
				if (se955) {
					if (buffer[i] == -13 && i - 1 >= 0 && startIndex < 0
							&& len < 0) {
						startIndex = i - 1;
						len = buffer[i - 1];
					}
					if (temp == null && startIndex >= 0 && len > 0
							&& buffer.length - startIndex > len) {
						temp = new byte[len + 2];
						temp[0] = buffer[i - 1];
						temp[1] = buffer[i];
					}
					if (temp != null) {
						if (i - startIndex < temp.length)
							temp[i - startIndex] = buffer[i];

					}
				} 
			}
			if (isAllInvalib) {
				return;
			}
			if (buffer.length == 1 && buffer[0] == 0x00) {
				// 兼容新大陆一维引??
				// 新大陆一维引擎只能上电一??下次????源必须在500ms以上
				// se955 = false;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			if (mScanListener != null) {
				mScanListener.result(buffer);
			}
			oneDBeepManager.setPlayBeep(true);
			oneDBeepManager.play();
			isClick = false;
			scan("1");
		}
	}

	public void setOnScanListener(ScanListener scanListener) {
		this.mScanListener = scanListener;
	}

	public Binder myBinder = new MyBinder();

	public class MyBinder extends Binder {

		public ScanService getService() {

			return ScanService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
	}

	/**
	 * 
	 * @param trig
	 *            1拉高，关闭；0拉低，打??
	 */

	public void scan(String trig) {
		if (this.hardwareVersion < 103) {
			// 旧版
			if (!isInit && se955) {
				ScanService.this.trig = trig;
				init();
			}
			// 两次触发信号的间隔时间不低于50ms
			try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (trig.equals("0"))
				mHandler.postDelayed(countdown, 3 * 1000);
			else
				mHandler.removeCallbacks(countdown);
			isScan = true;
			dataReceived.notifyReader();
			writeFile(trigFile, trig);
		} else {
			// 新版
			if ("1".equals(trig)) {
				mBarcodeManager.Barcode_Stop();
			} else if ("0".equals(trig)) {
				mBarcodeManager.Barcode_Start();
			}

		}
	}

	/**
	 * 初始??
	 */
	public void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				com("1");// 转换串口到扫描模??
				power("1");// 给扫描头上电
				if (dataReceived != null) {
					dataReceived.writeCommand(wakeUp);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					isScan = true;
					dataReceived.notifyReader();
					dataReceived.writeCommand(pack_code_protocol);
				}

			}
		}).start();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * COM口开??
	 * 
	 * @param status
	 *            1????0??
	 */
	private void com(String status) {
		writeFile(openCom, status);
	}

	/**
	 * 给扫描头上电
	 * 
	 * @param power
	 *            1 ????0??
	 */
	private void power(String p) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		writeFile(power, p);
	}

	private synchronized static void writeFile(File file, String value) {
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(value.getBytes());
			outputStream.flush();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
				if (dataReceived != null) {
					dataReceived.onDestroy();
				}
				dataReceived = new DataReceived();
				dataReceived.init();
				init();
				return;
			}
			if (km.inKeyguardRestrictedInputMode()) {// 屏已??
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
					mWakeLockUtil.unLock();
					return;
				}
				// 先关闭，再打??
				// scan("1");
				scan("0");// 打开扫描
				isClick = true;
				mWakeLockUtil.lock();
			} else if (extra.equals("up")) {
				mHandler.removeCallbacks(countdown);
				isClick = false;
				scan("1");// 关闭扫描
				mWakeLockUtil.unLock();
				isScan = false;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		dataReceived.onDestroy();
		unregisterReceiver(f4Receiver);
		power("0");
		scan("1");
		mWakeLockUtil.unLock();
		sendBroadcast(new Intent("ScanServiceDestroy"));
	}

	public boolean isSe955() {
		return se955;
	}

	public static void backToScanCom(Context context) {
		context.sendBroadcast(new Intent("ComBackToScan"));
	}

	public static void backToScanCom() {
		try {
			FileOutputStream outputStream = new FileOutputStream(openCom);
			outputStream.write("1".getBytes());
			outputStream.flush();
			outputStream.close();
			System.out.println("backToScanCom");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getHardwareVersion() {
		String line = "";
		FileInputStream is = null;
		try {
			is = new FileInputStream(versionFile);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			line = new String(buffer);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return line;
	}

	@Override
	public void Barcode_Read(byte[] arg0, String arg1, int arg2) {
		if (mScanListener != null) {
			oneDBeepManager.setPlayBeep(true);
			oneDBeepManager.play();
			mScanListener.result(arg0);
		}
	}

}
