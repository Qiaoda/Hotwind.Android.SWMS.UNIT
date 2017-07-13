package cn.jitmarketing.hot.ui.sku;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ex.lib.core.callback.ExRequestCallback;
import com.ex.lib.core.exception.ExException;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;
import com.jiebao.scanlib.ScanService;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.AddStockListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.ui.shelf.HandAddStockActivity;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

public class AddStockActivity extends BaseSwipeOperationActivity implements OnClickListener, OnItemClickListener {
	
	@ViewInject(R.id.add_stock_title)
	TitleWidget add_stock_title;
	@ViewInject(R.id.add_stock_list)
	ListView add_stock_list;
	
	private static final int FOR_MOVE = 0x11;
	private HotApplication ap;
	private String skuCodeStr;
	private boolean req = true;//屏幕关闭时 不再请求

	public List<StockBean> stockList = new ArrayList<StockBean>();
	private List<StockBean> manualStockList = new ArrayList<StockBean>();
	private static final int WHAT_NET_GET_STOCK_LIST = 0x10;
	private static final int WHAT_NET_GET_STOCK_DEL = 0x11;
	private AddStockListAdapter stockListAdapter;
	
	private Timer timer;
	private MyTask task = new MyTask();
	private String stockCodeDel;
	private int postionDel;
	private boolean isReqing = false;//是否正在请求（针对定时请求）

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_add_stock;
	}

	@Override
	protected void exInitView() {
		timer = new Timer();
		timer.schedule(task, 0, 1 * 5 * 1000);
		ap = (HotApplication) getApplication();
		add_stock_title.setOnLeftClickListner(this);
		add_stock_title.setOnRightClickListner(this);
		stockListAdapter = new AddStockListAdapter(this, stockList);
		add_stock_list.setAdapter(stockListAdapter);
		add_stock_list.setOnItemClickListener(this);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Stock.GET_SHELF, WHAT_NET_GET_STOCK_LIST, NET_METHOD_POST, false);
	}
	
	@Override
	public void onReceiver(Intent intent) {
//		byte[] barcode = intent.getByteArrayExtra("barocode");
//		int barocodelen = intent.getIntExtra("length", 0);
//		skuCodeStr = new String(barcode, 0, barocodelen).toUpperCase().trim();
//		dealBarCode(skuCodeStr);
		
		
//		if(barcode != null) {
//			skuCodeStr = new String(barcode, 0, barocodelen).toUpperCase().trim();
//			dealBarCode(skuCodeStr);
//		}else {
//			ap.getsoundPool(ap.Sound_error);
//			return;
//		}
//		if (SkuUtil.isWarehouse(skuCodeStr)) {
//			ap.getsoundPool(ap.Sound_location);
//			addStock(skuCodeStr);
//		} else{
//			ap.getsoundPool(ap.Sound_error);
//		}
	}
	
//	@Override
//	public void fillCode(String code) {
//		skuCodeStr = code;
//		dealBarCode(code);
//	}
	
	private void dealBarCode(String skuCodeStr) {
		if (SkuUtil.isWarehouse(skuCodeStr)) {
			ap.getsoundPool(ap.Sound_location);
			addStock(skuCodeStr);
		} else{
			ap.getsoundPool(ap.Sound_error);
		}
	}
	
	/**
	 * 添加库位
	 */
	private void addStock(String skuCode) {
		boolean isExist = false;
		for(StockBean st : stockList) {
			if(st.stockCode.equals(skuCode)) {
				isExist = true;
				break;
			}
		}
		if(!isExist) {
			StockBean stock = new StockBean();
			stock.stockCode = skuCode;
			stock.status = 1;
			manualStockList.add(stock);
			stockList.add(stock);
			stockListAdapter.notifyDataSetChanged();
			add_stock_list.setSelection(stockList.size()-1);
		} else {
			Ex.Toast(mContext).show(skuCode+"已存在");
		}
	}

	@Override
	public void onError(int what, int e, String message) {
         switch (what) {
		case WHAT_NET_GET_STOCK_LIST:
			Ex.Toast(mActivity).show("你的网速不太好，申请失败，请稍后重试");
			break;
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_STOCK_LIST:
			return WarehouseNet.stockListParams(MgrPerference.getInstance(this).getString(HotConstants.Unit.UNIT_ID));
		case WHAT_NET_GET_STOCK_DEL:
			return WarehouseNet.stockDelParams(stockCodeDel);
		}
		return null;
	}


	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_GET_STOCK_LIST:
			if (null == net.data) {
				Ex.Toast(mContext).show(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			stockList = mGson.fromJson(stockStr, new TypeToken<List<StockBean>>() {}.getType());
			stockListAdapter = new AddStockListAdapter(this, stockList);
			add_stock_list.setAdapter(stockListAdapter);
			break;
		case WHAT_NET_GET_STOCK_DEL:
			Ex.Toast(mContext).show("删除成功");
			stockList.remove(postionDel);
			stockListAdapter.notifyDataSetChanged();
			break;
		}
	}
	
	@Override
	protected void onResume() {
		// 开启扫描服务
//		startService(new Intent(this,ScanService.class));
//		// 绑定服务
//		bindService(new Intent(this,ScanService.class), serviceConnection,Context.BIND_AUTO_CREATE);
		super.onResume();
		req = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		req = false;
	}
	@Override
	protected void onStop() {
		super.onStop();
		req = false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer != null)
			timer.cancel();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			if(manualStockList.size() != 0) {
				new AlertDialog.Builder(this)
				.setTitle(R.string.noteTitle)
				.setMessage("部分库位码未提交，确定返回？")
				.setPositiveButton(R.string.sureTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						AddStockActivity.this.finish();
					}
				})
				.setNegativeButton(R.string.cancelTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {

					}
				}).show();
			} else {
				this.finish();
			}
			break;
		case R.id.lv_right:	
			Intent intent = new Intent();
			intent.setClass(this, HandAddStockActivity.class);
			startActivityForResult(intent, FOR_MOVE);
			//关闭扫描服务
