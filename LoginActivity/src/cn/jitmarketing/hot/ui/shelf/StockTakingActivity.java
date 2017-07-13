package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.helper.StringUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.color;
import cn.jitmarketing.hot.adapter.CGYStockAdapter;
import cn.jitmarketing.hot.entity.CGYBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.entity.StockItem;
import cn.jitmarketing.hot.entity.StockResultBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingActivity extends BaseSwipeOperationActivity implements
		OnClickListener, OnItemClickListener {

	private static final int WHAT_NET_CHECK_STOCK = 0x10;
	private static final int WHAT_NET_stock_sure = 0x11;
	private static final int WHAT_NET_stock_finish = 0x12;
	private static final int WHAT_NET_CREATE_OK = 0x13;
	private static final int WHAT_NET_STOCK_CGY = 0x14;
	private static final int FOR_CHECK_IN_STOCK = 0x15;
	private static final int FOR_SHOW_STOCK = 0x16;
	@ViewInject(R.id.stock_taking_title)
	TitleWidget stock_taking_title;
	@ViewInject(R.id.stock_taking_cgy_title)
	TitleWidget stock_taking_cgy_title;
	@ViewInject(R.id.create_stock)
	Button create_stock;
	
	ListView stock_list;
	ListView stock_list_cgy;
	private ArrayList<StockItem> stockList;
	private ArrayList<StockItem> returnList;
	private ArrayList<CGYBean> cgyList;
	private String userID;
	private StockTakingListAdapter adapter;
	private String id;
	private int currCheckStatus;
	private boolean canReceive;
	private boolean cgyReceive;
	HotApplication ap;
	public static boolean hyomin=false;
	public static boolean sooyung=false;
	public static boolean enjung=false;
	
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
		userID = HotApplication.getInstance().getUserId();
		if (userID.equals("1")) {
			return R.layout.stock_taking;
		} else {
			return R.layout.stock_taking_cgy;
		}
	}


	@Override
	protected void exInitView() {
		ap=(HotApplication) getApplication();
		returnList = new ArrayList<StockItem>();
	 	canReceive=true;
  		if (userID.equals("1")) {
  			cgyReceive=false;
  			stock_list = (ListView) findViewById(R.id.stock_list);
  			startTask(HotConstants.Global.APP_URL_USER
  					+ HotConstants.Shelf.PlanStock, WHAT_NET_CHECK_STOCK,
  					NET_METHOD_POST, false);
  			stock_taking_title.setOnLeftClickListner(this);
  			stock_taking_title.setText("库位盘点");
  		} else {
  			cgyReceive=true;
  			stock_taking_cgy_title.setText("库位盘点");
  			stock_taking_cgy_title = (TitleWidget) findViewById(R.id.stock_taking_cgy_title);
  			stock_list_cgy = (ListView) findViewById(R.id.stock_list_cgy);
  			stock_taking_cgy_title.setOnLeftClickListner(new OnClickListener() {	
  				@Override
  				public void onClick(View v) {
  					StockTakingActivity.this.finish();					
  				}
  			});
  			startTask(HotConstants.Global.APP_URL_USER
  					+ HotConstants.Shelf.getCheckStockList, WHAT_NET_STOCK_CGY,
  					NET_METHOD_POST, false);
  		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		canReceive=false;
		super.onPause();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		canReceive=false;
		super.onStop();
	}
      @Override
    protected void onResume() {
    	  if(hyomin){
    		  startTask(HotConstants.Global.APP_URL_USER
    					+ HotConstants.Shelf.PlanStock, WHAT_NET_CHECK_STOCK,
    					NET_METHOD_POST, false);
    	  }
    	  if(sooyung){
    		  startTask(HotConstants.Global.APP_URL_USER
  					+ HotConstants.Shelf.PlanStock, WHAT_NET_CHECK_STOCK,
  					NET_METHOD_POST, false);
    	  }
    	  if(enjung){
    			startTask(HotConstants.Global.APP_URL_USER
      					+ HotConstants.Shelf.getCheckStockList, WHAT_NET_STOCK_CGY,
      					NET_METHOD_POST, false);
    	  }
    	super.onResume();
    }
      
      @Override
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		dealBarCode(barcode);
		
		
		
//		if (canReceive == true && cgyReceive == true) {
//			byte[] code = intent.getByteArrayExtra("barocode");
//			int codelen = intent.getIntExtra("length", 0);
//			if (null != code) {
//				String barcode = new String(code, 0, codelen).toUpperCase()
//						.trim();
//				if (SkuUtil.isWarehouse(barcode)) {
//					for (int i = 0; i < cgyList.size(); i++) {
//						if (barcode.equals(cgyList.get(i).ShelfLocationCode)) {
//							ap.getsoundPool(ap.Sound_location);
//							// ArrayList<SkuBean> sList=cgyList.get(i).SKUList;
//							Bundle bundle = new Bundle();
//							bundle.putString("ShelfLocationCode", barcode);
//							bundle.putString("ChecktaskShelflocationID",
//									cgyList.get(i).ChecktaskShelflocationID);
//							// bundle.putSerializable("list", sList);
//							Intent intent2 = new Intent();
//							intent2.putExtra("bundle", bundle);
//							intent2.setClass(this, CGYStockActivity.class);
//							startActivity(intent2);
//						}
//					}
//				} else {
//					ap.getsoundPool(ap.Sound_error);
//				}
//			}
//		}
	}
      
