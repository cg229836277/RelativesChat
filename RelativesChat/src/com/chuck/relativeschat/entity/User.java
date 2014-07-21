package com.chuck.relativeschat.entity;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-21 上午10:17:23
 * @author chengang
 * @version 1.0
 */
public class User extends BmobChatUser {
	//昵称
	private String userNickName;
	//备注名称
	private String userRemarkName;
	//状态
	private String userState;
	//个性签名
	private String personalSign;
	//最新动态
	private String currentActivity;
	//图像
	private String userIcon;
	
	public String getFriendsNickName() {
		return userNickName;
	}
	public String getFriendsRemarkName() {
		return userRemarkName;
	}
	public String getFriendsState() {
		return userState;
	}
	public String getPersonalSign() {
		return personalSign;
	}
	public String getCurrentActivity() {
		return currentActivity;
	}
	public String getFriendsIcon() {
		return userIcon;
	}
	public void setFriendsNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public void setFriendsRemarkName(String userRemarkName) {
		this.userRemarkName = userRemarkName;
	}
	public void setFriendsState(String userState) {
		this.userState = userState;
	}
	public void setPersonalSign(String personalSign) {
		this.personalSign = personalSign;
	}
	public void setCurrentActivity(String currentActivity) {
		this.currentActivity = currentActivity;
	}
	public void setFriendsIcon(String userIcon) {
		this.userIcon = userIcon;
	}
}
