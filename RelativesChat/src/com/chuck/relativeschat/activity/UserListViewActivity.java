package com.chuck.relativeschat.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.UserDataHandler;

import cn.bmob.im.bean.BmobChatUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.UserInfoBean;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.tools.HttpDownloader;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserListViewActivity extends BaseActivity implements OnClickListener{

	private HeadViewLayout mHeadViewLayout;
	private LinearLayout simpleFriendsContainerLayout;
	private RelativesChatApplication rcApp;
	private List<PersonBean> personBeanDataList;
	private LayoutInflater viewInflater;
	private BmobChatUser currentUser;
	private ImageView simpleUserIconView;
	private TextView simpleUserNameText;
	private TextView simpleUserStateText;
	private View simpleFriendsView;
	public static final String USER_DATA = "userData";
	public static final String USER_INDEX = "index";
	private List<UserInfoBean> userInfoList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_list_view);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("我的好友");
		
		bindEvent();
		addUserDataForSimpleView();
	}
	
	public void bindEvent(){
		simpleFriendsContainerLayout = (LinearLayout)findViewById(R.id.user_list_container_layout);
	}
	
	public void addUserDataForSimpleView(){
		
		viewInflater = LayoutInflater.from(getApplicationContext());
		rcApp = (RelativesChatApplication)getApplication();
		personBeanDataList = rcApp.getMyFriendsDataBean();
		currentUser = userManager.getCurrentUser();
				
		new AsyncTask<Void, List<UserInfoBean>, List<UserInfoBean>>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected List<UserInfoBean> doInBackground(Void... params) {
				
				if(IsListNotNull.isListNotNull(personBeanDataList)){
					List<UserInfoBean> infoBeanList = new ArrayList<UserInfoBean>();
					for(PersonBean data : personBeanDataList){
						if(!data.getObjectId().equals(currentUser.getObjectId())){									
							Bitmap tempBitmap = HttpDownloader.downfile(data.getAvatar(), data.getObjectId());
							tempBitmap = PhotoUtil.toRoundCorner(tempBitmap, 120);
							UserInfoBean infoBean = new UserInfoBean();
							infoBean.setIconBitmap(tempBitmap);
							infoBean.setUserName(data.getUsername());
							infoBean.setUserState(data.getUserState());
							infoBean.setNickName(data.getNickName());
							infoBean.setUserId(data.getObjectId());
							infoBeanList.add(infoBean);							
						}
					}
					return infoBeanList;
				}	
				return null;
			}
			
			@Override
			protected void onPostExecute(List<UserInfoBean> result) {
				super.onPostExecute(result);
				if(IsListNotNull.isListNotNull(result)){
					for(int i = 0 ; i < result.size() ; i++){
						UserInfoBean infoData = result.get(i);
						simpleFriendsView = viewInflater.inflate(R.layout.simple_friends_list_view_layout, null);
						simpleUserIconView = (ImageView)simpleFriendsView.findViewById(R.id.simple_user_icon_image);
						simpleUserNameText = (TextView)simpleFriendsView.findViewById(R.id.user_name_text);
						simpleUserStateText = (TextView)simpleFriendsView.findViewById(R.id.user_state_text);
						
						simpleUserIconView.setImageBitmap(infoData.getIconBitmap());
						if(!StringUtils.isEmpty(infoData.getNickName())){
							simpleUserNameText.setText(infoData.getNickName());
						}else{
							simpleUserNameText.setText(infoData.getUserName());
						}
						simpleUserStateText.setText(infoData.getUserState());
						
						simpleFriendsView.setOnClickListener(UserListViewActivity.this);
//						simpleUserIconView.setOnClickListener(UserListViewActivity.this);
//						simpleUserNameText.setOnClickListener(UserListViewActivity.this);
//						simpleUserStateText.setOnClickListener(UserListViewActivity.this);
						
						simpleFriendsView.setTag(i);
						simpleUserIconView.setTag(i);
						simpleUserNameText.setTag(i);
						simpleUserStateText.setTag(i);
						
						simpleFriendsContainerLayout.addView(simpleFriendsView);
					}
					userInfoList = result;
				}
				dialog.dismiss();
			}
			
		}.execute();
	}

	@Override
	public void onClick(View v) {
		View parentView = null;
		switch (v.getId()) {
		case R.layout.simple_friends_list_view_layout:
			break;
		default:
			break;
		}
		
		if(v.getTag() instanceof Integer){
			childViewClicked((Integer)v.getTag());
		}
	}
	
	public void childViewClicked(int index){
		if(IsListNotNull.isListNotNull(userInfoList)){
			rcApp.setCurrentUserInfoData(userInfoList.get(index));
		}
		Intent intent = new Intent(this , ShareToMyFriendActivity.class);
		startActivity(intent);
	}
}
