package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.AllocationBean;
import cn.jitmarketing.hot.ui.shelf.AllocationActivity;

public class AllocationListAdapter extends BaseAdapter {

	private List<AllocationBean> allocationList;
	private LayoutInflater inflater;    
	private AllocationActivity activity;

	public AllocationListAdapter(AllocationActivity activity, List<AllocationBean> allocationList) {
		this.allocationList = allocationList;
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getCount() {
		return allocationList.size();
	}

	@Override
	public Object getItem(int position) {

		return allocationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.allocation_item_layout, null);  
			holder = new ViewHolder();
			holder.allocation_code = (TextView) convertView
					.findViewById(R.id.allocation_code);
			holder.allcation_come = (TextView) convertView
					.findViewById(R.id.allcation_come);
			holder.allocation_operater_txt = (TextView) convertView
					.findViewById(R.id.allocation_operater_txt);
			holder.allocation_time = (TextView) convertView
					.findViewById(R.id.allocation_time);
			holder.allcation_type = (TextView) convertView
					.findViewById(R.id.allcation_type);
			holder.come_layout = (LinearLayout) convertView
					.findViewById(R.id.come_layout);
			holder.allcation_delete = (Button) convertView
					.findViewById(R.id.allcation_delete);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.allocation_code.setText(allocationList.get(position).allocationOrderCode);
		String iddd=allocationList.get(position).TargetUnitID;
		holder.allcation_come.setText(allocationList.get(position).targetUnitName+"("+allocationList.get(position).TargetUnitID+")");
		holder.allocation_time.setText(allocationList.get(position).createTime);
		holder.allocation_operater_txt.setText(allocationList.get(position).OperPerson);
		if(allocationList.get(position).targetUnitName == null || allocationList.get(position).targetUnitName.equals("")) {
			holder.come_layout.setVisibility(View.GONE);
		} else {	
			holder.come_layout.setVisibility(View.VISIBLE);
		}
		if(allocationList.get(position).Status == 0) {
			holder.allcation_type.setText("暂存中");
			holder.allcation_type.setVisibility(View.VISIBLE);
			holder.allcation_delete.setVisibility(View.VISIBLE);
			holder.allcation_type.setTextColor(activity.getResources().getColor(R.color.color_red_text));
		} else {
			holder.allcation_type.setText("");
			if(allocationList.get(position).OrderType == 1) {
				holder.allcation_type.setText("调拨");
				holder.allcation_type.setTextColor(activity.getResources().getColor(R.color.diaobo));
			} else if(allocationList.get(position).OrderType == 2) {
				holder.allcation_type.setText("退次");
				holder.allcation_type.setTextColor(activity.getResources().getColor(R.color.tuici));
			} else if(allocationList.get(position).OrderType == 3) {
				holder.allcation_type.setText("退仓");
				holder.allcation_type.setTextColor(activity.getResources().getColor(R.color.tuicang));
			} else {
				holder.allcation_type.setText("");
				holder.allcation_type.setTextColor(activity.getResources().getColor(R.color.color_red_text));
			}
			holder.allcation_type.setVisibility(View.VISIBLE);
			holder.allcation_delete.setVisibility(View.GONE);
		}
		holder.allcation_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(activity)
				.setMessage("是否清除暂存数据?")
				.setPositiveButton(R.string.sureTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						activity.delete(allocationList.get(position).AllocationOrderID);
					}
				})
				.setNegativeButton(R.string.cancelTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
	
					}
				}).show();
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView allocation_code;
		TextView allocation_status;
		TextView allcation_come;
		TextView allocation_time;
		TextView allcation_type;
		LinearLayout come_layout;
		TextView allocation_operater_txt;
		Button allcation_delete;
	}

}
