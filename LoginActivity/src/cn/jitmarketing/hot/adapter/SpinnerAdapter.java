package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;

public class SpinnerAdapter extends BaseAdapter {

	private List<String> nameList;
	private LayoutInflater inflater;    

	public SpinnerAdapter(Activity activity, List<String> nameList) {
		this.nameList = nameList;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return nameList.size();
	}

	@Override
	public Object getItem(int position) {

		return nameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.spinner_item_layout, null);  
			holder = new ViewHolder();
			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.spinner_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameTextView.setText(nameList.get(position));
		return convertView;
	}

	class ViewHolder {
		TextView nameTextView;
	}

}
