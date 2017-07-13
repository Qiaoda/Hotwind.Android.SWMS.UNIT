package cn.jitmarketing.hot.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResetEntity;
import cn.jitmarketing.hot.ui.shelf.HandResetActivity;
import cn.jitmarketing.hot.ui.shelf.ResetSkuActivity;
import cn.jitmarketing.hot.ui.shelf.ResetSkuBatchActivity;
import cn.jitmarketing.hot.util.CommonUtil;
import cn.jitmarketing.hot.util.ImageUtil;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResetListAdapter extends BaseAdapter {

	ResetSkuActivity ctx;
	int selectItem=-1;
	LayoutInflater inflater;
	ArrayList<ResetEntity> resetList;

	public ResetListAdapter(ResetSkuActivity ctx, ArrayList<ResetEntity> resetList) {
		inflater = LayoutInflater.from(ctx);
		this.resetList = resetList;
		this.ctx = ctx;
	}

	@Override
	public int getCount() {
		return resetList.size();
	}

	@Override
	public Object getItem(int position) {
		return resetList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.shelf_sku_item, null);
			holder = new ViewHolder();
			holder.stock_sku_icon = (ImageView) convertView.findViewById(R.id.stock_sku_icon);
			holder.self_sku_layout = (LinearLayout) convertView.findViewById(R.id.self_sku_layout);
			holder.shelf_sku_code = (TextView) convertView.findViewById(R.id.shelf_sku_co);
			holder.shelf_sku_type = (TextView) convertView.findViewById(R.id.shelf_sku_type);
			holder.shelf_sku_name = (TextView) convertView.findViewById(R.id.shelf_sku_name);
			holder.size = (TextView) convertView.findViewById(R.id.size);
			holder.color = (TextView) convertView.findViewById(R.id.color);
			holder.outnosale = (TextView) convertView.findViewById(R.id.outnosale);
			holder.shelf_sale = (TextView) convertView.findViewById(R.id.shelf_sale);
			holder.reset_time = (TextView) convertView.findViewById(R.id.reset_time);
			holder.location = (TextView) convertView.findViewById(R.id.location);
			holder.reset = (TextView) convertView.findViewById(R.id.reset);
			holder.person_type_text = (TextView) convertView.findViewById(R.id.person_type_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ResetEntity item = resetList.get(position);
		if (item.Qty.equals("1") || item.Qty.equals("0.5") || item.Qty.equals("0")) {
			holder.reset.setText("复位");
		} else {
			holder.reset.setText("批量复位");
		}
		holder.shelf_sku_code.setText(item.SKUCode);
		holder.shelf_sku_type.setText(item.OperTypeString);
		holder.shelf_sku_name.setText(item.ItemName);
		holder.color.setText(item.ColorName);
		holder.color.setTextColor(ctx.getResources().getColor(R.color.color_green));
		holder.size.setText(item.SizeName);
		holder.size.setTextColor(ctx.getResources().getColor(R.color.color_green));
		holder.outnosale.setText(item.QtyString);
		holder.outnosale.setTextColor(ctx.getResources().getColor(R.color.color_green));
		if (item.OpID.equals("4099")) {
			holder.person_type_text.setText("操作员:  ");
			// holder.location.setText("");
			holder.location.setText("推荐库位:  " + item.ShelfLocationCode);
		} else {
			holder.person_type_text.setText("销售员:  ");
			holder.location.setText("原库位:  " + item.ShelfLocationCode);
		}
		holder.shelf_sale.setText(item.OperPerson);
		holder.shelf_sale.setTextColor(ctx.getResources().getColor(R.color.color_green));
		Long ti = Long.valueOf(item.TimeOut);
		if (ti <= 60 * 60 / 2) {
			holder.reset_time.setTextColor(ctx.getResources().getColor(R.color.color_black_text));
		} else {
			holder.reset_time.setTextColor(Color.RED);
		}
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		if (Long.valueOf(item.TimeOut)/(60*60*24)>0) {
			holder.reset_time.setText(Long.valueOf(item.TimeOut)/(60*60*24)+"天"+format.format(Long.valueOf(item.TimeOut) * 1000 - TimeZone.getDefault().getRawOffset()));
		}else {
			holder.reset_time.setText(format.format(Long.valueOf(item.TimeOut) * 1000 - TimeZone.getDefault().getRawOffset()));	
		}
//		holder.reset_time.setText(format.format(Long.valueOf(item.TimeOut) * 1000 - TimeZone.getDefault().getRawOffset()));
//		holder.reset_time.setText(CommonUtil.formatTime((item.TimeOut)));
		holder.reset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ctx.selectEntity = resetList.get(position);
				if ((item.Qty.equals("1") || item.Qty.equals("0.5") || item.Qty.equals("0"))) {
					Intent intent = new Intent();
					Bundle bd = new Bundle();
					bd.putSerializable("reset", resetList.get(position));
					intent.putExtras(bd);
					intent.setClass(ctx, HandResetActivity.class);
					ctx.startActivity(intent);
				} else {
					Intent intentBatch = new Intent(ctx, ResetSkuBatchActivity.class);
					Bundle bd = new Bundle();
					bd.putString("skuCode", item.SKUCode);
					bd.putString("factId", item.FactID);
					bd.putInt("count", Integer.valueOf(item.Qty));
					intentBatch.putExtras(bd);
					ctx.startActivity(intentBatch);
				}
			}
		});

		if (position == selectItem) {
			holder.self_sku_layout.setBackgroundResource(R.drawable.shadow_red_bg);
		} else {
			holder.self_sku_layout.setBackgroundResource(R.drawable.shadow_bg);
		}
		String url = null;
		if (item.ColorID == null || item.ColorID.equals("")) {
			url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + item.SKUCode + ".jpg";
		} else {
			url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + item.ItemID + "$" + item.ColorID + ".jpg";
		}
		ImageUtil.uploadImage(ctx, url, holder.stock_sku_icon);
		return convertView;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	class ViewHolder {
		TextView shelf_sku_code;
		TextView shelf_sku_name;
		TextView shelf_sku_type;
		TextView person_type_text;
		TextView color;
		TextView size;
		TextView outnosale;
		TextView shelf_sale;
		TextView reset_time;
		TextView location;
		TextView reset;
		LinearLayout self_sku_layout;
		ImageView stock_sku_icon;
	}

}
