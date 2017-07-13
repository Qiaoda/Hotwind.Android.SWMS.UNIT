package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockDetailErrorEntity;

public class StockTaskErrorListAdapter extends BaseAdapter {

	private List<StockDetailErrorEntity> list;
	private LayoutInflater inflater;

	public StockTaskErrorListAdapter(Activity activity,
			List<StockDetailErrorEntity> list) {
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
					R.layout.stock_taking_detail_error_item, null);
			holder = new ViewHolder();
			holder.detail_error_txt = (TextView) convertView.findViewById(R.id.detail_error_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		StockDetailErrorEntity entity = list.get(position);
		String error = entity.ErrorMessage.replace("\t", "\n");
		holder.detail_error_txt.setText((position+1) + ". " + error);
		return convertView;
	}

	class ViewHolder {
		TextView detail_error_txt;
	}

}
