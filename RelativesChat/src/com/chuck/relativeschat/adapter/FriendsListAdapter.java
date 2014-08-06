package com.chuck.relativeschat.adapter;

import java.util.List;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.bean.PersonBean;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.tools.ImageLoadOptions;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-25 下午3:23:39
 * @author chengang
 * @version 1.0
 */
public class FriendsListAdapter extends FriendsBaseListAdapter<BmobChatUser> {

	/**
	 * @param context
	 * @param list
	 */
	private List<PersonBean> mbeanData;
	public FriendsListAdapter(Context context, List<BmobChatUser> datalist , List<PersonBean> list ) {
		super(context, datalist);
		this.mbeanData = list;
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.simple_friends_list, null);
		}
		final BmobChatUser msg = (BmobChatUser) getList().get(position);
//		final PersonBean data = (PersonBean) getList().get(position);
		//用户姓名
		TextView nameText = ViewHolder.get(convertView, R.id.friends_name_text);
		//用户个性签名
		TextView personalSignText = ViewHolder.get(convertView, R.id.friends_personal_sign_text);
		
		nameText.setText(msg.getNick()); 
		
		for(PersonBean data : mbeanData){
			if(msg.getObjectId().equals(data.getObjectId())){
				if(!StringUtils.isEmpty(data.getUserState()) && !StringUtils.isEmpty(data.getNickName())){
					personalSignText.setText(data.getUserState());
					nameText.setText(data.getNickName());
				}else{
					personalSignText.setText("好友最近没有什么动态");
					nameText.setText(msg.getUsername());
				}
				break;
			}
		}
		
		//用户图像
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.friends_icon_image);

		String avatar = msg.getAvatar();

		if (!StringUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
		iv_avatar.setImageResource(R.drawable.default_head);
		}
		return convertView;
	}

}
