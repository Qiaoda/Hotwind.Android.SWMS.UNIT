package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CheckStockListAdapter;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.BeforeReceiveBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

public class NewInStockCompleteActivity extends BaseSwipeBackAcvitiy implements OnClickListener{
	@ViewInject(R.id.check_title)
	TitleWidget check_title;
	@ViewInject(R.id.check_list)
	ListView check_list;
	private static final int WHAT_NET_INFO = 0x10;
	private String receiveSheetNo;
	private CheckStockListAdapter listAdapter;
	BeforeReceiveBean rb;
	@Override
	protected void exInitAfter() {
         
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}
   @Override
  protected String[] exInitReceiver() {
	// TODO Auto-generated method stub
	 return null;
}
	@Override
	protected int exInitLayout() {
		return R.layout.activity_in_check;
	}

	@Override
	protected void exInitView() {
		check_title.setText("入库记录");
		check_title.setOnLeftClickListner(this);
		receiveSheetNo = getIntent().getStringExtra("receiveSheetNo");
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.newInfoList, WHAT_NET_INFO,
				NET_METHOD_POST, false);
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
	  public void onError(int what, int e, String message) {
	  	// TODO Auto-generated method stub
	  	switch (what) {
	  	case WHAT_NET_INFO:
	  		Ex.Toast(this).show("你的网速不太好,入库单详细列表拿取失败，请重进");
	  		break;
	  	}
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
				rb = mGson.fromJson(str, BeforeReceiveBean.class);	
				if(rb.checkinstockdetailList==null){
					rb.checkinstockdetailList=new ArrayList<BeforeInStockBean>();
				}
				listAdapter = new CheckStockListAdapter(getLayoutInflater(), SkuUtil.newcbCheck(rb.checkinstockdetailList, rb.detailList));
				check_list.setAdapter(listAdapter);
				break;
			}
		}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		case R.id.check_list:		
			break;
		}
	}

}
