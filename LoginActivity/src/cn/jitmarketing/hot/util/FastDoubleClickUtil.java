package cn.jitmarketing.hot.util;

public class FastDoubleClickUtil {

	private static long lastClickTime;
	
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
	
}
