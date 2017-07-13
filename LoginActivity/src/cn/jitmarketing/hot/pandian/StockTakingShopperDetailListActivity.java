package cn.jitmarketing.hot.pandian;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingShopperCompleteAdapter;
import cn.jitmarketing.hot.adapter.StockTakingShopperNotCheckAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingShopownerEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.view.ClearEditText;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingShopperDetailListActivity extends BaseSwipeOperationActivity implements
		OnClickListener, OnItemClickListener {

	private static final int WHAT_NET_SAMPLE_FINISH = 0x09;
	private static final int WHAT_NET_STOCK_LIST = 0x10;
	private static final int OPEN_SAMPLE_SHELF_LOCATION = 0x11;
	private static final int OPEN_SMALL = 0x12;
	private static final int SMALL_FINISH = 0x13;
	@ViewInject(R.id.back_layout)
	LinearLayout back_layout;
	@ViewInject(R.id.sort_layout)
	LinearLayout sort_layout;
	@ViewInject(R.id.not_check_list)
	ListView not_check_list;
	@ViewInject(R.id.complete_list)
	ListView complete_list;
	@ViewInject(R.id.not_confirm_txt)
	TextView not_confirm_txt;
	@ViewInject(R.id.have_confirm_txt)
	TextView have_confirm_txt;
	@ViewInject(R.id.sort_txt)
	TextView sort_txt;
	
	@ViewInject(R.id.sku_search_edt)
	private ClearEditText clearEditText;
	
	private int tab = 1;
	private List<StockTakingShopownerEntity> notCheckList;
	private List<StockTakingShopownerEntity> completedList;
	private StockTakingShopperCompleteAdapter completeAdapter;
	private StockTakingShopperNotCheckAdapter notCheckAdapter;
	private String sampleShelf;
	private int mRequestTime, mOutTime = 2*60*1000;
	public static boolean refresh = false;
	private boolean isNotChecked;
	private boolean isSmallNotChecked;
	
	private boolean notCheckedSortAsc = false;//默认降序
	private boolean completeSortAsc = false;//默认降序
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_stock_taking_shopper_list;
	}

	@Override
	protected void exInitView() {
		initViews();
		back_layout.setOnClickListener(this);
		sort_layout.setOnClickListener(this);
		not_check_list.setOnItemClickListener(this);
		complete_list.setOnItemClickListener(this);
		not_confirm_txt.setOnClickListener(this);
		have_confirm_txt.setOnClickListener(this);
		sort_txt.setOnClickListener(this);
		hideSoftKeyBoard(this, clearEditText);
		startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
	}
	
	/**
	 * 开启样品库盘点
	 * @param sampleShelf
	 */
	public void openSample(String sampleShelf, boolean isNotChecked) {
		this.sampleShelf = sampleShelf;
		this.isNotChecked = isNotChecked;
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.openSampleShelflocation, OPEN_SAMPLE_SHELF_LOCATION,
				NET_METHOD_POST, false);
	}
	
	/**
	 * 开启小商品盘点
	 * @param sampleShelf
	 */
	public void openSmallSample(String sampleShelf, boolean isSmallNotChecked) {
		this.sampleShelf = sampleShelf;
		this.isSmallNotChecked = isSmallNotChecked;
		startTask(HotConstants.Global.APP_URL_USER
				+ HotConstants.Shelf.openSampleShelflocation, OPEN_SMALL,
				NET_METHOD_POST, false);
	}
	
	/**
	 * 样品库盘点完成
	 * @param taskId
	 */
	public void finishSampleTask(String sampleShelf) {
		startJsonLongTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmSampleStockCheck, 
				WHAT_NET_SAMPLE_FINISH, true, NET_METHOD_POST, SaveListUtil.ownerStockSave(sampleShelf), false, mRequestTime, mOutTime);
	}
	
	/**
	 * 小商品库盘点完成
	 * @param taskId
	 */
	public void finishSmallSampleTask(String sampleShelf) {
		startJsonLongTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ConfirmSmallSampleStockCheck, 
				WHAT_NET_SAMPLE_FINISH, true, NET_METHOD_POST, SaveListUtil.ownerStockSave(sampleShelf), false, mRequestTime, mOutTime);
	}
	
	List<StockTakingShopownerEntity> searchList = new ArrayList<StockTakingShopownerEntity>();
	private void initViews() {
		//根据输入框输入值的改变来过滤搜索
		clearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				searchList.clear();
				if(tab == 1) {
					if(TextUtils.isEmpty(s) || s.toString().trim().equals("")) {
//						completeAdapter = new StockTakingShopperCompleteAdapter(this, completedList);
						notCheckAdapter = new StockTakingShopperNotCheckAdapter(StockTakingShopperDetailListActivity.this, notCheckList);
					} else {
						for(StockTakingShopownerEntity entity : notCheckList) {
							if(entity.ShelfLocationName.replace("-", "").startsWith(s.toString().toUpperCase()) || entity.ShelfLocationName.startsWith(s.toString().toUpperCase())) {
								searchList.add(entity);
							}
						}
						notCheckAdapter = new StockTakingShopperNotCheckAdapter(StockTakingShopperDetailListActivity.this, searchList);
					}
					not_check_list.setAdapter(notCheckAdapter);
				} else if(tab == 2) {
					if(TextUtils.isEmpty(s) || s.toString().trim().equals("")) {
						completeAdapter = new StockTakingShopperCompleteAdapter(StockTakingShopperDetailListActivity.this, completedList);
					} else {
						for(StockTakingShopownerEntity entity : completedList) {
							if(entity.ShelfLocationName.replace("-", "").startsWith(s.toString().toUpperCase()) || entity.ShelfLocationName.startsWith(s.toString().toUpperCase())) {
								searchList.add(entity);
							}
						}
						completeAdapter = new StockTakingShopperCompleteAdapter(StockTakingShopperDetailListActivity.this, searchList);
					}
					complete_list.setAdapter(completeAdapter);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		clearEditText.setOnKeyListener(new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {// 修改回车键功能
					//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
					searchList.clear();
					if(tab == 1) {
						if(!TextUtils.isEmpty(clearEditText.getText()) && !clearEditText.getText().toString().trim().equals("")) {
							for(StockTakingShopownerEntity entity : notCheckList) {
								if(entity.ShelfLocationName.replace("-", "").startsWith(clearEditText.getText().toString().toUpperCase()) || entity.ShelfLocationName.startsWith(clearEditText.getText().toString().toUpperCase())) {
									searchList.add(entity);
								}
							}
							notCheckAdapter = new StockTakingShopperNotCheckAdapter(StockTakingShopperDetailListActivity.this, searchList);
						}
						not_check_list.setAdapter(notCheckAdapter);
					} else if(tab == 2) {
						if(!TextUtils.isEmpty(clearEditText.getText()) && !clearEditText.getText().toString().trim().equals("")) {
							for(StockTakingShopownerEntity entity : completedList) {
								if(entity.ShelfLocationName.replace("-", "").startsWith(clearEditText.getText().toString().toUpperCase()) || entity.ShelfLocationName.startsWith(clearEditText.getText().toString().toUpperCase())) {
									searchList.add(entity);
								}
							}
							completeAdapter = new StockTakingShopperCompleteAdapter(StockTakingShopperDetailListActivity.this, searchList);
						}
						complete_list.setAdapter(completeAdapter);
					}
				}
				return false;
			}
		});
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(refresh) {
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);	
		}
		//日志操作
		LogUtils.logOnFile("盘点明细->未确认");
	}


	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			return WarehouseNet.getCheckStockList();
		case OPEN_SAMPLE_SHELF_LOCATION:
			return WarehouseNet.openSampleShelf(sampleShelf);
		case OPEN_SMALL:
			return WarehouseNet.openSampleShelf(sampleShelf);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		Ex.Toast(this).show("你的网速不太好，获取失败");
		//日志操作
		LogUtils.logOnFile("盘点明细->未确认"+"你的网速不太好，获取失败");
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message);
			//日志操作
			LogUtils.logOnFile("盘点明细->未确认"+net.message);
			return;
		}
		refresh = false;
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				//日志操作
				LogUtils.logOnFile("盘点明细->未确认"+net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			try {
				JSONObject js = new JSONObject(str);
				String notCheck = js.getString("NotCheckList");
				String completed = js.getString("CompletedList");
				notCheckList = mGson.fromJson(notCheck,
				new TypeToken<List<StockTakingShopownerEntity>>() {}.getType());
				completedList = mGson.fromJson(completed,
						new TypeToken<List<StockTakingShopownerEntity>>() {
						}.getType());
				completeAdapter = new StockTakingShopperCompleteAdapter(this, completedList);
				notCheckAdapter = new StockTakingShopperNotCheckAdapter(this, notCheckList);
				complete_list.setAdapter(completeAdapter);
				not_check_list.setAdapter(notCheckAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case OPEN_SAMPLE_SHELF_LOCATION:
			if (!net.isSuccess) {
				Ex.Toast(mActivity).showLong(net.message);
				//日志操作
				LogUtils.logOnFile("盘点明细->未确认"+net.message);
				return;
			}
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
			Ex.Toast(mActivity).showLong("样品库开启盘点");
			//日志操作
			LogUtils.logOnFile("盘点明细->未确认"+"样品库开启盘点");
//			if(isNotChecked) {
//				notCheckList.get(0).IsStartCheck = "1";
//				notCheckAdapter.notifyDataSetChanged();
//			} else {
////				notCheckList.get(0).IsStartCheck = "1";
//				notCheckList.add(0, completedList.get(0));
//				notCheckAdapter.notifyDataSetChanged();
//				completedList.remove(0);
//				completeAdapter.notifyDataSetChanged();
//				
//			}
			break;
		case OPEN_SMALL:
			if (!net.isSuccess) {
				Ex.Toast(mActivity).showLong(net.message);
				//日志操作
				LogUtils.logOnFile("盘点明细->未确认"+net.message);
				return;
			}
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
			Ex.Toast(mActivity).showLong("小商品开启盘点");
			//日志操作
			LogUtils.logOnFile("盘点明细->未确认"+"小商品开启盘点");
//			if(isSmallNotChecked) {
//				notCheckList.get(1).IsStartCheck = "1";
//				notCheckAdapter.notifyDataSetChanged();
//			} else {
////				notCheckList.get(0).IsStartCheck = "1";
//				notCheckList.add(1, completedList.get(1));
//				notCheckAdapter.notifyDataSetChanged();
//				completedList.remove(1);
//				completeAdapter.notifyDataSetChanged();
//				
//			}
			break;
		case WHAT_NET_SAMPLE_FINISH:
			if (!net.isSuccess) {
				Ex.Toast(mActivity).showLong(net.message);
				//日志操作
				LogUtils.logOnFile("盘点明细->未确认"+net.message);
				return;
			}
			Ex.Toast(mActivity).showLong("盘点完成");
			//日志操作
			LogUtils.logOnFile("盘点明细->未确认"+"盘点完成");
//			completedList.add(0, notCheckList.get(0));
//			completeAdapter.notifyDataSetChanged();
//			notCheckList.remove(0);
//			notCheckAdapter.notifyDataSetChanged();
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.shopownerPlanStock, WHAT_NET_STOCK_LIST,
					NET_METHOD_POST, false);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_layout://返回
			//日志操作
			LogUtils.logOnFile("盘点明细->返回");
			this.finish();
			break;
		case R.id.sort_txt://排序
			//日志操作
			LogUtils.logOnFile("盘点明细->排序");
			if(tab == 1) {
				if(!notCheckedSortAsc) {
					notCheckedSortAsc = true;
					sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort_desc), null);
				} else {
					notCheckedSortAsc = false;
					sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort), null);
				}
