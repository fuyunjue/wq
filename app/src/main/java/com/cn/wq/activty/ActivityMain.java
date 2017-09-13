package com.cn.wq.activty;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshView.SimpleXRefreshListener;
import com.bowin.lib.utils.AlertDialogUtil;
import com.bowin.lib.widget.MyAlertDialog;
import com.cn.wq.R;
import com.cn.wq.adapter.AdapterPurchaseListView;
import com.cn.wq.app.AppApplication;
import com.cn.wq.database.DataUtils;
import com.cn.wq.database.DbAdapter;
import com.cn.wq.entity.ModelPhoto;
import com.cn.wq.entity.ModelPurchase;
import com.cn.wq.entity.ModelVersion;
import com.cn.wq.entity.Response;
import com.cn.wq.keeper.UserKeeper;
import com.cn.wq.utils.MessageHelper;
import com.cn.wq.utils.PictureUtil;
import com.cn.wq.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 
 * @Title:  ActivityMain.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-29 下午3:00:19 
 * @version:  V1.0 
 *
 */
@SuppressLint("NewApi")
public class ActivityMain extends Activity implements OnClickListener {
	
	private DbAdapter dbAdapter;

	private static Activity activity;
	private ListView lv;
	private XRefreshView refreshView;
	
	private static final int ADD_PURCHASE_REQUEST = 0x12;
	private static final int INFO_REQUEST = 0x13;
	
	private AdapterPurchaseListView adapter;
	private static final int PAGE_SIZE = 20;	//每页20条
	
	private List<ModelPurchase> source = new ArrayList<ModelPurchase>(); // listview数据源
	public ProgressDialog progressDialog;
	
	private HashMap<String, ModelPurchase> uploadPurchaseList = new HashMap<String, ModelPurchase>();
	private HashMap<String, ModelPurchase> uploadPurchaseList_copy = new HashMap<String, ModelPurchase>();
	private HashMap<String, ModelPhoto> uploadPhotoList = new HashMap<String, ModelPhoto>();
	private HashMap<String, ModelPhoto> uploadPhotoList_copy = new HashMap<String, ModelPhoto>();
	
	private MyAlertDialog showAlertDialog;
	private static long downloadId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		activity = this;
		dbAdapter = DbAdapter.getDbAdapter(activity);

		initView();

