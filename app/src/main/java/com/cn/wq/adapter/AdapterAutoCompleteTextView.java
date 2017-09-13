/**
 * 
 */
package com.cn.wq.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.cn.wq.R;
import com.cn.wq.entity.ModelVendors;

/**
 * @Title:  AdapterAutoCompleteTextView.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2016-1-1 下午10:09:37 
 * @version:  V1.0 
 * 
 */
public class AdapterAutoCompleteTextView extends ArrayAdapter<ModelVendors> implements Filterable {
    private List<ModelVendors> mList;  
    private Context context;  
    private ArrayList<ModelVendors> mUnfilteredData;  
    private AutoCompleteTextView mAutoCompleteTextView;
	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public AdapterAutoCompleteTextView(Context context, int resource,
			List<ModelVendors> mList ,AutoCompleteTextView autoCompleteTextView) {
		super(context, resource, mList);
		this.context = context;
		this.mList = mList;  
		this.mAutoCompleteTextView = autoCompleteTextView;
	}
	
	
	/**
	 * @return the mAutoCompleteTextView
	 */
	public AutoCompleteTextView getmAutoCompleteTextView() {
		return mAutoCompleteTextView;
	}


	public void setData(List<ModelVendors> mList) {
		this.mList = mList;  
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {  
        View view;  
        ViewHolder holder;  
        if(convertView==null){  
            view = View.inflate(context, R.layout.layout_vendors_item, null);  
            holder = new ViewHolder();  
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);  
            holder.tv_phone = (TextView) view.findViewById(R.id.tv_phone);  
            holder.tv_email = (TextView) view.findViewById(R.id.tv_email);  
            view.setTag(holder);  
        } else {
            view = convertView;  
            holder = (ViewHolder) view.getTag();  
        }  
        ModelVendors modelVendors = mList.get(position);  
        holder.tv_name.setText(context.getResources().getString(R.string.tv_desc_chi) + modelVendors.getDesc_chi());  
        holder.tv_phone.setText(context.getResources().getString(R.string.tv_phone) + modelVendors.getPhone1() + "/"+modelVendors.getPhone2()+ "/"+modelVendors.getPhone3());  
        holder.tv_email.setText(context.getResources().getString(R.string.tv_email)+modelVendors.getEmail());  
          
        return view;  
    }
	
	
	static class ViewHolder{  
        public TextView tv_name;  
        public TextView tv_phone;  
        public TextView tv_email;  
    } 
	
	@Override  
    public int getCount() {  
        return mList==null ? 0:mList.size();  
    }  
  
    @Override  
    public ModelVendors getItem(int position) {  
        return mList.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
    
	private class ArrayFilter extends Filter {  
		  
        @Override  
        protected FilterResults performFiltering(CharSequence prefix) {  
            FilterResults results = new FilterResults();  
  
            if (mUnfilteredData == null) {  
                mUnfilteredData = new ArrayList<ModelVendors>(mList);  
            }  
  
            if (prefix == null || prefix.length() == 0) {  
                ArrayList<ModelVendors> list = mUnfilteredData;  
                results.values = list;  
                results.count = list.size();  
            } else {  
                String prefixString = prefix.toString().toLowerCase();  
                ArrayList<ModelVendors> unfilteredValues = mUnfilteredData;  
                int count = unfilteredValues.size();  
                ArrayList<ModelVendors> newValues = new ArrayList<ModelVendors>(count);  
  
                for (int i = 0; i < count; i++) {  
                    ModelVendors pc = unfilteredValues.get(i);  
                    if (pc != null) {  
                          
                        if(pc.getDesc_chi()!=null && pc.getDesc_chi().startsWith(prefixString)){  
                              
                            newValues.add(pc);  
                        }else if(pc.getEmail()!=null && pc.getEmail().startsWith(prefixString)){  
                              
                            newValues.add(pc);  
                        }  
                    }  
                }  
                results.values = newValues;  
                results.count = newValues.size();  
            }  
  
            return results;  
        }  
  
        @Override  
        protected void publishResults(CharSequence constraint,  
                FilterResults results) {  
            mList = (List<ModelVendors>) results.values;  
            if (results.count > 0) {  
                notifyDataSetChanged();  
            } else {  
                notifyDataSetInvalidated();  
            }  
        }  
          
    }  
}
