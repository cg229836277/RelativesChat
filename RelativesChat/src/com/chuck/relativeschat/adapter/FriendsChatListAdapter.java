package com.chuck.relativeschat.adapter;

import java.util.List;

import com.chuck.relativeschat.R;
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
public class FriendsChatListAdapter extends FriendsBaseListAdapter<BmobChatUser> {

	/**
	 * @param context
	 * @param list
	 */
	public FriendsChatListAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.simple_friends_list, null);
		}
		final BmobChatUser msg = (BmobChatUser) getList().get(position);
		//用户姓名
		TextView nameText = ViewHolder.get(convertView, R.id.friends_name_text);
		//用户个性签名
		TextView personalSignText = ViewHolder.get(convertView, R.id.friends_personal_sign_text);
		//用户图像
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.friends_icon_image);

		String avatar = msg.getAvatar();

		if (!StringUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}
		return null;
	}

}
