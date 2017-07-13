package cn.jitmarketing.hot.ui.shelf;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockDiffrenceListAdapter;
import cn.jitmarketing.hot.util.ConstValue;

public class StockTakingDiffrenceActivity extends BaseSwipeBackAcvitiy implements OnClickListener {
	
	@ViewInject(R.id.stock_diffrence_listview)
	ListView stock_diffrence_listview;
	@ViewInject(R.id.hand_shelf_layout)
	LinearLayout hand_shelf_layout;
	@ViewInject(R.id.diffrence_sure)
    TextView diffrence_sure;
	@ViewInject(R.id.diffrence_num_text)
    TextView diffrence_num_text;
	
	private StockDiffrenceListAdapter adapter;
	private boolean detail = true;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_stock_taking_diffrence;
	}

	@Override
	protected void exInitView() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),(int)(ConstValue.SCREEN_HEIGHT*0.2));
		hand_shelf_layout.setLayoutParams(params);
		diffrence_sure.setOnClickListener(this);
		String[] arr = (String[])getIntent().getExtras().getStringArray("differenceShelf");
		detail = getIntent().getExtras().getBoolean("detail");
		String[] arrnew = new String[50];
		if(arr.length > 50) {
			for(int i=0; i<50; i++) {
				arrnew[i] = arr[i];
			}
			adapter = new StockDiffrenceListAdapter(this, arrnew);
		} else {
			adapter = new StockDiffrenceListAdapter(this, arr);
		}
		stock_diffrence_listview.setAdapter(adapter);
		diffrence_num_text.setText("有"+ arr.length + "中的sku有差异");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.diffrence_sure:
			if(!detail)
				StockTakingShopownerActivity.refresh = true;
			this.finish();
			break;
		}
	}
}
