package cn.jitmarketing.hot.ui.shelf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CheckStockListAdapter;
import cn.jitmarketing.hot.adapter.ShelfDetailAdapter;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.BeforeReceiveBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ReceiveBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.ChangeValueUtil;
import cn.jitmarketing.hot.util.SkuUtil;

import com.ex.lib.core.ExBaseFragment;
import com.ex.lib.core.ible.ExNetIble;
import com.ex.lib.core.ible.ExReceiverIble;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.Gson;

public class SecondFragment extends ExBaseFragment implements ExNetIble, ExReceiverIble,OnClickListener{
	@ViewInject(R.id.detail_lv)
	ListView detail_lv;
	private static final int WHAT_NET_INFO = 0x10;
	ShelfDetailAdapter adapter;
	private String receiveSheetNo;
	ReceiveBean rb;
	Gson mGson=new Gson();
	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.fragment2;
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this,this);
	}

	@Override
	protected void exInitView(View root) {
		receiveSheetNo = getActivity().getIntent().getStringExtra("receiveSheetNo");
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.stockInDetails, WHAT_NET_INFO,
				NET_METHOD_POST, false);
	}

	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClick(View arg0) {
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
	public Map<String, Object> onStartNetParam(int what) {
		// TODO Auto-generated method stub
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
			if(rb.checkinstockdetailList == null) {
				rb.checkinstockdetailList = new ArrayList<InStockDetail>();
			}
			adapter = new ShelfDetailAdapter(getActivity(), getActivity().getLayoutInflater(), ChangeValueUtil.value(rb.checkinstockdetailList));
			detail_lv.setAdapter(adapter);
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
		
	}

}
