package cn.jitmarketing.hot.ui.shelf;

import java.util.List;
import java.util.Map;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.platformtools.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter;
import cn.jitmarketing.hot.adapter.StockHistoryAdapter;
import cn.jitmarketing.hot.entity.DifferentBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.view.TitleWidget;

public class StockHistoryActivity extends BaseSwipeBackAcvitiy implements OnClickListener {
	@ViewInject(R.id.stock_history_title)
	TitleWidget stock_history_title;
	@ViewInject(R.id.history_list)
	ListView history_list;
	private static final int WHAT_NET_HISTORY = 0x10;
	String TaskID;
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
		// TODO Auto-generated method stub
		return R.layout.stock_history;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exInitView() {
		// TODO Auto-generated method stub
		stock_history_title.setOnLeftClickListner(this);
		stock_history_title.setText("库位差异");
		TaskID=getIntent().getStringExtra("TaskId");
		Log.d("TAG","a"+ TaskID);
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.sHistory, WHAT_NET_HISTORY,
				NET_METHOD_POST, false);
		
	}
	@Override
	public Map<String, String> onStart(int what) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_HISTORY:
			return WarehouseNet.stockHistory(TaskID);
		}
		return null;
	}
	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_HISTORY:
			Ex.Toast(mContext).showLong(R.string.urlError);
			break;
		}
	}
	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_HISTORY:
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			String history=mGson.toJson(net.data);
			List list=mGson.fromJson(history,
					new TypeToken<List<SkuBean>>() {
					}.getType());
			View headView = getLayoutInflater().inflate(R.layout.history_item_layout, null);
			StockHistoryAdapter adapter = new StockHistoryAdapter(getLayoutInflater(),
					list);
			
			history_list.addHeaderView(headView);
			history_list.setAdapter(adapter);
			break;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		}
	}
}
