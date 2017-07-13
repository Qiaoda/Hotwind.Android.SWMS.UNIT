package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.TrimBean;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrimAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<TrimBean> dlist;

	public TrimAdapter(LayoutInflater inflater, List<TrimBean> dlist) {
		this.inflater = inflater;
		this.dlist = dlist;
	}

	@Override
	public int getCount() {
		return dlist.size();
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.trim_item, null);
			holder = new ViewHolder();
			holder.trim_id = (TextView) convertView.findViewById(R.id.trim_id);
			holder.trim_count = (TextView) convertView
					.findViewById(R.id.trim_count);
			holder.trim_time = (TextView) convertView
					.findViewById(R.id.trim_time);
			holder.trim_code = (TextView) convertView
					.findViewById(R.id.trim_code);
			holder.trim_shelf_code = (TextView) convertView
					.findViewById(R.id.trim_shelf_code);
			holder.trim_oporater = (TextView) convertView
					.findViewById(R.id.trim_oporater);
			holder.trim_type = (TextView) convertView
					.findViewById(R.id.trim_type);
			holder.remark_txt = (TextView) convertView
					.findViewById(R.id.remark_txt);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.trim_id.setText(dlist.get(position).ItemName);
		holder.trim_count.setText(dlist.get(position).ChangeQry);
		holder.trim_time.setText(dlist.get(position).CreateTime);
		holder.trim_code.setText(dlist.get(position).SKUCode);
		holder.trim_shelf_code.setText(dlist.get(position).ShelfLocationCode);
		holder.trim_oporater.setText(dlist.get(position).CreateBy);
		holder.trim_type.setText(dlist.get(position).ChangeTypeString);
		holder.remark_txt.setText(dlist.get(position).Remark);
		return convertView;
	}

	class ViewHolder {
		TextView trim_id;
		TextView trim_count;
		TextView trim_time;
		TextView trim_code;
		TextView trim_oporater;
		TextView trim_shelf_code;
		TextView trim_type;;
		TextView remark_txt;
	}
}