//			if(serviceConnection!=null){
//				unbindService(serviceConnection);
//				stopService(new Intent(this,ScanService.class));
//			}
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == FOR_MOVE) {
			String skucode = data.getBundleExtra("bundle").getString("sku");
			addStock(skucode);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}
	
	public void delete(String stockCodeDel, int position) {
		this.stockCodeDel = stockCodeDel;
		this.postionDel = position;
		new AlertDialog.Builder(this)
		.setTitle(R.string.noteTitle)
		.setMessage("确定删除？")
		.setPositiveButton(R.string.sureTitle,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int whichButton) {
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.Stock.DELETE_SHELF, WHAT_NET_GET_STOCK_DEL, NET_METHOD_POST, false);
			}
		})
		.setNegativeButton(R.string.cancelTitle,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog,
					int which) {

			}
		}).show();
	}
	
	/**
	 * NEW_消息处理对象
	 */
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {

			// 获取操作码
			int what = msg.what;
			// 获取请求结果码
			int arg2 = msg.arg2;
			if(what == 1) {
				for(int i=0; i<stockList.size(); i++) {
    				if(manualStockList.contains(stockList.get(i))) {
    					stockList.get(i).status = 2;
    				}
    			}
				stockListAdapter.notifyDataSetChanged();
				add_stock_list.setSelection(stockList.size()-1);
			} else {
				// 获取结果值
				String result = msg.getData().getString("result");
				boolean isShow = msg.getData().getBoolean("isShow");
				// 判断对象是否销毁
				if (mActivity == null || mContext == null) {
					return;
				}
				ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
				if (!net.isSuccess) {
					Ex.Toast(mContext).show(net.message);
					return;
				}
				for(int i=0; i<stockList.size(); i++) {
					if(manualStockList.contains(stockList.get(i))) {
						stockList.get(i).status = 0;
					}
				}
				stockListAdapter.notifyDataSetChanged();
				manualStockList.clear();
				// 判断是否显示对话框
				if (isShow) {
					Ex.Dialog(mContext).dismissProgressDialog();
				}
			}
		}
	};
	
	ExRequestCallback requestCallback = new ExRequestCallback() {
		@Override
		public void onSuccess(InputStream inStream, HashMap<String, String> cookies) {
			isReqing = false;
			// 请求结果
			String result = "";
			// 判断结果流是否为空
			if (inStream != null) {
				// 转换流对象
				result = Ex.T().getInStream2Str(inStream);
				Log.e("result--", result);
			}
			// 创建消息对象
			Message msg = mHandler.obtainMessage();
			// 传入操作码
			msg.what = 2;
			// 请求结果码
			msg.arg2 = 0;
			// 请求结果
			msg.obj = inStream;
			// 请求结果参数
			Bundle data = new Bundle();
			data.putSerializable("cookies", cookies);
			data.putString("result", result);
			data.putBoolean("cache", false);
//						data.putBoolean("isShow", isShow);
			msg.setData(data);

			// 发送消息
			mHandler.sendMessage(msg);
		}

		@Override
		public void onError(int statusCode, ExException e) {
			isReqing = false;
			Bundle data = new Bundle();
			data.putString("result", e.getMessage());
		}
	};

	class MyTask extends java.util.TimerTask {     
        
        @Override     
        public void run() {   
        	if(req) {
	        	if(manualStockList.size() != 0) {
	        		if(!isReqing) {
	        			isReqing = true;
		        		Message msg = mHandler.obtainMessage();
		        		msg.what = 1;
		        		mHandler.sendMessage(msg);
		        		JSONObject postData = new JSONObject();  
		    			try {
							postData.put("UserID", MgrPerference.getInstance(AddStockActivity.this).getString(HotConstants.User.SHARE_USERID));
							postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			    			JSONArray array = new JSONArray();
			    			for(StockBean sb : manualStockList) {
			    				JSONObject mJson = new JSONObject();  
			    				mJson.put("ShelfLocationCode", sb.stockCode);
			    				mJson.put("LastUpdateTime", sb.lastUpdateTime);
			    				mJson.put("Status", sb.status);
			    				array.put(mJson);
			    			}
			    			postData.put("List", array);
			    			Ex.Net(AddStockActivity.this).sendJsonAsyncPost(HotConstants.Global.APP_URL_USER + HotConstants.Stock.SAVE_SHELF, postData.toString(), requestCallback);
		    			} catch (JSONException e) {
							Log.i("jsonError", e.getMessage());
						}  
	        		}
	        	}
        	}
        }
    }

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
	}  
}
