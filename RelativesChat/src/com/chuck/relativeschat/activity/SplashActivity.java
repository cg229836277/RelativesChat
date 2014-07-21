package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;

import cn.bmob.im.BmobChat;
import cn.bmob.v3.Bmob;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends BaseActivity {

	final private String appId = "198f48cd580b9819d82cbf356e674b99";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		BmobChat.getInstance().init(this, appId);
		
		Intent intent = new Intent(getApplicationContext() ,LoginActivity.class);
		startActivity(intent);
	}
}

