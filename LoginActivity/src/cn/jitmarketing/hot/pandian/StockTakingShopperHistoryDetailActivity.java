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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingDetailAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingDetailEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.AutoListView;
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
public class StockTakingShopperHistoryDetailActivity extends BaseSwipeOperationActivity implements
		OnClickListener,  OnRefreshListener, OnLoadListener {

	private static final int WHAT_NET_SHELF_LIST = 0x10;
	private static final int WHAT_NET_PANDIAN_SEARCH = 0x12;
	
	@ViewInject(R.id.stock_taking_task_title)
	TitleWidget detail_title;
	@ViewInject(R.id.detail_list)
	AutoListView only_list;
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
	
	private String CheckTaskID;
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_stock_taking_shopper_history_detail;
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		detail_title.setText("库位详情");
		detail_title.setOnLeftClickListner(this);
		detail_title.setOnRightClickListner(this);
		ShelfLocationCode = getIntent().getExtras().getString("ShelfLocationCode");
		CheckTaskID = getIntent().getExtras().getString("checkTaskID");
		detailAdapter = new StockTakingDetailAdapter(this, detailList);
		only_list.setAdapter(detailAdapter);
		only_list.setOnRefreshListener(this);
		only_list.setOnLoadListener(this);
		only_list.setPageSize(pageSize);
		only_list.onLoadComplete();
		startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.LoadShelfHistorylocation, WHAT_NET_SHELF_LIST,
					NET_METHOD_POST, false);
		searchAdapter = new StockTakingDetailAdapter(this, searchList); 
		search_list.setAdapter(searchAdapter);
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
			return WarehouseNet.getShelfHistoryDetail(ShelfLocationCode, CheckTaskID, String.valueOf(pageIndex), String.valueOf(pageSize));
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
			this.finish();
			break;
		case R.id.lv_right:
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
				HotConstants.Global.APP_URL_USER + HotConstants.Shelf.LoadShelfHistorylocation,
				WarehouseNet.getShelfHistoryDetail(ShelfLocationCode, CheckTaskID, String.valueOf(pageIndex), String.valueOf(pageSize)), requestCallback);			
	}

	@Override
	public void onRefresh() {
		only_list.onLoadComplete();
		isRefresh = true;
		Ex.Net(mContext).sendAsyncPost(
				HotConstants.Global.APP_URL_USER + HotConstants.Shelf.LoadShelfHistorylocation,
				WarehouseNet.getShelfHistoryDetail(ShelfLocationCode, CheckTaskID, "0", String.valueOf(pageSize)), requestCallback);		
		
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
			diffrence_count_txt.setEnabled(false);
			diffrence_money_txt.setEnabled(false);
		} else{
			ap.getsoundPool(ap.Sound_error);
		}
	}

}
