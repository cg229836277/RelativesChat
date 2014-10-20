package com.chuck.relativeschat.base;

import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobChat;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import com.baidu.mapapi.SDKInitializer;

import com.chuck.relativeschat.bean.UserInfoBean;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.tools.SharePreferenceUtil;
import com.chuck.relativeschat.tools.StringUtils;

import android.app.Application;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-17 下午5:13:28
 * @author chengang
 * @version 1.0
 */
public class RelativesChatApplication extends Application {
	
	private static RelativesChatApplication mInstance;
	public BmobChatUser currentUser;
	public PersonBean personDetailData;
	public List<PersonBean> myFriendsDataBean/* = new ArrayList<PersonBean>()*/;
	private Map<String, BmobChatUser> contactList /*= new HashMap<String, BmobChatUser>()*/;
	public boolean isExistMoreInfoMessage;//更多里面是否存在消息
	public UserInfoBean currentUserInfoData;
	
	@Override
	public void onCreate() {
		super.onCreate();
		BmobChat.DEBUG_MODE = true;
		mInstance = this;
		
		SDKInitializer.initialize(this);
	}
	
	public BmobChatUser getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(BmobChatUser currentUser) {
		this.currentUser = currentUser;
	}
	
	public static RelativesChatApplication getInstance() {
		return mInstance;
	}

	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		
//		if (contactList != null) {
//			if(contactList.size() > 0){
//				this.contactList.clear();
//				this.contactList.putAll(contactList);
//			}
//		}
		this.contactList = contactList;
	}
	
	public void logout() {
		BmobUserManager.getInstance(getApplicationContext()).logout();
		setContactList(null);
	}
	
	SharePreferenceUtil mSpUtil;
	public static final String PREFERENCE_NAME = "_sharedinfo";

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			String currentId = BmobUserManager.getInstance(
					getApplicationContext()).getCurrentUserObjectId();
			String sharedName = currentId + PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

	public boolean getIsExistMoreInfoMessage() {
		return isExistMoreInfoMessage;
	}

	public void setExistMoreInfoMessage(boolean isExistMoreInfoMessage) {
		this.isExistMoreInfoMessage = isExistMoreInfoMessage;
	}

	public PersonBean getPersonDetailData() {
		return personDetailData;
	}

	public void setPersonDetailData(PersonBean personDetailData) {
		this.personDetailData = personDetailData;
	}

	public List<PersonBean> getMyFriendsDataBean() {
		return myFriendsDataBean;
	}

	public void setMyFriendsDataBean(List<PersonBean> myFriendsDataBean) {
		this.myFriendsDataBean = myFriendsDataBean;
//		this.myFriendsDataBean.addAll(myFriendsDataBean);
	}
	
	public PersonBean getMyChatUser(String chatUserId){
		if(StringUtils.isEmpty(chatUserId)){
			return null;
		}
		for(PersonBean data : this.myFriendsDataBean){
			if(chatUserId.equals(currentUser.getObjectId())){
				return data;
			}
		}
		return null;
	}

	public UserInfoBean getCurrentUserInfoData() {
		return currentUserInfoData;
	}

	public void setCurrentUserInfoData(UserInfoBean currentUserInfoData) {
		this.currentUserInfoData = currentUserInfoData;
	}
}
