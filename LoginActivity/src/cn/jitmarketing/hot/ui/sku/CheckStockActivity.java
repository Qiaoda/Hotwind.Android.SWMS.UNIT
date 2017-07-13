package cn.jitmarketing.hot.ui.sku;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ItemListAdapter;
import cn.jitmarketing.hot.adapter.StockListAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SampleEntity;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.FastDoubleClickUtil;
import cn.jitmarketing.hot.util.ImageUtil;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 库存查询
 */
public class CheckStockActivity extends BaseSwipeOperationActivity implements OnClickListener, OnItemClickListener {
	/**
     *
     */
	@ViewInject(R.id.frame_layout)
	FrameLayout frame_layout;
	/**
	 * sku整体布局
	 */
	@ViewInject(R.id.check_layout)
	LinearLayout check_layout;
	/**
	 * sku搜索页面布局
	 */
	@ViewInject(R.id.check_list_layout)
	LinearLayout check_list_layout;
	/**
	 * title控件
	 */
	@ViewInject(R.id.check_stock_title)
	TitleWidget check_stock_title;
	/**
	 * sku搜索点击框
	 */
	@ViewInject(R.id.check_stock_search_layout)
	LinearLayout checkSearchLayout;
	/**
	 * sku详细信息布局
	 */
	@ViewInject(R.id.layout_content)
	LinearLayout layout_content;
	/**
	 * sku列表
	 */
	@ViewInject(R.id.list_sku)
	ListView list_sku;
	/**
	 * 商品编码
	 */
	@ViewInject(R.id.stock_sku_code)
	TextView stock_sku_code;
	/**
	 * 商品名称
	 */
	@ViewInject(R.id.stock_sku_name)
	TextView stock_sku_name;
	/**
	 * 商品原价格
	 */
	@ViewInject(R.id.stock_sku_price)
	TextView stock_sku_price;
	/**
	 * 商品优惠价格
	 */
	@ViewInject(R.id.stock_sku_change_price)
	TextView stock_sku_change_price;
	/**
	 * 商品数量
	 */
	@ViewInject(R.id.stock_sku_num)
	TextView stock_sku_num;
	/**
	 * 库存
	 */
	@ViewInject(R.id.stock_sku_stock)
	TextView stock_sku_stock;
	/**
	 * 商品图标
	 */
	@ViewInject(R.id.stock_sku_icon)
	ImageView stock_sku_icon;

	/**
	 * 取新
	 */
	@ViewInject(R.id.get_new)
	TextView get_new;
	/**
	 * 另一只
	 */
	@ViewInject(R.id.get_the_other)
	TextView get_the_other;
	/**
	 * 出样
	 */
	@ViewInject(R.id.get_the_sample)
	TextView get_the_sample;

	/**
	 * sku搜索列表
	 */
	@ViewInject(R.id.search_list)
	ListView searchListView;
	/**
	 * sku搜索输入框
	 */
	@ViewInject(R.id.check_stock_search_edt)
	ClearEditText searchEditText;
	/**
	 * sku搜索取消按钮
	 */
	@ViewInject(R.id.check_storck_cancel)
	TextView storckCancelButton;

	private static final int WHAT_NET_GET_STOCK = 0x10;
	private static final int WHAT_NET_GET_NEW = 0x11;
	private static final int WHAT_NET_GET_STOCK_FUZZY = 0x13;
	private static final int WHAT_NET_GET_STOCK1 = 0x14;
	private String skuCodeStr = "";
	private StockListAdapter stockListAdapter;
	private ArrayList<SkuEntity> skulist = new ArrayList<SkuEntity>();
	private SkuEntity selectedSku;
	private ItemEntity itemEntity;
	// private String skuInfo;
	private SearchSkuBean searchSkuBean;// sku搜索结果对象
	private ItemListAdapter itemListAdapter;
	private List<SearchSkuBean> searchSkuList = new ArrayList<SearchSkuBean>();
	private String searchCondition;

