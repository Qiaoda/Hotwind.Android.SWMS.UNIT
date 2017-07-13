package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTaskErrorListAdapter;
import cn.jitmarketing.hot.adapter.StockTaskShelfLocationDiffenceListAdapter;
import cn.jitmarketing.hot.adapter.StockTaskStockDiffenceListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockDetaiShelfDiffEntity;
import cn.jitmarketing.hot.entity.StockDetaiStockDiffEntity;
import cn.jitmarketing.hot.entity.StockDetailErrorEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.pandian.StockTakingShopperHistoryDetailActivity;
import cn.jitmarketing.hot.util.LogUtils;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingTaskDetailActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {

	private static final int WHAT_NET_STOCK_LIST = 0x10;
	@ViewInject(R.id.stock_list)
	ListView stock_list;
	@ViewInject(R.id.shelf_list)
	ListView shelf_list;
	@ViewInject(R.id.error_list)
	ListView error_list;
	@ViewInject(R.id.detail_back_btn)
	Button detail_back_btn;
	@ViewInject(R.id.detail_one_ckb)
	CheckBox detail_one_ckb;
	@ViewInject(R.id.detail_two_ckb)
	CheckBox detail_two_ckb;
	@ViewInject(R.id.detail_three_ckb)
	CheckBox detail_three_ckb;
	
	private StockTaskErrorListAdapter errorAdapter;
	private StockTaskShelfLocationDiffenceListAdapter shelfAdapter;
	private StockTaskStockDiffenceListAdapter stockAdapter;
	private List<StockDetailErrorEntity> errorAllList = new ArrayList<StockDetailErrorEntity>();//错误
	private List<StockDetaiShelfDiffEntity> shelfDiffAllList = new ArrayList<StockDetaiShelfDiffEntity>();//
	private List<StockDetaiStockDiffEntity> stockDiffAllList = new ArrayList<StockDetaiStockDiffEntity>();//
	private String checkTaskID;
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.stock_taking_task_detail;
	}

	@Override
	protected void exInitView() {
		detail_one_ckb.setOnClickListener(this);
		detail_two_ckb.setOnClickListener(this);
		detail_three_ckb.setOnClickListener(this);
		detail_back_btn.setOnClickListener(this);
		stockAdapter = new StockTaskStockDiffenceListAdapter(this, stockDiffAllList);
		shelfAdapter = new StockTaskShelfLocationDiffenceListAdapter(this, shelfDiffAllList);
		errorAdapter = new StockTaskErrorListAdapter(this, errorAllList);
		stock_list.setAdapter(stockAdapter);
		shelf_list.setAdapter(shelfAdapter);
		error_list.setAdapter(errorAdapter);
		checkTaskID = getIntent().getExtras().getString("checkTaskID");
		startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.stockTaskDetailList, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
		shelf_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(StockTakingTaskDetailActivity.this, StockTakingShopperHistoryDetailActivity.class);
				Bundle bd = new Bundle();
				bd.putString("ShelfLocationCode", shelfDiffAllList.get(position).ShelfLocationCode);
				bd.putString("checkTaskID", checkTaskID);
				intent.putExtras(bd);
				startActivity(intent);	 
				//日志操作
				LogUtils.logOnFile("盘点任务->历史盘点->盘点明细->库位->库位详情："+shelfDiffAllList.get(position).ShelfLocationCode);
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
		case WHAT_NET_STOCK_LIST:
			return WarehouseNet.getStockDetailList(checkTaskID);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
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
			String str = mGson.toJson(net.data);
			try {
				JSONObject js = new JSONObject(str);
				String shelfStr = js.getString("ShelfLocationDiffenceList");
				List<StockDetaiShelfDiffEntity> shelfList = mGson.fromJson(shelfStr,
						new TypeToken<List<StockDetaiShelfDiffEntity>>() {
						}.getType());
				shelfDiffAllList.addAll(shelfList);
				shelfAdapter.notifyDataSetChanged();
				String errorStr = js.getString("ErrorList");
				errorAllList.clear();
				List<StockDetailErrorEntity> errorList = mGson.fromJson(errorStr, new TypeToken<List<StockDetailErrorEntity>>() {}.getType());
				errorAllList.addAll(errorList);
				errorAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.detail_back_btn:
			//日志操作
			LogUtils.logOnFile("盘点任务->历史盘点->盘点明细->返回");
			this.finish();
			break;
		case R.id.detail_one_ckb:
			//日志操作
			LogUtils.logOnFile("盘点任务->历史盘点->盘点明细->库位");
			detail_one_ckb.setChecked(true);
			detail_two_ckb.setChecked(false);
			detail_three_ckb.setChecked(false);
			stock_list.setVisibility(View.VISIBLE);
			shelf_list.setVisibility(View.GONE);
			error_list.setVisibility(View.GONE);
//			if(stockDiffAllList.size() == 0) {
//				startTask(HotConstants.Global.APP_URL_USER
//						+ HotConstants.Shelf.stockTaskDetailList, WHAT_NET_STOCK_LIST,
//						NET_METHOD_POST, false);
//			}
			break;
		case R.id.detail_two_ckb:
			//日志操作
			LogUtils.logOnFile("盘点任务->历史盘点->盘点明细->错误");
			detail_one_ckb.setChecked(false);
			detail_two_ckb.setChecked(true);
			detail_three_ckb.setChecked(false);
			stock_list.setVisibility(View.GONE);
			shelf_list.setVisibility(View.VISIBLE);
			error_list.setVisibility(View.GONE);
//			if(shelfDiffAllList.size() == 0) {
//				startTask(HotConstants.Global.APP_URL_USER
//						+ HotConstants.Shelf.stockTaskDetailList, WHAT_NET_STOCK_LIST,
//						NET_METHOD_POST, false);
//			}
			break;
		case R.id.detail_three_ckb:
			detail_one_ckb.setChecked(false);
			detail_two_ckb.setChecked(false);
			detail_three_ckb.setChecked(true);
			stock_list.setVisibility(View.GONE);
			shelf_list.setVisibility(View.GONE);
			error_list.setVisibility(View.VISIBLE);
//			if(errorAllList.size() == 0) {
//				startTask(HotConstants.Global.APP_URL_USER
//						+ HotConstants.Shelf.stockTaskDetailList, WHAT_NET_STOCK_LIST,
//						NET_METHOD_POST, false);
//			}
			break;
		}
	}
	
}
