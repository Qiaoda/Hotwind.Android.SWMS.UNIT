package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter.ViewHolder;
import cn.jitmarketing.hot.entity.SkuBean;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StockHistoryAdapter extends BaseAdapter {
	private List<SkuBean> list;
	private LayoutInflater inflater;

	public StockHistoryAdapter(LayoutInflater inflater, List<SkuBean> list) {
		this.inflater = inflater;
		this.list = list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
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
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.cgy_scan_stock, null);
			holder=new ViewHolder();
			holder.scan_sku = (TextView) convertView.findViewById(R.id.scan_sku);
			holder.scan_count = (TextView) convertView.findViewById(R.id.scan_count);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}	
		holder.scan_sku.setText(list.get(position).skuCode);
		holder.scan_count.setText(""+list.get(position).DifferenceCount);
		return convertView;
	}
	  class ViewHolder{
		  TextView scan_sku;
		  TextView scan_count;
	  }
}
