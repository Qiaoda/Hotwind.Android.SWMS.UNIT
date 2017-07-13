package cn.jitmarketing.hot.adapter;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CGYScanSkuAdapter.ViewHolder;
import cn.jitmarketing.hot.entity.CGYBean;
import cn.jitmarketing.hot.entity.DifferentBean;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DifferentAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<DifferentBean> dlist;
    public DifferentAdapter (LayoutInflater inflater, List<DifferentBean> dlist){
    	this.inflater = inflater;
    	this.dlist = dlist;
    }
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dlist.size();
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
			convertView = inflater.inflate(R.layout.stock_different, null);
			holder=new ViewHolder();
			holder.different_tv=(TextView) convertView.findViewById(R.id.different_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		String locationStr=dlist.get(position).ShelfLocationCode;
		String s=locationStr.substring(0,1);
		String ss=locationStr.substring(1,3);
		String sss=locationStr.substring(3,4);
		String ssss=locationStr.substring(4,5);
		String endStr=s+"-"+ss+"-"+sss+"-"+ssss;
		holder.different_tv.setText(endStr);
		return convertView;
	}
   class ViewHolder{
	   TextView different_tv;
   }
}
