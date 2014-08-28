package com.chuck.relativeschat.bean;

import java.io.Serializable;
import java.util.List;

import com.chuck.relativeschat.entity.ShareFileBean;

import android.graphics.Bitmap;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-25 下午3:01:41
 * @author chengang
 * @version 1.0
 */
public class UserShareFileBean{

	String fileId;
	String fileUrl;
	String fileName;
	String shareUser;
	String createDate;
	String fileType;
	String shareToUser;
	String isShareToAll;
	String fileShareNumber;
	String wordRemarkNumber;
	String isGoodNumber;
	
	public String getFileUrl() {
		return fileUrl;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
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
	public String getShareToUser() {
		return shareToUser;
	}
	public void setShareToUser(String shareToUser) {
		this.shareToUser = shareToUser;
	}
	public String getIsShareToAll() {
		return isShareToAll;
	}
	public void setIsShareToAll(String isShareToAll) {
		this.isShareToAll = isShareToAll;
	}
	public String getFileShareNumber() {
		return fileShareNumber;
	}
	public String getWordRemarkNumber() {
		return wordRemarkNumber;
	}
	public String getIsGoodNumber() {
		return isGoodNumber;
	}
	public void setFileShareNumber(String fileShareNumber) {
		this.fileShareNumber = fileShareNumber;
	}
	public void setWordRemarkNumber(String wordRemarkNumber) {
		this.wordRemarkNumber = wordRemarkNumber;
	}
	public void setIsGoodNumber(String isGoodNumber) {
		this.isGoodNumber = isGoodNumber;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
}
