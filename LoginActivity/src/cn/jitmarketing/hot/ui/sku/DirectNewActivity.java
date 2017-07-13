package cn.jitmarketing.hot.ui.sku;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.layout;
import cn.jitmarketing.hot.adapter.ItemListAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.ui.sample.QueryResultFragment;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.BaseDialog;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DirectNewActivity extends BaseSwipeOperationActivity implements View.OnClickListener, QueryResultFragment.OnItemSelectedListener {

	@ViewInject(R.id.allocation_title)
	private TitleWidget allocation_title;
	@ViewInject(R.id.scaner_tip)
	private TextView scaner_tip;
	@ViewInject(R.id.ll_searchBar)
	private LinearLayout ll_searchBar; // 搜索布局
	@ViewInject(R.id.check_stock_search_edt)
	private ClearEditText editText; // 搜索输入框
	@ViewInject(R.id.fl_content)
	private FrameLayout fl_content; // 内存布局
	@ViewInject(R.id.ll_bottomBar)
	private LinearLayout ll_bottomBar; // 底部按钮布局
	@ViewInject(R.id.btn_bottomBtn1)
	private Button btn_bottom1; // 底部按钮1
	@ViewInject(R.id.btn_bottomBtn2)
	private Button btn_bottom2; // 底部按钮2
	// 搜索显示列表
	@ViewInject(R.id.search_list)
	private ListView search_list;
	private static final int WHAT_NET_GET_STOCK = 0x10;// 查库存
	private static final int REQUEST_CODE_DIRECT_GETNEW = 0x11;// 直接取新
	private static final int REQUEST_CODE_DIRECT_GETBOX = 0x12;// 直接取鞋盒
	private static final int WHAT_NET_GET_STOCK_FUZZY = 0x13;// 模糊搜索

	private HotApplication ap;
	private String skuCodeStr;// 扫描到的条码
	private String skuCode;// SKU码
	private String storage;// 库位码
	private String SKUCount;// 空鞋盒、一只鞋加鞋盒
	private List<SkuEntity> mList;

	private FragmentManager fm;
	private QueryResultFragment mFragment;
	// --------模糊搜索
	private String searchSku;
	private SearchSkuBean searchSkuBean;// sku搜索结果对象
	private ItemListAdapter itemListAdapter;
	private List<SearchSkuBean> searchSkuList = new ArrayList<SearchSkuBean>();
	// ---------直接取新
	private BaseDialog getNewDialog;
	private LayoutInflater inflater;
	private RadioGroup radioGroup;
	private RadioButton radioZero;
	private RadioButton radioOne;
	private SkuEditText skuEditText;
	private TextView btn_cancle, btn_sure;
	// ---------取新类型
	private int getNewType;// 1为取鞋和衣服 2为取鞋盒

	@Override
	protected void exInitBundle() {
		initIble(this, this);
		mList = new ArrayList<SkuEntity>();
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_direct_new;
	}

	@Override
	protected void exInitView() {
		allocation_title.setText("直接取新");
		inflater = LayoutInflater.from(mActivity);
		ap = (HotApplication) getApplication();
		fm = getSupportFragmentManager();
		allocation_title.setOnLeftClickListner(this);
		btn_bottom1.setOnClickListener(this);
		btn_bottom2.setOnClickListener(this);
		editText.setOnKeyListener(onKeyListener);
		editText.addTextChangedListener(new EditTextWatcher());
		// 初始化搜索列表
		itemListAdapter = new ItemListAdapter(this, searchSkuList);
		search_list.setAdapter(itemListAdapter);
		search_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				if (searchSkuList.get(position).SKUCode != null) {
					hideSoftKeyBoard(mActivity, editText);
					skuCode = searchSkuList.get(position).SKUCode;
					sendSearchSku();
				}

			}
		});
		createGetNewDialog();

	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_STOCK:// 查库存
			return SkuNet.getStoreForSKU(skuCode);
		case WHAT_NET_GET_STOCK_FUZZY:// 模糊查找
			return SkuNet.getStoreForSKUFuzzy(searchSku);
		case REQUEST_CODE_DIRECT_GETNEW:// 直接取新
			return SkuNet.directGetNew(skuCode, storage);
		case REQUEST_CODE_DIRECT_GETBOX:// 直接取鞋盒
			return SkuNet.directGetBox(skuCode, storage, SKUCount);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_STOCK:
		case REQUEST_CODE_DIRECT_GETNEW:
			ll_searchBar.setVisibility(View.VISIBLE);
			ll_bottomBar.setVisibility(View.GONE);
			break;

		case WHAT_NET_GET_STOCK_FUZZY:
			searchSkuList.clear();
			search_list.setVisibility(View.GONE);
			break;
		}

		Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		// 请求不成功
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			switch (what) {
			case WHAT_NET_GET_STOCK:
				if (mFragment != null) {
					FragmentTransaction transaction = fm.beginTransaction();
					transaction.remove(mFragment);
					transaction.commit();
				}
			case REQUEST_CODE_DIRECT_GETNEW:
				ll_searchBar.setVisibility(View.VISIBLE);
				ll_bottomBar.setVisibility(View.GONE);
				// 隐藏搜索列表
				searchSkuList.clear();
				search_list.setVisibility(View.GONE);
				break;
			case WHAT_NET_GET_STOCK_FUZZY:

				break;
			}

			return;
		}
		// 请求成功
		switch (what) {
		case WHAT_NET_GET_STOCK:// 查询库存
			if (scaner_tip.getVisibility() == View.VISIBLE) {
				scaner_tip.setVisibility(View.GONE);
			}
			ll_searchBar.setVisibility(View.GONE);
			ll_bottomBar.setVisibility(View.VISIBLE);
			try {
				String skuListStr = new JSONObject(mGson.toJson(net.data)).getString("skus");
				String itemStr = new JSONObject(mGson.toJson(net.data)).getString("item");

				ItemEntity itemEntity = mGson.fromJson(itemStr, ItemEntity.class);
				mList.clear();
				mList.addAll((ArrayList<SkuEntity>) mGson.fromJson(skuListStr, new TypeToken<List<SkuEntity>>() {
				}.getType()));
				for (int i = 0; i < mList.size(); i++) {
					if (mList.get(i).SKUCode.equals(skuCode)) {
						HotConstants.SELECTED = i;
						break;
					}
				}
				// 隐藏速锁列表
				searchSkuList.clear();
				search_list.setVisibility(View.GONE);
				// 加载碎片
				mFragment = new QueryResultFragment(itemEntity, mList, this);
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.fl_content, mFragment);
				transaction.commit();

				this.onItemSelected(itemEntity, mList.get(HotConstants.SELECTED));
				// 如果是鞋子
				if (itemEntity.IsSomeSampling) {
					if (btn_bottom2.getVisibility() == View.GONE) {
						btn_bottom2.setVisibility(View.VISIBLE);
					}
				} else {// 如果不是鞋子
					if (btn_bottom2.getVisibility() == View.VISIBLE) {
						btn_bottom2.setVisibility(View.GONE);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case REQUEST_CODE_DIRECT_GETNEW:// 取新请求
		case REQUEST_CODE_DIRECT_GETBOX:// 取鞋盒
			getNewDialog.dismiss();
			clearDate();
			Toast.makeText(mActivity, net.message, Toast.LENGTH_SHORT).show();
			// 刷新库存
			sendSearchSku();
			break;
		case WHAT_NET_GET_STOCK_FUZZY:// 模糊查找
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
				scaner_tip.setVisibility(View.GONE);
			} else {
				searchSkuList.clear();
				itemListAdapter.notifyDataSetChanged();
				search_list.setVisibility(View.GONE);
			}
			break;
		}

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

	/* 扫码处理 */
	private void dealBarCode(String skuCodeStr) {
		if (!SkuUtil.isWarehouse(skuCodeStr)) {
			ap.getsoundPool(ap.Sound_sku);
			if (getNewDialog.isShowing()) {
				ap.getsoundPool(ap.Sound_error);
				Toast.makeText(mActivity, "请扫库位码！", Toast.LENGTH_SHORT).show();
				return;
			}
			skuCode = skuCodeStr;
			sendSearchSku();
		} else {
			ap.getsoundPool(ap.Sound_location);
			storage = skuCodeStr;
			if (getNewDialog.isShowing()) {
				skuEditText.setText(storage);
			}
		}
	}

	/* 发送查询请求 */
	private void sendSearchSku() {
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK, NET_METHOD_POST, false);
	}

	/* 发送取新请求 */
	private void sendGetNew() {
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetNew, REQUEST_CODE_DIRECT_GETNEW, NET_METHOD_POST, false);
	}

	/* 发送取鞋盒请求 */
	private void sendGetBox() {
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetOther, REQUEST_CODE_DIRECT_GETBOX, NET_METHOD_POST, false);
	}

	/**
	 * fragment传递过来的选中项
	 */
	@Override
	public void onItemSelected(ItemEntity itemEntity, SkuEntity skuEntity) {
		skuCode = skuEntity.SKUCode;
		if (Integer.valueOf(skuEntity.SKUCount) > 0) {
			btn_bottom1.setEnabled(true);
		} else {
			btn_bottom1.setEnabled(false);
		}
		// 取鞋盒分类
		if (skuEntity.SampleList.size() == 0) {
			btn_bottom2.setEnabled(false);
		} else {
			btn_bottom2.setEnabled(true);
			if (skuEntity.SampleList.size() == 2) {
				radioZero.setVisibility(View.VISIBLE);
				radioOne.setVisibility(View.VISIBLE);
			} else if (Float.valueOf(skuEntity.SampleList.get(0).EndQty) == 1) {// 空鞋盒
				radioZero.setVisibility(View.VISIBLE);
				radioOne.setVisibility(View.GONE);
			} else if (Float.valueOf(skuEntity.SampleList.get(0).EndQty) == 0.5) {// 鞋盒一只鞋
				radioZero.setVisibility(View.GONE);
				radioOne.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			onBackPressed();
			break;
		case R.id.btn_bottomBtn1:// 取新
			getNewType = 1;
			if (radioGroup.getVisibility() == View.VISIBLE) {
				radioGroup.setVisibility(View.GONE);
			}
			getNewDialog.show();
			break;
		case R.id.btn_bottomBtn2:// 取鞋盒
			getNewType = 2;
			if (radioGroup.getVisibility() == View.GONE) {
				radioGroup.setVisibility(View.VISIBLE);
			}
			getNewDialog.show();
			break;
		case R.id.btn_cancle:// 取消
			getNewDialog.dismiss();
			clearDate();
			break;
		case R.id.btn_sure:// 确定
			storage = skuEditText.getText(mActivity);
			if (storage == null || storage.length() < 7) {
				Toast.makeText(mActivity, "请正确输入库位号!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (getNewType == 1) {
				/* 发送取新请求 */
				sendGetNew();
			} else if (getNewType == 2) {
				if (!(radioZero.isChecked() || radioOne.isChecked())) {
					Toast.makeText(mActivity, "请选择鞋盒类型!", Toast.LENGTH_SHORT).show();
					return;
				}
				/* 发送取鞋盒请求 */
				sendGetBox();
			}
			break;
		default:
			break;
		}

	}

	// 修改键盘搜索键
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

	// 文字改变监听
	class EditTextWatcher implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (!s.toString().equals("")) {
				searchSku = s.toString();
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKUFuzzy, WHAT_NET_GET_STOCK_FUZZY, false, NET_METHOD_POST, false);
			} else {
				scaner_tip.setVisibility(View.VISIBLE);
				searchSkuList.clear();
				itemListAdapter.notifyDataSetChanged();
				search_list.setVisibility(View.GONE);
			}

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * 隐藏软键盘
	 */
	private void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	/**
	 * 取新弹窗
	 */
	private void createGetNewDialog() {
		View view = inflater.inflate(R.layout.dialog_getnew, null);
		getNewDialog = new BaseDialog(mActivity, view);
		getNewDialog.setCanceledOnTouchOutside(false);
		radioGroup = (RadioGroup) view.findViewById(R.id.sheobox_layout);
		radioZero = (RadioButton) view.findViewById(R.id.radio_zero);
		radioOne = (RadioButton) view.findViewById(R.id.radio_one);
		skuEditText = (SkuEditText) view.findViewById(R.id.text_shelf);
		btn_cancle = (TextView) view.findViewById(R.id.btn_cancle);
		btn_sure = (TextView) view.findViewById(R.id.btn_sure);
		btn_cancle.setOnClickListener(this);
		btn_sure.setOnClickListener(this);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radio_zero:
					SKUCount = "1";
					break;
				case R.id.radio_one:
					SKUCount = "0.5";
					break;
				}
			}
		});
	}

	/* 清除数据 */
	private void clearDate() {
		skuEditText.setText("");
		radioGroup.clearCheck();
	}

}
