package cn.jitmarketing.hot.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.NoticeBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.ui.shelf.ResetSkuActivity;
import cn.jitmarketing.hot.ui.shelf.StockTakingActivity;
import cn.jitmarketing.hot.ui.sku.CheckStockActivity;
import cn.jitmarketing.hot.ui.sku.TakeGoodsActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Audio;
import android.util.Log;

public class BootService extends Service {

	private NotificationManager noticeManager;
	//定义通知栏展现的内容信息
	private CharSequence takeGoodsTitle = "拿货通知";
	private CharSequence resetSkuTitle = "复位通知";
	private CharSequence stockTakingTitle = "盘点通知";
	private CharSequence takeGoodsText = "客户要拿货";
	private CharSequence resetSkuText = "待复位商品超过30分钟";
	private CharSequence stockTakingText = "店长创建了盘底任务";
	private CharSequence notFindTitle = "未找到通知";
	private Intent takeGoodsIntent, resetSkuIntent, stockTakingIntent, checkStockIntent;
	private PendingIntent contentIntent;
	private Notification notification;
	private Timer timer = new Timer();
	private Gson mGson = new Gson();

	private final String url = HotConstants.Global.APP_URL_USER + HotConstants.Ser.NEWNOTICE;
	private int num = 1;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		initNotice();
		// 定时任务表
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				requestHttp();
//				noticeMessages("type");
			}
		}, 0, 1 * 10 * 1000);
		super.onCreate();
	}
	
	/**
	 * 初始化通知信息
	 */
	private void initNotice() {
		noticeManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        takeGoodsIntent = new Intent(this, TakeGoodsActivity.class);
        resetSkuIntent = new Intent(this, ResetSkuActivity.class);
        stockTakingIntent = new Intent(this, StockTakingActivity.class);
        checkStockIntent = new Intent(this, CheckStockActivity.class);
        //定义NotificationManager
        notification = new Notification();// 定义一个消息类
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击自动清除通知
        notification.defaults = Notification.DEFAULT_VIBRATE;// 震动
        notification.icon = R.drawable.ic_launcher;// 设置图标
//        notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.alarm);
//        notification.audioStreamType = Notification.DEFAULT_SOUND;
        notification.when = System.currentTimeMillis();// 设置时间
	}
	
	/**
	 * // 发送通知
	 * @param type
	 */
	private void noticeMessages(NoticeBean bean) {
		Bundle bundle = new Bundle();
    	bundle.putString("skuId", bean.SKUCode);
    	String msg = bean.OperPerson + "需要" + bean.OperTypeString + bean.SKUCode;
    	takeGoodsIntent = new Intent(this, TakeGoodsActivity.class);
        if(bean.NoticeType==1) {//1为拿货
        	takeGoodsIntent.putExtras(bundle);
        	if(bean.Type == 4) {//未找到
        		msg = bean.OperPerson + "处理" + bean.OperTypeString + bean.SKUCode + "未找到";
        	}
        	if(bean.OpID == 1024 || bean.OpID == 2048) {//1024 2048取新 ，4096 4098出样
        		notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.quxin);
        		bundle.putString("tab", "quxin");
        	} else if(bean.OpID == 4096 || bean.OpID == 4098) {
        		notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.chuyang);
        		bundle.putString("tab", "chuyang");
        	}
        	if(bean.Type == 4) {//发给销售人的通知
        		contentIntent = PendingIntent.getActivity(this, num, checkStockIntent, 0);
        		notification.setLatestEventInfo(this, takeGoodsTitle, msg, contentIntent);
        	} else {
	        	SharedPreferences shared = getSharedPreferences("selector", Context.MODE_PRIVATE);
	    		String defult=shared.getString("state", "");//获取  筛选的条件
	    		if(defult != null && !defult.equals("") && bean.AttDId != null) {//只能通知  在拿货页面筛选的条件
	    			String[] array = defult.split(",");
	    			boolean isExist = false;
	    			for(String con : array) {
	    				if(con.equals(bean.AttDId)) {
	    					isExist = true;
	    					break;
	    				}
	    			}
	    			if(isExist) {
	    				takeGoodsIntent.putExtras(bundle);
	    				contentIntent = PendingIntent.getActivity(this, num, takeGoodsIntent, 0);
	    	        	notification.setLatestEventInfo(this, takeGoodsTitle, msg, contentIntent);
	    			} else {
	    				return;
	    			}
	    		} else {
	    			return;
	    		}
        	}
        } else if(bean.NoticeType==2) {//2为待复位
        	resetSkuIntent.putExtras(bundle);
        	contentIntent = PendingIntent.getActivity(this, num, resetSkuIntent, 0);
        	notification.setLatestEventInfo(this, resetSkuTitle, msg, contentIntent);
        } else if(bean.NoticeType==3) {//3为盘点
        	stockTakingIntent.putExtras(bundle);
        	contentIntent = PendingIntent.getActivity(this, num, stockTakingIntent, 0);
        	notification.setLatestEventInfo(this, stockTakingTitle, msg, contentIntent);
        } else {
        	takeGoodsIntent.putExtras(bundle);
        	contentIntent = PendingIntent.getActivity(this, num, takeGoodsIntent, 0);
        	notification.setLatestEventInfo(this, takeGoodsTitle, msg, contentIntent);
        }  
        //用notificationManager的notify方法通知用户生成标题栏消息通知
        noticeManager.notify(num, notification);
        num++;
        if(num > 9) {
        	num = 0;
        }
	}
	
	private void requestHttp() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("UserID", MgrPerference.getInstance(this).getString(HotConstants.User.SHARE_USERID));
		map.put("UnitID", MgrPerference.getInstance(this).getString(HotConstants.Unit.UNIT_ID));
		map.put("RoleCode", MgrPerference.getInstance(this).getString(HotConstants.Unit.ROLE_CODE));
		Ex.Net(this).sendAsyncPost(url, map, requestCallback);
	}
	List<NoticeBean> noticeList = new ArrayList<NoticeBean>();
	ExRequestCallback requestCallback = new ExRequestCallback() {

		@Override
		public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {
			// 请求结果
			String result = "";
			// 判断结果流是否为空
			if (inStream != null) {
				// 转换流对象
				result = Ex.T().getInStream2Str(inStream);
				Log.e("result--", result);
				result = result.replaceAll("\\s+","");
				ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
				if (net.isSuccess) {
					String st = mGson.toJson(net.data);
					noticeList = mGson.fromJson(st,  
			                new TypeToken<List<NoticeBean>>() {}.getType()); 
					if(noticeList != null && noticeList.size() > 0) {
						for(NoticeBean bean : noticeList) {
							noticeMessages(bean);
						}
					}
					if(noticeList == null) 
						Log.i("noticeList", "noticeList为空");
					else
						Log.i("noticeList", noticeList.size()+"");
				}
			}
		}

		@Override
		public void onError(int statusCode, ExException e) {
			Log.i("noticeList", "noticeList error");
			Bundle data = new Bundle();
			data.putString("result", e.getMessage());
		}
	};

	@Override
	public void onDestroy() {
		timer.cancel();
		super.onDestroy();
	}
}
