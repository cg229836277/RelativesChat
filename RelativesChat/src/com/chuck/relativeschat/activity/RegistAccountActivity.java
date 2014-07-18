package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Window;

public class RegistAccountActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist_account);
	}
}
