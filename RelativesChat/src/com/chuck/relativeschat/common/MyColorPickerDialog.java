package com.chuck.relativeschat.common;

import net.margaritov.preference.colorpicker.ColorPickerDialog;
import net.margaritov.preference.colorpicker.ColorPickerDialog.OnColorChangedListener;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import android.R;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-19 上午11:12:09
 * @author chengang
 * @version 1.0
 */
public class MyColorPickerDialog extends ColorPickerDialog implements OnColorChangedListener{

	private ColorPickerDialog myColorPickerDialog;
	private Context mContext;
	private int choosedColor = 0;
	private Handler mHandler;
	/**
	 * @param context
	 */
	public MyColorPickerDialog(Context context , Handler handler) {
		super(context, 0);
		this.mContext = context;
		this.mHandler = handler;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		myColorPickerDialog = new ColorPickerDialog(mContext, R.color.white);
		
		myColorPickerDialog.setOnColorChangedListener(this);
		myColorPickerDialog.setAlphaSliderVisible(true);
		myColorPickerDialog.setHexValueEnabled(true);
		
		this.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				System.out.println("对话框关闭了  " + choosedColor);
				Message msg = new Message();
				msg.what = choosedColor;
				mHandler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onColorChanged(int color) {
		choosedColor = color;
	}
	
//	@Override
//	public void setDismissMessage(Message msg) {
//		super.setDismissMessage(msg);		
//		msg.what = choosedColor;
//		mHandler.sendMessage(msg);
//	}
//	
//	@Override
//	public void setOnDismissListener(OnDismissListener listener) {
//		super.setOnDismissListener(listener);
//		
//		Message msg = new Message();
//		msg.what = choosedColor;
//		mHandler.sendMessage(msg);
//	}
}
