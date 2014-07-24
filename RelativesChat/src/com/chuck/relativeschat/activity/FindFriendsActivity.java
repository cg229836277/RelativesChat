package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.inteface.MsgTag;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.User;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FindFriendsActivity extends BaseActivity {

	private HeadViewLayout mHeadViewLayout;
	private EditText searchFriendsEdit;
	private Button searchButton;
	private LinearLayout friendsResultLayout;
	private View simpleFriendView;
	private TextView friendsNameText;
	private TextView friendsPersonalSignText;
	private List<User> tempUser;
	private Button addFriendsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("查找好友");
		
		searchButton = (Button)findViewById(R.id.find_friends_button);
		searchFriendsEdit  = (EditText)findViewById(R.id.find_friend_name);
		friendsResultLayout = (LinearLayout)findViewById(R.id.friend_result_container);
		
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
					if(arg0 != null && arg0.size() > 0){
						getFriendsResult(arg0);
						tempUser = arg0;
					}
				}
			});
		}
	}
	
	public void getFriendsResult(List<User> resultList){
		LayoutInflater infla = getLayoutInflater();
		for(User userData : resultList){
			if(userData != null){
				if(tempUser != null && tempUser.size() > 0){
					for(User tempData : tempUser){
						if(tempData.getObjectId().equals(userData.getObjectId())){
							return;
						}
					}
				}
				simpleFriendView = infla.inflate(R.layout.simple_friends_list, null,false);
				
				friendsNameText = (TextView)simpleFriendView.findViewById(R.id.friends_name_text);
				friendsPersonalSignText = (TextView)simpleFriendView.findViewById(R.id.friends_personal_sign_text);
				addFriendsButton = (Button)simpleFriendView.findViewById(R.id.add_as_friends_button);
				addFriendsButton.setVisibility(View.VISIBLE);
				addFriendsButton.setTag(userData);
				
				addFriendsButton.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View arg0) {
						if(arg0.getTag() instanceof User){
							addFriend((User)arg0.getTag());
						}
					}
				});
				
				if(!StringUtils.isEmpty(userData.getUsername())){
					friendsNameText.setText(userData.getUsername());					
				}
				if(!StringUtils.isEmpty(userData.getPersonalSign())){
					friendsPersonalSignText.setText(userData.getPersonalSign());
				}
				
				friendsResultLayout.addView(simpleFriendView);
			}
		}
	}
	
	private void addFriend(User friendsInfo){
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		//发送tag请求
		BmobChatManager.getInstance(this).sendTagMessage(MsgTag.ADD_CONTACT, friendsInfo.getObjectId(),new PushListener() {
			
			@Override
			public void onSuccess() {
				progress.dismiss();
				mToast.showMyToast("发送请求成功，等待对方验证！" , Toast.LENGTH_SHORT);
			}
			
			@Override
			public void onFailure(int arg0, final String arg1) {
				progress.dismiss();
				mToast.showMyToast("发送请求失败:"+arg1 , Toast.LENGTH_SHORT);
			}
		});
	}
}
