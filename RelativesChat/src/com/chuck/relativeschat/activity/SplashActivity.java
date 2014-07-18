package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;

import cn.bmob.v3.Bmob;
import android.os.Bundle;
import android.app.Activity;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Bmob.initialize(this, "198f48cd580b9819d82cbf356e674b99");
	}
}
