package com.cn.wq.activty;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bowin.lib.utils.AlertDialogUtil;
import com.bowin.lib.widget.MyAlertDialog;
import com.cn.wq.R;
import com.cn.wq.app.AppApplication;
import com.cn.wq.database.DataUtils;
import com.cn.wq.database.DbAdapter;
import com.cn.wq.entity.ModelItemnumber;
import com.cn.wq.entity.ModelPhoto;
import com.cn.wq.entity.ModelPurchase;
import com.cn.wq.entity.ModelSizeInfo;
import com.cn.wq.entity.ModelStyleNumberPhoto;
import com.cn.wq.utils.PictureUtil;
import com.cn.wq.utils.Util;
import com.cn.wq.view.MyLinearLayout;

/**
 * V2.0需求，編輯採購需求界面
 * @author Terence
 *
 */
@SuppressLint("NewApi")
public class ActivityEditV2 extends Activity implements OnClickListener {

	private Activity activity;
	private MyLinearLayout ll;
	private LayoutInflater inflater;
	private ModelPurchase purchase = null;	//传递过来的参数
	private MyAlertDialog showAlertDialog;
	private DbAdapter dbAdapter;
	private ProgressDialog progressDialog;
	private TextView tv_harvest_time;
	private String time;
	private static final int SELECT_PHOTO = 0x12;	//请求相册或拍照
	private static final int SIZE_EDIT_RESULT = 0x13;	//碼數選擇圖片並編輯後返回
	private String companyId = java.util.UUID.randomUUID().toString();
	private String mCurrentPhotoPath;// 图片路径
	private ImageView mCurrentImageView;	//当前选择操作的IMAGEVIEW
	private static final String photo_type_company = "companyPhoto";
	private static final String photo_type_itemnumber = "itemnumberPhoto";
	private boolean save = false;	//儲存保存狀態，一次採購完成後返回列表頁面刷新列表
	
	
	/**
	 * 款號列表，一條記錄表示一個款哈
	 */
	private List<View> itemnumberViews = new ArrayList<View>();
	//HashMap用於保存橫向滾動列表
	private HashMap<View, List<View>> itemnumberHashViews = new HashMap<View, List<View>>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_v2);

		activity = this;
		dbAdapter = DbAdapter.getDbAdapter(activity);
		inflater = LayoutInflater.from(activity);
		if(getIntent().getSerializableExtra("purchase")!=null) {
			purchase = (ModelPurchase) getIntent().getSerializableExtra("purchase");
		}
		
		initView();
	}
	
	void initView() {
		findViewById(R.id.tv_back).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_back).setOnClickListener(this);
		findViewById(R.id.tv_clear).setOnClickListener(this);
		findViewById(R.id.btn_save).setOnClickListener(this);
		findViewById(R.id.iv_company).setOnClickListener(this);
		findViewById(R.id.iv_company).setTag(R.id.tag_second ,photo_type_company);	//第二個放圖片類型，用於創建文件名時判斷
		findViewById(R.id.tv_add_kh).setOnClickListener(this);
		
		ll = (MyLinearLayout) findViewById(R.id.ll);
		
		if(purchase != null) {
			//查詢檔口圖片信息
			ModelPhoto companyPhoto = purchase.getCompanyPhoto();
			if(companyPhoto != null) {
				//檔口
				((ImageView)findViewById(R.id.iv_company)).setImageBitmap(PictureUtil.getSmallBitmap(companyPhoto.getMobilepath()));
				((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_first ,companyPhoto.getMobilepath());	//第一個放圖片保存路徑
			}
			//款號列表
			List<ModelItemnumber> itemnumbers = purchase.getItemnumbers();
			for (ModelItemnumber itemnumber : itemnumbers) {
				addItemnumber(itemnumber);
			}
			if(purchase!=null &&purchase.getHasupload()==1) {
				//不可置焦，不可編輯
				findViewById(R.id.tv_add_kh).setVisibility(View.GONE);
				findViewById(R.id.btn_save).setVisibility(View.GONE);
				((MyLinearLayout) findViewById(R.id.ll_1)).setB(true);
			} else {
				findViewById(R.id.tv_clear).setVisibility(View.VISIBLE);
			}
		} else {
			//添加一個空的款號
			addItemnumber(null);
		}
	}
	
	/**
	 * 每個款號最後會添加一個空的ImageView
	 * @param v	添加多個圖片的view(ScrollView下的Linearlayout)
	 * @param numberPhoto
	 * @param view	一個款號view（包含上面的v）
	 */
	void addImageView(LinearLayout v ,ModelStyleNumberPhoto numberPhoto ,View view) {
		View hs_iv = inflater.inflate(R.layout.layout_item_itemnumber_iv, null);
		if(itemnumberHashViews.get(view)==null) {
			List<View> views = new ArrayList<View>();
			itemnumberHashViews.put(view, views);
		}
		itemnumberHashViews.get(view).add(hs_iv);
		ImageView iv = (ImageView) hs_iv.findViewById(R.id.iv);
		iv.setTag(R.id.tag_third, view);
		iv.setTag(R.id.tag_second ,photo_type_itemnumber);
		iv.setTag(R.id.tag_five, hs_iv);
		//每點擊添加都加在第二個
		if(numberPhoto==null) {
			v.addView(hs_iv ,0);	
		} else {
			if(purchase!=null &&purchase.getHasupload()==1) {
				//已上傳了，從第一章開始加
				v.addView(hs_iv ,0);	
			} else {
				v.addView(hs_iv ,1);	
			}
			ModelPhoto styleNumberPhoto = numberPhoto.getStyleNumberPhoto();
			if(styleNumberPhoto != null && styleNumberPhoto.getMobilepath()!=null)
				iv.setTag(R.id.tag_first, styleNumberPhoto.getMobilepath());
		}
		
		iv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//選擇相冊多張圖片或拍照
				View parentView = (View)v.getTag(R.id.tag_third);
				//款號必填
				if(((EditText) parentView.findViewById(R.id.et_kh)).getText().toString().trim().length()==0) {
					showAlertDialog(activity, getResources().getString(R.string.edit_kh), getResources().getString(R.string.sure), null, null, null);
					return;	
				}
				mCurrentImageView = (ImageView) v;
				//如果該圖片已經選擇過，文字變更為修改，且初始化已有碼數信息
				if(mCurrentImageView.getTag(R.id.tag_first) != null) {
					if(purchase!=null &&purchase.getHasupload()==1) {
						//只看
						Intent newIntent = new Intent(activity ,ActivityEditItemNumberImg.class);
						newIntent.putExtra("list", (ArrayList<ModelSizeInfo>)mCurrentImageView.getTag(R.id.tag_four));
						newIntent.putExtra("imgPath", mCurrentImageView.getTag(R.id.tag_first).toString());
						newIntent.putExtra("hasupload", purchase.getHasupload());
						startActivity(newIntent);
					} else {
						//編輯
						Intent newIntent = new Intent(activity, SelectPicPopupWindow.class);
						newIntent.putExtra("imgPath", mCurrentImageView.getTag(R.id.tag_first).toString());
						newIntent.putExtra("list", (ArrayList<ModelSizeInfo>)mCurrentImageView.getTag(R.id.tag_four));
						startActivityForResult(newIntent, SELECT_PHOTO);
					}
				} else {
					startActivityForResult(new Intent(activity, SelectPicPopupWindow.class), SELECT_PHOTO);
				}
			}
		});
		LinearLayout ll_delete = (LinearLayout) hs_iv.findViewById(R.id.ll_delete);
		ll_delete.setTag(R.id.tag_first, v);
		ll_delete.setTag(R.id.tag_second, hs_iv);
		ll_delete.setTag(R.id.tag_third, view);
		ll_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LinearLayout hs = (LinearLayout) v.getTag(R.id.tag_first);
				hs.removeView((View) v.getTag(R.id.tag_second));
				itemnumberHashViews.get(v.getTag(R.id.tag_third)).remove(v.getTag(R.id.tag_second));
			}
		});
		if(numberPhoto != null) {
			if(numberPhoto.getStyleNumberPhoto()!=null)
				iv.setImageBitmap(PictureUtil.getSmallBitmap(numberPhoto.getStyleNumberPhoto().getMobilepath()));
			if(purchase!=null &&purchase.getHasupload()==1) {
				hs_iv.findViewById(R.id.ll_delete).setVisibility(View.INVISIBLE);
			} else {
				hs_iv.findViewById(R.id.ll_delete).setVisibility(View.VISIBLE);
			}
			
			List<ModelSizeInfo> sizeInfos = numberPhoto.getSizeInfos();
			if(sizeInfos!=null && sizeInfos.size()!=0) {
				iv.setTag(R.id.tag_four, sizeInfos);
				//在圖片上設置文字顯示
				LinearLayout ll_text = (LinearLayout) hs_iv.findViewById(R.id.ll_text);
				hs_iv.findViewById(R.id.rl_info).setVisibility(View.VISIBLE);
				for (ModelSizeInfo sizeInfo : sizeInfos) {
					View inflate = inflater.inflate(R.layout.layout_item_size_text, null);
					((TextView)inflate.findViewById(R.id.textView1)).setText(sizeInfo.getColor()+"-" + sizeInfo.getSize() + "-" + sizeInfo.getAmount());
					ll_text.addView(inflate);
				}
			}
		}
	}
	/**
	 * 添加款號
	 */
	void addItemnumber(ModelItemnumber itemnumber) {
		View inflate = inflater.inflate(R.layout.layout_item_purchasev2, null);
		ll.addView(inflate);
		itemnumberViews.add(inflate);
		
		if(purchase!=null && purchase.getHasupload()==1) {
			((MyLinearLayout)inflate.findViewById(R.id.ll_edittext)).setB(true);
		}
		
		LinearLayout ll_itemnumber_iv = (LinearLayout) inflate.findViewById(R.id.ll_itemnumber_iv);
		//添加一個空的imageview
		if(itemnumber==null || (purchase!=null && purchase.getHasupload()!=1)) {
			addImageView(ll_itemnumber_iv, null ,inflate);
		}
		if(itemnumber!=null) {
			//初始化
			((EditText) inflate.findViewById(R.id.et_kh)).setText(itemnumber.getStyleNumber());
			((EditText) inflate.findViewById(R.id.et_rj)).setText(itemnumber.getPrice()+"");
			if(itemnumber.getHarvestTime().length()!=0 && !"null".equals(itemnumber.getHarvestTime().toLowerCase())) {
				((TextView) inflate.findViewById(R.id.tv_harvest_time)).setText(itemnumber.getHarvestTime()+"");
				((TextView) inflate.findViewById(R.id.tv_harvest_time)).setTag(R.id.tag_first ,itemnumber.getHarvestTime()+"");
			}
			
			List<ModelStyleNumberPhoto> styleNumberPhotos = itemnumber.getStyleNumberPhotos();
			for (ModelStyleNumberPhoto numberPhoto : styleNumberPhotos) {
				addImageView(ll_itemnumber_iv, numberPhoto ,inflate);
			}
		} 
		((TextView) inflate.findViewById(R.id.tv_harvest_time)).setOnClickListener(this);
		((EditText) inflate.findViewById(R.id.et_kh)).addTextChangedListener(new khTextWatcher((EditText) inflate.findViewById(R.id.et_kh)) {});
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_clear:
			//重置，清空當前界面內容
			clearUI();
			//刪除草稿
			DataUtils.deleteDraftPurchase(dbAdapter);
			break;
		case R.id.tv_back:
			//返回，保存當前界面信息
			goBack();
			break;
		case R.id.btn_save:
			//保存一條記錄
			boolean b = saveUI(0);
			if(b) doSaveSqlite();
			break;
		case R.id.iv_company:
			//選擇檔口照片
			mCurrentImageView = (ImageView) v;
			Intent newIntent = new Intent(activity ,SelectPicPopupWindow.class);
			if(v.getTag(R.id.tag_first)!=null) {
				newIntent.putExtra("imgPath", v.getTag(R.id.tag_first).toString());
			}
			startActivityForResult(newIntent, SELECT_PHOTO);
			break;
		case R.id.tv_add_kh:
			//添加款號
			addItemnumber(null);
			break;
		case R.id.tv_harvest_time:
			tv_harvest_time = (TextView) v;
			DatePickerFragment datePicker = new DatePickerFragment();  
		    datePicker.show(getFragmentManager(), "datePicker");  
			break;
		}
	}
	
	
	/**
	 * 重置當前界面
	 */
	void clearUI() {
		showAlertDialog(activity, getResources().getString(R.string.alert_clearui), getResources().getString(R.string.sure), new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						if(showAlertDialog!=null)showAlertDialog.dismiss();
						itemnumberViews.clear();
						purchase = null;
						mCurrentPhotoPath = null;// 图片路径
						mCurrentImageView = null;
						showAlertDialog = null;
						progressDialog = null;
						tv_harvest_time = null;
						time = "";
						ll.removeAllViews();
						addItemnumber(null);
						((ImageView)findViewById(R.id.iv_company)).setImageBitmap(null);
						((ImageView)findViewById(R.id.iv_company)).setBackgroundResource(R.drawable.collect_add);
						((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_first ,null);
						((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_second , null);
						((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_third , null);
					}
				});
			}
		}, null ,null);
	}
	
	@Override
	protected void onDestroy() {
		if(!save || (purchase!=null && purchase.getState()!=1))
			saveUI(1);
		super.onDestroy();
	}
	
	/**
	 * 保存當前界面內容，保存為草稿
	 * @param action 為1時為保存草稿，否則為保存數據
	 */
	boolean saveUI(int action) {
		if(purchase!=null && purchase.getHasupload()==0) {
			
		} else {
			purchase = new ModelPurchase();
			purchase.setId(java.util.UUID.randomUUID().toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			purchase.setCreatedate(format.format(new Date()));
			purchase.setV2(1);
			purchase.setState(0);	//標識為未完成
			purchase.setCompanyId(companyId);
			purchase.setUserid(AppApplication.getInstance().getUserInfo().getId());
		}
		purchase.getItemnumbers().clear();
		int i = 0;
		for (View view : itemnumberViews) {
			//保存數據時款號必填
//			if(action == 0 && ((EditText) view.findViewById(R.id.et_kh)).getText().toString().trim().length()==0) {
//				showAlertDialog(activity, getResources().getString(R.string.edit_kh), getResources().getString(R.string.sure), null, null, null);
//				return;	
//			}
			ModelItemnumber itemnumber = new ModelItemnumber();
			itemnumber.setId(java.util.UUID.randomUUID().toString());
			itemnumber.setStyleNumber(((EditText) view.findViewById(R.id.et_kh)).getText()+"");
			if(((EditText) view.findViewById(R.id.et_rj)).getText().toString().trim().length()==0) {
				itemnumber.setPrice(0);
			} else {
				itemnumber.setPrice(Double.parseDouble(((EditText) view.findViewById(R.id.et_rj)).getText().toString()));
			}
			if(((TextView)view.findViewById(R.id.tv_harvest_time)).getTag(R.id.tag_first)!=null)
				itemnumber.setHarvestTime(((TextView) view.findViewById(R.id.tv_harvest_time)).getTag(R.id.tag_first).toString());
			else
				itemnumber.setHarvestTime("");
			List<View> list = itemnumberHashViews.get(view);
			if(list!=null) {
				for (View view2 : list) {
					ImageView iv = (ImageView) view2.findViewById(R.id.iv);
					//初始化款號中的一個碼數信息
					ModelStyleNumberPhoto modelStyleNumberPhoto = new ModelStyleNumberPhoto();
					modelStyleNumberPhoto.setId(java.util.UUID.randomUUID().toString());
					//初始化圖片信息
					if(iv.getTag(R.id.tag_first) != null) {
						modelStyleNumberPhoto.setStyleNumberPhoto(initPhoto(iv, ++i, itemnumber.getStyleNumber()));
						modelStyleNumberPhoto.setIndex1(modelStyleNumberPhoto.getStyleNumberPhoto().getIndex1());
						if(iv.getTag(R.id.tag_four) != null)
							modelStyleNumberPhoto.setSizeInfos((ArrayList<ModelSizeInfo>)iv.getTag(R.id.tag_four));
						itemnumber.getStyleNumberPhotos().add(modelStyleNumberPhoto);
					}
				}
			}
			purchase.getItemnumbers().add(itemnumber);
		}
		
		if(findViewById(R.id.iv_company).getTag(R.id.tag_first) != null) {
			//初始化檔口圖片信息
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String timeStamp = format.format(new Date());
			String path = findViewById(R.id.iv_company).getTag(R.id.tag_first).toString();
			
			ModelPhoto companyPhoto = new ModelPhoto();
			companyPhoto.setId(java.util.UUID.randomUUID().toString());
			companyPhoto.setUserid(AppApplication.getInstance().getUserInfo().getId());
			companyPhoto.setHasupload(0);
			companyPhoto.setIndex1(0);
			companyPhoto.setCreatedate(timeStamp);
			if(purchase!=null) 
				companyPhoto.setPurchaseid(purchase.getId());
			companyPhoto.setMobilepath(path);
			companyPhoto.setFilename(path.substring(path.lastIndexOf("/")+1));
			companyPhoto.setType(1);
			purchase.setCompanyPhoto(companyPhoto);
		}
		
		
		if(action==1) {
			//保存草稿
			boolean insertGoods = DataUtils.insertPurchase(purchase, dbAdapter);
			if(insertGoods) {
				insertSuccess();
			}
			return false;	
		} else {
			//保存採購數據
			if(purchase.getCompanyPhoto()==null) {
				showAlertDialog(activity, getResources().getString(R.string.v2_company_photo_empty), getResources().getString(R.string.sure), null, null, null);
				return false;	
			}
			List<ModelItemnumber> itemnumbers = purchase.getItemnumbers();
			for (ModelItemnumber modelItemnumber : itemnumbers) {
				if(modelItemnumber.getStyleNumber()==null || modelItemnumber.getStyleNumber().trim().length()==0) {
					showAlertDialog(activity, getResources().getString(R.string.v2_itemnumber_kh_empty), getResources().getString(R.string.sure), null, null, null);
					return false;	
				} else if(modelItemnumber.getStyleNumberPhotos().size()==0) {
					showAlertDialog(activity, String.format(getResources().getString(R.string.v2_itemnumber_size_empty), modelItemnumber.getStyleNumber()) , getResources().getString(R.string.sure), null, null, null);
					return false;	
				}
			}
			return true;
		}
	}
	
	/**
	 * 保存到本地數據庫
	 */
	void doSaveSqlite () {
		if(progressDialog == null)
			progressDialog = Util.getProgressDialog(activity, true, false);

		((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ActivityEditV2.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		
		purchase.setState(1);
		boolean insertGoods = DataUtils.insertPurchase(purchase ,dbAdapter);
		if(insertGoods) {
//			insertSuccess();
			save = true;	//標注保存成功
			setResult(RESULT_OK);
			finish();
		} else {
			if(progressDialog != null) 
				progressDialog.cancel();
			showAlertDialog(activity, getResources().getString(R.string.czsb), getResources().getString(R.string.sure), null, null, null);
		}
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
				showAlertDialog = AlertDialogUtil.showAlertDialog(activity, getResources().getString(R.string.app_name), msg+"", yes+"", yesClick==null?new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						runOnUiThread(new Runnable() {
							public void run() {
								if(showAlertDialog!=null)showAlertDialog.dismiss();
							}
						});
					}
				}:yesClick, getResources().getString(R.string.cancel), new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						runOnUiThread(new Runnable() {
							public void run() {
								if(showAlertDialog!=null)showAlertDialog.dismiss();
							}
						});
					}
				});
			}
		});
	}
	
	/**
	 * 款號輸入框正則
	 * @author Terence
	 *
	 */
	class khTextWatcher implements TextWatcher {

		private TextView v;
		public khTextWatcher(TextView v) {
			this.v = v;
		}
		String str;	//改变前的字符串
		int lenth;
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			str = s.toString();
			lenth = s.length();
		}
		
		@Override
		public void afterTextChanged(final Editable s) {
			//正則表達式過濾款號
			if(s.toString().trim().length()>0 && !Pattern.matches("[0-9a-zA-Z-=]+", s)) {
				runOnUiThread(new Runnable() {
					public void run() {
						showAlertDialog(activity, getResources().getString(R.string.edit_kh_matches), getResources().getString(R.string.sure), null, null, null);
						v.setText(str+"");
					}
				});
				return;
			}
		}
	}

	
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {  
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setCancelable(true);
		}
		@Override  
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    final Calendar c = Calendar.getInstance();  
		    int year = c.get(Calendar.YEAR);  
		    int month = c.get(Calendar.MONTH);  
		    int day = c.get(Calendar.DAY_OF_MONTH);  
		    return new DatePickerDialog(getActivity(), this, year, month, day);  
		}  
		@Override  
		public void onDateSet(DatePicker view, int year, int month, int day) {
			time = ""+year+"-"+(month+1)+"-"+day; 
			tv_harvest_time.setText(time);
			tv_harvest_time.setTag(R.id.tag_first, time);
		}  
	}  
	

	
	void insertSuccess() {
			runOnUiThread(new Runnable() {
				public void run() {
					if(progressDialog!=null) progressDialog.dismiss();
					if(showAlertDialog!=null)showAlertDialog.dismiss();
					itemnumberViews.clear();
					purchase = null;
					mCurrentPhotoPath = null;// 图片路径
					mCurrentImageView = null;
					showAlertDialog = null;
					progressDialog = null;
					tv_harvest_time = null;
					time = "";
					ll.removeAllViews();
					addItemnumber(null);
					((ImageView)findViewById(R.id.iv_company)).setImageBitmap(null);
					((ImageView)findViewById(R.id.iv_company)).setBackgroundResource(R.drawable.collect_add);
					((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_first ,null);
					((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_second , null);
					((ImageView)findViewById(R.id.iv_company)).setTag(R.id.tag_third , null);
					setResult(RESULT_OK);
				}
			});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == SELECT_PHOTO && resultCode == SelectPicPopupWindow.REQUEST_TAKE_PHOTO) {
			//启动照相机
			//判斷是檔口還是款號
			if(mCurrentImageView!=null && photo_type_company.equals(mCurrentImageView.getTag(R.id.tag_second))) {
				//檔口
				takePhoto(null);
			} else if(mCurrentImageView!=null && photo_type_itemnumber.equals(mCurrentImageView.getTag(R.id.tag_second))){
				//款號圖片
				View parentView = (View) mCurrentImageView.getTag(R.id.tag_third);
				takePhoto(((EditText) parentView.findViewById(R.id.et_kh)).getText().toString());
			}
		} else if(requestCode == SELECT_PHOTO && resultCode == SelectPicPopupWindow.REQUEST_PICK_PHOTO) {
			//从相册选择
			PictureUtil.pickPhote(activity);
		} else if(requestCode == SelectPicPopupWindow.REQUEST_TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
			//拍照返回结果
			// 添加到图库,这样可以在手机的图库程序中看到程序拍摄的照片
			PictureUtil.galleryAddPic(this, mCurrentPhotoPath);
			//先删除原来添加的文件
			if(mCurrentImageView.getTag(R.id.tag_first)!=null) {
				String path = mCurrentImageView.getTag(R.id.tag_first).toString();
				File f = new File(path);
				if(f.exists() && f.isFile()) 
					f.delete();
			}
			//判斷是檔口還是款號
			if(mCurrentImageView!=null && photo_type_company.equals(mCurrentImageView.getTag(R.id.tag_second))) {
				//檔口
				mCurrentImageView.setImageBitmap(PictureUtil.getSmallBitmap(mCurrentPhotoPath));
				mCurrentImageView.setTag(R.id.tag_first ,mCurrentPhotoPath);
			} else if(mCurrentImageView!=null && photo_type_itemnumber.equals(mCurrentImageView.getTag(R.id.tag_second))){
				//款號圖片
				//進入到編輯圖片，編輯款號文字信息
				Intent newIntent = new Intent(activity ,ActivityEditItemNumberImg.class);
				newIntent.putExtra("list", (ArrayList<ModelSizeInfo>)mCurrentImageView.getTag(R.id.tag_four));
				newIntent.putExtra("imgPath", mCurrentPhotoPath);
				startActivityForResult(newIntent ,SIZE_EDIT_RESULT);
			}
		} else if(requestCode == SelectPicPopupWindow.REQUEST_PICK_PHOTO_REQUEST && resultCode == RESULT_OK){
			//相册选择图片返回
			try {
	            Uri originalUri = intent.getData();         //获得图片的uri 
	            //获取图片的路径
	            String[] proj = {MediaStore.Images.Media.DATA};
	            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            cursor.moveToFirst();
	            String string = cursor.getString(column_index);

	            if(mCurrentImageView.getTag(R.id.tag_first)!=null) {
		            //先删除原来添加的文件
					String path = mCurrentImageView.getTag(R.id.tag_first).toString();
					File f = new File(path);
					if(f.exists() && f.isFile()) 
						f.delete();
	            }

	            Object tag = mCurrentImageView.getTag(R.id.tag_second);
	            
	            //判斷是檔口還是款號
				if(mCurrentImageView!=null && photo_type_company.equals(tag)) {
					//檔口
					createImageFile(null);
					//复制文件
		            PictureUtil.fileChannelCopy(new File(string), new File(mCurrentPhotoPath));
		            PictureUtil.galleryAddPic(this, mCurrentPhotoPath);
		            mCurrentImageView.setImageBitmap(PictureUtil.getSmallBitmap(mCurrentPhotoPath));
		            mCurrentImageView.setTag(R.id.tag_first ,mCurrentPhotoPath);
				} else if(mCurrentImageView!=null && photo_type_itemnumber.equals(mCurrentImageView.getTag(R.id.tag_second))){
					View parentView = (View)mCurrentImageView.getTag(R.id.tag_third);
					createImageFile(((EditText) parentView.findViewById(R.id.et_kh)).getText().toString());
					//复制文件
		            PictureUtil.fileChannelCopy(new File(string), new File(mCurrentPhotoPath));
		            PictureUtil.galleryAddPic(this, mCurrentPhotoPath);
					//款號圖片
					//進入到編輯圖片，編輯款號文字信息
					Intent newIntent = new Intent(activity ,ActivityEditItemNumberImg.class);
					newIntent.putExtra("list", (ArrayList<ModelSizeInfo>)mCurrentImageView.getTag(R.id.tag_four));
					newIntent.putExtra("imgPath", mCurrentPhotoPath);
					startActivityForResult(newIntent ,SIZE_EDIT_RESULT);
				}

	        }catch (Exception e) {
	        	e.printStackTrace();
	        }
		} else if(requestCode == SIZE_EDIT_RESULT && resultCode == RESULT_OK){
			//編輯碼數後返回
			mCurrentImageView.setImageBitmap(PictureUtil.getSmallBitmap(mCurrentPhotoPath));
			mCurrentImageView.setTag(R.id.tag_first ,mCurrentPhotoPath);
			//把該size的文字信息集合放到tag中
			ArrayList<ModelSizeInfo> list = (ArrayList<ModelSizeInfo>)intent.getSerializableExtra("list");
			mCurrentImageView.setTag(R.id.tag_four, list);
			//在圖片上設置文字顯示
			View viewFive = (View)mCurrentImageView.getTag(R.id.tag_five);
			LinearLayout ll_text = (LinearLayout) viewFive.findViewById(R.id.ll_text);
			ll_text.removeAllViews();
			viewFive.findViewById(R.id.rl_info).setVisibility(View.VISIBLE);
			for (ModelSizeInfo sizeInfo : list) {
				View inflate = inflater.inflate(R.layout.layout_item_size_text, null);
				((TextView)inflate.findViewById(R.id.textView1)).setText(sizeInfo.getColor()+"-" + sizeInfo.getSize() + "-" + sizeInfo.getAmount());
				ll_text.addView(inflate);
			}
			
			if(intent.getStringExtra("action").equals("add")) {
				View parentView = (View) mCurrentImageView.getTag(R.id.tag_third);
				//新增一個空的
				//添加一個空的imageview
				addImageView((LinearLayout) parentView.findViewById(R.id.ll_itemnumber_iv), null ,parentView);
	
				View view = (View) mCurrentImageView.getTag(R.id.tag_five);
				view.findViewById(R.id.ll_delete).setVisibility(View.VISIBLE);
			}
		} else if(SELECT_PHOTO == requestCode && resultCode == SelectPicPopupWindow.REQUEST_SHOW_COMPANY) {
			//查看檔口圖片
			Intent newIntent = new Intent(activity ,ActivityImage.class);
			newIntent.putExtra("imgPath", mCurrentImageView.getTag(R.id.tag_first).toString());
			activity.startActivity(newIntent);
		} else if(SELECT_PHOTO == requestCode && resultCode == SelectPicPopupWindow.REQUEST_SHOW_ITEMNUMBER) {
			//查看碼數信息
			//進入到編輯圖片，編輯款號文字信息
			Intent newIntent = new Intent(activity ,ActivityEditItemNumberImg.class);
			newIntent.putExtra("list", (ArrayList<ModelSizeInfo>)mCurrentImageView.getTag(R.id.tag_four));
			mCurrentPhotoPath = mCurrentImageView.getTag(R.id.tag_first).toString();
			newIntent.putExtra("imgPath", mCurrentPhotoPath);
			startActivityForResult(newIntent ,SIZE_EDIT_RESULT);
		}
	}
	
	/**
	 * 拍照
	 */
	private void takePhoto(String kh) {
		try {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 指定存放拍摄照片的位置
			File f = createImageFile(kh);
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(takePictureIntent, SelectPicPopupWindow.REQUEST_TAKE_PHOTO_REQUEST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 把程序拍摄的照片放到 SD卡的 Pictures目录中 pw 文件夹中
	 * 檔口照片的命名规则为：companyId_timeStamp.jpg
	 * 款號照片的命名規則為：companyId_kh_timeStamp.jpg
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressLint("SimpleDateFormat")
	private File createImageFile(String kh) throws IOException {
		String imageFileName = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSSS");
		String timeStamp = format.format(new Date());
		if(photo_type_company.equals(mCurrentImageView.getTag(R.id.tag_second))) {
			//檔口圖片
			imageFileName = companyId + "_" + timeStamp + ".jpg";
		} else if(photo_type_itemnumber.equals(mCurrentImageView.getTag(R.id.tag_second))){
			//款號圖片
			imageFileName = companyId + "_" + kh + "_" + timeStamp + ".jpg";
		}
		File image = new File(PictureUtil.getAlbumDir(), imageFileName);
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	private void goBack() {
//		if(add)
//			setResult(RESULT_OK);
//		//清空分類、次分類、收貨日期本地記錄
//		UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.harvest_time);
//		UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.Model_c);
//		UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.Model_z);
		finish();
	}
	
	/**
	 * 初始化一個圖片信息
	 * 
	 * @param v
	 * @param index
	 * @param kh	款號
	 * @return
	 */
	private ModelPhoto initPhoto(View v ,int index ,String kh) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String timeStamp = format.format(new Date());
		String path = v.getTag(R.id.tag_first).toString();
		
		File f = new File(path);
		String p = path.substring(0 ,path.indexOf("_")+1) + kh + path.substring(path.lastIndexOf("_"));
		if(!p.equals(path)) {
			f.renameTo(new File(p));
			v.setTag(p);
		}
		path = p;
		
		ModelPhoto photo = new ModelPhoto();
		photo.setId(java.util.UUID.randomUUID().toString());
		photo.setUserid(AppApplication.getInstance().getUserInfo().getId());
		photo.setHasupload(0);
		photo.setIndex1(index);
		photo.setCreatedate(timeStamp);
		if(purchase!=null) 
			photo.setPurchaseid(purchase.getId());
		photo.setMobilepath(path);
		photo.setFilename(path.substring(path.lastIndexOf("/")+1));
		photo.setType(2);
		return photo;
	}
}
