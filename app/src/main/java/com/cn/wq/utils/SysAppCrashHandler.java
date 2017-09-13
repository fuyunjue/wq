package com.cn.wq.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

/**
 * 自定义的 异常处理类 , 实现了 UncaughtExceptionHandler接口
 * 
 * @author Administrator
 * 
 */
public class SysAppCrashHandler implements UncaughtExceptionHandler {
	private static SysAppCrashHandler myCrashHandler;
	private Context context;
//	public static String configURL = "http://14.18.205.235:2096/";
	private SimpleDateFormat dataFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// 1.私有化构造方法
	private SysAppCrashHandler() {

	}

	public static synchronized SysAppCrashHandler getInstance() {
		if (myCrashHandler != null) {
			return myCrashHandler;
		} else {
			myCrashHandler = new SysAppCrashHandler();
			return myCrashHandler;
		}
	}

	public void init(Context context) {// , DoubanService service){
		this.context = context;
		// this.service = service;
	}

	public void uncaughtException(Thread arg0, Throwable arg1) {
		try {
			// System.out.println("程序挂掉了 ");
			final String errorinfo = getErrorInfo(arg1);
			saveLog(dataFormat.format(new Date()) + "\n" + errorinfo);

			// Intent intent = new Intent(getInstance().context,
			// LoginActivity.class);
			// getInstance().context.startActivity(intent);
			
			new Thread(){
	    		@Override
	    		public void run(){
	    			//汇报错误到服务器
//	    			GetPostStream(configURL+"data.ashx?act=exceptionLog",errorinfo);
	    			// 干掉当前的程序
	    			android.os.Process.killProcess(android.os.Process.myPid());
	    		}
			}.start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static InputStream GetPostStream(String Url,String postStr){
		Log.e("GetPostStream", "get stream url:"+Url);
		
		try{
			byte[] post=postStr.getBytes();//需要发送的数据
			URL url=new URL(Url);
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(20*1000);
			conn.setDoOutput(true);

			//设置HTTP请求的头字段
			conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8"); //内容类型
			conn.setRequestProperty("Content-Length", String.valueOf(post.length));  //实体内容的长度

			conn.getOutputStream().write(post);//通过输出流把数据写到服务器
			if(conn.getResponseCode()==200){
				InputStream inStream = conn.getInputStream();
				return inStream;
			}else{
				Log.e("GetPostStream", "GetPostStream is code:"+conn.getResponseCode());
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			Log.e("GetPostStream", "GetPostStream is error:"+e.getMessage());
		}
		
		return null;
	}
	
	public static void saveLog(String content) throws Exception {
		saveToSDCard("wqSystem.log", content);
	}

	public static void saveToSDCard(String filename, String content) throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(), filename);
		FileWriter fr = null;
        try {
            //Below constructor argument decides whether to append or override
            fr = new FileWriter(file,true);
            fr.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		
//		FileOutputStream outStream = new FileOutputStream(file, true);
//		byte[] bytes = content.getBytes();
//		outStream.write(bytes, (int) file.length(), bytes.length);
//		outStream.close();
	}

	/**
	 * 获取错误的信息
	 * 
	 * @param arg1
	 * @return
	 */
	private String getErrorInfo(Throwable arg1) {
		StringWriter writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		return error;
	}

	/**
	 * 获取手机的硬件信息
	 * 
	 * @return
	 */
	private String getMobileInfo() {
		StringBuffer sb = new StringBuffer();
		// 通过反射获取系统的硬件信息
		try {
			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				// 暴力反射 ,获取私有的信息
				field.setAccessible(true);
				String name = field.getName();
				String value = field.get(null).toString();
				sb.append(name + "=" + value);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 获取手机的版本信息
	 * 
	 * @return
	 */
	private String getVersionInfo() {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}
}