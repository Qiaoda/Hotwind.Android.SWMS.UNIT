package cn.jitmarketing.hot;

public class HotConstants {

	public static int SELECTED = 0;

	public static class Global {

		public static String APP_URL_USER = "192.168.1.10:80/";
		// public static String APP_URL_USER = "10.1.6.235:80/";
		// public static final String APP_URL_USER =
		// "http://115.159.38.117:10080/";
		// public static final String DEFAULT_IP = "115.159.38.117:10080";
		public static final String DEFAULT_IP = "192.168.1.10";
		// public static final String DEFAULT_IP = "10.1.6.235:80";
		// public static final String APP_URL_USER =
		// "http://192.168.0.56:9501/";

		// public static final String APP_URL_USER =
		// "http://192.168.0.68:6677/";
		public static final String VERSION = "1.1.9"; // 正式版
		public static final String PLATFORM = "android";
		public static final String OSINFO = android.os.Build.VERSION.RELEASE;

		// 版本信息
		public static int localVersion = 0;
		public static String localVersionName = "1.0.0";
		public static String serverVersionName = "1.0.0";
		public static String downloadDir = "app/download/";// 本地保存app路径
		public static final String updateUrl = "AppInfo/GetNewApp";// 更新请求的url
		public static String servreDownloadUrl = null;// 服务器下载地址

		// 是否启用小商品版本
		public static boolean isStartXSPVersion = false;
		  /*SKU码*/
	    public static final int SKU_CODE = 1;
	    /*STOCK码*/
	    public static final int STOCK_CODE = 2;
	}

	public static class Stock {
		public static final String DELETE_SHELF = "ShelfLocation/DeleteShelfLocations";
		public static final String SAVE_SHELF = "ShelfLocation/SaveShelfLocations";
		public static final String GET_SHELF = "ShelfLocation/GetShelfLocations";
		public static final String MODIFY_SHELF = "NewCheck/EditNewCheckResults";// 修改盘点

	}

	public static class User {
		public static final String SHARE_FILE = "user_info";
		public static final String SHARE_USERID = "userid";
		public static final String SHARE_CUSTOMERID = "customerid";
		public static final String SHARE_USERNAME = "username";
		public static final String SHARE_PASSWORD = "password";
		public static final String SHARE_UNITID = "unitid";
		public static final String USER_CODE = "dispalyName";
		public static final String UNIT_NAME = "unitName";
		public static final String ROLE_CODE = "role_code";

		public static final String LOGIN = "user/Login";
	}

	public static class HotMain {
		public static final String HOT_MAIN = "user/StoreInfo";
	}

	public static class Unit {
		public static final String UNIT_ID = "unit_id";
		public static final String ROLE_CODE = "roleCode";
		public static final String IS_SINGLE = "isSingle";//是否为单库位
	}

	public static class SKU {
		/**
		 * 根据扫码结果显示商品列表
		 */
		public static final String GetStoreForSKU = "SKU/GetStoreForSKU";
		/**
		 * 模糊查询商品
		 */
		public static final String GetStoreForSKUFuzzy = "SKU/GetStoreForSKUs";
		/**
		 * 通知仓管员取新
		 */
		public static final String GetNew = "SKU/GetNew";
		/**
		 * 通知仓管员取另外一只
		 */
		public static final String GetOther = "SKU/NewGetOther";
		/**
		 * 点击出样按钮显示出样类型类别-点击确定
		 */
		public static final String GetSample = "SKU/GetSample";
		public static final String GetBatchSample = "SKU/GetBatchSample";// 批量出样
		public static final String BatchRevokeSample = "SKU/BatchRevokeSample";// 批量撤样
		public static final String BatchSample = "Deliver/BatchSample";// 批量拿货
		public static final String ResetBatchSample = "ResetSKU/BatchRevokeSample";// 批量复位

		public static final String CHANGE_SAMPLE = "SKU/NewChangeSample";
		public static final String REVOKE_SAMPLE = "SKU/NewRevokeSample";
		public static final String OneSample = "1";// 出一只
		public static final String TwoSample = "2";// 出一双
		public static final String ChangeSample = "4";// 换样
		// public static final String OtherSample = "8";//出另外一只:暂时不用
		// 1024 取新 2048 取另外一只 4096 出样
		public static final String GETNEW = "1024";// 取新
		public static final String GETOTHER = "2048";// 取另外一只
		public static final String GETSAMPLE = "4096";// 出样
		public static final String OPT_ID_HY = "4098";// 换样optId

