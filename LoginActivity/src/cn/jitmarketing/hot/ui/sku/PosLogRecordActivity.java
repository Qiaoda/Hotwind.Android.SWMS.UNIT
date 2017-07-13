package cn.jitmarketing.hot.ui.sku;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.PosRecordListAdapter;
import cn.jitmarketing.hot.entity.PosRecordEntity;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.view.AutoListView;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;
import cn.jitmarketing.hot.view.AutoListView.OnLoadListener;
import cn.jitmarketing.hot.view.AutoListView.OnRefreshListener;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

/**
 * 待处理
 * 
 * @author hotwind
 * 
 */
public class PosLogRecordActivity extends BaseSwipeBackAcvitiy implements OnClickListener, OnItemClickListener, OnRefreshListener, OnLoadListener {

	@ViewInject(R.id.pos_record_title)
	TitleWidget pos_record_title;
	@ViewInject(R.id.pos_record_list)
	AutoListView only_list;
	@ViewInject(R.id.pos_search_edt)
	ClearEditText pos_search_edt; 
	@ViewInject(R.id.search_listview)
	ListView search_listview;

	private static final int WHAT_NET_GET_RECORD_LIST = 0x10;
	private static final int WHAT_NET_SEARCH_RECORD_LIST = 0x11;
	private List<PosRecordEntity> posList = new ArrayList<PosRecordEntity>();
	private List<PosRecordEntity> searchList = new ArrayList<PosRecordEntity>();
	private PosRecordListAdapter recordAdapter;
	private PosRecordListAdapter searchAdapter;
	private static final int pageSize = 10;
	private int page = 0;
	private List<PosRecordEntity> newPosRecord;
	private boolean isRefresh = false;// 是否刷新

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_pos_record;
	}

	@Override
	protected void exInitView() {
		pos_search_edt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				if (TextUtils.isEmpty(s) || s.toString().trim().equals("")) {
					only_list.setVisibility(View.VISIBLE);
					search_listview.setVisibility(View.GONE);
					searchList.clear();
					searchAdapter.notifyDataSetChanged();
				} else {
					searchList.clear();
					only_list.setVisibility(View.GONE);
					search_listview.setVisibility(View.VISIBLE);
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SearchPosLogRecord, WHAT_NET_SEARCH_RECORD_LIST, false, NET_METHOD_POST, false);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		pos_search_edt.setOnKeyListener(new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {// 修改回车键功能
							// 隐藏键盘
							hideSoftKeyBoard(mActivity, pos_search_edt);
							if (pos_search_edt.getText() != null && !pos_search_edt.getText().toString().equals("")) {
								startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SearchPosLogRecord, WHAT_NET_SEARCH_RECORD_LIST, false, NET_METHOD_POST, false);
							}
						}
						return false;
					}
				});
		searchAdapter = new PosRecordListAdapter(this, searchList);
		search_listview.setAdapter(searchAdapter);
		pos_record_title.setOnLeftClickListner(this);
		recordAdapter = new PosRecordListAdapter(this, posList);
		only_list.setAdapter(recordAdapter);
		only_list.setOnRefreshListener(this);
		only_list.setOnLoadListener(this);
		only_list.setPageSize(pageSize);
		only_list.onLoadComplete();
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.GetPosLogRecord, WHAT_NET_GET_RECORD_LIST, NET_METHOD_POST, false);
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 1) {
				if (isRefresh) {
					page = 1;
					posList.clear();
					only_list.onRefreshComplete();
					Ex.Toast(mContext).showLong("刷新成功");
				} else {
					only_list.setResultSize(newPosRecord.size());
					page++;
					only_list.onLoadComplete();
				}
				posList.addAll(newPosRecord);
				recordAdapter.notifyDataSetChanged();
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
				String str = mGson.toJson(net.data);
				newPosRecord = mGson.fromJson(str, new TypeToken<List<PosRecordEntity>>() {
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
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_RECORD_LIST:
			Ex.Toast(mActivity).show("你的网速不太好，申请失败，请稍后重试");
			break;
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_RECORD_LIST:
			return WarehouseNet.posRecordParams(page, pageSize);
		case WHAT_NET_SEARCH_RECORD_LIST:
			return WarehouseNet.searchPosRecord(pos_search_edt.getText().toString(), pos_search_edt.getText().toString());
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_GET_RECORD_LIST:
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			List<PosRecordEntity> posRecord = mGson.fromJson(str, new TypeToken<List<PosRecordEntity>>() {
			}.getType());
			only_list.setResultSize(posRecord.size());
			posList.clear();
			posList.addAll(posRecord);
			recordAdapter.notifyDataSetChanged();
			page++;
			break;
		case WHAT_NET_SEARCH_RECORD_LIST:
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			String str1 = mGson.toJson(net.data);
			List<PosRecordEntity> posRecord1 = mGson.fromJson(str1, new TypeToken<List<PosRecordEntity>>() {
			}.getType());
			searchList.clear();
			searchList.addAll(posRecord1);
			searchAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			hideSoftKeyBoard(this, pos_search_edt);
			this.finish();
			break;
		}
	}

	@Override
	public void onLoad() {
		only_list.onRefreshComplete();
		isRefresh = false;
		Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.GetPosLogRecord, WarehouseNet.posRecordParams(page, pageSize), requestCallback);
	}

	@Override
	public void onRefresh() {
		only_list.onLoadComplete();
		isRefresh = true;
		page = 0;
		Ex.Net(mContext).sendAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.GetPosLogRecord, WarehouseNet.posRecordParams(page, pageSize), requestCallback);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}

	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}