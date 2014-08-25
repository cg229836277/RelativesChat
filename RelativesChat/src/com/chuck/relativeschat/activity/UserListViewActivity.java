package com.chuck.relativeschat.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.UserDataHandler;

import cn.bmob.im.bean.BmobChatUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.Share.activity.ShareToMyFriendActivity;
import com.chuck.relativeschat.adapter.MyFriendsListViewAdapter;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.UserBean;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UserListViewActivity extends BaseActivity implements OnItemClickListener{

	private HeadViewLayout mHeadViewLayout;
	private ListView friendsListView;
	private RelativesChatApplication rcApp;
	private List<PersonBean> personBeanDataList;
	private BmobChatUser currentUser;
	public static final String USER_DATA = "userData";
	public static final String USER_INDEX = "index";
	private List<UserInfoBean> userInfoList;
	private MyFriendsListViewAdapter adapter;
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
		friendsListView = (ListView)findViewById(R.id.my_friends_list_view);
		friendsListView.setOnItemClickListener(this);
	}
	
	public void addUserDataForSimpleView(){

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
					adapter = new MyFriendsListViewAdapter(getApplicationContext(), result);
					friendsListView.setAdapter(adapter);
				}
				dialog.dismiss();
			}
			
		}.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(adapter.getItem(arg2) instanceof UserInfoBean){
			UserInfoBean data = (UserInfoBean)adapter.getItem(arg2);
			rcApp.setCurrentUserInfoData(data);
			Intent intent = new Intent(this , ShareToMyFriendActivity.class);
			startActivity(intent);
		}
	}
}
