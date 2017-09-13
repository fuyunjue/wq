package com.cn.wq.keeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 
 * @Title:  UserKeeper.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-21 下午11:27:33 
 * @version:  V1.0 
 *
 */
public class UserKeeper {
	private static final String PREFERENCES_NAME = "wq_user_info";
	public static final String USER_ID = "id";
	public static final String USER_NAME = "name";
	public static final String USER_PHONE = "phone";
	public static final String USER_SYS = "sys";
	public static final String USER_PASSWORD = "password";
	public static final String CURRENCY = "currency";
	public static final String Model_z = "Model_z";	//主分類
	public static final String Model_c = "Model_c";	//次分類
	public static final String harvest_time = "harvest_time";	//預計收貨日期

	/**
	 * 清空sharepreference
	 * 
	 * @param context
	 */
	public static void clear(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * 根据key保存信息
	 * 
	 * @param key
	 * @param value
	 */
	public static void SaveSharepreferenceByKey(Context context, String key, String value) {
		SharedPreferences preference = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor = preference.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * 根据key移除信息
	 * 
	 * @param key
	 * @param value
	 */
	public static void removeSharepreferenceByKey(Context context, String key) {
		SharedPreferences preference = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preference.edit();
		editor = preference.edit();
		editor.remove(key);
		editor.commit();
	}


	/**
	 * 根据key获取信息
	 * 
	 * @param context
	 * @param key
	 *            名称
	 * @return 键对应的值，如果找不到对应的值， 则返回null
	 */
	public static String getStringValue(final Context context, final String key) {
		SharedPreferences preference = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
		return preference.getString(key, null);
	}
}
