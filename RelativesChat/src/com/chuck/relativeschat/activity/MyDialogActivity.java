package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class MyDialogActivity extends Activity {

	private TextView titleText;
	private TextView contentText;
	
	final static public String DIALOG_TITLE = "title";
	final static public String DIALOG_CONTENT = "content";
	
	private String dialogTitle;
	private String dialogContent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_my_dialog);
		
		dialogTitle = getIntent().getStringExtra(DIALOG_TITLE);
		dialogContent = getIntent().getStringExtra(DIALOG_CONTENT);
		
		titleText  = (TextView)findViewById(R.id.dialog_title_text);
		contentText = (TextView)findViewById(R.id.dialog_content_text);
		if(dialogTitle != null && dialogContent != null){
			titleText.setText(dialogTitle);
			contentText.setText(dialogContent);
		}else{
			titleText.setText(getResources().getString(R.string.dialog_title_notify));
			contentText.setText(getResources().getString(R.string.dialog_content_notify));
		}
	}
}
