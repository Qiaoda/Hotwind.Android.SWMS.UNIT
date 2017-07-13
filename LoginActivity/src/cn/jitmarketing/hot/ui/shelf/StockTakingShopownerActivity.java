package cn.jitmarketing.hot.ui.shelf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingShopownerDiffListAdapter;
import cn.jitmarketing.hot.adapter.StockTakingShopownerFinishListAdapter;
import cn.jitmarketing.hot.adapter.StockTakingShopownerListAdapter;
import cn.jitmarketing.hot.adapter.StockTakingShopownerUnfinishListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingShopownerEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.RefreshableView;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.RefreshableView.PullToRefreshListener;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingShopownerActivity extends BaseSwipeOperationActivity implements
		OnClickListener, OnItemClickListener {

	private static final int WHAT_NET_check_sure = 0x11;
	private static final int WHAT_NET_STOCK_LIST = 0x10;
	@ViewInject(R.id.stock_shopowner_list)
	ListView stock_list;
	@ViewInject(R.id.shopowner_back_btn)
	Button shopowner_back_btn;
	@ViewInject(R.id.shopowner_one_ckb)
	CheckBox shopowner_one_ckb;//未盘点
	@ViewInject(R.id.shopowner_two_ckb)
	CheckBox shopowner_two_ckb;//已盘点
	@ViewInject(R.id.shopowner_three_ckb)
	CheckBox shopowner_three_ckb;//有差异
	@ViewInject(R.id.refreshable_view)
	RefreshableView refreshableView;
	
	private StockTakingShopownerListAdapter adapter;
	private StockTakingShopownerUnfinishListAdapter unfinishAdapter;
	private StockTakingShopownerFinishListAdapter finishAdapter;
	private StockTakingShopownerDiffListAdapter diffAdapter;
	private List<StockTakingShopownerEntity> finishList = new ArrayList<StockTakingShopownerEntity>();//已经盘点
	private List<StockTakingShopownerEntity> unfinishList = new ArrayList<StockTakingShopownerEntity>();//未盘点
	private List<StockTakingShopownerEntity> differenceList = new ArrayList<StockTakingShopownerEntity>();//有差异
	private boolean canReceive;
	public static boolean refresh = false;
	private HotApplication ap;
	private int tab = 0 ;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.stock_taking_shopowner;
	}

	@Override
	protected void exInitView() {
		shopowner_one_ckb.setOnClickListener(this);
		shopowner_two_ckb.setOnClickListener(this);
		shopowner_three_ckb.setOnClickListener(this);
		shopowner_back_btn.setOnClickListener(this);
//		stock_list.setOnItemClickListener(this);
		ap = (HotApplication) getApplication();
		canReceive = true;
//		startTask(HotConstants.Global.APP_URL_USER
//					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
//					NET_METHOD_POST, false);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				Ex.Net(mContext).sendAsyncPost(
						HotConstants.Global.APP_URL_USER
						+ HotConstants.Shelf.shopownerPlanStock,
						WarehouseNet.getCheckStockList(), requestCallback);
			}
		}, 1);
	}
	
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
				String stockStr = mGson.toJson(net.data);
				showListViewValue(stockStr);
				handler.sendEmptyMessage(1);
			}
		}
		@Override
		public void onError(int statusCode, ExException e) {
			handler.sendEmptyMessage(2);
		}
	};
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg){
			if(msg.what==1){
				if(tab == 0)
					stock_list.setAdapter(unfinishAdapter);
				else if(tab == 1)
					stock_list.setAdapter(finishAdapter);
				if(tab == 2)
					stock_list.setAdapter(diffAdapter);
//				stock_list.setAdapter(adapter);
				Ex.Toast(mContext).showLong("刷新成功");
				refreshableView.finishRefreshing();
			} else if(msg.what ==2){
				refreshableView.finishRefreshing();
				Ex.Toast(mContext).showLong("刷新失败");
			}
		}
	};

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
	protected void onResume() {
		canReceive = true;
		super.onResume();
//		if(refresh)
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
	}

	@Override
	public void onReceiver(Intent intent) {
//		if (canReceive) {
//			byte[] barocode = intent.getByteArrayExtra("barocode");
//			int codelen = intent.getIntExtra("length", 0);
//			if (null != barocode) {
//				String code = new String(barocode, 0, codelen);
//				if (SkuUtil.isWarehouse(code)) {
//					for(StockTakingShopownerEntity entity : shopownerList) {
//						if(entity.ShelfLocationCode.equals(code)) {
//							startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmSampleStockCheck, 
//									WHAT_NET_check_sure, true, NET_METHOD_POST, SaveListUtil.ownerStockSave(entity.ShelfLocationCode), false);
//							break;
//						}
//					}
//				} else {
//					ap.getsoundPool(ap.Sound_error);
//				}
//			}
//		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			return WarehouseNet.getCheckStockList();
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		refresh = false;
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		refresh = false;
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			showListViewValue(stockStr);
			if(tab == 0)
				stock_list.setAdapter(unfinishAdapter);
			else if(tab == 1)
				stock_list.setAdapter(finishAdapter);
			if(tab == 2)
				stock_list.setAdapter(diffAdapter);
			break;
		case WHAT_NET_check_sure:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			try {
				JSONObject js = new JSONObject(str);
				int checkTimes = js.getInt("CheckTimes");
				String differenceShelf = js.getString("DifferenceShelfLocations"); 
				String[] difList = mGson.fromJson(differenceShelf,
						new TypeToken<String[]>() {
						}.getType());
				if(difList.length > 0) {
					Intent intent = new Intent(this, StockTakingDiffrenceActivity.class);
					Bundle bd = new Bundle();
					bd.putStringArray("differenceShelf", difList);
					bd.putBoolean("detail", false);
					intent.putExtras(bd);
					startActivity(intent);
				} else {
					startTask(HotConstants.Global.APP_URL_USER
							+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
							NET_METHOD_POST, false);
				}
			} catch (JSONException e) {
			}
			break;
			
		}
	}
	
	private void showListViewValue(String str) {
		try {
			JSONObject js = new JSONObject(str);
			String notCheck = js.getString("NotCheckList");
			String completed = js.getString("CompletedList");
			String difference = js.getString("DifferenceList");
			unfinishList = mGson.fromJson(notCheck,
					new TypeToken<List<StockTakingShopownerEntity>>() {
					}.getType());
			finishList = mGson.fromJson(completed,
					new TypeToken<List<StockTakingShopownerEntity>>() {
					}.getType());
			differenceList = mGson.fromJson(difference,
					new TypeToken<List<StockTakingShopownerEntity>>() {
					}.getType());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		finishAdapter = new StockTakingShopownerFinishListAdapter(this, finishList);
		unfinishAdapter = new StockTakingShopownerUnfinishListAdapter(this, unfinishList);
		diffAdapter = new StockTakingShopownerDiffListAdapter(this, differenceList);
//		if(tab == 0)
//			adapter = new StockTakingShopownerListAdapter(this, finishList);
//		else if(tab == 1)
//			adapter = new StockTakingShopownerListAdapter(this, unfinishList);
//		else if(tab == 2)
//			adapter = new StockTakingShopownerListAdapter(this, differenceList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.shopowner_back_btn:
			this.finish();
			break;
		case R.id.shopowner_one_ckb:
			tab = 0;
			shopowner_one_ckb.setChecked(true);
			shopowner_two_ckb.setChecked(false);
			shopowner_three_ckb.setChecked(false);
//			adapter = new StockTakingShopownerListAdapter(this, finishList);
			stock_list.setAdapter(unfinishAdapter);
			break;
		case R.id.shopowner_two_ckb:
			tab = 1;
			shopowner_one_ckb.setChecked(false);
			shopowner_two_ckb.setChecked(true);
			shopowner_three_ckb.setChecked(false);
//			adapter = new StockTakingShopownerListAdapter(this, unfinishList);
			stock_list.setAdapter(finishAdapter);
			break;
		case R.id.shopowner_three_ckb:
			tab = 2;
			shopowner_one_ckb.setChecked(false);
			shopowner_two_ckb.setChecked(false);
			shopowner_three_ckb.setChecked(true);
//			adapter = new StockTakingShopownerListAdapter(this, differenceList);
			stock_list.setAdapter(diffAdapter);
			break;
		}
	}
	
	public void request(StockTakingShopownerEntity select) {
		startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmSampleStockCheck, 
				WHAT_NET_check_sure, true, NET_METHOD_POST, SaveListUtil.ownerStockSave(select.ShelfLocationCode), false);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		StockTakingShopownerEntity select = shopownerList.get(position);
//		startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmSampleStockCheck, 
//				WHAT_NET_check_sure, true, NET_METHOD_POST, SaveListUtil.ownerStockSave(select.ShelfLocationCode), false);
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		
	}
}
