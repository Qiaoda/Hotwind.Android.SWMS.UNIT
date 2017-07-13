package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.view.MoreCustomDialog;
import cn.jitmarketing.hot.view.MoreCustomDialog.OnCustomDialogListener;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ResetSkuBatchAdapter extends BaseAdapter {
	
	private Activity activity;
	private List<InStockDetail> list;
	private LayoutInflater inflater;
	
    public ResetSkuBatchAdapter (Activity activity, List<InStockDetail> list) {
    	inflater = LayoutInflater.from(activity);
    	this.activity = activity;
    	this.list = list;
    }
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.take_goods_batch_item_layout, null);
			holder = new ViewHolder();
			holder.shelf_code_txt = (TextView) convertView.findViewById(R.id.shelf_code_txt);
			holder.sku_num_txt = (TextView) convertView.findViewById(R.id.sku_num_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				new MoreCustomDialog(activity, (int)list.get(position).SKUCount, new OnCustomDialogListener() {
					
					@Override
					public void delete() {
						list.remove(position);
						notifyDataSetChanged();
					}
					
					@Override
					public void confirm(int num) {
						list.get(position).SKUCount = num;
						notifyDataSetChanged();
					}
				}).show();
				return false;
			}
		});
		holder.shelf_code_txt.setText(list.get(position).ShelfLocationCode);
		holder.sku_num_txt.setText(String.valueOf((int)list.get(position).SKUCount));
		return convertView;
	}
   class ViewHolder{
	   TextView shelf_code_txt;
	   TextView sku_num_txt;
   }
}
