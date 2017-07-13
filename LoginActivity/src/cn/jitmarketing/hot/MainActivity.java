package cn.jitmarketing.hot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.OnFinished;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseOperationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import cn.jitmarketing.hot.HotConstants.Global;
import cn.jitmarketing.hot.adapter.MenuGridAdapter;
import cn.jitmarketing.hot.entity.MenuBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.UpDataBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.service.NoticeService;
import cn.jitmarketing.hot.service.UpdateService;
import cn.jitmarketing.hot.ui.sku.CheckStockActivity;
import cn.jitmarketing.hot.ui.sku.TakeGoodsActivity;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.BaseDialog;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class MainActivity extends BaseOperationActivity implements OnClickListener, OnItemClickListener {

	private static final String LOCKHOME = "urovo.rcv.message";
	/**
	 * 九宫格
	 */
	@ViewInject(R.id.grid_menu)
	GridView grid_menu;
	/**
	 * 设置按钮
	 */
	@ViewInject(R.id.txt_setting)
	TextView settingButton;
	/**
	 * 店面地址
	 */
	@ViewInject(R.id.home_main_addr)
	TextView home_main_addr;
	/**
	 * 用户名字
	 */
	@ViewInject(R.id.home_main_user)
	TextView home_main_user;
	/**
	 * 已查询
	 */
	@ViewInject(R.id.have_query_txt)
	TextView have_query_txt;
	/**
	 * 已出货
	 */
	@ViewInject(R.id.wait_chuhuo_text)
	TextView wait_chuhuo_text;
	/**
	 * 待复位
	 */ 
	@ViewInject(R.id.wait_fuwei_txt)
	TextView wait_fuwei_txt;

	private static final int WHAT_NET_UPDATE = 0x11;

	private MenuGridAdapter gridAdapter;
	private List<MenuBean> menuList;
	/* 角色 */
	private String roleCode;
	/* 当前扫描状态 */
	public static boolean canScran = false;
	HotApplication ap;
	NotificationManager nm;
	private Timer timer;
	private MyTask task = new MyTask();
	/* 当前网络请求状态 */
	private boolean req = true;// 能否发送请求
	private boolean isReqing = false;// 是否在请求中

	private Intent noticeIntent;

	/* 版本更新对话框 */
	private BaseDialog upDataDialog;
	private ProgressBar progressBar;
	private TextView progress;
	private TextView updata_content;

	@Override
	protected int exInitLayout() {
		EventBus.getDefault().register(this);
		return R.layout.activity_main;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void exInitView() {
		// 定时器每5秒钟刷新一次请求
		timer = new Timer();
		timer.schedule(task, 0, 1 * 5 * 1000);
		// 消息提示
		nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		ap = (HotApplication) getApplication();
		home_main_user = (TextView) findViewById(R.id.home_main_user);
		/* 菜单列表项 */
		menuList = (List<MenuBean>) getIntent().getSerializableExtra("menuList");
		// 测试
//		MenuBean cp=new MenuBean("CP", "抽盘");
//		menuList.add(cp);//		
//		MenuBean cprw=new MenuBean("CPRW","抽盘任务");
//		menuList.add(cprw);
//		MenuBean dkw=new MenuBean("DKW", "单库位");
//		menuList.add(dkw);
		roleCode = getIntent().getStringExtra("roleCode");
		grid_menu.setOnItemClickListener(this);
		settingButton.setOnClickListener(this);
		home_main_user.setText(MgrPerference.getInstance(this).getString(HotConstants.User.USER_CODE));
		home_main_addr.setText(MgrPerference.getInstance(this).getString(HotConstants.User.UNIT_NAME));
		// startService(new Intent(this, BootService.class));
		gridAdapter = new MenuGridAdapter(this, menuList);
		grid_menu.setAdapter(gridAdapter);
		/* 创建更新dialog */
		createUpDataDialog();
		/* 初始化APP的版本号和版本名称 */
		initGlobal();
		/* 检查更新请求 */
		startTask(HotConstants.Global.APP_URL_USER + Global.updateUrl, WHAT_NET_UPDATE, NET_METHOD_POST, false);
		/* 启动通知服务 */
		noticeIntent = new Intent(this, NoticeService.class);
		startService(noticeIntent);

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
			/* \\.转译为. 按“.”切割 */
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
		/* 如果有新版本则提示更新 */
		if (isUpdate) {
			// String msg = "发现新版本,建议立即更新使用\n 1.修改了盘点功能";
			// //发现新版本，提示用户更新
			// AlertDialog.Builder alert = new AlertDialog.Builder(this);
			// alert.setTitle("软件升级")
			// .setMessage(msg)
			// .setPositiveButton("立即更新", new DialogInterface.OnClickListener()
			// {
			// public void onClick(DialogInterface dialog, int which) {
			// //开启更新服务UpdateService
			// //这里为了把update更好模块化，可以传一些updateService依赖的值
			// //如布局ID，资源ID，动态获取的标题,这里以app_name为例
			// Intent updateIntent = new Intent(MainActivity.this,
			// UpdateService.class);
			// updateIntent.putExtra("titleId", R.string.app_name);
			// updateIntent.putExtra("downUrl", Global.servreDownloadUrl);
			// startService(updateIntent);
			// }
			// });
			// alert.create().show();
			// 强制更新
			upDataDialog.show();
			Intent updateIntent = new Intent(MainActivity.this, UpdateService.class);
			updateIntent.putExtra("titleId", R.string.app_name);
			updateIntent.putExtra("downUrl", Global.servreDownloadUrl);
			startService(updateIntent);
		} else {
			// 清理工作，略去
			// cheanUpdateFile();
		}
	}

	@Subscribe(threadMode = ThreadMode.MainThread)
	public void getUpData(UpDataBean upDataBean) {
		progressBar.setProgress(upDataBean.getProgressNum());
		progress.setText(upDataBean.getProgres());
		if (upDataBean.isFailed()) {
			upDataDialog.dismiss();
			Toast.makeText(MainActivity.this, "更新失败，请重新启动", Toast.LENGTH_SHORT).show();
		}
		if (upDataBean.isFinish()) {
			upDataDialog.dismiss();
		}
	}

	/**
	 * NEW_消息处理对象
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 获取结果值
			String result = msg.getData().getString("result");
			boolean isShow = msg.getData().getBoolean("isShow");
			// 获取操作码
			// int what = msg.what;
			// 获取请求结果码
			// int arg2 = msg.arg2;
			// 判断对象是否销毁
			if (mActivity == null || mContext == null) {
				return;
			}
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			} else {
				try {
					JSONObject json = new JSONObject(mGson.toJson(net.data));
					int seachCount = json.getInt("seachCount");
					int resetCount = json.getInt("resetCount");
					int outingCount = json.getInt("outingCount");
					int inStockOrderCount = json.getInt("inStockOrderCount");// 入库数量
					int checkNoticeCount = json.getInt("checkNoticeCount");// 盘点数量
					int posCount = 0;
					try {
						posCount = json.getInt("posCount");// 待处理数量
					} catch (Exception e) {
						e.printStackTrace();
					}
					have_query_txt.setText(String.valueOf(posCount));
					wait_chuhuo_text.setText(String.valueOf(outingCount));
					wait_fuwei_txt.setText(String.valueOf(resetCount));
					if (gridAdapter != null && menuList != null && menuList.size() > 0) {
						for (MenuBean mb : menuList) {
							if (mb.menuCode.equals("PDRW")) {
								mb.num = checkNoticeCount;
							} else if (mb.menuCode.equals("RKSJ")) {
								mb.num = inStockOrderCount;
							}
						}
						gridAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			// 判断是否显示对话框
			if (isShow) {
				Ex.Dialog(mContext).dismissProgressDialog();
			}
		}
	};

	/* 请求回调 */
	ExRequestCallback requestCallback = new ExRequestCallback() {

		@Override
		public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {
			isReqing = false;
			// 请求结果
			String result = "";
			// 判断结果流是否为空
			if (inStream != null) {
				// 转换流对象
				result = Ex.T().getInStream2Str(inStream);
				Log.e("result--", result);
			}
			// 创建消息对象
			Message msg = mHandler.obtainMessage();
			// 传入操作码
			// msg.what = what;
			// 请求结果码
			msg.arg2 = 0;
			// 请求结果
			msg.obj = inStream;
			// 请求结果参数
			Bundle data = new Bundle();
			data.putSerializable("cookies", cookies);
			data.putString("result", result);
			data.putBoolean("cache", false);
			// data.putBoolean("isShow", isShow);
			msg.setData(data);

			// 发送消息
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(int statusCode, ExException e) {
			isReqing = false;
			Bundle data = new Bundle();
			data.putString("result", e.getMessage());
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		canScran = true;
		req = true;
		// 操作日志
		LogUtils.logOnFile("进入->菜单界面");
	}

	@Override
	protected void onPause() {
		super.onPause();
		canScran = false;
		req = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		canScran = false;
		req = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(noticeIntent);
		nm.cancelAll();
		if (timer != null)
			timer.cancel();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected void exInitAfter() {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		HotApplication app = (HotApplication) this.getApplication();
		Class<?> cs = app.getConfigMap().get(menuList.get(position).menuCode).getActivityClass();
		// 操作日志
		LogUtils.logOnFile("进入->" + menuList.get(position).menuName);
		Ex.Activity(mActivity).start(cs, null);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_setting:
			Intent inen = new Intent();
			inen.setClass(MainActivity.this, SettingActivity.class);
			startActivity(inen);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this).setTitle(R.string.noteTitle).setMessage(getString(R.string.SureQuit)).setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					/* 关闭消息服务 */
					stopService(new Intent(MainActivity.this, NoticeService.class));
					// stopService(new Intent(MainActivity.this,
					// BootService.class));
					Ex.Activity(mActivity).finishAll();
				}
			}).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
		}
		return false;
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
		// TODO Auto-generated method stub
		super.onError(what, e, message);
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		/* 将返回结果转换成类对象 */
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);

		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}

		switch (what) {
		case WHAT_NET_UPDATE:// 更新请求
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

	@SuppressLint("DefaultLocale" )
	@Override
	public void onReceiver(Intent intent) {
		// byte[] barcode = intent.getByteArrayExtra("barocode");
		// int barocodelen = intent.getIntExtra("length", 0);
		// String skuCodeStr = new String(barcode, 0,
		// barocodelen).toUpperCase().trim();;
		// dealBarCode(skuCodeStr);
	}

	// @Override
	// public void fillCode(String code) {
	// Log.i("code", code);
	// dealBarCode(code);
	// }
	private void dealBarCode(String skuCodeStr) {
		// 判断能否扫描
		if (canScran&&!roleCode.equals("DKW")) {// 如果能扫描 判断是仓库码还是商品码  &&单库位不可用
			boolean isKW = SkuUtil.isWarehouse(skuCodeStr);
			Intent tintent;
			if (!roleCode.toUpperCase().equals("CGY")) {// 非仓管人员
				if (!isKW) {// 非库位码
					ap.getsoundPool(ap.Sound_sku);
					tintent = new Intent(MainActivity.this, CheckStockActivity.class);
				} else {// 库位码
					ap.getsoundPool(ap.Sound_location);
					return;
				}
			} else {// 仓管人员
				ap.getsoundPool(ap.Sound_location);
				tintent = new Intent(MainActivity.this, TakeGoodsActivity.class);
			}
			tintent.putExtra("skuCode", skuCodeStr);
			canScran = false;
			startActivity(tintent);
		}
	}

	@Override
	protected String[] exInitReceiver() {

		return new String[] { LOCKHOME };
	}

	/* 创建定时任务 */
	class MyTask extends java.util.TimerTask {

		@Override
		public void run() {
			if (req) {
				if (!isReqing) {
					isReqing = true;
					/* 发送异步请求 */
					Ex.Net(MainActivity.this).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.HotMain.HOT_MAIN, WarehouseNet.takeInfo(), requestCallback);
				}
			}
		}

	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
		// 操作日志
		LogUtils.logOnFile("扫码" + code);
	}

	/* 强制更新对话框 */
	private void createUpDataDialog() {
		String msg = "";
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_updata, null);
		upDataDialog = new BaseDialog(this, view);
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		progress = (TextView) view.findViewById(R.id.progress);
		updata_content = (TextView) view.findViewById(R.id.updata_content);
		updata_content.setText(msg);
		upDataDialog.setCancelable(false);
		upDataDialog.setCanceledOnTouchOutside(false);
	}

}
