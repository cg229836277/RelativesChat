package com.chuck.relativeschat.bean;

import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-17 下午4:44:32
 * @author chengang
 * @version 1.0
 */
public class PersonBean extends BmobObject {
	
	private String name;
    private String address;
    private String userId;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