//				StockTakingShopownerEntity sample = notCheckList.get(0);
//				if(sample.ShelfLocationCode.equals("EX00001")) {
//					notCheckList.remove(0);
//				}
//				StockTakingShopownerEntity sampleSmall = notCheckList.get(1);
//				if(sampleSmall.ShelfLocationCode.equals("SC00001")) {
//					notCheckList.remove(1);
//				}
				Collections.reverse(notCheckList);  
//				if(sample.ShelfLocationCode.equals("EX00001")) {
//					notCheckList.add(0,sample);
//				}
//				if(sampleSmall.ShelfLocationCode.equals("SC00001")) {
//					notCheckList.add(1,sampleSmall);
//				}
				notCheckAdapter.notifyDataSetChanged();
			} else if(tab == 2) {
				if(!completeSortAsc) {
					completeSortAsc = true;
					sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort_desc), null);
				} else {
					completeSortAsc = false;
					sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort), null);
				}
//				StockTakingShopownerEntity sample = completedList.get(0);
//				if(sample.ShelfLocationCode.equals("EX00001")) {
//					completedList.remove(0);
//				}
//				StockTakingShopownerEntity sampleSmall = completedList.get(1);
//				if(sampleSmall.ShelfLocationCode.equals("SC00001")) {
//					completedList.remove(1);
//				}
				Collections.reverse(completedList);  
