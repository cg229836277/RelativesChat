package com.chuck.relativeschat.entity;

import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-28 上午11:44:33
 * @author chengang
 * @version 1.0
 */
public class FileRemarkBean extends BmobObject {
	
	String shareFileId;
	String shareFileRemarkId;
	public String getShareFileId() {
		return shareFileId;
	}
	public String getShareFileRemarkId() {
		return shareFileRemarkId;
	}
	public void setShareFileId(String shareFileId) {
		this.shareFileId = shareFileId;
	}
	public void setShareFileRemarkId(String shareFileRemarkId) {
		this.shareFileRemarkId = shareFileRemarkId;
	}
}
