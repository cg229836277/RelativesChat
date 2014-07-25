package com.chuck.relativeschat.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.FriendsInvitionMessageActivity;
import com.chuck.relativeschat.activity.MainMenuActivity;
import com.chuck.relativeschat.fragment.FriendsListFragment;
import com.chuck.relativeschat.tools.CollectionUtils;
import com.chuck.relativeschat.tools.NetworkTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;

public class MyMessageReceiver extends BroadcastReceiver {

	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();
	
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;//
	BmobUserManager userManager;
	BmobChatUser currentUser;

	@Override
	public void onReceive(Context context, Intent intent) {
		String json = intent.getStringExtra("msg");
		BmobLog.i("Receive Message = " + json);
		userManager = BmobUserManager.getInstance(context);
		currentUser = userManager.getCurrentUser();
		boolean isNetConnected = NetworkTool.isNetworkConnected(context);
		if(isNetConnected){
			if(currentUser!=null){
				parseMessage(context, json);
			}
		}else{
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			//获取到的消息标签
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if(tag.equals(BmobConfig.TAG_OFFLINE)){
				if (ehList.size() > 0) {
					for (EventListener handler : ehList)
						handler.onOffline();
				}else{
					//离线状态就登出系统
					RelativesChatApplication.getInstance().logout();
				}
			}else{
				//发消息的人
				String fromId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETID);
				//收消息的人
				String toId = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TOID);
				if(fromId!=null && !BmobDB.create(context,toId).isBlackUser(fromId)){
					if(TextUtils.isEmpty(tag)){
						BmobMsg msg =BmobMsg.createReceiveMsg(context,json);
						if(toId.equals(currentUser.getObjectId())){
							if (ehList.size() > 0) {
								for (int i = 0; i < ehList.size(); i++) {
									((EventListener) ehList.get(i)).onMessage(msg);
								}
							} else {
								BmobChatManager.getInstance(context).saveReceiveMessage(true,msg);
								boolean isAllow = RelativesChatApplication.getInstance().getSpUtil().isAllowPushNotify();
								if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
									mNewNum++;
									showNotification(context,msg);
								}
							}
						}
					}else{
						if(tag.equals(BmobConfig.TAG_ADD_CONTACT)){//添加对话
							BmobInvitation message = BmobInvitation.createReceiverInvitation(json);
							BmobDB.create(context,toId).saveInviteMessage(message);
							if(toId.equals(currentUser.getObjectId())){
								if (ehList.size() > 0) {
									for (EventListener handler : ehList)
										handler.onAddUser(message);
								}else{
									boolean isAllow = RelativesChatApplication.getInstance().getSpUtil().isAllowPushNotify();
									boolean isAllowVoice = RelativesChatApplication.getInstance().getSpUtil().isAllowVoice();
									boolean isAllowVibrate = RelativesChatApplication.getInstance().getSpUtil().isAllowVibrate();
									if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
										String tickerText = message.getFromname()+"";
										BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,isAllowVibrate,R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(),FriendsInvitionMessageActivity.class);
									}
								}
							}
						}else if(tag.equals(BmobConfig.TAG_ADD_AGREE)){
							String username = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TARGETUSERNAME);
							BmobUserManager.getInstance(context).addContactAfterAgree(username, new FindListener<BmobChatUser>() {
								
								@Override
								public void onError(int arg0, final String arg1) {
									
								}
								
								@Override
								public void onSuccess(List<BmobChatUser> arg0) {
									RelativesChatApplication.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
								}
							});
							
							boolean isAllow = RelativesChatApplication.getInstance().getSpUtil().isAllowPushNotify();
							boolean isAllowVoice = RelativesChatApplication.getInstance().getSpUtil().isAllowVoice();
							boolean isAllowVibrate = RelativesChatApplication.getInstance().getSpUtil().isAllowVibrate();
							if(isAllow && currentUser!=null && currentUser.getObjectId().equals(toId)){
								String tickerText = username+"";
								BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,isAllowVibrate,R.drawable.ic_launcher, tickerText, username, tickerText.toString(),MainMenuActivity.class);
							}
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
							
						}else if(tag.equals(BmobConfig.TAG_READED)){
							String conversionId = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_CONVERSIONID);
							String msgTime = BmobJsonUtil.getString(jo,BmobConstant.PUSH_READED_MSGTIME);
							
							BmobChatManager.getInstance(context).updateMsgStatus(BmobConfig.STATUS_SEND_RECEIVERED, conversionId, msgTime);
							if(toId.equals(currentUser.getObjectId())){
								if (ehList.size() > 0) {
									for (EventListener handler : ehList)
										handler.onReaded(conversionId, msgTime);
								}
							}
						}
					}
				}else{
					BmobLog.i("该用户已经加入黑名单");
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
			BmobLog.i("parseMessage"+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @Title: showNotify
	 * @return void
	 * @throws
	 */
	public void showNotification(Context context,BmobMsg msg) {
		// ����֪ͨ��
		int icon = R.drawable.ic_launcher;
		String trueMsg = "";
		if(msg.getMsgType()==BmobConfig.TYPE_TEXT && msg.getContent().contains("\\ue")){
			trueMsg = "[文字]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_IMAGE){
			trueMsg = "[图片]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_VOICE){
			trueMsg = "[声音]";
		}else if(msg.getMsgType()==BmobConfig.TYPE_LOCATION){
			trueMsg = "[位置]";
		}else{
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername()+ " (" + mNewNum + ")";
		
		Intent intent = new Intent(context, MainMenuActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		boolean isAllowVoice = RelativesChatApplication.getInstance().getSpUtil().isAllowVoice();
		boolean isAllowVibrate = RelativesChatApplication.getInstance().getSpUtil().isAllowVibrate();
		
		BmobNotifyManager.getInstance(context).showNotifyWithExtras(isAllowVoice,isAllowVibrate,icon, tickerText.toString(), contentTitle, tickerText.toString(),intent);
		
	}
	
}
