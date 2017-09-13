package com.cn.wq.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wq.R;
import com.cn.wq.entity.ModelItemnumber;
import com.cn.wq.entity.ModelPhoto;
import com.cn.wq.entity.ModelPurchase;
import com.cn.wq.entity.ModelPurchaseList;
import com.cn.wq.entity.ModelSizeInfo;
import com.cn.wq.entity.ModelStyleNumberPhoto;

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
public class AdapterPurchaseListView extends ArrayAdapter<ModelPurchase> {

	private LayoutInflater inflater;
	private Context mContext;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public AdapterPurchaseListView(Context context, int resource,int textViewResourceId, List<ModelPurchase> objects) {
		super(context, resource, textViewResourceId, objects);
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ModelPurchase item = getItem(position);
		ViewHolder holder;
		if (null == convertView) {
			holder = new ViewHolder();
			if(item.getV2()!=null && item.getV2()==1) {
				convertView = inflater.inflate(R.layout.layout_main_listview_item_v2,null);
				holder.tv_vendors = (TextView) convertView.findViewById(R.id.tv_vendors);
				holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
				holder.tv_createdate = (TextView) convertView.findViewById(R.id.tv_createdate);
				holder.tv_lastupdatedate = (TextView) convertView.findViewById(R.id.tv_lastupdatedate);
				holder.tv_photo_upload = (TextView) convertView.findViewById(R.id.tv_photo_upload);
				holder.ll_sizeinfos = (LinearLayout) convertView.findViewById(R.id.ll_sizeinfos);
			} else {
				convertView = inflater.inflate(R.layout.layout_main_listview_item,null);
				holder.tv_vendors = (TextView) convertView.findViewById(R.id.tv_vendors);
				holder.tv_deposit = (TextView) convertView.findViewById(R.id.tv_deposit);
				holder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
				holder.tv_itemnumber = (TextView) convertView.findViewById(R.id.tv_itemnumber);
				holder.tv_harvest_time = (TextView) convertView.findViewById(R.id.tv_harvest_time);
				holder.tv_price_currency = (TextView) convertView.findViewById(R.id.tv_price_currency);
				holder.ll_purchase_list = (LinearLayout) convertView.findViewById(R.id.ll_purchase_list);
				holder.tv_photo_upload = (TextView) convertView.findViewById(R.id.tv_photo_upload);
				holder.tv_createdate = (TextView) convertView.findViewById(R.id.tv_createdate);
				holder.tv_lastupdatedate = (TextView) convertView.findViewById(R.id.tv_lastupdatedate);

			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.tv_vendors.setText(mContext.getResources().getString(R.string.tv_purchase) + item.getCompanyId());
			holder.tv_createdate.setText(mContext.getResources().getString(R.string.tv_createdate) + dateFormat.format(dateFormat1.parse(item.getCreatedate())));
			String lastupdatedate = item.getLastupdatedate();
			if(item.getLastupdatedate().indexOf("(datetime('now','localtime'))")!=-1 || lastupdatedate.length()==0 || lastupdatedate.toUpperCase().equals("NULL")) {
				lastupdatedate = dateFormat1.format(item.getCreatedate());
			}
			holder.tv_lastupdatedate.setText(mContext.getResources().getString(R.string.tv_lastupdatedate) + lastupdatedate);
			
			
			if(item.getV2()!=null && item.getV2()==1) {
				/**
				 * V2.0需求，列表展現內容不同
				 */
				List<ModelItemnumber> itemnumbers = item.getItemnumbers();
				int i = 0;
				holder.ll_sizeinfos.removeAllViews();
				for (ModelItemnumber itemnumber : itemnumbers) {
					View sizeView = inflater.inflate(R.layout.layout_main_item_size_info,null);
					holder.ll_sizeinfos.addView(sizeView);
					((TextView)sizeView.findViewById(R.id.tv_itemnumber)).setText(mContext.getResources().getString(R.string.tv_kh) + itemnumber.getStyleNumber());
					((TextView)sizeView.findViewById(R.id.tv_harvest_time)).setText(mContext.getResources().getString(R.string.tv_harvest_time_1) + itemnumber.getHarvestTime());
					((TextView)sizeView.findViewById(R.id.tv_deposit)).setText(mContext.getResources().getString(R.string.tv_rj) + itemnumber.getPrice());
					List<ModelStyleNumberPhoto> styleNumberPhotos = itemnumber.getStyleNumberPhotos();
					for (ModelStyleNumberPhoto modelStyleNumberPhoto : styleNumberPhotos) {
						if(modelStyleNumberPhoto.getStyleNumberPhoto().getHasupload()==0) 
							i++;
						List<ModelSizeInfo> sizeInfos = modelStyleNumberPhoto.getSizeInfos();
						for (ModelSizeInfo sizeInfo : sizeInfos) {
							View view = inflater.inflate(R.layout.layout_purchase_item_main_listview_item,null);
							((LinearLayout)sizeView.findViewById(R.id.ll_purchase_list)).addView(view);
							((TextView)view.findViewById(R.id.tv_amount)).setText(sizeInfo.getAmount()+"");
							((TextView)view.findViewById(R.id.tv_color)).setText(sizeInfo.getColor()+"");
							((TextView)view.findViewById(R.id.tv_sizes)).setText(sizeInfo.getSize()+"");
						}
					}
				}
				if(item.getHasupload()==1) {
					holder.tv_state.setText(mContext.getResources().getString(R.string.state_upload));
					holder.tv_state.setTextColor(mContext.getResources().getColor(android.R.color.black));
					if(item.getCompanyPhoto().getHasupload()==0) 
						i++;
					if(i>0) {
						holder.tv_photo_upload.setVisibility(View.VISIBLE);
						holder.tv_photo_upload.setText("(" + i + mContext.getResources().getString(R.string.tv_photo_upload_1));
					} else {
						holder.tv_photo_upload.setText(mContext.getResources().getString(R.string.tv_photo_upload_0));
						holder.tv_photo_upload.setVisibility(View.GONE);
					}
				} else {
					if(item.getState()==0) {	//未完成
						holder.tv_state.setText(mContext.getResources().getString(R.string.state_finish_none));
						holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.green));
					} else if(item.getState()==1){	//已完成
						holder.tv_state.setText(mContext.getResources().getString(R.string.state_upload_none));
						holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.red));
					}
				}
				
			} else {
				holder.tv_deposit.setText(mContext.getResources().getString(R.string.tv_deposit) + item.getDeposit());
				holder.tv_harvest_time.setText(mContext.getResources().getString(R.string.tv_harvest_time_1) + item.getHarvest_time());
				if(item.getHasupload()==1) {
					holder.tv_state.setText(mContext.getResources().getString(R.string.state_upload));
					holder.tv_state.setTextColor(mContext.getResources().getColor(android.R.color.black));
					List<ModelPhoto> pics = item.getPics();
					int i=0;
					for (ModelPhoto modelPhoto : pics) {
						if(modelPhoto.getHasupload()==0) 
							i++;
					}
					if(i>0) {
						holder.tv_photo_upload.setVisibility(View.VISIBLE);
						holder.tv_photo_upload.setText("(" + i + mContext.getResources().getString(R.string.tv_photo_upload_1));
					} else {
						holder.tv_photo_upload.setText(mContext.getResources().getString(R.string.tv_photo_upload_0));
						holder.tv_photo_upload.setVisibility(View.GONE);
					}
				} else {
					if(item.getState()==0) {	//未完成
						holder.tv_state.setText(mContext.getResources().getString(R.string.state_finish_none));
						holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.green));
					} else if(item.getState()==1){	//已完成
						holder.tv_state.setText(mContext.getResources().getString(R.string.state_upload_none));
						holder.tv_state.setTextColor(mContext.getResources().getColor(R.color.red));
					}
				}
				holder.tv_itemnumber.setText(mContext.getResources().getString(R.string.tv_kh) + item.getItemnumber());
				holder.tv_price_currency.setText(mContext.getResources().getString(R.string.tv_rj) + item.getPrice() + "(" + item.getCurrency() + ")");
				List<ModelPurchaseList> purchaseLists = item.getPurchaseLists();
				holder.ll_purchase_list.removeAllViews();
				for (ModelPurchaseList modelPurchaseList : purchaseLists) {
					View view = inflater.inflate(R.layout.layout_purchase_item_main_listview_item,null);
					holder.ll_purchase_list.addView(view);
					((TextView)view.findViewById(R.id.tv_amount)).setText(modelPurchaseList.getAmount()+"");
					((TextView)view.findViewById(R.id.tv_color)).setText(modelPurchaseList.getColor());
					((TextView)view.findViewById(R.id.tv_sizes)).setText(modelPurchaseList.getSizes()+"");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return convertView;
	}

	
	private class ViewHolder {
		private TextView tv_deposit;	//定金
		private TextView tv_createdate;	
		private TextView tv_lastupdatedate;	//
		private TextView tv_vendors;	//门店名称
		private TextView tv_state;		//当前状态,0为未上传、1为已上传
		private TextView tv_itemnumber;		//款号
		private TextView tv_harvest_time;		//預計收貨日期
		private TextView tv_price_currency; 	//入价(貨幣類型)
		private LinearLayout ll_purchase_list;	//颜色与数量列表
		private TextView tv_photo_upload;	//图片上传状态
		
		/**
		 * V2.0需求
		 */
		private LinearLayout ll_sizeinfos;	//循環添加所有sizeinfo
	}
}
