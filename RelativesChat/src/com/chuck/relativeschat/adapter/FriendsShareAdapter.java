package com.chuck.relativeschat.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.Share.activity.WatchShareImageActivity;
import com.chuck.relativeschat.bean.UserShareFileBean;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FriendsShareAdapter extends FriendsBaseListAdapter<UserShareFileBean> implements OnClickListener{

	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private List<String> urlList = new ArrayList<String>();
	
	public FriendsShareAdapter(Context context, List<UserShareFileBean> list) {
		super(context, list);
		if(IsListNotNull.isListNotNull(urlList)){
			urlList.clear();
		}
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.simple_friends_share_item, null);
		}
		final UserShareFileBean data = (UserShareFileBean) getList().get(position);
		TextView shareDesc = ViewHolder.get(convertView, R.id.share_desc_text);
		LinearLayout watchFeedBaLayout = ViewHolder.get(convertView, R.id.watch_share_layout);
		LinearLayout shareFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_share_layout);
		LinearLayout wordFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_word_layout);
		LinearLayout goodFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_good_layout);
		
		watchFeedBaLayout.setOnClickListener(this);
		shareFeedBaLayout.setOnClickListener(this);
		wordFeedBaLayout.setOnClickListener(this);
		goodFeedBaLayout.setOnClickListener(this);
		
		watchFeedBaLayout.setTag(position);
		
		String fileType = null;
		
		if(data != null){
			if(!StringUtils.isEmpty(data.getFileType())){
				fileType = data.getFileType();
				if(fileType.equals(ShareFileBean.PHOTO)){
//					fileTypeImage.setImageResource(R.drawable.send_photo);
					fileType = "照片";
				}else if(fileType.equals(ShareFileBean.MUSIC)){
					fileType = "音乐";
				}else if(fileType.equals(ShareFileBean.VIDEO)){
					fileType = "短视频";
				}else if(fileType.equals(ShareFileBean.SOUNG)){
					fileType = "语音";
				}
				
				if(!StringUtils.isEmpty(data.getShareUser())){	
					String date = data.getCreateDate().substring(0, 11);
					String desc = "来自" + data.getShareUser() +"的" + fileType +  "  " +date;
					StringBuffer buff = new StringBuffer(desc);
					shareDesc.setText(buff);
				}
				if(!StringUtils.isEmpty(data.getFileUrl())){
					urlList.add(data.getFileUrl());
				}
			}			
		}		
		return convertView;
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.feedback_share_layout:
			
			break;
		case R.id.feedback_word_layout:
			
			break;
		case R.id.feedback_good_layout:
			
			break;
		case R.id.watch_share_layout:
			if(arg0.getTag() instanceof Integer){
				int position = (Integer)arg0.getTag();
				String[] urlArray = (String[])urlList.toArray(new String[urlList.size()]);
				Intent intent = new Intent(mContext , WatchShareImageActivity.class);
				intent.putExtra(WatchShareImageActivity.POSITION, position);
				intent.putExtra(WatchShareImageActivity.IMAGE_URL, urlArray);
				mContext.startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
	
	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
