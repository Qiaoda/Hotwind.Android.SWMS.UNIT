/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;
import android.widget.RemoteViews;
import com.ex.lib.R;

/**
 * @ClassName: MgrUpdate
 * @Description: MgrUpdate 管理类
 * @author Aloneter
 * @date 2014年11月8日 下午2:09:10
 * @Version 1.0
 * 
 */
@SuppressLint("InlinedApi")
public class MgrUpdate {

	private static Context mContext;

	private final static String ACTION_BUTTON = "action_button"; // 按钮事件通知
	private final static String INTENT_BUTTONID_TAG = "intent_button_id_tag"; // 意图按钮
	private final static String BUTTON_START_ID = "button_start_id"; // 按钮中的开始
	private final static String INTENT_BUTTONSTATUS_TAG = "intent_button_status_tag"; // 意图按钮开始

	private NotificationManager notificationManager; // 通知管理器
	private AppInfo app; // 下载应用对象

	/**
	 * 创建者
	 */
	private static class UpdateHolder {

		private static final MgrUpdate mgr = new MgrUpdate();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrUpdate getInstance(Context context) {

		if (mContext == null) {
			mContext = context;
		}

		return UpdateHolder.mgr;
	}

	/**
	 * Method_注册更新广播
	 */
	public void registerReceiver() {

		mContext.registerReceiver(receiver, new IntentFilter(ACTION_BUTTON));
	}

