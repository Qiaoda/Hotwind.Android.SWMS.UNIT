package com.jiebao.scanlib;

public class CharsetUtil {

	/**
	 * 获取byte[]数组实际编码
	 * @param bytes
	 * @return
	 */
	public static String guessEncoding(byte[] bytes) {
	    String DEFAULT_ENCODING = "GB2312";
	    org.mozilla.universalchardet.UniversalDetector detector =
	        new org.mozilla.universalchardet.UniversalDetector(null);
	    detector.handleData(bytes, 0, bytes.length);
	    detector.dataEnd();
	    String encoding = detector.getDetectedCharset();
	    detector.reset();
	    if (encoding == null) {
	        encoding = DEFAULT_ENCODING;
	    }
	    return encoding;
	}

}
