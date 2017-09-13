package com.bowin.lib.utils;

import android.content.Context;
import android.view.View;

import com.bowin.lib.widget.MyAlertDialog;

/**
 * 
 * @author Terence
 *
 */
public class AlertDialogUtil {
	
	/**
	 * 弹出提示
	 * 
	 * @param title
	 * @param message
	 */
	public static MyAlertDialog showAlertDialog(Context context, String title, String message ,String ok ,View.OnClickListener okCallBack ,String cancel ,View.OnClickListener cancelCallBack) {
		MyAlertDialog myAlertDialog = new MyAlertDialog(context);
		myAlertDialog.setTitle(title);
		myAlertDialog.setMessage(message);
		if(ok != null && okCallBack != null)
			myAlertDialog.setPositiveButton(ok ,okCallBack);
		if(cancel != null && okCallBack != cancelCallBack)
			myAlertDialog.setNegativeButton(cancel ,cancelCallBack);
		return myAlertDialog;
	}

}
