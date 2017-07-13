package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter.ViewHolder;
import cn.jitmarketing.hot.entity.CGYBean;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CGYStockAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<CGYBean> cgyList;

	public CGYStockAdapter(LayoutInflater inflater, List<CGYBean> cgyList) {
		this.inflater = inflater;
		this.cgyList = cgyList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cgyList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder=null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.cgy_stock, null);
			holder=new ViewHolder();
			holder.cgy_location = (TextView) convertView.findViewById(R.id.cgy_location);
			holder.cgy_status = (TextView) convertView.findViewById(R.id.cgy_status);
			holder.cgy_date = (TextView) convertView.findViewById(R.id.cgy_date);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.cgy_location.setText("库位:" + cgyList.get(position).ShelfLocationCode);
		holder.cgy_date.setText(cgyList.get(position).CheckTime);
		if (cgyList.get(position).status.equals("2")) {
			holder.cgy_status.setText("已盘点");
		} else {
			holder.cgy_status.setText("");
		}
		return convertView;
	}
	
	class ViewHolder{
		TextView cgy_location;
		TextView cgy_status;
		TextView cgy_date;
	}
}
