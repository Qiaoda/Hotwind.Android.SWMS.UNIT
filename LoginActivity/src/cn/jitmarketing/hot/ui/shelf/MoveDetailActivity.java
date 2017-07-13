package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.view.TitleWidget;

public class MoveDetailActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {
	
	ListView move_list;
	@ViewInject(R.id.move_detail_title)
	TitleWidget move_detail_title;
	ArrayList<SkuBean>	skuList;
	CGYScanSkuAdapter adapter;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_move_detail;
	}

	@Override
	protected void exInitView() {
		move_detail_title.setOnLeftClickListner(this);
		move_detail_title.setText("明细");
		move_list = (ListView) findViewById(R.id.move_list);
		ArrayList<SkuBean> skuList = (ArrayList<SkuBean>) getIntent()
				.getBundleExtra("bundle").getSerializable("list");
		View headView = getLayoutInflater().inflate(R.layout.sku_shelves_item_layout, null);
		adapter = new CGYScanSkuAdapter(this, getLayoutInflater(), skuList, true);
		move_list.addHeaderView(headView);
		move_list.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			Bundle bundle=new Bundle();
		    bundle.putSerializable("updatedList", (ArrayList<SkuBean>)adapter.getScanSkuList());
		    Intent intent=new Intent();
		    intent.putExtra("bundle", bundle);
		    setResult(RESULT_OK, intent);
			this.finish();
			break;
		}
	}

}
