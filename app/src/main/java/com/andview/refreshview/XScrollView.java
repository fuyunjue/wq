package com.andview.refreshview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.andview.refreshview.listener.OnScrollBottomListener;

public class XScrollView extends ScrollView {
	private OnScrollBottomListener listener;
	private int calCount;

	public void registerOnBottomListener(OnScrollBottomListener l) {
		listener = l;
	}

	public void unRegisterOnBottomListener() {
		listener = null;
	}

	public XScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		View view = this.getChildAt(0);
		if (this.getHeight() + this.getScrollY() == view.getHeight()) {
			calCount++;
			if (calCount == 1) {
				if (listener != null) {
					listener.srollToBottom();
				}
			}
		} else {
			calCount = 0;
		}
	}
}
