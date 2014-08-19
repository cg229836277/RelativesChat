package com.chuck.relativeschat.entity;

import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-19 下午2:07:41
 * @author chengang
 * @version 1.0
 */
public class ShareFileBean extends BmobObject {
	
	public static final String MUSIC = "music";
	public static final String PHOTO = "photo";
	public static final String VIDEO = "video";
	public static final String SOUNG = "sound";
	
	//文件名称
	String fileName;
	//文件的网络路径
	String filePath;
	//文件类型
	String fileType;
	//分享给所有用户 0/不是  1/是
	String isShareToAll;
	//分享人
	String shareUser;
	//分享给单个用户的id
	String shareTo;
	public String getFileName() {
		return fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public String getFileType() {
		return fileType;
	}
	public String getIsShareToAll() {
		return isShareToAll;
	}
	public String getShareUser() {
		return shareUser;
	}
	public String getShareTo() {
		return shareTo;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public void setIsShareToAll(String isShareToAll) {
		this.isShareToAll = isShareToAll;
	}
	public void setShareUser(String shareUser) {
		this.shareUser = shareUser;
	}
	public void setShareTo(String shareTo) {
		this.shareTo = shareTo;
	}
	
}
