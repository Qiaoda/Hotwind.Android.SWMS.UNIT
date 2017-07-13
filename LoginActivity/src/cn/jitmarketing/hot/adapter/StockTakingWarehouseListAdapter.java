package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockTakingWarehouseEntity;

public class StockTakingWarehouseListAdapter extends BaseAdapter {

	private List<StockTakingWarehouseEntity> stockList;
	private LayoutInflater inflater;    
	private Activity activity;

	public StockTakingWarehouseListAdapter(Activity activity, List<StockTakingWarehouseEntity> stockList) {
		this.activity = activity;
		this.stockList = stockList;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return stockList.size();
	}

	@Override
	public Object getItem(int position) {

		return stockList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.stock_taking_warehouse_item, null);  
			holder = new ViewHolder();
			holder.shelf_code_txt = (TextView) convertView
					.findViewById(R.id.shelf_code_txt);
			holder.check_time_txt = (TextView) convertView
					.findViewById(R.id.check_time_txt);
			holder.status_txt = (TextView) convertView
					.findViewById(R.id.status_txt);
			holder.operator_txt = (TextView) convertView
					.findViewById(R.id.operator_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		StockTakingWarehouseEntity entity = stockList.get(position);
		if(entity.CheckStatus == 0) {
			holder.status_txt.setText("未盘点");
			holder.shelf_code_txt.setPadding(8, 8, 8, 8);
			holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_black_text));
		} else if(entity.CheckStatus == 1) {
			holder.shelf_code_txt.setPadding(8, 0, 0, 0);
			holder.status_txt.setText("已盘点");
			holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_green));
		} else if(entity.CheckStatus == 2) {
			holder.shelf_code_txt.setPadding(8, 0, 0, 0);
			holder.status_txt.setText("有差异");
			holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_red_text));
		}
		holder.shelf_code_txt.setText(entity.ShelfLocationName);
		if(entity.CheckDateTime != null && !entity.CheckDateTime.equals("")) {
			holder.check_time_txt.setVisibility(View.VISIBLE);
			holder.check_time_txt.setText("盘点时间:  " + entity.CheckDateTime);
		} else {
			holder.check_time_txt.setText(null);
			holder.check_time_txt.setVisibility(View.GONE);
		}
		if(entity.CheckPersonName != null && !entity.CheckPersonName.equals("")) {
			holder.operator_txt.setText(entity.CheckPersonName);
			holder.operator_txt.setVisibility(View.VISIBLE);
		} else {
			holder.operator_txt.setText(null);
			holder.operator_txt.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView shelf_code_txt;
		TextView check_time_txt;
		TextView operator_txt;
		TextView status_txt;
	}

}