		/**
		 * 出样另一只列表
		 **/
		public static final String OUT_SAMPLE_OTHER_ONE_LIST = "SKU/QueryTheOtherOneList";
		/**
		 * 出样另一只
		 **/
		public static final String OUT_SAMPLE_OTHER_ONE = "SKU/GetSampleTheOtherOne";
		/**
		 * 撤样列表
		 **/
		public static final String QUERY_WITHDRAW_LIST = "SKU/ShoesSampleWithdraw";
		/**
		 * 已出一双要撤一只
		 */
		public static final String REVOKE_ONE_SAMPLE = "SKU/RevokeSampleOne";
		/**
		 * 换样列表
		 **/
		public static final String GetChangeSample = "SKU/ChangeList";
		/**
		 * 批量换样
		 **/
		public static final String BatchChangeSample = "SKU/BatchChangeSample";
		/**
		 * 批量换样之出样（仓库出货）
		 **/
		public static final String DeliverChangeSample = "Deliver/BatchChangeSample";
		/***
		 * 出样提醒列表
		 ***/
		public static final String GetSampleRemind = "SKU/SampleRemind";
		/**
		 * 直接出样（非鞋类）
		 **/
		public static final String DirectOutSample = "Deliver/DirectSample";
		/**
		 * 直接撤样（非鞋类）
		 **/
		public static final String DirectRevokeSample = "ResetSKU/DirectRevokeSample";
		/**
		 * 直接撤样（鞋类），只能单条撤
		 **/
		public static final String ShoesDirectSample = "ResetSKU/ShoesDirectSample";

	}

	public static class Ser {
		public static final String NOTICE = "PushNotification/GetPushList";
		public static final String NEWNOTICE = "PushNotification/NewGetNotice";
	}

