package com.chuck.relativeschat.bean;

import android.graphics.Bitmap;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-25 下午3:01:41
 * @author chengang
 * @version 1.0
 */
public class UserShareFileBean {

	String fileUrl;
	String fileName;
	Bitmap fileBitmap;
	String shareUser;
	String createDate;
	String fileType;
	public String getFileUrl() {
		return fileUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public Bitmap getFileBitmap() {
		return fileBitmap;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setFileBitmap(Bitmap fileBitmap) {
		this.fileBitmap = fileBitmap;
	}
	public String getShareUser() {
		return shareUser;
	}
	public String getCreateDate() {
		return createDate;
	}
	public String getFileType() {
		return fileType;
	}
	public void setShareUser(String shareUser) {
		this.shareUser = shareUser;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
