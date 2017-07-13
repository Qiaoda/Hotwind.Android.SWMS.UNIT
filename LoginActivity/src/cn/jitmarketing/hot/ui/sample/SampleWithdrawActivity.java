package cn.jitmarketing.hot.ui.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ItemListAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SampleEntity;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 撤样 Created by fgy on 2016/4/7.
 */
public class SampleWithdrawActivity extends BaseSwipeOperationActivity implements View.OnClickListener, QueryResultFragment.OnItemSelectedListener {

	private static final int WHAT_NET_GET_STOCK = 0x10;
	private static final int REQUEST_CODE_DIRECT_WITHDRAW = 0x11;
	private static final int WHAT_NET_GET_STOCK_FUZZY = 0x13;// 模糊搜索

	@ViewInject(R.id.allocation_title)
	TitleWidget allocation_title;
	@ViewInject(R.id.ll_bottomBar)
	LinearLayout ll_bottomBar;
	@ViewInject(R.id.btn_withdraw)
	TextView btnWithdraw;
	@ViewInject(R.id.no_data)
	TextView tvNoData;

	@ViewInject(R.id.ll_searchBar)
	private LinearLayout ll_searchBar; // 搜索布局
	@ViewInject(R.id.check_stock_search_edt)
	private ClearEditText editText; // 搜索输入框
	// 搜索显示列表
	@ViewInject(R.id.search_list)
	private ListView search_list;

	private HotApplication ap;
	private FragmentManager fm;
	private String skuCodeStr;
	private List<SkuEntity> mList;
	private QueryResultFragment mFragment;

	// --------模糊搜索
	private String searchSku;
	private SearchSkuBean searchSkuBean;// sku搜索结果对象
	private ItemListAdapter itemListAdapter;
	private List<SearchSkuBean> searchSkuList = new ArrayList<SearchSkuBean>();

	@Override
	protected void exInitBundle() {
		initIble(this, this);
		mList = new ArrayList<SkuEntity>();
		skuCodeStr = getIntent().getStringExtra("skuCode");
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_sample_withdraw;
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		fm = getSupportFragmentManager();
		EventBus.getDefault().register(this);
		allocation_title.setOnLeftClickListner(this);
		btnWithdraw.setOnClickListener(this);
		// skuCodeStr = "10W590309L";
		// skuCodeStr = "61H573414230";//61H573104230
		editText.setOnKeyListener(onKeyListener);
		editText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if (!s.toString().equals("")) {
					tvNoData.setVisibility(View.GONE);
					searchSku = s.toString();
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKUFuzzy, WHAT_NET_GET_STOCK_FUZZY, false, NET_METHOD_POST, false);
				} else {
					searchSkuList.clear();
					itemListAdapter.notifyDataSetChanged();
					search_list.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { 
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		if (!TextUtils.isEmpty(skuCodeStr))
			dealBarCode(skuCodeStr);
		// 初始化搜索列表
		itemListAdapter = new ItemListAdapter(this, searchSkuList);
		search_list.setAdapter(itemListAdapter);
		search_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub 
				hideSoftKeyBoard(mActivity, search_list);
				if (searchSkuList.get(position).SKUCode != null) {
					skuCodeStr = searchSkuList.get(position).SKUCode;
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, false, NET_METHOD_POST, false);
				}

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if (skuCodeStr != null && skuCodeStr.length() > 0)
		// startTask(HotConstants.Global.APP_URL_USER +
		// HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, false,
		// NET_METHOD_POST, false);
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_STOCK:
			return SkuNet.getStoreForSKU(skuCodeStr);
		case WHAT_NET_GET_STOCK_FUZZY:
			return SkuNet.getStoreForSKUFuzzy(searchSku);
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			switch (what) {
			case WHAT_NET_GET_STOCK:
			case REQUEST_CODE_DIRECT_WITHDRAW:
				ll_searchBar.setVisibility(View.VISIBLE);
				ll_bottomBar.setVisibility(View.GONE);
				// 隐藏速锁列表
				searchSkuList.clear();
				search_list.setVisibility(View.GONE);
				break;
			case WHAT_NET_GET_STOCK_FUZZY:

				break;
			}
			if (mFragment != null) {
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.remove(mFragment);
				transaction.commit();
			}
			return;
		}
		switch (what) {
		case WHAT_NET_GET_STOCK:
		case REQUEST_CODE_DIRECT_WITHDRAW:
			ll_searchBar.setVisibility(View.GONE);
			ll_bottomBar.setVisibility(View.VISIBLE);
			break;
		case WHAT_NET_GET_STOCK_FUZZY:

			break;
		}

