package com.bowin.lib.widget;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.cn.wq.R;


public class MyAlertDialog {
	private android.app.AlertDialog ad;
	private TextView tv_title;
	private TextView tv_msg;
	private TextView tv_button1;
	private TextView tv_button2;
	private View ll;

	public MyAlertDialog(Context context) {
		ad = new android.app.AlertDialog.Builder(context).create();
		ad.show();
		// 关键在下面的两行,使用window.setContentView,替换整个对话框窗口的布局
		Window window = ad.getWindow();
		window.setContentView(R.layout.layout_alertdialog);
		tv_title = (TextView) window.findViewById(R.id.tv_title);
		tv_msg = (TextView) window.findViewById(R.id.tv_msg);
		tv_button1 = (TextView) window.findViewById(R.id.tv_button1);
		tv_button2 = (TextView) window.findViewById(R.id.tv_button2);
		ll = window.findViewById(R.id.ll);
	}

	public void setTitle(int resId) {
		tv_title.setText(resId);
	}

	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setMessage(int resId) {
		tv_msg.setText(resId);
	}

	public void setMessage(String message) {
		tv_msg.setText(message);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setPositiveButton(String text, View.OnClickListener listener) {
		tv_button1.setOnClickListener(listener);
		tv_button1.setVisibility(View.VISIBLE);
		if(tv_button2.isShown() && tv_button1.isShown()) 
			ll.setVisibility(View.VISIBLE);
		tv_button1.setText(text);
	}

	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setNegativeButton(String text, View.OnClickListener listener) {
		tv_button2.setOnClickListener(listener);
		tv_button2.setVisibility(View.VISIBLE);
		if(tv_button2.isShown() && tv_button1.isShown()) 
			ll.setVisibility(View.VISIBLE);
		tv_button2.setText(text);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}
	
}
