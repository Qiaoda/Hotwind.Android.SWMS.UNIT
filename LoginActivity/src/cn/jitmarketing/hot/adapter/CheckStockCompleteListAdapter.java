package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.InStockSkuCheck;

/** 核对信息的Adapter */
public class CheckStockCompleteListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<InStockSkuCheck> skuList;

	public CheckStockCompleteListAdapter(LayoutInflater inflater,
			List<InStockSkuCheck> skuList) {
		this.inflater = inflater;
		this.skuList = skuList;
	}

	@Override
	public int getCount() {
		return skuList.size();
	}

	@Override
	public Object getItem(int position) {

		return skuList.get(position);
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
			convertView = inflater.inflate(R.layout.check_item_layout, null);
			holder=new ViewHolder();
			holder.check_sku_code = (TextView) convertView
					.findViewById(R.id.check_sku_code);
			holder.check_sku_should_num = (TextView) convertView
					.findViewById(R.id.check_sku_should_num);
			holder.check_sku_really_num = (TextView) convertView
					.findViewById(R.id.check_sku_really_num);
			holder.lin_check=(LinearLayout) convertView.findViewById(R.id.lin_check);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		InStockSkuCheck item = skuList.get(position);
		holder.check_sku_code.setText("编码:  " + item.skuCode);
		if(item.planQty==item.factQty){
			holder.check_sku_really_num.setTextColor(Color.GREEN);
		}else{
			holder.check_sku_really_num.setTextColor(Color.RED);
		}
		holder.check_sku_should_num.setText(String.valueOf(item.planQty));
		holder.check_sku_really_num.setText(String.valueOf(item.factQty));
		return convertView;
	}
	class ViewHolder{
		LinearLayout lin_check;
		TextView check_sku_code;
		TextView check_sku_should_num;
		TextView check_sku_really_num;
	}
}
