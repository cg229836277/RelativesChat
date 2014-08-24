package com.chuck.relativeschat.biz.impl;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.adapter.FriendsShareAdapter;
import com.chuck.relativeschat.biz.GetFriendsShareDataBiz;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;

public class GetFriendsShareDataBizImpl implements GetFriendsShareDataBiz {
	private Context mContext;
	private List<ShareFileBean> data = null;
	private int count = 0;
	public GetFriendsShareDataBizImpl(Context context){
		mContext = context;
	}
	
	@Override
	public List<ShareFileBean> getAllFriendsShareData() {
		BmobQuery<ShareFileBean> dataQuery = new BmobQuery<ShareFileBean>();
		dataQuery.addWhereEqualTo("isShareToAll", "1");
		dataQuery.findObjects(mContext, new FindListener<ShareFileBean>() {			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					data = arg0;
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				System.out.println("查找好友分享数据  " + arg1);
			}
		});
		return data;
	}

	@Override
	public List<ShareFileBean> getSimpleFriendsShareData(String currentUserId) {
		BmobQuery<ShareFileBean> dataQuery = new BmobQuery<ShareFileBean>();
		dataQuery.addWhereEqualTo("isShareToAll", "0");
		dataQuery.addWhereEqualTo("shareTo", currentUserId);
		dataQuery.findObjects(mContext, new FindListener<ShareFileBean>() {			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					data = arg0;
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				
			}
		});
		return data;
	}

	@Override
	public int getAllFriendsShareDataNumber() {
		BmobQuery<ShareFileBean> dataQuery = new BmobQuery<ShareFileBean>();
		dataQuery.addWhereEqualTo("isShareToAll", "1");
		dataQuery.findObjects(mContext, new FindListener<ShareFileBean>() {			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					count = arg0.size();
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				System.out.println("查找好友分享数目  " + arg1);
			}
		});
		return count;
	}
}
