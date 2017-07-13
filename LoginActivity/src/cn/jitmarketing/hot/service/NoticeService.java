package cn.jitmarketing.hot.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.MqNoticeBean;
import cn.jitmarketing.hot.ui.sku.TakeGoodsActivity;
import cn.jitmarketing.hot.util.LogUtils;

import com.ex.lib.core.utils.mgr.MgrPerference;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class NoticeService extends Service {

	private Thread noticeThread = null;
	private com.rabbitmq.client.Connection con = null;
	private com.rabbitmq.client.Channel channel = null;
	private NotificationManager manager = null;
	private int currentNotificationIndex = 0;
	private Gson mGson = new Gson();
	private SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				String objstring = msg.obj.toString();
				MqNoticeBean noticeBean = mGson.fromJson(objstring, MqNoticeBean.class);
				notice(noticeBean);
				// notice1(noticeBean);
				break;
			default:
				super.handleMessage(msg);
				break;
			}

		}
	};

	/**
	 * 通知信息
	 */
	private void notice(MqNoticeBean noticeBean) {
		// 定义NotificationManager
		Notification notification = new Notification();// 定义一个消息类
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击自动清除通知
		notification.defaults = Notification.DEFAULT_VIBRATE;// 震动
		notification.icon = R.drawable.ic_launcher;// 设置图标
		notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);
		notification.when = System.currentTimeMillis();// 设置时间
		// 自定义样式
		RemoteViews contentView = new RemoteViews(this.getPackageName(), R.layout.notify_view);// 初始化自定义的通知样式
		contentView.setImageViewResource(R.id.notify_image, R.drawable.ic_launcher);
		contentView.setTextViewText(R.id.notify_title, noticeBean.Title);//
		contentView.setTextViewText(R.id.notify_tick, noticeBean.Content);//
		contentView.setTextViewText(R.id.notify_sub, noticeBean.Ticker);//
		contentView.setTextViewText(R.id.notify_sub, noticeBean.Ticker);//
		contentView.setTextViewText(R.id.notify_time, df.format(new Date()));//
		notification.contentView = contentView;// 设置通知样式为自定义的样式

		if (currentNotificationIndex == 5) {
			currentNotificationIndex = 0;
		}

		if (noticeBean.Type.startsWith("outgoods.")) {// 拿货通知
			if (noticeBean.SubType.equals("1")) {
				Intent intent = new Intent(this, TakeGoodsActivity.class);
				Bundle bundle = new Bundle();
				if (!noticeBean.IsOutSample) {// 取新
					notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.quxin);
					bundle.putString("tab", "quxin");
				} else {// 出样
					notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chuyang);
					bundle.putString("tab", "chuyang");
				}
				intent.putExtras(bundle);
				PendingIntent contentIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
				notification.contentIntent = contentIntent;
			} else if (noticeBean.SubType.equals("2")) {// 取消通知
				Intent intent = new Intent();
				PendingIntent contentIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
				notification.contentIntent = contentIntent;
			}
		} else if (noticeBean.Type.startsWith("notfound")) {// 未找到通知
			if (!noticeBean.Requester.equals(MgrPerference.getInstance(this).getString(HotConstants.User.SHARE_USERID))) {
				return;
			}
			Intent intent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
			notification.contentIntent = contentIntent;
		} else if (noticeBean.Type.startsWith("waiting.for.reset")) {// 待复位通知
			Intent intent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
			notification.contentIntent = contentIntent;
		} else if (noticeBean.Type.startsWith("check")) {// 盘点通知
			Intent intent = new Intent();
			PendingIntent contentIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
			notification.contentIntent = contentIntent;
		}
		// if(noticeBean.SubType.equals("1")) {//拿货通知
		// Intent intent = new Intent(this, TakeGoodsActivity.class);
		// Bundle bundle = new Bundle();
		// if(!noticeBean.IsOutSample) {//取新
		// notification.sound = Uri.parse("android.resource://" +
		// getPackageName() + "/" +R.raw.quxin);
		// bundle.putString("tab", "quxin");
		// } else {
		// notification.sound = Uri.parse("android.resource://" +
		// getPackageName() + "/" +R.raw.chuyang);
		// bundle.putString("tab", "chuyang");
		// }
		// intent.putExtras(bundle);
		// PendingIntent contentIntent = PendingIntent.getActivity(this,
		// currentNotificationIndex, intent, 0);
		// notification.contentIntent = contentIntent;
		// } else if(noticeBean.SubType.equals("2")) {//取消申请通知
		// Intent intent = new Intent();
		// PendingIntent contentIntent = PendingIntent.getActivity(this,
		// currentNotificationIndex, intent, 0);
		// notification.contentIntent = contentIntent;
		// } else if(noticeBean.SubType.equals("3")) {//待复位通知
		// Intent intent = new Intent();
		// PendingIntent contentIntent = PendingIntent.getActivity(this,
		// currentNotificationIndex, intent, 0);
		// notification.contentIntent = contentIntent;
		// }
		manager.notify(currentNotificationIndex, notification);
		currentNotificationIndex++;
	}

	private void notice1(MqNoticeBean noticeBean) {
		Notification notification = new Notification();// 定义一个消息类
		Notification.Builder builder = new Notification.Builder(this);
		if (noticeBean.SubType.equals("1")) {
			Intent intent = new Intent(this, TakeGoodsActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
			builder.setAutoCancel(true).setContentTitle(noticeBean.Title).setContentText(noticeBean.Ticker).setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm)).setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis());
		} else if (noticeBean.SubType.equals("2")) {
			Intent intent = new Intent();
			PendingIntent pendingIntent = PendingIntent.getActivity(this, currentNotificationIndex, intent, 0);
			builder.setAutoCancel(true).setContentTitle(noticeBean.Title).setContentText(noticeBean.Ticker).setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm)).setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher).setWhen(System.currentTimeMillis());
		}
		manager.notify(currentNotificationIndex, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.w("Service", "onCreate");
		if (manager == null)
			manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onDestroy() {
		if (channel != null) {
			try {
				channel.close();
			} catch (TimeoutException e) {
				Log.d("Service", "mq通道关闭失败!" + e.getMessage());
			} catch (IOException e) {
				Log.d("Service", "mq通道关闭失败!" + e.getMessage());
			}
			channel = null;
		}
		if (con != null) {
			try {
				con.close();

			} catch (IOException e) {
				Log.d("Service", "mq连接关闭失败!" + e.getMessage());
			}
			channel = null;
		}

		if (noticeThread != null) {
			try {
				noticeThread.interrupt();
			} catch (Exception ex) {
			}
			noticeThread = null;
		}
		super.onDestroy();
		Log.w("Service", "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		colse();
		noticeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				doLoopMQMessage();
			}
		});
		noticeThread.start();
	}

	private void colse() {
		if (channel != null && channel.isOpen()) {
			try {
				channel.close();
			} catch (TimeoutException e) {
				Log.d("Service", "mq通道关闭失败!" + e.getMessage());
			} catch (IOException e) {
				Log.d("Service", "mq通道关闭失败!" + e.getMessage());
			}
			channel = null;
		}
		if (noticeThread != null) {
			try {
				noticeThread.interrupt();
			} catch (Exception ex) {
			}
			noticeThread = null;
		}
	}

	protected void doLoopMQMessage() {
		String role = MgrPerference.getInstance(this).getString(HotConstants.Unit.ROLE_CODE);
		ArrayList<GroupEntity> groupList = (ArrayList<GroupEntity>) MgrPerference.getInstance(this).getObject(role);
		boolean isok = false;
		while (!isok) {
			try {
				com.rabbitmq.client.ConnectionFactory cfa = new ConnectionFactory();
				cfa.setAutomaticRecoveryEnabled(true);
				cfa.setUsername("swms");
				cfa.setPassword("hotwind@2015");
				cfa.setHost(HotConstants.Global.APP_URL_USER.split(":")[1].replaceAll("/", ""));
				con = cfa.newConnection();
				channel = con.createChannel();
				channel.exchangeDeclare("swms", "topic", true);
				String queuename = channel.queueDeclare().getQueue();// MgrPerference.getInstance(this).getString(HotConstants.User.USER_CODE)+
																		// new
																		// Date().getTime();

				Consumer consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
						String message = new String(body, "UTF-8");
						Log.w("NOTICE", " [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
						LogUtils.logOnFile("推送消息：" + message);
						handler.sendMessage(handler.obtainMessage(1, message));
					}
				};
				for (GroupEntity group : groupList) {
					for (NoticeSettingEntity noticeEntity : group.getSublist()) {
						if (noticeEntity.getOpen()) {
							String routekey = noticeEntity.getKey();
							channel.queueBind(queuename, "swms", routekey);
						}
					}
				}
				channel.basicConsume(queuename, false, consumer);
				isok = true;
				LogUtils.logOnFile("RabbitMQ连接成功");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
}
