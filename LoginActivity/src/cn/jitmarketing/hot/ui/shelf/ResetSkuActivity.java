package cn.jitmarketing.hot.ui.shelf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ResetListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ResetEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.RefreshableView;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.util.RefreshableView.PullToRefreshListener;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

/**
 * 待复位
 */
public class ResetSkuActivity extends BaseSwipeOperationActivity implements OnClickListener {
	private static final int WHAT_NET_GET_RESET_SKU = 0x10;
	private static final int WHAT_NET_GET_RESET_SKU_ok = 0x11;
	@ViewInject(R.id.reset_postion_title)
	TitleWidget titleWidget;
	// -----------------------搜索框布局
	@ViewInject(R.id.search_layout)
	LinearLayout searchLayout;
	// 搜索输入框
	@ViewInject(R.id.check_stock_search_edt)
	cn.jitmarketing.hot.view.ClearEditText searchClearEditText;
	// 搜索取消按钮
	@ViewInject(R.id.check_storck_cancel)
	TextView searchCancle;
	@ViewInject(R.id.hide_search_layout)
	LinearLayout hide_search_layout;
	
	@ViewInject(R.id.only_list)
	ListView only_list;
	@ViewInject(R.id.text_tips)
	TextView text_tips;
	@ViewInject(R.id.refreshable_view)
	RefreshableView refreshableView;

