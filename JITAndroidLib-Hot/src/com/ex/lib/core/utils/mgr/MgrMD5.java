/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: MgrMD5
 * @Description: MgrMD5 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrMD5 {

	/**
	 * 创建者
	 */
	private static class md5Holder {

		private static final MgrMD5 mgr = new MgrMD5();
	}

	/**
	 * 获取当前实例
	 * 
	 * @return
	 */
	public static MgrMD5 getInstance() {

		return md5Holder.mgr;
	}

	/**
	 * Method_获取 MD5 加密值
	 * 
	 * @param val_内容
	 * @return 结果
	 */
	public String getMD5(String val) {

		byte[] hash = null;

		try {
			hash = MessageDigest.getInstance("MD5").digest(val.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (hash == null) {

			return "";
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {
			if ((b & 0xFF) < 0x10) {
				hex.append("0");
			}

			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}

}
