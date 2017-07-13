package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.PosRecordEntity;

public class PosRecordListAdapter extends BaseAdapter {

	private List<PosRecordEntity> posList;
	private LayoutInflater inflater;
	Activity activity;

	public PosRecordListAdapter(Activity activity, List<PosRecordEntity> posList){
		this.activity = activity;
		inflater = activity.getLayoutInflater();
		this.posList = posList;
	}

	@Override
	public int getCount() {
		return posList.size();
	}

	@Override
	public Object getItem(int position) {
		return posList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.pos_record_item_layout, null);
			holder = new ViewHolder();
			holder.bill_code_txt = (TextView) convertView.findViewById(R.id.bill_code_txt);
			holder.sku_code_txt = (TextView) convertView.findViewById(R.id.sku_code_txt);
			holder.num_txt = (TextView) convertView.findViewById(R.id.num_txt);
			holder.type_txt = (TextView) convertView.findViewById(R.id.type_txt);
			holder.result_txt = (TextView) convertView.findViewById(R.id.result_txt);
			holder.result_desp_txt = (TextView) convertView.findViewById(R.id.result_desp_txt);
			holder.status_txt = (TextView) convertView.findViewById(R.id.status_txt);
			holder.line = convertView.findViewById(R.id.line);
			holder.time_txt = (TextView) convertView.findViewById(R.id.time_txt);
			holder.chuli_time_txt = (TextView) convertView.findViewById(R.id.chuli_time_txt);
			convertView.setTag(holder);
		} else{ 
			holder = (ViewHolder) convertView.getTag();
		}
		PosRecordEntity entity = posList.get(position);
		holder.bill_code_txt.setText(entity.BillNo);
		holder.sku_code_txt.setText(entity.SKUCode);
		holder.num_txt.setText(String.valueOf(entity.Qry));
		holder.type_txt.setText(entity.Type);
		holder.time_txt.setText(entity.CreateTime.replace("T", " "));
		holder.result_txt.setText(entity.Result);
		holder.status_txt.setText(entity.ProcessStatus);
		holder.chuli_time_txt.setText(entity.LastUpdateTime.replace("T", " "));
		if(entity.IsOK){
			holder.result_txt.setVisibility(View.VISIBLE);
			holder.status_txt.setTextColor(Color.GREEN);
			holder.line.setVisibility(View.VISIBLE);
			holder.result_desp_txt.setVisibility(View.VISIBLE);
		} else {
			holder.result_txt.setVisibility(View.GONE);
			holder.status_txt.setTextColor(Color.RED);
			holder.line.setVisibility(View.GONE);
			holder.result_desp_txt.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	class ViewHolder{
		TextView bill_code_txt;
		TextView sku_code_txt;
		TextView num_txt;
		TextView type_txt;
		TextView result_txt;
		TextView result_desp_txt;
		TextView status_txt;
		TextView time_txt;
		TextView chuli_time_txt;
		View line;
	}

}
