package cn.jitmarketing.hot.util;


public class CommonUtil {

//	public static String formatTime(String timeStr){
//		float time = Float.valueOf(timeStr);
//		int hour = (int) (time / 360);
//		int min = (int) (time / 60) ;
//		int ss= (int) (time % 60) ;
//		return formatTi(hour) + ":" + formatTi(min) + ":"+ss ;
//	}
//	
	public static String formatTi(long time){
		return time <= 9 ? "0" + time : String.valueOf(time) ;
	}
	
	public static String formatTime(String timeStr){
//		int time = Float.valueOf(timeStr).intValue();
		long time = Long.valueOf(timeStr);
		long hour = time / (60 * 60);
		long minute = (time % (60 * 60)) / 60;
		long second =  time % 60;
//		return formatTi(hour) + ":" + formatTi(minute) + ":"+ formatTi(second);
		return formatTi(hour) + ":" + formatTi(minute);
	}
	
}
