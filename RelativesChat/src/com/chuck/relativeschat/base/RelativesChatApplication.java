package com.chuck.relativeschat.base;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;

import com.chuck.relativeschat.entity.User;
import com.chuck.relativeschat.tools.SharePreferenceUtil;

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
	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mInstance = this;
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
}
