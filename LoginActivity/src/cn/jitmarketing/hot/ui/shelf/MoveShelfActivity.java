package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.Map;

import org.jsoup.helper.StringUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.jiebao.scanlib.ScanService;

/**
 * 移库
 */
public class MoveShelfActivity extends BaseSwipeOperationActivity implements
		OnClickListener {

	private static final int WHAT_NET_MOVE_SHELF = 0x10;
	private static final int FOR_MOVE = 0x11;
	private static final int FOR_DETAIL_SKU = 0x12;
	private boolean canReceive;
	@ViewInject(R.id.text_shelf_old)
	SkuEditText text_shelf_old;
	@ViewInject(R.id.text_shelf_new)
	SkuEditText text_shelf_new;
	@ViewInject(R.id.text_move_num)
	TextView text_move_num;
	@ViewInject(R.id.move_shelf_ok)
	TextView move_shelf_ok;
	@ViewInject(R.id.move_shelf_manual)
	TextView move_shelf_manual;
	@ViewInject(R.id.move_title)
	TitleWidget move_title;
	@ViewInject(R.id.move_shelf_clean)
	TextView move_shelf_clean;
	private TextView move_tv;
	private ArrayList<SkuBean> skuList;

	HotApplication ap;
	boolean isSku = false;
	boolean isLocation = false;

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_move_shelf;
	}

	@Override
	protected void exInitView() {
		ap = (HotApplication) getApplication();
		move_title.setText("移库");
		move_title.setOnLeftClickListner(this);
		move_tv = (TextView) findViewById(R.id.move_tv);
		move_shelf_ok.setOnClickListener(this);
		move_shelf_manual.setOnClickListener(this);
		move_shelf_clean.setOnClickListener(this);
		move_tv.setOnClickListener(this);
		skuList = new ArrayList<SkuBean>();
		text_shelf_old.stopEdit();
		text_shelf_new.stopEdit();
	}

	@Override
	public void onReceiver(Intent intent) {
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// String barcode = new String(code, 0, codelen).toUpperCase().trim();
		// dealBarCode(barcode);

		// if(canReceive){
		// byte[] code = intent.getByteArrayExtra("barocode");
		// int codelen = intent.getIntExtra("length", 0);
		// if (null != code) {
		// String barcode = new String(code, 0, codelen).toUpperCase().trim();
		// if(SkuUtil.isWarehouse(barcode)){
		// isLocation=true;
		// }else{
		// isSku=true;
		// }
		// if(isSku==true && isLocation==false){
		// ap.getsoundPool(ap.Sound_error);
		// isSku=false;
		// return;
		// }
		// if(isSku==false && isLocation==true && skuList.size()==0){
		// ap.getsoundPool(ap.Sound_location);
		// text_shelf_old.setText(barcode);
		// return;
		// }
		// if(isSku==true && isLocation==true &&
		// !StringUtil.isBlank(text_shelf_old.getText(this).toString())){
		// ap.getsoundPool(ap.Sound_sku);
		// SkuBean sb = new SkuBean(barcode, 1);
		// SkuUtil.getSku(skuList, sb);
		// text_move_num.setText("" + SkuUtil.getSkuCount(skuList));
		// isSku=false;
		// return;
		// }
		// if(isSku==false && isLocation==true &&
		// !StringUtil.isBlank(text_shelf_old.getText(this).toString())){
		// if
		// (barcode.equalsIgnoreCase(text_shelf_old.getText(this).toString())) {
		// ap.getsoundPool(ap.Sound_error);
		// } else {
		// ap.getsoundPool(ap.Sound_location);
		// text_shelf_new.setText(barcode);
		// }
		// return;
		// }
		// }
		// }
	}

	// @Override
	// public void fillCode(String code) {
	// dealBarCode(code);
	// }

	private void dealBarCode(String barcode) {
		if (canReceive) {
			if (SkuUtil.isWarehouse(barcode)) {
				isLocation = true;
			} else {
				isSku = true;
			}
			if (isSku == true && isLocation == false) {
				ap.getsoundPool(ap.Sound_error);
				isSku = false;
				return;
			}
			if (isSku == false && isLocation == true && skuList.size() == 0) {
				ap.getsoundPool(ap.Sound_location);
				text_shelf_old.setText(barcode);
				return;
			}
			if (isSku == true
					&& isLocation == true
					&& !StringUtil.isBlank(text_shelf_old.getText(this)
							.toString())) {
				ap.getsoundPool(ap.Sound_sku);
				SkuBean sb = new SkuBean(barcode, 1);
				SkuUtil.getSku(skuList, sb);
				text_move_num.setText("" + SkuUtil.getSkuCount(skuList));
				isSku = false;
				return;
			}
			if (isSku == false
					&& isLocation == true
					&& !StringUtil.isBlank(text_shelf_old.getText(this)
							.toString())) {
				if (barcode.equalsIgnoreCase(text_shelf_old.getText(this)
						.toString())) {
					ap.getsoundPool(ap.Sound_error);
				} else {
					ap.getsoundPool(ap.Sound_location);
					text_shelf_new.setText(barcode);
				}
				return;
			}
		}
	}

	@Override
	protected void onResume() {
		canReceive = true;
		// 开启扫描服务
		// startService(new Intent(this,ScanService.class));
		// // 绑定服务
		// bindService(new Intent(this,ScanService.class),
		// serviceConnection,Context.BIND_AUTO_CREATE);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// canReceive = false;
		super.onPause();
	}

	@Override
	protected void onStop() {
		canReceive = false;
		super.onStop();
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_MOVE_SHELF:
			return WarehouseNet.moveShelfLocation(text_shelf_old.getText(this)
					.toString(), text_shelf_new.getText(this).toString(),
					skuList);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_MOVE_SHELF:
			Ex.Toast(this).showLong(R.string.urlError);
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_MOVE_SHELF:
			Ex.Toast(mActivity).showLong(net.message);
			text_shelf_old.setText("");
			text_shelf_new.setText("");
			skuList.clear();
			text_move_num.setText("0");
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			this.finish();
			break;
		case R.id.move_shelf_ok:
			if (StringUtil.isBlank(text_shelf_old.getText(this).toString())) {
				Ex.Toast(mActivity).show("请先扫码原库位");
				return;
			}
			if (skuList.size() == 0) {
				Ex.Toast(mActivity).show("请先扫码商品码");
				return;
			}
			if (StringUtil.isBlank(text_shelf_new.getText(this).toString())) {
				Ex.Toast(mActivity).show("请先扫码新库位");
				return;
			}
			// 移库
			new AlertDialog.Builder(this)
					.setTitle(R.string.noteTitle)
					.setMessage("确认提交？")
					.setNegativeButton(R.string.cancelTitle,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setPositiveButton(R.string.sureTitle,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int whichButton) {
									if (skuList.size() == 0) {
										Ex.Toast(mContext).show("没有可提交的数据");
									} else {
										startJsonTask(
												HotConstants.Global.APP_URL_USER
														+ HotConstants.Shelf.MoveShelfLocation,
												WHAT_NET_MOVE_SHELF,
												true,
												NET_METHOD_POST,
												SaveListUtil.moveShelfSave(
														text_shelf_old
																.getText(MoveShelfActivity.this),
														text_shelf_new
																.getText(
																		MoveShelfActivity.this)
																.toString(),
														skuList), false);
									}
								}
							}).show();
			break;
		case R.id.move_shelf_manual:
			// 手工移库
			Intent intent = new Intent();
			intent.setClass(MoveShelfActivity.this, HandMoveActivity.class);
			startActivityForResult(intent, FOR_MOVE);
			// 关闭扫描服务
			// if(serviceConnection!=null){
			// unbindService(serviceConnection);
			// stopService(new Intent(this,ScanService.class));
			// }
			break;
		case R.id.move_tv:
			Bundle bundle = new Bundle();
			bundle.putSerializable("list", skuList);
			Intent intent1 = new Intent();
			intent1.putExtra("bundle", bundle);
			intent1.setClass(MoveShelfActivity.this, MoveDetailActivity.class);
			startActivityForResult(intent1, FOR_DETAIL_SKU);
			break;
		case R.id.move_shelf_clean:
			text_shelf_old.setText("");
			text_shelf_new.setText("");
			skuList.clear();
			text_move_num.setText("" + SkuUtil.getSkuCount(skuList));
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK && requestCode == FOR_MOVE) {
			SkuBean sb = (SkuBean) data.getBundleExtra("bundle")
					.getSerializable("sbBean");
			String old = data.getBundleExtra("bundle").getString("old");
			String news = data.getBundleExtra("bundle").getString("news");
			SkuUtil.HandgetSku(skuList, sb);
			text_shelf_old.setText(old);
			text_shelf_new.setText(news);
			text_move_num.setText("" + SkuUtil.getSkuCount(skuList));
		}
		if (resultCode == RESULT_OK && requestCode == FOR_DETAIL_SKU) {
			ArrayList<SkuBean> updatedList = (ArrayList<SkuBean>) data
					.getBundleExtra("bundle").getSerializable("updatedList");
			if (updatedList != null)
				skuList = updatedList;
			text_move_num.setText("" + SkuUtil.getSkuCount(skuList));
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
	}
}
