/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */
package com.ex.lib.core.utils.mgr;

import org.litepal.tablemanager.Connector;

import android.database.sqlite.SQLiteDatabase;

/**
 * @ClassName: MgrDB
 * @Description: MgrDB 管理类
 * @author Aloneter
 * @date 2014-10-24 下午5:53:20
 * @Version 1.0
 * 
 */
public class MgrDB {

	/**
	 * 创建者
	 */
	private static class DBHolder {

		private static final MgrDB mgr = new MgrDB();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrDB getInstance() {

		return DBHolder.mgr;
	}

	/**
	 * Method_初始化数据库
	 * 
	 * @return 初始化结果
	 */
	public boolean init() {

		try {
			SQLiteDatabase db = Connector.getDatabase();
			if (db != null) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}
