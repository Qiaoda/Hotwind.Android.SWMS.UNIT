package cn.jitmarketing.hot.pandian;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingDetailAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingDetailEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.AutoListView;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.PandianCustomDialog;
import cn.jitmarketing.hot.view.TitleWidget;
import cn.jitmarketing.hot.view.AutoListView.OnLoadListener;
import cn.jitmarketing.hot.view.AutoListView.OnRefreshListener;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingShopperDetailActivity extends BaseSwipeOperationActivity implements
		OnClickListener,  OnRefreshListener, OnLoadListener {

	private static final int WHAT_NET_SHELF_LIST = 0x10;
	private static final int WHAT_NET_PANDIAN_MODIFY = 0x11;
	private static final int WHAT_NET_PANDIAN_SEARCH = 0x12;
	
	@ViewInject(R.id.stock_taking_task_title)
	TitleWidget detail_title;
	@ViewInject(R.id.detail_list)
	AutoListView only_list;
	@ViewInject(R.id.pandian_edt)
	ClearEditText pandian_edt;
	@ViewInject(R.id.search_list)
	ListView search_list;
	@ViewInject(R.id.diffrence_count_txt)
	TextView diffrence_count_txt;
	@ViewInject(R.id.diffrence_money_txt)
	TextView diffrence_money_txt;
	
	private HotApplication ap;
	private String ShelfLocationCode;
	private int pageIndex = 0;
	private final int pageSize = 20;
	private List<StockTakingDetailEntity> searchList = new ArrayList<StockTakingDetailEntity>();
	private StockTakingDetailAdapter searchAdapter;
	private List<StockTakingDetailEntity> detailList = new ArrayList<StockTakingDetailEntity>();
	private List<StockTakingDetailEntity> newList = new ArrayList<StockTakingDetailEntity>();
	private StockTakingDetailAdapter detailAdapter;
	private StockTakingDetailEntity modifyEntity;
	private boolean isRefresh;
	private String SortName = "金额";//排序名称
	private String SortType = "desc";//排序类型
	private boolean moneySortDesc = true;
	private boolean countSortDesc = true;
	
	private String searchSku;
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_stock_taking_shopper_detail;
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		detail_title.setText("库位详情");
		detail_title.setOnLeftClickListner(this);
		detail_title.setOnRightClickListner(this);
		diffrence_money_txt.setOnClickListener(this);
		diffrence_count_txt.setOnClickListener(this);
		only_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, final long id) {
				if(id == -1) {
					return false;
				}
				new PandianCustomDialog(StockTakingShopperDetailActivity.this, detailList.get((int)id).PDSKUCount, detailList.get((int)id).SKUID, new PandianCustomDialog.OnCustomDialogListener() {
					
					@Override
					public void confirm(int num) {
						modifyEntity = detailList.get((int)id);
						modifyEntity.PDSKUCount = num;
						modifyEntity.Status = 1;
						List<StockTakingDetailEntity> list = new ArrayList<StockTakingDetailEntity>();
						list.add(modifyEntity);
						startJsonTask(HotConstants.Global.APP_URL_USER
								+ HotConstants.Stock.MODIFY_SHELF, WHAT_NET_PANDIAN_MODIFY, true,
								NET_METHOD_POST, SaveListUtil.updatePandian(list, ShelfLocationCode), false);
					}
				}).show();
				return false;
			}
		});
		search_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				new PandianCustomDialog(StockTakingShopperDetailActivity.this, searchList.get(position).PDSKUCount, searchList.get(position).SKUID, new PandianCustomDialog.OnCustomDialogListener() {
					
					@Override
					public void confirm(int num) {
						modifyEntity = searchList.get(position);
						modifyEntity.PDSKUCount = num;
						modifyEntity.Status = 1;
						List<StockTakingDetailEntity> list = new ArrayList<StockTakingDetailEntity>();
						list.add(modifyEntity);
						startJsonTask(HotConstants.Global.APP_URL_USER
								+ HotConstants.Stock.MODIFY_SHELF, WHAT_NET_PANDIAN_MODIFY, true,
								NET_METHOD_POST, SaveListUtil.updatePandian(list, ShelfLocationCode), false);
					}
				}).show();
				return false;
			}
		});
		ShelfLocationCode = getIntent().getExtras().getString("ShelfLocationCode");
		detailAdapter = new StockTakingDetailAdapter(this, detailList);
		only_list.setAdapter(detailAdapter);
		only_list.setOnRefreshListener(this);
		only_list.setOnLoadListener(this);
		only_list.setPageSize(pageSize);
		only_list.onLoadComplete();
		startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.LoadShelflocation, WHAT_NET_SHELF_LIST,
					NET_METHOD_POST, false);
		searchAdapter = new StockTakingDetailAdapter(this, searchList); 
		search_list.setAdapter(searchAdapter);
		hideSoftKeyBoard(this, pandian_edt);
		pandian_edt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
					if(TextUtils.isEmpty(s) || s.toString().trim().equals("")) {
						only_list.setVisibility(View.VISIBLE);
						search_list.setVisibility(View.GONE);
						searchList.clear();
						searchAdapter.notifyDataSetChanged();
						searchSku = null;
						diffrence_count_txt.setEnabled(true);
						diffrence_money_txt.setEnabled(true);
					} else {
						diffrence_count_txt.setEnabled(false);
						diffrence_money_txt.setEnabled(false);
						searchList.clear();
						only_list.setVisibility(View.GONE);
						search_list.setVisibility(View.VISIBLE);
						searchSku = s.toString();
						startTask(HotConstants.Global.APP_URL_USER
								+ HotConstants.Shelf.QueryShelflocation, WHAT_NET_PANDIAN_SEARCH, false,
								NET_METHOD_POST, false);
					}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		pandian_edt.setOnKeyListener(new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {// 修改回车键功能
					if(pandian_edt.getText() == null || pandian_edt.getText().toString().equals("")) {
						return false;
					}
					searchList.clear();
					only_list.setVisibility(View.GONE);
					search_list.setVisibility(View.VISIBLE);
					searchSku = pandian_edt.getText().toString();
					startTask(HotConstants.Global.APP_URL_USER
							+ HotConstants.Shelf.QueryShelflocation, WHAT_NET_PANDIAN_SEARCH, false,
							NET_METHOD_POST, false);
				}
				return false;
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_SHELF_LIST:
			return WarehouseNet.getShelfDetail(ShelfLocationCode, SortName, SortType, String.valueOf(pageIndex), String.valueOf(pageSize));
		case WHAT_NET_PANDIAN_SEARCH:
			return WarehouseNet.getShelfQuery(ShelfLocationCode, searchSku);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		Ex.Toast(this).show("你的网速不太好，获取失败");
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_SHELF_LIST:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			List<StockTakingDetailEntity> list = mGson.fromJson(str,
					new TypeToken<List<StockTakingDetailEntity>>() {
					}.getType());
			
			only_list.setResultSize(list.size());
			detailList.clear();
			detailList.addAll(list);
			detailAdapter.notifyDataSetChanged();
			pageIndex++;
			break;
		case WHAT_NET_PANDIAN_MODIFY:
			if (net.isSuccess) {
				Ex.Toast(mActivity).showLong("操作成功");
				if(net.data == null) {
					if(searchSku == null)
						detailList.remove(modifyEntity);
					else
						searchList.remove(modifyEntity);
				} else {
					StockTakingShopperDetailListActivity.refresh = true;
					StockTakingShopperActivity.refresh = true;
					String strmodify = mGson.toJson(net.data);
					List<StockTakingDetailEntity> modifyList = mGson.fromJson(strmodify,
							new TypeToken<List<StockTakingDetailEntity>>() {
							}.getType());
					if(searchSku == null) {
						for(int i=0; i<detailList.size(); i++) {
							if(detailList.get(i).SKUID.equals(modifyEntity.SKUID)) {
								detailList.get(i).KCSKUCount = modifyList.get(0).KCSKUCount;
								detailList.get(i).PDSKUCount = modifyList.get(0).PDSKUCount;
								detailList.get(i).CYPrice = modifyList.get(0).CYPrice;
								detailList.get(i).CYCount = modifyList.get(0).CYCount;
							}
						}
					} else {
						for(int i=0; i<searchList.size(); i++) {
							if(searchList.get(i).SKUID.equals(modifyEntity.SKUID)) {
								searchList.get(i).KCSKUCount = modifyList.get(0).KCSKUCount;
								searchList.get(i).PDSKUCount = modifyList.get(0).PDSKUCount;
								searchList.get(i).CYPrice = modifyList.get(0).CYPrice;
								searchList.get(i).CYCount = modifyList.get(0).CYCount;
							}
						}
					}
				}
				if(searchSku == null)
					detailAdapter.notifyDataSetChanged();
				else
					searchAdapter.notifyDataSetChanged();
			}
			
			break;
		case WHAT_NET_PANDIAN_SEARCH:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			if (net.isSuccess) {
				String strSearch = mGson.toJson(net.data);
				List<StockTakingDetailEntity> selist = mGson.fromJson(strSearch,
						new TypeToken<List<StockTakingDetailEntity>>() {
						}.getType());
				searchList.addAll(selist);
				searchAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认->库位详情->返回");
			hideSoftKeyBoard(this, pandian_edt);
			this.finish();
			break;
		case R.id.lv_right:
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认->库位详情->添加SKU弹窗");
			Intent intent = new Intent(this, StockTakingShopperAddActivity.class);
			Bundle bd = new Bundle();
			bd.putString("ShelfLocationCode", ShelfLocationCode);
			intent.putExtras(bd);
			startActivity(intent);
			break;
		case R.id.diffrence_money_txt:
			SortName = "金额";
			if(moneySortDesc) {
				moneySortDesc = false;
				SortType = "asc";
				diffrence_money_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort_desc), null);
			} else {
				moneySortDesc = true;
				SortType = "desc";
				diffrence_money_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort), null);
			}
			detailList.clear();
			detailAdapter.notifyDataSetChanged();
			pageIndex = 0;
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.LoadShelflocation, WHAT_NET_SHELF_LIST,
					NET_METHOD_POST, false);
			break;
		case R.id.diffrence_count_txt:
			SortName = "数量";
			if(countSortDesc) {
				countSortDesc = false;
				SortType = "asc";
				diffrence_count_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort_desc), null);
			} else {
				countSortDesc = true;
				SortType = "desc";
				diffrence_count_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort), null);
			}
			detailList.clear();
			detailAdapter.notifyDataSetChanged();
			pageIndex = 0;
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.LoadShelflocation, WHAT_NET_SHELF_LIST,
					NET_METHOD_POST, false);
			break;
		}
	}
	
	/**
	 * 修改
	 * @param entity
	 */
