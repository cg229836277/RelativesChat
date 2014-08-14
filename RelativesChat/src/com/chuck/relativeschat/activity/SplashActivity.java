package com.chuck.relativeschat.activity;

import java.util.List;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.CircleImageView;

import cn.bmob.im.BmobChat;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.Bmob;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends BaseActivity {

	final private String appId = "198f48cd580b9819d82cbf356e674b99";
	private  List<BmobInvitation> inviteList;
//	private RelativesChatApplication rcApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		BmobChat.getInstance().init(this, appId);
		
//		Intent intent = new Intent(getApplicationContext() ,LoginActivity.class);
//		startActivity(intent);
//		
//		ImageView testImage = (ImageView)findViewById(R.id.test_image);
//		CircleImageView imageView = new CircleImageView(getApplicationContext());
////		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_code);
//		//可以设置bitmap.getWidth()/2做半径
//		testImage.setImageBitmap(imageView.toRoundCorner(R.drawable.qr_code));
	}
}

