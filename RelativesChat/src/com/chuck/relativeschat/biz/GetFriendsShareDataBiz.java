package com.chuck.relativeschat.biz;

import java.util.List;

import com.chuck.relativeschat.entity.ShareFileBean;

public interface GetFriendsShareDataBiz {

	public List<ShareFileBean> getAllFriendsShareData();
	
	public int getAllFriendsShareDataNumber();
	
	public List<ShareFileBean> getSimpleFriendsShareData(String currentUserId);
}
