package cn.jitmarketing.hot;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.jitmarketing.hot.HotConstants.Global;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.service.BootService;
import cn.jitmarketing.hot.service.NoticeService;
import cn.jitmarketing.hot.service.UpdateService;
import cn.jitmarketing.hot.ui.shelf.AmendPasswordActivity;
import cn.jitmarketing.hot.view.TitleWidget;

public class SettingActivity extends BaseSwipeBackAcvitiy {
	private static final int WHAT_NET_UPDATE = 0x11;
	/**
	 * 标题
	 */
	@ViewInject(R.id.setting_title)
	TitleWidget titleLayout;
	/**
	 * 修改密码
	 */
	@ViewInject(R.id.setting_modify_pwd_txt)
	TextView modifyPwdBtn;
	/**
	 * 注销按钮
	 */
	@ViewInject(R.id.setting_logout_btn)
	Button logoutBtn;
	@ViewInject(R.id.setting_version_txt)
	TextView setting_version_txt;
	/**
	 * 检查更新
	 */
	@ViewInject(R.id.setting_check_update_txt)
	TextView setting_check_update_txt;
	/**
	 * 通知设置
	 */
	@ViewInject(R.id.notice_txt)
	TextView notice_txt;

	@Override
	protected int exInitLayout() {
		return R.layout.activity_setting;
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected void exInitView() {
		titleLayout.setOnLeftClickListner(clickListener);
		logoutBtn.setOnClickListener(clickListener);
		modifyPwdBtn.setOnClickListener(clickListener);
		setting_check_update_txt.setOnClickListener(clickListener);
		notice_txt.setOnClickListener(clickListener);
		try {
			setting_version_txt.setText("当前版本号：" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} // 设置本地版本号
		initGlobal();

	}

	/**
	 * 初始化全局变量 实际工作中这个方法中serverVersion从服务器端获取，最好在启动画面的activity中执行
	 */
	public void initGlobal() {
		try {
			Global.localVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode; // 设置本地版本号
			Global.localVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName; // 设置本地版本名称
			// Global.serverVersion = 1;//假定服务器版本为2，本地版本默认是1
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 检查更新版本
	 */
	public void checkVersion() {
		boolean isUpdate = false;
		if (Global.localVersionName != null && Global.serverVersionName != null) {
			String[] localArray = Global.localVersionName.split("\\.");
			String[] serverArray = Global.serverVersionName.split("\\.");
			if (Integer.valueOf(serverArray[0]) > Integer.valueOf(localArray[0])) {
				isUpdate = true;
			} else {
				if (Integer.valueOf(serverArray[0]) < Integer.valueOf(localArray[0])) {
					return;
				}
				if (Integer.valueOf(serverArray[1]) > Integer.valueOf(localArray[1])) {
					isUpdate = true;
				} else {
					if (Integer.valueOf(serverArray[1]) < Integer.valueOf(localArray[1])) {
						return;
					}
					if (Integer.valueOf(serverArray[2]) > Integer.valueOf(localArray[2])) {
						isUpdate = true;
					} else {
						isUpdate = false;
					}
				}
			}
		}
		if (isUpdate) {
			String msg = "发现新版本,建议立即更新使用\n 1.修改了盘点功能";
			// 发现新版本，提示用户更新
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("软件升级").setMessage(msg).setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 开启更新服务UpdateService
					// 这里为了把update更好模块化，可以传一些updateService依赖的值
					// 如布局ID，资源ID，动态获取的标题,这里以app_name为例
					Intent updateIntent = new Intent(SettingActivity.this, UpdateService.class);
					updateIntent.putExtra("titleId", R.string.app_name);
					updateIntent.putExtra("downUrl", Global.servreDownloadUrl);
					startService(updateIntent);
				}
			});
			alert.create().show();
		} else {
			// 清理工作，略去
			// cheanUpdateFile();
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_UPDATE:
			return WarehouseNet.updateApp(Global.localVersionName);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		super.onError(what, e, message);
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_UPDATE:
			String str = mGson.toJson(net.data);
			try {
				JSONObject ob = new JSONObject(str);
				Global.servreDownloadUrl = ob.getString("Url");
				Global.serverVersionName = ob.getString("Version");
				checkVersion();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lv_left:
				SettingActivity.this.finish();
				break;
			case R.id.setting_modify_pwd_txt:
				Intent intent = new Intent();
				intent.setClass(SettingActivity.this, AmendPasswordActivity.class);
				startActivity(intent);
				break; 
			case R.id.setting_logout_btn:// 注销按钮
				new AlertDialog.Builder(SettingActivity.this).setTitle(R.string.noteTitle).setMessage(getString(R.string.LoginOut)).setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						stopService(new Intent(SettingActivity.this, BootService.class));
						MgrPerference.getInstance(SettingActivity.this).putString(HotConstants.User.SHARE_PASSWORD, "");
						Ex.Activity(mActivity).finishAll();
						stopService(new Intent(SettingActivity.this, NoticeService.class));
						Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
						startActivity(intent);
					}
				}).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
				break;
			case R.id.setting_check_update_txt:
				startTask(HotConstants.Global.APP_URL_USER + Global.updateUrl, WHAT_NET_UPDATE, NET_METHOD_POST, false);
				break;
			case R.id.notice_txt:
				Intent intent1 = new Intent();
				intent1.setClass(SettingActivity.this, NoticeSettingActivity.class);
				startActivity(intent1);
				break;
			default:
				break;
			}
		}
	};

}
