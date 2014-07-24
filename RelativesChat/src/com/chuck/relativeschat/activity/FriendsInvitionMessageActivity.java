package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.R.menu;
import com.chuck.relativeschat.common.HeadViewLayout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

public class FriendsInvitionMessageActivity extends BaseActivity implements OnItemLongClickListener {

	private HeadViewLayout mHeadViewLayout;
	private ListView listview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_invition_message);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("好友验证");
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2 , long arg3) {
		
		return false; 
	}
}
