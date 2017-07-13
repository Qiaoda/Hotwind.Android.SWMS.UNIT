package cn.jitmarketing.hot.pandian;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingTaskHistoryListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ShopTaskBean;
import cn.jitmarketing.hot.entity.StockTakingTaskEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.ui.shelf.CreatStockTaskActivity;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.ClearEditText;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingShopperActivity extends BaseSwipeBackAcvitiy implements OnClickListener {

	private static final int WHAT_NET_PANDIAN = 0x10;
	private static final int WHAT_NET_PANDIAN_HISTORY_LIST = 0x11;
	private static final int WHAT_NET_STOCK_FINISH = 0x12;
	@ViewInject(R.id.search_edt)
	ClearEditText searchEdt;
	@ViewInject(R.id.search_layout)
	LinearLayout searchLayout;
	@ViewInject(R.id.back_layout)
	LinearLayout back_layout;
	@ViewInject(R.id.add_layout)
	LinearLayout add_layout;
	@ViewInject(R.id.history_pandian_listview)
	ListView history_pandian_listview;
	@ViewInject(R.id.this_pandian_txt)
	TextView this_pandian_txt;
	@ViewInject(R.id.history_pandian_txt)
	TextView history_pandian_txt;
	@ViewInject(R.id.this_pandian_layout)
	RelativeLayout this_pandian_layout;
	@ViewInject(R.id.actual_shelf_count)
	TextView actual_shelf_count;// 实际库位数
	@ViewInject(R.id.system_shelf_count)
	TextView system_shelf_count;// 系统库位数
	@ViewInject(R.id.actual_pandian_count)
	TextView actual_pandian_count;// 实际数量
	@ViewInject(R.id.system_pandian_count)
	TextView system_pandian_count;// 系统数量
	@ViewInject(R.id.diffrence_shelf)
	TextView diffrence_shelf;// 差异库位
	@ViewInject(R.id.diffrence_money)
	TextView diffrence_money;// 差异金额
	@ViewInject(R.id.diffrence_tiaozheng_money)
	TextView diffrence_tiaozheng_money;// 调整单金额
	@ViewInject(R.id.pending_money)
	TextView pending_money;// 待处理金额
	@ViewInject(R.id.stock_detail_btn)
	Button stock_detail_btn;
	@ViewInject(R.id.stock_finish_btn)
	Button stock_finish_btn;
	@ViewInject(R.id.no_task)
	TextView no_task;// 没有任务

	private int tab = 1;
	private String CheckNoticeID = "";
	private List<StockTakingTaskEntity> taskList;
	private List<StockTakingTaskEntity> searchList=new ArrayList<StockTakingTaskEntity>();
	private StockTakingTaskHistoryListAdapter adapter;
	public static boolean refresh = false;
	private String taskId;
	private int mRequestTime, mOutTime = 2 * 60 * 1000;
	private boolean isOpenCurrentPandian = false;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_stock_taking_shopper;
	}

	@Override
	protected void exInitView() {
		stock_detail_btn.setOnClickListener(this);
		stock_finish_btn.setOnClickListener(this);
		searchEdt.addTextChangedListener(textWatcher);
		searchEdt.setOnEditorActionListener(onEditorActionListener);
		back_layout.setOnClickListener(this);
		add_layout.setOnClickListener(this);
		this_pandian_txt.setOnClickListener(this);
		history_pandian_txt.setOnClickListener(this);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.stockTask, WHAT_NET_PANDIAN, NET_METHOD_POST, false);
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
		if (refresh) {
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.stockTask, WHAT_NET_PANDIAN, NET_METHOD_POST, false);
		}
		// 日志
		LogUtils.logOnFile("盘点任务->本次盘点");
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_PANDIAN:
			return WarehouseNet.getStockTask(CheckNoticeID);
		case WHAT_NET_PANDIAN_HISTORY_LIST:
			return WarehouseNet.getCheckStockList();
		case WHAT_NET_STOCK_FINISH:
			return WarehouseNet.stockTaskFinish(taskId);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_PANDIAN:
			refresh = false;
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		case WHAT_NET_PANDIAN_HISTORY_LIST:
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		// 操作日志
		LogUtils.logOnFile("任务盘点->" + result);
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			// 操作日志
			LogUtils.logOnFile("任务盘点->" + net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_PANDIAN:
			refresh = false;
			no_task.setVisibility(View.VISIBLE);
			if (null == net.data) {
				no_task.setText("您还未开启盘点任务");
				isOpenCurrentPandian = false;
				return;
			}
			isOpenCurrentPandian = true;
			no_task.setVisibility(View.GONE);
			String str = mGson.toJson(net.data);
			ShopTaskBean shopTaskBean = mGson.fromJson(str, ShopTaskBean.class);
			this.taskId = shopTaskBean.TaskID;
			actual_shelf_count.setText(String.valueOf(shopTaskBean.AlreadyShelfLocationCount));
			system_shelf_count.setText(String.valueOf(shopTaskBean.ShelfLocationCount));
			actual_pandian_count.setText(String.valueOf(Double.valueOf(shopTaskBean.AlreadyCount).intValue()));
			system_pandian_count.setText(String.valueOf(Double.valueOf(shopTaskBean.NeedCount).intValue()));
			diffrence_shelf.setText(String.valueOf(shopTaskBean.DifferenceShelfLocationCount));
			diffrence_money.setText(shopTaskBean.DifferenceMoneyCount);
			diffrence_tiaozheng_money.setText(shopTaskBean.AdjustMoneyCount);
			pending_money.setText(shopTaskBean.PosPrice);
			break;
		case WHAT_NET_PANDIAN_HISTORY_LIST:
			no_task.setVisibility(View.GONE);
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				// 操作日志
				LogUtils.logOnFile("任务盘点->" + net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			taskList = mGson.fromJson(stockStr, new TypeToken<List<StockTakingTaskEntity>>() {
			}.getType());
			searchList.clear();
			searchList.addAll(taskList);
			adapter = new StockTakingTaskHistoryListAdapter(this, taskList);
			history_pandian_listview.setAdapter(adapter);
			break;
		case WHAT_NET_STOCK_FINISH:
			no_task.setVisibility(View.VISIBLE);
			no_task.setText("您还未开启盘点任务");
			// startTask(HotConstants.Global.APP_URL_USER
			// + HotConstants.Shelf.stockTask, WHAT_NET_PANDIAN,
			// NET_METHOD_POST, false);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.stock_detail_btn:// 盘点明细
			// 日志操作
			LogUtils.logOnFile("盘点任务->盘点明细");
			Intent intent1 = new Intent();
			intent1.setClass(StockTakingShopperActivity.this, StockTakingShopperDetailListActivity.class);
			startActivity(intent1);
			break;
		case R.id.stock_finish_btn:// 盘点结束
			// 日志操作
			LogUtils.logOnFile("盘点任务->盘点结束");
			new AlertDialog.Builder(this).setTitle(R.string.noteTitle).setMessage("确定结束盘点？").setPositiveButton(R.string.sureTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					startLongTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.stockTaskFinish, WHAT_NET_STOCK_FINISH, true, NET_METHOD_POST, false, mRequestTime, mOutTime);
				}
			}).setNegativeButton(R.string.cancelTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
			break;
		case R.id.back_layout:// 返回
			// 日志操作
			LogUtils.logOnFile("盘点任务->返回");
			this.finish();
			break;
		case R.id.add_layout:// 添加
			// 日志操作
			LogUtils.logOnFile("盘点任务->添加");
			Intent intent = new Intent();
			intent.setClass(StockTakingShopperActivity.this, CreatStockTaskActivity.class);
			startActivity(intent);
			break;
		case R.id.this_pandian_txt:// 本次盘点
			searchLayout.setVisibility(View.GONE);
			// 日志操作
			LogUtils.logOnFile("盘点任务->本次盘点");
			if (!isOpenCurrentPandian)
				no_task.setVisibility(View.VISIBLE);
			else
				no_task.setVisibility(View.GONE);
			tab = 1;
			resetBtn();
			this_pandian_txt.setBackgroundResource(R.drawable.tab_left_true);
			this_pandian_txt.setTextColor(getResources().getColor(R.color.color_white_text));
			history_pandian_txt.setTextColor(getResources().getColor(R.color.color_black_text));
			this_pandian_layout.setVisibility(View.VISIBLE);
			history_pandian_listview.setVisibility(View.GONE);
			break;
		case R.id.history_pandian_txt:// 历史盘点
			searchLayout.setVisibility(View.VISIBLE);
			// 日志操作
			LogUtils.logOnFile("盘点任务->历史盘点");
			no_task.setVisibility(View.GONE);
			tab = 2;
			resetBtn();
			history_pandian_txt.setBackgroundResource(R.drawable.tab_right_true);
			this_pandian_txt.setTextColor(getResources().getColor(R.color.color_black_text));
			history_pandian_txt.setTextColor(getResources().getColor(R.color.color_white_text));
			if (taskList == null || taskList.size() == 0) {
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.stockTaskList, WHAT_NET_PANDIAN_HISTORY_LIST, NET_METHOD_POST, false);
			}
			this_pandian_layout.setVisibility(View.GONE);
			history_pandian_listview.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void resetBtn() {
		this_pandian_txt.setBackgroundResource(R.drawable.tab_left);
		history_pandian_txt.setBackgroundResource(R.drawable.tab_right);
	}
	/**
	 * 搜索框文字监听器
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.equals("")) {
				taskList.clear();
				taskList.addAll(searchList);
				adapter.notifyDataSetChanged();
			} else {
				taskList.clear();
				for (int i = 0; i < searchList.size(); i++) {
					if (searchList.get(i).CheckTaskName.contains(s.toString())) {
						taskList.add(searchList.get(i));
					}
				}
				adapter.notifyDataSetChanged();
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
}