//    @Override
//  	public void fillCode(String code) {
//  		dealBarCode(code);
//  	}
    
    private void dealBarCode(String barcode) {
		if (canReceive == true && cgyReceive == true) {
			if (SkuUtil.isWarehouse(barcode)) {
				for (int i = 0; i < cgyList.size(); i++) {
					if (barcode.equals(cgyList.get(i).ShelfLocationCode)) {
						ap.getsoundPool(ap.Sound_location);
						// ArrayList<SkuBean> sList=cgyList.get(i).SKUList;
						Bundle bundle = new Bundle();
						bundle.putString("ShelfLocationCode", barcode);
						bundle.putString("ChecktaskShelflocationID",
								cgyList.get(i).ChecktaskShelflocationID);
						// bundle.putSerializable("list", sList);
						Intent intent2 = new Intent();
						intent2.putExtra("bundle", bundle);
						intent2.setClass(this, CGYStockActivity.class);
						startActivity(intent2);
					}
				}
			} else {
				ap.getsoundPool(ap.Sound_error);
			}
		}
    }
      
	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_CHECK_STOCK:
			return WarehouseNet.getCheckStockList();
		case WHAT_NET_stock_sure:
			return WarehouseNet.suring(
					HotApplication.getInstance().getUserId(), id, "2");
		case WHAT_NET_stock_finish:
			return WarehouseNet.suring(
					HotApplication.getInstance().getUserId(), id, "3");
		case WHAT_NET_STOCK_CGY:
			return WarehouseNet.getCheckStockList();
		}
		return null;
	}
	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_CHECK_STOCK:
			hyomin=false;
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		case WHAT_NET_STOCK_CGY:
			enjung=false;
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		case WHAT_NET_stock_sure:
			sooyung=false;
			Ex.Toast(this).show("你的网速不太好，初盘确认失败");
			break;
		case WHAT_NET_stock_finish:
			Ex.Toast(this).show("你的网速不太好，盘点结束失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_CHECK_STOCK:
			hyomin=false;
			boolean o = false;
			if (null == net.data) {
				Ex.Toast(mActivity).show(net.message);
				return;
			}
			if(userID.equals("1")){
			String stockStr = mGson.toJson(net.data);
			stockList = mGson.fromJson(stockStr,
					new TypeToken<List<StockItem>>() {
					}.getType());
			adapter = new StockTakingListAdapter(getLayoutInflater(), stockList);
			stock_list.setAdapter(adapter);
			stock_list.setOnItemClickListener(this);
			for (int i = 0; i < stockList.size(); i++) {
					if (stockList.get(i).TaskStatus != 3) {
						o = true;
					}
			}
			if (o == true) {
				create_stock.setOnClickListener(this);
				create_stock.setClickable(false);
				create_stock.setBackgroundResource(color.color_gray_back);
			} else {
				create_stock.setOnClickListener(this);
				create_stock.setClickable(true);
				create_stock.setBackgroundResource(color.color_green);
			}
			}
			break;
		case WHAT_NET_stock_sure:
			sooyung=false;
			ResultNet<?> net4 = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).show(net.message);
				return;
			}
			String stockStra = mGson.toJson(net.data);
			StockResultBean curr = mGson.fromJson(stockStra,
					new TypeToken<StockResultBean>() {
					}.getType());
			int index=curr.CheckStatus;
			String currTaskId=curr.TaskID;
			Intent intent1=new Intent();
			intent1.putExtra("currCheckStatus", index);
			intent1.putExtra("currTaskId", currTaskId);
			intent1.setClass(StockTakingActivity.this, ShowStockActivity.class);
			startActivity(intent1);
			break;
		case WHAT_NET_stock_finish:
			ResultNet<?> net5 = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).show(net.message);
				return;
			}
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.PlanStock, WHAT_NET_CHECK_STOCK,
					NET_METHOD_POST, false);
			break;
		case WHAT_NET_STOCK_CGY:
			enjung=false;
			if (null == net.data) {
				Ex.Toast(mActivity).show(net.message);
				return;
			}
			if(userID.equals("2")){
			String cgyStr = mGson.toJson(net.data);
			cgyList = mGson.fromJson(cgyStr, new TypeToken<List<CGYBean>>() {
			}.getType());
			CGYStockAdapter adapter = new CGYStockAdapter(getLayoutInflater(),
					cgyList);
			stock_list_cgy.setAdapter(adapter);
			stock_list_cgy.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					// TODO Auto-generated method stub
					String ShelfLocationCode = cgyList.get(position).ShelfLocationCode;
					String ChecktaskShelflocationID = cgyList.get(position).ChecktaskShelflocationID;
//					ArrayList<SkuBean> skucgyList = cgyList.get(position).SKUList;
					Bundle bundle = new Bundle();
					bundle.putString("ShelfLocationCode", ShelfLocationCode);
					bundle.putString("ChecktaskShelflocationID",
							ChecktaskShelflocationID);
//					bundle.putSerializable("list", skucgyList);
					Intent intent = new Intent();
					intent.putExtra("bundle", bundle);
					intent.setClass(StockTakingActivity.this,
							CGYStockActivity.class);
					startActivity(intent);
				}
			});
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		case R.id.create_stock:
			Intent intent = new Intent();
			intent.setClass(StockTakingActivity.this,
					CreatStockTaskActivity.class);
			startActivity(intent);
			break;
		}
	}

	private class StockTakingListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<StockItem> stocklList;

		public StockTakingListAdapter(LayoutInflater inflater,
				List<StockItem> stocklList) {
			this.inflater = inflater;
			this.stocklList = stocklList;
		}

		@Override
		public int getCount() {
			return stocklList.size();
		}

		@Override
		public Object getItem(int position) {

			return stocklList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			boolean r = false;
			boolean l = false;
			if (stocklList.get(position).TaskStatus != 3) {
				convertView = inflater
						.inflate(R.layout.stock_taking_item, null);
				TextView stock_name = (TextView) convertView
						.findViewById(R.id.stock_name);
				TextView stock_time = (TextView) convertView
						.findViewById(R.id.stock_time);
				Button stock_sure = (Button) convertView
						.findViewById(R.id.stock_sure);
				Button stock_finish = (Button) convertView
						.findViewById(R.id.stock_finish);
				stock_name.setText(stocklList.get(position).CheckName);
				stock_time.setText("盘点时间:  " + stocklList.get(position).CheckTime);
				stock_finish.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						new AlertDialog.Builder(mActivity)
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
								id = stocklList.get(position).CheckTastID;
								startTask(HotConstants.Global.APP_URL_USER
										+ HotConstants.Shelf.suring,
										WHAT_NET_stock_finish, NET_METHOD_POST, false);
							}
						}).show();
					}
				});
				stock_sure.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						new AlertDialog.Builder(mActivity)
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
								id = stocklList.get(position).CheckTastID;
								startTask(HotConstants.Global.APP_URL_USER
										+ HotConstants.Shelf.suring,
										WHAT_NET_stock_sure, NET_METHOD_POST, false);
							}
						}).show();
						
					}
				});
				if (stocklList.get(position).TaskStatus == 1) {
					r = true;
				} else if (stocklList.get(position).TaskStatus == 2) {
					l = true;
				}
				if (r == true) {
					stock_finish.setClickable(false);
					stock_finish.setEnabled(false);
				} else {
					stock_finish.setClickable(true);
					stock_finish.setEnabled(true);
				}
				if (l == true) {
					stock_sure.setClickable(false);
					stock_sure.setEnabled(false);
				} else {
					stock_finish.setClickable(true);
					stock_sure.setEnabled(true);
				}
			} else {
				convertView = inflater.inflate(R.layout.finished_stock, null);
				TextView finish_stock_name = (TextView) convertView
						.findViewById(R.id.finish_stock_name);
				TextView finish_stock_time = (TextView) convertView
						.findViewById(R.id.finish_stock_time);
				TextView status = (TextView) convertView
						.findViewById(R.id.status);
				finish_stock_name.setText(stocklList.get(position).CheckName);
				finish_stock_time.setText("盘点时间:  " + stocklList.get(position).CheckTime);
				if (stocklList.get(position).CheckStatus == 2) {
					status.setText("正确");
					status.setTextColor(Color.GREEN);
				} else if (stocklList.get(position).CheckStatus == 3) {
					status.setText("有差异");
					status.setTextColor(Color.RED);
				}
			}
			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
        if(userID.equals("1") && stockList.get(position).TaskStatus==3){
        	Bundle bundle = new Bundle();
        	bundle.putString("TaskId", stockList.get(position).CheckTastID);
        	Ex.Activity(mActivity).start(StockHistoryActivity.class,bundle);
        }
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);		
	}
}
