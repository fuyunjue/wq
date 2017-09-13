package com.cn.wq.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

import com.cn.wq.R;

public class MessageHelper {

	private HashMap<String , String> urls = new HashMap<String, String>();
	public static final String POST_URL_FileUpload = "POST_URL_FileUpload";
	public static final String POST_URL_USER = "POST_URL_USER";
	public static final String POST_URL_Purchase = "POST_URL_Purchase";
	public static final String POST_URL_Synchronous_By_createdate = "POST_URL_Synchronous_By_createdate";
	public static final String POST_URL_Synchronous = "POST_URL_Synchronous";
	

	public MessageHelper(Context context) {
		try {
			Properties p = new Properties();
			p.load(context.getResources().openRawResource(R.raw.system));
			urls.put(POST_URL_USER, p.getProperty(POST_URL_USER));
			urls.put(POST_URL_FileUpload, p.getProperty(POST_URL_FileUpload));
			urls.put(POST_URL_Purchase, p.getProperty(POST_URL_Purchase));
			urls.put(POST_URL_Synchronous, p.getProperty(POST_URL_Synchronous));
			urls.put(POST_URL_Synchronous_By_createdate, p.getProperty(POST_URL_Synchronous_By_createdate));
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Http方式请求接口
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public String sendPost(String json ,String key) {
		try {
			HttpURLConnection httpcon = (HttpURLConnection) ((new URL(urls.get(key)).openConnection()));
			httpcon.setDoOutput(true);
			httpcon.setRequestProperty("Content-Type", "application/json");
			httpcon.setRequestProperty("Accept", "application/json");
			httpcon.setRequestProperty("contentType", "utf-8");  
			httpcon.setRequestMethod("POST");
			httpcon.connect();

			byte[] outputBytes = json.getBytes("UTF-8");
			OutputStream os = httpcon.getOutputStream();
			os.write(outputBytes);
			os.close();

			int status = httpcon.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpcon.getInputStream() ,"utf-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			br.close();
			
			System.out.println(sb.toString());
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
//	/**
//	 * 
//	 * @param url
//	 * @return
//	 */
//	public String sendPost(String url) {
//		try {
//			HttpURLConnection httpcon = (HttpURLConnection) ((new URL(url).openConnection()));
//			httpcon.setDoOutput(true);
//			httpcon.setRequestProperty("Content-Type", "application/json");
//			httpcon.setRequestProperty("Accept", "application/json");
//			httpcon.setRequestProperty("contentType", "utf-8");  
//			httpcon.setConnectTimeout(3 * 1000);  
//			httpcon.setRequestMethod("POST");
//			httpcon.connect();
//
//			int status = httpcon.getResponseCode();
//			if (status != 200) {
//				throw new IOException("Post failed with error code " + status);
//			}
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//					httpcon.getInputStream() ,"utf-8"));
//			StringBuilder sb = new StringBuilder();
//			String line;
//			while ((line = br.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			br.close();
//			Log.e("xxxx" ,""+sb.toString());
//			return sb.toString();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	

	/**
	 * @return the urls
	 */
	public HashMap<String, String> getUrls() {
		return urls;
	}

	
	
//	/**
//	 * 调用webservice
//	 * 
//	 * @param json
//	 * @return
//	 */
//	public String sendMsg(String json) {
//		try {
//			SoapObject rpc = new SoapObject(NAMESPACE, METHOD_NAME);
//			rpc.addProperty("arg0", json);
//
//			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//					SoapEnvelope.VER11);
//			envelope.dotNet = false;
//			envelope.encodingStyle = "UTF-8";
//			envelope.setOutputSoapObject(rpc);
//			new MarshalBase64().register(envelope);
//			HttpTransportSE aht = new HttpTransportSE(URL, 60 * 1000);
//
//			aht.call(SOAP_ACTION, envelope);
//			Object result = (Object) envelope.getResponse();
//			Log.d(TAG, result.toString());
//			return String.valueOf(result);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

}
