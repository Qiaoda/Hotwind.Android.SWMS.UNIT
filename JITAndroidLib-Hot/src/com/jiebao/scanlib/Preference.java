package com.jiebao.scanlib;

import com.ex.lib.R;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
	private static SharedPreferences getSP(Context context) {
		return context.getSharedPreferences(
				context.getString(R.string.app_name), Context.MODE_PRIVATE);
	}

	public static boolean isScreenOn(Context context) {
		return getSP(context).getBoolean("IsScreenOn", true);
	}

	public static void setScreenOn(Context context, boolean value) {
		getSP(context).edit().putBoolean("IsScreenOn", value).commit();
	}

	public static void setSe955(Context context, boolean se955) {
		getSP(context).edit().putBoolean("IsSE955", se955).commit();
	}

	public static boolean isSe955(Context context) {
		return getSP(context).getBoolean("IsSE955", false);
	}
}
