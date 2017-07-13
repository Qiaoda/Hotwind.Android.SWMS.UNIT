package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.BeforeReceiveBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.jiebao.scanlib.ScanService;

/** 商品入库 */
public class InStockActivity extends BaseSwipeOperationActivity implements
		OnClickListener {
	private static final int WHAT_NET_IN_STOCK = 0;
	private static final int FOR_CHECK_IN_STOCK = 0x13;
	private static final int WHAT_NET_INFO = 0x12;
	@ViewInject(R.id.shelf_title)
	TitleWidget shelf_title;
	@ViewInject(R.id.text_one)
	TextView text_one;
	@ViewInject(R.id.text_two)
	TextView text_two;
	@ViewInject(R.id.only_list)
	ListView in_stock_list;
	private TextView in_stock_detail;
	private TextView curr_in_stock_info;
	private TextView now_in_stock_info;
	private ArrayList<BeforeInStockBean> skuList;
	private ArrayList<BeforeInStockBean> showList;
	private InStockItemAdapter inStockItemAdapter;
	private String receiveSheetNo;

	private boolean canReceive;
	private HotApplication ap;
	
	BeforeReceiveBean rb;
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void exInitBundle() {
		this.initIble(this, this);

	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_only_list2;
	}

	@Override
	protected void exInitView() {
		ap=(HotApplication) getApplication();
		shelf_title.setText("入库");
		shelf_title.setOnLeftClickListner(this);
		showList = new ArrayList<BeforeInStockBean>();
		skuList = new ArrayList<BeforeInStockBean>();
		String skuqty=getIntent().getStringExtra("SKUQty");
		receiveSheetNo = getIntent().getStringExtra("receiveSheetNo");
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.beforeInfoList, WHAT_NET_INFO,
				NET_METHOD_POST, false);
		inStockItemAdapter = new InStockItemAdapter(getLayoutInflater(),
				showList);
		in_stock_list.addHeaderView(getLayoutInflater().inflate(
				R.layout.horizontal_layout, null));
		View footView = getLayoutInflater().inflate(R.layout.list_foot_layout,
				null);
		in_stock_list.addFooterView(footView);
		in_stock_list.setAdapter(inStockItemAdapter);
		text_one.setOnClickListener(this);
		text_two.setOnClickListener(this);
		curr_in_stock_info = (TextView) footView
				.findViewById(R.id.curr_in_stock_info);
		now_in_stock_info = (TextView) footView
				.findViewById(R.id.now_in_stock_info);
		in_stock_detail = (TextView) footView
				.findViewById(R.id.in_stock_detail);
		in_stock_detail.setOnClickListener(this);
		curr_in_stock_info.setText(skuqty);
		now_in_stock_info.setText("" + 0);
	}

	@Override
	protected void onResume() {
		canReceive = true;
		// 开启扫描服务
//		startService(new Intent(this,ScanService.class));
//		// 绑定服务
//		bindService(new Intent(this,ScanService.class), serviceConnection,Context.BIND_AUTO_CREATE);
		super.onResume();
	}

	@Override
	protected void onStop() {
		canReceive = false;
		super.onStop();
	}

	@Override
	protected void onPause() {
		canReceive = false;
		super.onPause();
	}

	@Override
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		dealBarCode(barcode);
		
		
//		if (canReceive) {
//			byte[] code = intent.getByteArrayExtra("barocode");
//			int codelen = intent.getIntExtra("length", 0);
//			if (null != code) {
//				String barcode = new String(code, 0, codelen).toUpperCase().trim();
//				if (SkuUtil.isWarehouse(barcode)) {
//					ap.getsoundPool(ap.Sound_location);
//				} else {
//					ap.getsoundPool(ap.Sound_sku);
//					BeforeInStockBean ins = new BeforeInStockBean(barcode,1);
//					SkuUtil.hhcheck(rb.checkinstockdetailList, ins);
//					BeforeInStockBean inss = new BeforeInStockBean(barcode,1);
//					SkuUtil.check(skuList, inss);
//					SkuUtil.newShowList(skuList, showList);
//					inStockItemAdapter.notifyDataSetChanged();
//					now_in_stock_info.setText(""
//							+ SkuUtil.getCount(skuList));
//				}
//			}
//		}
	}
	
