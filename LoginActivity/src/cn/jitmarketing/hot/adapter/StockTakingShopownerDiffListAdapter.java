package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockTakingShopownerEntity;
import cn.jitmarketing.hot.ui.shelf.StockTakingShopownerActivity;

public class StockTakingShopownerDiffListAdapter extends BaseAdapter {

	private List<StockTakingShopownerEntity> list;
	private LayoutInflater inflater;    
	private Activity activity;

	public StockTakingShopownerDiffListAdapter(Activity activity, List<StockTakingShopownerEntity> list) {
		this.activity = activity;
		this.list = list;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.stock_taking_shopowner_diff_item, null);  
			holder = new ViewHolder();
			holder.shelf_code_txt = (TextView) convertView
					.findViewById(R.id.shelf_code_txt);
			holder.check_time_txt = (TextView) convertView
					.findViewById(R.id.check_time_txt);
			holder.check_person_txt = (TextView) convertView
					.findViewById(R.id.check_person_txt);
			holder.taking_finish_btn = (Button) convertView
					.findViewById(R.id.taking_finish_btn);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final StockTakingShopownerEntity entity = list.get(position);
		if(entity.ShelfLocationTypeID == 2) {//样品库
			holder.taking_finish_btn.setVisibility(View.VISIBLE);
		} else {
			holder.taking_finish_btn.setVisibility(View.GONE);
		}
		holder.shelf_code_txt.setText(entity.ShelfLocationName);
		if(entity.CheckDateTime != null && !entity.CheckDateTime.equals("")) {
			holder.check_time_txt.setText("盘点时间:  " + entity.CheckDateTime);
			holder.check_time_txt.setVisibility(View.VISIBLE);
		} else {
			holder.check_time_txt.setVisibility(View.GONE);
			holder.check_time_txt.setText(null);
		}
		holder.check_person_txt.setText(entity.CheckPersonName);
		holder.taking_finish_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((StockTakingShopownerActivity)activity).request(entity);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView shelf_code_txt;
		TextView check_time_txt;
		TextView check_person_txt;
		Button taking_finish_btn;
	}

}