//	public void modify() {
////		this.modifyEntity = entity;
////		modifyEntity.PDSKUCount = num;
//		List<StockTakingDetailEntity> list = new ArrayList<StockTakingDetailEntity>();
//		list.add(entity);
//		startJsonTask(HotConstants.Global.APP_URL_USER
//				+ HotConstants.Stock.MODIFY_SHELF, WHAT_NET_PANDIAN_MODIFY, true,
//				NET_METHOD_POST, SaveListUtil.updatePandian(list, ShelfLocationCode), false);
//	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what==1) {
				only_list.setResultSize(newList.size());
				if(isRefresh) {
					pageIndex = 1;
					detailList.clear();
					only_list.onRefreshComplete();
					Ex.Toast(mContext).showLong("刷新成功");
				} else {
					pageIndex++;
					only_list.onLoadComplete();
				}
				detailList.addAll(newList);
				detailAdapter.notifyDataSetChanged();
			} else if(msg.what==2){
				if(isRefresh) {
					only_list.onRefreshComplete();
					Ex.Toast(mContext).showLong("刷新失败");
				} else {
					only_list.onLoadComplete();
					Ex.Toast(mContext).showLong("加载失败");
				}
			}
		}
	};
	
	ExRequestCallback requestCallback = new ExRequestCallback() {
		@Override
		public void onSuccess(InputStream inStream,
				HashMap<String, String> cookies) {
			// 请求结果
			String result = "";
			// 判断结果流是否为空
			if (inStream != null) {
				// 转换流对象
				result = Ex.T().getInStream2Str(inStream);
				ResultNet<?> net = Ex.T().getString2Cls(result,
						ResultNet.class);
				if (!net.isSuccess) {
					Ex.Toast(mContext).showLong(net.message);
					return;
				}
				String str = mGson.toJson(net.data);
				newList = mGson.fromJson(str,
						new TypeToken<List<StockTakingDetailEntity>>() {
						}.getType());
				handler.sendEmptyMessage(1);
			}
		}
		@Override
		public void onError(int statusCode, ExException e) {
			handler.sendEmptyMessage(2);
		}
	};

	@Override
	public void onLoad() {
		only_list.onRefreshComplete();
		isRefresh = false;
		Ex.Net(mContext).sendAsyncPost(
				HotConstants.Global.APP_URL_USER + HotConstants.Shelf.LoadShelflocation,
				WarehouseNet.getShelfDetail(ShelfLocationCode, SortName, SortType, String.valueOf(pageIndex), String.valueOf(pageSize)), requestCallback);			
	}

	@Override
	public void onRefresh() {
		only_list.onLoadComplete();
		isRefresh = true;
		Ex.Net(mContext).sendAsyncPost(
				HotConstants.Global.APP_URL_USER + HotConstants.Shelf.LoadShelflocation,
				WarehouseNet.getShelfDetail(ShelfLocationCode, SortName, SortType, "0", String.valueOf(pageSize)), requestCallback);		
		
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	public void fillCode(String code) {
		if (!SkuUtil.isWarehouse(code)) {
			ap.getsoundPool(ap.Sound_sku);
			pandian_edt.setText(code);
			pandian_edt.setSelection(code.length());
			diffrence_count_txt.setEnabled(false);
			diffrence_money_txt.setEnabled(false);
		} else{
			ap.getsoundPool(ap.Sound_error);
		}
	}

}
