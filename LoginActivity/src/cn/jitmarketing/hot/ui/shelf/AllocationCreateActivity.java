package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.platformtools.Log;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.OldSkuShelvesAdapter;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

public class AllocationCreateActivity extends BaseSwipeOperationActivity implements OnClickListener {
	
	@ViewInject(R.id.shelf_title)
	TitleWidget shelf_title;
	@ViewInject(R.id.only_list)
	ListView only_list;
	/**
	 * 退仓
	 */
	@ViewInject(R.id.text_one)
	TextView text_one;
	/**
	 * 手工
	 */
	@ViewInject(R.id.text_two)
	TextView text_two;
	/**
	 * 调拨
	 */
	@ViewInject(R.id.text_three)
	TextView text_three;
	private SkuEditText sku_shelf;
	private TextView sku_really_all_count;
	private TextView sku_scan_detail;
	private OldSkuShelvesAdapter listAdapter;
	private ArrayList<ShelfBean> wareList = new ArrayList<ShelfBean>();
	private ArrayList<SkuBean> skuList = new ArrayList<SkuBean>();
	private ArrayList<SkuBean> newList = new ArrayList<SkuBean>();
	private boolean canReceive;
	private static final int WHAT_NET_SAVE = 0x10;//调拨
	private static final int FOR_RESULT_HAND = 0x12;
	private static final int WHAT_NET_TEMP_SAVE = 0x11;//暂存
	private static final int WHAT_NET_TEMP_LIST = 0x14;//暂存列表
	private static final int FOR_RESULT_DETAIL = 0x15;
	private ArrayList<String> stringlist;
	private String s;
	private HotApplication ap;
	Intent intent = new Intent();
	public static AllocationCreateActivity allocationCreateActivity;
	private String AllocationOrderID;//单号，可以暂存

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_allocation_create;
	}

	@Override
	protected void exInitView() {
		allocationCreateActivity = this;
		ap = (HotApplication) getApplication();
		shelf_title.setOnLeftClickListner(this);
		text_one.setOnClickListener(this);
		text_two.setOnClickListener(this);
		text_three.setOnClickListener(this);
		stringlist = new ArrayList<String>();
		listAdapter = new OldSkuShelvesAdapter(getLayoutInflater(), newList);
		View footview = getLayoutInflater().inflate(R.layout.sku_shelves_foot,
				null);
		sku_shelf = (SkuEditText) footview.findViewById(R.id.sku_shelf);
		sku_shelf.stopEdit();
		sku_really_all_count = (TextView) footview
				.findViewById(R.id.sku_really_all_count);
		sku_scan_detail = (TextView) footview
				.findViewById(R.id.sku_scan_detail);
		sku_scan_detail.setOnClickListener(this);
		View headView = getLayoutInflater().inflate(
				R.layout.sku_shelves_item_layout, null);
		only_list.addHeaderView(headView);
		only_list.addFooterView(footview);
		only_list.setAdapter(listAdapter);
		sku_really_all_count.setText("" + 0);
		if(getIntent().getExtras() == null) {
			AllocationOrderID = "";
		} else {
			AllocationOrderID = getIntent().getExtras().getString("AllocationOrderID");
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ALLOCATION_TEMP_LIST, WHAT_NET_TEMP_LIST, NET_METHOD_POST, false);
			
		}
	}
	
	private ArrayList<InStockDetail> upload(List<ShelfBean> wList) {
		ArrayList<InStockDetail> upLoadList = new ArrayList<InStockDetail>();
		for(ShelfBean sfb : wList) {
			for(SkuBean sb : sfb.skuList) {
				InStockDetail detail = new InStockDetail(sb.skuCode, sb.skuCount, sfb.shelfCode);
				upLoadList.add(detail);
			}
			
		}
		return upLoadList;
	}

	@Override
	protected void onResume() {
		canReceive = true;
		super.onResume();
		//日志操作
		LogUtils.logOnFile("->创建调拨单");
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
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		dealBarCode(barcode);
	}
	
