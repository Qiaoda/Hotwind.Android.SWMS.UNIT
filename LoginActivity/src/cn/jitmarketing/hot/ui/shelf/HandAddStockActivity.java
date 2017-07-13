package cn.jitmarketing.hot.ui.shelf;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.SkuEditText;

public class HandAddStockActivity extends BaseSwipeOperationActivity implements OnClickListener {
	
	@ViewInject(R.id.hand_add_stock)
	SkuEditText hand_add_stock;
	@ViewInject(R.id.hand_add_stock_cancle)
	TextView hand_add_stock_cancle;
	@ViewInject(R.id.hand_add_stock_sure)
	TextView hand_add_stock_sure;
	@ViewInject(R.id.hand_add_stock_layout)
	LinearLayout hand_add_stock_layout;
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
		return R.layout.activity_hand_add_stock;
	}

	@Override
	public void onReceiver(Intent intent) {
//		byte[] code = intent.getByteArrayExtra("barocode");
//		int codelen = intent.getIntExtra("length", 0);
//		String barcode = new String(code, 0, codelen).toUpperCase().trim();
//		if (!SkuUtil.isWarehouse(barcode)) {
//			ap.getsoundPool(ap.Sound_error);
//		} else {
//			ap.getsoundPool(ap.Sound_location);
//			hand_add_stock.setText(barcode);
//		}
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),(int)(ConstValue.SCREEN_HEIGHT*0.3));
		hand_add_stock_layout.setLayoutParams(params);
		hand_add_stock_cancle.setOnClickListener(this);
		hand_add_stock_sure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hand_add_stock_cancle:
			this.finish();
			break;
		case R.id.hand_add_stock_sure:
			if (!hand_add_stock.invalid()) {
				Ex.Toast(mContext).show(getResources().getString(R.string.validat_sku));
				return;
			}
			String sku=hand_add_stock.getText(this);
			Bundle bundle=new Bundle();
			bundle.putString("sku", sku);
		    Intent intent = new Intent();
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
			ap.getsoundPool(ap.Sound_error);
		} else {
			ap.getsoundPool(ap.Sound_location);
			hand_add_stock.setText(code);
		}
	}

}
