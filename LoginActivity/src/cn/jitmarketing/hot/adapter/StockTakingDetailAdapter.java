package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockTakingDetailEntity;
import cn.jitmarketing.hot.pandian.StockTakingShopperDetailActivity;
import cn.jitmarketing.hot.view.PandianCustomDialog;
import cn.jitmarketing.hot.view.PandianCustomDialog.OnCustomDialogListener;

public class StockTakingDetailAdapter extends BaseAdapter {

	private List<StockTakingDetailEntity> stockList;
	private LayoutInflater inflater;    
	private Activity activity;

	public StockTakingDetailAdapter(Activity activity, List<StockTakingDetailEntity> stockList) {
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
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.stock_taking_shopper_detail_item, null);  
			holder = new ViewHolder();
			holder.sku_txt = (TextView) convertView
					.findViewById(R.id.sku_txt);
			holder.sys_txt = (TextView) convertView
					.findViewById(R.id.sys_txt);
			holder.actual_txt = (TextView) convertView
					.findViewById(R.id.actual_txt);
			holder.dif_txt = (TextView) convertView
					.findViewById(R.id.dif_txt);
			holder.dif_count_txt = (TextView) convertView
					.findViewById(R.id.dif_count_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final StockTakingDetailEntity entity = stockList.get(position);
		holder.sku_txt.setText(entity.SKUID);
		holder.sys_txt.setText(String.valueOf(entity.KCSKUCount));
		holder.actual_txt.setText(String.valueOf(entity.PDSKUCount));
		holder.dif_count_txt.setText(String.valueOf(entity.CYCount));
		holder.dif_txt.setText(entity.CYPrice);
		if(Double.valueOf(entity.CYPrice) < 0) {
			holder.dif_txt.setTextColor(activity.getResources().getColor(R.color.color_red_text));
		} else {
			holder.dif_txt.setTextColor(activity.getResources().getColor(R.color.color_green));
		}
//		convertView.setOnLongClickListener(new OnLongClickListener() {
//			
//			@Override
//			public boolean onLongClick(View v) {
//				new PandianCustomDialog(activity, entity.PDSKUCount, entity.SKUID, new OnCustomDialogListener() {
//					
//					@Override
//					public void confirm(int num) {
////						entity.PDSKUCount = num;
////						entity.Status = 1;
//						((StockTakingShopperDetailActivity)activity).modify(entity, num);
//					}
//				}).show();
//				return false;
//			}
//		});
		return convertView;
	}

	class ViewHolder {
		TextView sku_txt;
		TextView sys_txt;
		TextView actual_txt;
		TextView dif_txt;
		TextView dif_count_txt;
	}

}
