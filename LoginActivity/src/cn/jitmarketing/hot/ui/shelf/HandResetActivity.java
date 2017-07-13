package cn.jitmarketing.hot.ui.shelf;

import java.util.Map;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResetEntity;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.SkuEditText;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class HandResetActivity extends BaseSwipeOperationActivity implements
		OnClickListener {

	@ViewInject(R.id.hand_reset_layout)
	private LinearLayout hand_reset_layout;
	@ViewInject(R.id.hand_edittext)
	private SkuEditText hand_edittext;
	@ViewInject(R.id.reset_ok)
	private TextView reset_ok;
	@ViewInject(R.id.layout_reset_location)
	private LinearLayout layout_reset_location;
	@ViewInject(R.id.layout_reset_no_find)
	private LinearLayout layout_reset_no_find;
	@ViewInject(R.id.reset_no_find)
	private RadioButton reset_no_find;
	@ViewInject(R.id.reset_find)
	private RadioButton reset_find;
	private static final int WHAT_NET_GET_RESET_SKU_ok = 0x11;
//	private String SKUCode;
//	private String factId;
	private String shelfCode;
	private ResetEntity reset; 
	private HotApplication ap;

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_hand_reset;
	}

	@Override
	protected void exInitAfter() {
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
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
//			hand_edittext.setText(barcode);
//		}
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				(int) (ConstValue.SCREEN_WIDTH * 0.95),
				(int) (ConstValue.SCREEN_HEIGHT * 0.35));
		hand_reset_layout.setLayoutParams(params);
		Intent intent = getIntent();
		Bundle bd = intent.getExtras();
//		SKUCode = bd.getString("SKUCode");
//		factId = bd.getString("FactID");
//		shelfCode = bd.getString("ShelfLocationCode");
		reset = (ResetEntity)bd.getSerializable("reset");
		shelfCode = reset.ShelfLocationCode;
		reset_ok.setOnClickListener(this);
//		hand_edittext.setText(shelfCode);
		reset_no_find.setOnClickListener(this);
		reset_find.setOnClickListener(this);

	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		switch (what) {
		case WHAT_NET_GET_RESET_SKU_ok:
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).show(net.message);
				finish();
				return;
			} else {
				Ex.Toast(mContext).show("商品复位成功");
			}
			ResetSkuActivity.isRefreshList = true;
			this.finish();

		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_RESET_SKU_ok:
			return SkuNet.getResetSkuOk(reset.SKUCode, shelfCode, reset.FactID);
		}
		return super.onStart(what);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reset_ok:
			if (reset_no_find.isChecked()) {
				shelfCode = "";
			}
			if(reset_find.isChecked()) {
				shelfCode = hand_edittext.getText(this);
				if (shelfCode == null || shelfCode.equals("")) {
					Ex.Toast(mActivity).show("请输入库位");
					return;
				}
				if (!hand_edittext.invalid()) {
					Ex.Toast(mActivity).show(
							getResources().getString(R.string.validat_sku));
					return;
				}
			}
			if(reset.IsRevokeSample && reset.IsShoe) {
				if(reset.ShelfLocationCode.equals(shelfCode)) {
					startTask(HotConstants.Global.APP_URL_USER
							+ HotConstants.Shelf.ResetSkuOk, WHAT_NET_GET_RESET_SKU_ok,
							NET_METHOD_POST, false);
				} else {
					Ex.Toast(this).showLong("当前商品只能复位到原库位");
				}
			} else {
				startTask(HotConstants.Global.APP_URL_USER
						+ HotConstants.Shelf.ResetSkuOk, WHAT_NET_GET_RESET_SKU_ok,
						NET_METHOD_POST, false);
			}
			break;
		case R.id.reset_find:
			reset_find.setChecked(true);
			reset_no_find.setChecked(false);
			hand_edittext.setEnable(true);
			break;
		case R.id.reset_no_find:
			reset_find.setChecked(false);
			reset_no_find.setChecked(true);
			hand_edittext.setEnable(false);
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
			hand_edittext.setText(code);
		}
	}
}
