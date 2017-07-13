package com.ex.lib.ext.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.content.Context;

import com.ex.lib.core.utils.Ex;

public abstract class DES {
	/**
	 * ALGORITHM 算法 <br>
	 * 可替换为以下任意一种算法，同时key值的size相应改变。
	 * 
	 * <pre>
	 * DES          		key size must be equal to 56
	 * DESede(TripleDES) 	key size must be equal to 112 or 168
	 * AES          		key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available
	 * Blowfish     		key size must be multiple of 8, and can only range from 32 to 448 (inclusive)
	 * RC2          		key size must be between 40 and 1024 bits
	 * RC4(ARCFOUR) 		key size must be between 40 and 1024 bits
	 * </pre>
	 * 
	 * 在Key toKey(byte[] key)方法中使用下述代码
	 * <code>SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);</code> 替换
	 * <code>
	 * DESKeySpec dks = new DESKeySpec(key);
	 * SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
	 * SecretKey secretKey = keyFactory.generateSecret(dks);
	 * </code>
	 */
	public static final String ALGORITHM = "DES";
	public static final String ENCODING = "UTF-8";

	/**
	 * 生成密钥
	 * 
	 * @return
	 */
	public static String initKey(Context context) {
		// 默认种子
		String key = "ex@0829";

		try {
			// 获得机器设备ID，作为种子，来生成密钥
			String deviceId = Ex.Device(context).getDeviceId();

			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
			secureRandom.setSeed(Base64.decode(deviceId));

			KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);

			kg.init(secureRandom);

			SecretKey secretKey = kg.generateKey();

			key = Base64.encode(secretKey.getEncoded());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}

		return key;
	}

	/**
	 * 转换密钥<br>
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static Key toKey(byte[] key) {
		SecretKey secretKey = null;

		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
			secretKey = keyFactory.generateSecret(dks);

			// 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
			// SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

		} catch (InvalidKeyException e) {
			e.printStackTrace();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}

		return secretKey;
	}

	/**
	 * 解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String dest, Context context) {
		String src = "";

		if (!Ex.String().isEmpty(dest)) {
			try {
				String key = DES.initKey(context);
				Key k = toKey(Base64.decode(key));

				if (k != null) {
					Cipher cipher = Cipher.getInstance(ALGORITHM);
					cipher.init(Cipher.DECRYPT_MODE, k);

					byte[] srcBytes = cipher.doFinal(Base64.decode(dest));
					src = new String(srcBytes, ENCODING);
				}

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();

			} catch (NoSuchPaddingException e) {
				e.printStackTrace();

			} catch (InvalidKeyException e) {
				e.printStackTrace();

			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();

			} catch (BadPaddingException e) {
				e.printStackTrace();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return src;
	}

	/**
	 * 加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String src, Context context) {
		String dest = "";

		if (!Ex.String().isEmpty(src)) {
			try {
				String key = DES.initKey(context);
				Key k = toKey(Base64.decode(key));

				if (k != null) {

					Cipher cipher = Cipher.getInstance(ALGORITHM);
					cipher.init(Cipher.ENCRYPT_MODE, k);

					byte[] destBytes = cipher.doFinal(src.getBytes(ENCODING));
					dest = Base64.encode(destBytes);
				}

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();

			} catch (NoSuchPaddingException e) {
				e.printStackTrace();

			} catch (InvalidKeyException e) {
				e.printStackTrace();

			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();

			} catch (BadPaddingException e) {
				e.printStackTrace();

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return dest;
	}

}
