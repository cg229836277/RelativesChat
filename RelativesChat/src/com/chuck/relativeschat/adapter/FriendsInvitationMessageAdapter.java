package com.chuck.relativeschat.adapter;

import java.util.List;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.tools.ImageLoadOptions;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.UpdateListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-24 下午4:24:00
 * @author chengang
 * @version 1.0
 * @param <E>
 */
public class FriendsInvitationMessageAdapter<E> extends FriendsBaseListAdapter<E> {

	/**
	 * @param context
	 * @param list
	 */
	public FriendsInvitationMessageAdapter(Context context, List<E> list) {
		super(context, list);
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobInvitation msg = (BmobInvitation) getList().get(position);
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);
		
		final Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

		String avatar = msg.getAvatar();

		if (!StringUtils.isEmpty(avatar)) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}

		int status = msg.getStatus();
		if(status==BmobConfig.INVITE_ADD_NO_VALIDATION){
			btn_add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					BmobLog.i("请求添加好友的id是:"+msg.getFromid());
					agressAdd(btn_add, msg);
				}
			});
		}else if(status==BmobConfig.INVITE_ADD_AGREE){
			btn_add.setTextColor(mContext.getResources().getColor(R.color.black));
			btn_add.setEnabled(false);
		}
		name.setText(msg.getFromname());
		
		return convertView;
	}
	
	private void agressAdd(final Button btn_add,final BmobInvitation msg){
		final ProgressDialog progress = new ProgressDialog(mContext);
		progress.setMessage("正在发送消息...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		try {
			BmobUserManager.getInstance(mContext).agreeAddContact(msg, new UpdateListener() {
				
				@Override
				public void onSuccess() {
					progress.dismiss();
					btn_add.setTextColor(mContext.getResources().getColor(R.color.black));
					btn_add.setEnabled(false);
					//添加好友到会话列表
					RelativesChatApplication.getInstance().setContactList(com.chuck.relativeschat.tools.CollectionUtils.list2map(BmobDB.create(mContext).getContactList()));	
				}
				
				@Override
				public void onFailure(int arg0, final String arg1) {
					progress.dismiss();
					ShowToast("添加失败: " +arg1);
				}
			});
		} catch (final Exception e) {
			progress.dismiss();
			ShowToast("添加出现异常: " +e.getMessage());
		}
	}

}