		/**
		 * 检查本地数据库是否已更新了门店表、颜色表，未同步的提醒同步
		 */
//		new synTask().execute();
	}
	
	void initView() {
		findViewById(R.id.tv_add).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_add).setOnClickListener(this);
		
		lv = (ListView) findViewById(R.id.lv);
		refreshView = (XRefreshView) findViewById(R.id.custom_view);

		adapter = new AdapterPurchaseListView(getApplicationContext(), 0, 0, source);
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
				try {
					ModelPurchase item = adapter.getItem(position);
					if(item.getV2()==1) {
						//V2.0需求
						Intent intent = new Intent(activity ,ActivityEditV2.class);
						intent.putExtra("purchase", item);
						startActivityForResult(intent, INFO_REQUEST);
					} else {
						Intent intent = new Intent(activity ,ActivityAdd.class);
						intent.putExtra("purchase", item);
						startActivityForResult(intent, INFO_REQUEST);
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		refreshView.setXRefreshViewListener(new SimpleXRefreshListener() {

			@Override
			public void onRefresh() {

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						source.clear();
						loadData();
						refreshView.stopRefresh();
					}
				}, 2000);
			}

			@Override
			public void onLoadMore(boolean isSlience) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						loadData();
						refreshView.stopLoadMore();
					}
				}, 2000);
			}

			@Override
			public void onRelease(float direction) {
				super.onRelease(direction);
			}
		});

		findViewById(R.id.tv_login_out).setOnClickListener(this);
		
		if(AppApplication.getInstance().getUserInfo().getSys() == 1 && !AppApplication.getInstance().getUserInfo().isOutline()) {
			//管理员
			findViewById(R.id.tv_user_manage).setVisibility(View.VISIBLE);
			findViewById(R.id.tv_user_manage).setOnClickListener(this);
		} else {
			findViewById(R.id.tv_user_manage).setVisibility(View.GONE);
		}
		findViewById(R.id.tv_upload).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_upload).setOnClickListener(this);
	}
	
	
	
	private class submitPurchaseTask extends AsyncTask<ModelPurchase, Void, String> {
		Gson gson = new Gson();
		private ModelPurchase purchase;

		@Override
		protected String doInBackground(ModelPurchase... params) {
			MessageHelper helper = new MessageHelper(ActivityMain.this);
			purchase = params[0];
			return helper.sendPost(gson.toJson(params[0]) ,MessageHelper.POST_URL_Purchase);
		}
		
		@Override
		protected void onPostExecute(String result) {
			Response fromJson = gson.fromJson(result, Response.class);
			if(fromJson!=null && fromJson.getCode()==1) {
				//修改该采购信息状态为已上传
				DataUtils.updateUploadState(purchase.getId(),fromJson.getMsg(), DbAdapter.PURCHASE ,dbAdapter);
			}
			uploadPurchaseList_copy.put(purchase.getId(), purchase);
			if(uploadPurchaseList.size()==uploadPurchaseList_copy.size() && uploadPhotoList.size()==uploadPhotoList_copy.size()) {
				//已上传完成，关闭loading
				runOnUiThread(new Runnable() {
					public void run() {
						if(progressDialog!=null) 
							progressDialog.dismiss();
						refreshView.startRefresh();
					}
				});
			}
		}
	}
	
	
	
	/**
	 * 
	 */
	private void showDialog(final String msg) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(progressDialog == null)
					progressDialog = new ProgressDialog(ActivityMain.this);
				progressDialog.setMessage(msg);
				if(!progressDialog.isShowing())
					progressDialog.show();
			}
		});
	}
	
	private class FileUploadTask extends AsyncTask<Object, Void, String> {
		Gson gson = new Gson();
		private ModelPhoto photo;
		
		@Override
		protected String doInBackground(Object... params) {
			MessageHelper helper = new MessageHelper(ActivityMain.this);
			photo = (ModelPhoto) params[0];
			return helper.sendPost(gson.toJson(params[0]) ,MessageHelper.POST_URL_FileUpload);// 使用http post
		}
		
		@Override
		protected void onPostExecute(String result) {
			Gson gson = new Gson();
			Response fromJson = gson.fromJson(result, Response.class);
			if(fromJson!=null && fromJson.getCode()==1) {
				//图片上传成功，更改本地数据为已上传
				DataUtils.updateUploadState(photo.getId(), "", DbAdapter.PHOTO,dbAdapter);
			} else {
				//上传图片失败
			}
			uploadPhotoList_copy.put(photo.getId(), photo);
			if(uploadPurchaseList.size()==uploadPurchaseList_copy.size() && uploadPhotoList.size()==uploadPhotoList_copy.size()) {
				//已上传完成，关闭loading
				runOnUiThread(new Runnable() {
					public void run() {
						if(progressDialog!=null) 
							progressDialog.dismiss();
						refreshView.startRefresh();
					}
				});
			}
		}
		
	}
	
	/**
	 * 加载数据
	 */
	void loadData() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				final List<ModelPurchase> goodsList = DataUtils.getPurchaseList(AppApplication.getInstance().getUserInfo().getId() ,PAGE_SIZE ,source.size(),dbAdapter);
				runOnUiThread(new Runnable() {
					public void run() {
						if(goodsList.size() > 0) {
							source.addAll(goodsList);
							if(adapter == null) {
								adapter = new AdapterPurchaseListView(getApplicationContext(), 0, 0, source);
								lv.setAdapter(adapter);
							}
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode==ADD_PURCHASE_REQUEST && resultCode==RESULT_OK) {
			refreshView.startRefresh();
//		} else if(requestCode==INFO_REQUEST && resultCode==RESULT_OK) {
//			refreshView.startRefresh();
//		}
	}
	
	private void doUpload() {
		if(!Util.CheckNetworkState(activity)) {
			showAlertDialog(getResources().getString(R.string.network_error), getResources().getString(R.string.sure), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(showAlertDialog!=null) showAlertDialog.dismiss();
				}
			}, null, null);
			return;
		}
		//查询所有未上传的采购信息、图片，并启动后台服务进行上传
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				uploadPurchaseList.clear();
				uploadPurchaseList_copy.clear();
				List<ModelPurchase> uploadPurchaseList2 = DataUtils.uploadPurchaseList(AppApplication.getInstance().getUserInfo().getId() ,dbAdapter);
				for (ModelPurchase modelPurchase : uploadPurchaseList2) {
					uploadPurchaseList.put(modelPurchase.getId(), modelPurchase);
				}
				//提醒上传
				if(uploadPurchaseList.size()==0) {
					uploadPhoto();
				} else {
					runOnUiThread(new Runnable() {
						public void run() {
							new Builder(activity).setTitle(getResources().getString(R.string.dialog_message_title)).
							setMessage(getResources().getString(R.string.dialog_dqy) + uploadPurchaseList.size() + getResources().getString(R.string.dialog_upload_purchase)).setNegativeButton(
								getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
										dialog.dismiss();
										//把未上传的图片加入线程队列进行文件压缩处理然后上传
										runOnUiThread(new Runnable() {
											public void run() {
												showDialog(getResources().getString(R.string.dialog_upload));
												new Thread(new Runnable() {
													
													@Override
													public void run() {
														//提交一条采购信息
														Iterator<Entry<String, ModelPurchase>> iterator = uploadPurchaseList.entrySet().iterator();
														while (iterator.hasNext()) {
															Entry<String, ModelPurchase> next = iterator.next();
															new submitPurchaseTask().execute(uploadPurchaseList.get(next.getKey()));
														}
														uploadPhotoList.clear(); 
														uploadPhotoList_copy.clear(); 
														List<ModelPhoto> uploadPhotoList2 = DataUtils.uploadPhotoList(dbAdapter);
														File f = null;
														for (final ModelPhoto photo : uploadPhotoList2) {
															//判斷該圖片是否存在本地，不存在時結束以下所有邏輯並提醒
															f = new File(photo.getMobilepath());
															if(f.exists() && f.isFile())
																uploadPhotoList.put(photo.getId(), photo);
															else {
																runOnUiThread(new Runnable() {
																	public void run() {
																		uploadPhotoList.clear(); 
																		uploadPhotoList_copy.clear(); 
																		if(uploadPurchaseList.size()==uploadPurchaseList_copy.size() && uploadPhotoList.size()==uploadPhotoList_copy.size()) {
																			//已上传完成，关闭loading
																			runOnUiThread(new Runnable() {
																				public void run() {
																					if(progressDialog!=null) 
																						progressDialog.dismiss();
																				}
																			});
																		}
																		showAlertDialog(getResources().getString(R.string.not_upload_photo), getResources().getString(R.string.check), new OnClickListener() {
																			@Override
																			public void onClick(View v) {
																				if(showAlertDialog!=null) showAlertDialog.dismiss();
																				//跳入發現情況的採購訂單
																				try {
																					ModelPurchase item = DataUtils.getPurchaseByid(photo.getPurchaseid(), dbAdapter);
																					Intent intent = new Intent(activity ,ActivityAdd.class);
																					intent.putExtra("purchase", item);
																					startActivityForResult(intent, INFO_REQUEST);
																				} catch(Exception ex){
																					ex.printStackTrace();
																				}
																			}
																		}, getResources().getString(R.string.cancel), new OnClickListener() {
																			@Override
																			public void onClick(View v) {
																				runOnUiThread(new Runnable() {
																					public void run() {
																						if(showAlertDialog!=null) showAlertDialog.dismiss();
																						refreshView.startRefresh();
																					}
																				});
																			}
																		});
																	}
																});
																return;
															}
														}
														Iterator<Entry<String, ModelPhoto>> iterator1 = uploadPhotoList.entrySet().iterator();
														while (iterator1.hasNext()) {
															Entry<String, ModelPhoto> next = iterator1.next();
															ModelPhoto modelPhoto = uploadPhotoList.get(next.getKey());
															modelPhoto.setFileContent(PictureUtil.bitmapToString(modelPhoto.getMobilepath()));
															new FileUploadTask().execute(modelPhoto);
														}
													}
												}).start();
											}
										});
									}
								}).setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int which) {
										dialog.dismiss();
									}
								}).show();
						}
					});
				}
			}
		}).start();
	}
	
	/**
	 * 
	 */
	private void uploadPhoto() {
		//单独查询是否有图片未上传成功的
		uploadPhotoList.clear(); 
		uploadPhotoList_copy.clear(); 
		List<ModelPhoto> uploadPhotoList2 = DataUtils.uploadPhotoList(dbAdapter);
		File f = null;
		for (final ModelPhoto photo : uploadPhotoList2) {
				//判斷該圖片是否存在本地，不存在時結束以下所有邏輯並提醒
				f = new File(photo.getMobilepath());
				if(f.exists() && f.isFile())
					uploadPhotoList.put(photo.getId(), photo);
				else {
					runOnUiThread(new Runnable() {
						public void run() {
							uploadPhotoList.clear(); 
							uploadPhotoList_copy.clear(); 
							if(uploadPurchaseList.size()==uploadPurchaseList_copy.size() && uploadPhotoList.size()==uploadPhotoList_copy.size()) {
								//已上传完成，关闭loading
								runOnUiThread(new Runnable() {
									public void run() {
										if(progressDialog!=null) 
											progressDialog.dismiss();
									}
								});
							}
							showAlertDialog(getResources().getString(R.string.not_upload_photo), getResources().getString(R.string.check), new OnClickListener() {
								@Override
								public void onClick(View v) {
									if(showAlertDialog!=null) showAlertDialog.dismiss();
									//跳入發現情況的採購訂單
									try {
										ModelPurchase item = DataUtils.getPurchaseByid(photo.getPurchaseid(), dbAdapter);
										Intent intent = new Intent(activity ,ActivityAdd.class);
										intent.putExtra("purchase", item);
										startActivityForResult(intent, INFO_REQUEST);
									} catch(Exception ex){
										ex.printStackTrace();
									}
								}
							}, getResources().getString(R.string.cancel), new OnClickListener() {
								@Override
								public void onClick(View v) {
									runOnUiThread(new Runnable() {
										public void run() {
											if(showAlertDialog!=null) showAlertDialog.dismiss();
											refreshView.startRefresh();
										}
									});
								}
							});
						}
					});
					return;
				}
		}
		if(uploadPhotoList.size()==0) {
			//提醒没有未上传
			showAlertDialog(getResources().getString(R.string.not_more_upload), getResources().getString(R.string.sure), new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(showAlertDialog!=null) showAlertDialog.dismiss();
				}
			}, null, null);
		} else {
			runOnUiThread(new Runnable() {
				public void run() {
				new Builder(activity).setTitle(getResources().getString(R.string.dialog_message_title)).
				setMessage(getResources().getString(R.string.dialog_dqy) + uploadPhotoList.size() + getResources().getString(R.string.dialog_upload_photo)).setNegativeButton(
					getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							dialog.dismiss();
							//把未上传的图片加入线程队列进行文件压缩处理然后上传
							runOnUiThread(new Runnable() {
								public void run() {
									showDialog(getResources().getString(R.string.dialog_upload));
									new Thread(new Runnable() {
										
										@Override
										public void run() {
											Iterator<Entry<String, ModelPhoto>> iterator1 = uploadPhotoList.entrySet().iterator();
											while (iterator1.hasNext()) {
												Entry<String, ModelPhoto> next = iterator1.next();
												ModelPhoto modelPhoto = uploadPhotoList.get(next.getKey());
												modelPhoto.setFileContent(PictureUtil.bitmapToString(modelPhoto.getMobilepath()));
												new FileUploadTask().execute(modelPhoto);
											}
										}
									}).start();
								}
							});
						}
					}).setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int which) {
							dialog.dismiss();
						}
					}).show();
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_add:
			/**
			 * V1.0
			 */
