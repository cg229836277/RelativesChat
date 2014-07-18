package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;

public class ForgetPasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget_password);
	}
}
