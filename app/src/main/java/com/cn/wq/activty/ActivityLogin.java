/**
 * 
 */
package com.cn.wq.activty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bowin.lib.utils.AlertDialogUtil;
import com.bowin.lib.widget.MyAlertDialog;
import com.cn.wq.R;
import com.cn.wq.app.AppApplication;
import com.cn.wq.entity.ModelUserInfo;
import com.cn.wq.entity.Response;
import com.cn.wq.keeper.UserKeeper;
import com.cn.wq.utils.MessageHelper;
import com.cn.wq.utils.Util;
import com.google.gson.Gson;

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
@SuppressLint("NewApi")
public class ActivityLogin extends Activity implements OnClickListener {

	private MyAlertDialog showAlertDialog;
	private Button loginButton;
	private EditText userPhoneEditText;
	private EditText userPasswordEditText;
	private ProgressDialog progressDialog;
	

	private CheckBox cbAutoLogin;	//自动登录
	private static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		activity = this;
		
		loginButton = (Button) findViewById(R.id.button_login);
		userPhoneEditText = (EditText) findViewById(R.id.editText_phone);
		userPasswordEditText = (EditText) findViewById(R.id.editText_userPassword);
		cbAutoLogin = (CheckBox) findViewById(R.id.cbAutoLogin);

		loginButton.setOnClickListener(this);
		
		if(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE)!=null) {
			userPhoneEditText.setText(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE)+"");
		}
		if(UserKeeper.getStringValue(activity, UserKeeper.USER_PASSWORD)!=null) {
			userPasswordEditText.setText(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE)+"");
		}
		
		if(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE) != null && UserKeeper.getStringValue(activity, UserKeeper.USER_PASSWORD) != null) {
			cbAutoLogin.setChecked(true);
			userPhoneEditText.setText(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE)+"");
			userPasswordEditText.setText(UserKeeper.getStringValue(activity, UserKeeper.USER_PASSWORD)+"");
			if(!Util.CheckNetworkState(activity)) {
				//當前為離綫模式
				Toast.makeText(activity, getResources().getString(R.string.out_line), Toast.LENGTH_SHORT).show();
				ModelUserInfo userInfo = new ModelUserInfo();
				userInfo.setOutline(true);
				userInfo.setName(UserKeeper.getStringValue(activity, UserKeeper.USER_NAME)+"");
				userInfo.setPhone(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE)+"");
				userInfo.setId(Integer.parseInt(UserKeeper.getStringValue(activity, UserKeeper.USER_ID)+""));
				userInfo.setSys(Integer.parseInt(UserKeeper.getStringValue(activity, UserKeeper.USER_SYS)+""));
				AppApplication.getInstance().setUserInfo(userInfo);
				startActivity(new Intent(getApplicationContext() ,ActivityMain.class));
				finish();
			} else {
				loginInBackground(UserKeeper.getStringValue(activity, UserKeeper.USER_PHONE) , UserKeeper.getStringValue(activity, UserKeeper.USER_PASSWORD));
			}
		}
	}
	
	/**
	 * @param username
	 * @param password
	 */
	@SuppressWarnings("unchecked")
	private void loginInBackground(String phone, String password) {
		progressDialog = Util.getProgressDialog(activity, true, false);
		new loginTask().execute(phone ,password);
	}

	private class loginTask extends AsyncTask<String, Void, String> {
		private Gson gson = new Gson();
		@Override
		protected String doInBackground(String... params) {
			MessageHelper helper = new MessageHelper(ActivityLogin.this);
			userInfo = new ModelUserInfo();
			userInfo.setPhone(params[0]);
			userInfo.setPassword(params[1]);
			userInfo.setAction(1);
			return helper.sendPost(gson.toJson(userInfo) ,MessageHelper.POST_URL_USER);// 使用http post
		}
		
		@Override
		protected void onPostExecute(String result) {
			final Response fromJson = gson.fromJson(result, Response.class);
			Message obtainMessage = mHandler.obtainMessage();
			obtainMessage.what = login_finish;
			obtainMessage.obj = fromJson;
			mHandler.sendMessage(obtainMessage);
		}
	}
	
	private static final int login_finish = 0x30;
	ModelUserInfo userInfo = null;
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case login_finish:
				progressDialogDismiss();
				if(msg.obj != null && msg.obj instanceof Response) {
					Response fromJson = (Response) msg.obj;
					if(fromJson!=null && fromJson.getCode()==1) {
						Gson gson = new Gson();
						ModelUserInfo fromJson2 = gson.fromJson(fromJson.getMsg(),ModelUserInfo.class);  
						AppApplication.getInstance().setUserInfo(fromJson2);
						//如果勾选自动登录，保存数据到本地
						if(cbAutoLogin.isChecked()) {
							UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.USER_PHONE, userInfo.getPhone());
							UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.USER_NAME, userInfo.getName());
							UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.USER_PASSWORD, userInfo.getPassword());
							UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.USER_ID, userInfo.getId()+"");
							UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.USER_SYS, userInfo.getSys()+"");
						} else {
							//清除用户
							UserKeeper.clear(activity);
						}
						startActivity(new Intent(getApplicationContext() ,ActivityMain.class));
						finish();
					} else {
						showAlertDialog(activity ,fromJson==null?getResources().getString(R.string.login_faild):(fromJson.getMsg()==null?getResources().getString(R.string.login_faild):fromJson.getMsg()), getResources().getString(R.string.sure), null, null, null);
					}
				}
				break;
			}
		};
	};

	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_login:
			String username = userPhoneEditText.getText().toString();
			String password = userPasswordEditText.getText().toString();
			if (username.isEmpty()) {
				showAlertDialog(activity ,getResources().getString(R.string.error_register_user_name_null), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			if (password.isEmpty()) {
				showAlertDialog(activity ,getResources().getString(R.string.error_register_password_null), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			loginInBackground(username, password);
			break;
		}
	}
	
	private void progressDialogDismiss() {
		if (progressDialog != null)
			progressDialog.dismiss();
		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ActivityLogin.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
