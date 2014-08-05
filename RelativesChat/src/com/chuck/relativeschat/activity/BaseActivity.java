package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.MyDialog;
import com.chuck.relativeschat.tools.CollectionUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Window;

public class BaseActivity extends Activity {

	public BmobUserManager userManager;
	public BmobChatManager chatManager;
	public MyToast mToast;
	public MyDialog dialog;
	
	public RelativesChatApplication rcApp;
	
	protected int mScreenWidth;
	protected int mScreenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_base);
		rcApp = (RelativesChatApplication)getApplication();
		userManager = BmobUserManager.getInstance(this);
		chatManager = BmobChatManager.getInstance(this);
		mToast = new MyToast(BaseActivity.this);
		dialog = new MyDialog(BaseActivity.this);
		
		ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
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
