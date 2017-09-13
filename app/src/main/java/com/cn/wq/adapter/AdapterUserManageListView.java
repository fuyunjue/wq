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
import com.cn.wq.entity.ModelUserInfo;

/**
 * 
 * @Title:  AdapterGoodsListView.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2015-12-30 下午11:07:25 
 * @version:  V1.0 
 *
 */
@SuppressLint("ResourceAsColor")
public class AdapterUserManageListView extends ArrayAdapter<ModelUserInfo> {

	private LayoutInflater inflater;
	private Activity activity;
	private Handler mHandler;

	public AdapterUserManageListView(Activity activity, int resource,int textViewResourceId, List<ModelUserInfo> objects ,Handler mHandler) {
		super(activity, resource, textViewResourceId, objects);
		this.activity = activity;
		this.mHandler = mHandler;
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ModelUserInfo item = getItem(position);
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.layout_usermanage_listview_item,null);
			holder.tv_user_phone = (TextView) convertView.findViewById(R.id.tv_user_phone);
			holder.tv_user_name = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
			holder.tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_user_phone.setText(activity.getResources().getString(R.string.tv_user_phone) + item.getPhone());
		holder.tv_user_name.setText(activity.getResources().getString(R.string.tv_user_name) + item.getName());
//		if(item.getSys()==1) {
//			holder.tv_delete.setVisibility(View.INVISIBLE);
//		} else {
//			holder.tv_delete.setVisibility(View.VISIBLE);
//		}
//		if(item.getState()==1) {
//			holder.tv_state.setText("当前状态：正常");
//			holder.tv_delete.setText("冻结该账户");
//		} else {
//			holder.tv_state.setText("当前状态：已冻结");
//			holder.tv_delete.setText("解除冻结");
//			holder.tv_state.setTextColor(R.color.red);
//		}
//		holder.tv_delete.setTag(item);
//		holder.tv_delete.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				final ModelUserInfo userinfo = (ModelUserInfo) v.getTag();
//				activity.runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						String str = "";
//						if(userinfo.getState()==1) {
//							str = "确定冻结该账户吗?";
//						} else if(userinfo.getState()==0) {
//							str = "确定解冻该账户吗?";
//						}
//						new AlertDialog.Builder(activity).setTitle("提示").setMessage(str).setNegativeButton(
//							android.R.string.ok, new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,int which) {
//									dialog.dismiss();
//									Message obtainMessage = mHandler.obtainMessage();
//									obtainMessage.what = ActivityUserMagage.DELETE;
//									obtainMessage.obj =  userinfo;
//									mHandler.sendMessage(obtainMessage);
//								}
//							}).setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,int which) {
//									dialog.dismiss();
//								}
//							}).show();
//					}
//				});
//			}
//		});
		return convertView;
	}

	
	private class ViewHolder {
		private TextView tv_user_phone;	//门店名称
		private TextView tv_user_name;		//当前状态,0为未上传、1为已上传
		private TextView tv_state;		//款号
		private TextView tv_delete; 	//入价
	}
}
