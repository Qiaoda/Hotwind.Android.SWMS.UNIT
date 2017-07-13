package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockTakingWarehouseListAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.StockTakingWarehouseEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;
import cn.jitmarketing.hot.view.sortlistview.CharacterParser;
import cn.jitmarketing.hot.view.sortlistview.PinyinComparator;
import cn.jitmarketing.hot.view.sortlistview.SideBar;
import cn.jitmarketing.hot.view.sortlistview.SideBar.OnTouchingLetterChangedListener;
import cn.jitmarketing.hot.view.sortlistview.SortAdapter;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

/** 库存盘点列表界面 */
public class StockTakingWarehouseActivity extends BaseSwipeOperationActivity implements OnClickListener, OnItemClickListener {

	private static final int WHAT_NET_STOCK_LIST = 0x10;
	@ViewInject(R.id.stock_taking_title)
	TitleWidget stock_taking_title;
	@ViewInject(R.id.stock_list)
	ListView stock_list;
	@ViewInject(R.id.sidebar)
	private SideBar sideBar;
	@ViewInject(R.id.dialog)
	private TextView dialog;
	@ViewInject(R.id.sku_search_edt)
	private ClearEditText clearEditText;
	private SortAdapter adapter;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private StockTakingWarehouseListAdapter awarehouseAdapter;
	private List<StockTakingWarehouseEntity> warehouseList;
	private boolean canReceive;
	public static boolean refresh = false;
	private HotApplication ap;
	// 记录上次盘点库位
	private int lastPosition = 0;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.stock_taking_warehouse;
	}

	@Override
	protected void exInitView() {
		initViews();
		ap = (HotApplication) getApplication();
		awarehouseAdapter = new StockTakingWarehouseListAdapter(this, warehouseList);
		canReceive = true;
		stock_taking_title.setText("库位盘点");
		stock_taking_title.setOnLeftClickListner(this);
		stock_list.setOnItemClickListener(this);
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.PlanStock, WHAT_NET_STOCK_LIST, NET_METHOD_POST, false);
	}

	private void initViews() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					stock_list.setSelection(position);
				}

			}
		});

		stock_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
				// Toast.makeText(getApplication(),
				// ((StockTakingWarehouseEntity)adapter.getItem(position)).getName(),
				// Toast.LENGTH_SHORT).show();
			}
		});

		// 根据输入框输入值的改变来过滤搜索
		clearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<StockTakingWarehouseEntity> filledData(List<StockTakingWarehouseEntity> date) {
		List<StockTakingWarehouseEntity> mSortList = new ArrayList<StockTakingWarehouseEntity>();

		for (int i = 0; i < date.size(); i++) {
			StockTakingWarehouseEntity sortModel = date.get(i);
			// if(date.get(i).ShelfLocationCode.equals("EX00001") ||
			// date.get(i).ShelfLocationCode.equals("SC00001"))//样品库放第一位
			if (date.get(i).ShelfLocationCode.equals("EX00001"))// 样品库放第一位
				sortModel.setName("#");
			else
				sortModel.setName(date.get(i).ShelfLocationName);
			// 汉字转换成拼音
			String pinyin = null;
			// if(date.get(i).ShelfLocationCode.equals("EX00001") ||
			// date.get(i).ShelfLocationCode.equals("SC00001"))//样品库放第一位
			if (date.get(i).ShelfLocationCode.equals("EX00001"))// 样品库放第一位
				pinyin = characterParser.getSelling("#");
			else
				pinyin = characterParser.getSelling(date.get(i).ShelfLocationName);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}
			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<StockTakingWarehouseEntity> filterDateList = new ArrayList<StockTakingWarehouseEntity>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = warehouseList;
		} else {
			filterDateList.clear();
			for (StockTakingWarehouseEntity sortModel : warehouseList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString()) || characterParser.getSelling(name).replace("-", "").startsWith(filterStr.toString()) || characterParser.getSelling(name).startsWith(filterStr.toString().toUpperCase()) || characterParser.getSelling(name).replace("-", "").startsWith(filterStr.toString().toUpperCase())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	@Override
	protected void onPause() {
		canReceive = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		canReceive = false;
		super.onStop();
	}

	@Override
	protected void onResume() {
		canReceive = true;
		super.onResume();
		if (refresh)
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.PlanStock, WHAT_NET_STOCK_LIST, NET_METHOD_POST, false);
	}

	@Override
	public void onReceiver(Intent intent) {
		// byte[] barocode = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// String code = new String(barocode, 0, codelen).toUpperCase().trim();
		// dealBarCode(code);
	}

	private void dealBarCode(String code) {
		if (canReceive) {
			if (SkuUtil.isWarehouse(code)) {
				int position = 0;
				boolean isExist = false;
				for (StockTakingWarehouseEntity entity : warehouseList) {
					if (entity.ShelfLocationCode.equals(code)) {
						gotoNext(entity, position);
						ap.getsoundPool(ap.Sound_location);
						isExist = true;
						break;
					}
					position++;
				}
				if (!isExist) {
					ap.getsoundPool(ap.Sound_error);
				}
			} else {
				ap.getsoundPool(ap.Sound_error);
			}
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			return WarehouseNet.getCheckStockList();
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			refresh = false;
			Ex.Toast(this).show("你的网速不太好，获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean isCache) {
		refresh = false;
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).show(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_STOCK_LIST:
			if (null == net.data) {
				Ex.Toast(mActivity).showLong(net.message);
				return;
			}
			String stockStr = mGson.toJson(net.data);
			warehouseList = mGson.fromJson(stockStr, new TypeToken<List<StockTakingWarehouseEntity>>() {
			}.getType());
			if (warehouseList != null && warehouseList.size() != 0) {
				sideBar.setVisibility(View.VISIBLE);
				clearEditText.setVisibility(View.VISIBLE);
			}
			warehouseList = filledData(warehouseList);

			// 根据a-z进行排序源数据
			Collections.sort(warehouseList, pinyinComparator);
			adapter = new SortAdapter(this, warehouseList);
			stock_list.setAdapter(adapter);
			// 滚到上次盘点库位
			stock_list.setSelection(lastPosition);
			List<String> list = new ArrayList<String>();
			for (StockTakingWarehouseEntity entity : warehouseList) {  
				if (!list.contains(entity.getSortLetters()))
					list.add(entity.getSortLetters());
			}
			SideBar.b = new String[list.size()];// 动态获取右边字母列表
			for (int i = 0; i < list.size(); i++) {
				SideBar.b[i] = list.get(i);
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
			intent.setClass(StockTakingWarehouseActivity.this, CreatStockTaskActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		lastPosition = position;
		StockTakingWarehouseEntity selectEntity = adapter.getStockList().get(position);
		gotoNext(selectEntity, position);
	}

	private void gotoNext(StockTakingWarehouseEntity selectEntity, int position) {
		Intent it = new Intent();
		it.setClass(this, CGYStockActivity.class);
		Bundle bd = new Bundle();
		bd.putString("ShelfLocationCode", selectEntity.ShelfLocationCode);
		// 日志操作
		LogUtils.logOnFile("盘点->库位盘点：" + selectEntity.ShelfLocationCode);
		if (selectEntity.IsConfirm == 1) {
			Ex.Toast(this).show("该库位盘点结束");
			return;
		} else {
			if (selectEntity.ShelfLocationCode.equals("EX00001")) {
				// if(selectEntity.ShelfLocationCode.equals("EX00001") ||
				// selectEntity.ShelfLocationCode.equals("SC00001")) {
				if (selectEntity.IsStartCheck == 1) { // 开启了
					bd.putBoolean("isSampleShelfCode", true);
					bd.putInt("differenceCount", selectEntity.DiffCount);
					bd.putString("differenceMoney", selectEntity.PriceCount);
					if (selectEntity.ShelfLocationCode.equals("EX00001"))
						bd.putBoolean("isSmall", false);
					else
						bd.putBoolean("isSmall", true);
				} else {
					if (selectEntity.ShelfLocationCode.equals("EX00001"))
						Ex.Toast(this).show("尚未开启样品库盘点");
					else
						Ex.Toast(this).show("尚未小商品盘点");
					return;
				}
			} else {
				bd.putBoolean("isSampleShelfCode", false);
				bd.putBoolean("isSmall", false);
				if (selectEntity.CheckStatus == 0) {// 未盘点
					bd.putBoolean("havePandian", false);
				} else {
					bd.putBoolean("havePandian", true);
				}
				bd.putInt("differenceCount", selectEntity.DiffCount);
				bd.putString("differenceMoney", selectEntity.PriceCount);
			}
		}
		it.putExtras(bd);
		startActivity(it);
	}

	@Override
	public void fillCode(String code) {
		dealBarCode(code);
	}
}
