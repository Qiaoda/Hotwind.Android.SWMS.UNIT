package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CheckStockListAdapter.ViewHolder;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.CheckBean;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BeforeCheckStockListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<BeforeInStockBean> skuList;

	public BeforeCheckStockListAdapter(LayoutInflater inflater,
			List<BeforeInStockBean> skuList) {
		this.inflater = inflater;
		this.skuList = skuList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return skuList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.before_check_item, null);
			holder=new ViewHolder();
			holder.scan_sku=(TextView) convertView.findViewById(R.id.scan_sku);
			holder.scan_count=(TextView) convertView.findViewById(R.id.scan_count);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		BeforeInStockBean item=skuList.get(position);
		holder.scan_sku.setText(item.SKUCode);
		holder.scan_count.setText(""+item.SKUCount);
		return convertView;
	}
	class ViewHolder{
		TextView scan_sku;
		TextView scan_count;
	}
}
