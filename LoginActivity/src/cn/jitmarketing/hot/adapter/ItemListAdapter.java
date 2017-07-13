package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ItemBean;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.util.ImageUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemListAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<SearchSkuBean> searchSkuList;

	public ItemListAdapter(Context ctx, List<SearchSkuBean> searchSkuList) {
		this.searchSkuList = searchSkuList;
		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return searchSkuList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return searchSkuList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fuzzy_item_layout, null);
			holder = new ViewHolder();
			holder.goods_sku_icon = (ImageView) convertView
					.findViewById(R.id.goods_sku_icon);
			holder.goods_sku_code = (TextView) convertView
					.findViewById(R.id.goods_sku_code);
			holder.goods_sku_name = (TextView) convertView
					.findViewById(R.id.goods_sku_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageUtil.getBitmapByBase64(searchSkuList.get(position).ItemImage, holder.goods_sku_icon);
		holder.goods_sku_code.setText(searchSkuList.get(position).ItemCode);
		holder.goods_sku_name.setText(searchSkuList.get(position).ItemName);

		return convertView;
	}

	class ViewHolder {
		ImageView goods_sku_icon;
		TextView goods_sku_code;
		TextView goods_sku_name;
	}

}
