package cn.jitmarketing.hot.adapter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.MenuBean;
import cn.jitmarketing.hot.entity.MenuConfigBean;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.Des;


public class MenuGridAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private AbsListView.LayoutParams lp;
	private List<MenuBean> menuList;
	private Map<String, MenuConfigBean> map;
	
	public MenuGridAdapter(Activity activity, List<MenuBean> menuList){
		this.menuList = menuList;
		inflater = LayoutInflater.from(activity);
		lp = dynamicSetting(activity);
		HotApplication app = (HotApplication)activity.getApplication();
		map = app.getConfigMap();
	}
	
	@Override
	public int getCount() {
		return menuList.size();
	}

	@Override
	public Object getItem(int position) {
		return menuList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.menu_item_layout, null);  
			/*根据parent动态设置convertview的大小*/  
	        convertView.setLayoutParams(lp);// 动态设置item的高度 
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon_iv);
			holder.nametxt = (TextView) convertView.findViewById(R.id.text_menu);
			holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.menu_layout);
			holder.menu_num = (TextView) convertView.findViewById(R.id.menu_num);
			holder.menu_num_layout = (RelativeLayout) convertView.findViewById(R.id.menu_num_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			/*解决动态设置convertview大小，第一项不显示的BUG*/  
	        convertView.setLayoutParams(lp);// 动态设置item的高度 
		}
		MenuBean item = menuList.get(position);
		if(item.menuName != null){
			holder.nametxt.setText(item.menuName);
		}else
			holder.nametxt.setText(item.menuCode);
		if(map.get(menuList.get(position).menuCode) == null) {
			holder.icon.setBackgroundResource(R.drawable.icon1);
//			holder.contentLayout.setBackgroundResource(R.drawable.icon1);
		} else {
			holder.icon.setBackgroundResource(map.get(menuList.get(position).menuCode).getIconDrawableID());
//			holder.contentLayout.setBackgroundResource(map.get(menuList.get(position).menuCode).getBgDrawableID());
		}
		if(item.num == 0) {
			holder.menu_num_layout.setVisibility(View.GONE);
		} else {
			holder.menu_num_layout.setVisibility(View.VISIBLE);
		}
		if(item.num < 100)
			holder.menu_num.setText(String.valueOf(item.num));
		else
			holder.menu_num.setText("99+");
		return convertView;
	}
	
	class ViewHolder{
		public ImageView icon;
		public TextView nametxt;
		public LinearLayout contentLayout;
		public TextView menu_num;
		private RelativeLayout menu_num_layout;
	}
	
	/**
	 * 动态配置每个item的高度和宽度
	 */
	private AbsListView.LayoutParams dynamicSetting(Context context) {
		int px = Des.dip2px(context, 10);//10dp转换成px 10dp根据xml中的水平间隔的值而来
		int width = ConstValue.SCREEN_WIDTH / 3 - px;//屏幕三等分， 去掉中间间隔（10dp）
		int height = ConstValue.SCREEN_WIDTH / 3 - px - 12;//屏幕三等分， 去掉中间间隔（10dp）
		return new AbsListView.LayoutParams(width, height);
	}
}
