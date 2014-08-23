package com.chuck.relativeschat.activity;

import java.util.List;

import com.chuck.relativeschat.R;
import cn.bmob.im.BmobChat;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.v3.BmobPushManager;
import android.content.Intent;
import android.os.Bundle;

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
		
		Intent intent = new Intent(getApplicationContext() ,LoginActivity.class);
		startActivity(intent);
////		
//		ImageView testImage = (ImageView)findViewById(R.id.test_image);
//		CircleImageView imageView = new CircleImageView(getApplicationContext());
////		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.qr_code);
//		//可以设置bitmap.getWidth()/2做半径
//		testImage.setImageBitmap(imageView.toRoundCorner(R.drawable.qr_code));
	}
}

