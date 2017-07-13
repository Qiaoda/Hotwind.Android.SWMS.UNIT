package cn.jitmarketing.hot;

import java.util.ArrayList;

import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.service.GroupEntity;
import cn.jitmarketing.hot.service.NoticeService;
import cn.jitmarketing.hot.view.TitleWidget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

public class NoticeSettingActivity extends BaseSwipeBackAcvitiy {
	
	/**
	 * 标题
	 */
	@ViewInject(R.id.notice_setting_title)
	TitleWidget titleLayout;
	@ViewInject(R.id.expand_listview)
	ExpandableListView expandableListView;
	
	private ArrayList<GroupEntity> groupList;
	private String role;
	private Intent serviceIntent;
	
	@Override
	protected int exInitLayout() {
		return R.layout.activity_notice_setting;
	}
	
	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			startService(serviceIntent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};
	
	@Override
	protected void exInitView() {
		serviceIntent = new Intent(NoticeSettingActivity.this, NoticeService.class);
		titleLayout.setOnLeftClickListner(clickListener);
		titleLayout.setOnRightClickListner(clickListener);
		titleLayout.setText("通知设置");
		role = MgrPerference.getInstance(this).getString(HotConstants.Unit.ROLE_CODE);
		groupList = (ArrayList<GroupEntity>)MgrPerference.getInstance(this).getObject(role);
		expandableListView.setAdapter(new MyExpandableListViewAdapter(this));
		expandableListView.setGroupIndicator(this.getResources().getDrawable(
				R.color.nulls));
		int groupCount = expandableListView.getCount();
		for (int i = 0; i < groupCount; i++) {
			expandableListView.expandGroup(i);
		}// ---默认展开
		expandableListView.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});//禁止收缩
	}
	
	private OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.lv_left) {
				startService(serviceIntent);
				finish();
			}
		}
	};
	
	//用过ListView的人一定很熟悉，只不过这里是BaseExpandableListAdapter
	class MyExpandableListViewAdapter extends BaseExpandableListAdapter {

		private Context context;

		public MyExpandableListViewAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupList.get(groupPosition).getSublist().size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return groupList.get(groupPosition).getSublist().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupHolder groupHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.expendlist_group, null);
				groupHolder = new GroupHolder();
				groupHolder.parent_txt = (TextView) convertView
						.findViewById(R.id.parent_txt);
				convertView.setTag(groupHolder);
			} else {
				groupHolder = (GroupHolder) convertView.getTag();
			}
			groupHolder.parent_txt.setText(groupList.get(groupPosition).getGroupName());
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition, final int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ItemHolder itemHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.expendlist_item, null);
				itemHolder = new ItemHolder();
				itemHolder.child_txt = (TextView) convertView
						.findViewById(R.id.child_txt);
				itemHolder.switch_button = (CheckBox) convertView
						.findViewById(R.id.switch_button);
				convertView.setTag(itemHolder);
			} else {
				itemHolder = (ItemHolder) convertView.getTag();
			}
			final int childPos = childPosition;
			itemHolder.switch_button.setOnCheckedChangeListener(null); 
			itemHolder.child_txt.setText(groupList.get(groupPosition).getSublist().get(childPosition).getName());
			itemHolder.switch_button.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					Log.i("isChecked", isChecked+"");
					groupList.get(groupPosition).getSublist().get(childPos).setOpen(isChecked);
					MgrPerference.getInstance(NoticeSettingActivity.this).putObject(role, groupList);
				}
			});
			itemHolder.switch_button.setChecked(groupList.get(groupPosition).getSublist().get(childPos).getOpen());
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
	 
	 class GroupHolder {
		 public TextView parent_txt;
	 }
	 
	 class ItemHolder {
		 public TextView child_txt;
		 public CheckBox switch_button;
	 }
	
}
