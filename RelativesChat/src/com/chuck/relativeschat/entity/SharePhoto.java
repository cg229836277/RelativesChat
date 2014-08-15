package com.chuck.relativeschat.entity;

import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-15 上午10:30:01
 * @author chengang
 * @version 1.0
 */
public class SharePhoto extends BmobObject {
	
	String photoSize;
	String photoPath;
	String shareUserId;
	String shareDate;
	String photoName;
	public String getPhotoSize() {
		return photoSize;
	}
	public String getPhotoPath() {
		return photoPath;
	}
	public String getShareUserId() {
		return shareUserId;
	}
	public String getShareDate() {
		return shareDate;
	}
	public String getPhotoName() {
		return photoName;
	}
	public void setPhotoSize(String photoSize) {
		this.photoSize = photoSize;
	}
	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}
	public void setShareUserId(String shareUserId) {
		this.shareUserId = shareUserId;
	}
	public void setShareDate(String shareDate) {
		this.shareDate = shareDate;
	}
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}

}
