package com.cn.wq.activty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.cn.wq.R;
import com.cn.wq.utils.PictureUtil;

/**
 * 查看圖片
 * @author Terence
 *
 */
public class ActivityImage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		if(getIntent().getStringExtra("imgPath")!=null) {
			((ImageView)findViewById(R.id.iv)).setImageBitmap(PictureUtil.getSmallBitmap(getIntent().getStringExtra("imgPath")));
		}
		findViewById(R.id.tv_back).setVisibility(View.VISIBLE);
		findViewById(R.id.tv_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
