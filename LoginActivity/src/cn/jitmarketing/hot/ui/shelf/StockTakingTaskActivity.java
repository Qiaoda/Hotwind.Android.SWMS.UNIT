package cn.jitmarketing.hot.ui.shelf;

import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingTaskListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingTaskEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingTaskActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {

	private static final int WHAT_NET_STOCK_LIST = 0x10;
	private static final int WHAT_NET_STOCK_FINISH = 0x11;
	@ViewInject(R.id.stock_taking_task_title)
	TitleWidget stock_taking_title;
	@ViewInject(R.id.stock_task_list)
	ListView stock_list;
	
	private StockTakingTaskListAdapter adapter;
	private List<StockTakingTaskEntity> taskList;
	public static boolean refresh = false;
	private String taskId;
	private int mRequestTime, mOutTime = 2*60*1000;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.stock_taking_task;
	}

	@Override
	protected void exInitView() {
		stock_taking_title.setText("盘点任务");
		stock_taking_title.setOnLeftClickListner(this);
		stock_taking_title.setOnRightClickListner(this);
		startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.stockTaskList, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
	}
	
	public void finishTask(String taskId) {
		this.taskId = taskId;
		startLongTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.stockTaskFinish, WHAT_NET_STOCK_FINISH, true, NET_METHOD_POST, false, mRequestTime, mOutTime);
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
		if(refresh)
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.stockTaskList, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			return WarehouseNet.getCheckStockList();
		case WHAT_NET_STOCK_FINISH:
			return WarehouseNet.stockTaskFinish(taskId);
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
		refresh = false;
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			taskList = mGson.fromJson(stockStr,
					new TypeToken<List<StockTakingTaskEntity>>() {
					}.getType());
			adapter = new StockTakingTaskListAdapter(this, taskList);
			stock_list.setAdapter(adapter);
			break;
		case WHAT_NET_STOCK_FINISH:	
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.stockTaskList, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
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
			for(StockTakingTaskEntity entity : taskList) {
				if(entity.Status == 0) {
					Ex.Toast(this).showLong("当前有未完成的盘点任务");
					return;
				}
			}
			Intent intent = new Intent();
			intent.setClass(StockTakingTaskActivity.this, CreatStockTaskActivity.class);
			startActivity(intent);
			break;
		}
	}

}
