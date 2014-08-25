package com.chuck.relativeschat.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.bmob.im.bean.BmobChatUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.Share.activity.ShareToMyFriendActivity;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.UserBean;
import com.chuck.relativeschat.bean.UserInfoBean;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;

public class MyFriendsListViewAdapter extends FriendsBaseListAdapter<UserInfoBean> {
	
	private Context mContext;
	
	public MyFriendsListViewAdapter(Context context, List<UserInfoBean> list) {
		super(context, list);
		mContext = context;
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.simple_friends_list_view_layout, null);
		}
		
		final UserInfoBean data = (UserInfoBean) getList().get(position);
		ImageView simpleUserIconView = ViewHolder.get(convertView, R.id.simple_user_icon_image);
		TextView simpleUserNameText = ViewHolder.get(convertView, R.id.user_name_text);
		TextView simpleUserStateText = ViewHolder.get(convertView, R.id.user_state_text);
		
		simpleUserIconView.setImageBitmap(data.getIconBitmap());
		if(!StringUtils.isEmpty(data.getNickName())){
			simpleUserNameText.setText(data.getNickName());
		}else{
			simpleUserNameText.setText(data.getUserName());
		}
		simpleUserStateText.setText(data.getUserState());
		return convertView;
	}
}