		switch (what) {
		case WHAT_NET_GET_STOCK:
			try {
				String skulistStr = new JSONObject(mGson.toJson(net.data)).getString("skus");
				String itemStr = new JSONObject(mGson.toJson(net.data)).getString("item");

				ItemEntity itemEntity = mGson.fromJson(itemStr, ItemEntity.class);
				mList.clear();

				mList.addAll((ArrayList<SkuEntity>) mGson.fromJson(skulistStr, new TypeToken<List<SkuEntity>>() {
				}.getType()));

				if (mList != null) {
					for (SkuEntity entity : mList) {
						if (entity.SKUCode.equals(skuCodeStr)) {
							HotConstants.SELECTED = mList.indexOf(entity);
							break;
						}
					}
				}
				// 隐藏速锁列表
				searchSkuList.clear();
				search_list.setVisibility(View.GONE);
				// 加载碎片
				mFragment = new QueryResultFragment(itemEntity, mList, this, 1);
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.container, mFragment);
				transaction.commit();

				this.onItemSelected(itemEntity, mList.get(HotConstants.SELECTED));
				tvNoData.setVisibility(View.GONE);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case WHAT_NET_GET_STOCK_FUZZY:// 模糊搜索处理
			if (null == net.data) {
				Ex.Toast(mActivity).show(net.message);
				return;
			}
			String itemStr = mGson.toJson(net.data);
			searchSkuList.clear();
			searchSkuList.addAll((List<SearchSkuBean>) mGson.fromJson(itemStr, new TypeToken<List<SearchSkuBean>>() {
			}.getType()));
			if (searchSkuList.size() != 0) {
				search_list.setVisibility(View.VISIBLE);
				itemListAdapter.notifyDataSetChanged();
			} else {
				searchSkuList.clear();
				itemListAdapter.notifyDataSetChanged();
				search_list.setVisibility(View.GONE);
			}

			break;
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_STOCK:
		case REQUEST_CODE_DIRECT_WITHDRAW:
			ll_searchBar.setVisibility(View.VISIBLE);
			ll_bottomBar.setVisibility(View.GONE);
			tvNoData.setVisibility(View.VISIBLE);
			break;

		case WHAT_NET_GET_STOCK_FUZZY:
			searchSkuList.clear();
			search_list.setVisibility(View.GONE);
			break;
		}

		Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
	}

	@Override
	public void fillCode(String code) {
		this.skuCodeStr = code;
		if (code != null) {
			dealBarCode(code);
		} else {
			ap.getsoundPool(ap.Sound_error);
		}
	}

	private void dealBarCode(String skuCodeStr) {
		if (!SkuUtil.isWarehouse(skuCodeStr)) {
			ap.getsoundPool(ap.Sound_sku);
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
		} else {
			ap.getsoundPool(ap.Sound_location);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			finish();
			break;
		case R.id.btn_withdraw:
			if (mFragment == null) {
				Ex.Toast(mActivity).show(R.string.no_sku);
				return;
			}
			ItemEntity itemEntity = mFragment.getItemEntity();
			SkuEntity sku = mFragment.getCurSkuEntity();
			if (itemEntity == null || sku == null) {
				Ex.Toast(mActivity).show(R.string.no_sku);
				return;
			}
			Bundle bd = new Bundle();
			SampleEntity sampleEntity = new SampleEntity();
			bd.putSerializable("sampleEntity", sampleEntity);
			bd.putString("skuCodeStr", skuCodeStr);
			bd.putSerializable("sku", sku);
			bd.putSerializable("item", itemEntity);
			if (mFragment.getItemEntity().IsSomeSampling) {
				// 鞋类
				Ex.Activity(mActivity).startForResult(OutAndWithdrawActivity.class, bd, REQUEST_CODE_DIRECT_WITHDRAW);
			} else {
				// 非鞋类
				sampleEntity.isCanBatch = true;
				bd.putSerializable("sampleEntity", sampleEntity);
				bd.putInt("operateType", HandSampleDialog.OPERATE_TYPE_WITHDRAW_SAMPLE);
				// Ex.Activity(mActivity).start(HandSampleDialog.class, bd);
				Ex.Activity(mActivity).startForResult(HandSampleDialog.class, bd, REQUEST_CODE_DIRECT_WITHDRAW);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(ItemEntity itemEntity, SkuEntity skuEntity) {
		// 存在出样 
		if (skuEntity.IsHasSample) {// Integer.valueOf(skuEntity.SKUCount) > 0
			btnWithdraw.setEnabled(true);
		} else {
			btnWithdraw.setEnabled(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_DIRECT_WITHDRAW && resultCode == RESULT_OK) {
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, false, NET_METHOD_POST, false);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	// EventBus回调skucode
	@Subscribe(threadMode = ThreadMode.MainThread)
	public void getSkuCode(String code) {
		skuCodeStr = code;
	}

	OnKeyListener onKeyListener = new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {// 修改回车键功能
				if (editText.getText() == null || editText.getText().toString().equals("")) {
					return false;
				}
				hideSoftKeyBoard(mActivity, editText);

				skuCodeStr = editText.getText().toString();
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, false, NET_METHOD_POST, false);
			}
			return false;
		}
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
