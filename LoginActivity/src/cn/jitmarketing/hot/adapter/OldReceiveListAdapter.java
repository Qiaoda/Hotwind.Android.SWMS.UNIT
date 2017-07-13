package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ReceiveListAdapter.ViewHolder;
import cn.jitmarketing.hot.entity.ReceiveSheet;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OldReceiveListAdapter extends BaseAdapter{
	private LayoutInflater inflater;
	private List<ReceiveSheet> receList;
	private Activity activity;

	public OldReceiveListAdapter(Activity activity, LayoutInflater inflater, List<ReceiveSheet> receList){
		this.inflater = inflater;
		this.receList = receList;
		this.activity = activity;
	}


	@Override
	public int getCount() {
		return receList.size();
	}

	@Override
	public Object getItem(int position) {

		return receList.get(position);
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
			convertView = inflater.inflate(R.layout.receive_item_layout, null);
			holder = new ViewHolder();
			holder.rece_sku_code = (TextView) convertView.findViewById(R.id.rece_sku_code);
			holder.rece_count = (TextView) convertView.findViewById(R.id.rece_count);
			holder.rece_status = (TextView) convertView.findViewById(R.id.rece_status);
			holder.rece_in_time = (TextView) convertView.findViewById(R.id.rece_in_time);
			holder.finish_layout=(LinearLayout) convertView.findViewById(R.id.finish_layout);
			holder.rece_result=(TextView) convertView.findViewById(R.id.rece_result);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		ReceiveSheet item = receList.get(position);
		holder.rece_sku_code.setText("发货单号:" + item.receiveSheetNo);
		holder.rece_count.setText(Html.fromHtml("本批次共<font color='red'>" +(int)item.skuQty + "</font>件"));
		if(item.status == 1) {
			holder.rece_status.setText("待执行");
			holder.rece_status.setTextColor(activity.getResources().getColor(R.color.bbb));
			holder.rece_status.setVisibility(View.VISIBLE);
			holder.finish_layout.setVisibility(View.GONE);
		} else if(item.status == 2) {
			holder.rece_status.setText("已完成");
			holder.rece_status.setTextColor(activity.getResources().getColor(R.color.color_green));
			if(item.ResultStatus==2){
				holder.rece_result.setText("有差异");
				holder.rece_result.setTextColor(activity.getResources().getColor(R.color.bbb));
			}else{
				holder.rece_result.setText("无差异");
				holder.rece_result.setTextColor(activity.getResources().getColor(R.color.color_green));
			}
			holder.rece_status.setVisibility(View.VISIBLE);
			holder.finish_layout.setVisibility(View.VISIBLE);
			if(item.updateTime.split("T").length == 1) {
				holder.rece_in_time.setText(item.updateTime);
			} else if(item.updateTime.split("T").length == 2) {
				holder.rece_in_time.setText(item.updateTime.split("T")[0] + " " + item.updateTime.split("T")[1]);
			}
		}
		return convertView;
	}

	class ViewHolder{
		TextView rece_sku_code;
		TextView rece_count;
		TextView rece_status;
		TextView rece_in_time;
		LinearLayout finish_layout;
		TextView rece_result;
	}

}
