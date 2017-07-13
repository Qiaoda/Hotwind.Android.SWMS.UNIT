package com.jiebao.scanlib;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class WakeLockUtil {
	private static final String TAG = "WakeLockUtil";
	private PowerManager pManager = null;
	private WakeLock mWakeLock = null;

	@SuppressWarnings("deprecation")
	public WakeLockUtil(Context context) {
		pManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
		mWakeLock.setReferenceCounted(false);
	}

	public void lock() {
		if (mWakeLock != null) {
			mWakeLock.acquire(15*1000);
		}
	}

	public void unLock() {
		if (mWakeLock != null) {
			mWakeLock.release();
		}

	}

}
