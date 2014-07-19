package com.chuck.relativeschat.activity;

import java.security.NoSuchAlgorithmException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.MyDialog;
import com.chuck.relativeschat.tools.MD5;
import com.chuck.relativeschat.tools.NetworkTool;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private Button registBtn;
	private Button forgetPasswordBtn;
	private Button loginBtn;
	private EditText userInputAccount;
	private EditText userInputPassword;
	private String userAccount;
	private String userPassword;
	private MyToast mToast;
	private String loginResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		bindEvent();
	}
	
	public void bindEvent(){
		registBtn = (Button)findViewById(R.id.regist_account_btn);
		registBtn.setOnClickListener(this);
		forgetPasswordBtn = (Button)findViewById(R.id.forget_account_btn);
		forgetPasswordBtn.setOnClickListener(this);
		loginBtn = (Button)findViewById(R.id.login_btn);
		loginBtn.setOnClickListener(this);
		userInputAccount = (EditText)findViewById(R.id.user_login_account_text);
		userInputPassword = (EditText)findViewById(R.id.user_login_psw_text);
		userInputAccount.setText("chuck");
		userInputPassword.setText("cg19901018!");
		userInputAccount.setSingleLine(true);
		userInputPassword.setSingleLine(true);
		userInputAccount.setSelection(userInputAccount.getText().toString().length());
		userInputPassword.setSelection(userInputPassword.getText().toString().length());
		
		mToast = new MyToast(getApplicationContext());
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.regist_account_btn:
			registNewAccount();
			break;
		case R.id.forget_account_btn:
			findUserPassword();
			break;
		case R.id.login_btn:
			if(getLoginCondition()){
				getNetworkState();
			}else{
				mToast.showMyToast(getResources().getString(R.string.account_or_password_not_complete), Toast.LENGTH_LONG);
			}
			break;
		default:
			break;
		}
	}
	
	public void registNewAccount(){
		Intent intent = new Intent(getApplicationContext() , RegistAccountActivity.class);
		startActivity(intent);
	}
	
	public void findUserPassword(){
		Intent intent = new Intent(getApplicationContext() , ForgetPasswordActivity.class);
		startActivity(intent);
	}
	
	public boolean getLoginCondition(){
		userAccount = userInputAccount.getText().toString();
		userPassword = userInputPassword.getText().toString();
		if(!StringUtils.isEmpty(userAccount) && !StringUtils.isEmpty(userPassword)){
			return true;
		}
		return false;
	}
	
	public void loginSystem(){
		new AsyncTask<Void, Void, String>() {
			
			MyDialog dialog;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				dialog = new MyDialog(LoginActivity.this);
				dialog.show();
			}
			
			@Override
			protected String doInBackground(Void... params) {
			BmobUser bu2 = new BmobUser();
			bu2.setUsername(userAccount);
			String md5Password;
//			try {
//				md5Password = MD5.getMD5(userPassword);	
				bu2.setPassword(userPassword);
				bu2.login(LoginActivity.this, new SaveListener() {
				    @Override
				    public void onSuccess() {
						Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
						startActivity(intent);
				    	mToast.showMyToast(getResources().getString(R.string.login_success), Toast.LENGTH_SHORT);
				    	return;
				    }
				    @Override
				    public void onFailure(int code, String msg) {
//				    	mToast.showMyToast(getResources().getString(R.string.username_password_incorrect), Toast.LENGTH_LONG);
				    	mToast.showMyToast("code是" + code + " " + msg, Toast.LENGTH_SHORT);
				    	loginResult = "fail";
				    }
				});
//				} 
//			catch (NoSuchAlgorithmException e) {
//					e.printStackTrace();
//				}
				return loginResult;
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				dialog.dismiss();
			}
		}.execute();
	}
	
	public void getNetworkState(){
		boolean networkState = NetworkTool.isNetworkConnected(getApplicationContext());
		if(networkState){
			loginSystem();
		}else{
			mToast.showMyToast(getResources().getString(R.string.network_not_access), Toast.LENGTH_LONG);
		}
	}
}

