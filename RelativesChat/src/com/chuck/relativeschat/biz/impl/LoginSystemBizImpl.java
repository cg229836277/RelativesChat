package com.chuck.relativeschat.biz.impl;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.chuck.relativeschat.activity.MainMenuActivity;
import com.chuck.relativeschat.biz.LoginSystemBiz;
import com.chuck.relativeschat.common.MyDialog;

public class LoginSystemBizImpl implements LoginSystemBiz {

//	private Context mContext;
//	private Handler mHandler;
//	
//	public LoginSystemBizImpl(Context context , Handler handler){
//		this.mContext = context;
//		this.mHandler = handler;
//	}
//
	@Override
	public void begainLoginSystem(final String userAccount, final String userPassword) {
//		new AsyncTask<Void, Void, String>() {
//			
//			MyDialog dialog;
//			
//			@Override
//			protected void onPreExecute() {
//				super.onPreExecute();
//				
//				dialog = new MyDialog(mContext);
//				dialog.show();
//			}
//			
//			@Override
//			protected String doInBackground(Void... params) {
//				final Message msgInfo = new Message();
//				
////				BmobUser bu2 = new BmobUser();
////				bu2.setUsername(userAccount);
////				String md5Password;
////				bu2.setPassword(userPassword);
//				userManager.login(mContext, new SaveListener() {
//				    @Override
//				    public void onSuccess() {
//				    	dialog.dismiss();
//						Intent intent = new Intent(mContext, MainMenuActivity.class);
//						mContext.startActivity(intent);  	
//						
//						msgInfo.what = 0;
//						mHandler.sendMessage(msgInfo);
//						
//				    	return;
//				    }
//				    @Override
//				    public void onFailure(int code, String msg) {
//				    	dialog.dismiss();
//				    	msgInfo.what = 1;
//				    	mHandler.sendMessage(msgInfo);
//				    }
//				});
//				return null;
//			}
//			
//			@Override
//			protected void onPostExecute(String result) {
//				super.onPostExecute(result);
//			}
//		}.execute();
	}

}
