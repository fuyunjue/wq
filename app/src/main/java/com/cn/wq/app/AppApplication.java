/**
 * 
 */
package com.cn.wq.app;

import android.app.Application;

import com.cn.wq.entity.ModelUserInfo;
import com.cn.wq.utils.SysAppCrashHandler;

/**
 * @Title:  AppApplication.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-30 下午2:38:34 
 * @version:  V1.0 
 * 
 */
public class AppApplication extends Application {

	private ModelUserInfo userInfo;	//当前登陆的账号
	private static AppApplication mInstance = null;
	
	public static AppApplication getInstance() {
		return mInstance;
	}
	
	@Override
	public void onCreate() {
		SysAppCrashHandler handler = SysAppCrashHandler.getInstance();
		handler.init(getApplicationContext());
		Thread.setDefaultUncaughtExceptionHandler(handler);
		super.onCreate();
		mInstance = this;
	}
	
	/**
	 * @return the userInfo
	 */
	public ModelUserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * @param userInfo the userInfo to set
	 */
	public void setUserInfo(ModelUserInfo userInfo) {
		this.userInfo = userInfo;
	}
}
