package cn.jitmarketing.hot;

import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.telephony.TelephonyManager;
import cn.jitmarketing.hot.choupan.RandomCheckActivity;
import cn.jitmarketing.hot.choupan.RandomCheckTaskActivity;
import cn.jitmarketing.hot.entity.MenuConfigBean;
import cn.jitmarketing.hot.pandian.SingleStockActivity;
import cn.jitmarketing.hot.pandian.StockTakingShopperActivity;
import cn.jitmarketing.hot.ui.sample.SampleActivity;
import cn.jitmarketing.hot.ui.shelf.AllocationActivity;
import cn.jitmarketing.hot.ui.shelf.InStockListActivity;
import cn.jitmarketing.hot.ui.shelf.MoveShelfActivity;
import cn.jitmarketing.hot.ui.shelf.ResetSkuActivity;
import cn.jitmarketing.hot.ui.shelf.ShelfAndInStockActivity;
import cn.jitmarketing.hot.ui.shelf.SkuShelvesInfoActivity;
import cn.jitmarketing.hot.ui.shelf.StockTakingWarehouseActivity;
import cn.jitmarketing.hot.ui.shelf.StockTakingTaskActivity;
import cn.jitmarketing.hot.ui.shelf.TrimActivity;
import cn.jitmarketing.hot.ui.sku.AddStockActivity;
import cn.jitmarketing.hot.ui.sku.ApplyListActivity;
import cn.jitmarketing.hot.ui.sku.CheckStockActivity;
import cn.jitmarketing.hot.ui.sku.DirectNewActivity;
import cn.jitmarketing.hot.ui.sku.PosLogRecordActivity;
import cn.jitmarketing.hot.ui.sku.TakeGoodsActivity;
import cn.jitmarketing.hot.util.CrashHandler;

