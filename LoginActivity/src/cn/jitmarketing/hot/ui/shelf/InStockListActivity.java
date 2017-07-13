package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.OldReceiveListAdapter;
import cn.jitmarketing.hot.adapter.ReceiveListAdapter;
import cn.jitmarketing.hot.entity.ReceiveBean;
import cn.jitmarketing.hot.entity.ReceiveSheet;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;

/**发货单列表--需要入库*/
public class InStockListActivity extends BaseSwipeBackAcvitiy implements OnClickListener,OnItemClickListener{
	@ViewInject(R.id.reset_postion_title)
	TitleWidget reset_postion_title;
	@ViewInject(R.id.only_list)
	ListView only_list;
	private List<ReceiveSheet> receList = new ArrayList<ReceiveSheet>();
	private static final int WHAT_NET_GET_IN_STOCK_LIST = 0;
	private OldReceiveListAdapter receListAdapter;
	public static boolean stockComplete = false;
    
	@Override  
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_IN_STOCK_LIST:
			return WarehouseNet.getInStockList(1,50);
		}
		return super.onStart(what);
	}
	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_GET_IN_STOCK_LIST:
			stockComplete = false;
			Ex.Toast(this).showLong(R.string.getInfo);
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
			if(null == net.data){
				Ex.Toast(mActivity).showLong(net.message);
				return ;
			}
			String stockStr = mGson.toJson(net.data);
			receList= mGson.fromJson(stockStr,
					new TypeToken<List<ReceiveSheet>>() {
					}.getType());
			receListAdapter = new OldReceiveListAdapter(this, getLayoutInflater(), receList);
			only_list.setAdapter(receListAdapter);
			break;
		}
	}	
	
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_only_list;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exInitView() {
		stockComplete = false;
		reset_postion_title.setOnLeftClickListner(this);
		reset_postion_title.setText("入库");
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SkuStockInList, 
				WHAT_NET_GET_IN_STOCK_LIST, NET_METHOD_POST, false);
		only_list.setOnItemClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(stockComplete)
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SkuStockInList, 
					WHAT_NET_GET_IN_STOCK_LIST, NET_METHOD_POST, false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Bundle bundle = new Bundle();
		if(receList.get(position).status == 1) {
			bundle.putString("SKUQty",""+receList.get(position).skuQty);
			bundle.putString("receiveSheetNo", receList.get(position).receiveSheetNo);
			Ex.Activity(mActivity).start(InStockActivity.class, bundle);
		} else if(receList.get(position).status == 2) {
			bundle.putString("receiveSheetNo", receList.get(position).receiveSheetNo);
//			bundle.putSerializable("detailList", receList.get(position).detailList);
//			bundle.putSerializable("checkinstockdetailList", receList.get(position).checkinstockdetailList);
			Ex.Activity(mActivity).start(InStockCompleteCheckActivity.class, bundle);
		}
	}


}
