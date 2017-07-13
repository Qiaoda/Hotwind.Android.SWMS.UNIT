package com.jiebao.scanlib.activity;


import com.jiebao.scanlib.ScanHintManager;
import com.jiebao.scanlib.ScanListener;
import com.jiebao.scanlib.ScanService;
import com.jiebao.scanlib.ScanService.MyBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

/**
 * 所有和业务操作（扫描）有关的activity都继承自这个activity
 * 该类为其子类提供扫描服务
 * @author Administrator
 * 
 */
public abstract class BaseOperationActivity extends FragmentActivity implements
		ScanListener {
	
	protected ScanHintManager mScanHintManager; 
	
	public ServiceConnection serviceConnection = new ServiceConnection() {

		private ScanService scanService;

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MyBinder myBinder = (MyBinder) service;
			scanService = myBinder.getService();
			scanService.setOnScanListener(BaseOperationActivity.this);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 开启扫描服务
		startService(new Intent(BaseOperationActivity.this,ScanService.class));
		// 绑定服务
		bindService(new Intent(BaseOperationActivity.this,ScanService.class), serviceConnection,Context.BIND_AUTO_CREATE);
		mScanHintManager=new ScanHintManager(this, true, true);
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		//鉴于省电的需要，当用户退出时
				//关闭扫描服务
				if(serviceConnection!=null){
					unbindService(serviceConnection);
					stopService(new Intent(BaseOperationActivity.this,ScanService.class));
				}
	}

	/**
	 * 该方法运行在子线程 所有这里调用runOnUiThread
	 */
	@Override
	public void result(byte[] data) {
		final String code = new String(data);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				fillCode(code);
			}
		});
	}

	/**
	 * 填充扫描到的单号
	 * 
	 * @param code
	 */
	public abstract void fillCode(String code);

}
