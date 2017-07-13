package com.jiebao.scanlib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;


import android.util.Log;
import android_serialport_api.SerialPort;

public abstract class JBInterface {
	protected SerialPort mSerialPort;
	public OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	protected byte[] writeBytes;
	protected int tsleeptime = 50;
	protected byte[] inBytes;
	protected boolean bWriteCardData = false;
	private boolean begin;

	public void notifyReader() {
		if (mReadThread != null && mReadThread.isAlive()) {
			mReadThread.interrupt();
		}
	}

	public class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (begin) {
				if (ScanService.isScan) {
					int size;
					try {
						if (mInputStream == null)
							return;
						int cout = mInputStream.available();

						if (cout > 0) {
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} else {
							continue;
						}
						int temp = 0;
						while (begin) {
							cout = mInputStream.available();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
								break;
							}
							if (temp == cout) {
								break;
							}
							temp = cout;

						}

						cout = mInputStream.available();
						byte[] buffer = new byte[cout];
						size = mInputStream.read(buffer);

						if (size > 0) {

							onDataReceived(buffer, size);
						}
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				} else {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("readThreadRunning");
			}

		}
	}

	public void init() {
		begin = true;
		try {
			if (mSerialPort == null) {
				String path = "/dev/ttySAC1";
				int baudrate = 9600;
				if ((path.length() == 0) || (baudrate == -1)) {
					System.out.println("alven wrong port\n");
					throw new InvalidParameterException();
				}
				try {
					mSerialPort = new SerialPort(new File(path), baudrate, 0,false);
					mOutputStream = mSerialPort.getOutputStream();
					mInputStream = mSerialPort.getInputStream();
					mReadThread = new ReadThread();
					mReadThread.start();
					System.out.println("init()");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {

		} catch (InvalidParameterException e) {

		}
	}

	public void writeCommand(byte[] command) {
		try {
			if (mOutputStream != null) {
				mOutputStream.write(command);
				mOutputStream.flush();
			}

		} catch (Exception e) {
		}

	}

	protected abstract void onDataReceived(final byte[] buffer, final int size);

	public void onDestroy() {
		begin = false;
		if (mReadThread != null)
			mReadThread.interrupt();
		mSerialPort = null;
		Log.i("info", "程序关闭");
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}

	}

	/**
	 * Convert bytes to string,actually display only
	 * 
	 * @param bytes
	 * @return String
	 */
	public static String bytesToHexString(byte[] src, int start, int size) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || size <= 0) {
			return null;
		}
		for (int i = start; i < size; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

}
