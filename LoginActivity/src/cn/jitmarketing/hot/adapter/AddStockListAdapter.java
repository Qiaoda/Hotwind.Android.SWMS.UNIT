package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockBean;
import cn.jitmarketing.hot.ui.sku.AddStockActivity;

public class AddStockListAdapter extends BaseAdapter {

	private AddStockActivity activity;
	private List<StockBean> stockList;
	private LayoutInflater inflater;    

	public AddStockListAdapter(AddStockActivity activity, List<StockBean> stockList) {
		this.activity = activity;
		this.stockList = stockList;
		inflater = LayoutInflater.from(activity);
	}


	@Override
	public int getCount() {
		return stockList.size();
	}

	@Override
	public Object getItem(int position) {

		return stockList.get(position);
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
			convertView = inflater.inflate(R.layout.add_stock_item_layout, null);  
			holder = new ViewHolder();
			holder.stockTextView = (TextView) convertView
					.findViewById(R.id.stock_name);
			holder.stockStatusTextView = (TextView) convertView
					.findViewById(R.id.stock_status);
			holder.deleteButton = (TextView) convertView
					.findViewById(R.id.stock_delete_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(stockList.get(position).status == 0) {
			holder.stockStatusTextView.setText("已保存");
			holder.stockStatusTextView.setTextColor(activity.getResources().getColor(R.color.color_black_text));
		} else{
			holder.stockStatusTextView.setText("正在保存");
			holder.stockStatusTextView.setTextColor(activity.getResources().getColor(R.color.aaa));
		}
		holder.stockTextView.setText(stockList.get(position).stockCode);
		holder.deleteButton.setText("删除");
		holder.deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.delete(stockList.get(position).stockCode, position);
//				stockList.remove(position);
//				activity.stockList.remove(position);
//				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView stockTextView;
		TextView stockStatusTextView;
		TextView deleteButton;
	}

}
