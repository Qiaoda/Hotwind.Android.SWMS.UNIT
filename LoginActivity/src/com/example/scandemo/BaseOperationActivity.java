package com.example.scandemo;



import com.android.barcodescandemo.ScannerInerface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import cn.jitmarketing.hot.BaseActivity;
import cn.jitmarketing.hot.util.LogUtils;

/**
 * 所有和业务操作（扫描）有关的activity都继承自这个activity 该类为其子类提供扫描服务
 * 
 * @author Administrator
 * 
 */
public abstract class BaseOperationActivity extends BaseActivity {
	private static final String SCN_CUST_ACTION_SCODE = "com.android.server.scannerservice.broadcast";
	private static final String SCN_CUST_EX_SCODE = "scannerdata";
	private final static String SCAN_ACTION = "urovo.rcv.message";
	private static final String SCANACTION = "com.scan";
	public static final int FLAG_SCAN = -14;// 扫描操作标记
	public static final int FLAG_SCAN_ADD = -15;// 扫描添加
	public static final int FLAG_SCAN_REFRESH = -16;// 扫描刷新
	public static final int FLAG_SCAN_NOCACHE = -17;// 扫描 无缓存订单信息，需要联网获取
	public static final String JIEBAO_HT380 = "HT380";
	public static final String UROVO_I6200S = "i6200S";
	private static final String GIONEELY72_KK = "gioneely72_cwet_kk";//黑色新款
	private String model;
	protected boolean isScan;
	 /*GIONEELY72_KK相关操作*/
    private ScannerInerface scanner;
    private IntentFilter intentFilter;
    private BroadcastReceiver scanReceiver;
    private static final String RES_ACTION = "android.intent.action.SCANRESULT";

	@Override
	protected void onResume() {
		model = android.os.Build.MODEL;
		Log.i("TTT", model);
		// 开启扫描服务
		if (model.contains(JIEBAO_HT380)) {
		Intent mIntent = new Intent(this,ScanService.class);
		startService(mIntent);
		IntentFilter filter = new IntentFilter();
		filter.addAction(ScanService.CODE_RESULT);
		registerReceiver(mReceiver, filter);
		} else if(model.equals(UROVO_I6200S)) {  
			IntentFilter UrovoFilter = new IntentFilter();
			UrovoFilter.addAction(SCAN_ACTION);
			registerReceiver(mUrovoReceiver, UrovoFilter);
		} 
			else if (model.equals(GIONEELY72_KK)){
            scanner = new ScannerInerface(this);
            scanner.open();	//打开iscan中的扫描功能
            scanner.enablePlayBeep(false);//是否允许蜂鸣反馈
            scanner.enablePlayVibrate(false);//是否允许震动反馈
//            scanner.enablShowAPPIcon(true);//是否允许显示iscan的应用程序图标
//            scanner.enablShowNoticeIcon(true);//是否允许显示iscan通知栏图标
            scanner.lockScanKey();
            scanner.setOutputMode(1);
            intentFilter = new IntentFilter(RES_ACTION);
            //注册广播接受者
            scanReceiver = new ScannerResultReceiver();
            registerReceiver(scanReceiver, intentFilter);

        }
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (mReceiver != null&&model.contains(JIEBAO_HT380)) {
			unregisterReceiver(mReceiver);
		}else if (model.equals(UROVO_I6200S)) {
			if (mUrovoReceiver != null) {
				unregisterReceiver(mUrovoReceiver);
			}
		} 
		else if(model.equals(GIONEELY72_KK)&&scanReceiver!=null){
            scanner.scan_stop();
            scanner.lockScanKey();
            unregisterReceiver(scanReceiver);
        }
	
		super.onPause();
	}
	/**
	 * 填充扫描到的单号
	 * 
	 * @param code
	 */
	public abstract void fillCode(String code);

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ScanService.CODE_RESULT)) {
				fillCode(intent.getStringExtra(ScanService.CODE).toUpperCase().trim().replace(" ", ""));
			}
		}

	};
	
	private BroadcastReceiver mUrovoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			byte[] barcode = intent.getByteArrayExtra("barocode");
			int barocodelen = intent.getIntExtra("length", 0);
			String barcodeStr = new String(barcode, 0, barocodelen).toUpperCase().trim().replace(" ", ""); 
			if(!isScan){
				fillCode(barcodeStr);
			}
		}
	};
	
	/*创建GIONEELY72_KK广播接收者*/
    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RES_ACTION)) {
                String barocodeStr = intent.getStringExtra("value");
                fillCode(barocodeStr);
            }
        }
    }

}
