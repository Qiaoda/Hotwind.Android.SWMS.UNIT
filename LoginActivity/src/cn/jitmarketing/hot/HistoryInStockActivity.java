package cn.jitmarketing.hot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jitmarketing.hot.adapter.ReceiveListAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.ReceiveSheet;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.ui.sample.QueryResultFragment;
import cn.jitmarketing.hot.ui.shelf.MainShelfAndActivity;
import cn.jitmarketing.hot.ui.shelf.MyViewPage;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class HistoryInStockActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.shelf_and_stock)
	private TitleWidget shelf_and_stock;
	@ViewInject(R.id.search_edt)
	private ClearEditText clearEditText;
	@ViewInject(R.id.search_list)
	private ListView searcListView;
	private static final int SEARCH_HISTORY_LIST = 1;
	@ViewInject(R.id.search_tip)
	private TextView search_tip;
	@ViewInject(R.id.search_btn)
	private TextView search_btn;

	/* 搜索code */
	private String receiveSheetNo;
	private List<ReceiveSheet> receList = new ArrayList<ReceiveSheet>();
	private ReceiveListAdapter receListAdapter;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
        
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_history_in_stock;
	}

	@Override
	protected void exInitView() {
		shelf_and_stock.setText("入库单查找");
		shelf_and_stock.setOnLeftClickListner(this);
		search_btn.setOnClickListener(this);
		receListAdapter = new ReceiveListAdapter(this, getLayoutInflater(), receList);
		searcListView.setAdapter(receListAdapter);
		clearEditText.setOnEditorActionListener(onEditorActionListener);
		Intent intent=getIntent();
        if (intent.getStringExtra("ddCode")!=null) {
        	clearEditText.setText(intent.getStringExtra("ddCode"));
        	searchRequest();
		}
		searcListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
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
		});
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case SEARCH_HISTORY_LIST:
			return WarehouseNet.rukuSearch(receiveSheetNo);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case SEARCH_HISTORY_LIST:
			// 日志操作
			LogUtils.logOnFile("入库上架->" + "搜索请求失败");
			break;
		}
		Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		// 请求不成功
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			switch (what) {
			case SEARCH_HISTORY_LIST:// 返回数据不成功
				receList.clear();
				receListAdapter.notifyDataSetChanged();
			}
			return;
		}
		// 请求成功
		switch (what) {
		case SEARCH_HISTORY_LIST:
			String stockStr = mGson.toJson(net.data);
			ArrayList<ReceiveSheet> sheetList = mGson.fromJson(stockStr, new TypeToken<List<ReceiveSheet>>() {
			}.getType());
			receList.clear();
			receList.addAll(sheetList);
			receListAdapter.notifyDataSetChanged();
			if (receList.size() == 0) {
				search_tip.setVisibility(View.VISIBLE);
			} else {
				search_tip.setVisibility(View.GONE);
			}
			break;

		}

	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lv_left: 
			mActivity.finish();
			break;
		case R.id.search_btn:
			searchRequest();
			break;
		default:
			break;
		}
	}

	OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				searchRequest();
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
	 * 搜索请求
	 */
	private void searchRequest() {
		receiveSheetNo = clearEditText.getText().toString().trim();
		if (!receiveSheetNo.equals("")) {
			// 先隐藏键盘
			hideSoftKeyBoard(mActivity, clearEditText);
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.stockInSearch, SEARCH_HISTORY_LIST, NET_METHOD_POST, false);
		}else{
			Toast.makeText(mActivity, "请输入单号", Toast.LENGTH_SHORT).show();
		}
	}

}
