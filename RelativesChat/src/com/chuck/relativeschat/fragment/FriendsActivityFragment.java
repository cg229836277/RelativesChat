package com.chuck.relativeschat.fragment;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.HeadViewLayout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsActivityFragment extends Fragment {
	
	private View fActivityView;
	private HeadViewLayout mHeadViewLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		LayoutInflater infla = getActivity().getLayoutInflater();
		fActivityView = infla.inflate(R.layout.friends_activity_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
		
		mHeadViewLayout = (HeadViewLayout)fActivityView.findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.GONE);
		mHeadViewLayout.setTitleText("亲朋动态");
		
		
		return fActivityView;
	}
}
