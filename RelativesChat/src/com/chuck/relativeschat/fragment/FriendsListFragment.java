package com.chuck.relativeschat.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.adapter.FriendsChatListAdapter;
import com.chuck.relativeschat.adapter.FriendsInvitationMessageAdapter;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.CollectionUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsListFragment extends Fragment {
	private View fActivityView;
	private RelativesChatApplication rcApp;
	private Map<String, BmobChatUser> chatUserMap;
	private HeadViewLayout mHeadViewLayout;
	private ListView friendsListView;
	private FriendsChatListAdapter adapter;
	private List<BmobChatUser> chatUserList = new ArrayList<BmobChatUser>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {			

		rcApp = (RelativesChatApplication)getActivity().getApplication();
		if(rcApp != null){
			chatUserMap = rcApp.getContactList();
			//有朋友列表
			if(chatUserMap != null && chatUserMap.size() > 0){
				LayoutInflater infla = getActivity().getLayoutInflater();
				fActivityView = infla.inflate(R.layout.friends_list_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
				
				friendsListView = (ListView)fActivityView.findViewById(R.id.friends_list_view);
				
				initChatUserList();
				
				mHeadViewLayout = (HeadViewLayout)fActivityView.findViewById(R.id.title_menu_layout);
				mHeadViewLayout.setBackButtonVisiable(View.GONE);
				mHeadViewLayout.setTitleText("亲朋列表");
				
			}else{
				LayoutInflater infla = getActivity().getLayoutInflater();
				fActivityView = infla.inflate(R.layout.blank_layout, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
				TextView blankRemarkText = (TextView)fActivityView.findViewById(R.id.content_empty_text);
				blankRemarkText.setText(getActivity().getResources().getString(R.string.no_friends_notify));
			}
		}
			
		return fActivityView;
	}
	
	public void initChatUserList(){
		
		chatUserList = CollectionUtils.map2list(chatUserMap);
		
		adapter = new FriendsChatListAdapter(getActivity().getApplicationContext(),chatUserList);
		friendsListView.setAdapter(adapter);
		if(adapter != null && adapter.getCount() > 0){
			friendsListView.setSelection(adapter.getCount());
		}
	}
}
