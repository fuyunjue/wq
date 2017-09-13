package com.cn.wq.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cn.wq.R;
import com.cn.wq.entity.ModelSizes;
import com.cn.wq.entity.ModelUserInfo;

/**
 * 
 * @Title:  AdapterChooseSizesListView.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2016-1-14 下午8:22:57 
 * @version:  V1.0 
 *
 */
@SuppressLint("ResourceAsColor")
public class AdapterChooseSizesListView extends ArrayAdapter<ModelSizes> {

	private LayoutInflater inflater;

	public AdapterChooseSizesListView(Activity activity, int resource,int textViewResourceId, List<ModelSizes> objects) {
		super(activity, resource, textViewResourceId, objects);
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ModelSizes item = getItem(position);
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_choose_sizes_listview_item,null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(item.getSizes()+"");
		return convertView;
	}

	
	private class ViewHolder {
		private TextView tv_name;
	}
}
