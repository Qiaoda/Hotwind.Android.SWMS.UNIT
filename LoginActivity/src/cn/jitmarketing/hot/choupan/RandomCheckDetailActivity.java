package cn.jitmarketing.hot.choupan;

import java.util.Map;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.id;
import cn.jitmarketing.hot.R.layout;
import cn.jitmarketing.hot.adapter.RandomCheckAdapter;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RandomCheckDetailActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.ac_title)
	private TitleWidget ac_title;
	@ViewInject(R.id.stock_search_edt)
	private ClearEditText stock_search_edt;
	@ViewInject(R.id.check_storck_cancel)
	private TextView check_storck_cancel;
	@ViewInject(R.id.search_layout)
	private LinearLayout search_layout;
	@ViewInject(R.id.stock_list)
	private ListView stock_list;
	
	private RandomCheckAdapter randomCheckAdapter;
	private Animation titleLeftIn;
	private Animation titleLeftOut;
	private Animation searchRightIn;
	private Animation searchRightOut;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {

		return R.layout.activity_random_check_detail;
	}

	@Override
	protected void exInitView() {
		
		ac_title.setOnLeftClickListner(this);
		ac_title.setOnRightClickListner(this);
		check_storck_cancel.setOnClickListener(this);
		titleLeftIn = AnimationUtils.loadAnimation(this, R.anim.title_left_in);
		titleLeftOut = AnimationUtils.loadAnimation(this, R.anim.title_left_out);
		searchRightIn = AnimationUtils.loadAnimation(this, R.anim.search_right_in);
		searchRightOut = AnimationUtils.loadAnimation(this, R.anim.search_right_out);
		randomCheckAdapter=new RandomCheckAdapter(mActivity);
		stock_list.setAdapter(randomCheckAdapter);
		stock_list.setOnItemClickListener(new OnItemClickListener() { 

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
			startActivity(new Intent(mActivity,RandomCheckScanActivity.class));
			}
		});
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
		case R.id.lv_right:
			showSearchLayout();
			break;
		case R.id.check_storck_cancel:// 取消
			hideSearchLayout();
			break;
		default:
			break;
		}
	}
	/**
	 * 显示搜索框
	 */
	private void showSearchLayout() {
		ac_title.setVisibility(View.GONE);
		ac_title.startAnimation(titleLeftOut);
		search_layout.setVisibility(View.VISIBLE);
		search_layout.startAnimation(searchRightIn);
	}
	/**
	 * 隐藏搜索框
	 */
	private void hideSearchLayout() {
		ac_title.setVisibility(View.VISIBLE);
		ac_title.startAnimation(titleLeftIn);
		search_layout.setVisibility(View.GONE);
		search_layout.startAnimation(searchRightOut);
	}

}
