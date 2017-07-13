package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.TitleWidget;

public class CGYDetailActivity extends BaseSwipeBackAcvitiy implements OnClickListener {
	
	@ViewInject(R.id.cgy_detail_title)
	TitleWidget cgy_detail_title;
	@ViewInject(R.id.detail_list)
	private ListView detail_list;
	CGYScanSkuAdapter adapter;
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_cgy_detail;
	}

	@Override
	protected void exInitView() {
		cgy_detail_title.setOnLeftClickListner(this);
		cgy_detail_title.setText("明细");
		View headView = getLayoutInflater().inflate(R.layout.sku_shelves_item_layout, null);
		ArrayList<SkuBean> detailList=(ArrayList<SkuBean>) getIntent().getBundleExtra("bundle").getSerializable("list");
		adapter=new CGYScanSkuAdapter(this, getLayoutInflater(),detailList, true);
		detail_list.addHeaderView(headView);
		detail_list.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.lv_left:
			//日志操作
			LogUtils.logOnFile("盘点->库位盘点->明细->返回");
			Bundle bundle=new Bundle();
		    bundle.putSerializable("updatedList", (ArrayList<SkuBean>)adapter.getScanSkuList());
		    Intent intent=new Intent();
		    intent.putExtra("bundle", bundle);
		    setResult(RESULT_OK, intent);
			this.finish();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Bundle bundle=new Bundle();
		    bundle.putSerializable("updatedList", (ArrayList<SkuBean>)adapter.getScanSkuList());
		    Intent intent=new Intent();
		    intent.putExtra("bundle", bundle);
		    setResult(RESULT_OK, intent);
			this.finish();
			return true;
		}
		return false;
	}
}
