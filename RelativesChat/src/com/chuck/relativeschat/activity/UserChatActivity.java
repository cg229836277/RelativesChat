package com.chuck.relativeschat.activity;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.listener.XListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class UserChatActivity extends BaseActivity implements IXListViewListener , EventListener , OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_chat);
	}

	@Override
	public void onClick(View arg0) {
		
	}

	@Override
	public void onAddUser(BmobInvitation arg0) {
		
	}

	@Override
	public void onMessage(BmobMsg arg0) {
		
	}

	@Override
	public void onNetChange(boolean arg0) {
		
	}

	@Override
	public void onOffline() {
		
	}

	@Override
	public void onReaded(String arg0, String arg1) {
		
	}

	@Override
	public void onRefresh() {
		
	}

	@Override
	public void onLoadMore() {
		
	}
}
