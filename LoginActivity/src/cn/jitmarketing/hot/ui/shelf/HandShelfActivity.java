package cn.jitmarketing.hot.ui.shelf;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import java.util.ArrayList;
import java.util.List;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.CheckBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SkuEditText;

public class HandShelfActivity extends BaseSwipeOperationActivity implements
		OnClickListener {
	@ViewInject(R.id.hand_shelf_layout)
	LinearLayout hand_shelf_layout;
	@ViewInject(R.id.hand_shelf_location)
	SkuEditText hand_shelf_location;
	@ViewInject(R.id.hand_shelf_sku)
	ClearEditText hand_shelf_sku;
	@ViewInject(R.id.hand_shelf_count)
	ClearEditText hand_shelf_count;
	@ViewInject(R.id.hand_shelf_cancle)
	TextView hand_shelf_cancle;
	@ViewInject(R.id.hand_shelf_sure)
	TextView hand_shelf_sure;
	private HotApplication ap;

	private ArrayList<InStockDetail> detailList; // 明细list
	private ArrayList<InStockDetail> checkList; // 核对list
	private List<CheckBean> checkedList; // 核对过的list

	private int scanableCount; // 可以增加的数量
	private String curScanCode = ""; // 当前扫描的商品sku

	// 界面复用
	private static final String TYPE_DETAIL = "TYPE_DETAIL";// 从上架明细手动添加跳转过来
	private static final String TYPE_ALLOCATION = "TYPE_ALLOCATION";// 从调拨单手工添加跳转
	private String type = "";

	@Override
	protected void exInitAfter() {
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
		type = getIntent().getStringExtra("type");
		/* 从上架明细手动添加跳转过来 */
		if (type.equals(TYPE_DETAIL)) {
			detailList = (ArrayList<InStockDetail>) getIntent()
					.getSerializableExtra("detailList");
			checkList = (ArrayList<InStockDetail>) getIntent()
					.getSerializableExtra("checkList");
			Log.i("SHANGJIA", "detailList" + detailList.size() + "checkList"
					+ checkList.size() + "");

			checkedList = SkuUtil.cbCheck(detailList, checkList);
		}
		/* 从调拨单位置跳转 */
		if (type.equals(TYPE_ALLOCATION)) {

		}
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_hand_shelf;
	}

	@Override
	public void onReceiver(Intent intent) {
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// String barcode = new String(code, 0, codelen).toUpperCase().trim();
		// if (!SkuUtil.isWarehouse(barcode)) {
		// hand_shelf_sku.setText(barcode);
		// ap.getsoundPool(ap.Sound_sku);
		// } else {
		// ap.getsoundPool(ap.Sound_location);
		// hand_shelf_location.setText(barcode);
		// }
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				(int) (ConstValue.SCREEN_WIDTH * 0.9),
				(int) (ConstValue.SCREEN_HEIGHT * 0.6));
		hand_shelf_layout.setLayoutParams(params);
		hand_shelf_cancle.setOnClickListener(this);
		hand_shelf_sure.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hand_shelf_cancle:
			//日志操作
        	LogUtils.logOnFile("~->添加->取消");
			this.finish();
			break;
		case R.id.hand_shelf_sure:

			if (!hand_shelf_location.invalid()) {
				Ex.Toast(mContext).show(
						getResources().getString(R.string.validat_sku));
				return;
			}
			String location = hand_shelf_location.getText(this).toString()
					.toUpperCase();
			String sku = hand_shelf_sku.getText().toString().toUpperCase();
			String count = hand_shelf_count.getText().toString();
			if (location.equals("") || sku.equals("") || count.equals("")
					|| count.equals("0")) {
				Ex.Toast(mActivity).show("输入框内容不要为空或0");
			}
			// 上架操作
			if (type.equals(TYPE_DETAIL)) {
				addData();
				if (count.length() > 0
						&& Integer.valueOf(count) > scanableCount) {
					Ex.Toast(mActivity).show(
							"商品:" + sku + "最多可添加" + scanableCount + "个");
					return;
				}
				//日志操作
            	LogUtils.logOnFile("~->添加->SKU:"+sku+"数量："+count+"库位："+location);
			}
			//日志操作
        	LogUtils.logOnFile("~->添加->SKU:"+sku+"数量："+count+"库位："+location);
			// 调拨操作&&上架共同操作
			Intent intent = new Intent();
			intent.putExtra("location", location);
			intent.putExtra("sku", sku);
			intent.putExtra("count", count);
			setResult(RESULT_OK, intent);
			this.finish();
			break;
		}
	}

	@Override
	public void fillCode(String code) {
		if (!SkuUtil.isWarehouse(code)) {
			curScanCode = code;
            
			hand_shelf_sku.setText(code);
			ap.getsoundPool(ap.Sound_sku);
		} else {
			ap.getsoundPool(ap.Sound_location);
			hand_shelf_location.setText(code);
		}
		//日志操作
    	LogUtils.logOnFile("~->添加->扫码："+code);
	}
	
	
	

	// 填充表单数据
	private void addData() {
		curScanCode = hand_shelf_sku.getText().toString().toUpperCase().trim();
		boolean isExistThisProduct = false;
		for (CheckBean checkBean : checkedList) {
			if (checkBean.sku.equals(curScanCode)) {
				isExistThisProduct = true;
				scanableCount = (int) checkBean.shouldCount
						- (int) checkBean.reallyCount;
				Log.i("SHANGJIA", "shouldCount" + (int) checkBean.shouldCount
						+ "reallyCount" + (int) checkBean.reallyCount);
				break;
			}
		}
		Log.i("SHANGJIA", "curScanCode" + curScanCode);
		if (!isExistThisProduct) {
			Toast.makeText(HandShelfActivity.this, "没有此商品", Toast.LENGTH_SHORT) 
					.show();
			hand_shelf_sku.setText("");// 转换
			return;
		} else {
			hand_shelf_sku.setText(curScanCode);// 转换
		}

	}
	
	
}
