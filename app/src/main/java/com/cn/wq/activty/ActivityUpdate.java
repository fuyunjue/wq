package com.cn.wq.activty;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager.WakeLock;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wq.R;
import com.cn.wq.database.DataUtils;
import com.cn.wq.database.DbAdapter;
import com.cn.wq.entity.ModelColors;
import com.cn.wq.entity.ModelDetionary;
import com.cn.wq.entity.ModelModels;
import com.cn.wq.entity.ModelOther;
import com.cn.wq.entity.ModelSizes;
import com.cn.wq.entity.ModelVendors;
import com.cn.wq.entity.Response;
import com.cn.wq.utils.MessageHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ActivityUpdate extends Activity {

	private WakeLock mWakelock;
	private DbAdapter dbAdapter;
	private Context mContext;
	private LinearLayout ll;
	private HashMap<String ,String> versionHash = new HashMap<String ,String>();
	private HashMap<String ,String> finishHash = new HashMap<String ,String>();
	
	private LayoutInflater inflater;
	private HashMap<String, View> views = new HashMap<String, View>();
	
	public final static int SET_TEXT = 0x02;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
//		/** 保持屏幕常亮，直到當前界面destroy */
//		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
//		mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
//		mWakelock.acquire();
//		mWakelock.release();
		
		mContext = this;
		dbAdapter = DbAdapter.getDbAdapter(mContext);
		ll = (LinearLayout) findViewById(R.id.ll);
		inflater = LayoutInflater.from(mContext);
		
		versionHash = (HashMap<String, String>) getIntent().getSerializableExtra("versions");
		if(versionHash.size()>0) {
			Iterator iterator = versionHash.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next()+"";
				View v = inflater.inflate(R.layout.update_item, null);
				String text = "";
				if(key.equals(DbAdapter.SIZES)) {
					text = getResources().getString(R.string.cb_sizes) + getResources().getString(R.string.wait_update);
				} else if(key.equals(DbAdapter.COLORS)) {
					text = getResources().getString(R.string.cb_colors) + getResources().getString(R.string.wait_update);
				} else if(key.equals(DbAdapter.MODELS)) {
					text = getResources().getString(R.string.cb_models) + getResources().getString(R.string.wait_update);
				} else if(key.equals(DbAdapter.VENDORS)) {
					text = getResources().getString(R.string.cb_vendors) + getResources().getString(R.string.wait_update);
				} else if(key.equals(DbAdapter.DETIONARY)) {
					text = getResources().getString(R.string.cb_detionary) + getResources().getString(R.string.wait_update);
				}  else if(key.equals(DbAdapter.other)) {
					text = getResources().getString(R.string.cb_other) + getResources().getString(R.string.wait_update);
				}
				((TextView)v.findViewById(R.id.tv)).setText(text);
				ll.addView(v);
				views.put(key, v);
			}
			GoRef();
		} else {
			finish();
		}
	}
	
	
	void sendMsg(String key ,String text ,int what) {
		Message obtainMessage = mHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putString("text", text);
		obtainMessage.setData(data);
		obtainMessage.obj = views.get(key);
		obtainMessage.what = what;
		mHandler.sendMessage(obtainMessage);
	}
	
	Handler mHandler = new Handler() {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SET_TEXT:
				if(msg.obj instanceof View) {
					View view = (View) msg.obj;
					if(msg.getData()!=null) {
						Bundle data = msg.getData();
						String text = data.getString("text")+"";
						((TextView) view.findViewById(R.id.tv)).setText(text);
						if(text.indexOf(getResources().getString(R.string.update_finish))>0) {
							((CheckBox) view.findViewById(R.id.checkBox1)).setChecked(true);
						}
					}
				}
				break;
			}
			if(versionHash.size()==0) {
				finish();
			}
		}
	};

	String text = "";
	int index = 0;
	private void GoRef(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Gson gson = new Gson();
				Iterator iterator = versionHash.keySet().iterator();
				while (iterator.hasNext()) {
					index = 0;
					final String key = iterator.next()+"";
					String value = versionHash.get(key)+"";
					MessageHelper helper = new MessageHelper(mContext);
					HashMap map = new HashMap();
					map.put(key, value);
					runOnUiThread(new Runnable() {
						public void run() {
							if(key.equals(DbAdapter.SIZES)) {
								text = getResources().getString(R.string.cb_sizes) + getResources().getString(R.string.wait_load);
							} else if(key.equals(DbAdapter.COLORS)) {
								text = getResources().getString(R.string.cb_colors) + getResources().getString(R.string.wait_load);
							} else if(key.equals(DbAdapter.MODELS)) {
								text = getResources().getString(R.string.cb_models) + getResources().getString(R.string.wait_load);
							} else if(key.equals(DbAdapter.VENDORS)) {
								text = getResources().getString(R.string.cb_vendors) + getResources().getString(R.string.wait_load);
							} else if(key.equals(DbAdapter.DETIONARY)) {
								text = getResources().getString(R.string.cb_detionary) + getResources().getString(R.string.wait_load);
							}  else if(key.equals(DbAdapter.other)) {
								text = getResources().getString(R.string.cb_other) + getResources().getString(R.string.wait_load);
							}
							((TextView)views.get(key).findViewById(R.id.tv)).setText(text);
						}
					});
					
					String sendPost = helper.sendPost(gson.toJson(map) ,MessageHelper.POST_URL_Synchronous);
					Response fromJson = gson.fromJson(sendPost, Response.class);
					runOnUiThread(new Runnable() {
						public void run() {
							if(key.equals(DbAdapter.SIZES)) {
								text = getResources().getString(R.string.cb_sizes) + getResources().getString(R.string.wait_ready);
							} else if(key.equals(DbAdapter.COLORS)) {
								text = getResources().getString(R.string.cb_colors) + getResources().getString(R.string.wait_ready);
							} else if(key.equals(DbAdapter.MODELS)) {
								text = getResources().getString(R.string.cb_models) + getResources().getString(R.string.wait_ready);
							} else if(key.equals(DbAdapter.VENDORS)) {
								text = getResources().getString(R.string.cb_vendors) + getResources().getString(R.string.wait_ready);
							} else if(key.equals(DbAdapter.DETIONARY)) {
								text = getResources().getString(R.string.cb_detionary) + getResources().getString(R.string.wait_ready);
							} else if(key.equals(DbAdapter.other)) {
								text = getResources().getString(R.string.cb_other) + getResources().getString(R.string.wait_ready);
							}  
							((TextView)views.get(key).findViewById(R.id.tv)).setText(text);
						}
					});
					
					if(fromJson!=null && fromJson.getCode()==1) {
						if(key.equals(DbAdapter.SIZES)) {
								List<ModelSizes> sizeslist = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelSizes>>() {}.getType());
								sendMsg(key, getResources().getString(R.string.update_sizes) + sizeslist.size()+getResources().getString(R.string.update_tiao) ,SET_TEXT);
//								DataUtils.deleteSizes(dbAdapter);
								for (ModelSizes m : sizeslist) {
									boolean insertModels = DataUtils.insertSizes(m,dbAdapter);
									if(insertModels) {
										sendMsg(key, getResources().getString(R.string.update_sizes) + "("+(index++) +"/"+ sizeslist.size()+getResources().getString(R.string.update_tiao)+m.getSizes() ,SET_TEXT);
									}
								}
								sendMsg(key, getResources().getString(R.string.update_sizes) + sizeslist.size() + getResources().getString(R.string.update_tiao) + getResources().getString(R.string.update_finish) ,SET_TEXT);
						} else if(key.equals(DbAdapter.COLORS)) {
							List<ModelColors> colorslist = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelColors>>() {}.getType());
							sendMsg("colors", getResources().getString(R.string.update_colors) + colorslist.size()+getResources().getString(R.string.update_tiao) ,SET_TEXT);
//							DataUtils.deleteColors(dbAdapter);
							for (ModelColors m : colorslist) {
								boolean insertColors = DataUtils.insertColors(m,dbAdapter);
								if(insertColors) 
									sendMsg("colors", getResources().getString(R.string.update_colors) + (index++) +"/"+ colorslist.size() + getResources().getString(R.string.update_tiao) + m.getColor() ,SET_TEXT);
								}
								sendMsg("colors", getResources().getString(R.string.update_colors) + colorslist.size()+ getResources().getString(R.string.update_tiao) + getResources().getString(R.string.update_finish) ,SET_TEXT);
						} else if(key.equals(DbAdapter.MODELS)) {
							List<ModelModels> modelslist = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelModels>>() {}.getType());
							sendMsg(key, getResources().getString(R.string.update_models) + modelslist.size()+getResources().getString(R.string.update_tiao) ,SET_TEXT);
//							DataUtils.deleteModels(dbAdapter);
							for (ModelModels m : modelslist) {
								boolean insertModels = DataUtils.insertModels(m,dbAdapter);
								if(insertModels) 
									sendMsg(key, getResources().getString(R.string.update_models) + (index++) +"/"+ modelslist.size() + getResources().getString(R.string.update_tiao) + m.getModel() ,SET_TEXT);
							}
							sendMsg(key, getResources().getString(R.string.update_models) + modelslist.size()+ getResources().getString(R.string.update_tiao) + getResources().getString(R.string.update_finish) ,SET_TEXT);
						} else if(key.equals(DbAdapter.VENDORS)) {
							List<ModelVendors> vendorslist = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelVendors>>() {}.getType());
							sendMsg(key, getResources().getString(R.string.update_vendors) + vendorslist.size()+getResources().getString(R.string.update_tiao) ,SET_TEXT);
