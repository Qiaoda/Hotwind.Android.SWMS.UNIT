package cn.jitmarketing.hot.choupan;

import java.util.Map;

import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.layout;
import cn.jitmarketing.hot.adapter.RandomCheckScanAdapter;
import cn.jitmarketing.hot.view.BaseDialog;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.NumberAmendView;
import cn.jitmarketing.hot.view.TitleWidget;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RandomCheckScanActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.ac_title)
	private TitleWidget ac_title;
	@ViewInject(R.id.scan_list)
	private ListView scan_list;
	@ViewInject(R.id.btn_sure)
	private TextView btn_sure;
	@ViewInject(R.id.btn_save)
	private TextView btn_save;
	@ViewInject(R.id.btn_hand)
	private TextView btn_hand;

	private RandomCheckScanAdapter scanAdapter;
	private BaseDialog numberAmendDialog;
	private TextView dialog_title;
	private LinearLayout sku_layout;
	private ClearEditText dialog_sku;
	private LinearLayout btn_layout;
	private NumberAmendView amend_view;
	private TextView d_btn_sure, d_btn_cancel;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_random_check_scan;
	}

	@Override
	protected void exInitView() {
		ac_title.setOnLeftClickListner(this);
		btn_sure.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		btn_hand.setOnClickListener(this);
		ac_title.setText("A010101");
		scanAdapter = new RandomCheckScanAdapter(mActivity);
		scan_list.setAdapter(scanAdapter);
		scan_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				showAmendDialog();
				return false;
			}
		});
		createDialog();
	}

	@Override
	public Map<String, String> onStart(int what) {

		return super.onStart(what);
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {

	}

	@Override
	public void onError(int what, int e, String message) {

	}

	@Override
	public void fillCode(String code) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			onBackPressed();
			break;
		case R.id.btn_sure:
			break;
		case R.id.btn_save:
			break;
		case R.id.btn_hand:
			showHandDialog();
			break;
		case R.id.d_btn_sure:
			break;
		case R.id.d_btn_cancel:
			numberAmendDialog.dismiss();
			break;
		default:
			break;
		}

	}

	/**
	 * 修改数量dialog
	 */
	private void createDialog() {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_number_amend, null);
		numberAmendDialog = new BaseDialog(mActivity, view);
		numberAmendDialog.setCanceledOnTouchOutside(false);
		dialog_title = (TextView) view.findViewById(R.id.dialog_title);
		sku_layout = (LinearLayout) view.findViewById(R.id.sku_layout);
		dialog_sku = (ClearEditText) view.findViewById(R.id.dialog_sku);
		btn_layout = (LinearLayout) view.findViewById(R.id.btn_layout);
		amend_view = (NumberAmendView) view.findViewById(R.id.amend_view);
		d_btn_sure = (TextView) view.findViewById(R.id.d_btn_sure);
		d_btn_cancel = (TextView) view.findViewById(R.id.d_btn_cancel);
		d_btn_sure.setOnClickListener(this);
		d_btn_cancel.setOnClickListener(this);
	}
	/**
	 * show手工输入dialog
	 */
	private void showHandDialog() {
		dialog_title.setText("手工录入SKU数量");
		sku_layout.setVisibility(View.VISIBLE);
		numberAmendDialog.show();
	}

	/**
	 * show amend dialog
	 */
	private void showAmendDialog() {
		dialog_title.setText("修改抽盘SKU数量");
		sku_layout.setVisibility(View.GONE);
		numberAmendDialog.show();
	}
}
