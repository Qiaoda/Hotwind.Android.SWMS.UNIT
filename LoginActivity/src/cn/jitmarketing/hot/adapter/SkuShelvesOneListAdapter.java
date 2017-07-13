package cn.jitmarketing.hot.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter.ViewHolder;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;

public class SkuShelvesOneListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<InStockDetail> newList;
	
	public SkuShelvesOneListAdapter(LayoutInflater inflater, ArrayList<InStockDetail> newList){
		this.inflater = inflater;
		this.newList = newList;
	}
	
	@Override
	public int getCount() {
		return newList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.sku_shelves_item_layout, null);
			holder=new ViewHolder();
			holder.item_sku_code = (TextView) convertView.findViewById(R.id.item_sku_code);
			holder.item_sku_count = (TextView) convertView.findViewById(R.id.item_sku_count);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.item_sku_code.setText(newList.get(position).SKUCode);
		holder.item_sku_count.setText((int)newList.get(position).SKUCount+"");
		
		return convertView;
	}
	class ViewHolder{
		TextView item_sku_code;
		TextView item_sku_count;
	}
}