//			startActivityForResult(new Intent(activity ,ActivityAdd.class), ADD_PURCHASE_REQUEST);
			/**
			 * V2.0需求
			 */
			//查詢列表中是否有臨時保存的數據，有則查詢出來進行編輯，沒則新增空界面
			Intent intent = new Intent(activity ,ActivityEditV2.class);
			intent.putExtra("purchase", DataUtils.getDraftPurchase(AppApplication.getInstance().getUserInfo().getId(), dbAdapter));
			startActivityForResult(intent, INFO_REQUEST);
			break;
		case R.id.tv_login_out:
			showAlertDialog(getResources().getString(R.string.login_out_msg), getResources().getString(R.string.sure), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
					UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.USER_PASSWORD);
					startActivity(new Intent(activity ,ActivityLogin.class));
				}
				
			}, getResources().getString(R.string.cancel), null);
			break;
		case R.id.tv_user_manage:
			//进入员工管理模式
			startActivity(new Intent(activity ,ActivityUserMagage.class));
			break;
		case R.id.tv_upload:
			doUpload();
			break;
		}
	}
	
	
	/**
	 * 根据提示消息弹窗提示
	 * 
	 * @param msg
	 */
	void showAlertDialog(final String msg ,final String yes ,final OnClickListener yesClick ,final String no ,final OnClickListener noClick) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showAlertDialog = AlertDialogUtil.showAlertDialog(activity, getResources().getString(R.string.app_name), msg, yes, yesClick, no, noClick==null?new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(showAlertDialog!=null) showAlertDialog.dismiss();
					}
				}:noClick);
			}
		});
	}
	
	private class synTask extends AsyncTask<String, Void, String> {

		Gson gson = new Gson();
		@Override
		protected String doInBackground(String... params) {
			MessageHelper helper = new MessageHelper(activity);
			HashMap<String, String> hash = new HashMap<String, String>();
			//app版本
			hash.put("app", Util.getVersion(activity));
			hash.put(DbAdapter.COLORS, DataUtils.getLastTimeByTableName(DbAdapter.COLORS, dbAdapter));
			hash.put(DbAdapter.SIZES, DataUtils.getLastTimeByTableName(DbAdapter.SIZES, dbAdapter));
			hash.put(DbAdapter.MODELS, DataUtils.getLastTimeByTableName(DbAdapter.MODELS, dbAdapter));
			hash.put(DbAdapter.VENDORS, DataUtils.getLastTimeByTableName(DbAdapter.VENDORS, dbAdapter));
			hash.put(DbAdapter.DETIONARY, DataUtils.getLastTimeByTableName(DbAdapter.DETIONARY, dbAdapter));
			hash.put(DbAdapter.other, DataUtils.getLastTimeByTableName(DbAdapter.other, dbAdapter));
			return helper.sendPost(gson.toJson(hash) ,MessageHelper.POST_URL_Synchronous_By_createdate);// 使用http post
		}
		
		@Override
		protected void onPostExecute(String result) {
			Response response = gson.fromJson(result, Response.class);
			if(response!=null && response.getCode()==1) {
				List<ModelVersion> versions = gson.fromJson(response.getMsg(),new TypeToken<List<ModelVersion>>() {}.getType());
				final HashMap<String, String > hash = new HashMap<String, String>();
				for (ModelVersion modelVersion : versions) {
					if(modelVersion.getType().equals("app")) {
						//版本更新
						doDownloadVerion(Util.getVersion(activity), modelVersion.getVersion(), modelVersion.getMsg()+"", modelVersion.getName() ,true);
						return;
					} else {
						hash.put(modelVersion.getType(), modelVersion.getVersion());
					}
				}
				if(hash.size()>0) {
					//有基礎數據需要更新
					runOnUiThread(new Runnable() {
						public void run() {
							//判斷網絡狀況，如果在wifi網絡下，直接跳轉到數據同步界面執行同步，如果在3G網絡的情況下彈窗提示
							if(Util.isWifiConnected(activity)) {
								Intent updateIntent = new Intent(activity ,ActivityUpdate.class);
								updateIntent.putExtra("versions", hash);
								startActivity(updateIntent);
							} else if(!Util.isWifiConnected(activity) && Util.isConnected(activity)) {
								showAlertDialog(getResources().getString(R.string.dialog_syn_tables), getResources().getString(R.string.dialog_syn_tables_yes), new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										runOnUiThread(new Runnable() {
											public void run() {
												if(showAlertDialog!=null) showAlertDialog.dismiss();
												Intent updateIntent = new Intent(activity ,ActivityUpdate.class);
												updateIntent.putExtra("versions", hash);
												startActivity(updateIntent);
											}
										});
									}
								}, getResources().getString(R.string.dialog_syn_tables_no), new OnClickListener() {
									
									@Override
									public void onClick(View v) {
										runOnUiThread(new Runnable() {
											public void run() {
												if(showAlertDialog!=null) showAlertDialog.dismiss();
											}
										});
									}
								});
							}
						}
					});
				}
			}
		}
	}
	
	
	private void doDownloadVerionNow(String currVer, String versions, String content, String url) {
		Toast.makeText(getApplicationContext(), activity.getResources().getString(R.string.versioncheck_4), Toast.LENGTH_SHORT).show();
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+"wqSystem_" + versions + ".apk");
		if(file.exists() && file.isFile()) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(activity, activity.getResources().getString(R.string.has_download), Toast.LENGTH_SHORT).show();
				}
			});
			Intent i = new Intent();
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.setAction(Intent.ACTION_VIEW);
			String type = "application/vnd.android.package-archive";
			i.setDataAndType(Uri.fromFile(file), type);
			startActivity(i);
		} else {
			DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
			request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "wqSystem_" + versions + ".apk");
			 request.setDescription(getResources().getString(R.string.app_name) + versions);
			//设置允许使用的网络类型，这里是移动网络和wifi都可以  
			 request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);
			// request.setMimeType("application/com.trinea.download.file");
			 request.setVisibleInDownloadsUi(true);
			downloadId = downloadManager.enqueue(request); 
		}
	}
	
	/**
	   * 
	   * @description 下载完成广播接收器
	   * @create 2014-7-24下午3:28:34
	   *
	   */
	  public static class DownloadReceiver extends BroadcastReceiver {
	  
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
	    	if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())){
	    		DownloadManager.Query query = new DownloadManager.Query(); 
	    		//在广播中取出下载任务的id
	    		if(downloadId==-1)
	    			downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
	    		query.setFilterById(downloadId); 
	    		Cursor c = manager.query(query); 
	    		if(c.moveToFirst()) {
	    			//获取文件下载路径
	    			String filename = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
	    			//如果文件名不为空，说明已经存在了，拿到文件名想干嘛都好
	    			if(filename != null){
	    				File file = new File(filename);
	    				Intent i = new Intent();
	    				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    				i.setAction(Intent.ACTION_VIEW);
	    				String type = "application/vnd.android.package-archive";
	    				i.setDataAndType(Uri.fromFile(file), type);
	    				activity.startActivity(i);
	    			}
	    		}
	     	} else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
	      }
	      
	    }
	    
	  }
	  
	  /**
		 * 
		 * @param currVer
		 * @param versions
		 * @param content
		 * @param url
		 * @param must	是否必须更新
		 */
		private void doDownloadVerion(final String currVer, final String version, final String content, final String url ,final boolean must) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
			    		Builder dialogBuilder = new Builder(activity);
			    		dialogBuilder.setTitle(getResources().getString(R.string.versioncheck_1));
			    		dialogBuilder.setMessage(getResources().getString(R.string.versioncheck_2) + version + activity.getResources().getString(R.string.versioncheck_3) + content);
			    		dialogBuilder.setPositiveButton(getResources().getString(R.string.y), new DialogInterface.OnClickListener() {
			    			@Override
			    			public void onClick(DialogInterface dialog, int arg1) {
			    				dialog.cancel();
			    				doDownloadVerionNow(currVer, version, content, url);
			    			}
			    		});
			    		dialogBuilder.setNegativeButton(getResources().getString(R.string.n), new DialogInterface.OnClickListener() {
			    			@Override
			    			public void onClick(DialogInterface dialog, int arg1) {
			    				dialog.cancel();
			    				if(must) {
			    					finish();
			    				}
			    			}
			    		});
			    		dialogBuilder.show();
					}
				});
		}
}
