package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.common.HeadViewLayout;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShareToMyFriendActivity extends BaseActivity implements OnClickListener{

	private int[] imageViewId = {R.id.share_photo_icon , R.id.share_music_icon,
			R.id.share_video_icon,R.id.share_file_icon,R.id.share_sound_icon};
	private HeadViewLayout mHeadViewLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_to_my_friend);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("私密分享");
		
		bindEvent();		
	}
	
	public void bindEvent(){
		for(int i = 0 ; i < imageViewId.length ; i++){
			ImageView iconsImageView = (ImageView)findViewById(imageViewId[i]);
			iconsImageView.setOnClickListener(this);
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
		case R.id.share_file_icon:			
			break;
		case R.id.share_sound_icon:			
			break;
		default:
			break;
		}
	}
}
