package cn.jitmarketing.hot.adapter;

import java.util.ArrayList;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.DoWeBean;
import cn.jitmarketing.hot.view.MyListView;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShelfDetailAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<DoWeBean> wareList;
	private Activity activity;

	public ShelfDetailAdapter(Activity activity, LayoutInflater inflater,
			ArrayList<DoWeBean> wareList) {
		this.inflater = inflater;
		this.wareList = wareList;
		this.activity = activity;
	}

	public ArrayList<DoWeBean> getWareList() {
		return wareList;
	}

	public void setWareList(ArrayList<DoWeBean> wareList) {
		this.wareList = wareList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wareList.size();
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
		final ViewHolder holder;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.shelf_detail, null);
			holder=new ViewHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.tv);
			holder.lv = (MyListView) convertView.findViewById(R.id.lv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}	
		holder.tv.setText(wareList.get(position).ShelfLocation);
		ForAdapter adapter = new ForAdapter(activity, inflater, wareList.get(position).lastList, this, wareList, position);
		holder.lv.setAdapter(adapter);
		return convertView;
	}
	 class ViewHolder{
		 public TextView tv; 
		 public MyListView lv;
	 }
	 
}
