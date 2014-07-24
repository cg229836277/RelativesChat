package com.chuck.relativeschat.activity;

import java.io.IOException;

import com.chuck.relativeschat.R;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyToast {
	
	private TextView titleText;
	private TextView contentText;
	private Context mContext;
	
	public MyToast(Context context){
		this.mContext = context;
	}
	
	public void showMyToast(String toastContent , int showTime) {
		View layout = LayoutInflater.from(mContext).inflate(R.layout.activity_my_dialog, null);
		titleText = (TextView) layout.findViewById(R.id.dialog_title_text);
		titleText.setText(mContext.getResources().getString(R.string.dialog_title_notify));
		contentText = (TextView) layout.findViewById(R.id.dialog_content_text);
		contentText.setText(toastContent);
		Toast toast = new Toast(mContext);		
		toast.setDuration(showTime);
		toast.setView(layout);
		toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
		toast.show();
		
		Runtime run = Runtime.getRuntime();
		try {
			run.exec("input keyevent" + KeyEvent.KEYCODE_FORWARD);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
