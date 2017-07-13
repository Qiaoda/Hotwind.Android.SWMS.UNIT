package cn.jitmarketing.hot.ui.shelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.SkuShelvesOneListAdapter;
import cn.jitmarketing.hot.entity.CheckBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ReceiveBean;
import cn.jitmarketing.hot.entity.ReceiveRecommendBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.BaseDialog;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 入库上架扫描界面
 */
public class MainShelfAndActivity extends BaseSwipeOperationActivity implements OnClickListener {

	/**
	 * 获取推荐库位
	 */
	private static final int WHAT_NET_GET_RECOMMEND_STOCK_LIST = 0;
	/**
	 * 确定上架
	 */
	private static final int WHAT_NET_SKU_SHELF = 0x11;
	/**
	 * 明细（获取暂存）
	 */
	private static final int WHAT_NET_INFO = 0x12;
	/**
	 * 提交暂存
	 */
	private static final int WHAT_NET_SKU_TEMP = 0x13;

	private static final int FOR_RESULT_and = 0x10;

	@ViewInject(R.id.and_title)
	private TitleWidget and_title;
	@ViewInject(R.id.and_list)
	private ListView listView;
	@ViewInject(R.id.tv_confirm)
	private TextView tv_confirm; // 确定
	@ViewInject(R.id.tv_check)
	private TextView tv_check; // 核对
	@ViewInject(R.id.error_layout)
	private LinearLayout error_layout;
	@ViewInject(R.id.repeat_load_btn)
	private Button repeat_load_btn;
	@ViewInject(R.id.recommend_txt)
	private TextView recommend_txt; // 推荐库位

	private SkuEditText sku_shelf; // 库位
	private TextView sku_really_all_count; // 已扫数量
	private TextView sku_scan_detail; // 明细

	private String skuShelfCode; // 库位码（显示在库位位置）
	private ArrayList<String> stringList; // 存储扫码记录

	private HotApplication ap;
	private SkuShelvesOneListAdapter listAdapter;
	private ArrayList<InStockDetail> skuList = new ArrayList<InStockDetail>();
	private ArrayList<InStockDetail> newList = new ArrayList<InStockDetail>();// 显示三条数据的
																				// 集合
	private boolean canReceive;

	private String receiveSheetNo;
	private ReceiveBean rb;
	private boolean ifsame;
	private String difforsame;
	// 防止双击
	private long lastClickTime = 0;

