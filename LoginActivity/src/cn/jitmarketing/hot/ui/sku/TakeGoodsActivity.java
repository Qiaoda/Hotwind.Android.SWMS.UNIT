package cn.jitmarketing.hot.ui.sku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.TakeGoodsListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.TakeGoodsEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.service.GroupEntity;
import cn.jitmarketing.hot.service.NoticeService;
import cn.jitmarketing.hot.service.NoticeSettingEntity;
import cn.jitmarketing.hot.util.RefreshableView;
import cn.jitmarketing.hot.util.RefreshableView.PullToRefreshListener;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.util.TakeGoodsDialog;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SelectCustomDialog;
import cn.jitmarketing.hot.view.SelectCustomDialog.OnCustomDialogListener;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 拿货
 */
public class TakeGoodsActivity extends BaseSwipeOperationActivity implements OnClickListener, OnItemClickListener {

	@ViewInject(R.id.reset_postion_title)
	TitleWidget reset_postion_title;
	/* 搜索框 */
	@ViewInject(R.id.search_edt)
	ClearEditText searchEdt;
	@ViewInject(R.id.only_list)
	ListView only_list;
	@ViewInject(R.id.text_tips)
	TextView text_tips;
	@ViewInject(R.id.refreshable_view)
	RefreshableView refreshableView;
	@ViewInject(R.id.text1)
	TextView text1;
	@ViewInject(R.id.text2)
	TextView text2;
	@ViewInject(R.id.chu_Linear)
	LinearLayout chu_Linear;
	@ViewInject(R.id.chu_tv)
	TextView chu_tv;

	@ViewInject(R.id.stock_tab)
	RelativeLayout stock_tab;
	@ViewInject(R.id.cb1)
	CheckBox cb1;
	@ViewInject(R.id.cb2)
	CheckBox cb2;
	@ViewInject(R.id.cb3)
	CheckBox cb3;
	@ViewInject(R.id.cb4)
	CheckBox cb4;
	@ViewInject(R.id.cb5)
	CheckBox cb5;
	@ViewInject(R.id.cb6)
	CheckBox cb6;
	@ViewInject(R.id.cb7)
	CheckBox cb7;
	private static final int WHAT_NET_GET_STOCK_LIST = 0x10;// 列表请求
	private static final int WHAT_NET_GET_STOCK = 0x11;

	private List<TakeGoodsEntity> stockList = new ArrayList<TakeGoodsEntity>();
	private List<TakeGoodsEntity> quNew = new ArrayList<TakeGoodsEntity>();
	private List<TakeGoodsEntity> chuyang = new ArrayList<TakeGoodsEntity>();
	private List<TakeGoodsEntity> search_quNew = new ArrayList<TakeGoodsEntity>();
	private List<TakeGoodsEntity> search_chuyang = new ArrayList<TakeGoodsEntity>();
	private int tab = 1;

	private List<TakeGoodsEntity> newskuTemp;
	private boolean isScanSku;// 是否扫描了sku码
	private String skuCode;
	private String shelfCode = "";
	private String factId;
	private TakeGoodsDialog shelfDialog;
	private SkuEditText shelfText;
	private RadioButton rb_shelf;
	private RadioButton rb_no_find;
	private TakeGoodsListAdapter takeGoodsListAdapter1;// 取新
	private TakeGoodsListAdapter takeGoodsListAdapter2;// 出样
	private boolean canRecevice;// 是否可以扫描
	private HotApplication ap;
	private int DELYED = 60 * 1000;
	private String notOutSKUcount;
	private String outedSKUcount;
	private PopupWindow popupWindow;
	private View pop_view;
	private String defult;
	private String tabString = "quxin";// 值为quxin 或者 chuyang
	public static boolean isRefreshList = false;
	private TakeGoodsEntity selectEntity;
	int notOutSKUCount;
	int outedSKUCount;
	private ArrayList<GroupEntity> groupList;
	String role;
	private Intent serviceIntent;

