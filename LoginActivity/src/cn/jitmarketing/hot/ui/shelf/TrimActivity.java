package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.TrimAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.TrimBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

public class TrimActivity extends BaseSwipeBackAcvitiy implements OnClickListener {
	@ViewInject(R.id.trim_title)
	TitleWidget trim_title;
	@ViewInject(R.id.search_edt)
	ClearEditText searchEdt;
	@ViewInject(R.id.trim_list)
	ListView trim_list;
	private static final int WHAT_NET_Trim = 0x11;
	public static boolean isfinish = false;
	private ArrayList<TrimBean> stockList = new ArrayList<TrimBean>();
	private ArrayList<TrimBean> searchList = new ArrayList<TrimBean>();
	private TrimAdapter adapter;

	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		super.exInitAfter();
	} 

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_trim;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onResume() {
		if (isfinish) {
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.TrimHistory, WHAT_NET_Trim, NET_METHOD_POST, false);
		}
		super.onResume();
	}

	@Override
	protected void exInitView() {
		trim_title.setOnLeftClickListner(this);
		trim_title.setText("调整单");
		trim_title.setOnRightClickListner(this);
		searchEdt.addTextChangedListener(textWatcher);
		searchEdt.setOnEditorActionListener(onEditorActionListener);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.TrimHistory, WHAT_NET_Trim, NET_METHOD_POST, false);
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_Trim:
			return WarehouseNet.trimHistory();
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_Trim:
			isfinish = false;
			Ex.Toast(mContext).showLong("你的网速不太好,获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_Trim:
			isfinish = false;
			if (null == net.data) {
				Ex.Toast(mActivity).show(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			ArrayList<TrimBean> stockLists = mGson.fromJson(stockStr, new TypeToken<List<TrimBean>>() {
			}.getType());
			stockList.addAll(stockLists);
			searchList.clear();
			searchList.addAll(stockList);
			adapter = new TrimAdapter(getLayoutInflater(), stockList);
			trim_list.setAdapter(adapter);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			finish();
			break;
		case R.id.lv_right:
			// 日志操作
			LogUtils.logOnFile("调整单->添加");
			Intent intent = new Intent();
			intent.setClass(mActivity, TrimingActivity.class);
			startActivity(intent);
			break;
		}
	}

	/**
	 * 搜索框文字监听器
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.equals("")) {
				stockList.clear();
				stockList.addAll(searchList);
				adapter.notifyDataSetChanged();
			} else {
				stockList.clear();
				for (int i = 0; i < searchList.size(); i++) {
					if (searchList.get(i).SKUCode.contains(s.toString().toUpperCase())) {
						stockList.add(searchList.get(i));
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
	
	OnEditorActionListener onEditorActionListener=new OnEditorActionListener() {
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId==EditorInfo.IME_ACTION_SEARCH||(event!=null&&event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) {
				// 先隐藏键盘
				hideSoftKeyBoard(mActivity,searchEdt);
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
