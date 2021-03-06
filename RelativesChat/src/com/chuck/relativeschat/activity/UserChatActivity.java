package com.chuck.relativeschat.activity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.adapter.FriendsChatListAdapter;
import com.chuck.relativeschat.base.MyMessageReceiver;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.tools.CollectionUtils;
import com.chuck.relativeschat.tools.EmoticonsEditText;
import com.chuck.relativeschat.tools.NetworkTool;
import com.chuck.relativeschat.tools.StringUtils;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UserChatActivity extends BaseActivity implements IXListViewListener , EventListener , OnClickListener {

	private HeadViewLayout mHeadViewLayout;
	
	private BmobChatUser currentChatUser;
	
	private RelativesChatApplication rcApp;
	
	private XListView chatListView;
	
	private static int MsgPagerNum;
	private LinearLayout layout_more, layout_emo, layout_add;
	private Button btn_chat_send, btn_chat_add,btn_chat_keyboard, btn_speak;
//	private Button btn_chat_emo;
//	private Button btn_chat_voice;
	private EmoticonsEditText edit_user_comment;
	
	private String chatUserId;//聊天对象的id
	
	private FriendsChatListAdapter chatListAdapter;
	public static final int NEW_MESSAGE = 0x001;//新消息
	private boolean isNetConnected;//网络连接情况
//	private PersonBean beanData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_chat);
		
		rcApp = (RelativesChatApplication)getApplication();
		
		chatManager = BmobChatManager.getInstance(this);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		
		MsgPagerNum = 1;
		
		currentChatUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		
		if(currentChatUser != null){
			chatUserId = currentChatUser.getObjectId();
			if(!StringUtils.isEmpty(currentChatUser.getNick())){
				mHeadViewLayout.setTitleText("与" + currentChatUser.getNick() + "聊天");		
			}else{
				mHeadViewLayout.setTitleText("与" + currentChatUser.getUsername() + "聊天");		
			}
		}
		initView();
	}
	
	public void initView(){
		chatListView = (XListView)findViewById(R.id.mListView);
		
		initSendMessageView();//初始化发送视图中的控件
		
		initChatListView();//初始化聊天列表
	}
	
	public void initChatListView(){
		chatListView.setPullLoadEnable(false);
		chatListView.setPullRefreshEnable(true);
		chatListView.setXListViewListener(this);
		chatListView.pullRefreshing();
		chatListView.setDividerHeight(0);
		initOrUpdateChatList();
		chatListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				layout_more.setVisibility(View.GONE);
				layout_add.setVisibility(View.GONE);
//				btn_chat_voice.setVisibility(View.VISIBLE);
				btn_chat_keyboard.setVisibility(View.GONE);
				btn_chat_send.setVisibility(View.GONE);
				return false;
			}
		});
