package cn.jitmarketing.hot.ui.shelf;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.InStockSku;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;

public class HandInStockActivity extends BaseSwipeOperationActivity implements OnClickListener{
	@ViewInject(R.id.hand_in_stock_sku)
	ClearEditText hand_in_stock_sku;
	@ViewInject(R.id.hand_in_stock_count)
	ClearEditText hand_in_stock_count;
	@ViewInject(R.id.hand_in_stock_cancle)
	TextView hand_in_stock_cancle;
	@ViewInject(R.id.hand_in_stock_sure)
	TextView hand_in_stock_sure;
	@ViewInject(R.id.hand_in_stock_layout)
	private LinearLayout hand_in_stock_layout;
	
	private HotApplication ap;
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_hand_in_stock;
	}
	
	@Override
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		if (!SkuUtil.isWarehouse(barcode)) {
//			ap.getsoundPool(ap.Sound_sku);
//			hand_in_stock_sku.setText(barcode);
//		} else {
//			ap.getsoundPool(ap.Sound_error);
//		}
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),(int)(ConstValue.SCREEN_HEIGHT*0.4));
		hand_in_stock_layout.setLayoutParams(params);
		hand_in_stock_cancle.setOnClickListener(this);
		hand_in_stock_sure.setOnClickListener(this);
		hand_in_stock_sku.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				hand_in_stock_sku.removeTextChangedListener(this);//解除文字改变事件 
				hand_in_stock_sku.setText(s.toString().toUpperCase());//转换 
				hand_in_stock_sku.setSelection(s.toString().length());//重新设置光标位置 
				hand_in_stock_sku.addTextChangedListener(this);//重新绑 
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hand_in_stock_cancle:
			this.finish();
			break;
		case R.id.hand_in_stock_sure:
			String sku=hand_in_stock_sku.getText().toString().toUpperCase();
			String stringcount=hand_in_stock_count.getText().toString();
			if(sku.equals("")) {
				Ex.Toast(this).show("请输入sku");
				return;
			}
			if(stringcount.equals("")) {
				Ex.Toast(this).show("请输入数量");
				return;
			}
			Bundle bundle=new Bundle();
			bundle.putString("sku", sku);
			bundle.putString("stringcount", stringcount);
		    Intent intent=new Intent();
		    intent.putExtra("bundle", bundle);
		    setResult(RESULT_OK, intent);
		    this.finish();
			break;
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		if (!SkuUtil.isWarehouse(code)) {
			ap.getsoundPool(ap.Sound_sku);
			hand_in_stock_sku.setText(code);
		} else {
			ap.getsoundPool(ap.Sound_error);
		}
	}

}
