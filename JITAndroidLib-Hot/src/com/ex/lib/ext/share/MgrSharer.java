package com.ex.lib.ext.share;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.ex.lib.R;
import com.ex.lib.ext.share.entry.ExKeyShare;

/**
 * @ClassName: MgrSharer
 * @Description: ShareSDK 分享管理对象
 * @author Aloneter
 * @date 2014-10-31 上午11:40:09
 * @Version 1.0
 * 
 */
public class MgrSharer {

	private String mTitile = "分享中..."; // 分享标题

	private static Activity mActivity; // 上下文
	private static Context mContext;

	/**
	 * 创建者
	 */
	private static class SharerHolder {

		private static MgrSharer mgr = new MgrSharer();

	}

	/**
	 * 获取当前对象实例
	 */
	public static MgrSharer getInstance(Activity activity) {

		if (mActivity == null) {
			mActivity = activity;
			mContext = activity.getApplicationContext();
		}

		return SharerHolder.mgr;
	}

	/**
	 * Method_初始化
	 */
	public void init() {

		ShareSDK.initSDK(mContext);
		ShareSDK.setConnTimeout(5000);
		ShareSDK.setReadTimeout(10000);
	}

	/**
	 * Method_设置标题
	 * 
	 * @param title_标题
	 */
	public void setTitle(String title) {

		mTitile = title;
	}

	/**
	 * Method_短息分享
	 * 
	 * @param content_分享内容
	 */
	public void shareSMS(String content) {

		Uri smsToUri = Uri.parse("smsto:");

		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", content);
		mActivity.startActivity(intent);
	}

	/**
	 * Method_分享打开
	 */
	public void shareSend() {

	}

	/**
	 * Method_分享文字
	 * 
	 * @param content_内容
	 */
	public void shareSend(String content) {

		openShareByContentOrBinary(content, null);
	}

	/**
	 * Method_分享文件
	 * 
	 * @param binary_文件
	 */
	public void shareSend(File binary) {

		openShareByContentOrBinary(null, binary);
	}

	/**
	 * Method_分享文件和内容
	 * 
	 * @param content_内容
	 * @param binary_文件
	 */
	public void shareSend(String content, File binary) {

		openShareByContentOrBinary(content, binary);
	}

	/**
	 * Method_分享 URI
	 * 
	 * @param uris_URI
	 *            集合
	 */
	public void shareSend(ArrayList<Uri> uris) {

		openShareByList(uris);
	}

	/**
	 * Method_分享指定平台对象
	 * 
	 * @param silent_是否使用客户端
	 * @param platform_分享平台
	 * @param share_分享对象
	 */
	public void shareSend(boolean silent, String platform, ExKeyShare share) {

		shareOther(silent, platform, share);
	}

	/**
	 * Method_打开分享内容和文件
	 * 
	 * @param content_内容
	 * @param binary_文件
	 */
	private void openShareByContentOrBinary(String content, File binary) {

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");

		if (binary != null && binary.exists() && binary.isFile()) {
			intent.setType("image/*");

			Uri u = Uri.fromFile(binary);
			intent.putExtra(Intent.EXTRA_STREAM, u);
		}

		intent.putExtra(Intent.EXTRA_TEXT, content);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		mActivity.startActivity(Intent.createChooser(intent, mTitile));
	}

	/**
	 * Method_打开分享通过集合
	 * 
	 * @param uris_URI
	 *            集合
	 */
	private void openShareByList(ArrayList<Uri> uris) {

		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.setType("image/*");
		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		mActivity.startActivity(Intent.createChooser(intent, mTitile));
	}

	/**
	 * Method_分享其他
	 * 
	 * @param silent_是否使用客户端
	 * @param platform_分享平台参数
	 * @param share_分享对象
	 */
	private void shareOther(boolean silent, String platform, ExKeyShare share) {

		final OnekeyShare oks = new OnekeyShare();

		oks.setNotification(R.drawable.ex_sdk_share_notification_icon, share.getNotificationAppName());

		// oks.setAddress("12345678901");

		oks.setTitle(share.getTitle());
		oks.setTitleUrl(share.getTitleUrl());
		oks.setImageUrl(share.getImageUrl());
		oks.setText(share.getText());
		oks.setSite(share.getSite());
		oks.setSiteUrl(share.getSiteUrl());
		oks.setUrl(share.getUrl());
		oks.setSilent(silent);

		// oks.setAddress(share.getAddress()); // 12345678901
		// oks.setTitle(share.getTitle()); // 内容分享
		// oks.setTitleUrl(share.getTitleUrl()); // http://sharesdk.cn
		// oks.setText(share.getText()); // 这是一个很好玩的东西
		// oks.setImageUrl(share.getImageUrl()); //
		// http://res.3636.com/cpsapks/png/testkpk_1.6.6.007.png
		// oks.setUrl(share.getUrl()); // http://www.sharesdk.cn
		// oks.setImagePath(share.getImagePath());
		// oks.setFilePath(share.getFilePath());
		// oks.setComment(share.getComment()); //
		// oks.setSite(share.getSite()); //
		// oks.setSiteUrl(share.getSiteUrl()); // http://sharesdk.cn
		// oks.setVenueName(share.getVenueName()); // ShareSDK
		// oks.setVenueDescription(share.getVenueDescription()); // This is a
		// // beautiful
		// // place!
		// oks.setLatitude(share.getLatitude()); // 23.056081f
		// oks.setLongitude(share.getLongitude()); // 113.385708f

		// 是否有自定义编辑页初始化分享平台
		if (platform != null) {
			oks.setPlatform(platform);
		}

		oks.setDialogMode();
		oks.disableSSOWhenAuthorize();

		// 开始分享
		oks.show(mActivity);
	}

	/**
	 * Method_分享测试
	 * 
	 * @param silent_分享是否使用客户端
	 * @param platform_分享指定平台
	 */
	@SuppressWarnings("unused")
	private void demoOther(boolean silent, String platform) {

		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.ex_sdk_share_notification_icon, "demo");
		oks.setAddress("12345678901");
		oks.setTitle("ceshi");
		oks.setTitleUrl("http://sharesdk.cn");
		oks.setText("ceshi");
		// oks.setImageUrl("http://res.3636.com/cpsapks/png/testkpk_1.6.6.007.png");
		oks.setUrl("http://www.sharesdk.cn");
		oks.setSilent(silent);

		if (platform != null) {
			oks.setPlatform(platform);
		}

		oks.setDialogMode();

		oks.disableSSOWhenAuthorize();

		oks.show(mActivity);
	}

}