//				if(sample.ShelfLocationCode.equals("EX00001")) {
//					completedList.add(0,sample);
//				}
//				if(sampleSmall.ShelfLocationCode.equals("SC00001")) {
//					completedList.add(1,sampleSmall);
//				}
				completeAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.not_confirm_txt://未确认
			//日志操作
			LogUtils.logOnFile("盘点明细->未确认");
			if(!notCheckedSortAsc) {
				sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort), null);
			} else {
				sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort_desc), null);
			}
			tab = 1;
			resetBtn();
			not_confirm_txt.setBackgroundResource(R.drawable.tab_left_true);
			not_confirm_txt.setTextColor(getResources().getColor(R.color.color_white_text));
			have_confirm_txt.setTextColor(getResources().getColor(R.color.color_black_text));
			not_check_list.setVisibility(View.VISIBLE);
			complete_list.setVisibility(View.GONE);
			break;
		case R.id.have_confirm_txt://已确认
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认");
			if(!completeSortAsc) {
				sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort), null);
			} else {
				sort_txt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.sort_desc), null);
			}
			tab = 2;
			resetBtn();
			have_confirm_txt.setBackgroundResource(R.drawable.tab_right_true);
			not_confirm_txt.setTextColor(getResources().getColor(R.color.color_black_text));
			have_confirm_txt.setTextColor(getResources().getColor(R.color.color_white_text));
			not_check_list.setVisibility(View.GONE);
			complete_list.setVisibility(View.VISIBLE);
			break;
		}
	}

	private void resetBtn() {
		not_confirm_txt.setBackgroundResource(R.drawable.tab_left);
		have_confirm_txt.setBackgroundResource(R.drawable.tab_right);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
//		if(parent == complete_list) {
//			Intent intent = new Intent(this, StockTakingShopperDetailActivity.class);
//			Bundle bd = new Bundle();
//			bd.putString("ShelfLocationCode", completedList.get(position).ShelfLocationCode);
//			intent.putExtras(bd);
//			startActivity(intent);
//		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
}
