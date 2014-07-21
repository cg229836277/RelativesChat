package com.chuck.relativeschat.fragment;

import java.util.Map;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FriendsListFragment extends Fragment {
	private View fActivityView;
	private RelativesChatApplication rcApp;
	private Map<String, BmobChatUser> chatUserList;
	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
////		LayoutInflater infla = getActivity().getLayoutInflater();
////		fActivityView = infla.inflate(R.layout.friends_list_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
//	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

//		ViewGroup p = (ViewGroup)fActivityView.getParent();
//		if(p != null){
//			p.removeAllViewsInLayout();
//		}				

		rcApp = (RelativesChatApplication)getActivity().getApplication();
		if(rcApp != null){
			chatUserList = rcApp.getContactList();
			//有朋友列表
			if(chatUserList != null && chatUserList.size() > 0){
				LayoutInflater infla = getActivity().getLayoutInflater();
				fActivityView = infla.inflate(R.layout.friends_list_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
			}else{
				LayoutInflater infla = getActivity().getLayoutInflater();
				fActivityView = infla.inflate(R.layout.blank_layout, null,false);
				TextView blankRemarkText = (TextView)fActivityView.findViewById(R.id.content_empty_text);
				blankRemarkText.setText(getActivity().getResources().getString(R.string.no_friends_notify));
			}
		}
		
		
		return fActivityView;
	}
}
