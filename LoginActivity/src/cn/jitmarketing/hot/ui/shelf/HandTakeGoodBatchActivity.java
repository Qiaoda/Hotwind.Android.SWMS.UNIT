package cn.jitmarketing.hot.ui.shelf;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.ui.sku.TakeGoodsBatchActivity;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SkuEditText;

public class HandTakeGoodBatchActivity extends BaseSwipeOperationActivity implements OnClickListener{
	
	@ViewInject(R.id.sku_shelf)
	private SkuEditText sku_shelf;
	@ViewInject(R.id.hand_edit_count)
	private ClearEditText hand_edit_count;
	@ViewInject(R.id.hand_sku_cancle)
	private TextView hand_sku_cancle;
	@ViewInject(R.id.hand_sku_sure)
	private TextView hand_sku_sure;
	@ViewInject(R.id.hand_stock_layout)
	private LinearLayout hand_stock_layout;
	
	private HotApplication ap;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_hand_take_goods_batch;
	}
	
	@Override
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		if (!SkuUtil.isWarehouse(barcode)) {
//			hand_edit_sku.setText(barcode);
//			ap.getsoundPool(ap.Sound_sku);
//		} else {
//			ap.getsoundPool(ap.Sound_error);
//		}
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		hand_stock_layout=(LinearLayout) findViewById(R.id.hand_stock_layout);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),(int)(ConstValue.SCREEN_HEIGHT*0.4));
		hand_stock_layout.setLayoutParams(params);
		hand_sku_cancle.setOnClickListener(this);
		hand_sku_sure.setOnClickListener(this);
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hand_sku_cancle:
			hideSoftKeyBoard(this, sku_shelf);
			this.finish();
			break;
		case R.id.hand_sku_sure:
		     String shelfCode = sku_shelf.getText(this);
		     String stringcount = hand_edit_count.getText().toString();
		     if(shelfCode.equals("") || stringcount.equals("")){
		    	 Ex.Toast(mActivity).show("输入框内容不能为空");
		     } else {
		    	 hideSoftKeyBoard(this, sku_shelf);
		    	 int count = Integer.valueOf(stringcount);
			     Bundle bundle=new Bundle();
			     bundle.putString("shelfCode", shelfCode);
			     bundle.putInt("count", count);
			     Intent intent = new Intent(this, TakeGoodsBatchActivity.class);
			     intent.putExtras(bundle);
			     setResult(RESULT_OK, intent);
			     this.finish();
		     }
			break;
		}
	}
	
	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		if (!SkuUtil.isWarehouse(barcode)) {
//			hand_edit_sku.setText(barcode);
//			ap.getsoundPool(ap.Sound_sku);
//		} else {
//			ap.getsoundPool(ap.Sound_error);
//		}
	}
}
