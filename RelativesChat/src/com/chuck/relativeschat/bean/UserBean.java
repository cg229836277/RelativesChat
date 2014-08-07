package com.chuck.relativeschat.bean;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-21 上午10:17:23
 * @author chengang
 * @version 1.0
 */
public class UserBean{
	//个性签名
	private String userState;
	//好友列表
	private BmobChatUser chatUserData;
	public String getUserState() {
		return userState;
	}
	public void setUserState(String userState) {
		this.userState = userState;
	}
	public BmobChatUser getChatUserData() {
		return chatUserData;
	}
	public void setChatUserData(BmobChatUser chatUserData) {
		this.chatUserData = chatUserData;
	}

}
