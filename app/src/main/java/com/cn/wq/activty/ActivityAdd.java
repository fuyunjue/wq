/**
 * 
 */
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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bowin.lib.utils.AlertDialogUtil;
import com.bowin.lib.widget.MyAlertDialog;
import com.cn.wq.R;
import com.cn.wq.adapter.AdapterAutoCompleteTextView;
import com.cn.wq.adapter.AdapterChooseSizesListView;
import com.cn.wq.adapter.AdapterColorsAutoCompleteTextView;
import com.cn.wq.adapter.AdapterSizesAutoCompleteTextView;
import com.cn.wq.adapter.TestArrayAdapter;
import com.cn.wq.app.AppApplication;
import com.cn.wq.database.DataUtils;
import com.cn.wq.database.DbAdapter;
import com.cn.wq.entity.ModelColors;
import com.cn.wq.entity.ModelDetionary;
import com.cn.wq.entity.ModelModels;
import com.cn.wq.entity.ModelOther;
import com.cn.wq.entity.ModelPhoto;
import com.cn.wq.entity.ModelPurchase;
import com.cn.wq.entity.ModelPurchaseList;
import com.cn.wq.entity.ModelSizes;
import com.cn.wq.entity.ModelVendors;
import com.cn.wq.keeper.UserKeeper;
import com.cn.wq.utils.PictureUtil;
import com.cn.wq.utils.Util;
import com.cn.wq.view.MyLinearLayout;
import com.cn.wq.view.MyListView;
import com.cn.wq.view.MySpinner;

/**
 * @Title:  ActivityAdd.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-29 下午3:26:04 
 * @version:  V1.0 
 * 
 */
