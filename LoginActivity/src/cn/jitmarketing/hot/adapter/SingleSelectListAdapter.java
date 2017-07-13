package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.UnitBeanOne;

public class SingleSelectListAdapter extends BaseAdapter implements OnItemClickListener{

	private List<UnitBeanOne> unitList;
	private LayoutInflater inflater;
	private int mSelectItem = 0;

	public SingleSelectListAdapter(Context activity, List<UnitBeanOne> unitList) {
		this.unitList = unitList;
		inflater = LayoutInflater.from(activity);
	}
    
	public void setSelectItem() {
		mSelectItem = 0;
	}
	
	public int getSelectItem() {
		return mSelectItem;
	}
	
	@Override
	public int getCount() {
		return unitList.size();
	}

	@Override
	public Object getItem(int position) {
		return unitList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.single_select_item_layout, null);  
			holder = new ViewHolder();
			holder.name_txt = (TextView) convertView
					.findViewById(R.id.name_txt);
			holder.code_txt = (TextView) convertView
					.findViewById(R.id.code_txt);
			
			holder.cb = (RadioButton) convertView
					.findViewById(R.id.single_select_ckb);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name_txt.setText(unitList.get(position).unitName);
		holder.code_txt.setText(unitList.get(position).unitID);
		holder.cb.setChecked(mSelectItem == position);
		return convertView;
	}

	public class ViewHolder {
		public TextView name_txt;
		public TextView code_txt;
		public RadioButton cb;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position != mSelectItem) {
			mSelectItem = position;
			notifyDataSetChanged();
		}
	}

}
