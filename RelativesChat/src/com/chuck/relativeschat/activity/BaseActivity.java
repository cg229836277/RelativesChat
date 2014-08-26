package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.MyColorPickerDialog;
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
	
	public MyColorPickerDialog colorPickerDialog;
	
	protected ImageLoader imageLoader;
	
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
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}
}
