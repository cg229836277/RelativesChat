package com.chuck.relativeschat.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.StringUtils;

public class FriendsShareAdapter extends FriendsBaseListAdapter<ShareFileBean> implements OnClickListener{

	public FriendsShareAdapter(Context context, List<ShareFileBean> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.simple_friends_share_item, null);
		}
		final ShareFileBean data = (ShareFileBean) getList().get(position);
		ImageView fileTypeImage  = ViewHolder.get(convertView, R.id.share_type_image);
		TextView shareDesc = ViewHolder.get(convertView, R.id.share_desc_text);
		LinearLayout shareFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_share_layout);
		LinearLayout wordFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_word_layout);
		LinearLayout goodFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_good_layout);
		
		shareFeedBaLayout.setOnClickListener(this);
		wordFeedBaLayout.setOnClickListener(this);
		goodFeedBaLayout.setOnClickListener(this);
		
		String fileType = null;
		
		if(data != null){
			if(!StringUtils.isEmpty(data.getFileType())){
				fileType = data.getFileType();
				if(fileType.equals(ShareFileBean.PHOTO)){
					fileTypeImage.setImageResource(R.drawable.send_photo);
					fileType = "照片";
				}else if(fileType.equals(ShareFileBean.MUSIC)){
					fileTypeImage.setImageResource(R.drawable.send_music);
					fileType = "音乐";
				}else if(fileType.equals(ShareFileBean.VIDEO)){
					fileTypeImage.setImageResource(R.drawable.send_video);
					fileType = "短视频";
				}else if(fileType.equals(ShareFileBean.SOUNG)){
					fileTypeImage.setImageResource(R.drawable.send_sound);
					fileType = "语音";
				}
				
				if(!StringUtils.isEmpty(data.getShareUser())){	
					String date = data.getCreatedAt().substring(0, 11);
					String desc = "来自" + data.getShareUser() +"的" + fileType +  "  " +date;
					StringBuffer buff = new StringBuffer(desc);
					shareDesc.setText(buff);
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

		default:
			break;
		}
	}

}
