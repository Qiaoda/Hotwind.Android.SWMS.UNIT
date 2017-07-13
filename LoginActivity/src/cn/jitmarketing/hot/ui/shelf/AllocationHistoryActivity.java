package cn.jitmarketing.hot.ui.shelf;

import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.HotConstants.Shelf;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter;
import cn.jitmarketing.hot.entity.AllocationBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

public class AllocationHistoryActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {
	@ViewInject(R.id.allocation_history_title)
	TitleWidget allocation_history_title;
	@ViewInject(R.id.allocation_history_code)
	TextView allocation_history_code;
	@ViewInject(R.id.allcation_history_come)
	TextView allcation_history_come;
	@ViewInject(R.id.allocation_history_time)
	TextView allocation_history_time;
	@ViewInject(R.id.alh_list)
	ListView alh_list;
	@ViewInject(R.id.allocation_history_count)
	TextView allocation_history_count;
	@ViewInject(R.id.allocation_history_remark)
	TextView allocation_history_remark;
	@ViewInject(R.id.allocation_history_type)
	TextView allocation_history_type;
	@ViewInject(R.id.allocation_type)
	TextView allocation_type;
	@ViewInject(R.id.history_type_layout)
	LinearLayout history_type_layout;
	
	private static final int WHAT_NET_GET_ALLOCATION_History = 0x10;
	private String allocationOrderID;
	private AllocationBean allhistory;
	@Override
	protected void exInitAfter() {
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		this.initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_allocation_history;
	}

	@Override
	protected String[] exInitReceiver() {
		return null;
	}

	@Override
	protected void exInitView() {
		allocation_history_title.setOnLeftClickListner(this);
		allocation_history_title.setText("调拨记录");
		allocationOrderID = getIntent().getStringExtra("AllocationOrderID");
		startTask(HotConstants.Global.APP_URL_USER + Shelf.AlloHistory,
				WHAT_NET_GET_ALLOCATION_History, NET_METHOD_POST, false);
		String remark = getIntent().getStringExtra("Remark");
		int OrderType = getIntent().getIntExtra("OrderType", 0);//2为退次类型
		int BackDefectiveType = getIntent().getIntExtra("BackDefectiveType", 0);
		allocation_history_remark.setText(remark);
		if(OrderType == 2) {
			allocation_type.setText("退次");
			history_type_layout.setVisibility(View.VISIBLE);
			if(BackDefectiveType == 1) {
				allocation_history_type.setText("客户退次");
			} else {
				allocation_history_type.setText("门店退次");
			}
		} else if(OrderType == 1){
			allocation_type.setText("调拨");
			history_type_layout.setVisibility(View.GONE);
		} else if(OrderType == 3){
			allocation_type.setText("退仓");
			history_type_layout.setVisibility(View.GONE);
		} else {
			history_type_layout.setVisibility(View.GONE);
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_ALLOCATION_History:
			return WarehouseNet.allHistory(allocationOrderID);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_ALLOCATION_History:
			Ex.Toast(mContext).showLong("获取详情失败，请重试");
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
		case WHAT_NET_GET_ALLOCATION_History :
			String str = mGson.toJson(net.data);
			allhistory = mGson.fromJson(str, AllocationBean.class);
			allocation_history_code.setText(allhistory.allocationOrderCode);
			allcation_history_come.setText(allhistory.targetUnitName);
			allocation_history_time.setText(allhistory.createTime);
			CGYScanSkuAdapter adapter=new CGYScanSkuAdapter(getLayoutInflater(),allhistory.list);
			View headView = getLayoutInflater().inflate(R.layout.sku_shelves_item_layout, null);
			alh_list.addHeaderView(headView);
			alh_list.setAdapter(adapter);
			allocation_history_count.setText("总数为:  "+SkuUtil.getSkuCount(allhistory.list));
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		}
	}
}
