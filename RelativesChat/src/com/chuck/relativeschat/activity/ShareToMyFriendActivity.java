package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.UserInfoBean;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareToMyFriendActivity extends BaseActivity implements OnClickListener{

	private int[] imageViewId = {R.id.share_photo_icon , R.id.share_music_icon,
			R.id.share_video_icon,R.id.share_sound_icon};
	private HeadViewLayout mHeadViewLayout;
	private UserInfoBean simpleUserData;
	private ImageView userIconView;
	private TextView userNameText;
	private TextView userStateText;
	private RelativesChatApplication rcApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_to_my_friend);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("私密分享");
		rcApp = (RelativesChatApplication)getApplication();
		
		simpleUserData = rcApp.getCurrentUserInfoData();
		
		bindEvent();		
	}
	
	public void bindEvent(){
		for(int i = 0 ; i < imageViewId.length ; i++){
			ImageView iconsImageView = (ImageView)findViewById(imageViewId[i]);
			iconsImageView.setOnClickListener(this);
		}
		
		userNameText = (TextView)findViewById(R.id.my_friends_name_text);
		userStateText = (TextView)findViewById(R.id.my_friends_sign_text);
		
		userIconView = (ImageView)findViewById(R.id.friend_icon_view);
		if(simpleUserData != null){
			userIconView.setImageBitmap(simpleUserData.getIconBitmap());
			if(!StringUtils.isEmpty(simpleUserData.getNickName())){
				userNameText.setText(simpleUserData.getNickName());
			}else{
				userNameText.setText(simpleUserData.getUserName());
			}
			userStateText.setText(simpleUserData.getUserState());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_photo_icon:			
			break;
		case R.id.share_music_icon:			
			break;
		case R.id.share_video_icon:			
			break;
		case R.id.share_sound_icon:			
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		rcApp.setCurrentUserInfoData(null);
		super.onDestroy();
	}
}
