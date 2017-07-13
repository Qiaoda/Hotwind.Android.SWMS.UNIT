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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.jitmarketing.hot.ui.sku.SendRequestDialog;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * 出样
 */
public class OutSampleActivity extends BaseSwipeOperationActivity implements
		View.OnClickListener, QueryResultFragment.OnItemSelectedListener {

	private static final int WHAT_NET_GET_STOCK = 0x10;
	private static final int REQUEST_CODE_DIRECT_WITHDRAW = 0x11;
	private static final int WHAT_NET_GET_STOCK_FUZZY = 0x13;// 模糊搜索

	@ViewInject(R.id.allocation_title)
	private TitleWidget allocation_title;
	@ViewInject(R.id.ll_searchBar)
	private LinearLayout ll_searchBar; // 搜索布局
	@ViewInject(R.id.check_stock_search_edt)
	private ClearEditText editText; // 搜索输入框
	@ViewInject(R.id.fl_content)
	private FrameLayout fl_content; // 内存布局
	@ViewInject(R.id.ll_bottomBar)
	private LinearLayout ll_bottomBar; // 底部按钮布局

	@ViewInject(R.id.btn_bottomBtn1)
	private Button btn_bottom1; // 底部按钮1-左
	@ViewInject(R.id.btn_bottomBtn2)
	private Button btn_bottom2; // 底部按钮2-中
	@ViewInject(R.id.btn_bottomBtn3)
	private Button btn_bottom3; // 底部按钮3-右
	// 搜索显示列表
	@ViewInject(R.id.search_list)
	private ListView search_list;

	private HotApplication ap;
	private String skuCodeStr;
	private List<SkuEntity> mList;

	private FragmentManager fm;
	private QueryResultFragment mFragment;

	// --------模糊搜索
	private String searchSku;
	private SearchSkuBean searchSkuBean;// sku搜索结果对象
	private ItemListAdapter itemListAdapter;
	private List<SearchSkuBean> searchSkuList = new ArrayList<SearchSkuBean>();

	@Override
	protected int exInitLayout() {
		return R.layout.activity_out_sample;
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
		mList = new ArrayList<SkuEntity>();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			skuCodeStr = bundle.getString("skuCode");
		}
	}

	@Override
    protected void exInitView() {
        ap = (HotApplication) getApplication();
        fm = getSupportFragmentManager();
        EventBus.getDefault().register(this);
        allocation_title.setOnLeftClickListner(this);
        btn_bottom1.setOnClickListener(this);
        btn_bottom2.setOnClickListener(this);
        btn_bottom3.setOnClickListener(this);
        editText.setOnKeyListener(onKeyListener);
        //文字改变监听
        editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(!s.toString().equals("")){
					searchSku=s.toString();
					startTask(HotConstants.Global.APP_URL_USER
                            + HotConstants.SKU.GetStoreForSKUFuzzy,
                    WHAT_NET_GET_STOCK_FUZZY, false, NET_METHOD_POST, false);
				}else{
					searchSkuList.clear();
					itemListAdapter.notifyDataSetChanged();
					search_list.setVisibility(View.GONE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});

        if (!TextUtils.isEmpty(skuCodeStr))
            fillCode(skuCodeStr);
//        初始化搜索列表
        itemListAdapter=new ItemListAdapter(this, searchSkuList);
        search_list.setAdapter(itemListAdapter);
        search_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				hideSoftKeyBoard(mActivity, search_list);
				if(searchSkuList.get(position).SKUCode!=null){
					skuCodeStr=searchSkuList.get(position).SKUCode;
					startTask(HotConstants.Global.APP_URL_USER
	    					+ HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK,false,
	    					NET_METHOD_POST, false);
				}
				
			}
		});
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
				//隐藏搜索列表
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
				String skuListStr = new JSONObject(mGson.toJson(net.data))
						.getString("skus");
				String itemStr = new JSONObject(mGson.toJson(net.data))
						.getString("item");

				ItemEntity itemEntity = mGson.fromJson(itemStr,
						ItemEntity.class);
				mList.clear();
				mList.addAll((ArrayList<SkuEntity>) mGson.fromJson(skuListStr,
						new TypeToken<List<SkuEntity>>() {
						}.getType()));
				for (int i = 0; i < mList.size(); i++) {
					if (mList.get(i).SKUCode.equals(skuCodeStr)) {
						HotConstants.SELECTED = i;
						break;
					}
				}
				//隐藏速锁列表
				searchSkuList.clear();
				search_list.setVisibility(View.GONE);
				//加载碎片
				mFragment = new QueryResultFragment(itemEntity, mList, this);
				FragmentTransaction transaction = fm.beginTransaction();
				transaction.replace(R.id.fl_content, mFragment);
				transaction.commit();

				this.onItemSelected(itemEntity,
						mList.get(HotConstants.SELECTED));
				// 如果是鞋子
				if (itemEntity.IsSomeSampling) {
					btn_bottom1.setText("出一只");
					btn_bottom2.setText("出一双");
					btn_bottom3.setText("出另一只");
				}
				// 如果不是鞋子
				else {
					btn_bottom1.setText("出一个");
					btn_bottom2.setText("出多个");
					btn_bottom3.setText("直接出");
				}
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
			searchSkuList.addAll((List<SearchSkuBean>) mGson.fromJson(itemStr,
					new TypeToken<List<SearchSkuBean>>() {
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
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK,
					NET_METHOD_POST, false);
		} else {
			ap.getsoundPool(ap.Sound_location);
		}
	}

	@Override
	public void onClick(View view) {
		Bundle bd = new Bundle();
		SampleEntity sampleEntity = new SampleEntity();

		switch (view.getId()) {
		case R.id.lv_left:
			finish();
			break;
		case R.id.btn_bottomBtn1:
			if (mFragment.getItemEntity().IsSomeSampling) { // 鞋类 —— 出一只
				sampleEntity.EndQty = "0.5";
			} else { // 非鞋类 —— 出一个
				sampleEntity.EndQty = "1";
			}
			sampleEntity.isCanBatch = false;
			sampleEntity.Type = "2";
			bd.putSerializable("sampleEntity", sampleEntity);
			bd.putString("skuCodeStr", skuCodeStr);
			bd.putSerializable("sku", mFragment.getCurSkuEntity());

			Ex.Activity(mActivity).start(SendRequestDialog.class, bd);
			break;
		case R.id.btn_bottomBtn2:
			if (mFragment.getItemEntity().IsSomeSampling) { // 鞋类 —— 出一双
				sampleEntity.isCanBatch = false;
			} else { // 非鞋类 —— 出多个
				sampleEntity.isCanBatch = true;
			}
			sampleEntity.Type = "2";
			sampleEntity.EndQty = "1";
			bd.putSerializable("sampleEntity", sampleEntity);
			bd.putString("skuCodeStr", skuCodeStr);
			bd.putSerializable("sku", mFragment.getCurSkuEntity());

			Ex.Activity(mActivity).start(SendRequestDialog.class, bd);
			break;
		case R.id.btn_bottomBtn3:
			if (mFragment.getItemEntity().IsSomeSampling) { // 鞋类 —— 出另一只
				bd.putInt("type", OutAndWithdrawActivity.SHOE_OUT);
				bd.putSerializable("sampleEntity", sampleEntity);
				bd.putSerializable("sku", mFragment.getCurSkuEntity());
				bd.putSerializable("item", mFragment.getItemEntity());
				bd.putString("skuCodeStr", skuCodeStr);

				Ex.Activity(mActivity).start(OutAndWithdrawActivity.class, bd);
			} else { // 非鞋类 —— 直接出
				// Toast.makeText(this, "直接出", Toast.LENGTH_SHORT).show();
				bd.putSerializable("item", mFragment.getItemEntity());
				bd.putSerializable("sku", mFragment.getCurSkuEntity());
				bd.putSerializable("skuCodeStr", skuCodeStr);
				bd.putSerializable("skuCode", skuCodeStr);
				bd.putInt("type", DirectWithdrawActivity.OUT);
				bd.putInt("selCount",
						Integer.valueOf(mFragment.getCurSkuEntity().SHU));
				// Ex.Activity(mActivity).start(DirectWithdrawActivity.class,
				// bd);
				Ex.Activity(mActivity).startForResult(
						DirectWithdrawActivity.class, bd,
						REQUEST_CODE_DIRECT_WITHDRAW);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_DIRECT_WITHDRAW
				&& resultCode == RESULT_OK) {
			startTask(HotConstants.Global.APP_URL_USER
					+ HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK,
					false, NET_METHOD_POST, false);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	private void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	public void onItemSelected(ItemEntity itemEntity, SkuEntity skuEntity) {
		if (Integer.valueOf(skuEntity.SHU) > 0) {
			btn_bottom1.setEnabled(true);
			btn_bottom2.setEnabled(true);
			btn_bottom3.setEnabled(true);
		} else {
			btn_bottom1.setEnabled(false);
			btn_bottom2.setEnabled(false);
			btn_bottom3.setEnabled(false);
		}
		if (itemEntity.IsSomeSampling)
			btn_bottom3.setEnabled(false);
		if (Integer.valueOf(skuEntity.ZHI) > 0) {
			btn_bottom3.setEnabled(true);
		}
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
	                hideSoftKeyBoard(mActivity,editText);
	                
	                skuCodeStr = editText.getText().toString();
	                startTask(HotConstants.Global.APP_URL_USER
	    					+ HotConstants.SKU.GetStoreForSKU, WHAT_NET_GET_STOCK,false,
	    					NET_METHOD_POST, false);
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
