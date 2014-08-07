package com.chuck.relativeschat.entity;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-17 下午4:44:32
 * @author chengang
 * @version 1.0
 */
public class PersonBean extends BmobChatUser {
	
	private String nickName;
    private String address;
    private String userId;
    private String userState;
    
	public String getNickName() {
		return nickName;
	}
	public String getAddress() {
		return address;
	}
	public String getUserId() {
		return userId;
	}
	public String getUserState() {
		return userState;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setUserState(String userState) {
		this.userState = userState;
	}

}