	@Override
	protected void onResume() {
		super.onResume();
		isScanSku = false;
		canRecevice = true;
		if (isRefreshList) {
			if (selectEntity != null) {
				if (tab == 1) {
					quNew.remove(selectEntity);
					takeGoodsListAdapter1.notifyDataSetChanged();
				} else if (tab == 2) {
					chuyang.remove(selectEntity);
					takeGoodsListAdapter2.notifyDataSetChanged();
				}
			}
			outedSKUCount++;
			notOutSKUCount--;
			String source = "今日已处理申请<font color='green'>" + outedSKUCount + "</font>条，待处理申请<font color='red'>" + notOutSKUCount + "</font>条。";
			text_tips.setText(Html.fromHtml(source));
			selectEntity = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		canRecevice = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		canRecevice = false;
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(runnable);// 停止定时器
		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);// must store the new intent unless getIntent() will
							// return the old one
		processExtraData();
	}

	private void processExtraData() {
		Intent intent = getIntent();
		Bundle bd = intent.getExtras();
		if (bd != null) {// 点击通知，查看是哪种类型的出货
			tabString = bd.getString("tab");
			resetBtn();
			if (tabString != null) {
				if (tabString.equals("quxin")) {
					tab = 1;
					text1.setBackgroundResource(R.drawable.tab_left_true);
					text1.setTextColor(getResources().getColor(R.color.color_white_text));
					text2.setTextColor(getResources().getColor(R.color.color_black_text));
					only_list.setAdapter(takeGoodsListAdapter1);
				} else {
					tab = 2;
					text2.setBackgroundResource(R.drawable.tab_right_true);
					text1.setTextColor(getResources().getColor(R.color.color_black_text));
					text2.setTextColor(getResources().getColor(R.color.color_white_text));
					only_list.setAdapter(takeGoodsListAdapter2);
				}
			} else {
				tab = 1;
				text1.setBackgroundResource(R.drawable.tab_left_true);
				text1.setTextColor(getResources().getColor(R.color.color_white_text));
				text2.setTextColor(getResources().getColor(R.color.color_black_text));
				only_list.setAdapter(takeGoodsListAdapter1);
			}
		}
	}

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

