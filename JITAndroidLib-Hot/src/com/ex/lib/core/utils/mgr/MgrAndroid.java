/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

/**
 * @ClassName: MgrAndroid
 * @Description: MgrAndroid 管理类
 * @author Aloneter
 * @date 2014-10-24 下午5:52:58
 * @Version 1.0
 * 
 */
public class MgrAndroid {

	private static Context mContext;

	/**
	 * 创建者
	 */
	private static class AndroidHolder {

		private static final MgrAndroid mgr = new MgrAndroid();
	}

	/**
	 * 获取当前实例
	 * 
	 * @param context
	 * @return
	 */
	public static MgrAndroid getInstance(Context context) {

		mContext = context;

		return AndroidHolder.mgr;
	}

	/**
	 * Method_通过名称和类型获取 ID
	 * 
	 * @param name_名称
	 * @param defType_类型
	 * @return 资源 ID
	 */
	private int getIdentifier(String name, String defType) {

		int result = 0;

		String skinPkgName = mContext.getPackageName();

		try {
			result = mContext.createPackageContext(skinPkgName, Context.CONTEXT_IGNORE_SECURITY).getResources().getIdentifier(name, defType, skinPkgName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Method_改变本地语言
	 * 
	 * @param lanAtr_目标语言字段
	 * @return 更改结果
	 */
	public boolean changeLanguage(String lanAtr) {

		try {
			Resources resources = mContext.getResources();
			Configuration config = resources.getConfiguration();
			DisplayMetrics dm = resources.getDisplayMetrics();

			if (lanAtr.equals("en")) {
				config.locale = Locale.ENGLISH;
			} else {
				config.locale = Locale.SIMPLIFIED_CHINESE;
			}

			resources.updateConfiguration(config, dm);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Method_获取 View 辅助对象
	 * 
	 * @param view_视图对象
	 * @param id_编号管理
	 * @return 对象
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getViewHolder(View view, int id) {

		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();

		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}

		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}

		return (T) childView;
	}

	/**
	 * Method_Resources
	 * 
	 * @return 对象
	 */
	public Resources resources() {

		return mContext.getResources();
	}

	/**
	 * Method_stirng
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int string(String name) {

		return getIdentifier(name, "string");
	}

	public String string(int id) {

		return resources().getString(id);
	}

	/**
	 * Method_color
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int color(String name) {

		return getIdentifier(name, "color");
	}

	/**
	 * Method_anim
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int anim(String name) {

		return getIdentifier(name, "anim");
	}

	/**
	 * Method_attr
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int attr(String name) {

		return getIdentifier(name, "attr");
	}

	/**
	 * Method_dimen
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int dimen(String name) {

		return getIdentifier(name, "dimen");
	}

	/**
	 * Method_drawable
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int drawable(String name) {

		return getIdentifier(name, "drawable");
	}

	/**
	 * Method_id
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int id(String name) {

		return getIdentifier(name, "id");
	}

	/**
	 * Method_layout
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int layout(String name) {

		return getIdentifier(name, "layout");
	}

	/**
	 * Method_menu
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int menu(String name) {

		return getIdentifier(name, "menu");
	}

	/**
	 * Method_style
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int style(String name) {

		return getIdentifier(name, "style");
	}

	/**
	 * Method_xml
	 * 
	 * @param name_资源名称
	 * @return 资源 ID
	 */
	public int xml(String name) {

		return getIdentifier(name, "xml");
	}

}
