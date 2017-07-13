package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.SampleEntity;

public class SomeSampleAdapter extends BaseAdapter {

	private Activity mActivity;
	private List<SampleEntity> sampleList;
	private int selectedPosition = -1;
	
	public SomeSampleAdapter(Activity mActivity, List<SampleEntity> sampleList){
		this.mActivity = mActivity;
		this.sampleList = sampleList;
	}
	
	public void setSelected(int selectedPosition){
		this.selectedPosition = selectedPosition;
	}
	
	@Override
	public int getCount() {
		return sampleList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return sampleList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mActivity.getLayoutInflater().inflate(R.layout.some_sample_item, null);  
			holder = new ViewHolder();
			holder.some_sample_text = (TextView) convertView
					.findViewById(R.id.some_sample_text);
			holder.some_sample_time_text = (TextView) convertView
					.findViewById(R.id.some_sample_time_text);
			holder.some_sample_image = (ImageView) convertView
					.findViewById(R.id.some_sample_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.some_sample_text.setText(sampleList.get(position).Label);
		if(sampleList.get(position).SampleTime != null && !sampleList.get(position).Type.equals("1"))
			holder.some_sample_time_text.setText("已出样" + sampleList.get(position).SampleTime);
		else
			holder.some_sample_time_text.setText(sampleList.get(position).SampleTime);
		Bitmap bitmap = null;
		if(position == selectedPosition){ 
			bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.radiobutton_on_background);   
		}else{
			bitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.radiobutton_off_background);   
		}
		holder.some_sample_image.setImageBitmap(bitmap);
		return convertView;
	}
	
	public class ViewHolder {
		public TextView some_sample_text;
		public ImageView some_sample_image;
		public TextView some_sample_time_text;
	}

}
