package cn.jitmarketing.hot.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.jitmarketing.hot.LoginActivity;
import cn.jitmarketing.hot.MainActivity;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.UpDataBean;
import cn.jitmarketing.hot.ui.shelf.NewInStockCompleteActivity;
import de.greenrobot.event.EventBus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class UpdateService extends Service {

	// 下载状态
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;

	// 标题
	private int titleId = 0;
	
	private String downUrl;//下载地址

	// 文件存储
	private File updateFile = null;
	public static final String fileName = "LoginActivity.apk"; 

	// 通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获取传值
		titleId = intent.getIntExtra("titleId", 0);
		downUrl = intent.getStringExtra("downUrl");
		updateFile = new File(getFilesDir(), fileName);

//		this.updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		this.updateNotification = new Notification();
//
//		// 设置下载过程中，点击通知栏，回到主界面
//		updateIntent = new Intent(this, MainActivity.class);
//		updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent,
//				0);
//		// 设置通知栏显示内容
//		updateNotification.icon = R.drawable.ic_launcher;
//		updateNotification.tickerText = "开始下载";
//		updateNotification.setLatestEventInfo(this, "热风门店", "0%",
//				null);
//		// 发出通知
//		updateNotificationManager.notify(0, updateNotification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new updateRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, flags, startId);
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent
//				Uri uri = Uri.fromFile(updateFile);
//				Intent installIntent = new Intent(Intent.ACTION_VIEW);
//				installIntent.setDataAndType(uri,
//						"application/vnd.android.package-archive");
//				updatePendingIntent = PendingIntent.getActivity(
//						UpdateService.this, 0, installIntent, 0);
//
//				updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
//				updateNotification.setLatestEventInfo(UpdateService.this,
//						"热风门店", "下载完成,点击安装。", updatePendingIntent);
//				updateNotificationManager.notify(0, updateNotification);
				/*强制自动更新*/
				 if (!updateFile.exists()) {
					   return;
					  }
				 // 通过Intent安装APK文件
				  Intent i = new Intent(Intent.ACTION_VIEW); 
				  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				  i.setDataAndType(Uri.parse("file://" + updateFile.toString()),
				    "application/vnd.android.package-archive");
				  startActivity(i);
				  android.os.Process.killProcess(android.os.Process.myPid());
				// 停止服务
				stopSelf();
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
//				updateNotification.setLatestEventInfo(UpdateService.this,
//						"热风门店", "下载失败。", null);
//				updateNotificationManager.notify(0, updateNotification);
				EventBus.getDefault().post(new UpDataBean(0, "0%", true, false)); 
				// 停止服务
				stopSelf();
				break;
			default:
				stopSelf();
			}
		}
	};

	class updateRunnable implements Runnable {
		Message message = updateHandler.obtainMessage();

		public void run() {
			try {
				if (!updateFile.exists()) {
					updateFile.createNewFile();
				}
				// 下载函数，以QQ为例子
				// 增加权限;
				boolean downloadSize = downloadUpdateFile(
							downUrl,
							updateFile);
				if (downloadSize) {
					// 下载成功
					message.what = DOWNLOAD_COMPLETE;
					updateHandler.sendMessage(message);
				} else {
					message.what = DOWNLOAD_FAIL;
					updateHandler.sendMessage(message);
				}
			} catch (Exception e) {
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				updateHandler.sendMessage(message);
			}
		}
	}

	public boolean downloadUpdateFile(String downloadUrl, File saveFile)
			throws Exception {
		// 这样的下载代码很多，我就不做过多的说明
		int downloadCount = 0;
		int currentSize = 0;
		long totalSize = 0;
		int updateTotalSize = 0;
		
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
//			fos = new FileOutputStream(saveFile, false);
			fos = openFileOutput(fileName, MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);   
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 5 > downloadCount) {
					downloadCount += 1;
					EventBus.getDefault().post(new UpDataBean((int) totalSize * 100 / updateTotalSize, (int) totalSize * 100 / updateTotalSize+"%", false, false));
//					updateNotification.setLatestEventInfo(UpdateService.this,
//							"正在下载", (int) totalSize * 100 / updateTotalSize
//									+ "%", updatePendingIntent);
//					updateNotificationManager.notify(0, updateNotification);
				}
			}
			if(totalSize == updateTotalSize) {
				EventBus.getDefault().post(new UpDataBean(100, "100%", false, true));
				return true;
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return false;
	}

}
