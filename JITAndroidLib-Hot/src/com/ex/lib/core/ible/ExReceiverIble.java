/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.ible;

import android.content.Intent;

/**
 * @ClassName: ExReceiverIble
 * @Description: 广播处理接口
 * @author Aloneter
 * @date 2014-10-24 下午5:35:03
 * @Version 1.0
 * 
 */
public interface ExReceiverIble {

	/**
	 * Method_广播接口回调
	 * 
	 * @param intent_操作意图对象
	 */
	public void onReceiver(Intent intent);

}
