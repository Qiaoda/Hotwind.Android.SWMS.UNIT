package cn.jitmarketing.hot.pandian;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingDetailEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;

public class StockTakingShopperAddActivity extends BaseSwipeOperationActivity implements
		OnClickListener {
	
	private static final int WHAT_NET_PANDIAN_SEARCH = 0x12;
	private static final int  WHAT_NET_PANDIAN_MODIFY = 0x13;
	
	@ViewInject(R.id.sku_search_edt)
	ClearEditText sku_search_edt;
	@ViewInject(R.id.query_btn)
	Button query_btn;
	@ViewInject(R.id.jian_btn)
	Button jian_btn;
	@ViewInject(R.id.jia_btn)
	Button jia_btn;
	@ViewInject(R.id.cancel_btn)
	Button cancel_btn;
	@ViewInject(R.id.confirm_btn)
	Button confirm_btn;
	@ViewInject(R.id.num_edit)
	EditText num_edit;
	@ViewInject(R.id.layout)
	LinearLayout layout;
	
	private int num = 0;
	
	private List<StockTakingDetailEntity> searchList = new ArrayList<StockTakingDetailEntity>();
	private String ShelfLocationCode;
	private HotApplication ap;

	@Override
	protected void exInitAfter() {
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_stock_taking_shopper_add;
	}
	
	@Override
	public void onReceiver(Intent intent) {
	}

	@Override
	protected void exInitView() {
		ShelfLocationCode = getIntent().getExtras().getString("ShelfLocationCode");
		ap = (HotApplication) getApplication();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),(int)(ConstValue.SCREEN_HEIGHT*0.4));
		layout.setLayoutParams(params);
		jian_btn.setOnClickListener(this);
		jia_btn.setOnClickListener(this);
		cancel_btn.setOnClickListener(this);
		confirm_btn.setOnClickListener(this);
		query_btn.setOnClickListener(this);
		sku_search_edt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				sku_search_edt.removeTextChangedListener(this);//解除文字改变事件 
				sku_search_edt.setText(s.toString().toUpperCase());//转换 
				sku_search_edt.setSelection(s.toString().length());//重新设置光标位置 
				sku_search_edt.addTextChangedListener(this);//重新绑 
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		num_edit.setText("0");
		num_edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if(s != null) {
					if(s.toString().equals("0-")) {
						num_edit.setText("-");
						num_edit.setSelection(1);
					} else {
						if(!s.toString().equals("-") && !s.toString().equals("")) {
							try {
								num = Integer.valueOf(s.toString());
								if(s.toString().length() > 1 && s.toString().startsWith("0")) {
									num_edit.setText("" + num);
									num_edit.setSelection(num_edit.getText().toString().length());
								}
							} catch(Exception e) {
								num = 0;
								num_edit.setText("0");
								num_edit.setSelection(1);
							}
						}
					}
					
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_PANDIAN_SEARCH:
			return WarehouseNet.getShelfQuery(ShelfLocationCode, sku_search_edt.getText() == null ? null : sku_search_edt.getText().toString());
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		Ex.Toast(this).show("你的网速不太好，获取失败");
	}
	
	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_PANDIAN_SEARCH:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			if (net.isSuccess) {
				String strSearch = mGson.toJson(net.data);
				searchList = mGson.fromJson(strSearch,
						new TypeToken<List<StockTakingDetailEntity>>() {
						}.getType());
				if(searchList == null || searchList.size() == 0) {
					confirm_btn.setText("新增");
				} else {
					confirm_btn.setText("修改");
				}
			}
			break;
		case WHAT_NET_PANDIAN_MODIFY:
			if (net.isSuccess) {
				Ex.Toast(mActivity).showLong("操作成功");
				StockTakingShopperDetailListActivity.refresh = true;
				StockTakingShopperActivity.refresh = true;
				finish();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jian_btn:
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认->库位详情->添加SKU弹窗->减1");
			if(num_edit.getText() == null || num_edit.getText().toString().equals("") || num_edit.getText().toString().equals("-")) {
				num = -1;
			} else {
				num = Integer.valueOf(num_edit.getText().toString()) - 1;
			}
			num_edit.setText(String.valueOf(num));
			num_edit.setSelection(num_edit.getText().toString().length());
			break;
		case R.id.jia_btn:
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认->库位详情->添加SKU弹窗->加1");
			if(num_edit.getText() == null || num_edit.getText().toString().equals("") || num_edit.getText().toString().equals("-")) {
				num = 1;
			} else {
				num = Integer.valueOf(num_edit.getText().toString()) + 1;
			}
			num_edit.setText(String.valueOf(num));
			num_edit.setSelection(num_edit.getText().toString().length());
			break;
		case R.id.query_btn:
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Shelf.QueryShelflocation, WHAT_NET_PANDIAN_SEARCH,
					NET_METHOD_POST, false);
			break;
		case R.id.confirm_btn:
			
			if(sku_search_edt.getText().toString().trim().equals("")) {
				Ex.Toast(this).show("请输入sku");
				return;
			}
			if(num <= 0) {
				Ex.Toast(this).show("数量不能小于1");
				return;
			}
			searchList = new ArrayList<StockTakingDetailEntity>();
			StockTakingDetailEntity entity = new StockTakingDetailEntity();  
			entity.KCSKUCount = 0;
			entity.PDSKUCount = num;
			entity.SKUID = sku_search_edt.getText().toString().toUpperCase().trim();
			entity.Status = 0;
			searchList.add(entity);
			startJsonTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.Stock.MODIFY_SHELF, WHAT_NET_PANDIAN_MODIFY, true,
					NET_METHOD_POST, SaveListUtil.updatePandian(searchList, ShelfLocationCode), false);
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认->库位详情->添加SKU弹窗->新增："+entity.SKUID+"数量："+entity.PDSKUCount);
			break;
		case R.id.cancel_btn:
			//日志操作
			LogUtils.logOnFile("盘点明细->已确认->库位详情->添加SKU弹窗->取消");
			hideSoftKeyBoard(this, sku_search_edt);
			finish();
			break;
		}
	}

	@Override
	public void fillCode(String code) {
		if (!SkuUtil.isWarehouse(code)) {
			ap.getsoundPool(ap.Sound_sku);
			sku_search_edt.setText(code);
			sku_search_edt.setSelection(code.length());
		} else{
			ap.getsoundPool(ap.Sound_error);
		}
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
}
