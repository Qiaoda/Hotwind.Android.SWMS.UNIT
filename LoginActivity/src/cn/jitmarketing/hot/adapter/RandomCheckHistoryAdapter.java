package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.RandomCheckHistoryBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RandomCheckHistoryAdapter extends BaseAdapter{
	
	private Context context;
	private List<RandomCheckHistoryBean> list;

	public RandomCheckHistoryAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list==null?0:list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView==null) {
			viewHolder=new ViewHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_random_history, null);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	
	
	private static class ViewHolder{
		private TextView randomCode;
		private TextView randomTime;
	}

}
