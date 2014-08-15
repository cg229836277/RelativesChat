package com.chuck.relativeschat.activity;

import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.id;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.tools.CollectionUtils;
import com.chuck.relativeschat.tools.HttpDownloader;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.PhotoUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyMainMenuActivity extends BaseActivity implements OnClickListener{

	private int[] parentViewId = {R.id.user_layout_child_1 , R.id.sound_layout,
			R.id.photo_layout , R.id.main_menu_video_layout , R.id.image_layout,
			R.id.music_layout , R.id.main_menu_setting_layout};
	
	private LinearLayout parentViewLayout;//正对多个icon的父视图
	private LinearLayout userParentLayout;//好友图标的父视图，单独处理
	private LinearLayout specialUserLayout;//排名靠前的好友
	private ImageView userIconImage;
	private RelativesChatApplication rcApp;
	private Map<String, BmobChatUser> chatUserMap;
	private List<BmobChatUser> chatUserList;
	private TextView userNumberText;
	private ImageView firstUserIconImage;
	private TextView firstUserNameText;
	private ImageView secondUserIconImage;
	private TextView secondUserNameText;
	private TextView userNameText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_main_menu);
		bindEvent();
	}
	
	public void bindEvent(){
		for(int i = 0 ; i < parentViewId.length ; i++){
			parentViewLayout = (LinearLayout)findViewById(parentViewId[i]);
			parentViewLayout.setOnClickListener(this);
		}
		
		initUserView();
		initSoundView();
		initImageView();
		initVideoView();
		initPhotoView();
		initMusicView();
		initSettingView();
	}
	
	/**
	 * 初始化好友的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:07
	 */
	public void initUserView(){
		userParentLayout = (LinearLayout)findViewById(parentViewId[0]);
		specialUserLayout = (LinearLayout)findViewById(R.id.main_menu_user_icons);
		
		userIconImage = (ImageView)findViewById(R.id.user_icon_image);
		userNumberText = (TextView)findViewById(R.id.my_friends_number_text);//没有用户时
		
		userNameText = (TextView)findViewById(R.id.main_menu_user_text);//我的好友
		
		firstUserIconImage = (ImageView)findViewById(R.id.first_user_icon);
		firstUserNameText = (TextView)findViewById(R.id.first_user_name);
		secondUserIconImage = (ImageView)findViewById(R.id.second_user_icon);
		secondUserNameText = (TextView)findViewById(R.id.second_user_name);
		
		initUserViewData();
	}
	
	/**
	 * 初始化录音的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:28
	 */
	public void initSoundView(){
		
	}
	
	/**
	 * 初始化图片的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:28
	 */
	public void initImageView(){
		
	}
	
	/**
	 * 初始化视频的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:28
	 */
	public void initVideoView(){
		
	}
	
	/**
	 * 初始化照相的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:28
	 */
	public void initPhotoView(){
		
	}
	
	/**
	 * 初始化音乐的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:28
	 */
	public void initMusicView(){
		
	}
	
	/**
	 * 初始化设置的视图
	 * 
	 * @author chengang
	 * @date 2014-8-15 上午10:44:28
	 */
	public void initSettingView(){
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.user_layout_child_1:
			intent = new Intent(this , UserListViewActivity.class);
			break;
		case R.id.sound_layout:	
			break;
		case R.id.photo_layout:
			break;
		case R.id.image_layout:
			break;
		case R.id.music_layout:
			break;
		case R.id.main_menu_setting_layout:
			break;
		case R.id.main_menu_video_layout:
			break;
		case R.id.main_menu_user_text:
			userParentLayout.performClick();
//			userIconImage.performClick();
			break;
		case R.id.user_icon_image:
			userParentLayout.performClick();
			break;
		case R.id.first_user_icon:
			break;
		case R.id.second_user_icon:
			break;
		default:
			break;
		}
		
		if(intent != null){
			startActivity(intent);
		}
	}
	
	public void initUserViewData(){
		rcApp = (RelativesChatApplication)getApplication();
		chatUserMap = rcApp.getContactList();
		if(chatUserMap != null && chatUserMap.size() > 0){
			chatUserList = CollectionUtils.map2list(chatUserMap);
			if(IsListNotNull.isListNotNull(chatUserList)){
				
				userNumberText.setVisibility(View.GONE);
				specialUserLayout.setVisibility(View.VISIBLE);
				
				for(int i = 0 ; i < chatUserList.size() ; i++){
					if(chatUserList.get(i).getObjectId().equals(rcApp.getCurrentUser().getObjectId())){
						chatUserList.remove(i);//从列表中删除自己
						break;
					}
				}
				
				//开始设置排名靠前的两位好友，将其图像和用户名放在首页显示	
				new AsyncTask<Void , Void, Void>() {
					
					Bitmap firstUserBitmap;
					Bitmap secondUserBitmap;
					String firstUserName;
					String secondUserName;
					
					@Override
					protected Void doInBackground(Void... params) {
						for(int i = 0 ; i < chatUserList.size() ; i++){
							String imageUrl = chatUserList.get(i).getAvatar();
							if(i == 0){
								firstUserName = chatUserList.get(0).getUsername();
								firstUserBitmap = HttpDownloader.downfile(imageUrl, firstUserName);
								firstUserBitmap = PhotoUtil.toRoundCorner(firstUserBitmap, 120);
							}else if(i == 1){
								secondUserName = chatUserList.get(1).getUsername();
								secondUserBitmap = HttpDownloader.downfile(imageUrl, secondUserName);
								secondUserBitmap = PhotoUtil.toRoundCorner(secondUserBitmap, 120);
							}
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);					
						firstUserIconImage.setImageBitmap(firstUserBitmap);
						firstUserNameText.setText(firstUserName);
					
						secondUserIconImage.setImageBitmap(secondUserBitmap);
						secondUserNameText.setText(secondUserName);
					}
				}.execute();	
			}
		}
	}
}