	private boolean showSearch;
	HotApplication ap;
	public static boolean sampleSuccess;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (showSearch) {
				hideSearchView();// 隐藏搜索框
			} else {
				finish();
			}
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
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
		return R.layout.activity_check_stock;
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		get_new.setOnClickListener(this);
		get_the_other.setOnClickListener(this);
		get_the_sample.setOnClickListener(this);
		check_stock_title.setOnLeftClickListner(this);
		check_stock_title.setOnRightClickListner(this);
		stock_sku_stock.setOnClickListener(this);
		checkSearchLayout.setOnClickListener(this);
		searchEditText.addTextChangedListener(textWatcher);
		storckCancelButton.setOnClickListener(this);
		searchListView.setOnItemClickListener(this);
		searchEditText.setOnKeyListener(onKeyListener);
		hideSoftKeyBoard(this, searchEditText);
		check_list_layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		// 从扫描动作进入的（主界面扫描直接跳转）
		String skuCode = getIntent().getStringExtra("skuCode");
		if (skuCode != null) {
			skuCodeStr = skuCode;
			stock_sku_code.setText(skuCode);
			/* 根据skuCode查询商品信息 */
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
			layout_content.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 出样，换样，撤样成功后，返回该界面刷新
		// if(sampleSuccess) {
		// startTask(HotConstants.Global.APP_URL_USER
		// + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK,
		// NET_METHOD_POST, false);
		// sampleSuccess = false;
		// }
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_STOCK:// 获取商品信息
			return SkuNet.getStoreForSKU(skuCodeStr);
		case WHAT_NET_GET_STOCK1:
			return SkuNet.getStoreForSKU(skuCodeStr);
		case WHAT_NET_GET_NEW:// 取新
			return SkuNet.getNew(skuCodeStr);
		case WHAT_NET_GET_STOCK_FUZZY:// 搜索提示
			return SkuNet.getStoreForSKUFuzzy(searchCondition);
		}
		return null;
	}

	@Override
	public void onReceiver(Intent intent) {
		// byte[] barcode = intent.getByteArrayExtra("barocode");
		// int barocodelen = intent.getIntExtra("length", 0);
		// if(barcode != null) {
		// skuCodeStr = new String(barcode, 0,
		// barocodelen).toUpperCase().trim();
		// dealBarCode(skuCodeStr);
		// } else {
		// ap.getsoundPool(ap.Sound_error);
		// return;
		// }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void dealBarCode(String skuCodeStr) {
		if (null != popupWindow) {
			popupWindow.dismiss();
			popupWindow = null;
		}
		if (!showSearch) {
			if (!SkuUtil.isWarehouse(skuCodeStr)) {
				ap.getsoundPool(ap.Sound_sku);
				stock_sku_code.setText(skuCodeStr);
				/* 获取商品信息 */
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
			} else {
				ap.getsoundPool(ap.Sound_location);
			}
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		sampleSuccess = false;
		Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		sampleSuccess = false;
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_GET_STOCK1:
		case WHAT_NET_GET_STOCK:// 获取商品信息
			if (null == net.data) {
				Ex.Toast(mActivity).show(net.message);
				layout_content.setVisibility(View.GONE);
				return;
			} else {
				layout_content.setVisibility(View.VISIBLE);
			}
			try {
				String skulistStr = new JSONObject(mGson.toJson(net.data)).getString("skus");
				String itemStr = new JSONObject(mGson.toJson(net.data)).getString("item");
				itemEntity = mGson.fromJson(itemStr, ItemEntity.class);
				skulist.clear();
				skulist.addAll((ArrayList<SkuEntity>) mGson.fromJson(skulistStr, new TypeToken<List<SkuEntity>>() {
				}.getType()));
				/* 在列表中找出当前商品并选中 */
				HotConstants.SELECTED = 0;
				selectedSku = skulist.get(0);
				for (int i = 0; i < skulist.size(); i++) {
					if (skuCodeStr.equals(skulist.get(i).SKUCode)) {
						HotConstants.SELECTED = i;
						selectedSku = skulist.get(i);
					}
				}
				/* 显示商品信息 */
				setSkuData(itemEntity);
				/* 为取新存值 */
				setValue();
				String url = null;
				if (selectedSku.ColorID == null || selectedSku.ColorID.equals("")) {
					url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + selectedSku.SKUCode + ".jpg";
				} else {
					url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + itemEntity.ItemID + "$" + selectedSku.ColorID + ".jpg";
				}
				/* 显示图片 */
				ImageUtil.uploadImage(this, url, stock_sku_icon);

				if (showSearch) {
					hideSearchView();
					layout_content.setVisibility(View.VISIBLE);
				}
				// 判断是否启用小商品版
				if (HotConstants.Global.isStartXSPVersion) {
					if (itemEntity.AttDId.equals("3")) {// 小商品
						get_new.setEnabled(false);
						get_new.setClickable(false);
						get_the_sample.setEnabled(false);
						get_the_sample.setClickable(false);
					} else {
						get_new.setEnabled(true);
						get_new.setClickable(true);
						get_the_sample.setEnabled(true);
						get_the_sample.setClickable(true);
					}
				}
				get_the_sample.setVisibility(View.GONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case WHAT_NET_GET_NEW:// 取新
			// 日志打印
			LogUtils.logOnFile("->取新成功");
		case WHAT_NET_GET_STOCK_FUZZY:
			if (null == net.data) {
				Ex.Toast(mActivity).show(net.message);
				return;
			}
			String itemStr = mGson.toJson(net.data);
			searchSkuList.clear();
			searchSkuList.addAll((List<SearchSkuBean>) mGson.fromJson(itemStr, new TypeToken<List<SearchSkuBean>>() {
			}.getType()));
			if (itemListAdapter == null) {
				itemListAdapter = new ItemListAdapter(this, searchSkuList);
				searchListView.setAdapter(itemListAdapter);
			} else {
				itemListAdapter.notifyDataSetChanged();
			}
			check_list_layout.setBackgroundColor(getResources().getColor(R.color.color_bg));
		}
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			if (s != null && !s.toString().trim().equals("")) {
				searchCondition = s.toString();
				/* 搜素提示请求 */
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKUFuzzy, WHAT_NET_GET_STOCK_FUZZY, false, NET_METHOD_POST, false);
			} else {
				if (searchSkuList != null && itemListAdapter != null) {
					searchSkuList.clear();
					itemListAdapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
		}
	};

	/**
	 * 把获取到的商品信息数据 赋值给控件
	 * 
	 * @param itemEntity
	 */
	private void setSkuData(ItemEntity itemEntity) {
		stock_sku_name.setText(itemEntity.ItemName);
		// ImageUtil.getBitmapByBase64(itemEntity.ItemImage, stock_sku_icon);
		if (stockListAdapter == null) {
			stockListAdapter = new StockListAdapter(this, skulist);
			list_sku.setAdapter(stockListAdapter);
			list_sku.setOnItemClickListener(this);
		} else {
			stockListAdapter.notifyDataSetChanged();
		}
		list_sku.setSelection(HotConstants.SELECTED);
		if (itemEntity.IsSomeSampling) {// 鞋子
			get_the_other.setVisibility(View.VISIBLE);
		} else {
			get_the_other.setVisibility(View.GONE);
		}

		showPrice(selectedSku);
	}

	// 显示当前商品价格
	private void showPrice(SkuEntity selectedSku) {
		stock_sku_price.setText("￥" + SkuUtil.formatPrice(selectedSku.Price)); 
		if (selectedSku.ChangePrice != null) {
			if (!selectedSku.ChangePrice.equals(selectedSku.Price)) {
				stock_sku_change_price.setText(SkuUtil.formatPrice(selectedSku.ChangePrice));
				stock_sku_price.setTextColor(getResources().getColor(R.color.color_gray_text));
				stock_sku_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
				stock_sku_price.setTextSize(12);
			} else {
				stock_sku_change_price.setText("");
				stock_sku_price.setTextSize(14);
				stock_sku_price.setTextColor(getResources().getColor(R.color.color_green));
				stock_sku_price.getPaint().setFlags(0); // 取消设置的的划线
			}
		} else {
			stock_sku_change_price.setText("");
			stock_sku_price.setTextSize(14);
			stock_sku_price.setTextColor(getResources().getColor(R.color.color_green));
			stock_sku_price.getPaint().setFlags(0); // 取消设置的的划线
		}
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
		if (listview == list_sku) {
			HotConstants.SELECTED = position;
			stockListAdapter.notifyDataSetChanged();
			selectedSku = skulist.get(position);
			showPrice(selectedSku);
			setValue();
			String url = null;
			if (selectedSku.ColorID == null || selectedSku.ColorID.equals("")) {
				url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + selectedSku.SKUCode + ".jpg";
			} else {
				url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + itemEntity.ItemID + "$" + selectedSku.ColorID + ".jpg";
			}
			ImageUtil.uploadImage(this, url, stock_sku_icon);
		} else if (listview == searchListView) {
			searchSkuBean = searchSkuList.get(position);
			skuCodeStr = searchSkuBean.SKUCode;
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
		}
	}

	private void setValue() {
		stock_sku_code.setText(selectedSku.SKUCode);
		stock_sku_num.setText(selectedSku.SKUCount);
		stock_sku_stock.setText(selectedSku.StockSKUShelfLocationString);
		skuCodeStr = selectedSku.SKUCode;
		// skuInfo = "商品编码:  " + selectedSku.SKUCode + "\n\r" + "颜色:  " +
		// selectedSku.ColorName + "\n\r"
		// + "尺寸:  " + selectedSku.SizeName;
		if (!itemEntity.AttDId.equals("3")) {
			if (selectedSku.IsHasSample) {// 取另一只
				get_the_other.setEnabled(true);
				get_the_other.setClickable(true);
			} else {
				get_the_other.setEnabled(false);
				get_the_other.setClickable(false);
			}
			if (selectedSku.SKUCount.equals("0")) {
				get_new.setEnabled(false);
				get_new.setClickable(false);
				get_the_sample.setEnabled(false);
				get_the_sample.setClickable(false);
			} else {
				get_new.setEnabled(true);
				get_new.setClickable(true);
				get_the_sample.setEnabled(true);
				get_the_sample.setClickable(true);
			}
		}
	}

	/**
	 * 显示搜索界面
	 */
	private void showSearchView() {
		showSearch = true;
		// check_list_layout.setBackgroundResource(R.drawable.common_img_mask);
		check_list_layout.setVisibility(View.VISIBLE);
		rotateyAnimRun(checkSearchLayout, 1f, 0f, 500);// 从有到无
		verticalRun(checkSearchLayout, 0, -check_stock_title.getHeight(), 500);// 往上位移
		popSoftKeyBoard(this, searchEditText);
		searchEditText.requestFocus();
	}

	/**
	 * 隐藏搜索界面
	 */
	private void hideSearchView() {
		showSearch = false;
		check_list_layout.setVisibility(View.GONE);
		rotateyAnimRun(checkSearchLayout, 0f, 1f, 500);// 从无到有
		verticalRun(checkSearchLayout, -check_stock_title.getHeight(), 0, 500);// 往下位移
		hideSoftKeyBoard(this, searchEditText);
	}

	/**
	 * 点击出样按钮显示的列表
	 */
	private ArrayList<SampleEntity> getSampleList(ItemEntity item, SkuEntity sku, boolean isGetOther) {
		ArrayList<SampleEntity> arraylist = new ArrayList<SampleEntity>();
		if (isGetOther) {// 取另一只
			if (sku.SampleList == null)
				return arraylist;
			for (SampleEntity sam : sku.SampleList) {
				SampleEntity three = sam.copy(sam);
				three.isCanBatch = false;
				if (sam.EndQty.equals("1"))
					three.Label = "空鞋盒";
				else
					three.Label = "鞋盒(一只)";
				three.Type = "1";
				arraylist.add(three);
			}
			return arraylist;
		} else {// 出样，撤样，换样
			if (item.IsSomeSampling) {// 是鞋子
				SampleEntity one = new SampleEntity();
				one.isCanBatch = false;
				one.Label = "出样:  一双";
				one.EndQty = "1";
				one.Type = "2";
				SampleEntity two = new SampleEntity();
				two.isCanBatch = false;
				two.Label = "出样:  一只";
				two.EndQty = "0.5";
				two.Type = "2";
				arraylist.add(one);
				arraylist.add(two);
				if (sku.IsHasSample) {// 出样
					if (sku.SampleList.size() == 1) {
						for (SampleEntity sam : sku.SampleList) {
							SampleEntity three = sam.copy(sam);
							SampleEntity four = sam.copy(sam);
							three.isCanBatch = false;
							three.Label = "换样:  " + sam.Label;
							three.Type = "3";
							four.isCanBatch = false;
							four.Label = "撤样:  " + sam.Label;
							four.Type = "4";
							arraylist.add(three);
							arraylist.add(four);
						}
					} else {
						SampleEntity three = sku.SampleList.get(0).copy(sku.SampleList.get(0));
						SampleEntity four = sku.SampleList.get(1).copy(sku.SampleList.get(1));
						three.isCanBatch = false;
						three.Label = "换样:  " + sku.SampleList.get(0).Label;
						three.Type = "3";
						four.isCanBatch = false;
						four.Label = "换样:  " + sku.SampleList.get(1).Label;
						four.Type = "3";
						arraylist.add(three);
						arraylist.add(four);
						SampleEntity five = sku.SampleList.get(0).copy(sku.SampleList.get(0));
						SampleEntity six = sku.SampleList.get(1).copy(sku.SampleList.get(1));
						five.isCanBatch = false;
						five.Label = "撤样:  " + sku.SampleList.get(0).Label;
						five.Type = "4";
						six.isCanBatch = false;
						six.Label = "撤样:  " + sku.SampleList.get(1).Label;
						six.Type = "4";
						arraylist.add(five);
						arraylist.add(six);
					}
				}
			} else {
				SampleEntity one = new SampleEntity();
				one.isCanBatch = true;
				// one.Label = "出样:  一" + item.ItemUnit;
				one.Label = "出样";
				one.EndQty = "1";
				one.Type = "2";
				arraylist.add(one);
				if (sku.IsHasSample) {// 出样
					if (sku.SampleList.size() > 0) {
						for (SampleEntity sam : sku.SampleList) {
							SampleEntity three = sam.copy(sam);
							SampleEntity four = sam.copy(sam);
							three.isCanBatch = false;
							// three.Label = "换样:  " + sam.Label;
							three.Label = "换样";
							three.Type = "3";
							four.isCanBatch = true;
							// four.Label = "撤样:  " + sam.Label;
							four.Label = "撤样";
							four.Type = "4";
							arraylist.add(three);
							arraylist.add(four);
						}
					}
				}
			}
		}
		return arraylist;
	}

	@Override
	public void onClick(View v) {
		if (FastDoubleClickUtil.isFastDoubleClick()) {
			Log.i("fast", "fast");
			return;
		}
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		case R.id.lv_right:
			// 日志操作
			LogUtils.logOnFile("搜索");
			showSearchView();
			break;
		case R.id.get_new:
			// 取新
			// 日志操作
			LogUtils.logOnFile("->取新" + skuCodeStr);
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetNew, WHAT_NET_GET_NEW, NET_METHOD_POST, false);
			break;
		case R.id.get_the_other:
			// 取鞋盒
			// 日志操作
			LogUtils.logOnFile("->取鞋盒" + skuCodeStr);
			Bundle bundle = new Bundle();
			bundle.putString("skuCodeStr", skuCodeStr);
			bundle.putSerializable("sku", selectedSku);
			bundle.putSerializable("sampleList", getSampleList(itemEntity, selectedSku, true));
			Intent intent = new Intent(CheckStockActivity.this, SomeSampleDialog.class);
			intent.putExtra("bundle", bundle);
			startActivity(intent);
			break;
		case R.id.get_the_sample:
			// 出样
			if (selectedSku == null) {
				Ex.Toast(mActivity).show("此商品暂无库存");
				return;
			}
			if (selectedSku.SKUCount.equals("0")) {
				Ex.Toast(mActivity).show("此商品暂无库存");
				return;
			}
			Bundle bundle1 = new Bundle();
			bundle1.putString("skuCodeStr", skuCodeStr);
			// bundle1.putString("skuInfo", skuInfo);
			bundle1.putSerializable("sku", selectedSku);
			bundle1.putSerializable("sampleList", getSampleList(itemEntity, selectedSku, false));
			Intent intent1 = new Intent(CheckStockActivity.this, SomeSampleDialog.class);
			intent1.putExtra("bundle", bundle1);
			startActivity(intent1);
			break;
		case R.id.check_stock_search_layout:
			showSearchView();
			break;
		case R.id.check_storck_cancel:
			hideSearchView();
			break;
		case R.id.stock_sku_stock:
			if (selectedSku.StockSKUShelfLocationString.length() > 1) {
				getPopupWindow();
				// 这里是位置显示方式,在屏幕的左侧
				popupWindow.showAtLocation(v, Gravity.CENTER, 0, -6);
			}
			break;
		}

	}

	PopupWindow popupWindow;

	/**
	 * 创建PopupWindow
	 */
	protected void initPopuptWindow() {
		// 获取自定义布局文件activity_popupwindow_left.xml的视图
		View popupWindow_view = getLayoutInflater().inflate(R.layout.activity_popupwindow_left, null, false);
		TextView txt = (TextView) popupWindow_view.findViewById(R.id.pop_txt);
		txt.setText(selectedSku.StockSKUShelfLocationString);
		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view, ConstValue.SCREEN_WIDTH - 24, LayoutParams.WRAP_CONTENT, true);
		// 设置动画效果
		popupWindow.setAnimationStyle(R.style.AnimationFade);
		// 点击其他地方消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupWindow != null && popupWindow.isShowing()) {
					popupWindow.dismiss();
					popupWindow = null;
				}
				return false;
			}
		});
	}

	/***
	 * 获取PopupWindow实例
	 */
	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopuptWindow();
		}
	}

	OnKeyListener onKeyListener = new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {// 修改回车键功能
				if (searchEditText.getText() == null || searchEditText.getText().toString().equals("")) {
					return false;
				}
				searchCondition = searchEditText.getText().toString();
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKUFuzzy, WHAT_NET_GET_STOCK_FUZZY, false, NET_METHOD_POST, false);
			}
			return false;
		}
	};

	/**
	 * 缩放动画
	 * 
	 * @param view
	 * @param start
	 * @param end
	 * @param duration
	 */
	public void rotateyAnimRun(final View view, float start, float end, long duration) {
		ObjectAnimator anim = ObjectAnimator//
				.ofFloat(view, "zhy", start, end)//
				.setDuration(duration);//
		anim.start();
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float cVal = (Float) animation.getAnimatedValue();
				view.setAlpha(cVal);
				view.setScaleX(cVal);
				view.setScaleY(cVal);
			}
		});
	}

	/**
	 * 位移动画
	 * 
	 * @param view
	 * @param start
	 * @param end
	 * @param duration
	 */
	public void verticalRun(View view, float start, float end, long duration) {
		ValueAnimator animator = ValueAnimator.ofFloat(start, end);
		animator.setTarget(frame_layout);
		animator.setDuration(duration).start();
		animator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				frame_layout.setTranslationY((Float) animation.getAnimatedValue());
			}
		});
	}

	/**
	 * 弹出软键盘
	 * 
	 * @param context
	 * @param view
	 */
	public void popSoftKeyBoard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	public void fillCode(String code) {
		this.skuCodeStr = code;
		if (code != null) {
			dealBarCode(code);
			// 日志操作
			LogUtils.logOnFile("扫码->" + code);
		} else {
			ap.getsoundPool(ap.Sound_error);
			return;
		}
	}
}
