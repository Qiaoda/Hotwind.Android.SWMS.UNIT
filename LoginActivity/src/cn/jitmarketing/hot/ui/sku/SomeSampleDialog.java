package cn.jitmarketing.hot.ui.sku;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.SomeSampleAdapter;
import cn.jitmarketing.hot.entity.SampleEntity;
import cn.jitmarketing.hot.entity.SkuEntity;

import com.ex.lib.ext.xutils.annotation.ViewInject;

public class SomeSampleDialog extends BaseSwipeBackAcvitiy implements OnItemClickListener{

	@ViewInject(R.id.only_list)
	ListView only_list;

	private ArrayList<SampleEntity> sampleList;
	private SomeSampleAdapter arrayAdapter;
	private SampleEntity sampleEntity;
	private String skuCodeStr;
//	private String skuInfo;
	private SkuEntity sku;
	
	@Override
	protected int exInitLayout() {
		return R.layout.activity_some_sample;
	}

	@Override
	protected void exInitView() {
		sampleList = (ArrayList<SampleEntity>) getIntent().getBundleExtra("bundle")
				.getSerializable("sampleList");
		skuCodeStr = getIntent().getBundleExtra("bundle").getString("skuCodeStr");
		sku = (SkuEntity)getIntent().getBundleExtra("bundle").getSerializable("sku");
		arrayAdapter = new SomeSampleAdapter(mActivity, sampleList);
		only_list.setAdapter(arrayAdapter);
		only_list.setOnItemClickListener(this);
	}

	@Override
	protected void exInitAfter() {
		initIble(this, this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		sampleEntity = sampleList.get(position);
		arrayAdapter.setSelected(position);
		arrayAdapter.notifyDataSetInvalidated();
		Intent intent = new Intent(SomeSampleDialog.this, SendRequestDialog.class);
		Bundle bd = new Bundle();
		bd.putSerializable("sampleEntity", sampleEntity);
		bd.putString("skuCodeStr", skuCodeStr);
		bd.putSerializable("sku", sku);
		intent.putExtras(bd);
		startActivity(intent);
		finish();
	}

}
