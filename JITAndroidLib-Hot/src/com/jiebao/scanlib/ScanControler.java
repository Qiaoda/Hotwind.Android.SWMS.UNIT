package com.jiebao.scanlib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android_serialport_api.SerialPort;

public class ScanControler extends AbsSerialPort {
	private static ScanControler scanCon;
	private boolean isPlaySound = false;
	public AudioManager manager;
	public OutputStream mOutputStream;
	@SuppressWarnings("unused")
	private InputStream mInputStream;
//	private ReadThread mReadThread;
	@SuppressWarnings("unused")
	private boolean isClick = false;
	public static boolean isScan = false;
	private File trigFile = new File("/sys/devices/platform/em3095/trig");
	private File power = new File("/sys/devices/platform/em3095/dc_power");
	private static File openCom = new File("/sys/devices/platform/em3095/com");
	private Handler mHandler = new Handler();
	public boolean isPlaySound() {
		return isPlaySound;
	}

	public void setPlaySound(boolean isPlaySound) {
		this.isPlaySound = isPlaySound;
	}

	Context context;

	BeepManager beepManager;

	public static ScanControler getScanControler(Activity context,
			OnReadSerialPortDataListener scanListener) {
		scanCon = new ScanControler(context);
		scanCon.initBM(context);
		scanCon.kaidian();
		scanCon.read(scanListener);
		return scanCon;
	}

	public static void setScanNull() {
		if (scanCon != null) {
			scanCon.xiadian();
			scanCon = null;
		}
	}

	private ScanControler(Context context) {
		super("/dev/ttySAC1", 9600, 8, 'N', 1);
		this.context = context;
	}

	public void initBM(Activity activity) {
		beepManager = new BeepManager(activity);
	}

	public void doScan() {
//		checkIsSleep();
		gongzuo();
	}

	public void gongzuo() {
//		GpioJNI.gpio_switch_scan_trig(1);
		scan("0");
	}

//}

private void power(String p) {
	try {
		Thread.sleep(100);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}

	writeFile(power, p);
}
	
	public void kaidian() {
//		GpioJNI.gpio_switch_scan_rf_ired(0);
//		GpioJNI.gpio_switch_scan_power(1);
//		com("0");
		power("1");
//		scan("0");
	}

	public void xiadian() {
//		GpioJNI.gpio_switch_scan_power(0);
//		power("0");
		try {
			power("0");
			Thread.sleep(2);
			scan("1");
			
			Thread.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		scan("1");
		super.destroy();
	}

	public void cxhuanxing() {
		xiuxi();
		gongzuo();
	}

	public void xiuxi() {
//		GpioJNI.gpio_switch_scan_trig(0);
		scan("1");
	}

	
	/**
	 * COM口开�?
	 * 
	 * @param status
	 */
	public void com(String status) {

		writeFile(openCom, status);
	}

	void checkIsSleep() {
		int triggerVal = GpioJNI.gpio_get_scan_trig();
		System.err.println("checkIsSleep: " + triggerVal);
		if (triggerVal == 1)
			xiuxi();
	}

	public void trunVON() {
		beepManager.turnOnV();
	}

	public void trunVOFF() {
		beepManager.turnOffV();
	}

	public void turnOFFV() {
		beepManager.release();
	}

	public void destroy() {
		xiuxi();
		xiadian();
		super.destroy();
	}

	boolean music = true;

	public synchronized void playSound() {
		if (isPlaySound) {
			beepManager.playBeepSoundAndVibrate();
			return;
		}

	}
	
	
	public void init() {
//		begin = true;
		try {
			if (mSerialPort == null) {
				String path = "/dev/ttySAC1";
				int baudrate = 9600;
				if ((path.length() == 0) || (baudrate == -1)) {
					System.out.println("alven wrong port\n");
					throw new InvalidParameterException();
				}

				// "/dev/ttySAC1", 9600, 8, 'N', 1

				try {
					mSerialPort = new SerialPort(new File(path), baudrate, 0,false);
					// mSerialPort = new SerialPort(new File("/dev/ttySAC1"),
					// 9600, 8,'N',1,0);
					mOutputStream = mSerialPort.getOutputStream();
					mInputStream = mSerialPort.getInputStream();
//					mReadThread = new ReadThread();
//					mReadThread.start();
					System.out.println("init()");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {

		} catch (InvalidParameterException e) {

		}
	}
	
	
	/**
	 * 
	 * @param trig
	 *            1拉高，关闭；0拉低，打�?
	 */

	public void scan(String trig) {

//		 if (!isInit) {
//		 ScanService.this.trig = trig;
//		 init();
//		 }
//		 com("1");// 转换串口到扫描模�?
//		 power("1");// 给扫描头上电
//		 if (dataReceived != null) {
//		 dataReceived.writeCommand(wakeUp);
//		 try {
//		 Thread.sleep(50);
//		 } catch (InterruptedException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
//		 dataReceived.writeCommand(pack_code_protocol);
//		 }
		// 两次触发信号的间隔时间不低于50ms
		Log.i("info", "trig === " + trig);
		try {
			Thread.sleep(60);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//
//		// Exception e = new Exception("this is a log");
//		// e.printStackTrace();
		if (trig.equals("0")) {
			mHandler.postDelayed(countdown, 3 * 1000);//3 * 1000
			isScan = true;
		}

		else {
			mHandler.removeCallbacks(countdown);
			isScan = false;
		}

		// if (trig.equals("0")) {
		// // power_se955("1");// 判断是否讯宝头，是则上电，不是不作操�?
		// mHandler.postDelayed(countdown, 3 * 1000);// 3秒后，如果没有扫描到任何结果，则关闭
		//
		// } else {
		// // power_se955("0");// 判断是否讯宝头，是则下电，不是不作操�?
		// mHandler.removeCallbacks(countdown);
		//
		// }

//		dataReceived.notifyReader();
		writeFile(trigFile, trig);

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
			// if (se955) {
			// power("0");
			// }
			scan("1");
			isClick = false;
			isScan = false;
		}
	};
}
