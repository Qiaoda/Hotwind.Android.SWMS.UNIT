package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.StockCustomDialog;
import cn.jitmarketing.hot.view.StockCustomDialog.OnCustomDialogListener;
import cn.jitmarketing.hot.view.TitleWidget;

public class CGYStockActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.cgy_title)
	TitleWidget cgy_title;
	private TextView sku_really_all_count;
	private TextView sku_sure;
	private TextView sku_hand;
	private TextView sku_scan_detail;
	private SkuEditText sku_shelf;
	private static final int WHAT_NET_check_sure = 0x11;
	private static final int WHAT_NET_Small_check_sure = 0x16;
	private static final int FOR_RESULT_HAND_SKU = 0x10;
	private static final int WHAT_NET_DIFF_DETAIL = 0x12;
	private static final int FOR_DETAIL_SKU = 0x13;
	private static final int CONFIRM_PANDIAN = 0x14;
	private boolean canReceive;
	private List<SkuBean> scanSkuList;
	private ArrayList<SkuBean> scanLists;
	private String ShelfLocationCode;
	private ListView sku_list;
	private CGYScanSkuAdapter adapter;
	private HotApplication ap;
	private boolean isSampleShelfCode;// 是否样品库\
	private boolean isSmall;// 是否小商品
	private boolean havePandian;// 是否盘点过//true盘点过
	private int checkTimes;// 盘点的次数
	private String[] difList;
	private boolean requestMore;// 再次请求（首次请求失败）

	private StockCustomDialog stockDialog;
	// 防止双击操作
	private long lastClickTime = 0;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.cgy_stock_item;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (scanLists != null && scanLists.size() > 0) {
				new AlertDialog.Builder(CGYStockActivity.this).setMessage("是否放弃本次盘点").setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						CGYStockActivity.this.finish();
					}
				}).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		cgy_title.setOnLeftClickListner(this);
		cgy_title.setOnRightClickListner(this);
		cgy_title.setText("库位盘点");
		sku_list = (ListView) findViewById(R.id.sku_list);
		sku_hand = (TextView) findViewById(R.id.sku_hand);
		sku_sure = (TextView) findViewById(R.id.sku_sure);
		sku_hand.setOnClickListener(this);
		sku_sure.setOnClickListener(this);
		scanSkuList = new ArrayList<SkuBean>();
		scanLists = new ArrayList<SkuBean>();
		View headView = getLayoutInflater().inflate(R.layout.sku_shelves_item_layout, null);
		View footview = getLayoutInflater().inflate(R.layout.sku_shelves_foot, null);
		sku_scan_detail = (TextView) footview.findViewById(R.id.sku_scan_detail);
		sku_really_all_count = (TextView) footview.findViewById(R.id.sku_really_all_count);
		sku_shelf = (SkuEditText) footview.findViewById(R.id.sku_shelf);
		Bundle bd = getIntent().getExtras();
		ShelfLocationCode = bd.getString("ShelfLocationCode");
		isSampleShelfCode = bd.getBoolean("isSampleShelfCode");
		isSmall = bd.getBoolean("isSmall");
		havePandian = bd.getBoolean("havePandian");
		checkTimes = bd.getInt("CheckTimes");
		int differenceCount = bd.getInt("differenceCount");
		String differenceMoney = bd.getString("differenceMoney");
		if (checkTimes == 1 && isSampleShelfCode) {
			cgy_title.setRightVisibility(View.VISIBLE);
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.PlanStockDiffList, WHAT_NET_DIFF_DETAIL, NET_METHOD_POST, false);
		} else {
			cgy_title.setRightVisibility(View.GONE);
		}
		sku_shelf.setText(ShelfLocationCode);
		sku_shelf.stopEdit();
		sku_scan_detail.setOnClickListener(this);
		adapter = new CGYScanSkuAdapter(getLayoutInflater(), scanSkuList);
		sku_list.addHeaderView(headView);
		sku_list.addFooterView(footview);
		sku_list.setAdapter(adapter);
		sku_really_all_count.setText("0");
		if (!isSampleShelfCode && havePandian) {
			stockDialog = new StockCustomDialog(CGYStockActivity.this, new OnCustomDialogListener() {

				@Override
				public void isReset(boolean reset) {
					StockTakingWarehouseActivity.refresh = true;
					if (reset) {
						scanSkuList.clear();
						scanLists.clear();
						adapter.notifyDataSetChanged();
					} else {
						startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmShelf, CONFIRM_PANDIAN, NET_METHOD_POST, false);
					}
				}
			}, String.valueOf(differenceCount), differenceMoney);
			stockDialog.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		canReceive = true;
		super.onResume();
	}

	@Override
	protected void onStop() {
		canReceive = false;
		super.onStop();
	}

	@Override
	protected void onPause() {
		canReceive = false;
		super.onPause();
	}

	@Override
	public void onReceiver(Intent intent) {
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// String barcode = new String(code, 0, codelen).toUpperCase().trim();
		// dealBarCode(barcode);
	}

	// @Override
	// public void fillCode(String code) {
	// dealBarCode(code);
	// }

	private void dealBarCode(String barcode) {
		if (canReceive == true) {
			if (!SkuUtil.isWarehouse(barcode)) {
				ap.getsoundPool(ap.Sound_sku);
				SkuBean s = new SkuBean(barcode, 1);
				SkuUtil.getSku(scanLists, s);
				SkuUtil.skuShowList(scanLists, scanSkuList);
				adapter.notifyDataSetChanged();
				sku_really_all_count.setText("" + SkuUtil.getSkuCount(scanLists));
			} else {
				ap.getsoundPool(ap.Sound_error);
			}
			
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_DIFF_DETAIL:
			return WarehouseNet.getCheckStockList();
		case CONFIRM_PANDIAN:
			return WarehouseNet.pandianCofirm(ShelfLocationCode);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		// 操作日志
		LogUtils.logOnFile("库位盘点->" + message);
		Ex.Toast(this).show("你的网速不太好，获取失败");
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// 操作日志
		LogUtils.logOnFile("库位盘点->" + result);
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message, 6000);
			// 操作日志
			LogUtils.logOnFile("库位盘点->" + net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_check_sure:
			if (isSampleShelfCode) {// 样品库
				StockTakingWarehouseActivity.refresh = true;
				Ex.Toast(mActivity).show("盘点成功");
				// 操作日志
				LogUtils.logOnFile("库位盘点->" + "盘点成功");
				this.finish();
			} else {// 非样品库
				String str = mGson.toJson(net.data);
				try {
					JSONObject js = new JSONObject(str);
					int differenceCount = js.getInt("DifferenceCount");
					String differenceMoney = js.getString("DifferencePriceCount");
					// 是否重盘弹窗
					stockDialog = new StockCustomDialog(CGYStockActivity.this, new OnCustomDialogListener() {

						@Override
						public void isReset(boolean reset) {
							StockTakingWarehouseActivity.refresh = true;
							if (reset) {
								scanSkuList.clear();
								scanLists.clear();
								adapter.notifyDataSetChanged();
							} else {
								// 确定不重盘,避免双击
								long currentTime = System.currentTimeMillis();
								if (currentTime - lastClickTime > 1000) {
									lastClickTime = currentTime;
									startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmShelf, CONFIRM_PANDIAN, NET_METHOD_POST, false);
								}
							}
						}
					}, String.valueOf(differenceCount), differenceMoney);
					stockDialog.show();
				} catch (JSONException e) {
				}
			}

			break;
		case WHAT_NET_DIFF_DETAIL:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				// 操作日志
				LogUtils.logOnFile("库位盘点->" + net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			difList = mGson.fromJson(str, new TypeToken<String[]>() {
			}.getType());
			if (requestMore) {
				requestMore = false;
				Intent intent1 = new Intent(this, StockTakingDiffrenceActivity.class);
				Bundle bd = new Bundle();
				bd.putStringArray("differenceShelf", difList);
				bd.putBoolean("detail", true);
				intent1.putExtras(bd);
				startActivity(intent1);
			}
			break;
		case CONFIRM_PANDIAN:
			if (net.isSuccess) {
				if (stockDialog != null)
					stockDialog.dismiss();
				Ex.Toast(mActivity).showLong("盘点成功");
				// 操作日志
				LogUtils.logOnFile("库位盘点->" + "盘点成功");
				CGYStockActivity.this.finish();
				return;
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			// 日志操作
			LogUtils.logOnFile("盘点->库位盘点->返回");
			if (scanLists != null && scanLists.size() > 0) {
				new AlertDialog.Builder(CGYStockActivity.this).setMessage("是否放弃本次盘点").setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						// 日志操作
						LogUtils.logOnFile("盘点->库位盘点->放弃盘点->确定");
						CGYStockActivity.this.finish();
					}
				}).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 日志操作
						LogUtils.logOnFile("盘点->库位盘点->放弃盘点->取消");
					}
				}).show();
			} else {
				this.finish();
			}
			break;
		case R.id.lv_right:
			if (difList == null) {
				requestMore = true;
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.PlanStockDiffList, WHAT_NET_DIFF_DETAIL, NET_METHOD_POST, false);
			} else {
				Intent intent1 = new Intent(this, StockTakingDiffrenceActivity.class);
				Bundle bd = new Bundle();
				bd.putStringArray("differenceShelf", difList);
				bd.putBoolean("detail", true);
				intent1.putExtras(bd);
				startActivity(intent1);
			}
			break;
		case R.id.sku_sure:
			// 日志操作
			LogUtils.logOnFile("盘点->库位盘点->确定");
			new AlertDialog.Builder(CGYStockActivity.this).setMessage("确定提交盘点？").setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					// 日志操作
					LogUtils.logOnFile("盘点->库位盘点->确定提交->确定");
					if (isSampleShelfCode) {
						if (!isSmall)
							startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SampleStockCheck, WHAT_NET_check_sure, true, NET_METHOD_POST, SaveListUtil.CGYStockSave(ShelfLocationCode, scanLists), false);
						else
							startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SamplesSmallStockCheck, WHAT_NET_check_sure, true, NET_METHOD_POST, SaveListUtil.CGYStockSave(ShelfLocationCode, scanLists), false);
					} else {
						startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.NomalStockCheck, WHAT_NET_check_sure, true, NET_METHOD_POST, SaveListUtil.CGYStockSave(ShelfLocationCode, scanLists), false);
					}
				}
			}).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 日志操作
					LogUtils.logOnFile("盘点->库位盘点->确定提交->取消");
				}
			}).show();

			break;
		case R.id.sku_hand:
			// 日志操作
			LogUtils.logOnFile("盘点->库位盘点->手工");
			Intent handIntent = new Intent();
			handIntent.setClass(CGYStockActivity.this, HandStockActivity.class);
			startActivityForResult(handIntent, FOR_RESULT_HAND_SKU);
			break;
		case R.id.sku_scan_detail:
			// 日志操作
			LogUtils.logOnFile("盘点->库位盘点->明细");
			Bundle bundle = new Bundle();
			bundle.putSerializable("list", scanLists);
			Intent intent = new Intent();
			intent.putExtra("bundle", bundle);
			intent.setClass(CGYStockActivity.this, CGYDetailActivity.class);
			startActivityForResult(intent, FOR_DETAIL_SKU);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == FOR_RESULT_HAND_SKU) {
				SkuBean ssb = (SkuBean) data.getBundleExtra("bundle").getSerializable("sbBean");
				if (ssb != null) {
					SkuUtil.HandgetSku(scanLists, ssb);
					SkuUtil.skuShowList(scanLists, scanSkuList);
					adapter.notifyDataSetChanged();
					sku_really_all_count.setText("" + SkuUtil.getSkuCount(scanLists));
				}
			} else if (requestCode == FOR_DETAIL_SKU) {
				ArrayList<SkuBean> updatedList = (ArrayList<SkuBean>) data.getBundleExtra("bundle").getSerializable("updatedList");
				if (updatedList != null) {
					scanLists = updatedList;
				}
				SkuUtil.skuShowList(scanLists, scanSkuList);
				adapter.notifyDataSetChanged();
				sku_really_all_count.setText("" + SkuUtil.getSkuCount(scanLists));
			}
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
		// 日志操作
		LogUtils.logOnFile("盘点->库位盘点->扫码：" + code);
	}

}