	protected void exInitView() {
		serviceIntent = new Intent(this, NoticeService.class);
		searchEdt.addTextChangedListener(textWatcher);
		searchEdt.setOnEditorActionListener(onEditorActionListener);
		role = MgrPerference.getInstance(this).getString(HotConstants.Unit.ROLE_CODE);
		groupList = (ArrayList<GroupEntity>) MgrPerference.getInstance(this).getObject(role);
		StringBuffer sb = new StringBuffer();
		sb.append("");
		for (GroupEntity group : groupList) {
			for (NoticeSettingEntity entity : group.getSublist()) {
				if (entity.getShaixuanId() != 0 && entity.getOpen()) {
					sb.append(entity.getShaixuanId());
					sb.append(",");
				}
			}
		}
		if (sb.toString().length() > 1)
			defult = sb.toString().substring(0, sb.toString().length() - 1);
		else
			defult = "";
		ap = (HotApplication) getApplication();
		stock_tab.setVisibility(View.VISIBLE);
		reset_postion_title.setOnLeftClickListner(this);
		reset_postion_title.setText("仓库出货");
		reset_postion_title.setVisibility(View.GONE);
		text1.setOnClickListener(this);
		text2.setOnClickListener(this);
		text1.setTextColor(getResources().getColor(R.color.color_white_text));
		text2.setTextColor(getResources().getColor(R.color.color_black_text));
		chu_tv.setOnClickListener(this);
		chu_Linear.setOnClickListener(this);
		text1.performClick();
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				if (role != null && role.equals("DZ"))
					Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStockList, WarehouseNet.checkGoodsList("1,2,3,4,5,6,7"), requestCallback);
				else {
					Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStockList, WarehouseNet.checkGoodsList(defult), requestCallback);
				}
			}
		}, 3);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStockList, WHAT_NET_GET_STOCK_LIST, NET_METHOD_POST, false);
		only_list.setOnItemClickListener(this);
		text_tips.setVisibility(View.VISIBLE);
		handler.postDelayed(runnable, DELYED);
		takeGoodsListAdapter1 = new TakeGoodsListAdapter(this, quNew);
		takeGoodsListAdapter2 = new TakeGoodsListAdapter(this, chuyang);
		only_list.setAdapter(takeGoodsListAdapter1);
		processExtraData();
		if (role != null && role.equals("DZ"))
			chu_tv.setVisibility(View.GONE);
		else
			chu_tv.setVisibility(View.VISIBLE);
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
				String stockStr = mGson.toJson(net.data);
				try {
					JSONObject js = new JSONObject(stockStr);
					notOutSKUcount = js.getString("NotOutSKUCount");
					outedSKUcount = js.getString("OutedSKUCount");
					String getList = js.getString("List");
					newskuTemp = mGson.fromJson(getList, new TypeToken<List<TakeGoodsEntity>>() {
					}.getType());
					handler.sendEmptyMessage(1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onError(int statusCode, ExException e) {
			handler.sendEmptyMessage(2);
		}
	};

	/**
	 * 在取新TAB搜索到匹配的factId
	 * 
	 * @param skuCode
	 * @param shelfCode
	 */
	private void searchQuValue(String skuCode, String shelfCode) {
		for (TakeGoodsEntity takeGooge : quNew) {
			if (skuCode.equals(takeGooge.SKUID)) {
				boolean isExist = false;
				for (int i = 0; i < takeGooge.CanOperShelfLocationCodes.length; i++) {
					if (shelfCode.equals(takeGooge.CanOperShelfLocationCodes[i])) {
						selectEntity = takeGooge;
						isExist = true;
						factId = takeGooge.FactID;
						break;
					}
				}
				if (isExist)
					break;
			}
		}
		if (factId == null) {
			Ex.Toast(mContext).showLong("没有找到匹配的任务");
		} else {
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStock, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
		}

	}

	/**
	 * 在出样TAB搜索到匹配的factId
	 * 
	 * @param skuCode
	 * @param shelfCode
	 */
	private void searchChuValue(String skuCode, String shelfCode) {
		for (TakeGoodsEntity takeGooge : chuyang) {
			if (skuCode.equals(takeGooge.SKUID) && (takeGooge.Qty.equals("1") || takeGooge.Qty.equals("0.5") || takeGooge.Qty.equals("0"))) {
				boolean isExist = false;
				for (int i = 0; i < takeGooge.CanOperShelfLocationCodes.length; i++) {
					if (shelfCode.equals(takeGooge.CanOperShelfLocationCodes[i])) {
						selectEntity = takeGooge;
						isExist = true;
						factId = takeGooge.FactID;
						break;
					}
				}
				if (isExist)
					break;
			}
		}
		if (factId == null) {
			Ex.Toast(mContext).showLong("没有找到匹配的任务");
		} else {
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStock, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
		}

	}

	/**
	 * 先扫描sku码,再扫描库位，完成一次提交
	 */
	@Override
	public void onReceiver(Intent intent) {
		// if(canRecevice){
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// String barcode = new String(code, 0, codelen).toUpperCase().trim();
		// dealBarCode(barcode);
		// }
	}

	@Override
	public void fillCode(String code) {
		dealBarCode(code);
	}

	private void dealBarCode(String barcode) {
		if (shelfDialog == null) {
			canRecevice = true;
		} else {
			if (!shelfDialog.isShowing()) {
				canRecevice = true;
				
			} else {
				canRecevice = false;
			}
		}
		if (canRecevice) {
			// 没有扫描sku码
			if (!isScanSku) {
				if (SkuUtil.isWarehouse(barcode)) {// 没有扫描sku码，缺先扫描了库位码，报错
					ap.getsoundPool(ap.Sound_error);
				} else {
					skuCode = barcode;
					ap.getsoundPool(ap.Sound_sku);
					// 如果列表中有对应sku，列表滚动到对应位置
					selectedItem();
					isScanSku = true;// 表示已经扫描sku码
				}
			} else {// 扫描了sku码
				if (!SkuUtil.isWarehouse(barcode)) {// 扫描sku码结束，如果接着扫描sku码
					skuCode = barcode;
					// 如果列表中有对应sku，列表滚动到对应位置
					selectedItem();
					ap.getsoundPool(ap.Sound_sku);
				} else {
					shelfCode = barcode;
					ap.getsoundPool(ap.Sound_location);
					// 在取新TAB扫描
					if (tab == 1) {
						searchQuValue(skuCode, shelfCode);
					} else if (tab == 2) {// 在出样TAB扫描
						searchChuValue(skuCode, shelfCode);
					}
					isScanSku = false;// 重置
				}
			}
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_STOCK_LIST:
			Ex.Toast(this).show("你的网速不太好，请稍后重试");
			isRefreshList = false;
			break;
		case WHAT_NET_GET_STOCK:
			factId = null;
			isScanSku = false;
			Ex.Toast(this).show("你的网速不太好，提交失败，请稍后重试");
			break;
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_STOCK_LIST:
			if (role != null && role.equals("DZ"))
				return WarehouseNet.checkGoodsList("1,2,3,4,5,6,7");
			else
				return WarehouseNet.checkGoodsList(defult);
		case WHAT_NET_GET_STOCK:
			return WarehouseNet.outStock(skuCode, shelfCode, factId);
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			if (what == WHAT_NET_GET_STOCK) {
				factId = null;
				isScanSku = false;
				skuCode = null;
				shelfCode = "";
			}
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_GET_STOCK_LIST:
			isRefreshList = false;
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			try {
				stockList.clear();
				JSONObject js = new JSONObject(stockStr);
				notOutSKUCount = Integer.valueOf(js.getString("NotOutSKUCount"));
				outedSKUCount = Integer.valueOf(js.getString("OutedSKUCount"));
				String getList = js.getString("List");
				List<TakeGoodsEntity> skuTemp = mGson.fromJson(getList, new TypeToken<List<TakeGoodsEntity>>() {
				}.getType());
				stockList.clear();
				stockList.addAll(skuTemp);
				String source = "今日已处理申请<font color='green'>" + outedSKUCount + "</font>条，待处理申请<font color='red'>" + notOutSKUCount + "</font>条。";
				text_tips.setText(Html.fromHtml(source));
				quNew.clear();
				chuyang.clear();
				for (int i = 0; i < stockList.size(); i++) {
					if (stockList.get(i).OpID.equals("1024") || stockList.get(i).OpID.equals("2048")) {
						quNew.add(stockList.get(i));
					} else {
						chuyang.add(stockList.get(i));
					}
				}
				// 填充搜索数据
				search_quNew.clear();
				search_quNew.addAll(quNew);
				search_chuyang.clear();
				search_chuyang.addAll(chuyang);
				if (tab == 1) {
					takeGoodsListAdapter1.notifyDataSetChanged();
				} else if (tab == 2) {
					takeGoodsListAdapter2.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case WHAT_NET_GET_STOCK:
			factId = null;
			isScanSku = false;
			skuCode = null;
			shelfCode = "";
			Ex.Toast(mContext).show(net.message);
			if (selectEntity != null) {
				if (tab == 1) {
					quNew.remove(selectEntity);
					search_quNew.remove(selectEntity);
					takeGoodsListAdapter1.notifyDataSetChanged();
				} else if (tab == 2) {
					chuyang.remove(selectEntity);
					search_chuyang.remove(selectEntity);
					takeGoodsListAdapter2.notifyDataSetChanged();
				}
			}
			outedSKUCount++;
			notOutSKUCount--;
			String source = "今日已处理申请<font color='green'>" + outedSKUCount + "</font>条，待处理申请<font color='red'>" + notOutSKUCount + "</font>条。";
			text_tips.setText(Html.fromHtml(source));
			selectEntity = null;
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.chu_Linear:
			this.finish();
			break;
		case R.id.rb_shelf:
			rb_shelf.setChecked(true);
			rb_no_find.setChecked(false);
			shelfText.setEnable(true);
			break;
		case R.id.rb_no_find:
			rb_shelf.setChecked(false);
			rb_no_find.setChecked(true);
			shelfText.setEnable(false);
			break;
		case R.id.take_goods_ok:
			if (rb_no_find.isChecked()) {
				shelfCode = "";
			}
			if (rb_shelf.isChecked()) {
				shelfCode = shelfText.getText(this);
				if (shelfCode == null || shelfCode.equals("")) {
					Ex.Toast(mActivity).show("请输入库位");
					return;
				}
				if (!shelfText.invalid()) {
					Ex.Toast(mActivity).show(getResources().getString(R.string.validat_sku));
					return;
				}
			}
			if (shelfDialog != null) {
				shelfDialog.dismiss();
			}
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStock, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
			break;
		case R.id.text1:
			tab = 1;
			resetBtn();
			text1.setBackgroundResource(R.drawable.tab_left_true);
			text1.setTextColor(getResources().getColor(R.color.color_white_text));
			text2.setTextColor(getResources().getColor(R.color.color_black_text));
			only_list.setAdapter(takeGoodsListAdapter1);
			break;
		case R.id.text2:
			tab = 2;
			resetBtn();
			text2.setBackgroundResource(R.drawable.tab_right_true);
			text1.setTextColor(getResources().getColor(R.color.color_black_text));
			text2.setTextColor(getResources().getColor(R.color.color_white_text));
			only_list.setAdapter(takeGoodsListAdapter2);
			break;
		case R.id.chu_tv:
			// showPopUp();
			new SelectCustomDialog(this, groupList, new OnCustomDialogListener() {

				@Override
				public void change(int buttonId, boolean isChecked) {
					changeNotice(buttonId, isChecked);
				}

				@Override
				public void confirm() {
					StringBuffer sb = new StringBuffer();
					sb.append("");
					for (GroupEntity group : groupList) {
						for (NoticeSettingEntity entity : group.getSublist()) {
							if (entity.getShaixuanId() != 0 && entity.getOpen()) {
								sb.append(entity.getShaixuanId());
								sb.append(",");
							}
						}
					}
					if (sb.toString().length() > 1)
						defult = sb.toString().substring(0, sb.toString().length() - 1);
					else
						defult = "";
					MgrPerference.getInstance(TakeGoodsActivity.this).putObject(role, groupList);
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.OutStockList, WHAT_NET_GET_STOCK_LIST, NET_METHOD_POST, false);
					startService(serviceIntent);
				}
			}).show();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (tab == 1) {
			selectEntity = quNew.get(position);
			shelfDialog = new TakeGoodsDialog(mActivity, R.layout.show_shelf_dialog, R.style.Theme_dialog);
			shelfDialog.show();
			shelfText = (SkuEditText) shelfDialog.findViewById(R.id.the_customer_shelf);
			TextView take_goods_ok = (TextView) shelfDialog.findViewById(R.id.take_goods_ok);
			rb_shelf = (RadioButton) shelfDialog.findViewById(R.id.rb_shelf);
			rb_no_find = (RadioButton) shelfDialog.findViewById(R.id.rb_no_find);
			take_goods_ok.setOnClickListener(this);
			rb_shelf.setOnClickListener(this);
			rb_no_find.setOnClickListener(this);
			skuCode = quNew.get(position).SKUID;
			factId = quNew.get(position).FactID;
		} else if (tab == 2) {
			selectEntity = chuyang.get(position);
			if (chuyang.get(position).Qty.equals("1") || chuyang.get(position).Qty.equals("0.5") || chuyang.get(position).Qty.equals("0")) {
				skuCode = chuyang.get(position).SKUID;
				factId = chuyang.get(position).FactID;
				shelfDialog = new TakeGoodsDialog(mActivity, R.layout.show_shelf_dialog, R.style.Theme_dialog);
				shelfDialog.show();
				shelfText = (SkuEditText) shelfDialog.findViewById(R.id.the_customer_shelf);
				TextView take_goods_ok = (TextView) shelfDialog.findViewById(R.id.take_goods_ok);
				rb_shelf = (RadioButton) shelfDialog.findViewById(R.id.rb_shelf);
				rb_no_find = (RadioButton) shelfDialog.findViewById(R.id.rb_no_find);
				take_goods_ok.setOnClickListener(this);
				rb_shelf.setOnClickListener(this);
				rb_no_find.setOnClickListener(this);
			} else {
				Intent intent = new Intent(this, TakeGoodsBatchActivity.class);
				Bundle bd = new Bundle();
				bd.putString("skuCode", chuyang.get(position).SKUID);
				bd.putString("factId", chuyang.get(position).FactID);
				if (selectEntity.OpID.equals(HotConstants.SKU.OPT_ID_HY))
					bd.putBoolean("isChangeSample", true);
				bd.putInt("count", Integer.valueOf(chuyang.get(position).Qty));
				intent.putExtras(bd);
				startActivity(intent);
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				Ex.Toast(mContext).showLong("刷新成功");
				stockList.clear();
				stockList.addAll(newskuTemp);
				String source = "今日已处理申请<font color='green'>" + outedSKUcount + "</font>条，待处理申请<font color='red'>" + notOutSKUcount + "</font>条。";
				text_tips.setText(Html.fromHtml(source));
				quNew.clear();
				chuyang.clear();
				for (int i = 0; i < stockList.size(); i++) {
					if (stockList.get(i).OpID.equals("1024") || stockList.get(i).OpID.equals("2048")) {
						quNew.add(stockList.get(i));
					} else {
						chuyang.add(stockList.get(i));
					}
				}
				if (tab == 1) {
					takeGoodsListAdapter1.notifyDataSetChanged();
				} else if (tab == 2) {
					takeGoodsListAdapter2.notifyDataSetChanged();
				}
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
				if (takeGoodsListAdapter1 != null) {
					if (quNew != null && quNew.size() > 0) {
						for (TakeGoodsEntity en : quNew) {
							en.TimeOut = String.valueOf(Long.valueOf(en.TimeOut) + 60);
						}
						takeGoodsListAdapter1.notifyDataSetChanged();
					}
				}
				if (takeGoodsListAdapter2 != null) {
					if (chuyang != null && chuyang.size() > 0) {
						for (TakeGoodsEntity en : chuyang) {
							en.TimeOut = String.valueOf(Long.valueOf(en.TimeOut) + 60);
						}
						takeGoodsListAdapter2.notifyDataSetChanged();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("exception...");
			}
		}
	};

	void resetBtn() {
		text1.setBackgroundResource(R.drawable.tab_left);
		text2.setBackgroundResource(R.drawable.tab_right);

	}

	void showPopUp() {
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		int wid = windowManager.getDefaultDisplay().getWidth();
		int hig = windowManager.getDefaultDisplay().getHeight();
		if (popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			pop_view = layoutInflater.inflate(R.layout.popwindow, null);
			cb1 = (CheckBox) pop_view.findViewById(R.id.cb1);
			cb2 = (CheckBox) pop_view.findViewById(R.id.cb2);
			cb3 = (CheckBox) pop_view.findViewById(R.id.cb3);
			cb4 = (CheckBox) pop_view.findViewById(R.id.cb4);
			cb5 = (CheckBox) pop_view.findViewById(R.id.cb5);
			cb6 = (CheckBox) pop_view.findViewById(R.id.cb6);
			cb7 = (CheckBox) pop_view.findViewById(R.id.cb7);
			popupWindow = new PopupWindow(pop_view, wid, LayoutParams.WRAP_CONTENT, true);
		}
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.common_img_mask));
		popupWindow.showAsDropDown(stock_tab, wid / 2, 0);
		// popupWindow.showAtLocation(stock_tab, Gravity.CENTER, wid, hig);
		// value();
		checkBoxLintener();
	}

	void checkBoxLintener() {
		cb1.setOnCheckedChangeListener(changeListener);
		cb2.setOnCheckedChangeListener(changeListener);
		cb3.setOnCheckedChangeListener(changeListener);
		cb4.setOnCheckedChangeListener(changeListener);
		cb5.setOnCheckedChangeListener(changeListener);
		cb6.setOnCheckedChangeListener(changeListener);
		cb7.setOnCheckedChangeListener(changeListener);
	}

	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

		}

	};

	private void changeNotice(int buttonId, boolean isChecked) {
		switch (buttonId) {
		case R.id.cb1:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.1")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;
		case R.id.cb2:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.2")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;
		case R.id.cb3:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.3")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;
		case R.id.cb4:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.4")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;
		case R.id.cb5:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.5")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;
		case R.id.cb6:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.6")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;
		case R.id.cb7:
			for (GroupEntity group : groupList) {
				if (group.getId() == 2) {
					for (int i = 0; i < group.getSublist().size(); i++) {
						if (group.getSublist().get(i).getKey().equals("outgoods.7")) {
							group.getSublist().get(i).setOpen(isChecked);
							break;
						}
					}
					break;
				}
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 搜索框文字监听器
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!s.equals("")) {

				if (tab == 1) {// 取新
					quNew.clear();
					for (int i = 0; i < search_quNew.size(); i++) {
						if (search_quNew.get(i).SKUID.contains(s.toString().toUpperCase())) {
							quNew.add(search_quNew.get(i));
						}
					}
					takeGoodsListAdapter1.notifyDataSetChanged();
				} else if (tab == 2) {// 出样
					chuyang.clear();
					for (int i = 0; i < search_chuyang.size(); i++) {
						if (search_chuyang.get(i).SKUID.contains(s.toString().toUpperCase())) {
							chuyang.add(search_chuyang.get(i));
						}
					}
					takeGoodsListAdapter2.notifyDataSetChanged();
				}
			} else {
				if (tab == 1) {// 取新
					quNew.clear();
					quNew.addAll(search_quNew);
					takeGoodsListAdapter1.notifyDataSetChanged();
				} else if (tab == 2) {// 出样
					chuyang.clear();
					chuyang.addAll(search_chuyang);
					takeGoodsListAdapter2.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};
	OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				// 先隐藏键盘
				hideSoftKeyBoard(mActivity, searchEdt);
			}
			return true;
		}
	};

	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * 列表选中项滚动到可见位置
	 */
	private void selectedItem() {
		if (tab == 1) {// 取新
			for (int i = 0; i < quNew.size(); i++) {
				if (skuCode.equals(quNew.get(i).SKUID)) {
					takeGoodsListAdapter1.setSelectItem(i);
					only_list.smoothScrollToPosition(i);
				}
			}

		} else if (tab == 2) {// 出样
			for (int i = 0; i < chuyang.size(); i++) {
				if (skuCode.equals(chuyang.get(i).SKUID)) {
					takeGoodsListAdapter2.setSelectItem(i);
					only_list.smoothScrollToPosition(i);
				}
			}
		}
	}

	
}
