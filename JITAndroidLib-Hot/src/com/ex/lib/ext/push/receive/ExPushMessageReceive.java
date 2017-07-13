package com.ex.lib.ext.push.receive;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.ex.lib.ext.push.MgrPush;

public class ExPushMessageReceive extends FrontiaPushMessageReceiver {

	public static final String TAG = "Baidu Push";

	/**
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {

		String responseString = "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId + " channelId=" + channelId + " requestId=" + requestId;

		Log.d(TAG, responseString);

		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		if (errorCode == 0) {
			MgrPush.getInstance((Activity) context).setBind(true);
		}
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void onMessage(Context context, String message, String customContentString) {

		String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;

		Log.d(TAG, messageString);

		// 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
		if (customContentString != null && !TextUtils.isEmpty(customContentString)) {

			try {
				JSONObject customJson = new JSONObject(customContentString);
				Iterator it = customJson.keys();

				while (it.hasNext()) {
					String key = it.next().toString();
					String value = customJson.getString(key);

					Log.d(TAG, "透传消息 " + key + "：" + value);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {

		String notifyString = "通知点击 title=\"" + title + "\" description=\"" + description + "\" customContent=" + customContentString;

		Log.d(TAG, notifyString);

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (customContentString != null && !TextUtils.isEmpty(customContentString)) {

			try {
				JSONObject customJson = new JSONObject(customContentString);
				Iterator it = customJson.keys();

				while (it.hasNext()) {
					String key = it.next().toString();
					String value = customJson.getString(key);

					Log.d(TAG, "透传消息 " + key + "：" + value);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {

		String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;

		Log.d(TAG, responseString);
	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {

		String responseString = "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;

		Log.d(TAG, responseString);
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {

		String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;

		Log.d(TAG, responseString);
	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {

		String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;

		Log.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			MgrPush.getInstance((Activity) context).setBind(false);
		}
	}
}
