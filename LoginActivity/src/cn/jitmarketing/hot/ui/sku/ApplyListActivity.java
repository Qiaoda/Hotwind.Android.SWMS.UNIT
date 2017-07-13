package cn.jitmarketing.hot.ui.sku;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ApplyListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ApplyEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.AutoListView;
import cn.jitmarketing.hot.view.AutoListView.OnLoadListener;
import cn.jitmarketing.hot.view.AutoListView.OnRefreshListener;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

/** 申请列表 */
public class ApplyListActivity extends BaseSwipeBackAcvitiy implements OnClickListener, OnRefreshListener, OnLoadListener {

	@ViewInject(R.id.detail_title)
	TitleWidget detail_title;
	// -----------------------搜索框布局
	@ViewInject(R.id.search_layout)
	LinearLayout searchLayout;
	// 搜索输入框
	@ViewInject(R.id.check_stock_search_edt)
	cn.jitmarketing.hot.view.ClearEditText searchClearEditText;
	// 搜索取消按钮
	@ViewInject(R.id.check_storck_cancel)
	TextView searchCancle;
	@ViewInject(R.id.only_list)
	AutoListView only_list;
	@ViewInject(R.id.text_tips)
	TextView text_tips;

	private List<ApplyEntity> applyList = new ArrayList<ApplyEntity>();
	private List<ApplyEntity> searchList = new ArrayList<ApplyEntity>();
	private List<ApplyEntity> newapply;
	private static final int WHAT_NET_GET_APPLY_LIST = 0x10;
	private static final int WHAT_NET_GET_APPLY_CANCEL = 0x11;
	private ApplyListAdapter applyListAdapter;
	private ApplyEntity cancelItem;
	private int requestcount;
	private int notFindcount;
	private int needResetcount;
	private int page = 0;
	private static final int pageSize = 10;
	private boolean isRefresh = false;// 是否刷新
	private int DELYED = 60 * 1000;
	int requestCount;
	int notFindCount;
	int needResetCount;

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_apply_list;
	}

	@Override
	protected void exInitView() {
		detail_title.setOnLeftClickListner(this);
		detail_title.setText("申请列表");
		detail_title.setOnRightClickListner(this);
		searchCancle.setOnClickListener(this);
		searchClearEditText.addTextChangedListener(textWatcher);
		searchClearEditText.setOnEditorActionListener(onEditorActionListener);
		applyListAdapter = new ApplyListAdapter(this, applyList);
		only_list.setAdapter(applyListAdapter);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ApplyList, WHAT_NET_GET_APPLY_LIST, NET_METHOD_POST, false);
		only_list.setOnRefreshListener(this);
		only_list.setOnLoadListener(this);
		only_list.setPageSize(pageSize);
		only_list.onLoadComplete();
		handler1.postDelayed(runnable, DELYED);
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
				if (!net.isSuccess) {
					Ex.Toast(mContext).showLong(net.message);
					return;
				}
				String stockStr = mGson.toJson(net.data);
				try {
					JSONObject js = new JSONObject(stockStr);
					requestcount = js.getInt("RequestCount");
					notFindcount = js.getInt("NotFindCount");
					needResetcount = js.getInt("NeedResetCount");
					String getList = js.getString("List");
					newapply = mGson.fromJson(getList, new TypeToken<List<ApplyEntity>>() {
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

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_APPLY_LIST:
			Ex.Toast(mContext).showLong("你的网速不太好，申请失败，请稍后重试");
			break;
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_APPLY_LIST:
			return WarehouseNet.getGoodsList(page, pageSize);
		case WHAT_NET_GET_APPLY_CANCEL:
			return WarehouseNet.getApplyCancel(cancelItem.FactID);
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_GET_APPLY_LIST:
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			try {
				String stockStr = mGson.toJson(net.data);
				JSONObject js = new JSONObject(stockStr);
				requestCount = js.getInt("RequestCount");
				notFindCount = js.getInt("NotFindCount");
				needResetCount = js.getInt("NeedResetCount");
				String getList = js.getString("List");
				List<ApplyEntity> apply = mGson.fromJson(getList, new TypeToken<List<ApplyEntity>>() {
				}.getType());
				applyList.clear();
				applyList.addAll(apply);
				resetSearchList();
				applyListAdapter.notifyDataSetChanged();
				only_list.setResultSize(apply.size());
				text_tips.setVisibility(View.VISIBLE);
				String source = "今日共申请<font color='green'>" + requestCount + "</font>件，待复位<font color='red'>" + needResetCount + "</font>件，未找到<font color='red'>" + notFindCount + "</font>件。";
				text_tips.setText(Html.fromHtml(source));
				page++;
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case WHAT_NET_GET_APPLY_CANCEL:
			Ex.Toast(mContext).show("取消成功");
			if (cancelItem != null) {
				applyList.remove(cancelItem);
				applyListAdapter.notifyDataSetChanged();
				cancelItem = null;
				requestCount--;
				String source = "今日共申请<font color='green'>" + requestCount + "</font>件，待复位<font color='red'>" + needResetCount + "</font>件，未找到<font color='red'>" + notFindCount + "</font>件。";
				text_tips.setText(Html.fromHtml(source));
			}
			// startTask(HotConstants.Global.APP_URL_USER +
			// HotConstants.Shelf.ApplyList,
			// WHAT_NET_GET_APPLY_LIST, NET_METHOD_POST, false);
			break;
		}
	}

	public void applayCancel(ApplyEntity item) {
		page = 0;
		this.cancelItem = item;
		// 日志操作
		LogUtils.logOnFile("取消" + item.SKUID);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.APPLY_CANCEL, WHAT_NET_GET_APPLY_CANCEL, NET_METHOD_POST, false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left: 
			this.finish();
			break;
		case R.id.lv_right: 
			showSearchLayout();
			break;
		case R.id.check_storck_cancel:
			hideSearchLayout();
			applyList.clear();
			applyList.addAll(searchList);
			applyListAdapter.notifyDataSetChanged();
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				text_tips.setVisibility(View.VISIBLE);
				String source = "今日共申请<font color='green'>" + requestcount + "</font>件，待复位<font color='red'>" + needResetcount + "</font>件，未找到<font color='red'>" + notFindcount + "</font>件。";
				text_tips.setText(Html.fromHtml(source));
				only_list.setResultSize(newapply.size());
				if (isRefresh) {
					page = 1;
					applyList.clear();
					only_list.onRefreshComplete();
					// 日志操作
					LogUtils.logOnFile("刷新列表");
					Ex.Toast(mContext).showLong("刷新成功");
				} else {
					page++;
					only_list.onLoadComplete();
				}
				applyList.addAll(newapply);
				resetSearchList();
				applyListAdapter.notifyDataSetChanged();
			} else if (msg.what == 2) {
				if (isRefresh) {
					only_list.onRefreshComplete();
					Ex.Toast(mContext).showLong("刷新失败");
				} else {
					only_list.onLoadComplete();
					Ex.Toast(mContext).showLong("加载失败");
				}
			}
		}
	};

	@Override
	public void onLoad() {
		only_list.onRefreshComplete();
		isRefresh = false;
		Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ApplyList, WarehouseNet.getGoodsList(page, pageSize), requestCallback);
	}

	@Override
	public void onRefresh() {
		only_list.onLoadComplete();
		isRefresh = true;
		page = 0;
		Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ApplyList, WarehouseNet.getGoodsList(0, pageSize), requestCallback);
	}

	@Override
	protected void onDestroy() {
		handler1.removeCallbacks(runnable);// 停止定时器
		super.onDestroy();
	}

	Handler handler1 = new Handler();

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// handler自带方法实现定时器
			try {
				handler1.postDelayed(this, DELYED);
				if (applyListAdapter != null) {
					if (applyList != null && applyList.size() > 0) {
						for (ApplyEntity item : applyList) {
							if (item.TransactionStatus == 1) {
								item.TimeOut = String.valueOf(Long.valueOf(item.TimeOut) + 60);
							} else if (item.TransactionStatus == 2) {
								if (item.OpID.equals("1024") || item.OpID.equals("2048")) {
									item.TimeOut = String.valueOf(Long.valueOf(item.TimeOut) + 60);
								}
							}
						}
						applyListAdapter.notifyDataSetChanged();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("exception...");
			}
		}
	};
	
	/**
	 * show搜索框
	 */
	private void showSearchLayout() {
		detail_title.setVisibility(View.GONE);
		searchLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * hide搜索框
	 */
	private void hideSearchLayout() {
		detail_title.setVisibility(View.VISIBLE);
		searchLayout.setVisibility(View.GONE);
	}

	/**
	 * 搜索框输入监听
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub 
			if (s!=null) {
				applyList.clear();
				for (int i = 0; i < searchList.size(); i++) {
					if (searchList.get(i).SKUID.contains(s.toString().toUpperCase())) {
						applyList.add(searchList.get(i));
					}
				}
				applyListAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}};
		OnEditorActionListener onEditorActionListener=new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId==EditorInfo.IME_ACTION_SEARCH||(event!=null&&event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) {
					// 先隐藏键盘
					hideSoftKeyBoard(mActivity,searchClearEditText);
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
		 * 重置搜索数据
		 */
		private void resetSearchList() {
			searchList.clear();
			searchList.addAll(applyList);
		}

}
