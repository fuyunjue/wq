package com.cn.wq.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cn.wq.R;
import com.cn.wq.adapter.ArrayAdapterCheckBox;
import com.cn.wq.entity.ModelOther;

public class MySpinner extends Button {
	
	private String defaultText = "    ▼";

	private List<Integer> list = new ArrayList<Integer>();

	public MySpinner(Context context) {
		super(context);
	}

	public MySpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		setTag(text==null||text.toString().toLowerCase().equals("null")?"":text);
		super.setText((text==null||text.toString().toLowerCase().equals("null")?"":text) + defaultText, type);
	}

	public void initContent(List<ModelOther> others) {
		setText("", null);
		final String[] otherStrs = new String[others.size()];
		for (int i = 0; i< otherStrs.length ; i++) {
			otherStrs[i] = others.get(i).getName();
		}
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				final SpinnerDialog dialog = new SpinnerDialog(getContext());
				TextView tv_sure = (TextView) dialog.findViewById(R.id.tv_sure);
				tv_sure.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (list.size() == 0) {
							setText("", null);
						} else {
							StringBuffer buffer = new StringBuffer();
							int size = list.size();
							for (int i = 0; i < size; i++) {
								buffer.append(otherStrs[list.get(i)] + ',');
							}
							setText(buffer.toString().substring(0,buffer.length() - 1), null);
						}
						dialog.cancel();
						list.clear();
					}
				});
				TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
				tv_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				dialog.show();
				ListView listView = (ListView) dialog.findViewById(R.id.listview);
				listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
				ArrayAdapterCheckBox adapter = new ArrayAdapterCheckBox((Activity)getContext(), otherStrs);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						Integer object = Integer.valueOf(arg2);
						if (list.contains(object)) {
							list.remove(object);
						} else {
							list.add(object);
						}
					}
				});
				dialog.show();
			}
		};
		super.setOnClickListener(listener);
	}

	private class SpinnerDialog extends Dialog {
		public SpinnerDialog(Context context) {
			super(context, R.style.Theme_dialog);
			setContentView(R.layout.spinner);
			Window window = getWindow();
			WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.gravity = Gravity.CENTER;
			DisplayMetrics dm = new DisplayMetrics();
			getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			int screenHeigh = dm.heightPixels;
			params.height = (int) (screenHeigh * 0.8);
			params.width = (int) (screenWidth * 0.8);
			window.setAttributes(params);
		}
	}
}
