package com.cn.wq.activty;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.cn.wq.R;

/**
 * 
 * @Title:  SelectPicPopupWindow.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-30 下午4:28:56 
 * @version:  V1.0 
 *
 */
public class SelectPicPopupWindow extends Activity implements OnClickListener {

	private Button btn_take_photo, btn_pick_photo, btn_cancel ,btn_show;
//	private LinearLayout layout;
	public static final int REQUEST_TAKE_PHOTO = 0x90;
	public static final int REQUEST_PICK_PHOTO = 0x91;
	public static final int REQUEST_TAKE_PHOTO_REQUEST = 0x92;
	public static final int REQUEST_PICK_PHOTO_REQUEST = 0x93;
	public static final int REQUEST_SHOW_COMPANY = 0x94;	//查看檔口圖片
	public static final int REQUEST_SHOW_ITEMNUMBER = 0x95;	//查看碼數信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_pop);
		btn_take_photo = (Button) this.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) this.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
		btn_show = (Button) this.findViewById(R.id.btn_show);
//		layout = (LinearLayout) findViewById(R.id.pop_layout);
		
		if(getIntent().getStringExtra("imgPath")!=null) {
			btn_take_photo.setText(R.string.menu_photo_v2);
			btn_pick_photo.setText(R.string.menu_pick_photo_v2);
			btn_show.setVisibility(View.VISIBLE);
			btn_show.setOnClickListener(this);
		}

		// 添加按钮监听
		btn_cancel.setOnClickListener(this);
		btn_pick_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
	}

	// 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_take_photo:
			setResult(REQUEST_TAKE_PHOTO);
			break;
		case R.id.btn_pick_photo:
			setResult(REQUEST_PICK_PHOTO);
			break;
		case R.id.btn_cancel:
			break;
		case R.id.btn_show:
			if(getIntent().getSerializableExtra("list")==null) 
				setResult(REQUEST_SHOW_COMPANY);
			else
				setResult(REQUEST_SHOW_ITEMNUMBER);
			break;
		}
		finish();
	}
}
