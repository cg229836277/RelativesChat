package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.R.menu;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.MyDialog;
import com.chuck.relativeschat.tools.CollectionUtils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;

public class BaseActivity extends Activity {

	public BmobUserManager userManager;
	public BmobChatManager chatManager;
	public MyToast mToast;
	public MyDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_base);
		
		userManager = BmobUserManager.getInstance(this);
		chatManager = BmobChatManager.getInstance(this);
		mToast = new MyToast(BaseActivity.this);
		dialog = new MyDialog(BaseActivity.this);
	}
	
	public void updateUserInfos(){
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
			@Override
			public void onError(int arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				RelativesChatApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
			}
		});
	}
}
