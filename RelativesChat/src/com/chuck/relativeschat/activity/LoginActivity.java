package com.chuck.relativeschat.activity;

import c.sweet;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class LoginActivity extends Activity implements OnClickListener {

	private Button registBtn;
	private Button forgetPasswordBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		bindEvent();
	}
	
	public void bindEvent(){
		registBtn = (Button)findViewById(R.id.regist_account_btn);
		registBtn.setOnClickListener(this);
		forgetPasswordBtn = (Button)findViewById(R.id.forget_account_btn);
		forgetPasswordBtn.setOnClickListener(this);
		
		showDialog();
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.regist_account_btn:
			registNewAccount();
			break;
		case R.id.forget_account_btn:
			findUserPassword();
			break;
		default:
			showDialog();
			break;
		}
	}
	
	public void registNewAccount(){
		Intent intent = new Intent(getApplicationContext() , RegistAccountActivity.class);
		startActivity(intent);
	}
	
	public void findUserPassword(){
		Intent intent = new Intent(getApplicationContext() , ForgetPasswordActivity.class);
		startActivity(intent);
	}
	
	public void showDialog(){
		Intent intent = new Intent(getApplicationContext() , MyDialogActivity.class);
		startActivity(intent);
	}
}

