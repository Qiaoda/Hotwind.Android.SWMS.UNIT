package cn.jitmarketing.hot.ui.shelf;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.ex.lib.core.utils.Ex;
import com.google.gson.reflect.TypeToken;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingNoticeEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.pandian.StockTakingShopperActivity;
import cn.jitmarketing.hot.util.LogUtils;

public class CreatStockTaskActivity extends BaseSwipeBackAcvitiy implements OnClickListener {

	private static final int WHAT_NET_CREATE_OK = 0x11;
	private static final int WHAT_NET_NEW_OK = 0x12;

	private Spinner create_spinner;
	private TextView create_count;
	private TextView create_new_sure;
	private TextView create_new_cancel;
	private List<StockTakingNoticeEntity> infoList;
	private StockTakingNoticeEntity selectStockTaking;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.create_stock_task;
	}

	@Override
	protected void exInitView() {
		create_spinner = (Spinner) findViewById(R.id.create_spinner);
		create_count = (TextView) findViewById(R.id.create_count);
		create_new_sure = (TextView) findViewById(R.id.create_new_sure);	
		create_new_cancel = (TextView) findViewById(R.id.create_new_cancel);
		create_new_sure.setOnClickListener(this);
		create_new_cancel.setOnClickListener(this);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.getStockList, WHAT_NET_CREATE_OK, NET_METHOD_POST, false);
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_CREATE_OK:
			return WarehouseNet.getStockinfoList();
		case WHAT_NET_NEW_OK:
			return WarehouseNet.newOk(selectStockTaking.CheckNoticeID);
		}

		return super.onStart(what);
	}

	@Override
	public void onError(int what, int e, String message) {
		// 操作日志
		LogUtils.logOnFile("任务盘点->添加" + message);
		switch (what) {
		case WHAT_NET_CREATE_OK:
			Ex.Toast(mContext).showLong("你的网速不太好，获取盘点列表失败");
			break;
		case WHAT_NET_NEW_OK:
			Ex.Toast(mContext).showLong("你的网速不太好，创建失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// 操作日志
		LogUtils.logOnFile("任务盘点->添加" + result);
		switch (what) {
		case WHAT_NET_CREATE_OK:
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).showLong(net.message);
				return;
			}
			String spinnerStr = mGson.toJson(net.data);
			try {
				JSONObject js = new JSONObject(spinnerStr);
				String getList = js.getString("List");
				infoList = mGson.fromJson(getList, new TypeToken<List<StockTakingNoticeEntity>>() {
				}.getType());
				int shelfLocationCount = js.getInt("ShelfLocationCount");
				create_count.setText(String.valueOf(shelfLocationCount));
				if (infoList.size() > 0) {
					create_new_sure.setEnabled(true);
					create_new_sure.setClickable(true);
					String[] strArray = new String[infoList.size()];
					for (int i = 0; i < infoList.size(); i++) {
						strArray[i] = infoList.get(i).NoticenName;
					}
					ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, strArray);
					adapter.setDropDownViewResource(R.layout.spinner_item_layout);
					create_spinner.setAdapter(adapter);
					selectStockTaking = infoList.get(0);
				} else {
					create_new_sure.setEnabled(false);
					create_new_sure.setClickable(false);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case WHAT_NET_NEW_OK:
			ResultNet<?> net1 = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net1.isSuccess) {
				Ex.Toast(mContext).show(net1.message);
				return;
			}
			StockTakingShopperActivity.refresh = true;
			this.finish();
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.create_new_sure:
			if (infoList.size() == 0) {
				Ex.Toast(mContext).show("无盘点通知，不能创建");
			} else {
				selectStockTaking = infoList.get(create_spinner.getSelectedItemPosition());
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.NewOk, WHAT_NET_NEW_OK, NET_METHOD_POST, false);
			}
			// 日志操作
			LogUtils.logOnFile("盘点任务->添加->创建->通知：" + selectStockTaking.NoticenName);
			break;
		case R.id.create_new_cancel:
			// 日志操作
			LogUtils.logOnFile("盘点任务->添加->取消");
			this.finish();
			break;
		}
	}

}
