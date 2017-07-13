package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.MyViewPageAdapter;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.ExBaseFragment;
import com.ex.lib.ext.xutils.annotation.ViewInject;

public class MyViewPage extends BaseSwipeBackAcvitiy implements OnClickListener{
	TabHost tabhost;
	TabWidget tabwidget;
	 LinearLayout title;
	@Override
	protected int exInitLayout() {
		return R.layout.fragement;
	}

	@Override
	protected void exInitBundle() {
	}

	@Override
	protected void exInitView() {
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabhost.setup();
		tabwidget = tabhost.getTabWidget();
		createTab("看差异",R.id.fragment1);
		createTab1("看记录",R.id.fragment2);
	}

	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}
	public void createTab(String text,int s){
		tabhost.addTab(tabhost.newTabSpec(text).setIndicator(createTabView(text)).setContent(s));
	}
	public void createTab1(String text,int s){
		tabhost.addTab(tabhost.newTabSpec(text).setIndicator(createTabView1(text)).setContent(s));
	}
	 private View createTabView(String text) {
         View view = LayoutInflater.from(this).inflate(R.layout.tab_indicatior, null);
         title=(LinearLayout) view.findViewById(R.id.title);
     	 title.setOnClickListener(this);;
         TextView tv = (TextView) view.findViewById(R.id.tab_tv);
         tv.setText(text);
         return view;
 }
	 private View createTabView1(String text) {
         View view = LayoutInflater.from(this).inflate(R.layout.tab_indicatior_right, null);
         TextView tv = (TextView) view.findViewById(R.id.tab_tv1);
         tv.setText(text);
         return view;
 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.title:
			title.setBackgroundResource(R.drawable.bg_topbar);
			this.finish();
			break;
		}
	}
}
