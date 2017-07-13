package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockDetaiStockDiffEntity;

public class StockTaskStockDiffenceListAdapter extends BaseAdapter {

	private List<StockDetaiStockDiffEntity> list;
	private LayoutInflater inflater;

	public StockTaskStockDiffenceListAdapter(Activity activity,
			List<StockDetaiStockDiffEntity> list) {
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
			convertView = inflater.inflate(
					R.layout.stock_taking_detail_stock_diff_item, null);
			holder = new ViewHolder();
			holder.detail_sku_txt = (TextView) convertView
					.findViewById(R.id.detail_sku_txt);
			holder.detail_sku_diff_count_txt = (TextView) convertView
					.findViewById(R.id.detail_sku_diff_count_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		StockDetaiStockDiffEntity entity = list.get(position);
		holder.detail_sku_txt.setText(entity.SKUCode);
		holder.detail_sku_diff_count_txt.setText("差异数量:  " + entity.DifferenceCount);
		return convertView;
	}

	class ViewHolder {
		TextView detail_sku_txt;
		TextView detail_sku_diff_count_txt;
	}

}
