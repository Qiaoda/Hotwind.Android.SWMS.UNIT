package cn.jitmarketing.hot.view.sortlistview;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockTakingWarehouseEntity;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<StockTakingWarehouseEntity> stockList = null;
	private Activity activity;
	
	public SortAdapter(Activity activity, List<StockTakingWarehouseEntity> stockList) {
		this.activity = activity;
		this.stockList = stockList;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param stockList
	 */
	public void updateListView(List<StockTakingWarehouseEntity> stockList){
		this.stockList = stockList;
		notifyDataSetChanged();
	}

	public List<StockTakingWarehouseEntity> getStockList() {
		return stockList;
	}

	public int getCount() {
		return this.stockList.size();
	}

	public Object getItem(int position) {
		return stockList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		final StockTakingWarehouseEntity entity = stockList.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(activity).inflate(R.layout.stock_taking_warehouse_item, null);
//			holder.tvTitle = (TextView) view.findViewById(R.id.title);
			holder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			holder.shelf_code_txt = (TextView) convertView
					.findViewById(R.id.shelf_code_txt);
			holder.check_time_txt = (TextView) convertView
					.findViewById(R.id.check_time_txt);
			holder.status_txt = (TextView) convertView
					.findViewById(R.id.status_txt);
			holder.operator_txt = (TextView) convertView
					.findViewById(R.id.operator_txt);
			holder.dif_money_txt = (TextView) convertView
					.findViewById(R.id.dif_money_txt);
			holder.dif_count_txt = (TextView) convertView
					.findViewById(R.id.dif_count_txt);
			holder.show_layout = (LinearLayout) convertView
					.findViewById(R.id.show_layout);
			holder.money_layout = (LinearLayout) convertView
					.findViewById(R.id.money_layout);
			holder.count_layout = (LinearLayout) convertView
					.findViewById(R.id.count_layout);
			holder.line = convertView
					.findViewById(R.id.line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(entity.getSortLetters());
		}else{
			holder.tvLetter.setVisibility(View.GONE);
		}
	
		holder.dif_count_txt.setText(String.valueOf(entity.DiffCount));
		holder.dif_money_txt.setText(entity.PriceCount);
		if(entity.ShelfLocationCode.equals("EX00001")) {//样品库
//		if(entity.ShelfLocationCode.equals("EX00001") || entity.ShelfLocationCode.equals("SC00001")) {//样品库
			if(entity.IsConfirm == 1) {
				holder.money_layout.setVisibility(View.VISIBLE);
				holder.count_layout.setVisibility(View.VISIBLE);
			} else {
				holder.money_layout.setVisibility(View.GONE);
				holder.count_layout.setVisibility(View.GONE);
			}
		} else {
			holder.money_layout.setVisibility(View.VISIBLE);
			holder.count_layout.setVisibility(View.VISIBLE);
		}
		if(entity.CheckStatus == 0) {
			if(entity.ShelfLocationCode.equals("EX00001")) {//样品库
//			if(entity.ShelfLocationCode.equals("EX00001") || entity.ShelfLocationCode.equals("SC00001")) {//样品库
				holder.line.setVisibility(View.VISIBLE);
				holder.shelf_code_txt.setPadding(0, 0, 0, 0);
				if(entity.IsConfirm == 1) {
					holder.status_txt.setText("已确认");
					holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_green));
				} else {
					if(entity.IsStartCheck == 1) {
						holder.status_txt.setText("未确认");
						holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_red_text));
					} else {
						holder.status_txt.setText("未开启");
						holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_red_text));
						holder.shelf_code_txt.setPadding(0, 4, 8, 4);
					}
				}
			} else {
				holder.status_txt.setText("");
				holder.shelf_code_txt.setPadding(0, 4, 8, 4);
				holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_black_text));
				holder.line.setVisibility(View.GONE);
			}
		} else {
			holder.shelf_code_txt.setPadding(0, 0, 0, 0);
			holder.line.setVisibility(View.VISIBLE);
			if(entity.IsConfirm == 1) {
				holder.status_txt.setText("已确认");
				holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_green));
			} else {
				holder.status_txt.setText("未确认");
				holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_red_text));
			}
		}
		holder.shelf_code_txt.setText(entity.ShelfLocationName);
		if(entity.CheckDateTime != null && !entity.CheckDateTime.equals("")) {
			holder.check_time_txt.setVisibility(View.VISIBLE);
			holder.check_time_txt.setText(entity.CheckDateTime);
		} else {
			holder.check_time_txt.setText(null);
			holder.check_time_txt.setVisibility(View.GONE);
		}
		if(entity.CheckPersonName != null && !entity.CheckPersonName.equals("")) {
			holder.operator_txt.setText(entity.CheckPersonName);
			holder.operator_txt.setVisibility(View.VISIBLE);
		} else {
			holder.operator_txt.setText(null);
			holder.operator_txt.setVisibility(View.GONE);
		}
		
		if(entity.CheckStatus == 0) {
			holder.show_layout.setVisibility(View.GONE);
		} else {
			holder.show_layout.setVisibility(View.VISIBLE);
		}
//		if(entity.IsStartCheck != null) {
//			if(entity.IsStartCheck.equals("1")) {//开启盘点
//				
//			} else {
//				
//			}
//		} else {
//			
//		}
		return convertView;

	}
	


	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		TextView shelf_code_txt;
		TextView check_time_txt;
		TextView operator_txt;
		TextView status_txt;
		TextView dif_money_txt;
		TextView dif_count_txt;
		LinearLayout show_layout;
		LinearLayout money_layout;
		LinearLayout count_layout;
		View line;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return stockList.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = stockList.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}