//	@Override
//	public void fillCode(String code) {
//		dealBarCode(code);
//	}
	
	private void dealBarCode(String barcode) {
		if (canReceive) {
			s = sku_shelf.getText(this);
			if ((!SkuUtil.isWarehouse(barcode)) && s.length() == 0) {//初次扫描商品码
				ap.getsoundPool(ap.Sound_sku);
				stringlist.add(barcode);
				SkuBean sb = new SkuBean(barcode, 1);
				SkuUtil.getSku(skuList, sb);
				SkuUtil.skuShowList(skuList, newList);
				listAdapter.notifyDataSetChanged();
				sku_really_all_count.setText(""
						+ SkuUtil.getSkuCount(skuList));
			} else if ((!SkuUtil.isWarehouse(barcode)) && s.length() > 0) {//扫描完库位码 再次扫描商品码
				ap.getsoundPool(ap.Sound_sku);
				skuList.clear();
				sku_shelf.setText("");
				stringlist.add(barcode);
				SkuBean sb = new SkuBean(barcode, 1);
				SkuUtil.getSku(skuList, sb);
				SkuUtil.skuShowList(skuList, newList);
				listAdapter.notifyDataSetChanged();
				sku_really_all_count.setText(""
						+ SkuUtil.getSkuCount(skuList));
			} else if (SkuUtil.isWarehouse(barcode)
					&& stringlist.size() > 0) {//扫描库位
				ap.getsoundPool(ap.Sound_location);
				sku_shelf.setText(barcode);
				// 遍历寻找库位是否存在
				for(int i=0; i<skuList.size(); i++) {
					skuList.get(i).skuShelfLocation = barcode;
				}
				SkuUtil.addcheckShelf(wareList, barcode, skuList);
				stringlist.clear();
			}
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_TEMP_LIST:
			return WarehouseNet.tempList(AllocationOrderID);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		//操作日志
    	LogUtils.logOnFile("调拨单->添加->创建调拨单->调拨"+message);
		switch (what) {
		case WHAT_NET_SAVE:
			Ex.Toast(mContext).showLong("你的网速不太好，上架失败，请稍后重试");
			break;
		case WHAT_NET_TEMP_LIST:
			Ex.Toast(mContext).showLong("获取暂存数据出现异常");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			//操作日志
	    	LogUtils.logOnFile("调拨单->添加->创建调拨单->调拨"+net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_SAVE:
		case WHAT_NET_TEMP_SAVE:
			Ex.Toast(mActivity).show(net.message);
			//操作日志
	    	LogUtils.logOnFile("调拨单->添加->创建调拨单->调拨"+net.message);
			AllocationActivity.allocation = true;
			this.finish();
			break;
		case WHAT_NET_TEMP_LIST:
			String str = mGson.toJson(net.data);
			final List<InStockDetail> tempList = mGson.fromJson(str, new TypeToken<List<InStockDetail>>() {}.getType());
			wareList.clear();
			wareList.addAll(changeDetailList(tempList));
			skuList.clear();
			if(wareList.size() > 0) {
				skuList.addAll(wareList.get(wareList.size()-1).skuList);
				sku_shelf.setText(wareList.get(wareList.size()-1).shelfCode);
			}
			SkuUtil.skuShowList(skuList, newList);
			listAdapter.notifyDataSetChanged();
			sku_really_all_count.setText("" + SkuUtil.getSkuCount(skuList));
			break;
		}
	}
	
	private ArrayList<ShelfBean> changeDetailList(List<InStockDetail> tempList) {
		ArrayList<ShelfBean> list = new ArrayList<ShelfBean>();
		Map<String, List<SkuBean>> map = new HashMap<String, List<SkuBean>>();
		for(InStockDetail detail : tempList) {
			if(map.get(detail.ShelfLocationCode) == null) {
				List<SkuBean> skuList = new ArrayList<SkuBean>();
				SkuBean sb = new SkuBean(detail.SKUCode, detail.SKUCount);
				sb.skuShelfLocation = detail.ShelfLocationCode;
				skuList.add(sb);
				map.put(detail.ShelfLocationCode, skuList);
			} else {
				List<SkuBean> skuList = map.get(detail.ShelfLocationCode);
				SkuBean sb = new SkuBean(detail.SKUCode, detail.SKUCount);
				sb.skuShelfLocation = detail.ShelfLocationCode;
				skuList.add(sb);
			}
		}
		for(Map.Entry<String, List<SkuBean>> entry : map.entrySet()){ 
			ShelfBean bean = new ShelfBean(entry.getKey(), entry.getValue());
			list.add(bean);
		} 
		return list;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setTitle(R.string.noteTitle)
			.setMessage("是否暂存数据？")
			.setPositiveButton(R.string.sureTitle,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int whichButton) {
					//日志操作
					LogUtils.logOnFile("创建调拨单->暂存弹框->确定");
					startJsonTask(HotConstants.Global.APP_URL_USER
							+ HotConstants.Shelf.ALLOCATION_TEMP_SAVE, WHAT_NET_TEMP_SAVE, true,
							NET_METHOD_POST, SaveListUtil.skuTempShelfSave(upload(wareList), AllocationOrderID == null ? "" : AllocationOrderID), false);
				}
			}).setNegativeButton(R.string.cancelTitle,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					//日志操作
					LogUtils.logOnFile("创建调拨单->暂存弹框->取消");
					AllocationCreateActivity.this.finish();
					dialog.dismiss();
				}
			}).show();
			return true;
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			if(wareList == null || wareList.size() == 0) {
				this.finish();
			} else {
			new AlertDialog.Builder(this)
			.setTitle(R.string.noteTitle)
			.setMessage("是否暂存数据？")
			.setPositiveButton(R.string.sureTitle,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int whichButton) {
					//日志操作
					LogUtils.logOnFile("创建调拨单->暂存弹框->确定");
					startJsonTask(HotConstants.Global.APP_URL_USER
							+ HotConstants.Shelf.ALLOCATION_TEMP_SAVE, WHAT_NET_TEMP_SAVE, true,
							NET_METHOD_POST, SaveListUtil.skuTempShelfSave(upload(wareList), AllocationOrderID == null ? "" : AllocationOrderID), false);
				}
			}).setNegativeButton(R.string.cancelTitle,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					//日志操作
					LogUtils.logOnFile("创建调拨单->暂存弹框->取消");
					AllocationCreateActivity.this.finish();
					dialog.dismiss();
				}
			}).show();
			}
			break;
		case R.id.text_one:
