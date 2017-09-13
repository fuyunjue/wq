/**
 * 
 */
package com.cn.wq.activty;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bowin.lib.utils.AlertDialogUtil;
import com.bowin.lib.widget.MyAlertDialog;
import com.cn.wq.R;
import com.cn.wq.entity.ModelUserInfo;
import com.cn.wq.entity.Response;
import com.cn.wq.utils.MessageHelper;
import com.cn.wq.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @Title:  ActivityLogin.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-29 下午3:01:24 
 * @version:  V1.0 
 * 
 */
public class ActivityAddUser extends Activity implements OnClickListener {

	private MyAlertDialog showAlertDialog;
	private Button button_submit;
	private EditText editText_phone;
	private EditText editText_userPassword;
	private EditText editText_name;
	private ProgressDialog progressDialog;
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_user);
		activity = this;
		
		button_submit = (Button) findViewById(R.id.button_submit);
		editText_phone = (EditText) findViewById(R.id.editText_phone);
		editText_userPassword = (EditText) findViewById(R.id.editText_userPassword);
		editText_name = (EditText) findViewById(R.id.editText_name);

		button_submit.setOnClickListener(this);
	}
	
	/**
	 * @param username
	 * @param password
	 */
	@SuppressWarnings("unchecked")
	private void loginInBackground(String phone, String userPassword ,String name) {
		progressDialog = Util.getProgressDialog(activity, true, false);
		new addTask().execute(phone ,userPassword ,name);
	}
	
	private class addTask extends AsyncTask<String, Void, String> {
		private Gson gson = new Gson();
		
		@Override
		protected String doInBackground(String... params) {
			MessageHelper helper = new MessageHelper(ActivityAddUser.this);
			ModelUserInfo userInfo = new ModelUserInfo();
			userInfo.setPhone(params[0]);
			userInfo.setPassword(params[1]);
			userInfo.setName(params[2]);
			userInfo.setAction(2);
			return helper.sendPost(gson.toJson(userInfo) ,MessageHelper.POST_URL_USER);// 使用http post
		}
		
		@Override
		protected void onPostExecute(String result) {
			final Response fromJson = gson.fromJson(result, Response.class);
			progressDialogDismiss();
			if(fromJson!=null && fromJson.getCode()==1) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity, getResources().getString(R.string.add_success), Toast.LENGTH_SHORT).show();
					}
				});
				ArrayList<ModelUserInfo> list = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelUserInfo>>() {}.getType());  
				Intent data = new Intent();
				data.putExtra("list", list);
				setResult(RESULT_OK, data);
				finish();
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						showAlertDialog(activity ,fromJson==null?getResources().getString(R.string.add_faild):(fromJson.getMsg()==null?getResources().getString(R.string.add_faild):fromJson.getMsg()), getResources().getString(R.string.sure), null, null, null);
					}
				});
			}
		}
	}

	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_submit:
			String phone = editText_phone.getText().toString();
			String userPassword = editText_userPassword.getText().toString();
			String name = editText_name.getText().toString();
			if (phone.isEmpty()) {
				showAlertDialog(activity ,getResources().getString(R.string.error_register_user_name_null), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			if (userPassword.isEmpty()) {
				showAlertDialog(activity ,getResources().getString(R.string.editText_name_hint), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			if (userPassword.isEmpty()) {
				showAlertDialog(activity ,getResources().getString(R.string.register_password), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			loginInBackground(phone, userPassword ,name);
			break;
		}
	}
	
	private void progressDialogDismiss() {
		if (progressDialog != null)
			progressDialog.dismiss();
	}
	
	/**
	 * 根据提示消息弹窗提示
	 * 
	 * @param msg
	 */
	void showAlertDialog(Context context ,final String msg ,final String yes ,final OnClickListener yesClick ,final String no ,final OnClickListener noClick) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				showAlertDialog = AlertDialogUtil.showAlertDialog(activity, getResources().getString(R.string.app_name), msg, yes, yesClick==null?new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(showAlertDialog!=null)showAlertDialog.dismiss();
					}
				}:yesClick, no, noClick);
			}
		});
	}
	
}
