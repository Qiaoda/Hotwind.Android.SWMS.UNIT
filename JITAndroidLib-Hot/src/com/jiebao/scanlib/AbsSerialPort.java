package com.jiebao.scanlib;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;


public class AbsSerialPort {

	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String portName;
	private boolean begin;

	private OnReadSerialPortDataListener onReadSerialPortDataListener;

	public void read(OnReadSerialPortDataListener _onReadSerialPortDataListener) {
		this.onReadSerialPortDataListener = _onReadSerialPortDataListener;
	}

	private SerialPort getSerialPort(String port, int baudrate, int bits,
			char event, int stop) throws SecurityException, IOException,
			InvalidParameterException {
		if (mSerialPort == null) {
			if ((port.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}
			portName = port;
			mSerialPort = new SerialPort(new File(port), baudrate, bits, event,
					stop, 0);
		}
		return mSerialPort;
	}

	public AbsSerialPort(String port, int baudrate, int bits, char event,
			int stop) {
		try {
			mSerialPort = this.getSerialPort(port, baudrate, bits, event, stop);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			begin = true;
			mReadThread = new ReadThread();
			mReadThread.start();

		} catch (SecurityException e) {
			System.err
					.println("You do not have read/write permission to the serial port.");
			e.printStackTrace();
		} catch (IOException e) {
			System.err
					.println("The serial port can not be opened for an unknown reason.");
			e.printStackTrace();
		} catch (InvalidParameterException e) {
			System.err.println("Please configure your serial port first.");
			e.printStackTrace();
		}
	}

	public class SerialPortData {
		private byte[] dataByte;
		private int size;

		public SerialPortData(byte[] _dataByte, int _size) {
			this.setDataByte(_dataByte);
			this.setSize(_size);
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public byte[] getDataByte() {
			return dataByte;
		}

		public void setDataByte(byte[] dataByte) {
			this.dataByte = dataByte;
		}
	}

	private void doRead() {

		int size;
		try {
			if (mInputStream == null) {
				return;
			}
			int cout = mInputStream.available();
			byte[] buffer1 = new byte[cout];
			if (buffer1.length >= 16) {
				cout = 0;
				buffer1 = null;
				Thread.sleep(100);
				cout = mInputStream.available();
				buffer1 = new byte[cout];
			} else {
				Thread.sleep(100);
			}
			size = mInputStream.read(buffer1);
			if (size > 0) {
				SerialPortData data = new SerialPortData(buffer1, size);
				if (onReadSerialPortDataListener != null) {
					onReadSerialPortDataListener.onReadSerialPortData(data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	class ReadThread extends Thread {
		public void run() {
			System.out.println("监听" + portName);// + ":......");
			while (begin) {
				doRead();
			}
		}
	}

	public interface OnReadSerialPortDataListener {
		public void onReadSerialPortData(SerialPortData serialPortData);
	}

	public void close() {
		if (mSerialPort != null) {
			mSerialPort.close();
			System.err.println("关闭串口");
			begin = false;
			mSerialPort = null;
		}
	}

	public void reset(String port, int baudrate) {
		destroy();

	}

	public void destroy() {
		
		this.close();
		begin = false;
		mReadThread = null;
		System.err.println("关闭串口");//
		mSerialPort = null;
	}

	// [s] 输出
	public void write(String msg) {
		try {
			if (allowToWrite()) {
				if (msg == null)
					msg = "";
				mOutputStream.write(msg.getBytes());
				mOutputStream.flush();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(byte[] b) {
		try {
			if (allowToWrite()) {
				if (b == null)
					return;
				System.out.println("对串口写入：" + b);
				mOutputStream.write(b);
				mOutputStream.flush(); // 1
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(int oneByte) {
		try {
			if (allowToWrite()) {
				mOutputStream.write(oneByte);
				mOutputStream.flush(); // 1
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean allowToWrite() {
		if (mOutputStream == null) {
			return false;
		}
		return true;
	}

	// [e]
	public static String toHexString(String s) {
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = (int) s.charAt(i);
			String s4 = Integer.toHexString(ch);
			str = str + s4;
		}
		return str;
	}

	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}

	public static byte[] byteMerger1(byte[] byte_1, byte[] byte_2, byte[] buffer) {
		buffer = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, buffer, 0, byte_1.length);
		System.arraycopy(byte_2, 0, buffer, byte_1.length, byte_2.length);
		return buffer;
	}
}
