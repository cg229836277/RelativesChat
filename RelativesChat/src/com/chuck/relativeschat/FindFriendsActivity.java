package com.chuck.relativeschat;

import java.util.List;

import cn.bmob.v3.listener.FindListener;

import com.bmob.im.demo.bean.User;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class FindFriendsActivity extends BaseActivity {

	private HeadViewLayout mHeadViewLayout;
	private EditText searchFriendsEdit;
	private Button searchButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_find_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("查找好友");
		
		searchButton = (Button)findViewById(R.id.find_friends_button);
		searchFriendsEdit  = (EditText)findViewById(R.id.find_friend_name);
		
		searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				findFriends();
			}
		});
	}
	
	public void findFriends(){
		String searchUserName = searchFriendsEdit.getText().toString();
		if(!StringUtils.isEmpty(searchUserName)){
			userManager.queryUserInfo(searchUserName, new FindListener<User>() {

				@Override
				public void onError(int arg0, String arg1) {
				}

				@Override
				public void onSuccess(List<User> arg0) {
					if(arg0!=null && arg0.size()>0){
						user = arg0.get(0);
						btn_chat.setEnabled(true);
						btn_back.setEnabled(true);
						btn_add_friend.setEnabled(true);
						updateUser(user);
					}else{
					}
				}
			});
		}
	}
}
