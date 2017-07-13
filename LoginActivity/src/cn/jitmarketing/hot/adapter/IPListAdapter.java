package cn.jitmarketing.hot.adapter;

import java.util.List;

import com.ex.lib.core.utils.mgr.MgrPerference;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import cn.jitmarketing.hot.LoginActivity;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.HotConstants.Global;

public class IPListAdapter extends BaseAdapter {

	private Activity activity;
	private List<String> ipList;
	private LayoutInflater inflater;    

	public IPListAdapter(Activity activity, List<String> ipList) {
		this.activity = activity;
		this.ipList = ipList;
		inflater = LayoutInflater.from(activity);
	}


	@Override
	public int getCount() {
		return ipList.size();
	}

	@Override
	public Object getItem(int position) {

		return ipList.get(position);
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
			convertView = inflater.inflate(R.layout.ip_item_layout, null);  
			holder = new ViewHolder();
			holder.ipTextView = (TextView) convertView
					.findViewById(R.id.ip_txt);
			holder.ipNameTextView = (TextView) convertView
					.findViewById(R.id.ip_name);
			holder.deleteButton = (Button) convertView
					.findViewById(R.id.ip_delete_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(position == 0) {
			holder.deleteButton.setVisibility(View.GONE);
		} else {
			holder.deleteButton.setVisibility(View.VISIBLE);
		}
		holder.ipTextView.setText(ipList.get(position).split(LoginActivity.SPLIT_MARK)[0]);
		holder.ipNameTextView.setText(ipList.get(position).split(LoginActivity.SPLIT_MARK)[1]);
		holder.deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("ffffff", ipList.get(position).split(LoginActivity.SPLIT_MARK)[0]);
				Log.i("ggggggg", MgrPerference.getInstance(activity).getString(LoginActivity.COMMON_IP));
				if(ipList.get(position).split(LoginActivity.SPLIT_MARK)[0].equals(MgrPerference.getInstance(activity).getString(LoginActivity.COMMON_IP))) {
					MgrPerference.getInstance(activity).putString(LoginActivity.COMMON_IP, Global.DEFAULT_IP);
					MgrPerference.getInstance(activity).putString(LoginActivity.COMMON_IP_NAME, "默认接入点");
//					activity.loginTextView.setText(Global.DEFAULT_IP);
					((LoginActivity)activity).loginTextView.setText("默认接入点");

				}
				ipList.remove(position);
				StringBuffer ipSts = new StringBuffer();
				if(ipList.size() > 1) {
					for(int i=1; i<ipList.size(); i++) {
						ipSts.append(ipList.get(i));
						if(i != ipList.size()-1)
							ipSts.append(";");
					}
				}
				MgrPerference.getInstance(activity).putString(LoginActivity.IP_STR, ipSts.toString());
				notifyDataSetChanged();
//				activity.setListViewHeightBasedOnChildren(activity.loginListView);
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView ipTextView;
		TextView ipNameTextView;
		Button deleteButton;
	}

}