	/**
	 * Method_注销更新广播
	 */
	public void unregisterReceiver() {

		try {
			if (receiver != null) {
				mContext.unregisterReceiver(receiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method_启动更新
	 * 
	 * @param name_应用名
	 * @param apkUrl_下载
	 *            URL
	 */
	public void update(String name, String apkUrl) {

		app = new AppInfo();

		app.id = 10;
		app.name = name;
		app.apkUrl = apkUrl;
		app.ticker = "启动下载";
		app.saveFilePath = Environment.getExternalStorageDirectory() + "/update_" + app.name + "" + ".ap0";

		showButtonNotify(false, app);

		app.run = new DownRunnable(mContext, app);

		Thread thread = new Thread(app.run);
		thread.start();
	}

	/**
	 * Method_显示通知信息
	 * 
	 * @param isStatus_状态判断
	 * @param app_应用信息
	 */
	private void showButtonNotify(boolean isStatus, AppInfo app) {

		String status = AppInfo.DOWN_STOP + "";

		clearNotification(app.id);

		notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder = new Builder(mContext);

		RemoteViews mRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.ex_notification_app_update);

		mRemoteViews.setTextViewText(R.id.notificationTitle, app.name);
		mRemoteViews.setTextViewText(R.id.notificationPercent, app.currPropress + "%");

		if (app.currPropress != 0 && app.currPropress != 100) {
			mRemoteViews.setProgressBar(R.id.notificationProgress, 100, app.currPropress, false);
		}

		mRemoteViews.setImageViewResource(R.id.notificationBtn, R.drawable.ex_button_pause);

		if (isStatus) {
			status = AppInfo.DOWN_START + "";
			mRemoteViews.setImageViewResource(R.id.notificationBtn, R.drawable.ex_button_start);
		}

		Intent buttonIntent = new Intent(ACTION_BUTTON);

		buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_START_ID);
		buttonIntent.putExtra(INTENT_BUTTONSTATUS_TAG, status);

		// 这里加了广播，所及INTENT的必须用getBroadcast方法
		PendingIntent intent_prev = PendingIntent.getBroadcast(mContext, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mRemoteViews.setOnClickPendingIntent(R.id.notificationBtn, intent_prev);

		// 点击该通知后要跳转的Activity
		Intent notificationIntent = new Intent(mContext, mContext.getClass());
		PendingIntent contentItent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

		mBuilder.setContent(mRemoteViews);
		mBuilder.setContentIntent(contentItent);
		// 通知产生的时间，会在通知信息里显示
		mBuilder.setWhen(System.currentTimeMillis());
		mBuilder.setTicker(app.ticker);
		// 设置该通知优先级
		mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
		mBuilder.setOngoing(true);
		mBuilder.setSmallIcon(R.drawable.ex_app_notification_icon);

		Notification notify = mBuilder.build();

		notify.flags = Notification.FLAG_ONGOING_EVENT;

		app.notification = notify;
	}

	/**
	 * NEW_广播处理对象
	 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(ACTION_BUTTON)) {
				String value = intent.getExtras().getString(INTENT_BUTTONID_TAG);
				String status = intent.getExtras().getString(INTENT_BUTTONSTATUS_TAG);

				if (value.equals(BUTTON_START_ID)) {
					if (status.equals(AppInfo.DOWN_START + "")) {
						if (app == null) {
							return;
						}

						update(app.name, app.apkUrl);
					} else {
						app.run.isStop = true;

						showButtonNotify(true, app);
						notificationManager.notify(app.id, app.notification);
					}
				}
			}
		}
	};

	/**
	 * Method_清除通知栏信息
	 * 
	 * @param id_通知编号
	 */
	private void clearNotification(int id) {

		NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(id);
	}

	/**
	 * NEW_应用信息对象
	 */
	private static class AppInfo {

		/**
		 * FINAL_下载开始
		 */
		public final static int DOWN_START = 0;
		/**
		 * FINAL_下载停止
		 */
		public final static int DOWN_STOP = 1;
		/**
		 * FINAL_下载成功
		 */
		public final static int DOWN_SUCCESS = 2;

		public int id; // 应应用编号
		public String name; // 应用名称
		public String saveFilePath; // 保存地址

		public long currSize; // 当前大小
		public long totalSize; // 总大小

		public int currPropress; // 当前进度

		public Notification notification; // 通知对象
		public String apkUrl; // 应用下载 URL
		public String ticker; // 应用提示信息

		public DownRunnable run; // 应用下载对象
		public int status; // 下载状态
	}

	/**
	 * NEW_下载进程对象
	 */
	private class DownRunnable implements Runnable {

		private AppInfo mEntity = null;
		private Context mContext = null;
		private long totalSize = 0;

		public boolean isStop = false;

		public DownRunnable(Context context, AppInfo entity) {

			mEntity = entity;
			mContext = context;
		}

		@Override
		public void run() {

			downloadApp(mEntity);
		}

		private void downloadApp(AppInfo app) {

			HttpURLConnection httpConnection = null;
			InputStream is = null;
			FileOutputStream fos = null;

			try {
				File file = new File(app.saveFilePath);

				if (!file.exists()) {
					String destFileName = app.saveFilePath.replace(".ap0", ".apk");
					renameFile(destFileName, app.saveFilePath);
				}

				if (!file.exists()) {
					file.createNewFile();
				}

				app.currSize = file.length();
				String urlStr = app.apkUrl;

				if (totalSize == 0) {
					if (!TextUtils.isEmpty(urlStr)) {
						URL l = new URL(urlStr);
						HttpURLConnection c = (HttpURLConnection) l.openConnection();

						if (HttpURLConnection.HTTP_OK == c.getResponseCode()) {
							totalSize = c.getContentLength();
						}

						if (c != null) {
							c.disconnect();
						}
					}
				}

				app.totalSize = totalSize;

				if (app.currSize >= app.totalSize) {
					app.currPropress = 100;
					app.status = AppInfo.DOWN_SUCCESS;

					String destFileName = app.saveFilePath.replace(".ap0", ".apk");

					if (renameFile(app.saveFilePath, destFileName)) {
						app.saveFilePath = destFileName;
					}

					MgrApp.getInstance(mContext).install(app.saveFilePath);

					return;
				}

				URL url = new URL(urlStr);
				httpConnection = (HttpURLConnection) url.openConnection();

				httpConnection.setConnectTimeout(200000);
				httpConnection.setReadTimeout(200000);
				httpConnection.setRequestProperty("Connection", "Keep-Alive");

				String range = "bytes=" + app.currSize + "-";

				httpConnection.setRequestProperty("RANGE", range);

				int ret = httpConnection.getResponseCode();

				if (ret == HttpURLConnection.HTTP_OK || ret == HttpURLConnection.HTTP_PARTIAL) {
					is = httpConnection.getInputStream();

					// 206断点续传
					fos = new FileOutputStream(file, ret == HttpURLConnection.HTTP_PARTIAL ? true : false);

					int bufflen = 1024;
					byte buffer[] = new byte[bufflen];
					int readsize = 0;

					// 初始化消息栏的百分比
					int progress = (int) (app.currSize * 100 / app.totalSize);

					app.currPropress = progress >= 100 ? 100 : progress;
					app.notification.contentView.setTextViewText(R.id.notificationPercent, app.currPropress + "%");
					app.notification.contentView.setProgressBar(R.id.notificationProgress, 100, app.currPropress, false);

					notificationManager.notify(app.id, app.notification);

					while ((readsize = is.read(buffer)) > 0) {
						if (isStop) {
							app.status = AppInfo.DOWN_STOP;
							app.ticker = app.name + ":暂停下载";

							notificationManager.notify(app.id, app.notification);

							break;
						}

						app.status = AppInfo.DOWN_START;
						fos.write(buffer, 0, readsize);
						app.currSize += readsize;

						progress = (int) (app.currSize * 100 / app.totalSize);

						if (app.currPropress != progress && progress <= 100) {
							app.currPropress = progress >= 100 ? 100 : progress;

							// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
							if ((app.currPropress % 10) == 0) {

								app.notification.contentView.setTextViewText(R.id.notificationPercent, app.currPropress + "%");
								app.notification.contentView.setProgressBar(R.id.notificationProgress, 100, app.currPropress, false);

								notificationManager.notify(app.id, app.notification);
							}
						}

						if (app.currPropress == 100) {
							app.status = AppInfo.DOWN_SUCCESS;
						}
					}

					if (app.status == AppInfo.DOWN_SUCCESS) {
						String destFileName = app.saveFilePath.replace(".ap0", ".apk");

						if (renameFile(app.saveFilePath, destFileName)) {
							app.saveFilePath = destFileName;
						}

						MgrApp.getInstance(mContext).install(app.saveFilePath);

						clearNotification(app.id);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (httpConnection != null) {
					httpConnection.disconnect();
				}

				try {
					if (is != null) {
						is.close();
					}
					if (fos != null) {
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Method_重命名
	 * 
	 * @param srcFilePath
	 * @param destFilePath
	 * @return
	 */
	private boolean renameFile(String srcFilePath, String destFilePath) {

		File srcfile = new File(srcFilePath);
		File destfile = new File(destFilePath);

		return srcfile.renameTo(destfile);
	}

}
