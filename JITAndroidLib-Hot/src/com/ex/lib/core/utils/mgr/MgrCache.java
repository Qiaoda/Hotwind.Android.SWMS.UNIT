/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ex.lib.ext.utils.DES;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;

/**
 * @ClassName: MgrCache
 * @Description: MgrCache 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrCache {

	private String mPath = "/temp"; // 默认缓存文件
	private int mClearTime = 10 * 60 * 1000; // 定时清理时长 （时间）
	private int mClearDay = 7 * 24 * 3600 * 1000; // 定时清理时长 （天）

	public static Context mContext; // 上下文

	private File mCacheFile; // 缓存文件

	private static Map<String, Object> mInstanceMap = new HashMap<String, Object>(); // 一级缓存
	private List<Map.Entry<String, Long>> mCacheFileList = new ArrayList<Map.Entry<String, Long>>(); // 二级缓存

	/**
	 * 创建者
	 */
	private static class CacheHolder {

		private static final MgrCache mgr = new MgrCache();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrCache getInstance(Context context) {

		mContext = context;

		return CacheHolder.mgr;
	}

	/**
	 * Construction_构造函数
	 */
	public MgrCache() {

		mCacheFile = getCacheDir();
		clearTask();
	}

	/**
	 * Method_设置缓存路径
	 * 
	 * @param path_路径
	 */
	public void setPath(String path) {

		mPath = path;
	}

	/**
	 * Method_设置缓存清理时长 （分钟）
	 * 
	 * @param time_分钟
	 */
	public void setClearTime(int time) {

		mClearTime = time * 60 * 1000;
	}

	/**
	 * Method_设置缓存清理时长 （天）
	 * 
	 * @param day_天数
	 */
	public void setClearDay(int day) {

		mClearDay = day * 24 * 3600 * 1000;
	}

	/**
	 * Method_获取缓存文件目录
	 * 
	 * @return 文件对象
	 */
	public File getCacheDir() {

		File cache = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			cache = Environment.getExternalStorageDirectory();
			cache = new File(cache.getAbsolutePath() + mPath);

			if (!cache.exists()) {
				try {
					if (cache.mkdirs()) {
						return cache;
					} else {

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				return cache;
			}
		} else {
			cache = mContext.getCacheDir();
		}

		cache = new File(cache.getAbsolutePath() + mPath);

		if (!cache.exists()) {
			try {
				if (cache.mkdirs()) {

					return cache;
				} else {

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {

			return cache;
		}

		return cache;
	}

	/**
	 * Method_添加缓存文件 （字符串）
	 * 
	 * @param key_文件标识
	 * @param value_字符串
	 */
	public void put(String key, String value) {

		File file = createFile(key);
		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(file), 1024);
			out.write(DES.encrypt(value, mContext));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_获取缓存文件 （字符串）
	 * 
	 * @param key_文件标识
	 * @return 字符串
	 */
	public String getAsString(String key) {

		Object cacheValue = mInstanceMap.get(key);

		if (cacheValue != null) {
			return cacheValue.toString();
		}

		File file = createFile(key);
		if (file == null || !file.exists()) {

			return null;
		}

		BufferedReader in = null;
		StringBuffer sb = new StringBuffer();

		try {
			in = new BufferedReader(new FileReader(file));

			String currentLine;

			while ((currentLine = in.readLine()) != null) {
				sb.append(currentLine);
			}

			String result = DES.decrypt(sb.toString(), mContext);

			mInstanceMap.put(key, result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_添加缓存文件 （JSON）
	 * 
	 * @param key_文件标识
	 * @param value_JSON
	 *            对象
	 */
	public void put(String key, JSONObject value) {

		put(key, value.toString());
	}

	/**
	 * Method_获取缓存文件 （JSON）
	 * 
	 * @param key_文件标识
	 * @return JSON 对象
	 */
	public JSONObject getAsJSONObject(String key) {

		String JSONString = getAsString(key);

		try {
			JSONObject obj = new JSONObject(JSONString);

			return obj;
		} catch (JSONException e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Method_添加缓存文件 （JSONArray）
	 * 
	 * @param key_文件标识
	 * @param value_JSON
	 *            数组
	 */
	public void put(String key, JSONArray value) {

		put(key, value.toString());
	}

	/**
	 * Method_获取缓存文件 （JSONArray）
	 * 
	 * @param key_文件标识
	 * @return JSON 数组
	 */
	public JSONArray getAsJSONArray(String key) {

		String JSONString = getAsString(key);

		try {
			JSONArray obj = new JSONArray(JSONString);

			return obj;
		} catch (JSONException e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Method_添加缓存文件 （Byte）
	 * 
	 * @param key_文件标识
	 * @param value_byte
	 *            数组
	 */
	public void put(String key, byte[] value) {

		File file = createFile(key);
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			out.write(value);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_获取缓存文件 （流）
	 * 
	 * @param key_文件标识
	 * @return 流对象
	 */
	public InputStream getAsInputStream(String key) {

		File file = getFile(key);

		if (file == null||  !file.exists()) {

			return null;
		}
		try {

			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * Method_获取缓存文件 （Byte）
	 * 
	 * @param key_文件标识
	 * @return byte 数组
	 */
	public byte[] getAsByte(String key) {

		RandomAccessFile randomAccessFile = null;

		File file = getFile(key);

		if (file == null ||!file.exists()) {

			return null;
		}

		try {
			randomAccessFile = new RandomAccessFile(file, "r");
			byte[] byteArray = new byte[(int) randomAccessFile.length()];

			int result = randomAccessFile.read(byteArray);

			if (result < 0) {
				return null;
			}

			return byteArray;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		} finally {
			try {
				if (randomAccessFile != null) {
					randomAccessFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_添加缓存文件 （对象）
	 * 
	 * @param key_文件标识
	 * @param value_对象
	 */
	public void put(String key, Serializable value) {

		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);

			oos.writeObject(value);
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_获取缓存文件 （对象）
	 * 
	 * @param key_文件标识
	 * @return 对象
	 */
	public Object getAsObject(String key) {

		byte[] data = getAsByte(key);

		if (data == null) {

			return null;
		}

		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;

		try {
			bais = new ByteArrayInputStream(data);
			ois = new ObjectInputStream(bais);

			Object obj = ois.readObject();

			return obj;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (bais != null) {
					bais.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_添加缓存文件 （Bitmap）
	 * 
	 * @param key_文件标识
	 * @param value_Bitmap
	 *            对象
	 */
	public void put(String key, Bitmap value) {

		put(key, MgrT.getInstance().getBitmap2Bytes(value));
	}

	/**
	 * Method_获取缓存文件 （Bitmap）
	 * 
	 * @param key_文件标识
	 * @return Bitmap 对象
	 */
	public Bitmap getAsBitmap(String key) {

		byte[] data = getAsByte(key);

		if (data == null) {
			return null;
		}

		return MgrT.getInstance().getBytes2Bitmap(data);
	}

	/**
	 * Method_添加缓存文件 （Drawable）
	 * 
	 * @param key_文件标识
	 * @param value_Drawable
	 *            对象
	 */
	public void put(String key, Drawable value) {

		put(key, MgrT.getInstance().getDrawable2Bitmap(value));
	}

	/**
	 * Method_获取缓存文件 （Drawable）
	 * 
	 * @param key_文件标识
	 * @return Drawable 对象
	 */
	public Drawable getAsDrawable(String key) {

		return MgrT.getInstance().getBitmap2Drawable(getAsBitmap(key));
	}

	/**
	 * Method_获取缓存文件 （File）
	 * 
	 * @param key_文件标识
	 * @return 文件对象
	 */
	public File getAsFile(String key) {

		File file = createFile(key);

		if (file.exists()) {
			return file;
		}

		return null;
	}

	/**
	 * Method_移除缓存文件
	 * 
	 * @param key_文件标识
	 * @return 结果
	 */
	public boolean remove(String key) {

		File f = createFile(key);

		return f.delete();
	}

	/**
	 * Method_清理缓存文件
	 */
	public void clear() {

		if (mCacheFile == null) {
			return;
		}

		if (mInstanceMap != null) {
			mInstanceMap.clear();
		}

		MgrThread.getInstance().execute(new Runnable() {

			@Override
			public void run() {

				File[] cacheFiles = mCacheFile.listFiles();

				if (cacheFiles == null) {

					return;
				}

				for (int i = 0; i < cacheFiles.length; i++) {
					File f = cacheFiles[i];

					if (f.isFile()) {
						f.delete();
					}
				}
			}
		});
	}

	/**
	 * Method_清理缓存文件任务
	 */
	public void clearTask() {

		Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				if (mCacheFile == null) {

					return;
				}

				File[] cacheFiles = mCacheFile.listFiles();

				if (cacheFiles == null) {

					return;
				}

				Map<String, Long> cacheFileMap = new HashMap<String, Long>();

				for (File cacheFile : cacheFiles) {
					long lastModfiedTime = cacheFile.lastModified();
					long nowtime = System.currentTimeMillis();

					if ((nowtime - lastModfiedTime) >= mClearTime) {
						cacheFile.delete();
					}
					cacheFileMap.put(cacheFile.getName(), lastModfiedTime);
				}

				mCacheFileList.addAll(cacheFileMap.entrySet());

				Collections.sort(mCacheFileList, new Comparator<Map.Entry<String, Long>>() {

					@Override
					public int compare(Entry<String, Long> lhs, Entry<String, Long> rhs) {

						return rhs.getValue().compareTo(lhs.getValue());
					}
				});
			}
		}, mClearDay, mClearTime);
	}

	/**
	 * Method_创建新文件
	 * 
	 * @param key_文件标识
	 * @return 文件对象
	 */
	public File createFile(String key) {

		return new File(mCacheFile, key);
	}

	/**
	 * Method_获取文件
	 * 
	 * @param key_文件标识
	 * @return 文件对象
	 */
	public File getFile(String key) {

		File file = createFile(key);

		if (file == null || !file.exists()) {
			return null;
		}

		Long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);

		return file;
	}

}
