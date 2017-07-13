package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.TakeGoodsEntity;
import cn.jitmarketing.hot.util.CommonUtil;
import cn.jitmarketing.hot.util.ImageUtil;

public class TakeGoodsListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<TakeGoodsEntity> skuList;
	private Activity activity;
	int selectItem=-1;
	
  	public TakeGoodsListAdapter(Activity activity, List<TakeGoodsEntity> skuList){
		inflater = activity.getLayoutInflater();
		this.skuList = skuList;
		this.activity = activity;
	}
	
	public void setData(List<TakeGoodsEntity> skuList){
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.take_goods_item_layout, null);
			holder = new ViewHolder();
			holder.apply_time = (TextView) convertView.findViewById(R.id.apply_time);
			holder.self_sku_layout = (LinearLayout) convertView.findViewById(R.id.self_sku_layout);
			holder.goods_sku_code = (TextView) convertView.findViewById(R.id.goods_sku_code);
			holder.goods_sku_sales = (TextView) convertView.findViewById(R.id.goods_sku_sales);
			holder.goods_sku_action = (TextView) convertView.findViewById(R.id.goods_sku_action);
			holder.goods_sku_icon = (ImageView) convertView.findViewById(R.id.goods_sku_icon);
			holder.goods_sku_name = (TextView) convertView.findViewById(R.id.goods_sku_name);
			holder.goods_sku_color = (TextView) convertView.findViewById(R.id.goods_sku_color);
			holder.goods_sku_size = (TextView) convertView.findViewById(R.id.goods_sku_size);
			holder.goods_sku_price = (TextView) convertView.findViewById(R.id.goods_sku_price);
			holder.goods_sku_change_price = (TextView) convertView.findViewById(R.id.goods_sku_change_price);
			holder.goods_sku_stock_num = (TextView) convertView.findViewById(R.id.goods_sku_stock_num);
			holder.goods_sku_stock = (TextView) convertView.findViewById(R.id.goods_sku_stock);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		TakeGoodsEntity item = skuList.get(position);
		holder.goods_sku_code.setText(item.SKUID);
		holder.goods_sku_sales.setText(item.LastUpdateByName);
		holder.goods_sku_action.setText(item.OperTypeString);
		holder.goods_sku_price.setText("￥" + item.Price);
		holder.goods_sku_change_price.setText(item.ChangePrice);
		if(item.ChangePrice != null && !item.ChangePrice.equals("")&&!item.ChangePrice.equals(item.Price)) {
			holder.goods_sku_price.setTextColor(activity.getResources().getColor(R.color.color_gray_text));
			holder.goods_sku_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //设置中划线并加清晰
			holder.goods_sku_price.setTextSize(12);
			holder.goods_sku_change_price.setVisibility(View.VISIBLE);
		} else {
			holder.goods_sku_price.setTextSize(14);
			holder.goods_sku_price.setTextColor(activity.getResources().getColor(R.color.color_green));
			holder.goods_sku_price.getPaint().setFlags(0); // 取消设置的的划线
			holder.goods_sku_change_price.setVisibility(View.GONE);
		}
		
		ImageUtil.getBitmapByBase64(item.Image, holder.goods_sku_icon);
		holder.goods_sku_name.setText(item.ItemName);
		holder.goods_sku_color.setText(item.ColorName);
		holder.goods_sku_size.setText(item.SizeName);
		holder.goods_sku_stock_num.setText(item.Qty);
		holder.goods_sku_stock.setText(item.StockSKUShelfLocationString);
//		holder.apply_time.setText(item.TimeOutString);
		Long ti = Long.valueOf(item.TimeOut);
//		if (ti <= 2 * 60) {
//			holder.apply_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
//		} else {
//			holder.apply_time.setTextColor(Color.RED);
//		}
		if (position == selectItem) {
			holder.self_sku_layout.setBackgroundResource(R.drawable.shadow_red_bg);
		} else {
			holder.self_sku_layout.setBackgroundResource(R.drawable.shadow_bg);
		}
		holder.apply_time.setText(CommonUtil.formatTime((item.TimeOut)));
		if(item.TransactionStatus == 1) {
			if(item.OpID.equals("4097")) {//撤样
				if (ti <= 60 * 60 / 2) {
					holder.apply_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				} else {
					holder.apply_time.setTextColor(Color.RED);
				}
			} else if(item.OpID.equals("1024") || item.OpID.equals("2048")  || item.OpID.equals("4096") || item.OpID.equals("4098")) {//待出货
				if (ti <= 2 * 60) {
					holder.apply_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				} else {
					holder.apply_time.setTextColor(Color.RED);
				}
			}
		} else if(item.TransactionStatus == 2) {
			if(item.OpID.equals("1024") || item.OpID.equals("2048")) {//待复位
				if (ti <= 60 * 60 / 2) {
					holder.apply_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				} else {
					holder.apply_time.setTextColor(Color.RED);
				}
			} else {
				holder.apply_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
			}
		} else {
			holder.apply_time.setTextColor(activity.getResources().getColor(R.color.color_black_text));
		}
		String url = null;
		if(item.ColorID == null || item.ColorID.equals("")) {
			url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + item.SKUID + ".jpg";
		} else {
			url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + item.ItemID + "$" + item.ColorID + ".jpg";
		}
		ImageUtil.uploadImage(activity, url, holder.goods_sku_icon);
		return convertView;
	}
	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
		notifyDataSetChanged();
	}
	
	class ViewHolder{
		TextView goods_sku_code;
		TextView goods_sku_sales;
		TextView goods_sku_action;
		ImageView goods_sku_icon;
		TextView goods_sku_name;
		TextView goods_sku_color;
		TextView goods_sku_size;
		TextView goods_sku_price;
		TextView goods_sku_change_price;
		TextView goods_sku_stock_num;
		TextView goods_sku_stock;
		TextView apply_time;
		LinearLayout self_sku_layout;
	}

}
