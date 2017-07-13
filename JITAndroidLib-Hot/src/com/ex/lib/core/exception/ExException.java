/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.exception;

/**
 * @ClassName: ExException
 * @Description: ExException 异常数据接口
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class ExException extends Throwable {

	/**
	 * FINAL_序列 ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construction_构造函数
	 */
	public ExException() {
	}

	/**
	 * Construction_构造函数
	 * 
	 * @param detailMessage_详细信息
	 */
	public ExException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * Construction_构造函数
	 * 
	 * @param throwable_异常对象
	 */
	public ExException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * Construction_构造函数
	 * 
	 * @param detailMessage_详细信息
	 * @param throwable_异常对象
	 */
	public ExException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}