	public static class Shelf {
		/**
		 * 暂存
		 */
		public static final String ALLOCATION_TEMP_SAVE = "AllocationOrder/TempSaveAllocationOrders";
		/**
		 * 商品移架
		 */
		public static final String MoveShelfLocation = "MoveShelfLocation/MoveShelfLocation";
		/**
		 * 商品上架
		 */
		public static final String SKUShelves = "Shelves/SKUShelves";
		/**
		 * 商品复位
		 */
		public static final String ResetSkuList = "ResetSKU/ResetSKUList";
		/**
		 * 商品复位成功
		 */
		public static final String ResetSkuOk = "ResetSKU/ResetSKU";
		/**
		 * 商品入库
		 */
		public static final String SkuStockIn = "StorageIn/stockIn";
		/**
		 * 初步盘点任务确认
		 */
		public static final String suring = "Check/CheckTaskConfirm";
		public static final String stockTaskFinish = "NewCheck/CloseCheckTask";
		/**
		 * 获取盘点差异库位
		 */
		public static final String different = "Check/DifferenceList";
		/**
		 * 获取盘点通知列表
		 */
		public static final String getStockList = "NewCheck/GetCheckNoticeList";
		/**
		 * 创建盘点成功
		 */
		public static final String NewOk = "NewCheck/StartCheckTask";
		/**
		 * 盘点任务店长
		 */
		public static final String stockTask = "NewCheck/CurrentCheckList";
		/**
		 * 盘点任务列表
		 */
		public static final String stockTaskList = "NewCheck/GetCheckTaskList";
		/**
		 * 库位详情
		 */
		public static final String LoadShelflocation = "NewCheck/LoadShelflocationCheckTaskResultDetailed";
		/**
		 * 库位历史详情
		 */
		public static final String LoadShelfHistorylocation = "NewCheck/GetHistoryNewCheckShelfLocationCodeDetaileList";
		/**
		 * 库位详情查询
		 */
		public static final String QueryShelflocation = "NewCheck/QueryNewcheckShelflocationSKU";
		/**
		 * 盘点任务详情
		 */
		public static final String stockTaskDetailList = "NewCheck/LoadHistoryCheckTaskResult";
		/**
		 * 盘点库位差异列表
		 */
		public static final String PlanStockDiffList = "NewCheck/GetSampleSLDifferenceSKUList";
		/**
		 * 盘点库位确认
		 */
		public static final String ConfirmShelf = "NewCheck/ConfirmShelflocationNewcheck";
		/**
		 * 盘点库位列表
		 */
		public static final String PlanStock = "NewCheck/GetNeedCheckSelfLocation";
		/**
		 * 店长盘点库位列表
		 */
		public static final String shopownerPlanStock = "NewCheck/GetSelfLocationList";
		/**
		 * 店长开启样品库盘点
		 */
		public static final String openSampleShelflocation = "NewCheck/OpenSampleShelflocation";
		/**
		 * 需要盘点的库位列表
		 */
		public static final String getCheckStockList = "Check/ShelfLocationListForCheck";
		/**
		 * 库存盘点
		 */
		public static final String StockCheck = "Check/ShelfLocationCheckConfirm";
		/**
		 * 普通库存盘点
		 */
		public static final String NomalStockCheck = "NewCheck/CheckEntitySelfLocation";
		/**
		 * 样品库存盘点
		 */
		public static final String SampleStockCheck = "NewCheck/CheckSampleSelfLocation";
		/**
		 * 小商品库盘点
		 */
		public static final String SamplesSmallStockCheck = "NewCheck/CheckSmallGoodsShelfLocation";
		/**
		 * 确认样品库存盘点
		 */
		public static final String ConfirmSampleStockCheck = "NewCheck/ConfirmSampleShelfLocation";
		/**
		 * 确认小商品库存盘点
		 */
		public static final String ConfirmSmallSampleStockCheck = "NewCheck/ConfirmSmallGoodsShelfLocation";
		/**
		 * 仓管员获取待出货列表
		 */
		public static final String OutStockList = "Deliver/GetOutStockList";
		/**
		 * 仓管员出货
		 */
		public static final String OutStock = "Deliver/DeliverStock";
		/**
		 * 销售员申请列表
		 */
		public static final String ApplyList = "Request/ApplyListPaging";
		/**
		 * 申请列表取消
		 */
		public static final String APPLY_CANCEL = "Request/CacelRequest";
		/**
		 * 入库单列表
		 */
		public static final String SkuStockInList = "StorageIn/ReceiveList";
		/**
		 * 修改密码
		 */
		public static final String amendpass = "user/changepassword";
		/**
		 * 盘点历史
		 */
		public static final String sHistory = "Check/DifferenceSKU";
		/**
		 * 老入库记录
		 */
		public static final String beforeInfoList = "StorageIn/ReceiveListDetail";
		/**
		 * 调整单历史
		 */
		public static final String TrimHistory = "ChangeStock/GetChangeStocks";
		/**
		 * 调整单保存
		 */
		public static final String TrimSave = "ChangeStock/SaveChangeStocks";
		/**
		 * 暂存
		 */
		public static final String temporary = "StorageInAndShelves/TemporaryMemory";
		/**
		 * 入库单明细list
		 */
		public static final String InfoList = "StorageInAndShelves/ReceiveListDetails";
		/**
		 * 新入库上架提交
		 */
		public static final String newIn = "StorageInAndShelves/SKUStockShelves";
		/**
		 * 新入库单列表
		 */
		public static final String newStockInList = "StorageInAndShelves/ReceiveLists";
		/**
		 * 入库单搜索接口
		 */
		public static final String stockInSearch = "StorageInAndShelves/ReceiveHistoryLists";
		/**
		 * 推荐库位列表
		 */
		public static final String recommendStockList = "StorageInAndShelves/RecommendShelflocationList";
		/**
		 * 新入库记录
		 */
		public static final String newInfoList = "StorageInAndShelves/ReceiveListDetails";
		/*入库明细*/
		public static final String stockInDetails = "StorageInAndShelves/ReceiveHistoryListDetails";
		public static final String ALLOCATION_SAVE = "AllocationOrder/SaveAllocationOrders";
		public static final String ALLOCATION_SELETE_LIST = "Unit/GetUnitInfo";
		public static final String ALLOCATION_ORDER = "AllocationOrder/GetAllocationOrders";
		/**
		 * 挑拨记录
		 */
		public static final String AlloHistory = "AllocationOrder/LoadDetail";
		public static final String ALLOCATION_TEMP_LIST = "AllocationOrder/LoadTempDetail";
		public static final String ALLOCATION_TEMP_DELETE = "AllocationOrder/DeleteTempAllocationOrders";// 删除暂存
		public static final String GetPosLogRecord = "Pos/GetPosLogRecord";// 查询交易记录
		public static final String SearchPosLogRecord = "Pos/QueryPosLogRecord";// 搜索交易记录

	}
	/**
	 * 抽盘
	 */
	public static class RandomCheck{
		/*获取抽盘单号*/
		public static final String RandomCheckCode="InventoryCheck/GetInventoryCheckTaskList";
		/*创建抽盘*/
		public static final String CreateRandomCheck="InventoryCheck/UpdInventoryCheckTaskStart";
		/*获取抽盘明细*/
		public static final String GetRandomCheckDetail="InventoryCheck/GetInventoryCheckTaskStartList";
	}
	/**单库位*/
	public static class SingleStock{
		/*盘点文件上传*/
		public static final String SingleStockCheck="Inventory/InventoryResultsUpload";
	}
}
