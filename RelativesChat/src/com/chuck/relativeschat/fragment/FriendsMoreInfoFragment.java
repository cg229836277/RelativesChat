package com.chuck.relativeschat.fragment;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.FindFriendsActivity;
import com.chuck.relativeschat.common.HeadViewLayout;

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
	
	private ImageView addFriendsIconImage;
	private TextView addFriendsText;
	private TextView addFriendsDetailText;
	private HeadViewLayout mHeadViewLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		LayoutInflater infla = getActivity().getLayoutInflater();
		fActivityView = infla.inflate(R.layout.friends_more_info_fragment, (ViewGroup)getActivity().findViewById(R.id.friends_info_viewpage),false);
		
		currentUserLayout = (RelativeLayout)fActivityView.findViewById(R.id.current_user_info_layout);
		addFriendsLayout = (RelativeLayout)fActivityView.findViewById(R.id.current_add_friends_layout);
		addFriendsLayout.setOnClickListener(this);
		
		addFriendsIconImage = (ImageView)addFriendsLayout.findViewById(R.id.friends_icon_image);
		addFriendsText = (TextView)addFriendsLayout.findViewById(R.id.friends_name_text);
		addFriendsDetailText = (TextView)addFriendsLayout.findViewById(R.id.friends_personal_sign_text);
		addFriendsDetailText.setVisibility(View.GONE);
		addFriendsIconImage.setBackgroundResource(R.drawable.add_user);
		addFriendsText.setText(getResources().getString(R.string.find_friends));
		
		mHeadViewLayout = (HeadViewLayout)fActivityView.findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.GONE);
		mHeadViewLayout.setTitleText("更多");
		
		return fActivityView;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.current_add_friends_layout:
			Intent intent = new Intent(getActivity().getApplicationContext() , FindFriendsActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}		
	}
}
