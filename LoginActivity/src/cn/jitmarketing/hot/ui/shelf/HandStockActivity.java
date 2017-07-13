package cn.jitmarketing.hot.ui.shelf;

import com.ex.lib.core.utils.Ex;
import com.example.scandemo.BaseSwipeOperationActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.ClearEditText;

public class HandStockActivity extends BaseSwipeOperationActivity implements OnClickListener{
	private ClearEditText hand_edit_sku;
	private ClearEditText hand_edit_count;
	private TextView hand_sku_cancle;
	private TextView hand_sku_sure;
	private LinearLayout hand_stock_layout;
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
		// TODO Auto-generated method stub
		setFinishOnTouchOutside(false);
		return R.layout.activity_hand_stock;
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
		hand_edit_sku=(ClearEditText) findViewById(R.id.hand_edit_sku);
		hand_edit_count=(ClearEditText) findViewById(R.id.hand_edit_count);
		hand_sku_cancle=(TextView) findViewById(R.id.hand_sku_cancle);
		hand_sku_sure=(TextView) findViewById(R.id.hand_sku_sure);
		hand_sku_cancle.setOnClickListener(this);
		hand_sku_sure.setOnClickListener(this);
		hand_edit_sku.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				hand_edit_sku.removeTextChangedListener(this);//解除文字改变事件 
				hand_edit_sku.setText(s.toString().toUpperCase());//转换 
				hand_edit_sku.setSelection(s.toString().length());//重新设置光标位置 
				hand_edit_sku.addTextChangedListener(this);//重新绑 
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

	@SuppressLint("DefaultLocale")
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hand_sku_cancle:
			//日志操作
			LogUtils.logOnFile("盘点->库位盘点->手工->取消");
			this.finish();
			break;
		case R.id.hand_sku_sure:
			
		     String sku=hand_edit_sku.getText().toString().toUpperCase();
		     String stringcount=hand_edit_count.getText().toString();
		     if(sku.equals("") || stringcount.equals("")){
		    	 Ex.Toast(mActivity).show("输入框内容不能为空");
		     }else{
		    	 int count=Integer.valueOf(stringcount);
			     SkuBean sb=new SkuBean(sku, count);
			     Bundle bundle=new Bundle();
			     bundle.putSerializable("sbBean", sb);
			     Intent intent=new Intent();
			     intent.putExtra("bundle", bundle);
			     setResult(RESULT_OK, intent);
			     this.finish();
			   //日志操作
			   LogUtils.logOnFile("盘点->库位盘点->手工->确定->SKU:"+sku+"数量："+stringcount);
		     }
			break;
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		if (!SkuUtil.isWarehouse(code)) {
			hand_edit_sku.setText(code);
			ap.getsoundPool(ap.Sound_sku);
		} else {
			ap.getsoundPool(ap.Sound_error);
		}
	}
}
