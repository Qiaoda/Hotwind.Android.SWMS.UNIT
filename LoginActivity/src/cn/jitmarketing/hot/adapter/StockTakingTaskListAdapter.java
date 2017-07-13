package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.StockTakingTaskEntity;
import cn.jitmarketing.hot.ui.shelf.StockTakingShopownerActivity;
import cn.jitmarketing.hot.ui.shelf.StockTakingTaskActivity;
import cn.jitmarketing.hot.ui.shelf.StockTakingTaskDetailActivity;

public class StockTakingTaskListAdapter extends BaseAdapter {

	private List<StockTakingTaskEntity> stockList;
	private LayoutInflater inflater;    
	private Activity activity;

	public StockTakingTaskListAdapter(Activity activity, List<StockTakingTaskEntity> stockList) {
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
			convertView = inflater.inflate(R.layout.stock_taking_task_item, null);  
			holder = new ViewHolder();
			holder.stock_name = (TextView) convertView
					.findViewById(R.id.stock_name);
			holder.stock_time = (TextView) convertView
					.findViewById(R.id.stock_time);
			holder.stock_detail = (Button) convertView
					.findViewById(R.id.stock_detail);
			holder.stock_finish = (Button) convertView
					.findViewById(R.id.stock_finish);
			holder.stock_status = (TextView) convertView
					.findViewById(R.id.stock_status);
			holder.isfinsh_layout = (LinearLayout) convertView
					.findViewById(R.id.isfinsh_layout);
			holder.stock_line = convertView
					.findViewById(R.id.stock_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final StockTakingTaskEntity task = stockList.get(position);
		if(task.Status == 0) {//0是开始1是结束
			holder.isfinsh_layout.setVisibility(View.VISIBLE);
			holder.stock_status.setVisibility(View.GONE);
			holder.stock_line.setVisibility(View.GONE);
			holder.stock_time.setVisibility(View.GONE);
		} else {
			holder.isfinsh_layout.setVisibility(View.GONE);
			holder.stock_status.setVisibility(View.VISIBLE);
			holder.stock_line.setVisibility(View.VISIBLE);
			holder.stock_time.setVisibility(View.VISIBLE);
		}
		holder.stock_name.setText(task.CheckTaskName);
		holder.stock_time.setText("盘点时间:  " + task.ConfirmTaskTime);
		holder.stock_status.setText(task.ResultStatusString);
		holder.stock_detail.setOnClickListener(clickListener);
		holder.stock_finish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((StockTakingTaskActivity)activity).finishTask(task.CheckTaskID);
			}
		});
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(task.Status == 1) {
					Intent intent = new Intent();
					intent.setClass(activity, StockTakingTaskDetailActivity.class);
					Bundle bd = new Bundle();
					bd.putString("checkTaskID", task.CheckTaskID);
					intent.putExtras(bd);
					activity.startActivity(intent);
				}
			}
		});
		return convertView;
	}
	
	public OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.stock_detail:
				Intent intent = new Intent();
				intent.setClass(activity, StockTakingShopownerActivity.class);
				activity.startActivity(intent);
				break;

			}
			
		}
	};

	class ViewHolder {
		TextView stock_name;
		TextView stock_time;
		Button stock_detail;
		Button stock_finish;
		LinearLayout isfinsh_layout;
		View stock_line;
		TextView stock_status;
	}

}