//	@Override
//	public void fillCode(String code) {
//		dealBarCode(code);
//	}
	
	private void dealBarCode(String barcode) {
		if (canReceive) {
			if (SkuUtil.isWarehouse(barcode)) {
				ap.getsoundPool(ap.Sound_location);
			} else {
				ap.getsoundPool(ap.Sound_sku);
				BeforeInStockBean ins = new BeforeInStockBean(barcode,1);
				SkuUtil.hhcheck(rb.checkinstockdetailList, ins);
				BeforeInStockBean inss = new BeforeInStockBean(barcode,1);
				SkuUtil.check(skuList, inss);
				SkuUtil.newShowList(skuList, showList);
				inStockItemAdapter.notifyDataSetChanged();
				now_in_stock_info.setText(""
						+ SkuUtil.getCount(skuList));
			}
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_IN_STOCK:
			return WarehouseNet.skuStockIn(receiveSheetNo, SkuUtil.beforeCheck(rb.checkinstockdetailList));
		case WHAT_NET_INFO:
			return WarehouseNet.stockInfo(receiveSheetNo);
		}
		return null;
	}
	
	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_IN_STOCK:
			Ex.Toast(this).showLong(R.string.urlError);
			break;
		case WHAT_NET_INFO:
			Ex.Toast(this).showLong(R.string.listgetError);
			break;
		}
	}
	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// TODO Auto-generated method stub
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_IN_STOCK:
			Ex.Toast(mActivity).showLong(net.message);
			InStockListActivity.stockComplete = true;
			this.finish();
			break;
		case WHAT_NET_INFO:
			if(null == net.data){
				Ex.Toast(mActivity).showLong(net.message);
				return ;
			}
			String str = mGson.toJson(net.data);
			rb = mGson.fromJson(str, BeforeReceiveBean.class);	
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
				finish();
			break;
		case R.id.text_two:
			Intent intent = new Intent(mActivity, HandInStockActivity.class);
			startActivityForResult(intent, FOR_CHECK_IN_STOCK);
			//关闭扫描服务
//			if(serviceConnection!=null){
//				unbindService(serviceConnection);
//				stopService(new Intent(this,ScanService.class));
//			}
			break;
		case R.id.in_stock_detail:
			Intent intent1 = new Intent(mActivity, InStockCheckActivity.class);
			intent1.putExtra("sendList", rb.detailList);
			intent1.putExtra("skuList", rb.checkinstockdetailList);
			startActivity(intent1);
			break;
		case R.id.text_one:
			// TODO: 提交入库数据
			new AlertDialog.Builder(this)
			.setTitle(R.string.noteTitle)
			.setMessage("确认提交？")
			.setNegativeButton(R.string.cancelTitle,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
				}
			})
			.setPositiveButton(R.string.sureTitle,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int whichButton) {
			        if(skuList.size()==0){
			        	Ex.Toast(mContext).show("当前没扫描任何数据,不能提交");
			        }else{
			        	startJsonTask(HotConstants.Global.APP_URL_USER
								+ HotConstants.Shelf.SkuStockIn, WHAT_NET_IN_STOCK, true, NET_METHOD_POST, 
								SaveListUtil.instockSave(receiveSheetNo, SkuUtil.beforeCheck(rb.checkinstockdetailList)), false);
					}
				}
			}).show();	
			break;
		}
	}

	public class InStockItemAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<BeforeInStockBean> skuList;

		public InStockItemAdapter(LayoutInflater inflater,
				List<BeforeInStockBean> skuList) {
			this.inflater = inflater;
			this.skuList = skuList;
		}

		@Override
		public int getCount() {
			return skuList.size();
		}

		@Override
		public Object getItem(int position) {

			return skuList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.horizontal_layout, null);
			TextView hor_name = (TextView) convertView
					.findViewById(R.id.hor_name);
			TextView hor_count = (TextView) convertView
					.findViewById(R.id.hor_count);
			BeforeInStockBean item = skuList.get(position);
			hor_name.setText(item.SKUCode);
			hor_count.setText(""+item.SKUCount);
			return convertView;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == FOR_CHECK_IN_STOCK) {
			String sku=data.getBundleExtra("bundle").getString("sku");
			String stringcount=data.getBundleExtra("bundle").getString("stringcount");
			int count=Integer.valueOf(stringcount);
			BeforeInStockBean bs = new BeforeInStockBean(sku,count);
			SkuUtil.hhcheck(rb.checkinstockdetailList, bs);
			BeforeInStockBean bss = new BeforeInStockBean(sku,count);
			SkuUtil.check(skuList, bss);
			SkuUtil.newShowList(skuList, showList);
			inStockItemAdapter.notifyDataSetChanged();
			now_in_stock_info.setText("" + SkuUtil.getCount(skuList));
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
	}

}
