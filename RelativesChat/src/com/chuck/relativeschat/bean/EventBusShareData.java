package com.chuck.relativeschat.bean;

import java.util.List;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-28 下午4:04:36
 * @author chengang
 * @version 1.0
 */
public class EventBusShareData {

	private List<UserShareFileBean> mFileData;
	
	public List<UserShareFileBean> getmFileData() {
		return mFileData;
	}

	public void setmFileData(List<UserShareFileBean> mFileData) {
		this.mFileData = mFileData;
	}

	public EventBusShareData(List<UserShareFileBean> shareFileData){
		this.mFileData = shareFileData;
	}
}
