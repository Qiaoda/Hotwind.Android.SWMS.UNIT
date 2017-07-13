package cn.jitmarketing.hot.pandian;

import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ShopTaskBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.ui.shelf.StockTakingTaskDetailActivity;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

/** 库存盘点列表界面 */
public class StockTakingShopperHistoryActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {

	private static final int WHAT_NET_PANDIAN = 0x10;
	private static final int WHAT_NET_PANDIAN_HISTORY_LIST = 0x11;
	
	@ViewInject(R.id.history_pandian_title)
	TitleWidget history_pandian_title;
	@ViewInject(R.id.actual_shelf_count)
	TextView actual_shelf_count;//实际库位数
	@ViewInject(R.id.system_shelf_count)
	TextView system_shelf_count;//系统库位数
	@ViewInject(R.id.actual_pandian_count)
	TextView actual_pandian_count;//实际数量
	@ViewInject(R.id.system_pandian_count)
	TextView system_pandian_count;//系统数量
	@ViewInject(R.id.diffrence_shelf)
	TextView diffrence_shelf;//差异库位
	@ViewInject(R.id.diffrence_money)
	TextView diffrence_money;//差异金额
	@ViewInject(R.id.diffrence_tiaozheng_money)
	TextView diffrence_tiaozheng_money;//调整单金额
	@ViewInject(R.id.pending_money)
	TextView pending_money;// 待处理金额
	@ViewInject(R.id.stock_detail_btn)
	Button stock_detail_btn;
	
	private String checkTaskID;
	private String checkTaskName;
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_stock_taking_shopper_history;
	}

	@Override
	protected void exInitView() {
		checkTaskID = getIntent().getExtras().getString("checkTaskID");
		checkTaskName= getIntent().getExtras().getString("checkTaskName");
		history_pandian_title.setOnLeftClickListner(this);
		stock_detail_btn.setOnClickListener(this);
		startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.stockTask, WHAT_NET_PANDIAN,
					NET_METHOD_POST, false);
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
		case WHAT_NET_PANDIAN:
			return WarehouseNet.getStockTask(checkTaskName);
		case WHAT_NET_PANDIAN_HISTORY_LIST:
			return WarehouseNet.getCheckStockList();
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_PANDIAN:
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		case WHAT_NET_PANDIAN_HISTORY_LIST:
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_PANDIAN:
			if (null == net.data) {
				return;
			}
			String str = mGson.toJson(net.data);
			ShopTaskBean shopTaskBean = mGson.fromJson(str, ShopTaskBean.class);
			actual_shelf_count.setText(String.valueOf(shopTaskBean.AlreadyShelfLocationCount));
			system_shelf_count.setText(String.valueOf(shopTaskBean.ShelfLocationCount));
			actual_pandian_count.setText(String.valueOf(Double.valueOf(shopTaskBean.AlreadyCount).intValue()));
			system_pandian_count.setText(String.valueOf(Double.valueOf(shopTaskBean.NeedCount).intValue()));
			diffrence_shelf.setText(String.valueOf(shopTaskBean.DifferenceShelfLocationCount));
			diffrence_money.setText(shopTaskBean.DifferenceMoneyCount);
			diffrence_tiaozheng_money.setText(shopTaskBean.AdjustMoneyCount);
			pending_money.setText(shopTaskBean.PosPrice);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			//日志操作
			LogUtils.logOnFile("盘点任务->历史盘点->返回");
			finish();
			break;
		case R.id.stock_detail_btn:
			//日志操作
			LogUtils.logOnFile("盘点任务->历史盘点->盘点明细");
			Intent intent = new Intent();
			intent.setClass(this, StockTakingTaskDetailActivity.class);
			Bundle bd = new Bundle();
			bd.putString("checkTaskID", checkTaskID);
			intent.putExtras(bd);
			startActivity(intent);
			break;
		}
	}
	

}
