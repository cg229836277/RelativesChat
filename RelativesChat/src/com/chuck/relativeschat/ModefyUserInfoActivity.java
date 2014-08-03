package com.chuck.relativeschat;

import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.HeadViewLayout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class ModefyUserInfoActivity extends BaseActivity {

	private HeadViewLayout mHeadViewLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modefy_user_info);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("修改用户信息");
	}
}
