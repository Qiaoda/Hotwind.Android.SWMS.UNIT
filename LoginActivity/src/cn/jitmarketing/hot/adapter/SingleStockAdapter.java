package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.SingleStockSKUBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleStockAdapter extends BaseAdapter {

	private Context mContext;
	private List<SingleStockSKUBean> list;

	public SingleStockAdapter(Context mContext, List<SingleStockSKUBean> list) {
		super();
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_single_stock, null);
            viewHolder.adapter_layout=(LinearLayout) convertView.findViewById(R.id.adapter_layout);
			viewHolder.sku=(TextView) convertView.findViewById(R.id.sku);
            viewHolder.num=(TextView) convertView.findViewById(R.id.num);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (list.get(position).isFlag()) {
			viewHolder.adapter_layout.setBackgroundResource(R.drawable.list_item_bg);
		}else{
			viewHolder.adapter_layout.setBackgroundResource(R.drawable.list_item_error_bg);
		}
		viewHolder.sku.setText(list.get(position).getSku());
        viewHolder.num.setText(String.valueOf(list.get(position).getNum()));
		return convertView;
	}

	private static class ViewHolder {
        LinearLayout adapter_layout;
		TextView sku;
		TextView num;
	}
}
