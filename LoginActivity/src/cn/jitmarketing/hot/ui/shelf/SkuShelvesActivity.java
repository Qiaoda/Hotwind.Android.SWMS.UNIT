package cn.jitmarketing.hot.ui.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import java.util.ArrayList;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;

/**
 * 商品上架
 */
public class SkuShelvesActivity extends BaseSwipeOperationActivity  implements OnClickListener{

	private static final int WHAT_NET_SKU_SHELF = 0x10;
	@ViewInject(R.id.text_title)
	TextView text_title;
	@ViewInject(R.id.btn_back)
	ImageView btn_back;

	@ViewInject(R.id.sku_code)
	TextView sku_code;
	@ViewInject(R.id.shelf_code)
	TextView shelf_code;
	@ViewInject(R.id.sku_shelf_num)
	TextView sku_shelf_num;
	@ViewInject(R.id.selves_detail)
	TextView selves_detail;
	@ViewInject(R.id.selves_ok)
	TextView selves_ok;

	private ArrayList<ShelfBean> wareList = new ArrayList<ShelfBean>();
	private ArrayList<SkuBean> skuList = new ArrayList<SkuBean>();
	private String skuStr;
	private String shelfStr;
	private boolean lastestIsSku;
	HotApplication ap;
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
		// TODO Auto-generated method stub
		return R.layout.activity_sku_selves;
	}

	@Override
	protected void exInitView() {
		ap=(HotApplication) getApplication();
		text_title.setText("上架");
		btn_back.setOnClickListener(this);

		selves_detail.setOnClickListener(this);
		selves_ok.setOnClickListener(this);
	}

	@Override
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		dealBarCode(barcode);
		
		
//		if(null != code){
//			String barcode = new String(code, 0, codelen).toUpperCase().trim();
//			if(SkuUtil.isWarehouse(barcode) && lastestIsSku){
//				ap.getsoundPool(ap.Sound_location);
//				lastestIsSku = false;
//				shelfStr = barcode;
//				shelf_code.setText(shelfStr);
//				//遍历寻找库位是否存在
//				SkuUtil.addShelf(wareList, shelfStr, skuStr);	
//			}else if(SkuUtil.isWarehouse(barcode)){
//				shelfStr = barcode;
//				ap.getsoundPool(ap.Sound_location);
//			}else{
//				ap.getsoundPool(ap.Sound_sku);
//				lastestIsSku = true;
//				skuStr = barcode;
//				SkuUtil.addSku(skuList, skuStr);
//				sku_shelf_num.setText("共" + skuList.size() + "件");
//			}
//
//		}


	}

//	@Override
//	public void fillCode(String code) {
//		dealBarCode(code);
//	}
	
	private void dealBarCode(String barcode) {
		if(SkuUtil.isWarehouse(barcode) && lastestIsSku){
			ap.getsoundPool(ap.Sound_location);
			lastestIsSku = false;
			shelfStr = barcode;
			shelf_code.setText(shelfStr);
			//遍历寻找库位是否存在
			SkuUtil.addShelf(wareList, shelfStr, skuStr);	
		}else if(SkuUtil.isWarehouse(barcode)){
			shelfStr = barcode;
			ap.getsoundPool(ap.Sound_location);
		}else{
			ap.getsoundPool(ap.Sound_sku);
			lastestIsSku = true;
			skuStr = barcode;
			SkuUtil.addSku(skuList, skuStr);
			sku_shelf_num.setText("共" + skuList.size() + "件");
		}
	}
	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_SKU_SHELF:
			return WarehouseNet.skuShelf(shelfStr, wareList);
		}
		return null;
	}

	@Override
	public Map<String, Object> onStartNetParam(int what) {
		// TODO Auto-generated method stub
		return null;
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
		case WHAT_NET_SKU_SHELF:
			if(null == net.data){
				Ex.Toast(mActivity).showLong(net.message);
				return ;
			}
			
			Ex.Toast(mActivity).showLong(net.data.toString());
			this.finish();
			break;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.selves_detail:
			Bundle bundle = new Bundle();
			bundle.putSerializable("wareList", wareList);
			Ex.Activity(mActivity).start(SkuShelvesInfoActivity.class, bundle);
			break;
		case R.id.selves_ok:
			//TODO: 提交上架数据
//			startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SKUShelves, 
//    				WHAT_NET_SKU_SHELF, NET_METHOD_POST, false);
			startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.SKUShelves, 
    				WHAT_NET_SKU_SHELF, true, NET_METHOD_POST, SaveListUtil.skuShelfSave(shelfStr, wareList), false);
			break;
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
	}

}
