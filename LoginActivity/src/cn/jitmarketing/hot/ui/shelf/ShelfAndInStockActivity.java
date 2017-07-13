package cn.jitmarketing.hot.ui.shelf;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jitmarketing.hot.HistoryInStockActivity;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.id;
import cn.jitmarketing.hot.adapter.ReceiveListAdapter;
import cn.jitmarketing.hot.entity.ReceiveSheet;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.RefreshableView;
import cn.jitmarketing.hot.util.RefreshableView.PullToRefreshListener;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 入库上架
 */
public class ShelfAndInStockActivity extends BaseSwipeOperationActivity implements OnClickListener, OnItemClickListener {
	@ViewInject(R.id.shelf_and_stock)
	private TitleWidget shelf_and_stock;
	// @ViewInject(R.id.shelfandinstock_et)
	// EditText shelfandinstock_et;
	// @ViewInject(R.id.shelfandinstock_iv)
	// ImageView shelfandinstock_iv;
	@ViewInject(R.id.and_list)
	private ListView and_list;
	@ViewInject(R.id.refreshable_view)
	private RefreshableView refreshableView;
	// 加载更多布局
	private LinearLayout load_layout;
	private ProgressBar progressbar;
	private TextView load_tip;
	private boolean canReceive;
	private List<ReceiveSheet> receList = new ArrayList<ReceiveSheet>();
	private static final int WHAT_NET_GET_IN_STOCK_LIST = 0;
	private ReceiveListAdapter receListAdapter;
	public static boolean stockComplete = false;
	private HotApplication ap;
	private int TotalCount;// 列表总数
	private int PageIndex = 1;
	private static final int PageSize = 100;

	@Override
	protected int exInitLayout() {
		return R.layout.activity_shelf_and_stock;
	}