//
//		// �ط���ť�ĵ���¼�
//		mAdapter.setOnInViewClickListener(R.id.iv_fail_resend,
//				new MessageChatAdapter.onInternalClickListener() {
//
//					@Override
//					public void OnClickListener(View parentV, View v,
//							Integer position, Object values) {
//						// �ط���Ϣ
//						showResendDialog(parentV, v, values);
//					}
//				});
	}
	
	public void initOrUpdateChatList(){
		if (chatListAdapter != null) {
			int chatMsgCount = chatListAdapter.getCount();
			if(chatMsgCount > 0){
				initMsgData(chatMsgCount / 10 , false);
			}
		} else {
			initMsgData(1 , true);
		}
	}
	
	private void initMsgData(int pageIndex , final boolean isInit) {
		
		String systemUserId = rcApp.getCurrentUser().getObjectId();
		String chatUserId = currentChatUser.getObjectId();
		String mixConversationStr1 = systemUserId + "&" + chatUserId;
		String mixConversationStr2 = chatUserId + "&" + systemUserId;
		String[] conversationList = {mixConversationStr1 , mixConversationStr2};
		
		BmobQuery<BmobMsg> msgQuery = new BmobQuery<BmobMsg>();
		if(pageIndex >= 0){
			msgQuery.setLimit(pageIndex * 10);
		}
		msgQuery.addWhereContainedIn("conversationId", Arrays.asList(conversationList));
		msgQuery.order("-createdAt");
		msgQuery.addWhereEqualTo("msgType", 1);
		msgQuery.findObjects(getApplicationContext(), new FindListener<BmobMsg>() {
			
			@Override
			public void onSuccess(List<BmobMsg> arg0) {
				if(arg0 != null && arg0.size() > 0){
					
					int currents = 0;
					
					Collections.reverse(arg0);
					
					if(chatListAdapter != null){
						currents = chatListAdapter.getCount();
					}
					
					if(arg0.size() < currents){
						mToast.showMyToast("没有更多消息", Toast.LENGTH_SHORT);
						return;
					}
					if(!isInit){
						chatListView.setSelection(chatListAdapter.getCount() - currents - 1);
						chatListAdapter.notifyDataSetChanged();
					}else{
						chatListAdapter = new FriendsChatListAdapter(UserChatActivity.this, arg0);
						chatListView.setAdapter(chatListAdapter);
						chatListView.setSelection(chatListAdapter.getCount() - 1);
					}
				}else{
					chatListAdapter = new FriendsChatListAdapter(UserChatActivity.this, arg0);
					chatListView.setAdapter(chatListAdapter);
					chatListView.setSelection(chatListAdapter.getCount() - 1);
				}
				
				chatListAdapter.setList(arg0);
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				mToast.showMyToast("请求消息出错", Toast.LENGTH_SHORT);
			}
		});
	}
	
	public void initSendMessageView(){
		layout_more = (LinearLayout) findViewById(R.id.layout_more);
		layout_emo = (LinearLayout) findViewById(R.id.layout_emo);
		layout_add = (LinearLayout) findViewById(R.id.layout_add);
		btn_chat_add = (Button) findViewById(R.id.btn_chat_add);
//		btn_chat_emo = (Button) findViewById(R.id.btn_chat_emo);
		btn_chat_add.setOnClickListener(this);
//		btn_chat_emo.setOnClickListener(this);

		btn_chat_keyboard = (Button) findViewById(R.id.btn_chat_keyboard);
//		btn_chat_voice = (Button) findViewById(R.id.btn_chat_voice);
//		btn_chat_voice.setOnClickListener(this);
		btn_chat_keyboard.setOnClickListener(this);
		btn_chat_send = (Button) findViewById(R.id.btn_chat_send);
		btn_chat_send.setOnClickListener(this);
		btn_chat_send.setVisibility(View.VISIBLE);

//		btn_speak = (Button) findViewById(R.id.btn_speak);
		
		edit_user_comment = (EmoticonsEditText) findViewById(R.id.edit_user_comment);
		edit_user_comment.setOnClickListener(this);
		edit_user_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {
				if (!TextUtils.isEmpty(s)) {
//					btn_chat_send.setVisibility(View.VISIBLE);
					btn_chat_keyboard.setVisibility(View.GONE);
//					btn_chat_voice.setVisibility(View.GONE);
				}
//				else {
//					if (btn_chat_voice.getVisibility() != View.VISIBLE) {
////						btn_chat_voice.setVisibility(View.VISIBLE);
//						btn_chat_send.setVisibility(View.VISIBLE);
//						btn_chat_keyboard.setVisibility(View.GONE);
//					}
//				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.edit_user_comment://发送文字消息
			if(chatListAdapter != null){
				chatListView.setSelection(chatListAdapter.getCount() - 1);
			}
			if (layout_more.getVisibility() == View.VISIBLE) {
				layout_add.setVisibility(View.GONE);
				layout_emo.setVisibility(View.GONE);
				layout_more.setVisibility(View.GONE);
			}
			break;
		case R.id.btn_chat_send://发送事件
			final String msg = edit_user_comment.getText().toString();
			if (msg.equals("")) {
				mToast.showMyToast("请先输入要发送的消息", Toast.LENGTH_SHORT);
				return;
			}
			isNetConnected = NetworkTool.isNetworkConnected(getApplicationContext());
			if (!isNetConnected) {
				mToast.showMyToast("请先连接网络", Toast.LENGTH_SHORT);
				// return;
			}
			BmobMsg message = BmobMsg.createTextSendMsg(this, chatUserId, msg);
			System.out.println("发消息给" + currentChatUser.getUsername() + chatUserId);
			
			chatManager.sendTextMessage(currentChatUser, message);

			refreshMessage(message);

			break;
		default:
			break;
		}
	}
	
	private void refreshMessage(BmobMsg msg) {
		chatListAdapter.add(msg);
		chatListView.setSelection(chatListAdapter.getCount() - 1);
		edit_user_comment.setText("");
	}


	@Override
	public void onAddUser(BmobInvitation arg0) {
		
	}

	@Override
	public void onMessage(BmobMsg arg0) {
		Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
		handlerMsg.obj = arg0;
		handler.sendMessage(handlerMsg);
	}

	@Override
	public void onNetChange(boolean arg0) {
		if (NetworkTool.isNetworkConnected(getApplicationContext())) {
			mToast.showMyToast("请先连接网络", Toast.LENGTH_SHORT);
		}
	}

	@Override
	public void onOffline() {
		mToast.showMyToast("请先连接网络", Toast.LENGTH_SHORT);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == NEW_MESSAGE) {
				BmobMsg message = (BmobMsg) msg.obj;
				String uid = message.getBelongId();
				if (!uid.equals(chatUserId))//
				{
					return;
				}
				chatListAdapter.add(message);
				message.setIsReaded(BmobConfig.STATE_READED);
				BmobChatManager.getInstance(getApplicationContext()).saveReceiveMessage(true, message);
				chatListView.setSelection(chatListAdapter.getCount() - 1);
			}
		}
	};

	@Override
	public void onReaded(String arg0, String arg1) {
		if (arg0.split("&")[1].equals(chatUserId)) {
			for (BmobMsg msg : chatListAdapter.getList()) {
				if (msg.getConversationId().equals(arg0)&& msg.getMsgTime().equals(arg1)) {
					msg.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
				}
				chatListAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				MsgPagerNum++;
				initMsgData(MsgPagerNum , false);
				chatListView.stopRefresh();
			}
		}, 1000);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initOrUpdateChatList();
		MyMessageReceiver.ehList.add(this);//
		MyMessageReceiver.mNewNum=0;
		BmobNotifyManager.getInstance(this).cancelNotify();
		BmobDB.create(this).resetUnread(chatUserId);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);
	}

	@Override
	public void onLoadMore() {
		
	}
}
