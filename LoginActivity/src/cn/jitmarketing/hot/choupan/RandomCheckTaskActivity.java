package cn.jitmarketing.hot.choupan;

import java.util.Map;

import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.layout;
import cn.jitmarketing.hot.view.TitleWidget;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class RandomCheckTaskActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.ac_title)
	private TitleWidget ac_title;
	@ViewInject(R.id.random_check_detail)
	private TextView random_check_detail;
	@ViewInject(R.id.random_check_end)
	private TextView random_check_end;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_random_check_task;
	}

	@Override
	protected void exInitView() {
		ac_title.setOnLeftClickListner(this);
		ac_title.setOnRightClickListner(this);
		random_check_detail.setOnClickListener(this);
		random_check_end.setOnClickListener(this);
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
			startActivity(new Intent(mActivity,RandomCheckHistoryActivity.class));
			break;
		case R.id.random_check_detail:
            startActivity(new Intent(mActivity,RandomCheckDetailActivity.class)); 
			break;
		case R.id.random_check_end:
			
			break;

		default:
			break;
		}

	}

}