//							DataUtils.deleteVendors(dbAdapter);
							for (ModelVendors m : vendorslist) {
								boolean insertVendors2 = DataUtils.insertVendors(m,dbAdapter);
								if(insertVendors2) 
									sendMsg(key, getResources().getString(R.string.update_vendors) + (index++) +"/"+ vendorslist.size()+getResources().getString(R.string.update_tiao)+m.getDesc_chi() ,SET_TEXT);
							}
							sendMsg(key, getResources().getString(R.string.update_vendors) + vendorslist.size()+ getResources().getString(R.string.update_tiao) + getResources().getString(R.string.update_finish),SET_TEXT);
						} else if(key.equals(DbAdapter.DETIONARY)) {
							List<ModelDetionary> detionarylist = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelDetionary>>() {}.getType());
							sendMsg(key, getResources().getString(R.string.update_detionary) + detionarylist.size()+getResources().getString(R.string.update_tiao)  ,SET_TEXT);
//							DataUtils.deleteDetionary(dbAdapter);
							for (ModelDetionary m : detionarylist) {
								boolean insertDetionary = DataUtils.insertDetionary(m,dbAdapter);
								if(insertDetionary) 
									sendMsg(key, getResources().getString(R.string.update_detionary) + (index++) +"/"+ detionarylist.size()+getResources().getString(R.string.update_tiao)+m.getValue() ,SET_TEXT);
							}
							sendMsg(key, getResources().getString(R.string.update_detionary) + detionarylist.size() + getResources().getString(R.string.update_tiao) + getResources().getString(R.string.update_finish)   ,SET_TEXT);
						} else if(key.equals(DbAdapter.other)) {
							List<ModelOther> otherlist = gson.fromJson(fromJson.getMsg(),new TypeToken<List<ModelOther>>() {}.getType());
							sendMsg(key, getResources().getString(R.string.update_other) + otherlist.size()+getResources().getString(R.string.update_tiao)  ,SET_TEXT);
//							DataUtils.deleteOther(dbAdapter);
							for (ModelOther m : otherlist) {
								boolean insertOther = DataUtils.insertOther(m,dbAdapter);
								if(insertOther) 
									sendMsg(key, getResources().getString(R.string.update_other) +(index++) +"/"+ otherlist.size()+getResources().getString(R.string.update_tiao)+m.getName() ,SET_TEXT);
							}
							sendMsg(key, getResources().getString(R.string.update_other) + otherlist.size()+getResources().getString(R.string.update_tiao) + getResources().getString(R.string.update_finish)  ,SET_TEXT);
						}
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								if(key.equals(DbAdapter.SIZES)) {
									text = getResources().getString(R.string.cb_sizes) + getResources().getString(R.string.update_faild);
								} else if(key.equals(DbAdapter.COLORS)) {
									text = getResources().getString(R.string.cb_colors) + getResources().getString(R.string.update_faild);
								} else if(key.equals(DbAdapter.MODELS)) {
									text = getResources().getString(R.string.cb_models) + getResources().getString(R.string.update_faild);
								} else if(key.equals(DbAdapter.VENDORS)) {
									text = getResources().getString(R.string.cb_vendors) + getResources().getString(R.string.update_faild);
								} else if(key.equals(DbAdapter.DETIONARY)) {
									text = getResources().getString(R.string.cb_detionary) + getResources().getString(R.string.update_faild);
								} else if(key.equals(DbAdapter.other)) {
									text = getResources().getString(R.string.cb_other) + getResources().getString(R.string.update_faild);
								}  
								((TextView)views.get(key).findViewById(R.id.tv)).setText(text);
							}
						});
					}
					
					finishHash.put(key, value);
					if(versionHash.size()==finishHash.size()) {
						finish();
					}
				}
			}
		}).start();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK) {
			if(versionHash.size()==0) {
				finish();
			} else {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	};
}
