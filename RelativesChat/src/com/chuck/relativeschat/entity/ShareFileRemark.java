package com.chuck.relativeschat.entity;

import cn.bmob.v3.BmobObject;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-28 上午11:42:07
 * @author chengang
 * @version 1.0
 */
public class ShareFileRemark extends BmobObject {

	String remarkUser;
	String remarkType;
	String remarkContent;
	String remarkFileUrl;
	String remarkFileId;
	String isGood;
	public final static String REMARK_TYPE_SHARE = "share";
	public final static String REMARK_TYPE_WORD = "word";
	public final static String REMARK_TYPE_GOOD = "good";
	public String getRemarkUser() {
		return remarkUser;
	}
	public String getRemarkType() {
		return remarkType;
	}
	public String getRemarkContent() {
		return remarkContent;
	}
	public String getRemarkFileUrl() {
		return remarkFileUrl;
	}
	public String getRemarkFileId() {
		return remarkFileId;
	}
	public String getIsGood() {
		return isGood;
	}
	public void setRemarkUser(String remarkUser) {
		this.remarkUser = remarkUser;
	}
	public void setRemarkType(String remarkType) {
		this.remarkType = remarkType;
	}
	public void setRemarkContent(String remarkContent) {
		this.remarkContent = remarkContent;
	}
	public void setRemarkFileUrl(String remarkFileUrl) {
		this.remarkFileUrl = remarkFileUrl;
	}
	public void setRemarkFileId(String remarkFileId) {
		this.remarkFileId = remarkFileId;
	}
	public void setIsGood(String isGood) {
		this.isGood = isGood;
	}
}
