package cn.jitmarketing.hot.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyViewPageAdapter extends FragmentPagerAdapter {
	private List<Fragment> mFragments;
	public MyViewPageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		mFragments=fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFragments.size();  
	}

}
