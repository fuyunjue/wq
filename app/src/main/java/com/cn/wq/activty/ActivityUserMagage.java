/**
 * 
 */
package com.cn.wq.activty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshView.SimpleXRefreshListener;
import com.cn.wq.R;
import com.cn.wq.adapter.AdapterUserManageListView;
import com.cn.wq.entity.ModelUserInfo;
import com.cn.wq.entity.Response;
import com.cn.wq.utils.MessageHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @Title:  ActivityUserMagage.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2016-1-5 下午9:17:13 
 * @version:  V1.0 
 * 
 */
public class ActivityUserMagage extends Activity {
	private AdapterUserManageListView adapter;
	private Activity activity;
	private ListView lv;
	private XRefreshView refreshView;
	private List<ModelUserInfo> source = new ArrayList<ModelUserInfo>(); // listview数据源
	
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final int DELETE = 0x02;
	private static final int ADD_USER_REQUEST = 0x13;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_manage);
		activity = this;
		
		initView();
	}
	
	void initView() {
		findViewById(R.id.tv_add).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.tv_add)).setText(getResources().getString(R.string.button_add_user));
		findViewById(R.id.tv_add).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(activity ,ActivityAddUser.class), ADD_USER_REQUEST);
			}
		});
		
		lv = (ListView) findViewById(R.id.lv);
		refreshView = (XRefreshView) findViewById(R.id.custom_view);
		
		adapter = new AdapterUserManageListView(activity, 0, 0, source ,mHandler);
		lv.setAdapter(adapter);

		// 设置是否可以下拉刷新
		refreshView.setPullRefreshEnable(true);
		// 设置是否可以上拉加载
		refreshView.setPullLoadEnable(true);
		// 设置时候可以自动刷新
		refreshView.setAutoRefresh(true);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				try {
//					if(adapter!=null && view.getTag()!=null) {
//						ModelPurchase item = adapter.getItem(position-1);
//						Intent intent = new Intent(activity ,ActivityAdd.class);
//						intent.putExtra("purchase", item);
//						startActivityForResult(intent, INFO_REQUEST);
//					}
//				} catch(Exception ex){
//					ex.printStackTrace();
//				}
			}
		});
		
		refreshView.setXRefreshViewListener(new SimpleXRefreshListener() {

			@Override
			public void onRefresh() {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						loadData(0);
						refreshView.stopRefresh();
					}
				}, 2000);
			}

			@Override
			public void onLoadMore(boolean isSlience) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						loadData(1);
						refreshView.stopLoadMore();
					}
				}, 2000);
			}

			@Override
			public void onRelease(float direction) {
				super.onRelease(direction);
			}
		});
	}
	
	/**
	 * 加载数据
	* @param type 0:刷新  1:加载更多
	 */
	void loadData(int type) {
//		if(type == 0) source.clear();	//刷新
		source.clear();	//刷新
		new GetUserListTask().execute();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
//			case LOAD_FINISH:
//				onLoad();
//				List<ModelUserInfo> list = (List<ModelUserInfo>)msg.obj;
//				source.addAll(list);
//				if(adapter == null) {
//					adapter = new AdapterUserManageListView(activity, 0, 0, source ,mHandler);
//					lv.setAdapter(adapter);
//				}
//				adapter.notifyDataSetChanged();
//				break;
			case DELETE:
				ModelUserInfo info = (ModelUserInfo) msg.obj;
				if(info.getState() == 1) {
					//冻结该账户
					info.setState(0);
				} else if(info.getState() == 0) {
					//解冻该账户
					info.setState(1);
				}
				info.setState(4);
				new DELETETask().equals(info);
				break;
			}
		}
	};
	
	private class DELETETask extends AsyncTask<ModelUserInfo, Void, String> {
		Gson gson = new Gson();
		
		@Override
		protected String doInBackground(ModelUserInfo... params) {
			MessageHelper helper = new MessageHelper(ActivityUserMagage.this);
			return helper.sendPost(gson.toJson(params[0]) ,MessageHelper.POST_URL_USER);// 使用http post
		}
		
		@Override
		protected void onPostExecute(String result) {
			Gson gson = new Gson();
			final Response fromJson = gson.fromJson(result, Response.class);
			if(fromJson!=null && fromJson.getCode()==1) {
				final List<ModelUserInfo> list = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelUserInfo>>() {}.getType());  
				runOnUiThread(new Runnable() {
					public void run() {
						source.addAll(list);
						if(adapter == null) {
							adapter = new AdapterUserManageListView(activity, 0, 0, source ,mHandler);
							lv.setAdapter(adapter);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity, fromJson==null?getResources().getString(R.string.czsb):(fromJson.getMsg()==null?getResources().getString(R.string.czsb):fromJson.getMsg()), Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
		
	}
	
	private class GetUserListTask extends AsyncTask<Object, Void, String> {
		Gson gson = new Gson();
		
		@Override
		protected String doInBackground(Object... params) {
			MessageHelper helper = new MessageHelper(ActivityUserMagage.this);
			ModelUserInfo info = new ModelUserInfo();
			info.setAction(3);
			return helper.sendPost(gson.toJson(info) ,MessageHelper.POST_URL_USER);// 使用http post
		}
		
		@Override
		protected void onPostExecute(String result) {
			Gson gson = new Gson();
			final Response fromJson = gson.fromJson(result, Response.class);
			if(fromJson!=null && fromJson.getCode()==1) {
				final List<ModelUserInfo> list = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelUserInfo>>() {}.getType());  
				runOnUiThread(new Runnable() {
					public void run() {
						source.addAll(list);
						if(adapter == null) {
							adapter = new AdapterUserManageListView(activity, 0, 0, source ,mHandler);
							lv.setAdapter(adapter);
						}
						adapter.notifyDataSetChanged();
					}
				});
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(activity, fromJson==null?getResources().getString(R.string.czsb):(fromJson.getMsg()==null?getResources().getString(R.string.czsb):fromJson.getMsg()), Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==ADD_USER_REQUEST && resultCode==RESULT_OK) {
			final ArrayList<ModelUserInfo> list = (ArrayList<ModelUserInfo>) data.getSerializableExtra("list");
			runOnUiThread(new Runnable() {
				public void run() {
					source.clear();
					source.addAll(list);
					if(adapter == null) {
						adapter = new AdapterUserManageListView(activity, 0, 0, source ,mHandler);
						lv.setAdapter(adapter);
					}
					adapter.notifyDataSetChanged();
				}
			});
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
