package com.cn.wq.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

public class ArrayAdapterCheckBox extends ArrayAdapter<String> {
	private Activity activity;
	private String[] mStringArray;

	public ArrayAdapterCheckBox(Activity activity, String[] stringArray) {
		super(activity, android.R.layout.simple_list_item_checked, stringArray);
		this.activity = activity;
		mStringArray = stringArray;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// 修改Spinner展开后的字体颜色
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(activity);
			convertView = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
		}

		// 此处text1是Spinner默认的用来显示文字的TextView
		CheckedTextView tv = (CheckedTextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mStringArray[position]);
		tv.setTextSize(15f);
		tv.setTextColor(Color.BLACK);
		return convertView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 修改Spinner选择后结果的字体颜色
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(activity);
			convertView = inflater.inflate(android.R.layout.simple_list_item_checked, parent, false);
		}

		// 此处text1是Spinner默认的用来显示文字的TextView
		CheckedTextView tv = (CheckedTextView) convertView.findViewById(android.R.id.text1);
		tv.setText(mStringArray[position]);
		tv.setTextSize(15f);
		tv.setTextColor(Color.BLACK);
		return convertView;
	}
	
	

}