	private ArrayList<ReceiveRecommendBean> recommendList;// 推荐库位列表
	private BaseDialog addDialog;
	private ClearEditText dialogSku;
	private ClearEditText dialogCount;
	private SkuEditText dialogStock;
	private TextView dialogSure;
	private TextView dialogCancel;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_main_shelf_and;
	}

	@Override
	protected String[] exInitReceiver() {
		return super.exInitReceiver();
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		and_title.setOnLeftClickListner(this);
		and_title.setOnRightClickListner(this);
		and_title.setText("上架");
		repeat_load_btn.setOnClickListener(this);
		tv_confirm.setOnClickListener(this);
		tv_check.setOnClickListener(this);
		rb = new ReceiveBean();
		rb.checkinstockdetailList = new ArrayList<InStockDetail>();
		rb.detailList = new ArrayList<InStockDetail>();
		stringList = new ArrayList<String>();
		receiveSheetNo = getIntent().getStringExtra("receiveSheetNo");

		// 明细（+获取暂存）
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.InfoList, WHAT_NET_INFO, NET_METHOD_POST, false);

		listAdapter = new SkuShelvesOneListAdapter(getLayoutInflater(), newList);

		View footView = getLayoutInflater().inflate(R.layout.sku_shelves_foot, null);
		sku_shelf = (SkuEditText) footView.findViewById(R.id.sku_shelf);
		sku_shelf.stopEdit();
		sku_really_all_count = (TextView) footView.findViewById(R.id.sku_really_all_count);
		sku_scan_detail = (TextView) footView.findViewById(R.id.sku_scan_detail);
		sku_scan_detail.setOnClickListener(this);

		View headView = getLayoutInflater().inflate(R.layout.sku_shelves_item_layout, null);
		listView.addHeaderView(headView);
		listView.addFooterView(footView);
		listView.setAdapter(listAdapter);
		sku_really_all_count.setText("" + SkuUtil.getnewSkuCount(rb.checkinstockdetailList));

		// 推荐库位
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.recommendStockList, WHAT_NET_GET_RECOMMEND_STOCK_LIST, NET_METHOD_POST, false);
		createDialog();
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_SKU_SHELF:
		case WHAT_NET_SKU_TEMP:
			return WarehouseNet.tempsto(receiveSheetNo, rb.checkinstockdetailList);
		case WHAT_NET_INFO:
			return WarehouseNet.stockInfo(receiveSheetNo);
		case WHAT_NET_GET_RECOMMEND_STOCK_LIST:
			return WarehouseNet.recommend(receiveSheetNo);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		// 操作日志
		LogUtils.logOnFile("入库上架->上架" + message);
		switch (what) {
		case WHAT_NET_SKU_SHELF:
			Ex.Toast(this).showLong(R.string.urlError);
			break;
		case WHAT_NET_INFO:
			Ex.Toast(this).showLong(R.string.listgetError);
			break;
		case WHAT_NET_SKU_TEMP:
			Ex.Toast(this).showLong(R.string.getInfo);
			break;
		case WHAT_NET_GET_RECOMMEND_STOCK_LIST:
			Ex.Toast(this).showLong(R.string.getInfo);
			error_layout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			tv_confirm.setVisibility(View.GONE);
			tv_check.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// 操作日志
		LogUtils.logOnFile("入库上架->上架" + result);
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_SKU_SHELF:
		case WHAT_NET_SKU_TEMP:
			Ex.Toast(mActivity).showLong(net.message);
			ShelfAndInStockActivity.stockComplete = true;
			this.finish();
			break;
		case WHAT_NET_INFO:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			rb = mGson.fromJson(str, ReceiveBean.class);
			if (rb.checkinstockdetailList == null)
				rb.checkinstockdetailList = new ArrayList<InStockDetail>();
			if (rb.checkinstockdetailList.size() > 0) {
				new AlertDialog.Builder(mActivity).setTitle(R.string.noteTitle).setMessage("当前有暂存数据,是否使用").setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 日志操作
						LogUtils.logOnFile("上架->暂存弹窗->否");
						rb.checkinstockdetailList.clear();
						sku_really_all_count.setText("" + SkuUtil.getnewSkuCount(rb.checkinstockdetailList));
						dialog.dismiss();
					}
				}).setPositiveButton("是", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// 日志操作
						LogUtils.logOnFile("上架->暂存弹窗->是");
						sku_shelf.setText(rb.checkinstockdetailList.get(rb.checkinstockdetailList.size() - 1).ShelfLocationCode);
						sku_really_all_count.setText("" + SkuUtil.getnewSkuCount(rb.checkinstockdetailList));
						ArrayList<InStockDetail> ddList = new ArrayList<InStockDetail>();
						for (InStockDetail dd : rb.checkinstockdetailList) {
							if (dd.ShelfLocationCode.equals(sku_shelf.getText(MainShelfAndActivity.this))) {
								ddList.add(dd);
							}
						}
						// 获取暂存数据
						SkuUtil.skunewShowList(ddList, newList);
						listAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				}).show();
			}
			break;

		case WHAT_NET_GET_RECOMMEND_STOCK_LIST:
			error_layout.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			tv_check.setVisibility(View.VISIBLE);
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String strCheck = mGson.toJson(net.data);
			recommendList = mGson.fromJson(strCheck, new TypeToken<List<ReceiveRecommendBean>>() {
			}.getType());
			break;
		}
	}

	@Override
	protected void onResume() {
		canReceive = true;
		super.onResume();
		// 日志操作
		LogUtils.logOnFile("进入->上架界面");
	}

	@Override
	protected void onPause() {
		canReceive = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		canReceive = false;
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:// 返回按钮
			if (rb.checkinstockdetailList != null && rb.checkinstockdetailList.size() > 0) {
				new AlertDialog.Builder(mActivity).setTitle(R.string.noteTitle).setMessage("当前有操作数据，点击确认暂存").setNegativeButton("不暂存", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 日志操作
						LogUtils.logOnFile("上架->弹窗->不暂存");
						dialog.dismiss();
						finish();
					}
				}).setPositiveButton("暂存", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// 日志操作
						LogUtils.logOnFile("上架->弹窗->暂存");
						startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.temporary, WHAT_NET_SKU_TEMP, true, NET_METHOD_POST, SaveListUtil.shelfSave(receiveSheetNo, rb.checkinstockdetailList), false);
					}
				}).show();
			} else {
				finish();
			}
			break;
		case R.id.tv_check:// 核对
			// 日志操作
			LogUtils.logOnFile("上架->核对");
			Intent intent1 = new Intent(mActivity, ShelfAndCheckActivity.class);
			intent1.putExtra("skuList", rb.checkinstockdetailList == null ? new ArrayList<InStockDetail>() : rb.checkinstockdetailList);
			intent1.putExtra("sendList", rb.detailList);
			startActivity(intent1);
			break;
		case R.id.sku_scan_detail:// 明细
			// 日志操作
			LogUtils.logOnFile("上架->明细");
			Intent intent = new Intent(mActivity, ShelfAndStockDetailActivity.class);
			intent.putExtra("skuList", rb.checkinstockdetailList == null ? new ArrayList<InStockDetail>() : rb.checkinstockdetailList);
			intent.putExtra("checkList", rb.detailList);
			startActivityForResult(intent, FOR_RESULT_and);
			break;
		case R.id.tv_confirm:// 确定
			Log.i("TAG", newList.toString());
			// 日志操作
			LogUtils.logOnFile("上架->确定");
			ifsame = false;
			List<CheckBean> skulist = SkuUtil.cbCheck(rb.checkinstockdetailList == null ? new ArrayList<InStockDetail>() : rb.checkinstockdetailList, rb.detailList);
			for (int i = 0; i < skulist.size(); i++) {
				CheckBean cb = skulist.get(i);
				if (cb.reallyCount != cb.shouldCount) {
					ifsame = true;
				}
			}
			if (ifsame) {
				difforsame = "数量有差异，检查核对数量再确认";
			} else {
				difforsame = "数量正确";
			}
			new AlertDialog.Builder(this).setTitle(R.string.noteTitle).setMessage(difforsame).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 日志操作
					LogUtils.logOnFile("上架->弹窗->取消");
					dialog.dismiss();
				}
			}).setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					// 日志操作
					LogUtils.logOnFile("上架->弹窗->确定");
					// 防止双击
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastClickTime > 1000) {
						lastClickTime = currentTime;
						// 操作事件
						if (rb.checkinstockdetailList.size() > 0) {
							startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.newIn, WHAT_NET_SKU_SHELF, true, NET_METHOD_POST, SaveListUtil.shelfSave(receiveSheetNo, rb.checkinstockdetailList), false);
						} else {
							Ex.Toast(mActivity).show("无数据，不能提交");
						}
					}

				}
			}).show();
			break;
		case R.id.repeat_load_btn:// 重新加载（未用）
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.recommendStockList, WHAT_NET_GET_RECOMMEND_STOCK_LIST, NET_METHOD_POST, false);
			break;
		case R.id.lv_right:
			addDialog.show();
			break;
		case R.id.dialog_sure:
			handAddSku();
			break;
		case R.id.dialog_cancel:
			clearDialogDate();
			addDialog.dismiss();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == FOR_RESULT_and) {
			rb.checkinstockdetailList.clear();
			List<InStockDetail> result = (List<InStockDetail>) data.getSerializableExtra("wList");
			rb.checkinstockdetailList.addAll(result);
			sku_really_all_count.setText("" + SkuUtil.getnewSkuCount(rb.checkinstockdetailList));
			boolean isNoValue = data.getBooleanExtra("isNoValue", false);
			if (!isNoValue) {
				ArrayList<InStockDetail> ddList = new ArrayList<InStockDetail>();// 显示的列表
				for (InStockDetail dd : rb.checkinstockdetailList) {
					if (dd.ShelfLocationCode.equals(sku_shelf.getText(this))) {
						ddList.add(dd);
					}
				}
				SkuUtil.skunewShowList(ddList, newList);
				listAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (rb.checkinstockdetailList.size() > 0) {
			new AlertDialog.Builder(mActivity).setTitle(R.string.noteTitle).setMessage("当前有操作数据，点击确认暂存").setNegativeButton("不暂存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 日志操作
					LogUtils.logOnFile("上架->暂存弹窗->不暂存");
					dialog.dismiss();
					finish();
				}
			}).setPositiveButton("暂存", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					// 日志操作
					LogUtils.logOnFile("上架->暂存弹窗->暂存");
					startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.temporary, WHAT_NET_SKU_TEMP, true, NET_METHOD_POST, SaveListUtil.shelfSave(receiveSheetNo, rb.checkinstockdetailList), false);
				}
			}).show();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void fillCode(String code) {
		if (!addDialog.isShowing()) {
			dealBarCode(code);
		}
		LogUtils.logOnFile("上架->扫码：" + code);
	}

	private void dealBarCode(String barcode) {
		if (canReceive) {
			skuShelfCode = sku_shelf.getText(this);
			// 如果扫的是商品
			if ((!SkuUtil.isWarehouse(barcode))) {
				boolean isExist = false;
				if (rb != null && rb.detailList != null) {
					for (InStockDetail isd : rb.detailList) {
						if (isd.SKUCode.equals(barcode)) {
							isExist = true;
						}
					}
					if (!isExist) {
						Toast.makeText(this, "没有此商品", Toast.LENGTH_LONG).show();
						ap.getsoundPool(ap.Sound_error);
						return;
					}
				}

				ap.getsoundPool(ap.Sound_sku);
				if (skuShelfCode.length() > 0) {
					skuList.clear();
					sku_shelf.setText("");
				}

				int maxCount = 0;
				for (InStockDetail isd : rb.detailList) {
					if (isd.SKUCode.equalsIgnoreCase(barcode)) {
						maxCount = (int) isd.SKUCount;
						break;
					}
				}
				int scanedCount = 0;
				for (InStockDetail isd : rb.checkinstockdetailList) {
					if (isd.SKUCode.equalsIgnoreCase(barcode)) {
						scanedCount += isd.SKUCount;
					}
				}
				// if (stringList.size() + scanedCount >= maxCount) {
				// Toast.makeText(this, "已达到最大数值", Toast.LENGTH_LONG).show();
				// ap.getsoundPool(ap.Sound_error);
				// return;
				// }

				stringList.add(barcode);
				InStockDetail ssb = new InStockDetail(barcode, 1);
				SkuUtil.newcheck(skuList, ssb);
				SkuUtil.skunewShowList(skuList, newList);
				listAdapter.notifyDataSetChanged();
				if (recommendList != null) {
					boolean haveRecommend = false;
					for (ReceiveRecommendBean recommend : recommendList) {
						if (recommend.SKUCode.equals(barcode)) {
							recommend_txt.setText(recommend.ShelfLocationCode);
							haveRecommend = true;
							break;
						}
					}
					if (!haveRecommend) {
						recommend_txt.setText("无");
					}
				}
			}
			// 如果扫的是库位
			else if (SkuUtil.isWarehouse(barcode) && stringList.size() > 0) {
				ap.getsoundPool(ap.Sound_location);
				sku_shelf.setText(barcode);
				// 遍历寻找库位是否存在
				for (int i = 0; i < skuList.size(); i++) {
					skuList.get(i).ShelfLocationCode = barcode;
					push(skuList.get(i));
				}
				sku_really_all_count.setText(SkuUtil.getnewSkuCount(rb.checkinstockdetailList) + "");
				stringList.clear();
			}
		}
	}

	private void push(InStockDetail item) {
		if (rb.checkinstockdetailList == null)
			rb.checkinstockdetailList = new ArrayList<InStockDetail>();
		for (int i = 0; i < rb.checkinstockdetailList.size(); i++) {
			InStockDetail tItem = rb.checkinstockdetailList.get(i);
			if (tItem.SKUCode.equals(item.SKUCode) && tItem.ShelfLocationCode.equals(item.ShelfLocationCode)) {
				tItem.SKUCount += item.SKUCount;
				return;
			}
		}
		rb.checkinstockdetailList.add(item);
	}

	@Override
	public void onReceiver(Intent intent) {
		
	}

	/**
	 * 手动添加弹窗
	 */
	private void createDialog() {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_hand_add, null);
		addDialog = new BaseDialog(mActivity, view);
		addDialog.setCanceledOnTouchOutside(false);
		dialogSku = (ClearEditText) view.findViewById(R.id.dialog_sku);
		dialogCount = (ClearEditText) view.findViewById(R.id.dialog_count);
		dialogStock = (SkuEditText) view.findViewById(R.id.dialog_stock);
		dialogSure = (TextView) view.findViewById(R.id.dialog_sure);
		dialogCancel = (TextView) view.findViewById(R.id.dialog_cancel);
		dialogSure.setOnClickListener(this);
		dialogCancel.setOnClickListener(this);
	}

	/**
	 * 手动添加商品
	 */
	private void handAddSku() {
		// 输入框空数据验证
		if (TextUtils.isEmpty(dialogSku.getText().toString())) {
			Toast.makeText(this, "请输入SKU", Toast.LENGTH_LONG).show();
			return;
		}
		if (TextUtils.isEmpty(dialogCount.getText().toString())) {
			Toast.makeText(this, "请输入数量", Toast.LENGTH_LONG).show();
			return;
		}
		if (TextUtils.isEmpty(dialogStock.getText(mActivity))) {
			Toast.makeText(this, "请输入库位码", Toast.LENGTH_LONG).show();
			return;
		}
		// 入库码验证
		if (rb != null && rb.detailList != null) {
			boolean isExist = false;
			for (InStockDetail isd : rb.detailList) {
				if (isd.SKUCode.equals(dialogSku.getText().toString().toUpperCase())) {
					isExist = true;
				}
			}
			if (!isExist) {
				Toast.makeText(this, "没有此商品", Toast.LENGTH_LONG).show();
				return;
			}
		}
		// 添加数据到列表
		for (int i = 0; i < Integer.valueOf(dialogCount.getText().toString()); i++) {
			dealBarCode(dialogSku.getText().toString().toUpperCase());
		}
		dealBarCode(dialogStock.getText(mActivity));
		// 清除数据
		clearDialogDate();
		addDialog.dismiss();
	}

	/**
	 * 清除弹框数据
	 */
	private void clearDialogDate() {
		dialogSku.setText("");
		dialogCount.setText("");
		dialogStock.setText("");
	}
}
