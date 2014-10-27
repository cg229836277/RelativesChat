package com.chuck.relativeschat.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.common.DialogTips;
import com.chuck.relativeschat.common.MyColorPickerDialog;
import com.chuck.relativeschat.common.MyDialog;
import com.chuck.relativeschat.common.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.view.Window;

public class BaseActivity extends Activity {

	public BmobUserManager userManager;
	public BmobChatManager chatManager;
	public MyToast mToast;
	public MyDialog dialog;
	public MyProgressDialog mProgressDialog;
	
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
		mProgressDialog = new MyProgressDialog(BaseActivity.this);
		
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		
		RegListener();
	}
	
    ExitListenerReceiver exitre = null;
    /**注册退出事件监听**/
    public void RegListener() {
       exitre = new ExitListenerReceiver();
       IntentFilter intentfilter = new IntentFilter();
       intentfilter.addAction(this.getPackageName() + "." + "ExitListenerReceiver");
       this.registerReceiver(exitre, intentfilter);
    }
    class ExitListenerReceiver extends BroadcastReceiver {
       @Override
        public void onReceive(Context context, Intent i) {
           finish();
       }
   }
	
	public void logoutSystem(){
		DialogTips dialog = new DialogTips(BaseActivity.this, "提示", "退出系统!", "确认", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				deleteCacheVideoFile();
				Intent intent = new Intent(getPackageName()+".ExitListenerReceiver");
				sendBroadcast(intent);
			}				
		});
		
		dialog.show();
		dialog = null;
	}
	
	public void deleteCacheVideoFile(){
		dialog.show();
		File videoCacheFileDir = new File(BmobConstants.RECORD_VIDEO_CACHE_PATH);
		File[] cacheVideoFiles = videoCacheFileDir.listFiles();
		if(cacheVideoFiles != null && cacheVideoFiles.length > 0){
			for(int i = 0 ; i < cacheVideoFiles.length ; i++){
				if(cacheVideoFiles[i] != null && cacheVideoFiles[i].exists()){
					cacheVideoFiles[i].delete();
				}
			}
		}
		dialog.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unregisterReceiver(exitre);
	}
}
