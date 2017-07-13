package cn.jitmarketing.hot.ui.shelf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CheckStockListAdapter;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.BeforeReceiveBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ReceiveBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.ExBaseFragment;
import com.ex.lib.core.ible.ExNetIble;
import com.ex.lib.core.ible.ExReceiverIble;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.Gson;

public class FirstFragment extends ExBaseFragment implements ExNetIble, ExReceiverIble,OnClickListener{
	@ViewInject(R.id.check_list)
	ListView check_list;
	Gson mGson=new Gson();
	private static final int WHAT_NET_INFO = 0x10;
	private String receiveSheetNo;
	private CheckStockListAdapter listAdapter;
	ReceiveBean rb;
	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.fragment1;
	}
	@Override
	protected void exInitView(View root) {
		receiveSheetNo = getActivity().getIntent().getStringExtra("receiveSheetNo");
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.stockInDetails, WHAT_NET_INFO,
				NET_METHOD_POST, false);
	}
	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this,this);
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onReceiver(Intent intent) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_INFO:
			return WarehouseNet.stockInfo(receiveSheetNo);
		}
		return null;
	}

	  @Override
			public void onSuccess(int what, String result, boolean hashCache) {
				// TODO Auto-generated method stub
				ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
				if (!net.isSuccess) {
					Ex.Toast(mContext).show(net.message);
					return;
				}
				switch (what) {
				case WHAT_NET_INFO:
					if(null == net.data){
						Ex.Toast(mActivity).show(net.message);
						return ;
					}
					String str = mGson.toJson(net.data);
					rb = mGson.fromJson(str, ReceiveBean.class);
					if(rb.checkinstockdetailList == null)
						rb.checkinstockdetailList = new ArrayList<InStockDetail>();
					listAdapter = new CheckStockListAdapter(getActivity().getLayoutInflater(), SkuUtil.cbCheck(rb.checkinstockdetailList, rb.detailList));
					check_list.setAdapter(listAdapter);
					break;
				}
			}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.lv_left:
				getActivity().finish();
				break;
			case R.id.check_list:		
				break;
			}
		}
	@Override
	public void onSuccess(int what, InputStream result,
			HashMap<String, String> cookies, boolean hashCache) {
		// TODO Auto-generated method stub
		
	}
	 @Override
	  public void onError(int what, int e, String message) {
	  	// TODO Auto-generated method stub
	  	switch (what) {
	  	case WHAT_NET_INFO:
	  		Ex.Toast(getActivity()).show("你的网速不太好,入库单详细列表拿取失败，请重进");
	  		break;
	  	}
	  }

	@Override
	public Map<String, Object> onStartNetParam(int what) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