	@Override
	protected void exInitBundle() {
		this.initIble(this, this);
	}

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
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		shelf_and_stock.setText("入库上架");
		shelf_and_stock.setOnLeftClickListner(this);
		shelf_and_stock.setOnRightClickListner(this);
		receListAdapter = new ReceiveListAdapter(this, getLayoutInflater(), receList);
		initListViewFooter();
		and_list.setAdapter(receListAdapter);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				// 日志操作
				LogUtils.logOnFile("入库上架->刷新入库列表");
				PageIndex = 1;
				stateLoaded();
				Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.newStockInList, WarehouseNet.getInStockList(PageIndex, PageSize), requestCallback);
			}
		}, 0);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.newStockInList, WHAT_NET_GET_IN_STOCK_LIST, NET_METHOD_POST, false);
		and_list.setOnItemClickListener(this);
		// 分页效果 滚动到底部加载下一页
		and_list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:// 当不滚动时
					if (view.getLastVisiblePosition() == view.getCount() - 1 && TotalCount != 0) {// 已滚动到最底部
						if (receList.size() < TotalCount) {
							PageIndex++;
							stateLoading();
							and_list.setSelection(and_list.getBottom());
							startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.newStockInList, WHAT_NET_GET_IN_STOCK_LIST, false, NET_METHOD_POST, false);
						} else if (receList.size() == TotalCount) {
							if (load_layout.getVisibility() == View.GONE) {
								stateUnload();
								and_list.setSelection(and_list.getBottom());
							}
						}
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});
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
				try {
					JSONObject jsonObject = new JSONObject(mGson.toJson(net.data));
					String stockStr = jsonObject.getString("List");
					ArrayList<ReceiveSheet> sheetList = mGson.fromJson(stockStr, new TypeToken<List<ReceiveSheet>>() {
					}.getType());
					receList.clear();
					receList.addAll(sheetList);
					handler.sendEmptyMessage(1);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onError(int statusCode, ExException e) {
			handler.sendEmptyMessage(2);
		}
	};

	@Override
	protected void onResume() {
		canReceive = true;
		if (stockComplete) {
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.newStockInList, WHAT_NET_GET_IN_STOCK_LIST, NET_METHOD_POST, false);
		}
		super.onResume();
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_IN_STOCK_LIST:
			return WarehouseNet.getInStockList(PageIndex, PageSize);
		}
		return super.onStart(what);
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_IN_STOCK_LIST:
			stockComplete = false;
			Ex.Toast(this).showLong(R.string.getInfo);
			// 日志操作
			LogUtils.logOnFile("入库上架->" + getText(R.string.getInfo).toString());
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		stockComplete = false;
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_GET_IN_STOCK_LIST:
			// 日志操作
			LogUtils.logOnFile("入库上架->获取列表数据");
			try {
				JSONObject jsonObject = new JSONObject(mGson.toJson(net.data));
				String stockStr = jsonObject.getString("List");
				TotalCount = jsonObject.getInt("TotalCount");
				ArrayList<ReceiveSheet> sheetList = mGson.fromJson(stockStr, new TypeToken<List<ReceiveSheet>>() {
				}.getType());
				if (PageIndex == 1) {
					receList.clear();
				} else {
					stateLoaded();
				}
				receList.addAll(sheetList);
				receListAdapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	protected void exInitAfter() {
	}

	@Override
	protected String[] exInitReceiver() {
		return super.exInitReceiver();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			mActivity.finish();
			break;
		case R.id.lv_right:
			startActivity(new Intent(mActivity, HistoryInStockActivity.class));
			break;
		// case R.id.shelfandinstock_iv:
		// String search=shelfandinstock_et.getText().toString();
		// for(int i=0;i<receList.size();i++){
		// if(search.equals(receList.get(i).DisOrderNo)){
		// and_list.setSelection(i);
		// receListAdapter.setSelectItem(i);
		// receListAdapter.notifyDataSetInvalidated();
		// return;
		// }
		// }
		//
		// break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle bundle = new Bundle();
		if (position < receList.size()) {//防止点击footview
			// 日志操作
			LogUtils.logOnFile("入库上架->操作单号：" + receList.get(position).DisOrderNo);
			if (receList.get(position).status != 4) {// 未入库
				bundle.putString("receiveSheetNo", receList.get(position).receiveSheetNo);
				Ex.Activity(mActivity).start(MainShelfAndActivity.class, bundle);
			} else {// 已入库（有差异或无差异）
				bundle.putString("receiveSheetNo", receList.get(position).receiveSheetNo);
				Ex.Activity(mActivity).start(MyViewPage.class, bundle);
			}
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				// 日志操作
				LogUtils.logOnFile("入库上架->刷新成功");
				Ex.Toast(mContext).showLong("刷新成功");
				// receList.clear();
				// receList.addAll(sheetList);
				receListAdapter.notifyDataSetChanged();
				refreshableView.finishRefreshing();
			} else if (msg.what == 2) {
				refreshableView.finishRefreshing();
				// 日志操作
				LogUtils.logOnFile("入库上架->刷新失败");
				Ex.Toast(mContext).showLong("刷新失败");
			}
		}

		;
	};

	@Override
	public void onReceiver(Intent intent) {
		// if(canReceive) {
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// String barcode = new String(code, 0, codelen).toUpperCase().trim();
		// ap.getsoundPool(ap.Sound_sku);
		// for(int i=0;i<receList.size();i++){
		// if(barcode.equals(receList.get(i).DisOrderNo.toUpperCase())){
		// and_list.setSelection(i);
		// receListAdapter.setSelectItem(i);
		// receListAdapter.notifyDataSetInvalidated();
		// return;
		// }
		// if(i == receList.size()-1){
		// Toast.makeText(mContext, "没有此单号",Toast.LENGTH_SHORT).show();
		// }
		// }
		// }
	}

	@Override
	public void fillCode(String code) {
		if (canReceive) {
			ap.getsoundPool(ap.Sound_sku);
			Boolean isHave = true;
			for (int i = 0; i < receList.size(); i++) {
				if (code.equals(receList.get(i).DisOrderNo.toUpperCase())) {
					and_list.setSelection(i);
					receListAdapter.setSelectItem(i);
					receListAdapter.notifyDataSetInvalidated();
					isHave = false;
					return;
				}
			}
			if (isHave) {
				Intent intent = new Intent(ShelfAndInStockActivity.this, HistoryInStockActivity.class);
				intent.putExtra("ddCode", code);
				startActivity(intent);
			}
		}
	}

	private void initListViewFooter() {
		View footerView = LayoutInflater.from(mActivity).inflate(R.layout.listview_footer_view, null);
		load_layout = (LinearLayout) footerView.findViewById(R.id.load_layout);
		progressbar = (ProgressBar) footerView.findViewById(R.id.progressbar);
		load_tip = (TextView) footerView.findViewById(R.id.load_tip);
		and_list.addFooterView(footerView);
	}

	/* 正在加载 */
	private void stateLoading() {
		load_layout.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.VISIBLE);
		load_tip.setText("正在加载更多...");
	}

	/* 正在结束 */
	private void stateLoaded() {
		load_layout.setVisibility(View.GONE);
	}

	/* 没有更多 */
	private void stateUnload() {
		load_layout.setVisibility(View.VISIBLE);
		progressbar.setVisibility(View.GONE);
		load_tip.setText("没有更多了");
	}
}