//			new AlertDialog.Builder(this)
//			.setTitle(R.string.noteTitle)
//			.setMessage("确认退仓？")
//			.setPositiveButton(R.string.sureTitle,
//					new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,
//						int whichButton) {
//					if (!StringUtil.isBlank(sku_shelf.getText(mContext))
//							&& wareList.size() != 0) {
//						startJsonTask(HotConstants.Global.APP_URL_USER
//								+ HotConstants.Shelf.ALLOCATION_SAVE, WHAT_NET_SAVE, true,
//								NET_METHOD_POST, SaveListUtil.skuShelfSave(upload(wareList), null, AllocationOrderID), false);
//					} else if (skuList.size() == 0) {
//						Ex.Toast(mActivity).show("请先扫描商品码");
//					} else if (StringUtil.isBlank(sku_shelf.getText(mContext))) {
//						Ex.Toast(mActivity).show("请先扫描商品码");
//					}
//				}
//			}).setNegativeButton(R.string.cancelTitle,
//					new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,
//						int which) {
//					dialog.dismiss();
//				}
//			}).show();
			break;
		case R.id.sku_scan_detail://明细
			//日志操作
			LogUtils.logOnFile("创建调拨单->明细");
			Bundle bundle = new Bundle();
			bundle.putSerializable("list", wareList);
			intent.putExtra("bundle", bundle);
			intent.setClass(this,
					ShelfDetailActivity.class);