@SuppressLint({ "NewApi", "ValidFragment" })
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ActivityAdd extends Activity implements OnClickListener {
	private Activity activity;
	private LinearLayout ll;
	private LayoutInflater inflater;
	private AutoCompleteTextView searchkey;
	private EditText et_phone;
	private MyAlertDialog showAlertDialog;
	
	private DbAdapter dbAdapter;
	private boolean add = false;

	private static final int REQUEST = 0x10;	//请求
	
	private EditText et_kh;
	private TextView tv_harvest_time;
	private EditText et_rj;
	private EditText et_remark;	//備註
	private EditText et_deposit;	//定金
	private Button btn_save;
	private ProgressDialog progressDialog;
	private ModelVendors vendors;	//当前选择的门店
	private ModelPurchase purchase;	//传递过来的参数
	
	private ImageView iv_1;
	private ImageView iv_2;
	private ImageView iv_3;
	private ImageView iv_4;
	private ImageView iv_5;
	private ImageView iv_6;
	
	private Spinner spinner_z;	//一級分類
	private Spinner spinner_c;	//二級分類
	private MySpinner spinner_other;
	private TestArrayAdapter cSpinner;
	private TestArrayAdapter zSpinner;
	private Spinner spinner_currency;	//貨幣
	private TestArrayAdapter currencySpinnerAdapter;
	
	private String mCurrentPhotoPath;// 图片路径
	private ImageView mImageView;	//当前选择操作的IMAGEVIEW
	
	private LinearLayout ll_purchase_list;
	private List<View> purchase_list = new ArrayList<View>();
	private HashMap<AutoCompleteTextView, Adapter> adapters = new HashMap<AutoCompleteTextView, Adapter>();
	
	private MyLinearLayout ll_1;

	private String time;
	
	private Dialog mDialog = null;						// 弹窗选择碼數对话框
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		activity = this;
		dbAdapter = DbAdapter.getDbAdapter(activity);
		inflater = LayoutInflater.from(activity);

		purchase = (ModelPurchase) getIntent().getSerializableExtra("purchase");
		
		initView();
	}
	
	void initView() {
		ll_1 = (MyLinearLayout) findViewById(R.id.ll_1);
		findViewById(R.id.tv_back).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_back).setOnClickListener(this);
		ll = (LinearLayout) findViewById(R.id.ll);
		searchkey = (AutoCompleteTextView) findViewById(R.id.searchkey);
		et_phone = (EditText) findViewById(R.id.et_phone);
		

		if(purchase != null) {
			searchkey.setText(purchase.getDesc_chi());
			ModelVendors item = DataUtils.getVendorsByCompany(purchase.getCompany(), dbAdapter);
			if(item!=null){
				et_phone.setText(""+item.getPhone1()==null?(item.getPhone2()==null?(item.getPhone3()==null?"":item.getPhone3()):item.getPhone2()):item.getPhone1());
				vendors = item;
			}
			if(purchase.getHasupload()==1) {
				//不能置焦
				ll_1.setB(true);
			} else {
				initPurchaseAotuCompleteTextView();
			}
		} else {
			initPurchaseAotuCompleteTextView();
		}
		
		addView();
		addListView(0);
	}
	
	void initPurchaseAotuCompleteTextView () {
		final AdapterAutoCompleteTextView adapterAutoCompleteTextView = new AdapterAutoCompleteTextView(activity, 0, new ArrayList<ModelVendors>() ,searchkey);
		searchkey.setAdapter(adapterAutoCompleteTextView);
		///当输入关键字变化时，动态更新建议列表
		searchkey.addTextChangedListener(new TextWatcher() {
			String before = "";
			@Override
			public void afterTextChanged(final Editable arg0) {
				//查询本地数据库数据，显示下拉框选择
				if(arg0.toString().trim().length() > 0 && !before.equals(arg0)) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							final List<ModelVendors> vendorsByKeyword = DataUtils.getVendorsByKeyword(arg0.toString() ,dbAdapter);
							runOnUiThread(new Runnable() {
								public void run() {
									adapterAutoCompleteTextView.clear();
									adapterAutoCompleteTextView.setData(vendorsByKeyword);
									adapterAutoCompleteTextView.notifyDataSetChanged();
								}
							});
						}
					}).start();
				} 
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {
				before = arg0.toString();
			}
			
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {}
		});
		searchkey.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object adapter = arg0.getAdapter();
				if(adapter!=null && adapter instanceof AdapterAutoCompleteTextView) {
					AdapterAutoCompleteTextView adapterAutoCompleteTextView = (AdapterAutoCompleteTextView)adapter;
					ModelVendors item = adapterAutoCompleteTextView.getItem(arg2);
					vendors = item;
					AutoCompleteTextView autoCompleteTextView = adapterAutoCompleteTextView.getmAutoCompleteTextView();
					autoCompleteTextView.setTag(item);
					autoCompleteTextView.setText(item.getDesc_chi());
					et_phone.setText(""+item.getPhone1()==null?(item.getPhone2()==null?(item.getPhone3()==null?"":item.getPhone3()):item.getPhone2()):item.getPhone1());
				}
			}
			
		});
	}
	
	private HashMap<String, String> spinnerHash = new HashMap<String, String>();
	private HashMap<String, String> spinnerHash_c = new HashMap<String, String>();
	private boolean set = false;	//
	
	void addView() {
		View inflate = inflater.inflate(R.layout.layout_item, null);
		et_kh = (EditText) inflate.findViewById(R.id.et_kh);
		et_rj = (EditText) inflate.findViewById(R.id.et_rj);
		spinner_c = (Spinner) inflate.findViewById(R.id.spinner_c);
		spinner_c.setPrompt(getResources().getString(R.string.spinner_models_c));
		spinner_other = (MySpinner) inflate.findViewById(R.id.spinner_other);
		spinner_z = (Spinner) inflate.findViewById(R.id.spinner_z);
		spinner_z.setPrompt(getResources().getString(R.string.spinner_models_z));

		spinner_currency = (Spinner) inflate.findViewById(R.id.spinner_currency);
		spinner_currency.setPrompt(getResources().getString(R.string.spinner_models_currency));
		
		tv_harvest_time = (TextView) inflate.findViewById(R.id.tv_harvest_time);
		if(UserKeeper.getStringValue(activity, UserKeeper.harvest_time)!=null && (UserKeeper.getStringValue(activity, UserKeeper.harvest_time)+"").length()>0) {
			tv_harvest_time.setText(UserKeeper.getStringValue(activity, UserKeeper.harvest_time)+"");
			tv_harvest_time.setTag(UserKeeper.getStringValue(activity, UserKeeper.harvest_time));
		}
		
		tv_harvest_time.setOnClickListener(this);
		et_remark = (EditText) inflate.findViewById(R.id.et_remark);
		et_deposit = (EditText) inflate.findViewById(R.id.et_deposit);
		ll_purchase_list = (LinearLayout) inflate.findViewById(R.id.ll_purchase_list);
		iv_1 = (ImageView) inflate.findViewById(R.id.iv_1);iv_1.setOnClickListener(this);
		iv_2 = (ImageView) inflate.findViewById(R.id.iv_2);iv_2.setOnClickListener(this);
		iv_3 = (ImageView) inflate.findViewById(R.id.iv_3);iv_3.setOnClickListener(this);
		iv_4 = (ImageView) inflate.findViewById(R.id.iv_4);iv_4.setOnClickListener(this);
		iv_5 = (ImageView) inflate.findViewById(R.id.iv_5);iv_5.setOnClickListener(this);
		iv_6 = (ImageView) inflate.findViewById(R.id.iv_6);iv_6.setOnClickListener(this);
		
		btn_save = (Button) inflate.findViewById(R.id.btn_save);
		btn_save.setOnClickListener(this);
		ll.addView(inflate);
		
		et_kh.addTextChangedListener(new TextWatcher() {
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
							et_kh.setText(str+"");
						}
					});
					return;
				}
			}

		});
		
		List<ModelOther> modelOtherByGroup = DataUtils.getModelOtherByGroup(dbAdapter);
		spinner_other.initContent(modelOtherByGroup);

		List<ModelDetionary> detionaryByType = DataUtils.getDetionaryByType("currency", dbAdapter);
		String[] currencyStrs = new String[detionaryByType.size()];
		for (int i=0 ; i<detionaryByType.size() ;i++) {
			currencyStrs[i] = detionaryByType.get(i).getValue();
		} 
		currencySpinnerAdapter = new TestArrayAdapter(activity, currencyStrs);
		spinner_currency.setAdapter(currencySpinnerAdapter);
		
		List<ModelModels> modelsByGroup = DataUtils.getModelsByGroup(dbAdapter);
		String[] zSpinnerStrs = new String[modelsByGroup.size()];
		for (int i=0 ; i<modelsByGroup.size() ;i++) {
			zSpinnerStrs[i] = modelsByGroup.get(i).getDesc_chi();
			spinnerHash.put(modelsByGroup.get(i).getDesc_chi(), modelsByGroup.get(i).getModel());
		} 
		zSpinner = new TestArrayAdapter(activity, zSpinnerStrs);
		spinner_z.setAdapter(zSpinner);
		
		spinner_z.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object item2 = arg0.getAdapter().getItem(arg2);
				if(item2!=null && !set) {
					final List<ModelModels> modelsByModel = DataUtils.getModelsByModel(spinnerHash.get(item2), dbAdapter);
					spinnerHash_c.clear();
					if(modelsByModel.size()==0) {
						runOnUiThread(new Runnable() {
							public void run() {
								spinner_c.setVisibility(View.INVISIBLE);
							}
						});
					} else {
						runOnUiThread(new Runnable() {
							public void run() {
								spinner_c.setVisibility(View.VISIBLE);
								String[] cSpinnerStrs = new String[modelsByModel.size()];
								for (int i=0 ; i<modelsByModel.size() ;i++) {
									cSpinnerStrs[i] = modelsByModel.get(i).getDesc_chi();
									spinnerHash_c.put(modelsByModel.get(i).getDesc_chi(),  modelsByModel.get(i).getModel());
								} 
								cSpinner = new TestArrayAdapter(activity,cSpinnerStrs);
								spinner_c.setAdapter(cSpinner);
							}
						});
					}
				} else {
					set = false;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		
		
		if(purchase != null) {
			et_kh.setText(purchase.getItemnumber());
			et_rj.setText(purchase.getPrice()+"");
			if(purchase.getHarvest_time().length()>0) {
				tv_harvest_time.setText(purchase.getHarvest_time());
				UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.harvest_time, purchase.getHarvest_time()+"");
				tv_harvest_time.setTag(purchase.getHarvest_time());
			}
			et_deposit.setText(purchase.getDeposit()==0?"":purchase.getDeposit()+"");
			et_remark.setText(purchase.getRemark()+"");
			spinner_other.setText(purchase.getOthers(), null);
			
			int size = modelsByGroup.size();
			for (int i = 0; i < size; i++) {
				if(purchase.getModel_z().equals(modelsByGroup.get(i).getModel())) {
					spinner_z.setSelection(i);
					break;
				}
			}
			
			if(purchase.getCurrency().length()>0) {
				for (int i = 0; i < currencyStrs.length; i++) {
					if(currencyStrs[i].equals(purchase.getCurrency())) {
						spinner_currency.setSelection(i);
						break;
					}
				}
			}

			List<ModelModels> modelsByModel = DataUtils.getModelsByModel(purchase.getModel_z(), dbAdapter);
			final String[] cSpinnerStrs = new String[modelsByModel.size()];
			spinnerHash_c.clear();
			for (int i=0 ; i<modelsByModel.size() ;i++) {
				cSpinnerStrs[i] = modelsByModel.get(i).getDesc_chi();
				spinnerHash_c.put(modelsByModel.get(i).getDesc_chi(),  modelsByModel.get(i).getModel());
			}
			cSpinner = new TestArrayAdapter(activity ,cSpinnerStrs);
			spinner_c.setAdapter(cSpinner);
			int size1 = modelsByModel.size();
			for (int i = 0; i < size1; i++) {
				if(purchase.getModel_c().equals(modelsByModel.get(i).getModel())) {
					spinner_c.setSelection(i);
					set = true;
					break;
				}
			}
			
			if(purchase.getHasupload()==1) {
				//不能置焦
				((MyLinearLayout)inflate.findViewById(R.id.ll_2)).setB(true);
				((MyLinearLayout)inflate.findViewById(R.id.ll_4)).setB(true);
			}
			List<ModelPhoto> pics = purchase.getPics();
			boolean a = false;
			for (ModelPhoto modelPhoto : pics) {
				if(modelPhoto.getHasupload()==1) {
					a = true;
				}
				switch (modelPhoto.getIndex1()) {
				case 1:
					iv_1.setImageBitmap(PictureUtil.getSmallBitmap(modelPhoto.getMobilepath()));
					iv_1.setTag(modelPhoto.getMobilepath());
					break;
				case 2:
					iv_2.setImageBitmap(PictureUtil.getSmallBitmap(modelPhoto.getMobilepath()));
					iv_2.setTag(modelPhoto.getMobilepath());
					break;
				case 3:
					iv_3.setImageBitmap(PictureUtil.getSmallBitmap(modelPhoto.getMobilepath()));
					iv_3.setTag(modelPhoto.getMobilepath());
					break;
				case 4:
					iv_4.setImageBitmap(PictureUtil.getSmallBitmap(modelPhoto.getMobilepath()));
					iv_4.setTag(modelPhoto.getMobilepath());
					break;
				case 5:
					iv_5.setImageBitmap(PictureUtil.getSmallBitmap(modelPhoto.getMobilepath()));
					iv_5.setTag(modelPhoto.getMobilepath());
					break;
				case 6:
					iv_6.setImageBitmap(PictureUtil.getSmallBitmap(modelPhoto.getMobilepath()));
					iv_6.setTag(modelPhoto.getMobilepath());
					break;
				};
			}
			if(a) {
				//图片也不可编辑
				((MyLinearLayout)inflate.findViewById(R.id.ll_3)).setB(true);
				if(purchase.getHasupload()==1) {
					btn_save.setVisibility(View.GONE);
				}
			}
		} else {
			//默認選項，在這裡處理
			if(UserKeeper.getStringValue(activity, UserKeeper.CURRENCY)!=null) {
				for (int i = 0; i < currencyStrs.length; i++) {
					if(currencyStrs[i].equals(UserKeeper.getStringValue(activity, UserKeeper.CURRENCY))) {
						spinner_currency.setSelection(i);
						break;
					}
				}
			}
			if(UserKeeper.getStringValue(activity, UserKeeper.Model_z)!=null) {
				for (int i = 0; i < modelsByGroup.size(); i++) {
					if(modelsByGroup.get(i).getModel().equals(UserKeeper.getStringValue(activity, UserKeeper.Model_z))) {
						spinner_z.setSelection(i);
						break;
					}
				}
				
				List<ModelModels> modelsByModel = DataUtils.getModelsByModel(UserKeeper.getStringValue(activity, UserKeeper.Model_z), dbAdapter);
				final String[] cSpinnerStrs = new String[modelsByModel.size()];
				spinnerHash_c.clear();
				for (int i=0 ; i<modelsByModel.size() ;i++) {
					cSpinnerStrs[i] = modelsByModel.get(i).getDesc_chi();
					spinnerHash_c.put(modelsByModel.get(i).getDesc_chi(),  modelsByModel.get(i).getModel());
				}
				cSpinner = new TestArrayAdapter(activity ,cSpinnerStrs);
				spinner_c.setAdapter(cSpinner);
				int size1 = modelsByModel.size();
				for (int i = 0; i < size1; i++) {
					if(UserKeeper.getStringValue(activity, UserKeeper.Model_c).equals(modelsByModel.get(i).getModel())) {
						spinner_c.setSelection(i);
						set = true;
						break;
					}
				}
			}
		}
	}
	
	void addListView(int index) {
		if(index==1) {
			View inflate = inflater.inflate(R.layout.layout_purchase_item, null);
			initView(inflate);
			purchase_list.add(inflate);
			ll_purchase_list.addView(inflate);
		} else {
			if(purchase != null && purchase.getHasupload()==1) {
				List<ModelPurchaseList> purchaseLists = purchase.getPurchaseLists();
				for (ModelPurchaseList modelPurchaseList : purchaseLists) {
					View inflate = inflater.inflate(R.layout.layout_purchase_item, null);
					((AutoCompleteTextView) inflate.findViewById(R.id.searchkey_color)).setText(modelPurchaseList.getColor());
					((AutoCompleteTextView) inflate.findViewById(R.id.searchkey_sizes)).setText(modelPurchaseList.getSizes());
					((EditText) inflate.findViewById(R.id.et_sl)).setText(modelPurchaseList.getAmount()+"");
					//不能置焦
					inflate.findViewById(R.id.tv_add).setVisibility(View.GONE);
					inflate.findViewById(R.id.tv_del).setVisibility(View.GONE);
					ll_purchase_list.addView(inflate);
				}
			} else {
				if(purchase != null) {
					List<ModelPurchaseList> purchaseLists = purchase.getPurchaseLists();
					int j=0;
					for (ModelPurchaseList modelPurchaseList : purchaseLists) {
						View inflate = inflater.inflate(R.layout.layout_purchase_item, null);
						ModelColors colorsById = DataUtils.getColorsById(modelPurchaseList.getColorid(), dbAdapter);
						if(colorsById!=null) 
							inflate.findViewById(R.id.searchkey_color).setTag(colorsById);
						ModelSizes modelSizes = DataUtils.getSizesById(modelPurchaseList.getSizesid(), dbAdapter);
						if(modelSizes!=null) 
							inflate.findViewById(R.id.searchkey_sizes).setTag(modelSizes);
						((AutoCompleteTextView) inflate.findViewById(R.id.searchkey_color)).setText(modelPurchaseList.getColor());
						((AutoCompleteTextView) inflate.findViewById(R.id.searchkey_sizes)).setText(modelPurchaseList.getSizes());
						((EditText) inflate.findViewById(R.id.et_sl)).setText(modelPurchaseList.getAmount()+"");
						if(j!=2) {
							inflate.findViewById(R.id.tv_add).setVisibility(View.GONE);
							inflate.findViewById(R.id.tv_del).setVisibility(View.GONE);
						}
						initView(inflate);
						purchase_list.add(inflate);
						ll_purchase_list.addView(inflate);
						j++;
					}
				}
				if(purchase_list.size()<3) {
					for (int i = 0; i < 3-purchase_list.size();) {
						View inflate = inflater.inflate(R.layout.layout_purchase_item, null);
						initView(inflate);
						purchase_list.add(inflate);
						ll_purchase_list.addView(inflate);
						if(purchase_list.size()!=3) {
							inflate.findViewById(R.id.tv_add).setVisibility(View.GONE);
							inflate.findViewById(R.id.tv_del).setVisibility(View.GONE);
						}
					}
				}
			}
		}
	}

	/**
	 * @param inflate
	 */
	private void initView(View inflate) {
		AutoCompleteTextView searchkey_sizes = (AutoCompleteTextView) inflate.findViewById(R.id.searchkey_sizes);
		searchkey_sizes.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus) {
					current_searchkey_sizes = (AutoCompleteTextView) v;
					showDialogWindow();
				}
			}
		});
		
		AutoCompleteTextView searchkey_color = (AutoCompleteTextView) inflate.findViewById(R.id.searchkey_color);
		AdapterColorsAutoCompleteTextView adapterColorsAutoCompleteTextView = new AdapterColorsAutoCompleteTextView(activity, 0, new ArrayList<ModelColors>() ,searchkey_color ,inflate);
		searchkey_color.setAdapter(adapterColorsAutoCompleteTextView);
		adapters.put(searchkey_color, adapterColorsAutoCompleteTextView);
		searchkey_color.addTextChangedListener(new l2(searchkey_color));
		searchkey_color.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Object adapter = arg0.getAdapter();
				if(adapter!=null && adapter instanceof AdapterColorsAutoCompleteTextView) {
					AdapterColorsAutoCompleteTextView adapterColorsAutoCompleteTextView = (AdapterColorsAutoCompleteTextView)adapter;
					ModelColors itemColors = adapterColorsAutoCompleteTextView.getItem(arg2);
					final AutoCompleteTextView autoCompleteTextView = adapterColorsAutoCompleteTextView.getmAutoCompleteTextView();
					View parentView = adapterColorsAutoCompleteTextView.getParentView();
					Object tagSizes = parentView.findViewById(R.id.searchkey_sizes).getTag();
					if(tagSizes != null) {
						ModelSizes modelSizes = (ModelSizes) tagSizes;
						for (View view : purchase_list) {
							Object tagColor = view.findViewById(R.id.searchkey_color).getTag();
							if(tagColor!=null && tagColor instanceof ModelColors) {
								ModelColors colors = (ModelColors) tagColor;
								if(colors.getId()==itemColors.getId()) {
									Object tagSize = view.findViewById(R.id.searchkey_sizes).getTag();
									if(tagSize!=null && tagSize instanceof ModelSizes) {
										ModelSizes sizes = (ModelSizes) tagSize;
										if(sizes.getId()==modelSizes.getId()) {
											runOnUiThread(new Runnable() {
												public void run() {
													showAlertDialog(activity, getResources().getString(R.string.colors_exits), getResources().getString(R.string.sure), new OnClickListener() {
														
														@Override
														public void onClick(View v) {
															if(showAlertDialog!=null) showAlertDialog.dismiss();
														}
													}, null ,null);
													autoCompleteTextView.setText("");
													autoCompleteTextView.setTag(null);
												}
											});
											return;
										}
									}
								}
							}
						}
					}
					autoCompleteTextView.setTag(itemColors);
					autoCompleteTextView.setText(itemColors.getColor());
				}
			}
		});
		
		TextView tv_add = (TextView) inflate.findViewById(R.id.tv_add);
		TextView tv_del = (TextView) inflate.findViewById(R.id.tv_del);
		tv_add.setTag(inflate);
		tv_add.setOnClickListener(this);
		tv_del.setTag(inflate);
		tv_del.setOnClickListener(this);
	}
	

	/**
	 * 
	 * @Title:  ActivityAdd.java
	 * 
	 * @Description:  TODO<请描述此文件是做什么的>
	 *
	 * @author Terence
	 * @data:  2016-1-8 上午12:19:40 
	 * @version:  V1.0 
	 *
	 */
	class l2 implements TextWatcher {
		private AutoCompleteTextView v;
		private String before = "";
		public l2(AutoCompleteTextView v) {
			this.v = v;
		}
		
		@Override
		public void afterTextChanged(final Editable arg0) {
			//查询本地数据库数据，显示下拉框选择
			if(arg0.toString().trim().length() > 0 && !arg0.equals(before)) {
				before = arg0.toString();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							public void run() {
								Adapter adapter = adapters.get(v);
								if(adapter instanceof AdapterSizesAutoCompleteTextView) {
//									final List<ModelSizes> sizesByKeyword = DataUtils.getSizesByKeyword(arg0.toString() ,dbAdapter);
//									AdapterSizesAutoCompleteTextView adapterSizesAutoCompleteTextView = (AdapterSizesAutoCompleteTextView) adapter;
//									adapterSizesAutoCompleteTextView.clear();
//									adapterSizesAutoCompleteTextView.setData(sizesByKeyword);
//									adapterSizesAutoCompleteTextView.notifyDataSetChanged();
								} else if(adapter instanceof AdapterColorsAutoCompleteTextView) {
									final List<ModelColors> colorsByKeyword = DataUtils.getColorsByKeyword(arg0.toString() ,dbAdapter);
									if(colorsByKeyword.size()==1) {
										//只有一條，直接選擇，不需讓用戶再點
										v.removeTextChangedListener(l2.this);
										v.setTag(colorsByKeyword.get(0));
										v.setText(colorsByKeyword.get(0).getColor());
										before = colorsByKeyword.get(0).getColor();
										v.setSelection(before.length());
										v.clearFocus();
										((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ActivityAdd.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
										v.addTextChangedListener(new l2(v));
									} 
//									else {
										AdapterColorsAutoCompleteTextView adapterColorsAutoCompleteTextView = (AdapterColorsAutoCompleteTextView) adapter;
										adapterColorsAutoCompleteTextView.clear();
										adapterColorsAutoCompleteTextView.setData(colorsByKeyword);
										adapterColorsAutoCompleteTextView.notifyDataSetChanged();
//									}
								}
							}
						});
					}
				}).start();
			} else {
				v.setTag(null);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			before = s.toString();
		}

		@SuppressWarnings("unused")
		@Override
		public void onTextChanged(final CharSequence arg0, int arg1, int arg2, int arg3) {}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void goBack() {
		if(add)
			setResult(RESULT_OK);
		//清空分類、次分類、收貨日期本地記錄
		UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.harvest_time);
		UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.Model_c);
		UserKeeper.removeSharepreferenceByKey(activity, UserKeeper.Model_z);
		finish();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_harvest_time:
			time = "";
			DatePickerFragment datePicker = new DatePickerFragment();  
		    datePicker.show(getFragmentManager(), "datePicker");  
			break;
		case R.id.tv_back:
			goBack();
			break;
		case R.id.tv_add:
			View i = (View)v.getTag();
			i.findViewById(R.id.tv_add).setVisibility(View.GONE);
			i.findViewById(R.id.tv_del).setVisibility(View.VISIBLE);
			addListView(1);
			break;
		case R.id.tv_del:
			View item = (View)v.getTag();
			purchase_list.remove(item);
			ll_purchase_list.removeView(item);
			int size = purchase_list.size();
			for (int j = 0; j < size; j++) {
				View view = purchase_list.get(j);
				if(size==3 && j==3) {
					view.findViewById(R.id.tv_add).setVisibility(View.VISIBLE);
					view.findViewById(R.id.tv_del).setVisibility(View.GONE);
				} else if(size>=3) {
					if(j==0 || j==1) {
						view.findViewById(R.id.tv_add).setVisibility(View.GONE);
						view.findViewById(R.id.tv_del).setVisibility(View.GONE);
					} else if(j==size-1) {
						view.findViewById(R.id.tv_add).setVisibility(View.VISIBLE);
						view.findViewById(R.id.tv_del).setVisibility(View.GONE);
					} else {
						view.findViewById(R.id.tv_add).setVisibility(View.GONE);
						view.findViewById(R.id.tv_del).setVisibility(View.VISIBLE);
					}
				}
			}
			break;
		case R.id.btn_save:
			doSaveSqlite();
			break;
		case R.id.iv_1:
			goSelectPic(iv_1 ,1);  
			break;
		case R.id.iv_2:
			goSelectPic(iv_2 ,2);  
			break;
		case R.id.iv_3:
			goSelectPic(iv_3 ,3);   
			break;
		case R.id.iv_4:
			goSelectPic(iv_4 ,4);   
			break;
		case R.id.iv_5:
			goSelectPic(iv_5 ,5);   
			break;
		case R.id.iv_6:
			goSelectPic(iv_6 ,6);   
			break;
		case R.id.ll_delete_1:
			iv_1.setBackgroundResource(R.drawable.collect_add);
			iv_1.setTag(null);
			break;
		case R.id.ll_delete_2:
			iv_2.setBackgroundResource(R.drawable.collect_add);
			iv_2.setTag(null);
			break;
		case R.id.ll_delete_3:
			iv_3.setBackgroundResource(R.drawable.collect_add);
			iv_3.setTag(null);
			break;
		}
	}

	/**
	 * 保存到本地數據庫
	 */
	private void doSaveSqlite() {
		if(purchase!=null && purchase.getHasupload()==1) {
			//此时只能更改图片
			List<ModelPhoto> photos = new ArrayList<ModelPhoto>();
			if(iv_1.getTag() != null) 
				photos.add(initPhoto(iv_1 ,1));
			if(iv_2.getTag() != null)
				photos.add(initPhoto(iv_2 ,2));
			if(iv_3.getTag() != null)
				photos.add(initPhoto(iv_3 ,3));
			if(iv_4.getTag() != null)
				photos.add(initPhoto(iv_4 ,4));
			if(iv_5.getTag() != null)
				photos.add(initPhoto(iv_5 ,5));
			if(iv_6.getTag() != null)
				photos.add(initPhoto(iv_6 ,6));
			if(photos.size()==0) {
				showAlertDialog(activity, getResources().getString(R.string.choose_photo), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			//执行保存图片
			boolean insertPhotos = DataUtils.insertPhotos(photos, purchase.getId(), dbAdapter);
			if(insertPhotos) {
				insertSuccess();
			}
		} else {
			if(vendors==null) {
				showAlertDialog(activity, getResources().getString(R.string.choose_vendors), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			if (et_kh.getText().toString().trim().isEmpty()) {
				showAlertDialog(activity, getResources().getString(R.string.edit_kh), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			//正則表達式過濾款號
			if(!Pattern.matches("[0-9a-zA-Z-=]+", et_kh.getText().toString().trim())) {
				showAlertDialog(activity, getResources().getString(R.string.edit_kh_matches), getResources().getString(R.string.sure), null, null, null);
				return;
			}
//			if (et_rj.getText().toString().trim().isEmpty()) {
//				showAlertDialog(activity, getResources().getString(R.string.edit_rj), getResources().getString(R.string.sure), null, null, null);
//				return;
//			}
			List<ModelPurchaseList> purchaseLists = new ArrayList<ModelPurchaseList>();
			ModelPurchaseList purchaseList = null;
			for (View color : purchase_list) {
				if(color.findViewById(R.id.searchkey_color).getTag() == null ) {
					if(color.findViewById(R.id.searchkey_sizes).getTag() == null ) {
						if (((EditText)color.findViewById(R.id.et_sl)).getText().toString().trim().length() == 0){
							continue;
						} else {
							showAlertDialog(activity, getResources().getString(R.string.edit_colors), getResources().getString(R.string.sure), null, null, null);
							return;
						}
					} else {
						showAlertDialog(activity, getResources().getString(R.string.edit_colors), getResources().getString(R.string.sure), null, null, null);
						return;
					}
				} else {
					if(color.findViewById(R.id.searchkey_sizes).getTag() == null ) {
						showAlertDialog(activity, getResources().getString(R.string.edit_sizes), getResources().getString(R.string.sure), null, null, null);
						return;
					} else {
						if (((EditText)color.findViewById(R.id.et_sl)).getText().toString().trim().length() == 0){
							showAlertDialog(activity, getResources().getString(R.string.edit_sl), getResources().getString(R.string.sure), null, null, null);
							return;
						} 
					}
				}
				purchaseList = new ModelPurchaseList();
//					purchaseList.setPurchaseid(purchaseid)
				purchaseList.setId(java.util.UUID.randomUUID().toString());
				purchaseList.setSizesid(((ModelSizes)color.findViewById(R.id.searchkey_sizes).getTag()).getId());
				purchaseList.setSizes(((ModelSizes)color.findViewById(R.id.searchkey_sizes).getTag()).getSizes());
				purchaseList.setColorid(((ModelColors)color.findViewById(R.id.searchkey_color).getTag()).getId());
				purchaseList.setColor(((ModelColors)color.findViewById(R.id.searchkey_color).getTag()).getColor());
				purchaseList.setAmount(Integer.parseInt(((EditText)color.findViewById(R.id.et_sl)).getText().toString()));
				purchaseLists.add(purchaseList);
			}
			if(purchaseLists.size()==0) {
				showAlertDialog(activity, getResources().getString(R.string.add_colors), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			
			List<ModelPhoto> photos = new ArrayList<ModelPhoto>();
			if(iv_1.getTag() != null) 
				photos.add(initPhoto(iv_1 ,1));
			if(iv_2.getTag() != null)
				photos.add(initPhoto(iv_2 ,2));
			if(iv_3.getTag() != null)
				photos.add(initPhoto(iv_3 ,3));
			if(iv_4.getTag() != null)
				photos.add(initPhoto(iv_4 ,4));
			if(iv_5.getTag() != null)
				photos.add(initPhoto(iv_5 ,5));
			if(iv_6.getTag() != null)
				photos.add(initPhoto(iv_6 ,6));
			if(photos.size()==0) {
				showAlertDialog(activity, getResources().getString(R.string.choose_photo), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			
			if(purchase!=null && purchase.getHasupload()==0) {
				
			} else {
				purchase = new ModelPurchase();
				purchase.setId(java.util.UUID.randomUUID().toString());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				purchase.setCreatedate(format.format(new Date()));
			}
			
			purchase.setOthers(spinner_other.getTag()+"");
			purchase.setDesc_chi(vendors.getDesc_chi());
			purchase.setDesc_end(vendors.getDesc_end());
			purchase.setCompany(vendors.getCompany());
			if(spinner_z.getSelectedItem()==null) {
				//未選擇主分類
				showAlertDialog(activity, getResources().getString(R.string.choose_models_z), getResources().getString(R.string.sure), null, null, null);
				return;
			} else {
				purchase.setModel_z(spinnerHash.get(spinner_z.getSelectedItem()));
				UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.Model_z, spinnerHash.get(spinner_z.getSelectedItem())+"");
			}
			if(spinner_c.isShown() && spinner_c.getSelectedItem()==null) {
				//未選擇次分類
				showAlertDialog(activity, getResources().getString(R.string.choose_models_c), getResources().getString(R.string.sure), null, null, null);
				return;
			} else {
				purchase.setModel_c(spinnerHash_c.get(spinner_c.getSelectedItem()));
				UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.Model_c, spinnerHash_c.get(spinner_c.getSelectedItem())+"");
			}
//			if(tv_harvest_time.getTag()==null) {
//				showAlertDialog(activity, getResources().getString(R.string.et_harvest_time_hint), getResources().getString(R.string.sure), null, null, null);
//				return;
//			}
			purchase.setHasupload(0);
			purchase.setCurrency(spinner_currency.getSelectedItem()+"");
			UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.CURRENCY, spinner_currency.getSelectedItem()+"");
			purchase.setItemnumber(et_kh.getText().toString().trim());
			purchase.setPrice(Double.parseDouble(et_rj.getText().toString().trim().length()==0?"0":et_rj.getText().toString().trim()));
			
			purchase.setHarvest_time(tv_harvest_time.getTag()==null?"":tv_harvest_time.getText().toString().trim());
			if(tv_harvest_time.getTag()!=null)
				UserKeeper.SaveSharepreferenceByKey(activity, UserKeeper.harvest_time, tv_harvest_time.getText().toString().trim());
			
			purchase.setDeposit(et_deposit.getText().toString().trim().length()==0?0:Double.parseDouble(et_deposit.getText().toString().trim()));
			purchase.setRemark(et_remark.getText().toString()+"");
			purchase.setUserid(AppApplication.getInstance().getUserInfo().getId());
			purchase.setPurchaseLists(purchaseLists);
			purchase.setPics(photos);
			
			/**
			 * 判斷入价是否為0，預計收貨日期是否為空，當符合其中一個條件時，state狀態為0（為完成，不能執行提交），都不滿足條件時state為1，可以執行提交動作
			 */
			if(purchase.getPrice()==0 || purchase.getHarvest_time()==null || purchase.getHarvest_time().trim().length()==0) {
				purchase.setState(0);
			} else {
				purchase.setState(1);
			}
			
			progressDialog = Util.getProgressDialog(activity, true, false);

			((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ActivityAdd.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			//按先后顺序保存一条商品信息
			boolean insertGoods = DataUtils.insertPurchase(purchase ,dbAdapter);
			if(insertGoods) {
				insertSuccess();
			} else {
				if(progressDialog != null) 
					progressDialog.cancel();
				showAlertDialog(activity, getResources().getString(R.string.czsb), getResources().getString(R.string.sure), null, null, null);
			}
		}
	}

	/**
	 * 
	 */
	private void goSelectPic(ImageView iv ,int index) {
		if(vendors==null) {
			showAlertDialog(activity, getResources().getString(R.string.choose_vendors), getResources().getString(R.string.sure), null, null, null);
			return;
		}
		if(et_kh.getText().toString().trim().length()==0) {
			showAlertDialog(activity, getResources().getString(R.string.edit_kh), getResources().getString(R.string.sure), null, null, null);
			return;
		}
		mImageView = iv;
		startActivityForResult(new Intent(activity, SelectPicPopupWindow.class), REQUEST);
	}

	/**
	 * @param modelPurchase
	 */
	private ModelPhoto initPhoto(View v ,int index) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String timeStamp = format.format(new Date());
		String path = v.getTag().toString();
		
		File f = new File(path);
		String p = path.substring(0 ,path.indexOf("_")+1) + et_kh.getText().toString() + path.substring(path.lastIndexOf("_"));
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
		return photo;
	}
	
	/**
	 * 拍照
	 */
	private void takePhoto() {
		try {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// 指定存放拍摄照片的位置
			File f = createImageFile();
			takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			startActivityForResult(takePictureIntent, SelectPicPopupWindow.REQUEST_TAKE_PHOTO_REQUEST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 把程序拍摄的照片放到 SD卡的 Pictures目录中 pw 文件夹中，根据门店ID保存图片
	 * 照片的命名规则为：sheqing_20130125_173729.jpg
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmSSS");
		String timeStamp = format.format(new Date());
		String imageFileName = vendors.getCompany()+ "_" + et_kh.getText().toString().trim() +"_" + timeStamp + ".jpg";
		File image = new File(PictureUtil.getAlbumDir(), imageFileName);
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	void insertSuccess() {
		try {
			add = true;
			purchase = null;
			clearView();
			mCurrentPhotoPath = null;// 图片路径
			mImageView = null;	//当前选择操作的IMAGEVIEW
			adapters.clear();
			purchase_list.clear();
			addView();
			addListView(0);
			if(progressDialog != null) 
				progressDialog.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void clearView() {
		ll.removeAllViews();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == REQUEST && resultCode == SelectPicPopupWindow.REQUEST_TAKE_PHOTO) {
			//启动照相机
			takePhoto();
		} else if(requestCode == REQUEST && resultCode == SelectPicPopupWindow.REQUEST_PICK_PHOTO) {
			//从相册选择
			PictureUtil.pickPhote(activity);
		} else if(requestCode == SelectPicPopupWindow.REQUEST_TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
			//拍照返回结果
			// 添加到图库,这样可以在手机的图库程序中看到程序拍摄的照片
			PictureUtil.galleryAddPic(this, mCurrentPhotoPath);
			//先删除原来添加的文件
			if(mImageView.getTag()!=null) {
				String path = mImageView.getTag().toString();
				File f = new File(path);
				if(f.exists() && f.isFile()) 
					f.delete();
			}
			mImageView.setImageBitmap(PictureUtil.getSmallBitmap(mCurrentPhotoPath));
			mImageView.setTag(mCurrentPhotoPath);
		} else if(requestCode == SelectPicPopupWindow.REQUEST_PICK_PHOTO_REQUEST && resultCode == RESULT_OK){
			//相册选择图片返回
			try {
	            Uri originalUri = intent.getData();        //获得图片的uri 
	            //获取图片的路径
	            String[] proj = {MediaStore.Images.Media.DATA};
	            Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	            cursor.moveToFirst();
	            String string = cursor.getString(column_index);
	            
	            createImageFile();
	            
	            if(mImageView.getTag()!=null) {
		            //先删除原来添加的文件
					String path = mImageView.getTag().toString();
					File f = new File(path);
					if(f.exists() && f.isFile()) 
						f.delete();
	            }
				//复制文件
	            PictureUtil.fileChannelCopy(new File(string), new File(mCurrentPhotoPath));
	            PictureUtil.galleryAddPic(this, mCurrentPhotoPath);
				mImageView.setImageBitmap(PictureUtil.getSmallBitmap(mCurrentPhotoPath));
				mImageView.setTag(mCurrentPhotoPath);
	        }catch (Exception e) {
	        	e.printStackTrace();
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
			tv_harvest_time.setTag(time);
		}  
	}  
	
	private EditText dialog_choose_et_size_key;
	private MyListView lv_choice_sizes;
	private View more = null;	// 加载更多
	private TextView LoadText = null;				// 加载更多_文本框
	private ProgressBar LoadBar = null;			// 加载更多_进度条
//	private LinearLayout ll_ProgressBar = null;	// 读取数据进度条
	private AdapterChooseSizesListView chooseSizesListView;

	private List<ModelSizes> sizesList = new ArrayList<ModelSizes>();
	private AutoCompleteTextView current_searchkey_sizes;
	private static final int PAGE_SIZE = 10;	//每次加載10條
	
	/**
	 * 弹窗选择号码
	 */
	void showDialogWindow() {
		mDialog = new Dialog(this);
		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(sizesList!=null){
					sizesList.clear();
					chooseSizesListView = null;
				}
			}
		});
		mDialog.setContentView(R.layout.dialog_choice_sizes);
		mDialog.setTitle(getResources().getString(R.string.dialog_choose_sizes));
		mDialog.hide();
		
		dialog_choose_et_size_key =(EditText) mDialog.findViewById(R.id.dialog_choose_et_size_key);
		dialog_choose_et_size_key.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(sizesList!=null)
					sizesList.clear();
				loadSizes(s.toString());
			}
		});
		
		lv_choice_sizes = (MyListView) mDialog.findViewById(R.id.lv_choice_sizes);
		
		more = getLayoutInflater().inflate(R.layout.load, null);
		LoadText = (TextView) more.findViewById(R.id.LoadText);
		LoadText.setTextColor(getResources().getColor(R.color.black_333333));
		LoadBar = (ProgressBar) more.findViewById(R.id.LoadBar);
		lv_choice_sizes.addFooterView(more);
		
		//第一次初始化加載數據
		loadSizes("");
		
		lv_choice_sizes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if(chooseSizesListView.getCount() > arg2) {
						ModelSizes item = chooseSizesListView.getItem(arg2);
						current_searchkey_sizes.setTag(item);
						current_searchkey_sizes.setText(item.getSizes());
						current_searchkey_sizes.clearFocus();
						for (int i = 0; i < purchase_list.size(); i++) {
							View view = purchase_list.get(i);
							Object tagSize = view.findViewById(R.id.searchkey_sizes).getTag();
							Object tagColor = view.findViewById(R.id.searchkey_color).getTag();
							if(tagSize!=null && tagSize instanceof ModelSizes && tagColor!=null && tagColor instanceof ModelColors) {
								//只有當顏色和碼數都選擇了，才會進行對比動作
								ModelSizes sizes = (ModelSizes) tagSize;
								ModelColors colors = (ModelColors) tagColor;
								for (int j = i+1; j < purchase_list.size(); j++) {
									View view1 = purchase_list.get(j);
									Object tagSize1 = view1.findViewById(R.id.searchkey_sizes).getTag();
									Object tagColor1 = view1.findViewById(R.id.searchkey_color).getTag();
									if(tagSize1!=null && tagSize1 instanceof ModelSizes && tagColor1!=null && tagColor1 instanceof ModelColors) {
										ModelSizes sizes1 = (ModelSizes) tagSize1;
										ModelColors colors1 = (ModelColors) tagColor1;
										if(sizes.getId()==sizes1.getId() && colors1.getId()==colors.getId()) {
											//存在相同的
											runOnUiThread(new Runnable() {
												public void run() {
													showAlertDialog(activity, getResources().getString(R.string.colors_exits), getResources().getString(R.string.sure), null, null, null);
													current_searchkey_sizes.setText("");
													current_searchkey_sizes.setTag(null);
													mDialog.dismiss();
												}
											});
											return;
										}
									}
								
								}
							} else continue;
					
						}
						mDialog.dismiss();
					} else {
						if (LoadText.getText().toString().equals(getResources().getString(R.string.not_more_data))) {
							LoadBar.setVisibility(View.GONE);
						} else {
							//加載下一頁
							loadSizes(dialog_choose_et_size_key.getText().toString());
						}
					}
			}
		});
		
		mDialog.show();
	}
	void loadSizes(String keyword) {
		final List<ModelSizes> sizesByKeyword = DataUtils.getSizesByKeyword(keyword ,PAGE_SIZE ,sizesList.size() ,dbAdapter);
		if(sizesByKeyword.size()==0) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					LoadText.setText(getResources().getString(R.string.not_more_data));
					LoadBar.setVisibility(View.GONE);
				}
			});
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					LoadText.setText(getResources().getString(R.string.load_more_data));
					LoadBar.setVisibility(View.GONE);
					sizesList.addAll(sizesByKeyword);
					if(chooseSizesListView==null) {
						chooseSizesListView = new AdapterChooseSizesListView(activity, 10, 0, sizesList);
						if(lv_choice_sizes!=null) lv_choice_sizes.setAdapter(chooseSizesListView);
					} 
					if(chooseSizesListView!=null) chooseSizesListView.notifyDataSetChanged();
				}
			});
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
}