	private String skuCode = null;
	private String shelfCode = null;
	private ArrayList<ResetEntity> resetList = new ArrayList<ResetEntity>();
	private ArrayList<ResetEntity> searchList = new ArrayList<ResetEntity>();
	private ArrayList<ResetEntity> newreset;
	private int listLocation;
	private String factID;
	private ResetListAdapter adapter;
	private HotApplication ap;
	private String barcode;
	private static String RESET_ONE = "1024";// 复位
	private static String RESET_TWO = "2048";// 复位
	private static String REVOIKE = "4097";// 撤样
	private int DELYED = 60 * 1000;
	private long ResetCount;
	private long NeedResetCount;
	private boolean isScan = true;// 是否可以扫描
	public static boolean isRefreshList;
	public ResetEntity selectEntity;

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_only_list;
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		titleWidget.setOnLeftClickListner(this);
		titleWidget.setOnRightClickListner(this);
		searchCancle.setOnClickListener(this);
        //界面复用 隐藏该控件
		hide_search_layout.setVisibility(View.GONE);
		searchClearEditText.addTextChangedListener(textWatcher);
		text_tips.setVisibility(View.VISIBLE);
		adapter = new ResetListAdapter(ResetSkuActivity.this, resetList);
		only_list.setAdapter(adapter);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ResetSkuList, SkuNet.getResetSkuList(), requestCallback);
			}
		}, 1);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ResetSkuList, WHAT_NET_GET_RESET_SKU, NET_METHOD_POST, false);
		handler.postDelayed(runnable, DELYED);
	}

	ExRequestCallback requestCallback = new ExRequestCallback() {
		@Override
		public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {
			// 请求结果
			String result = "";
			// 判断结果流是否为空
			if (inStream != null) {
				// 转换流对象
				result = Ex.T().getInStream2Str(inStream);
				ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
				String resetStr = mGson.toJson(net.data);
				try {
					JSONObject js = new JSONObject(resetStr);
					String getList = js.getString("List");
					NeedResetCount = js.getLong("NeedResetCount");
					ResetCount = js.getLong("ResetCount");
					newreset = mGson.fromJson(getList, new TypeToken<List<ResetEntity>>() {
					}.getType());
					handler1.sendEmptyMessage(1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onError(int statusCode, ExException e) {
			handler1.sendEmptyMessage(2);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		isScan = true;
		if (isRefreshList) {
			if (selectEntity != null) {
				resetList.remove(selectEntity);
				adapter.notifyDataSetChanged();
				selectEntity = null;
				ResetCount++;
				NeedResetCount--;
				String source = "今日已复位<font color='green'>" + ResetCount + "</font>条，待复位<font color='red'>" + NeedResetCount + "</font>条。";
				text_tips.setText(Html.fromHtml(source));
			}
		}
		// startTask(HotConstants.Global.APP_URL_USER
		// + HotConstants.Shelf.ResetSkuList, WHAT_NET_GET_RESET_SKU,
		// NET_METHOD_POST, false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		isScan = false;
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(runnable);// 停止定时器
		super.onDestroy();
	}

	int scanResetCount = 1;// 重复扫描同一个sku的次数,默认是1次

	@SuppressLint("ShowToast")
	@Override
	public void onReceiver(Intent intent) {
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// barcode = new String(code, 0, codelen).toUpperCase().trim();
		// dealBarCode(barcode);
	}

	@Override
	public void fillCode(String code) {
		dealBarCode(code);
	}

	private void dealBarCode(String barcode) {
		if (!isScan) {
			return;
		}
		// 复位列表为空
		if (resetList == null || resetList.size() == 0) {
			ap.getsoundPool(ap.Sound_error);
			return;
		}

		if (!SkuUtil.isWarehouse(barcode)) {// 先扫描商品码
			boolean isExist = false;
			if (skuCode == null) {
				skuCode = barcode;
			} else {
				if (skuCode.equals(barcode)) {// 重复扫描同一个sku
					scanResetCount++;
				} else {
					scanResetCount = 1;
					skuCode = barcode;
				}
			}
			int foundRestCount = 0;// 在list中找到相同sku的次数
			for (int i = 0; i < resetList.size(); i++) {
				if (resetList.get(i).SKUCode.equals(skuCode)) {
					ap.getsoundPool(ap.Sound_sku);
					isExist = true;
					factID = resetList.get(listLocation).FactID;
					foundRestCount++;
					if (foundRestCount == scanResetCount) {
						only_list.smoothScrollToPosition(i);
						adapter.setSelectItem(i);
						adapter.notifyDataSetChanged();
						break;
					}
				}
			}
			if (!isExist) {
				ap.getsoundPool(ap.Sound_error);
				skuCode = null;
				Ex.Toast(mContext).showLong("当前复位列表没有此商品");
			}
		} else {// 扫过商品码再扫描库位码
			if (skuCode != null) {
				shelfCode = barcode;
				// 是鞋子并且是撤样类型的，必须回到原库位
				boolean isExist = false;
				for (ResetEntity reset : resetList) {
					if (reset.SKUCode.equals(skuCode) && (reset.Qty.equals("1") || reset.Qty.equals("0.5") || reset.Qty.equals("0"))) {// 鞋子一只是qty为0.5，0是空鞋盒其他的为1
						factID = reset.FactID;

						if (!reset.IsShoe) {// 不是鞋子
							isExist = true;
							selectEntity = reset;
							startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ResetSkuOk, WHAT_NET_GET_RESET_SKU_ok, NET_METHOD_POST, false);
							break;
						} else {// 鞋类

							if (reset.IsRevokeSample) {// 撤样类型
								if (reset.ShelfLocationCode.equals(shelfCode)) {
									isExist = true;
									selectEntity = reset;
									startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ResetSkuOk, WHAT_NET_GET_RESET_SKU_ok, NET_METHOD_POST, false);
									break;
								} else {
									Ex.Toast(mContext).showLong("库位不准确");
								}

							} else if (reset.OperTypeString.equals("复位")) {// 取新复位类型
								// if
								// (reset.ShelfLocationCode.equals(shelfCode))
								// {//复位到原库位
								isExist = true;
								selectEntity = reset;
								startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ResetSkuOk, WHAT_NET_GET_RESET_SKU_ok, NET_METHOD_POST, false);
								break;
								// } else {
								// Ex.Toast(mContext).showLong("库位不准确");
								// }
							} else {
								isExist = true;
								selectEntity = reset;
								startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ResetSkuOk, WHAT_NET_GET_RESET_SKU_ok, NET_METHOD_POST, false);
								break;
							}
						}
					}
				}
				if (!isExist) {
					skuCode = null;
					ap.getsoundPool(ap.Sound_error);
					Ex.Toast(mContext).showLong("没有找到匹配的任务");
				} else {
					ap.getsoundPool(ap.Sound_location);
				}
			} else {
				ap.getsoundPool(ap.Sound_error);
				Toast.makeText(this, "请先扫商品码！", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_RESET_SKU:
			return SkuNet.getResetSkuList();
		case WHAT_NET_GET_RESET_SKU_ok:
			return SkuNet.getResetSkuOk(skuCode, shelfCode, factID);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_RESET_SKU:
			isRefreshList = false;
			Ex.Toast(mContext).showLong("你的网速不太好，获取列表失败");
			break;
		case WHAT_NET_GET_RESET_SKU_ok:
			skuCode = null;
			shelfCode = null;
			Ex.Toast(mContext).showLong("你的网速不太好，复位失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			skuCode = null;
			shelfCode = null;
			Ex.Toast(mContext).showLong(net.message);
			isRefreshList = false;
			return;
		}
		switch (what) {
		case WHAT_NET_GET_RESET_SKU:
			isRefreshList = false;
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			String resetStr = mGson.toJson(net.data);
			try {
				resetList.clear();
				searchList.clear();
				JSONObject js = new JSONObject(resetStr);
				String getList = js.getString("List");
				NeedResetCount = js.getLong("NeedResetCount");
				ResetCount = js.getLong("ResetCount");
				ArrayList<ResetEntity> reset = mGson.fromJson(getList, new TypeToken<List<ResetEntity>>() {
				}.getType());
				resetList.addAll(reset);
				searchList.addAll(reset);
				Collections.sort(resetList, new ResetEntity());
				Collections.reverse(resetList);
				adapter.notifyDataSetChanged();
				String source = "今日已复位<font color='green'>" + ResetCount + "</font>条，待复位<font color='red'>" + NeedResetCount + "</font>条。";
				text_tips.setText(Html.fromHtml(source));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case WHAT_NET_GET_RESET_SKU_ok:
			skuCode = null;
			shelfCode = null;
			Ex.Toast(mContext).show("复位成功");
			if (selectEntity != null) {
				resetList.remove(selectEntity);
				adapter.notifyDataSetChanged();
				selectEntity = null;
			}
			// startTask(HotConstants.Global.APP_URL_USER
			// + HotConstants.Shelf.ResetSkuList, WHAT_NET_GET_RESET_SKU,
			// NET_METHOD_POST, false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		case R.id.lv_right:
			showSearchLayout();
			break;
		case R.id.check_storck_cancel:
			hideSearchLayout();
			resetList.clear();
			resetList.addAll(searchList);
			Collections.sort(resetList, new ResetEntity());
			Collections.reverse(resetList);
			adapter.notifyDataSetChanged();
			break;
		}

	}

	Handler handler = new Handler();
	Handler handler1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				Ex.Toast(mContext).showLong("刷新成功");
				resetList.clear();
				searchList.clear();
				resetList.addAll(newreset);
				Collections.sort(resetList, new ResetEntity());
				Collections.reverse(resetList);
				searchList.addAll(newreset);
				String source = "今日已复位<font color='green'>" + ResetCount + "</font>条，待复位<font color='red'>" + NeedResetCount + "</font>条。";
				text_tips.setText(Html.fromHtml(source));
				adapter.notifyDataSetChanged();
				refreshableView.finishRefreshing();
			} else if (msg.what == 2) {
				refreshableView.finishRefreshing();
				Ex.Toast(mContext).showLong("刷新失败");
			}
		}
	};
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				handler.postDelayed(this, DELYED);
				if (adapter != null) {
					if (resetList != null && resetList.size() > 0) {
						for (ResetEntity en : resetList) {
							en.TimeOut = String.valueOf(Long.valueOf(en.TimeOut) + 60);
						}
						adapter.notifyDataSetChanged();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("exception...");
			}
		}
	};

	/**
	 * show搜索框
	 */
	private void showSearchLayout() {
		titleWidget.setVisibility(View.GONE);
		searchLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * hide搜索框
	 */
	private void hideSearchLayout() {
		titleWidget.setVisibility(View.VISIBLE);
		searchLayout.setVisibility(View.GONE);
	}

	/**
	 * 搜索框输入监听
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			if (s != null) {
				resetList.clear();
				for (int i = 0; i < searchList.size(); i++) {
					if (searchList.get(i).SKUCode.contains(s.toString().toUpperCase())) {
						resetList.add(searchList.get(i));
					}
				}
				adapter.notifyDataSetChanged();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}
	};
}
