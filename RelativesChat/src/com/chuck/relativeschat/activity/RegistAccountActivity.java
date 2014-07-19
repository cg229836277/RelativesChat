package com.chuck.relativeschat.activity;

import java.security.NoSuchAlgorithmException;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.MyDialog;
import com.chuck.relativeschat.tools.MD5;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegistAccountActivity extends Activity {

	private EditText userNameEdit;
	private EditText userPasswordEdit;
	private EditText confirmUserPasswordEdit;
	private EditText emailEdit;
	private Button registButton;
	private String userName;
	private String userPassword;
	private String confirmUserPassword;
	private String emailStr;
	private MyToast mToast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_regist_account);
		
		mToast = new MyToast(getApplicationContext());
		
		bindEvent();
	}

	public void bindEvent() {
		userNameEdit = (EditText) findViewById(R.id.regist_account_edit);
		userPasswordEdit = (EditText) findViewById(R.id.regist_password_edit);
		confirmUserPasswordEdit = (EditText) findViewById(R.id.confirm_password_edit);
		emailEdit = (EditText) findViewById(R.id.user_email_edit);

		registButton = (Button) findViewById(R.id.regist_new_account_btn);
		registButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				userName = userNameEdit.getText().toString();
				userPassword = userPasswordEdit.getText().toString();
				confirmUserPassword = confirmUserPasswordEdit.getText()
						.toString();
				emailStr = emailEdit.getText().toString();
				if (!StringUtils.isEmpty(userName)&& !StringUtils.isEmpty(userPassword)
						&& !StringUtils.isEmpty(emailStr) && !StringUtils.isEmpty(confirmUserPassword)) {
					if (userPassword.equals(confirmUserPassword)) {
						registNewAccount();
					} else {
						mToast.showMyToast(getResources().getString(R.string.password_not_correct_with_former),Toast.LENGTH_LONG);
					}
				} else {
					mToast.showMyToast(getResources().getString(R.string.regist_information_not_complete),Toast.LENGTH_LONG);
				}
			}
		});
	}

	public void registNewAccount() {		
		new AsyncTask<Void, Void, Void>() {
			
			MyDialog dialog;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				
				dialog = new MyDialog(RegistAccountActivity.this);
				dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				BmobUser bu = new BmobUser();
				bu.setUsername(userName);
				String md5Password;
//				try {
//					md5Password = MD5.getMD5(userPassword);
					bu.setPassword(userPassword);
					bu.setEmail(emailStr);
					bu.signUp(RegistAccountActivity.this, new SaveListener() {
						@Override
						public void onSuccess() {
							mToast.showMyToast(getResources().getString(R.string.regist_success),Toast.LENGTH_SHORT);
						}
		
						@Override
						public void onFailure(int code, String msg) {
							mToast.showMyToast(msg,Toast.LENGTH_SHORT);
						}
					});
//				} 
//				catch (NoSuchAlgorithmException e) {
//					e.printStackTrace();
//				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				dialog.dismiss();
			}
		}.execute();
	}
}
