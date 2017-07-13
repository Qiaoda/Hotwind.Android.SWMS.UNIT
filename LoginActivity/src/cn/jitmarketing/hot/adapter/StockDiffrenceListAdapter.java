package cn.jitmarketing.hot.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;

public class StockDiffrenceListAdapter extends BaseAdapter {

	private String[] list;
	private LayoutInflater inflater;    

	public StockDiffrenceListAdapter(Activity activity, String[] list) {
		this.list = list;
		inflater = LayoutInflater.from(activity);
	}


	@Override
	public int getCount() {
		return list.length;
	}

	@Override
	public Object getItem(int position) {
		return list[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.stock_taking_diffrence_item, null);  
			holder = new ViewHolder();
			holder.diffrence_txt = (TextView) convertView
					.findViewById(R.id.diffrence_txt);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.diffrence_txt.setText(list[position]);
		return convertView;
	}

	class ViewHolder {
		TextView diffrence_txt;
	}

}
