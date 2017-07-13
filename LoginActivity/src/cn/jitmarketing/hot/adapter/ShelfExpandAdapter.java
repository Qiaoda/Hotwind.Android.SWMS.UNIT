package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;

public class ShelfExpandAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<ShelfBean> shelfList;
	public ShelfExpandAdapter(Context mContext, List<ShelfBean> shelfList){
		this.mContext = mContext;
		this.shelfList = shelfList;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return shelfList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return shelfList.get(groupPosition).skuList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return shelfList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return shelfList.get(groupPosition).skuList.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(0);
		TextView text = new TextView(mContext);
		text.setTextSize(20);
		text.setPadding(8, 10, 0, 10);
		
		text.setText("库位码：" + ((ShelfBean)getGroup(groupPosition)).shelfCode);
		layout.addView(text);
		return layout;

	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(0);
		TextView skuCode = new TextView(mContext);
		skuCode.setTextSize(15);
		skuCode.setPadding(15, 5, 0, 5);
		skuCode.setText(((SkuBean)getChild(groupPosition, childPosition)).skuCode);
		TextView skuCount = new TextView(mContext);
		skuCount.setTextSize(15);
		skuCount.setPadding(35, 5, 0, 5);
		skuCount.setText("数量：" + ((SkuBean)getChild(groupPosition, childPosition)).skuCount);
		layout.addView(skuCode);
		layout.addView(skuCount);
		return layout;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
