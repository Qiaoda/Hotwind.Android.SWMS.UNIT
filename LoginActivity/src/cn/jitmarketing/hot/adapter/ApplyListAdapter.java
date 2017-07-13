package cn.jitmarketing.hot.adapter;

import java.util.List;

import com.ex.lib.core.utils.mgr.MgrPerference;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ApplyEntity;
import cn.jitmarketing.hot.ui.sku.ApplyListActivity;
import cn.jitmarketing.hot.util.CommonUtil;

public class ApplyListAdapter extends BaseAdapter {

	private ApplyListActivity activity;
	private List<ApplyEntity> skuList;
	private LayoutInflater inflater;

	public ApplyListAdapter(ApplyListActivity activity, List<ApplyEntity> skuList){
		this.activity = activity;
		inflater = activity.getLayoutInflater();
		this.skuList = skuList;
	}

	public void setData(List<ApplyEntity> skuList){
		this.skuList.clear();
		skuList.addAll(skuList);
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
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.apply_item_layout, null);
			holder = new ViewHolder();
			holder.v_view = (View) convertView.findViewById(R.id.v_view);
			holder.apply_type = (TextView) convertView.findViewById(R.id.apply_type);
			holder.apply_cancel = (TextView) convertView.findViewById(R.id.apply_cancel);
			holder.goods_sku_code = (TextView) convertView.findViewById(R.id.goods_sku_code);
			holder.goods_sku_icon = (ImageView) convertView.findViewById(R.id.goods_sku_icon);
			holder.goods_sku_name = (TextView) convertView.findViewById(R.id.goods_sku_name);
			holder.goods_sku_color = (TextView) convertView.findViewById(R.id.goods_sku_color);
			holder.goods_sku_size = (TextView) convertView.findViewById(R.id.goods_sku_size);
			holder.actual_size = (TextView) convertView.findViewById(R.id.actual_size);
			holder.goods_sku_stock_num = (TextView) convertView.findViewById(R.id.goods_sku_stock_num);
			holder.goods_sku_stock = (TextView) convertView.findViewById(R.id.goods_sku_stock);
			holder.goods_sku_action = (TextView) convertView.findViewById(R.id.goods_sku_action);
			holder.goods_sku_time = (TextView) convertView.findViewById(R.id.goods_sku_time);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		final ApplyEntity item = skuList.get(position);
		holder.goods_sku_code.setText("商品编码:  " + item.SKUID);
//		ImageUtil.getBitmapByBase64(item.Image, holder.goods_sku_icon);
		holder.goods_sku_name.setText(item.ItemName);
		holder.goods_sku_color.setText(item.ColorName);
		holder.goods_sku_size.setText(item.SizeName);
//		holder.goods_sku_price.setText("价格: " + item.Price);
		holder.goods_sku_stock_num.setText(item.QtyString);
//		holder.goods_sku_stock.setText(item.ShelfLocation);
		holder.actual_size.setText(item.OpeQty);
		holder.goods_sku_action.setText(item.TransactionStatusString);
		holder.apply_type.setText(item.OperTypeString);
		holder.goods_sku_time.setText(item.OperTime);
		if(item.TransactionStatus == 1) {
			if(MgrPerference.getInstance(activity).getString(HotConstants.User.ROLE_CODE).equals("DZ") || item.RequestPersonID.equals(HotApplication.getInstance().getUserId())) {
				holder.apply_cancel.setVisibility(View.VISIBLE);
				holder.v_view.setVisibility(View.VISIBLE);
			} else {
				holder.apply_cancel.setVisibility(View.GONE);
				holder.v_view.setVisibility(View.GONE);
			}
		} else {
			holder.apply_cancel.setVisibility(View.GONE);
			holder.v_view.setVisibility(View.GONE);
		}
		
//		if(item.RequestPersonID.equals(HotApplication.getInstance().getUserId()) && item.TransactionStatus == 1) {//未完成状态
//			holder.apply_cancel.setVisibility(View.VISIBLE);
//			holder.v_view.setVisibility(View.VISIBLE);
//		} else {
//			holder.apply_cancel.setVisibility(View.GONE);
//			holder.v_view.setVisibility(View.GONE);
//		}
		Long ti = Long.valueOf(item.TimeOut);
		if(item.TransactionStatus == 1) {
			holder.goods_sku_time.setText(CommonUtil.formatTime((item.TimeOut)));
			if(item.OpID.equals("4097")) {//撤样
				if (ti <= 60 * 60 / 2) {
					holder.goods_sku_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				} else {
					holder.goods_sku_time.setTextColor(Color.RED);
				}
			} else if(item.OpID.equals("1024") || item.OpID.equals("2048")  || item.OpID.equals("4096") || item.OpID.equals("4098")) {//待出货
				if (ti <= 2 * 60) {
					holder.goods_sku_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				} else {
					holder.goods_sku_time.setTextColor(Color.RED);
				}
			}
		} else if(item.TransactionStatus == 2) {
			if(item.OpID.equals("1024") || item.OpID.equals("2048")) {//待复位
				holder.goods_sku_time.setText(CommonUtil.formatTime((item.TimeOut)));
				if (ti <= 60 * 60 / 2) {
					holder.goods_sku_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				} else {
					holder.goods_sku_time.setTextColor(Color.RED);
				}
			} else {
				holder.goods_sku_time.setText(item.OperTime);
				holder.goods_sku_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
			}
		} else {
			holder.goods_sku_time.setText(item.OperTime);
			holder.goods_sku_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
		}
		holder.apply_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				activity.applayCancel(item);
			}
		});
		return convertView;
	}
	
	class ViewHolder{
		TextView goods_sku_code;
		ImageView goods_sku_icon;
		TextView goods_sku_name;
		TextView goods_sku_color;
		TextView goods_sku_size;
		TextView actual_size;
		TextView goods_sku_stock_num;
		TextView goods_sku_stock;
		TextView goods_sku_action;
		TextView goods_sku_time;
		TextView apply_cancel;
		TextView apply_type;
		View v_view;
		
	}

}
