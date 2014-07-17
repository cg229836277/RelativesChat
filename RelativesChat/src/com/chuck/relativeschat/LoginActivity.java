package com.chuck.relativeschat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
	}
}
