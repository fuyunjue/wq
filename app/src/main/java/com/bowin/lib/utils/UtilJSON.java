package com.bowin.lib.utils;

import org.json.JSONObject;

public class UtilJSON {
	
	/**
	 * 
	 * 
	 * @param object
	 * @param key
	 * @param local
	 * @return
	 */
	public static int getIntFromJSONObject(JSONObject object ,String key ,String local ,int defaultResult) {
		try {
			if(object==null) return defaultResult;
			if(object.has(key) && (object.get(key)+"").toUpperCase().indexOf("NULL")<0) {
				return object.getInt(key);
			} else {
				return defaultResult;
			}
		} catch (Exception e) {
			System.out.println(local + " getIntFromJSONObject" + e.getMessage());
			e.printStackTrace();
		}
		return defaultResult;
	}

	/**
	 * 
	 * 
	 * @param object
	 * @param key
	 * @param local
	 * @return
	 */
	public static String getStringFromJSONObject(JSONObject object ,String key ,String local ,String defaultResult) {
		try {
			if(object==null) return defaultResult;
			if(object.has(key) && (object.get(key)+"").toUpperCase().indexOf("NULL")<0) {
				return object.get(key).toString();
			} else {
				return defaultResult;
			}
		} catch (Exception e) {
			System.out.println(local + " getStringFromJSONObject" + e.getMessage());
			e.printStackTrace();
		}
		return defaultResult;
	}
	

	/**
	 * 
	 * 
	 * @param object
	 * @param key
	 * @param local
	 * @return
	 */
	public static long getLongFromJSONObject(JSONObject object ,String key ,String local ,long defaultResult) {
		try {
			if(object==null) return defaultResult;
			if(object.has(key) && (object.get(key)+"").toUpperCase().indexOf("NULL")<0) {
				return object.getLong(key);
			} else {
				return defaultResult;
			}
		} catch (Exception e) {
			System.out.println(local + " getLongFromJSONObject" + e.getMessage());
			e.printStackTrace();
		}
		return defaultResult;
	}
	
	/**
	 * 
	 * 
	 * @param object
	 * @param key
	 * @param local
	 * @return
	 */
	public static double getDoubleFromJSONObject(JSONObject object ,String key ,String local ,double defaultResult) {
		try {
			if(object==null) return defaultResult;
			if(object.has(key) && (object.get(key)+"").toUpperCase().indexOf("NULL")<0) {
				return object.getDouble(key);
			} else {
				return defaultResult;
			}
		} catch (Exception e) {
			System.out.println(local + " getDoubleFromJSONObject" + e.getMessage());
			e.printStackTrace();
		}
		return defaultResult;
	}
}
