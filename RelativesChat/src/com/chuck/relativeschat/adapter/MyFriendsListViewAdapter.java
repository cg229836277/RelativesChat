package com.chuck.relativeschat.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.bean.UserInfoBean;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MyFriendsListViewAdapter extends FriendsBaseListAdapter<UserInfoBean>{
	
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
		final ImageView simpleUserIconView = ViewHolder.get(convertView, R.id.simple_user_icon_image);
		TextView simpleUserNameText = ViewHolder.get(convertView, R.id.user_name_text);
		TextView simpleUserStateText = ViewHolder.get(convertView, R.id.user_state_text);
		
		simpleUserIconView.setImageBitmap(data.getIconBitmap());
		if(!StringUtils.isEmpty(data.getNickName())){
			simpleUserNameText.setText(data.getNickName());
		}else{
			simpleUserNameText.setText(data.getUserName());
		}
		if(!StringUtils.isEmpty(data.getAvatorUrl())){
			ImageLoader.getInstance().displayImage(data.getAvatorUrl(), simpleUserIconView, new ImageLoadingListener() {
				
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					simpleUserIconView.setImageResource(R.drawable.chat_add_picture_normal);
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view , FailReason failReason) {
					simpleUserIconView.setImageResource(R.drawable.chat_add_picture_normal);
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					Bitmap tempBitmap = PhotoUtil.toRoundCorner(loadedImage, 120);
					simpleUserIconView.setImageBitmap(tempBitmap);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					simpleUserIconView.setImageResource(R.drawable.chat_add_picture_normal);
				}
			});
		}
		simpleUserStateText.setText(data.getUserState());
		return convertView;
	}
}
