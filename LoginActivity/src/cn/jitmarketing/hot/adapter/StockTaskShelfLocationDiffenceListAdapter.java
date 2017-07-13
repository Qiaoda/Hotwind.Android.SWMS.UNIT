package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockDetaiShelfDiffEntity;
import cn.jitmarketing.hot.pandian.StockTakingShopperHistoryDetailActivity;

public class StockTaskShelfLocationDiffenceListAdapter extends BaseAdapter {

	private List<StockDetaiShelfDiffEntity> list;
	private LayoutInflater inflater;
	private Activity activity;

	public StockTaskShelfLocationDiffenceListAdapter(Activity activity,
			List<StockDetaiShelfDiffEntity> list) {
		this.list = list;
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.stock_taking_detail_shelf_diff_item, null);
			holder = new ViewHolder();
			holder.shelf_code_txt = (TextView) convertView
					.findViewById(R.id.shelf_code_txt);
			holder.check_time_txt = (TextView) convertView
					.findViewById(R.id.check_time_txt);
			holder.status_txt = (TextView) convertView
					.findViewById(R.id.status_txt);
			holder.operator_txt = (TextView) convertView
					.findViewById(R.id.operator_txt);
			holder.money_txt = (TextView) convertView
					.findViewById(R.id.money_txt);
			holder.count_txt = (TextView) convertView
					.findViewById(R.id.count_txt);
			holder.line = convertView
					.findViewById(R.id.line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final StockDetaiShelfDiffEntity entity = list.get(position);
		holder.shelf_code_txt.setText(entity.ShelfLocationName);
		holder.check_time_txt.setText(entity.LastUpdateTime.replace("T", " "));
		holder.operator_txt.setText(entity.UserCode);
		holder.money_txt.setText(entity.DiffPriceCount == null ? "0" : entity.DiffPriceCount);
		holder.count_txt.setText(String.valueOf(entity.DiffCount));
		if(entity.CheckStatus == 0) {
			holder.status_txt.setVisibility(View.GONE);
			holder.line.setVisibility(View.GONE);
		} else if(entity.CheckStatus == 1) {
			holder.status_txt.setVisibility(View.VISIBLE);
			holder.line.setVisibility(View.VISIBLE);
			holder.status_txt.setText("正确");
			holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_green));
		} else if(entity.CheckStatus == 2) {
			holder.status_txt.setVisibility(View.VISIBLE);
			holder.line.setVisibility(View.VISIBLE);
			holder.status_txt.setText("有差异");
			holder.status_txt.setTextColor(activity.getResources().getColor(R.color.color_red_text));
		}
		return convertView;
	}

	class ViewHolder {
		TextView shelf_code_txt;
		TextView check_time_txt;
		TextView operator_txt;
		TextView status_txt;
		TextView money_txt;
		TextView count_txt;
		View line;
	}

}
