package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.view.MyListView;
import cn.jitmarketing.hot.view.TitleWidget;

public class ShelfDetailActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {
	
	@ViewInject(R.id.shelf_detail_title)
	TitleWidget shelf_detail_title;
	ListView detail_lv;
	ArrayList<ShelfBean> wareList;
	
	private boolean isNoValue;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_shelf_old_detail;
	}

	@Override
	protected void exInitView() {
		shelf_detail_title.setText("明细");
		shelf_detail_title.setOnLeftClickListner(this);
		detail_lv = (ListView) findViewById(R.id.detail_lv);
		wareList = (ArrayList<ShelfBean>) getIntent()
				.getBundleExtra("bundle").getSerializable("list");
		OldShelfDetailAdapter adapter = new OldShelfDetailAdapter(getLayoutInflater(), wareList);
		detail_lv.setAdapter(adapter);
		if(wareList == null || wareList.size() == 0) {
			isNoValue = true;
		} else {
			isNoValue = false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			Intent intent1 = new Intent(ShelfDetailActivity.this, AllocationCreateActivity.class);
			intent1.putExtra("wList", wareList);
			intent1.putExtra("isNoValue", isNoValue);
			setResult(RESULT_OK, intent1);
			finish();
			break;
		}
	}
	public class OldShelfDetailAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		private ArrayList<ShelfBean> wareList;

		public OldShelfDetailAdapter(LayoutInflater inflater,
				ArrayList<ShelfBean> wareList) {
			this.inflater = inflater;
			this.wareList = wareList;
		}

		@Override
		public int getCount() {
			return wareList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder=null;
			if(convertView==null){
				convertView = inflater.inflate(R.layout.shelf_detail, null);
				holder=new ViewHolder();
				holder.tv = (TextView) convertView.findViewById(R.id.tv);
				holder.lv = (MyListView) convertView.findViewById(R.id.lv);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}	
			holder.tv.setText(wareList.get(position).shelfCode);
			CGYScanSkuAdapter adapter = new CGYScanSkuAdapter(ShelfDetailActivity.this, inflater, wareList.get(position).skuList, true, this, wareList, position);
			holder.lv.setAdapter(adapter);
			return convertView;
		}
		
		 class ViewHolder{
			 TextView tv; 
			 MyListView lv;
		 }
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent1 = new Intent(ShelfDetailActivity.this, AllocationCreateActivity.class);
			intent1.putExtra("wList", wareList);
			intent1.putExtra("isNoValue", isNoValue);
			setResult(RESULT_OK, intent1);
			finish();
			return true;
		}
		return false;
	}
}
