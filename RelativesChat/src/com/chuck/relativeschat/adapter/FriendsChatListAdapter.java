package com.chuck.relativeschat.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.tools.FaceTextUtils;
import com.chuck.relativeschat.tools.ImageLoadOptions;
import com.chuck.relativeschat.tools.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-7-28 上午9:11:55
 * @author chengang
 * @version 1.0
 */
public class FriendsChatListAdapter extends FriendsBaseListAdapter<BmobMsg> {
	//发送文字消息
	private final int TYPE_RECEIVER_TXT = 0;
	private final int TYPE_SEND_TXT = 1;
	
	String currentObjectId = "";
	
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	
	/**
	 * @param context
	 * @param list
	 */
	public FriendsChatListAdapter(Context context, List<BmobMsg> list) {
		super(context, list);
		
		currentObjectId = BmobUserManager.getInstance(context).getCurrentUserObjectId();
	}
	
	@Override
	public int getItemViewType(int position) {
		BmobMsg msg = list.get(position);
		return msg.getBelongId().equals(currentObjectId) ? TYPE_SEND_TXT: TYPE_RECEIVER_TXT;
	}
	
	@Override
	public int getViewTypeCount() {
		return 8;
	}

	@Override
	public View bindView(int position, View convertView, ViewGroup parent) {
		final BmobMsg item = list.get(position);
		
		if (convertView == null) {
			convertView = createViewByType(item, position);
		}
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
		
		final ImageView iv_fail_resend = ViewHolder.get(convertView, R.id.iv_fail_resend);//
		final TextView tv_send_status = ViewHolder.get(convertView, R.id.tv_send_status);
		
		TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
		TextView tv_message = ViewHolder.get(convertView, R.id.tv_message);		
		final ProgressBar progress_load = ViewHolder.get(convertView, R.id.progress_load);//	
		TextView tv_location = ViewHolder.get(convertView, R.id.tv_location);	
		
		String avatar = item.getBelongAvatar();
//		if(avatar!=null && !avatar.equals("")){
//			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions(),animateFirstListener);
//		}else{
		iv_avatar.setImageResource(R.drawable.chat_default);
//		}
		
		tv_time.setText(TimeUtil.getChatTime(Long.parseLong(item.getMsgTime())));
		
		if(getItemViewType(position)==TYPE_SEND_TXT)
//				||getItemViewType(position)==TYPE_SEND_IMAGE//ͼƬ��������
//				||getItemViewType(position)==TYPE_SEND_LOCATION
//				||getItemViewType(position)==TYPE_SEND_VOICE){//ֻ���Լ����͵���Ϣ�����ط�����
			if(item.getStatus()==BmobConfig.STATUS_SEND_SUCCESS){//
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_send_status.setVisibility(View.GONE);
//					tv_voice_length.setVisibility(View.VISIBLE);
				}else{
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("发送成功");
				}
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_FAIL){//
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.VISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
//					tv_voice_length.setVisibility(View.GONE);
				}
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){//�Է��ѽ��յ�
				progress_load.setVisibility(View.INVISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
					tv_send_status.setVisibility(View.GONE);
//					tv_voice_length.setVisibility(View.VISIBLE);
				}else{
					tv_send_status.setVisibility(View.VISIBLE);
					tv_send_status.setText("已接收");
				}
			}else if(item.getStatus()==BmobConfig.STATUS_SEND_START){//��ʼ�ϴ�
				progress_load.setVisibility(View.VISIBLE);
				iv_fail_resend.setVisibility(View.INVISIBLE);
				tv_send_status.setVisibility(View.INVISIBLE);
				if(item.getMsgType()==BmobConfig.TYPE_VOICE){
//					tv_voice_length.setVisibility(View.GONE);
				}
			}
		
		//
		final String text = item.getContent();
		switch (item.getMsgType()) {
		case BmobConfig.TYPE_TEXT:
			try {
				SpannableString spannableString = FaceTextUtils.toSpannableString(mContext, text);
				tv_message.setText(spannableString);
			} catch (Exception e) {
				
			}
			break;
		default:
			break;
		}		
		return convertView;
	}
	
	private View createViewByType(BmobMsg message, int position) {
		int type = message.getMsgType();
		
		if(type==BmobConfig.TYPE_TEXT){
			return getItemViewType(position) == TYPE_RECEIVER_TXT ? 
					mInflater.inflate(R.layout.item_chat_received_message, null):
					mInflater.inflate(R.layout.item_chat_sent_message, null);
		}
		return null;
	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

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
