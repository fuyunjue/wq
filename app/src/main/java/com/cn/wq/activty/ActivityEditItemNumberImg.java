package com.cn.wq.activty;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bowin.lib.utils.AlertDialogUtil;
import com.bowin.lib.widget.MyAlertDialog;
import com.cn.wq.R;
import com.cn.wq.entity.ModelSizeInfo;
import com.cn.wq.view.MyLinearLayout;

/**
 * 編輯款號文字信息
 * 
 * @author Terence
 *
 */
public class ActivityEditItemNumberImg extends Activity implements OnClickListener {
	
	private Activity activity;
	private LayoutInflater inflater;
	private ImageView mImageView;
	private View title;
	private MyLinearLayout ll_itemnumbers;
	private String imgPath;
	private MyAlertDialog showAlertDialog;
	
	private List<View> views = new ArrayList<View>();
	private ArrayList<ModelSizeInfo> sizeInfos = null;
	private Integer hasUpload = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_itemnumberimg);
		
		activity = this;
		inflater = LayoutInflater.from(activity);
		findViewById(R.id.tv_back).setOnClickListener(this);
		findViewById(R.id.tv_back).setVisibility(View.VISIBLE);
		
		sizeInfos = (ArrayList<ModelSizeInfo>) getIntent().getSerializableExtra("list");
		hasUpload = getIntent().getIntExtra("hasupload", 0);
		
		if(getIntent().getStringExtra("imgPath")!=null) {
			imgPath = getIntent().getStringExtra("imgPath");
		} else {
			//提示
			
			return;
		}
		
		initView();	
		
		if(hasUpload == 1) {
			//只看
			findViewById(R.id.tv_save).setVisibility(View.GONE);
			findViewById(R.id.tv_add_size).setVisibility(View.INVISIBLE);
			ll_itemnumbers.setB(true);
		}
	}
	
	void initView() {
		findViewById(R.id.tv_save).setOnClickListener(this);
		findViewById(R.id.tv_save).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_add_size).setOnClickListener(this);
		mImageView = (ImageView) findViewById(R.id.iv);
		mImageView.setOnClickListener(this);
		if(imgPath!=null) {
			mImageView.setImageBitmap(BitmapFactory.decodeFile(imgPath));
		}
		title = findViewById(R.id.title_include);
		ll_itemnumbers = (MyLinearLayout) findViewById(R.id.ll_itemnumbers);
		
		if(sizeInfos==null || sizeInfos.size()==0) {
			//初始一个
			addSize(null);
		} else {
			for (ModelSizeInfo sizeInfo : sizeInfos) {
				addSize(sizeInfo);
			}
		}
	}
	
	void addSize(ModelSizeInfo sizeInfo) {
		View view = inflater.inflate(R.layout.layout_item_size, null);
		ll_itemnumbers.addView(view ,views.size());
		TextView tv_delete_item = (TextView) view.findViewById(R.id.tv_delete_item);
		if(views.size() == 0 || hasUpload == 1) {
			tv_delete_item.setVisibility(View.INVISIBLE);
		}
		tv_delete_item.setTag(R.id.tag_first, view);
		tv_delete_item.setOnClickListener(this);
		
		if(sizeInfo != null) {
			((EditText) view.findViewById(R.id.et_item_color)).setText(sizeInfo.getColor());
			((EditText) view.findViewById(R.id.et_item_size)).setText(sizeInfo.getSize());
			((EditText) view.findViewById(R.id.et_item_amount)).setText(sizeInfo.getAmount()+"");
		}

		views.add(view);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_add_size:
			addSize(null);
			break;
		case R.id.tv_delete_item:
			//删除item
			views.remove(v.getTag(R.id.tag_first));
			ll_itemnumbers.removeView((View)v.getTag(R.id.tag_first));
			break;
		case R.id.tv_back:
			//返回
			goBack();
			break;
		case R.id.tv_save:
			save();
			break;
		case R.id.iv:
			Intent newIntent = new Intent(activity ,ActivityImage.class);
			newIntent.putExtra("imgPath", getIntent().getStringExtra("imgPath"));
			activity.startActivity(newIntent);
			break;
		}
	}
	
	private void goBack() {
		finish();
	}
	
	
	void save() {
		ArrayList<ModelSizeInfo> list = new ArrayList<ModelSizeInfo>();
//		ModelStyleNumberPhoto numberPhoto = new ModelStyleNumberPhoto();
//		numberPhoto.setId(java.util.UUID.randomUUID().toString());
		ModelSizeInfo sizeInfo = null;
//		ModelPhoto photo = new ModelPhoto();
//		numberPhoto.setStyleNumberPhoto(photo);
		for (View view : views) {
			if(((EditText) view.findViewById(R.id.et_item_color)).getText().toString().trim().length()==0) {
				//顏色不能為空
				showAlertDialog(activity, getResources().getString(R.string.edit_colors_v2), getResources().getString(R.string.sure), null, null, null);
				return;
			} else if(((EditText) view.findViewById(R.id.et_item_size)).getText().toString().trim().length()==0) {
				showAlertDialog(activity, getResources().getString(R.string.edit_sizes_v2), getResources().getString(R.string.sure), null, null, null);
				return;
			} else if(((EditText) view.findViewById(R.id.et_item_amount)).getText().toString().trim().length()==0) {
				showAlertDialog(activity, getResources().getString(R.string.edit_amount_v2), getResources().getString(R.string.sure), null, null, null);
				return;
			}
			sizeInfo = new ModelSizeInfo();
//			sizeInfo.setStyleNumberPhotoId(numberPhoto.getId());
			sizeInfo.setAmount(Integer.parseInt(((EditText) view.findViewById(R.id.et_item_amount)).getText().toString()));
			sizeInfo.setColor(((EditText) view.findViewById(R.id.et_item_color)).getText().toString());
			sizeInfo.setSize(((EditText) view.findViewById(R.id.et_item_size)).getText().toString());
			list.add(sizeInfo);
		}
//		numberPhoto.setSizeInfos(list);
		
		Intent newIntent = new Intent();
		newIntent.putExtra("list", list);
		newIntent.putExtra("action", sizeInfos==null?"add":"edit");
		setResult(RESULT_OK, newIntent);
		finish();
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
