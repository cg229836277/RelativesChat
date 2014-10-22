package com.chuck.relativeschat.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.common.DialogTips;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.service.GetUserLocationService;
import com.chuck.relativeschat.share.activity.FriendShareActivity;
import com.chuck.relativeschat.share.activity.ShareImageToFriendsActivity;
import com.chuck.relativeschat.share.activity.ShareSoundToFriendsActivity;
import com.chuck.relativeschat.share.activity.ShareVideoToFriendsActivity;
import com.chuck.relativeschat.tools.CollectionUtils;
import com.chuck.relativeschat.tools.HttpDownloader;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyMainMenuActivity extends BaseActivity implements OnClickListener{

	private int[] parentViewId = {R.id.user_layout_child_1 , R.id.sound_layout,
			R.id.photo_layout , R.id.main_menu_video_layout , R.id.share_layout,
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
	private TextView friendsShareNumber;
	private TextView shareVideoNumberText;
	private int count = 0;
	private GetUserLocationService  mMyService;  
	
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
		
		friendsShareNumber = (TextView)findViewById(R.id.friends_share_number_text);
		
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
		shareVideoNumberText = (TextView)findViewById(R.id.my_videos_number_text);
		setFriendsShareNumber(ShareFileBean.VIDEO);
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
			intent = new Intent(this , ShareSoundToFriendsActivity.class);
			break;
		case R.id.photo_layout:
			intent = new Intent(this , ShareImageToFriendsActivity.class);
			break;
		case R.id.share_layout:
			intent = new Intent(this , FriendShareActivity.class);
			break;
		case R.id.music_layout:
//			intent = new Intent(this , FriendsLocationActivity.class);
			intent = new Intent(this , BaiduMapTestActivity.class);
			break;
		case R.id.main_menu_setting_layout:
			intent = new Intent(this , SystemSettingActivity.class);
			break;
		case R.id.main_menu_video_layout:
			intent = new Intent(this , ShareVideoToFriendsActivity.class);
			break;
		case R.id.main_menu_user_text:
			userParentLayout.performClick();
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
			startActivityForResult(intent, 0);
		}
	}
	
	public void initUserViewData(){
		
		setFriendsShareNumber(null);
		
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			DialogTips dialog = new DialogTips(MyMainMenuActivity.this, "提示", "退出系统!", "确认", true, true);
			dialog.SetOnSuccessListener(new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					logoutSystem();
					deleteCacheVideoFile();
					finish();
				}				
			});
			
			dialog.show();
			dialog = null;
			
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
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
	
	public void logoutSystem(){
		BmobUserManager.getInstance(getApplicationContext()).logout();
		rcApp.setContactList(null);
		rcApp.setCurrentUser(null);
		rcApp.setPersonDetailData(null);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		setFriendsShareNumber(null);
	}
	
	public void setFriendsShareNumber(final String flag){		
		BmobQuery<ShareFileBean> dataQuery1 = new BmobQuery<ShareFileBean>();
		dataQuery1.addWhereEqualTo("isShareToAll", "1");
		BmobQuery<ShareFileBean> dataQuery2 = new BmobQuery<ShareFileBean>();
		dataQuery2.addWhereEqualTo("isShareToAll", "0");
		dataQuery2.addWhereEqualTo("shareTo", userManager.getCurrentUserObjectId());
		
		if(!StringUtils.isEmpty(flag)){
			dataQuery1.addWhereEqualTo("fileType", flag);
			dataQuery2.addWhereEqualTo("fileType", flag);
		}
		
		List<BmobQuery<ShareFileBean>> queries = new ArrayList<BmobQuery<ShareFileBean>>();
		queries.add(dataQuery2);
		queries.add(dataQuery1);
		BmobQuery<ShareFileBean> mainQuery = new BmobQuery<ShareFileBean>();
		mainQuery.or(queries);
		mainQuery.count(getApplicationContext(), ShareFileBean.class, new CountListener() {
			
			@Override
			public void onSuccess(int arg0) {
				if(StringUtils.isEmpty(flag)){
					friendsShareNumber.setText("" + arg0);	
				}else if(!StringUtils.isEmpty(flag) && flag.equals(ShareFileBean.VIDEO)){
					shareVideoNumberText.setText("" + arg0);	
				}
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				
			}
		});
	}

    private ServiceConnection mConnection = new ServiceConnection() {  
        public void onServiceConnected(ComponentName className,IBinder localBinder) {  
        	mMyService = ((GetUserLocationService.MyBinder)localBinder).getService();  
        }  
        public void onServiceDisconnected(ComponentName arg0) {  
        	mMyService = null;  
        }  
    }; 

    protected void onStart() {  
        super.onStart();  
        Intent intent = new Intent(this, GetUserLocationService.class);  
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);  
    }  
    
    @Override
    protected void onDestroy() {
    	unbindService(mConnection);  
    	super.onDestroy();
    }		
}
