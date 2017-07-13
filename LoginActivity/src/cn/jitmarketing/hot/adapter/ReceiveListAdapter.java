package cn.jitmarketing.hot.adapter;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ReceiveSheet;

public class ReceiveListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<ReceiveSheet> receList;
	private Activity activity;
	int selectItem;

	public ReceiveListAdapter(Activity activity, LayoutInflater inflater, List<ReceiveSheet> receList) {
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.receive_item_layout, null);
			holder = new ViewHolder();
			holder.self_sku_layout = (LinearLayout) convertView.findViewById(R.id.self_sku_layout);
			holder.rece_sku_code = (TextView) convertView.findViewById(R.id.rece_sku_code);
			holder.sku_code = (TextView) convertView.findViewById(R.id.sku_code);
			holder.souce_txt = (TextView) convertView.findViewById(R.id.souce_txt);
			holder.rece_count = (TextView) convertView.findViewById(R.id.rece_count);
			holder.rece_status = (TextView) convertView.findViewById(R.id.rece_status);
			holder.rece_in_time = (TextView) convertView.findViewById(R.id.rece_in_time);
			holder.finish_layout = (LinearLayout) convertView.findViewById(R.id.finish_layout);
			holder.rece_result = (TextView) convertView.findViewById(R.id.rece_result);
			holder.from_remark = (TextView) convertView.findViewById(R.id.from_remark);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ReceiveSheet item = receList.get(position);
		holder.rece_sku_code.setText(item.DisOrderNo);
		holder.sku_code.setText(item.receiveSheetNo);
		holder.from_remark.setText("备注：" + item.FromRemark.trim());
		holder.rece_count.setText(Html.fromHtml("本批次共<font color='red'>" + (int) item.skuQty + "</font>件"));
		if (item.status == 1) {
			holder.rece_status.setText("未入库");
			holder.rece_status.setTextColor(activity.getResources().getColor(R.color.bbb));
			// holder.rece_status.setVisibility(View.VISIBLE);
			holder.finish_layout.setVisibility(View.VISIBLE);
			// holder.rece_result.setVisibility(View.GONE);
			// holder.rece_in_time.setText(item.createTime);
		} else if (item.status == 4) {
			// holder.rece_status.setText("已完成");
			holder.rece_status.setTextColor(activity.getResources().getColor(R.color.color_green));
			if (item.ResultStatus == 2) {
				holder.rece_status.setText("有差异");
				holder.rece_status.setTextColor(activity.getResources().getColor(R.color.bbb));
			} else {
				holder.rece_status.setText("无差异");
				holder.rece_status.setTextColor(activity.getResources().getColor(R.color.color_green));
			}
			// holder.rece_result.setVisibility(View.VISIBLE);
			// holder.rece_status.setVisibility(View.VISIBLE);
			holder.finish_layout.setVisibility(View.VISIBLE);
			// if (item.updateTime.split("T").length == 1) {
			// holder.rece_in_time.setText(item.updateTime);
			// } else if (item.updateTime.split("T").length == 2) {
			// holder.rece_in_time.setText(item.updateTime.split("T")[0]
			// + " " + item.updateTime.split("T")[1]);
			// }
		} else if (item.status == 3) {
			holder.rece_status.setText("暂存中");
			holder.rece_status.setTextColor(activity.getResources().getColor(R.color.aaa));
			// holder.rece_status.setVisibility(View.VISIBLE);
			holder.finish_layout.setVisibility(View.GONE);
		} else {
			holder.finish_layout.setVisibility(View.VISIBLE);
		}
		holder.rece_in_time.setText(item.createTime);
		holder.souce_txt.setText(item.FromStoreName);
		if (position == selectItem) {
			holder.self_sku_layout.setBackgroundResource(R.drawable.shadow_red_bg);
		} else {
			holder.self_sku_layout.setBackgroundResource(R.drawable.shadow_bg);
		}

		return convertView;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	class ViewHolder {
		TextView rece_sku_code;
		TextView rece_count;
		TextView rece_status;
		TextView rece_in_time;
		LinearLayout finish_layout;
		TextView rece_result;
		TextView souce_txt;
		TextView sku_code;
		TextView from_remark;
		LinearLayout self_sku_layout;
	}

}
