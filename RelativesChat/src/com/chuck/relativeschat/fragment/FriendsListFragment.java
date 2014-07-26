package com.chuck.relativeschat.fragment;

import java.util.List;
import java.util.Map;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.adapter.FriendsChatListAdapter;
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
	private List<BmobChatUser> chatUserList;
	public BmobUserManager userManager;
	private LayoutInflater infla;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {			

		rcApp = (RelativesChatApplication)getActivity().getApplication();
		userManager = BmobUserManager.getInstance(this.getActivity().getApplicationContext());
		chatUserMap = rcApp.getContactList();
		
		infla = getActivity().getLayoutInflater();
		fActivityView = infla.inflate(R.layout.friends_list_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);			
		friendsListView = (ListView)fActivityView.findViewById(R.id.friends_list_view);
		mHeadViewLayout = (HeadViewLayout)fActivityView.findViewById(R.id.title_menu_layout);	
		initHeadTitle();			
		//本地有朋友列表
		if(chatUserMap != null && chatUserMap.size() > 0){
			chatUserList = CollectionUtils.map2list(chatUserMap);
			
			initChatUserList();		
		}else{
			updateUserInfos();
		}
			
		return fActivityView;
	}
	
	public void initChatUserList(){		
		adapter = new FriendsChatListAdapter(getActivity().getApplicationContext(),chatUserList);
		friendsListView.setAdapter(adapter);
		if(adapter != null && adapter.getCount() > 0){
			friendsListView.setSelection(adapter.getCount());
		}
	}
	
	public void updateUserInfos(){
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
			@Override
			public void onError(int arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				RelativesChatApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
				chatUserList = arg0;
				if(chatUserList != null && chatUserList.size() > 0){
					initChatUserList();
				}else{
					fActivityView = infla.inflate(R.layout.blank_layout, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
					TextView blankRemarkText = (TextView)fActivityView.findViewById(R.id.content_empty_text);
					blankRemarkText.setText(getActivity().getResources().getString(R.string.no_friends_notify));
					mHeadViewLayout = (HeadViewLayout)fActivityView.findViewById(R.id.bank_menu_header_list_layout);	
					initHeadTitle();
				}
			}
		});
	}
	
	public void initHeadTitle(){
		mHeadViewLayout.setBackButtonVisiable(View.GONE);
		mHeadViewLayout.setTitleText("亲朋列表");
	}
}