import com.ex.lib.core.ExApplication;
import com.ex.lib.core.utils.Ex;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class HotApplication extends ExApplication {

	private static HotApplication instance;

	private String unitId;
	private String areaId;
	private String cityId;
	private String customId;
	private String customerName;
	private String userId = "";
	private String userName;
	private String password;
	private String userCode;
	private String unitName;
	public static String sessionId = "";// sessionId：sessionId，一串字符串，由服务端登录时提供
	public static String version = HotConstants.Global.VERSION;// version:
																// 客户端版本号
	public static String plat = HotConstants.Global.PLATFORM;// plat：客户端平台分别：iphone、android
	public static String deviceToken = "abcdef";// deviceToken：客户端设备硬件串号
	public static String osInfo = HotConstants.Global.OSINFO;// osInfo：操作系统信息

	private SharedPreferences sharePre;
	private Map<String, MenuConfigBean> configMap = new HashMap<String, MenuConfigBean>();
	SoundPool soundPool;
	Map<Integer, Integer> soundMap;
	public int Sound_sku = 1;// sku码的声音
	public int Sound_location = 2;// 库位码的声音
	public int Sound_error = 3;// 错误声音

	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		sharePre = getSharedPreferences(HotConstants.User.SHARE_FILE, 0);
		initImageLoader();
		initMenuConfig();
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(1, soundPool.load(getApplicationContext(), R.raw.sku, 1));
		soundMap.put(2, soundPool.load(getApplicationContext(), R.raw.store, 1));
		soundMap.put(3, soundPool.load(getApplicationContext(), R.raw.error, 1));
		// 错误日志捕获
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}

	public void getsoundPool(int what) {
		soundPool.play(soundMap.get(what), 1, 1, 0, 0, 1);
	}

	/**
	 * 初始化主页中menu的信息，比如按钮背景颜色，icon的图标，对应的activity
	 */
	private void initMenuConfig() {
		// 查库存
		MenuConfigBean beanCKC = new MenuConfigBean();
		beanCKC.setBgDrawableID(R.drawable.main_search_click_button);
		beanCKC.setIconDrawableID(R.drawable.search);
		beanCKC.setActivityClass(CheckStockActivity.class);
		configMap.put("CKC", beanCKC);
		// 待复位
		MenuConfigBean beanDFW = new MenuConfigBean();
		beanDFW.setBgDrawableID(R.drawable.main_reset_click_button);
		beanDFW.setIconDrawableID(R.drawable.reset);
		beanDFW.setActivityClass(ResetSkuActivity.class);
		configMap.put("DFW", beanDFW);
		// 移库
		MenuConfigBean beanYK = new MenuConfigBean();
		beanYK.setBgDrawableID(R.drawable.main_change_click_button);
		beanYK.setIconDrawableID(R.drawable.change);
		beanYK.setActivityClass(MoveShelfActivity.class);
		configMap.put("YK", beanYK);
		// 盘点
		MenuConfigBean beanPD = new MenuConfigBean();
		beanPD.setBgDrawableID(R.drawable.main_pandian_click_button);
		beanPD.setIconDrawableID(R.drawable.pandian);
		beanPD.setActivityClass(StockTakingWarehouseActivity.class);
		configMap.put("PD", beanPD);
		// 盘点任务 店长独有的菜单
		MenuConfigBean beanPDRW = new MenuConfigBean();
		beanPDRW.setBgDrawableID(R.drawable.main_pandian_click_button);
		beanPDRW.setIconDrawableID(R.drawable.cangkuchuwei);
		beanPDRW.setActivityClass(StockTakingShopperActivity.class);
		configMap.put("PDRW", beanPDRW);
		// 入库（暂未使用）
		MenuConfigBean beanRK = new MenuConfigBean();
		beanRK.setBgDrawableID(R.drawable.main_instore_click_button);
		beanRK.setIconDrawableID(R.drawable.instore);
		beanRK.setActivityClass(InStockListActivity.class);
		configMap.put("RK", beanRK);
		// 上架（暂未使用）
		MenuConfigBean beanSJ = new MenuConfigBean();
		beanSJ.setBgDrawableID(R.drawable.main_shangjia_click_button);
		beanSJ.setIconDrawableID(R.drawable.shangjia);
		beanSJ.setActivityClass(SkuShelvesInfoActivity.class);
		configMap.put("SJ", beanSJ);
		// 仓库出货
		MenuConfigBean beanNH = new MenuConfigBean();
		beanNH.setBgDrawableID(R.drawable.main_outstore_click_button);
		beanNH.setIconDrawableID(R.drawable.outstore);
		beanNH.setActivityClass(TakeGoodsActivity.class);
		configMap.put("NH", beanNH);
		// 申请列表
		MenuConfigBean beanSQLB = new MenuConfigBean();
		beanSQLB.setBgDrawableID(R.drawable.main_sqlb_click_button);
		beanSQLB.setIconDrawableID(R.drawable.shenqingliebiao);
		beanSQLB.setActivityClass(ApplyListActivity.class);
		configMap.put("SQLB", beanSQLB);
		// 入库上架
		MenuConfigBean rcsjNH = new MenuConfigBean();
		rcsjNH.setBgDrawableID(R.drawable.main_outstore_click_button);
		rcsjNH.setIconDrawableID(R.drawable.shangjia);
		rcsjNH.setActivityClass(ShelfAndInStockActivity.class);
		configMap.put("RKSJ", rcsjNH);
		// 添加库位
		MenuConfigBean addStock = new MenuConfigBean();
		addStock.setBgDrawableID(R.drawable.main_outstore_click_button);
		addStock.setIconDrawableID(R.drawable.kuwei);
		addStock.setActivityClass(AddStockActivity.class);
		configMap.put("KWGL", addStock);
		// 调整单
		MenuConfigBean trim = new MenuConfigBean();
		trim.setBgDrawableID(R.drawable.main_outstore_click_button);
		trim.setIconDrawableID(R.drawable.tiaozheng);
		trim.setActivityClass(TrimActivity.class);
		configMap.put("TZD", trim);
		// 调拨单
		MenuConfigBean allocation = new MenuConfigBean();
		allocation.setBgDrawableID(R.drawable.main_outstore_click_button);
		allocation.setIconDrawableID(R.drawable.tiaobo);
		allocation.setActivityClass(AllocationActivity.class);
		configMap.put("DBD", allocation);
		// 样品管理
		MenuConfigBean sample = new MenuConfigBean();
		sample.setBgDrawableID(R.drawable.main_outstore_click_button);
		sample.setIconDrawableID(R.drawable.sample_icon);
		sample.setActivityClass(SampleActivity.class);
		configMap.put("YPGL", sample);
		// 直接取新
		MenuConfigBean getNew = new MenuConfigBean();
		getNew.setBgDrawableID(R.drawable.main_outstore_click_button);
		getNew.setIconDrawableID(R.drawable.zhijieqx);
		getNew.setActivityClass(DirectNewActivity.class);
		configMap.put("ZJQX", getNew);
		// 待处理
		MenuConfigBean record = new MenuConfigBean();
		record.setBgDrawableID(R.drawable.main_outstore_click_button);
		record.setIconDrawableID(R.drawable.tiaobo);
		record.setActivityClass(PosLogRecordActivity.class);
		configMap.put("JYJL", record);
//		// 抽盘
//		MenuConfigBean randomCheck = new MenuConfigBean();
//		randomCheck.setBgDrawableID(R.drawable.main_outstore_click_button);
//		randomCheck.setIconDrawableID(R.drawable.tiaobo);
//		randomCheck.setActivityClass(RandomCheckActivity.class);
//		configMap.put("CP", randomCheck);
//		// 抽盘任务
//		MenuConfigBean randomCheckTask = new MenuConfigBean();
//		randomCheckTask.setBgDrawableID(R.drawable.main_outstore_click_button);
//		randomCheckTask.setIconDrawableID(R.drawable.tiaobo);
//		randomCheckTask.setActivityClass(RandomCheckTaskActivity.class);
//		configMap.put("CPRW", randomCheckTask);
		// 单库位
		MenuConfigBean singleStock = new MenuConfigBean();
		singleStock.setBgDrawableID(R.drawable.main_outstore_click_button);
		singleStock.setIconDrawableID(R.drawable.tiaobo);
		singleStock.setActivityClass(SingleStockActivity.class);
		configMap.put("DKWPD", singleStock);
		// 拿货筛选条件
		SharedPreferences shared = getSharedPreferences("selector", Context.MODE_PRIVATE);
		Editor editor = shared.edit();
		String statis = "1,2,3,4,5,6,7";
		editor.putString("state", statis);
		editor.commit();
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setCusCompanyName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		if (Ex.String().isEmpty(customerName)) {
			customerName = sharePre.getString(HotConstants.User.SHARE_CUSTOMERID, "");
		}
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserId() {
		if (Ex.String().isEmpty(userId)) {
			userId = sharePre.getString(HotConstants.User.SHARE_USERID, "0");
		}
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		if (Ex.String().isEmpty(userName)) {
			userName = sharePre.getString(HotConstants.User.SHARE_USERNAME, "");
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public Map<String, MenuConfigBean> getConfigMap() {
		return configMap;
	}

	public void setConfigMap(Map<String, MenuConfigBean> configMap) {
		this.configMap = configMap;
	}

	public HotApplication() {
		super();
	}

	public static HotApplication getInstance() {
		if (null == instance) {
			instance = new HotApplication();
			restartApp();
		}
		return instance;
	}

	// 重启应用程序
	public static void restartApp() {
		Intent intent = new Intent(instance, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		instance.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}

	/**
	 * ImageLoader的配置初始化
	 */
	private void initImageLoader() {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).memoryCacheExtraOptions(480, 800).threadPoolSize(5).threadPriority(Thread.NORM_PRIORITY - 1).denyCacheImageMultipleSizesInMemory().memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)).discCacheFileNameGenerator(new Md5FileNameGenerator()).defaultDisplayImageOptions(DisplayImageOptions.createSimple()).build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 获取手机的唯一表示IMEI
	 * @return
	 */
	public String getPhoneIMEI() {
		TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		return TelephonyMgr.getDeviceId();
	}

}
