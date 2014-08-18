package com.chuck.relativeschat.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-15 下午4:31:31
 * @author chengang
 * @version 1.0
 */
public class UserInfoBean{
	
	Bitmap iconBitmap;
	String userName;
	String nickName;
	String userState;
	String userId;
	public Bitmap getIconBitmap() {
		return iconBitmap;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserState() {
		return userState;
	}
	public void setIconBitmap(Bitmap iconBitmap) {
		this.iconBitmap = iconBitmap;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserState(String userState) {
		this.userState = userState;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
