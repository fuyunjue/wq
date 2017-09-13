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
import com.cn.wq.entity.ModelColors;

/**
 * 
 * @Title:  AdapterColorsAutoCompleteTextView.java
 * 
 * @Description:  TODO<请描述此文件是做什么的>
 *
 * @author Terence
 * @data:  2016-1-2 上午1:28:46 
 * @version:  V1.0 
 *
 */
public class AdapterColorsAutoCompleteTextView extends ArrayAdapter<ModelColors> implements Filterable {
    private List<ModelColors> mList;  
    private Context context;  
    private ArrayList<ModelColors> mUnfilteredData;  
    private AutoCompleteTextView mAutoCompleteTextView;
    private View parentView;
	/**
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public AdapterColorsAutoCompleteTextView(Context context, int resource,
			List<ModelColors> mList ,AutoCompleteTextView mAutoCompleteTextView ,View parentView) {
		super(context, resource, mList);
		this.context = context;
		this.mList = mList;  
		this.mAutoCompleteTextView = mAutoCompleteTextView;
		this.parentView = parentView;
	}
	
	/**
	 * @return the parentView
	 */
	public View getParentView() {
		return parentView;
	}

	/**
	 * @return the mAutoCompleteTextView
	 */
	public AutoCompleteTextView getmAutoCompleteTextView() {
		return mAutoCompleteTextView;
	}
	
	public void setData(List<ModelColors> mList) {
		this.mList = mList;  
		super.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {  
        ViewHolder holder;  
        if(convertView==null){
        	convertView = View.inflate(context, R.layout.layout_colors_item, null);  
            holder = new ViewHolder();  
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);  
            convertView.setTag(holder);  
        } else {
            holder = (ViewHolder) convertView.getTag();  
        }  
        ModelColors modelColors = mList.get(position);  
        holder.tv_name.setText(modelColors.getColor()+"");  
        return convertView;  
    }
	
	
	static class ViewHolder{  
        public TextView tv_name;  
    } 
	
	@Override  
    public int getCount() {  
        return mList==null ? 0:mList.size();  
    }  
  
    @Override  
    public ModelColors getItem(int position) {  
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
                mUnfilteredData = new ArrayList<ModelColors>(mList);  
            }  
  
            if (prefix == null || prefix.length() == 0) {  
                ArrayList<ModelColors> list = mUnfilteredData;  
                results.values = list;  
                results.count = list.size();  
            } else {  
                String prefixString = prefix.toString().toLowerCase();  
                ArrayList<ModelColors> unfilteredValues = mUnfilteredData;  
                int count = unfilteredValues.size();  
                ArrayList<ModelColors> newValues = new ArrayList<ModelColors>(count);  
  
                for (int i = 0; i < count; i++) {  
                    ModelColors pc = unfilteredValues.get(i);  
                    if (pc != null) {  
                          
                        if(pc.getColor()!=null && pc.getColor().startsWith(prefixString)){  
                              
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
            mList = (List<ModelColors>) results.values;  
            if (results.count > 0) {  
                notifyDataSetChanged();  
            } else {  
                notifyDataSetInvalidated();  
            }  
        }  
          
    }  
}
