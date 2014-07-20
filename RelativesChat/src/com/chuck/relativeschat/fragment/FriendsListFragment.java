package com.chuck.relativeschat.fragment;

import com.chuck.relativeschat.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsListFragment extends Fragment {
private View fActivityView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater infla = getActivity().getLayoutInflater();
		fActivityView = infla.inflate(R.layout.friends_list_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup)fActivityView.getParent();
		if(p != null){
			p.removeAllViewsInLayout();
		}
		return fActivityView;
	}
}
