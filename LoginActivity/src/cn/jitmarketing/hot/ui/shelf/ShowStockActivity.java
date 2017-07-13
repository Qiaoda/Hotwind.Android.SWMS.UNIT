package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ex.lib.core.utils.Ex;
import com.google.gson.reflect.TypeToken;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.DifferentAdapter;
import cn.jitmarketing.hot.entity.DifferentBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockItem;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.ConstValue;

public class ShowStockActivity extends BaseSwipeBackAcvitiy implements OnClickListener{
	TextView show_tv;
	ListView show_lv;
	TextView show_btn;
	LinearLayout show_layout;
	private static final int WHAT_NET_DIFFERENT = 0x10;
	String currTaskId;
    private ArrayList<DifferentBean> dlist;
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
		return R.layout.activity_show_stock;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return super.exInitReceiver();
	}

	@Override
	protected void exInitView() {
		show_layout=(LinearLayout) findViewById(R.id.show_layout);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),LayoutParams.WRAP_CONTENT);
		show_layout.setLayoutParams(params);
		show_tv = (TextView) findViewById(R.id.show_tv);
		show_lv = (ListView) findViewById(R.id.show_lv);
		show_btn = (TextView) findViewById(R.id.show_btn);
		show_btn.setOnClickListener(this);
		int currCheckStatus = getIntent().getIntExtra("currCheckStatus", 0);
		if (currCheckStatus == 2) {
			show_tv.setText("恭喜您本次初盘数据正确，" + "\n" + "      请点击确认后,  " + "\n"
					+ "      结束本次盘点");
		} else if(currCheckStatus == 3){
			show_tv.setText("本次盘点差异库位");
			show_lv=(ListView) findViewById(R.id.show_lv);
			currTaskId = getIntent().getStringExtra("currTaskId");
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.different, WHAT_NET_DIFFERENT,
					NET_METHOD_POST, false);
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_DIFFERENT:
			return WarehouseNet.getStockdifferent(currTaskId);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_DIFFERENT:
			Ex.Toast(mContext).show("网络不太好，提交失败");
			break;
		}
	}
	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_DIFFERENT:
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).show(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			dlist = mGson.fromJson(stockStr,
					new TypeToken<List<DifferentBean>>() {
					}.getType());
			DifferentAdapter adapter=new DifferentAdapter(getLayoutInflater(), dlist);
			show_lv.setAdapter(adapter);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.show_btn:
			 StockTakingActivity.sooyung=true;
		     this.finish();
			break;
		}
	}
}
