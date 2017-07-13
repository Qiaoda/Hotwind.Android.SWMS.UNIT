package cn.jitmarketing.hot.ui.sample;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.RemindSampleAdapter;
import cn.jitmarketing.hot.entity.RemindSampleBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 出样提醒 Created by fgy on 2016/4/11.
 */
public class SampleRemindActivity extends BaseSwipeOperationActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	private static final int WHAT_NET_GET_REMIND_SAMPLE_LIST = 0;
	private static final int WHAT_NET_GET_REMIND_SAMPLE_ONT = 1;

	@ViewInject(R.id.allocation_title)
	private TitleWidget allocation_title;
	@ViewInject(R.id.check_stock_search_edt)
	private ClearEditText check_stock_search_edt;
	@ViewInject(R.id.tv_no_data)
	private TextView tv_no_data;
	@ViewInject(R.id.ll_loadMore)
	private LinearLayout ll_loadMore;
	@ViewInject(R.id.list_sku)
	private ListView list_sku;

	private static final int PAGE_SIZE = 10000;
	private int curPageIndex = 0;
	private boolean isLoading = false;

	private HotApplication ap;
	private String scanSkuCodeStr = "";
	private ArrayList<RemindSampleBean> mList;
	private RemindSampleAdapter adapter;

	@Override
	protected int exInitLayout() {
		return R.layout.activity_sample_remind;
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
		mList = new ArrayList<RemindSampleBean>();
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		allocation_title.setOnLeftClickListner(this);
		check_stock_search_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					hideSoftKeyBoard(mActivity, check_stock_search_edt);
					fillCode(textView.getText().toString());
				}
				return false;
			}
		});
		check_stock_search_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				/*
				 * if (hasFocus) btn_custom_search.setVisibility(View.VISIBLE);
				 * else btn_custom_search.setVisibility(View.GONE);
				 */
			}
		});
		list_sku.setOnScrollListener(onScrollListener);

		adapter = new RemindSampleAdapter(mActivity, mList);
		list_sku.setAdapter(adapter);
		list_sku.setOnItemClickListener(this);
		list_sku.setEmptyView(tv_no_data);

		getList();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mList.clear();
		mList = null;
		adapter = null;
	}

	private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView absListView, int scrollState) {
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// 滚到底部了
			if (totalItemCount == firstVisibleItem + visibleItemCount) {
//				ll_loadMore.setVisibility(View.VISIBLE);

				if (!isLoading) {
//					 getList();
				}
			}
		}
	};

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_REMIND_SAMPLE_LIST:
		case WHAT_NET_GET_REMIND_SAMPLE_ONT:
			return SkuNet.getRemindSampleList(scanSkuCodeStr, curPageIndex, PAGE_SIZE);
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		isLoading = false;
		check_stock_search_edt.setFocusableInTouchMode(true);
		curPageIndex++;
		if (ll_loadMore.isShown())
			ll_loadMore.setVisibility(View.GONE);

		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		try {
			String skuListStr = new JSONArray(mGson.toJson(net.data)).toString();
			List<RemindSampleBean> resultList = mGson.fromJson(skuListStr, new TypeToken<List<RemindSampleBean>>() {
			}.getType());
			if (resultList.size() < PAGE_SIZE) {
				list_sku.setOnScrollListener(null);
			}
			switch (what) {
			case WHAT_NET_GET_REMIND_SAMPLE_LIST:
				mList.addAll(resultList);
				break;
			case WHAT_NET_GET_REMIND_SAMPLE_ONT:
				mList.clear();
				mList.addAll(resultList);
				break;
			}
			adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		isLoading = false;
		Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
	}

	private void getList() {
		isLoading = true;
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetSampleRemind, WHAT_NET_GET_REMIND_SAMPLE_LIST, false, NET_METHOD_POST, false);
		check_stock_search_edt.setFocusable(false);
		check_stock_search_edt.setFocusableInTouchMode(false);
	}

	private void getListForOne() {
		isLoading = true;
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetSampleRemind, WHAT_NET_GET_REMIND_SAMPLE_ONT, true, NET_METHOD_POST, false);
	}

	@Override
	public void fillCode(String code) {
		if (code != null) {
			if (!SkuUtil.isWarehouse(code)) {
				curPageIndex = 0;
				ap.getsoundPool(ap.Sound_sku);
				this.scanSkuCodeStr = code;
				check_stock_search_edt.setText(scanSkuCodeStr);
				getListForOne();
			} else {
				ap.getsoundPool(ap.Sound_location);
			}
		} else {
			ap.getsoundPool(ap.Sound_error);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			finish();
			break;

		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Bundle bd = new Bundle();
		bd.putString("skuCode", mList.get(position).SKUID);
		Ex.Activity(mActivity).start(OutSampleActivity.class, bd);
		HotConstants.SELECTED = position;
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
