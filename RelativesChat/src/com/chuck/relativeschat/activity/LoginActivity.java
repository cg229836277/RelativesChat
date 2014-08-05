package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.PersonBean;
import com.chuck.relativeschat.tools.NetworkTool;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private Button registBtn;
	private Button forgetPasswordBtn;
	private Button loginBtn;
	private EditText userInputAccount;
	private EditText userInputPassword;
	private String userAccount;
	private String userPassword;
	private String loginResult;
	private MyHandler mHandler;
	private RelativesChatApplication rcApp;
	private  List<BmobInvitation> inviteList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_login);
		
		mHandler = new MyHandler();
		rcApp = (RelativesChatApplication) getApplication();
		
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
//		userInputAccount.setText("cg229836277");
//		userInputPassword.setText("cg19901018!");
		
		userInputAccount.setText("rr785753550");
		userInputPassword.setText("qinyanhui172587");
		userInputAccount.setSingleLine(true);
		userInputPassword.setSingleLine(true);
		userInputAccount.setSelection(userInputAccount.getText().toString().length());
		userInputPassword.setSelection(userInputPassword.getText().toString().length());
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
//		LoginSystemBizImpl loginImpl = new LoginSystemBizImpl(LoginActivity.this, mHandler);
//		loginImpl.begainLoginSystem(userAccount, userPassword);
		begainLoginSystem();
	}
	
	public void getNetworkState(){
		boolean networkState = NetworkTool.isNetworkConnected(getApplicationContext());
		if(networkState){
			loginSystem();
		}else{
			mToast.showMyToast(getResources().getString(R.string.network_not_access), Toast.LENGTH_LONG);
		}
	}
	
	public class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 0:
//				rcApp.setCurrentUser(userManager.getCurrentUser());
//				updateUserInfos();
//				mToast.showMyToast(getResources().getString(R.string.login_success), Toast.LENGTH_SHORT);
				break;
			case 1:
//				mToast.showMyToast(getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT);
				break;
			default:
				break;
			}
		}
	}
	
	public void begainLoginSystem() {
		new AsyncTask<Void, Void, String>() {
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected String doInBackground(Void... params) {
				final Message msgInfo = new Message();
				
				userManager.login(userAccount , userPassword, new SaveListener() {
				    @Override
				    public void onSuccess() {
				    	
				    	inviteList = BmobDB.create(getApplicationContext()).queryBmobInviteList();
						if(inviteList != null && inviteList.size() > 0){
							//有消息的时候就显示小红点
							rcApp = (RelativesChatApplication)getApplication();
							rcApp.setExistMoreInfoMessage(true);
						}else{
							rcApp.setExistMoreInfoMessage(false);
						}
				    	
						rcApp.setCurrentUser(userManager.getCurrentUser());
						
						BmobQuery<PersonBean> beanQuery = new BmobQuery<PersonBean>();
						beanQuery.addWhereEqualTo("objectId", userManager.getCurrentUser().getObjectId());
						beanQuery.findObjects(getApplicationContext(), new FindListener<PersonBean>() {
							
							@Override
							public void onSuccess(List<PersonBean> arg0) {
								rcApp.setPersonDetailData(arg0.get(0));
							}
							
							@Override
							public void onError(int arg0, String arg1) {
								System.out.println("初始化用户详细信息失败");
							}
						});
				    	
						dialog.dismiss();
						Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
						startActivity(intent);  	
						
						mToast.showMyToast(getResources().getString(R.string.login_success), Toast.LENGTH_SHORT);
						
				    	return;
				    }
				    @Override
				    public void onFailure(int code, String msg) {
				    	dialog.dismiss();
				    	mToast.showMyToast(getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT);
				    }
				});
				return null;
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
			}
		}.execute();
	}
}

