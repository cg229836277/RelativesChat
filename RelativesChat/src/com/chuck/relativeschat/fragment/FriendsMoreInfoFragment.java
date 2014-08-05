package com.chuck.relativeschat.fragment;


import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.FindFriendsActivity;
import com.chuck.relativeschat.activity.FriendsInvitionMessageActivity;
import com.chuck.relativeschat.activity.ModefyUserInfoActivity;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.bean.PersonBean;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.ImageLoadOptions;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FriendsMoreInfoFragment extends Fragment implements OnClickListener{
	private View fActivityView;
	private RelativeLayout currentUserLayout;
	private RelativeLayout addFriendsLayout;
	private RelativeLayout addFriendsMessageLayout;
	
	private ImageView myIconImage;
	private TextView myNameText;
	private TextView myDetailText;
	
	private ImageView addFriendsIconImage;
	private TextView addFriendsText;
	private TextView addFriendsDetailText;
	
	private ImageView addFriendsMessageIconImage;
	private TextView addFriendsMessageText;
	private TextView addFriendsMessageDetailText;
	
	private HeadViewLayout mHeadViewLayout;
	private ImageView messageTipsImage;
	
	private BmobChatUser currentUser;
	
	private  RelativesChatApplication rcApp;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		LayoutInflater infla = getActivity().getLayoutInflater();
		fActivityView = infla.inflate(R.layout.friends_more_info_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
		rcApp = (RelativesChatApplication)getActivity().getApplication();
		
		currentUser = rcApp.getCurrentUser();
		
		initView();
		
		return fActivityView;
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = null;
		switch (arg0.getId()) {
		case R.id.current_add_friends_layout:
			intent = new Intent(getActivity().getApplicationContext() , FindFriendsActivity.class);			
			break;
		case R.id.add_friends_message_layout:
			intent = new Intent(getActivity().getApplicationContext() , FriendsInvitionMessageActivity.class);
			break;
		case R.id.current_user_info_layout:
			intent = new Intent(getActivity().getApplicationContext() , ModefyUserInfoActivity.class);
			startActivityForResult(intent, 0);
			intent = null;
			break;
		default:
			break;
		}	
		
		if(intent != null){
			startActivity(intent);
		}
	}
	
	public void initView(){
		currentUserLayout = (RelativeLayout)fActivityView.findViewById(R.id.current_user_info_layout);
		currentUserLayout.setOnClickListener(this);
		addFriendsLayout = (RelativeLayout)fActivityView.findViewById(R.id.current_add_friends_layout);
		addFriendsLayout.setOnClickListener(this);
		myIconImage = (ImageView)currentUserLayout.findViewById(R.id.friends_icon_image);
		myNameText = (TextView)currentUserLayout.findViewById(R.id.friends_name_text);
		myDetailText = (TextView)currentUserLayout.findViewById(R.id.friends_personal_sign_text);		
		updateUserInfo();
		
		addFriendsMessageLayout = (RelativeLayout)fActivityView.findViewById(R.id.add_friends_message_layout);
		addFriendsMessageLayout.setOnClickListener(this);
		
		addFriendsIconImage = (ImageView)addFriendsLayout.findViewById(R.id.friends_icon_image);
		addFriendsText = (TextView)addFriendsLayout.findViewById(R.id.friends_name_text);
		addFriendsDetailText = (TextView)addFriendsLayout.findViewById(R.id.friends_personal_sign_text);
		addFriendsDetailText.setVisibility(View.GONE);
		addFriendsIconImage.setBackgroundResource(R.drawable.add_user);
		addFriendsText.setText(getResources().getString(R.string.find_friends));
		
		addFriendsMessageIconImage = (ImageView)addFriendsMessageLayout.findViewById(R.id.friends_icon_image);
		addFriendsMessageText = (TextView)addFriendsMessageLayout.findViewById(R.id.friends_name_text);
		addFriendsMessageDetailText = (TextView)addFriendsMessageLayout.findViewById(R.id.friends_personal_sign_text);
		messageTipsImage = (ImageView)addFriendsMessageLayout.findViewById(R.id.msg_tips_image);
		addFriendsMessageDetailText.setVisibility(View.GONE);
		addFriendsMessageIconImage.setBackgroundResource(R.drawable.add_user_message);
		addFriendsMessageText.setText(getResources().getString(R.string.invite_friends));
		
		if(rcApp.getIsExistMoreInfoMessage()){
			//有消息的时候就显示小红点
			messageTipsImage.setVisibility(View.VISIBLE);
		}
		
		mHeadViewLayout = (HeadViewLayout)fActivityView.findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.GONE);
		mHeadViewLayout.setTitleText("更多");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		updateUserInfo();		
	}
	
	public void updateUserInfo(){
		if(rcApp.getPersonDetailData() != null){
			PersonBean currentUser  = rcApp.getPersonDetailData();
			myNameText.getPaint().setFakeBoldText(true);//加粗
			if(!StringUtils.isEmpty(currentUser.getNickName())){
				myNameText.setText(currentUser.getNickName());
			}else{
				myNameText.setText(currentUser.getUsername());
			}
			if(!StringUtils.isEmpty(currentUser.getUserState())){
				myDetailText.setVisibility(View.VISIBLE);
				myDetailText.setText(currentUser.getUserState());
			}else{
				myDetailText.setVisibility(View.GONE);
			}
			if(!StringUtils.isEmpty(currentUser.getAvatar())){
				ImageLoader.getInstance().displayImage(currentUser.getAvatar(), myIconImage, ImageLoadOptions.getOptions());
			}else{
				myIconImage.setBackgroundResource(R.drawable.default_head);
			}
		}
	}
	
}
