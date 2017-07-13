package com.jiebao.scanlib;

public class GpioJNI {

	static public native void gpio_switch_gps_bluetooth(int flag);

	static public native int gpio_get_gps_bluetooth();

	static public native void gpio_switch_gps_power(int flag);

	static public native int gpio_get_gps_power();

	static public native void gpio_switch_rs485_rs232(int flag);

	static public native int gpio_get_rs485_rs232();

	static public native void gpio_switch_rs485_power(int flag);

	static public native int gpio_get_rs485_power();

	static public native void gpio_switch_rs232_power(int flag);

	static public native int gpio_get_rs232_power();

	static public native void gpio_switch_scan_rf_ired(int flag);

	static public native int gpio_get_scan_rf_ired();

	static public native void gpio_switch_rf_power(int flag);

	static public native int gpio_get_rf_power();

	static public native void gpio_switch_scan_power(int flag);

	static public native int gpio_get_scan_power();

	static public native void gpio_switch_scan_powerdown(int flag);

	static public native int gpio_get_scan_powerdown();

	static public native void gpio_switch_scan_trig(int flag);// ????????出光扫描

	static public native int gpio_get_scan_trig();

	static public native void gpio_switch_scan_reset(int flag);

	static public native int gpio_get_scan_reset();

	static public native void gpio_switch_rf_reset(int flag);

	static public native int gpio_get_rf_reset();

	static public native void gpio_switch_ired(int flag);

	static public native int gpio_get_ired();

	static {
		try {
			System.loadLibrary("jni_gpio");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