//			startActivity(intent);
			startActivityForResult(intent, FOR_RESULT_DETAIL);
			break;
		case R.id.text_two://手工
			//  手工添加数据
			//日志操作
			LogUtils.logOnFile("创建调拨单->手工");
			intent.setClass(this,
					HandShelfActivity.class);
			intent.putExtra("type", "TYPE_ALLOCATION");
			startActivityForResult(intent, FOR_RESULT_HAND);
			//关闭扫描服务
//			if(serviceConnection!=null){
//				unbindService(serviceConnection);
//				stopService(new Intent(this,ScanService.class));
//			}
			break;
		case R.id.text_three://调拨
			//日志操作
			LogUtils.logOnFile("创建调拨单->调拨");
			if(wareList == null || wareList.size() == 0) {
				Ex.Toast(this).show("请先扫描库位码");
				return;
			}
			intent.setClass(this, AllocationSelectActivity.class);
			Bundle bd = new Bundle();
			bd.putSerializable("List", upload(wareList));
			bd.putString("AllocationOrderID", AllocationOrderID);
			intent.putExtras(bd);
			startActivity(intent);
//			startActivityForResult(intent, FOR_RESULT_HAND);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == FOR_RESULT_HAND) {
			String location = data.getStringExtra("location");
			String sku = data.getStringExtra("sku");
			String count = data.getStringExtra("count");
//			SkuBean sb = new SkuBean(sku, Integer.valueOf(count));
//			sb.skuShelfLocation = location;
//			SkuUtil.HandgetSku(skuList, sb);
//			SkuBean ssb = new SkuBean(sku, Integer.valueOf(count));
//			ssb.skuShelfLocation = location;
//			SkuUtil.skuShowList(skuList, newList);
//			listAdapter.notifyDataSetChanged();
//			sku_shelf.setText(location);
//			SkuUtil.handaddShelf(wareList, location, ssb);
			
			stringlist.clear();
			SkuBean sb = new SkuBean(sku, Integer.valueOf(count));
			sb.skuShelfLocation = location;
			if(sku_shelf.getText(this) != null && !sku_shelf.getText(this).equals("")) {
				skuList.clear();
				SkuUtil.getSku(skuList, sb);
			} else {
//				for(SkuBean ss : skuList) {
//					ss.skuShelfLocation = sb.skuShelfLocation;
//				}
				for(int i=0; i<skuList.size(); i++) {
					skuList.get(i).skuShelfLocation = location;
				}
				SkuUtil.HandgetSku(skuList, sb);
			}
			SkuUtil.skuShowList(skuList, newList);
			SkuUtil.addcheckShelf(wareList, location, skuList);
			listAdapter.notifyDataSetChanged();
			sku_really_all_count.setText("" + SkuUtil.getSkuCount(skuList));
			sku_shelf.setText(location);

		} 
		if(resultCode == RESULT_OK && requestCode == FOR_RESULT_DETAIL) {
			boolean isNoValue = data.getBooleanExtra("isNoValue", false);
			if(!isNoValue) {
				wareList.clear();
				wareList.addAll((ArrayList<ShelfBean>) data.getSerializableExtra("wList"));
				skuList.clear();
				for(ShelfBean sf : wareList) {
					for(SkuBean bean : sf.skuList) {
						skuList.add(bean);
					}
				}
				List<SkuBean> show = new ArrayList<SkuBean>();//显示的
				for(SkuBean ss : skuList) {
					if(ss.skuShelfLocation.equals(sku_shelf.getText(this))) {
						show.add(ss);
					}
				}
				SkuUtil.skuShowList(show, newList);
			
				listAdapter.notifyDataSetChanged();
				sku_really_all_count.setText("" + SkuUtil.getSkuCount(skuList));
			}
			
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
		//日志操作
		LogUtils.logOnFile("创建调拨单->扫码："+code);
	}
}