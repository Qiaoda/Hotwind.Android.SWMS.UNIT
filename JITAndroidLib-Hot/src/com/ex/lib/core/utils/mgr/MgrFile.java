/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * @ClassName: MgrFile
 * @Description: MgrFile 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrFile {

	/**
	 * 创建者
	 */
	private static class SpaceHolder {

		private static final MgrFile mgr = new MgrFile();
	}

	/**
	 * 获取当前实例
	 * 
	 * @return
	 */
	public static MgrFile getInstance() {

		return SpaceHolder.mgr;
	}

	/**
	 * Method_计算剩余空间
	 * 
	 * @param path_路径
	 * @return 结果
	 */
	private long getAvailableSize(String path) {

		StatFs fileStats = new StatFs(path);
		fileStats.restat(path);

		return (long) fileStats.getAvailableBlocks() * fileStats.getBlockSize(); // 注意与fileStats.getFreeBlocks()的区别
	}

	/**
	 * Method_计算总空间
	 * 
	 * @param path_路径
	 * @return 结果
	 */
	private long getTotalSize(String path) {

		StatFs fileStats = new StatFs(path);
		fileStats.restat(path);

		return (long) fileStats.getBlockCount() * fileStats.getBlockSize();
	}

	/**
	 * Method_计算SD卡的剩余空间
	 * 
	 * @return 结果
	 */
	public long getSDAvailableSize() {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			return getAvailableSize(Environment.getExternalStorageDirectory().toString());
		}

		return 0;
	}

	/**
	 * Method_计算系统的剩余空间
	 * 
	 * @return 结果
	 */
	public long getSystemAvailableSize() {

		return getAvailableSize("/data");
	}

	/**
	 * Method_是否有足够的空间
	 * 
	 * @param filePath_文件路径
	 * @return 结果
	 */
	@SuppressLint("SdCardPath")
	public boolean hasEnoughMemory(String filePath) {

		File file = new File(filePath);
		long length = file.length();

		if (filePath.startsWith("/sdcard") || filePath.startsWith("/mnt/sdcard")) {

			return getSDAvailableSize() > length;
		} else {

			return getSystemAvailableSize() > length;
		}
	}

	/**
	 * Method_获取SD卡的总空间
	 * 
	 * @return 结果
	 */
	public long getSDTotalSize() {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			return getTotalSize(Environment.getExternalStorageDirectory().toString());
		}

		return 0;
	}

	/**
	 * Method_获取系统可读写的总空间
	 * 
	 * @return 结果
	 */
	public long getSysTotalSize() {

		return getTotalSize("/data");
	}

	/**
	 * Method_是否有可用空间
	 * 
	 * @return 结果
	 */
	public boolean isAvailableSpace() {

		if (getSDAvailableSize() / 1024 > 10 || getSystemAvailableSize() / 1024 > 10) {

			return true;
		}

		return false;
	}

	/**
	 * Method_将文件保存到本地
	 */
	public void saveFileCache(byte[] fileData, String folderPath, String fileName) {

		File folder = new File(folderPath);
		folder.mkdirs();

		File file = new File(folderPath, fileName);
		ByteArrayInputStream is = new ByteArrayInputStream(fileData);

		OutputStream os = null;

		if (!file.exists()) {
			try {
				file.createNewFile();
				os = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;

				while (-1 != (len = is.read(buffer))) {
					os.write(buffer, 0, len);
				}

				os.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {

				closeIO(is, os);
			}
		}
	}

	/**
	 * Method_从指定文件夹获取文件
	 */
	public File getSaveFile(String folder, String fileNmae) {

		File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + folder + "/" + fileNmae);

		return file.exists() ? file : null;
	}

	/**
	 * Method_获取文件夹路径
	 */
	public String getSavePath(String folderName) {

		File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + folderName + "/");
		file.mkdirs();

		return file.getAbsolutePath();
	}

	/**
	 * Method_复制文件
	 * 
	 * @param from_来源
	 * @param to_目标
	 */
	public void copyFile(File from, File to) {

		if (null == from || !from.exists()) {

			return;
		}
		if (null == to) {

			return;
		}
		InputStream is = null;
		OutputStream os = null;

		try {
			is = new FileInputStream(from);

			if (!to.exists()) {
				to.createNewFile();
			}

			os = new FileOutputStream(to);

			byte[] buffer = new byte[1024];
			int len = 0;

			while (-1 != (len = is.read(buffer))) {
				os.write(buffer, 0, len);
			}

			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			closeIO(is, os);
		}
	}

	/**
	 * Method_关闭流
	 * 
	 * @param closeables
	 *            需要关闭的流对象
	 */
	public void closeIO(Closeable... closeables) {

		if (null == closeables || closeables.length <= 0) {

			return;
		}

		for (Closeable cb : closeables) {
			try {
				if (null == cb) {

					continue;
				}

				cb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method_从文件中读取文本
	 * 
	 * @param filePath_文件路径
	 * @return 结果
	 */
	public String readFile(String filePath) {

		InputStream is = null;

		try {
			is = new FileInputStream(filePath);
		} catch (Exception e) {
		}

		return MgrT.getInstance().getInStream2Str(is);
	}

	/**
	 * Method_从 assets 中读取文本
	 * 
	 * @param name_名字
	 * @return 结果
	 */
	public String readFileFromAssets(Context context, String name) {

		InputStream is = null;

		try {
			is = context.getResources().getAssets().open(name);
		} catch (Exception e) {
		}

		return MgrT.getInstance().getInStream2Str(is);
	}

}
