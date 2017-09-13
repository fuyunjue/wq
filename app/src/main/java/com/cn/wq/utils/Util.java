/**
 * 
 */
package com.cn.wq.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.cn.wq.R;

/**
 * @Title: Util.java
 * 
 * @Description: TODO<请描述此文件是做什么的>
 * 
 * @author Terence
 * @data: 2015-12-12 下午11:30:52
 * @version: V1.0
 * 
 */
public class Util {
	
	/**
	 * 判斷wifi是否鏈接
	 * @param context
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
//		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (connectivity == null)
//			return false;
//		return connectivity.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected()) {
            return true ;
        }
        return false ;
    }
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (null != connectivity) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断是否存在网络问题
	 * 
	 * @param activity
	 * @return
	 */

	public static boolean CheckNetworkState(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (conn.getActiveNetworkInfo() == null || (!conn.getActiveNetworkInfo().isAvailable())) 
			return false;
		else 
			return true;
	}
	
	/**
	 * 获取版本号
	 * 
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 获取本机手机号码
	 * 
	 * @param activity
	 * @return
	 */
	public static String getPhoneNumber(Activity activity) {
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	/**
	 * 初始化进度条
	 * 
	 * @param activity
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static ProgressDialog getProgressDialog(Activity activity,
			boolean b1, boolean b2) {
		return ProgressDialog.show(activity,
				activity.getResources().getText(R.string.dialog_message_title),
				activity.getResources().getText(R.string.dialog_text_wait), b1,
				b2);
	}

	/**
	 * 验证手机号是否符合大陆的标准格式
	 * 
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNumberValid(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/**
	 * 判断验证码是否为 6 位纯数字，LeanCloud 统一的验证码均为 6 位纯数字。
	 * 
	 * @param smsCode
	 * @return
	 */
	public static boolean isSMSCodeValid(String smsCode) {
		String regex = "^\\d{6}$";
		return smsCode.matches(regex);
	}
}
