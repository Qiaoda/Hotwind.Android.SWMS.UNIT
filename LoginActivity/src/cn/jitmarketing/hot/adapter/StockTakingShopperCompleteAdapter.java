package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockTakingShopownerEntity;
import cn.jitmarketing.hot.pandian.StockTakingShopperDetailActivity;
import cn.jitmarketing.hot.pandian.StockTakingShopperDetailListActivity;
import cn.jitmarketing.hot.util.LogUtils;

public class StockTakingShopperCompleteAdapter extends BaseAdapter {

	private List<StockTakingShopownerEntity> stockList;
	private LayoutInflater inflater;    
	private Activity activity;

	public StockTakingShopperCompleteAdapter(Activity activity, List<StockTakingShopownerEntity> stockList) {
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
			convertView = inflater.inflate(R.layout.stock_taking_shopper_complete_item, null);  
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
			holder.sample_layout = (LinearLayout) convertView
					.findViewById(R.id.sample_layout);
			holder.pandian_start_btn = (Button) convertView
					.findViewById(R.id.pandian_start_btn);
			holder.pandian_end_btn = (Button) convertView
					.findViewById(R.id.pandian_end_btn);
			holder.horizontal_line = convertView
					.findViewById(R.id.horizontal_line);
			holder.system_count_txt = (TextView)convertView
					.findViewById(R.id.system_count_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final StockTakingShopownerEntity entity = stockList.get(position);
		holder.shelf_code_txt.setText(entity.ShelfLocationName);
		holder.check_time_txt.setText(entity.CheckDateTime);
		holder.operator_txt.setText(entity.CheckPersonName);
		holder.system_count_txt.setText(String.valueOf(entity.SysStock));
		holder.money_txt.setText(entity.PriceCount);
		holder.count_txt.setText(entity.DiffCount);
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
//		if(entity.ShelfLocationCode.equals("EX00001") || entity.ShelfLocationCode.equals("SC00001")) {
		if(entity.ShelfLocationCode.equals("EX00001")) {
			holder.sample_layout.setVisibility(View.VISIBLE);
			holder.horizontal_line.setVisibility(View.VISIBLE);
		} else {
			holder.sample_layout.setVisibility(View.GONE);
			holder.horizontal_line.setVisibility(View.GONE);
		}
		holder.pandian_start_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//日志操作
				LogUtils.logOnFile("盘点明细->样品库->盘点开始");
				new AlertDialog.Builder(activity)
				.setTitle(R.string.noteTitle)
				.setMessage("确定开启盘点？")
				.setPositiveButton(R.string.sureTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						//日志操作
						LogUtils.logOnFile("盘点明细->样品库->盘点开始->确认");
						if(entity.ShelfLocationCode.equals("EX00001"))
							((StockTakingShopperDetailListActivity)activity).openSample("EX00001", false);
//						else if(entity.ShelfLocationCode.equals("SC00001"))
//							((StockTakingShopperDetailListActivity)activity).openSmallSample("SC00001", false);
					}
				})
				.setNegativeButton(R.string.cancelTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						LogUtils.logOnFile("盘点明细->样品库->盘点开始->取消");
					}
				}).show();
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, StockTakingShopperDetailActivity.class);
				Bundle bd = new Bundle();
				bd.putString("ShelfLocationCode", entity.ShelfLocationCode);
				intent.putExtras(bd);
				activity.startActivity(intent);
				//日志操作
				LogUtils.logOnFile("盘点明细->已确认->库位："+entity.ShelfLocationCode);
			}
		});
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
		View horizontal_line;
		LinearLayout sample_layout;
		Button pandian_start_btn;
		Button pandian_end_btn;
		TextView system_count_txt;
	}

}
