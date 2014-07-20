package com.chuck.relativeschat.activity;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.fragment.FriendsActivityFragment;
import com.chuck.relativeschat.fragment.FriendsListFragment;
import com.chuck.relativeschat.fragment.FriendsMoreInfoFragment;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMenuActivity extends FragmentActivity implements OnClickListener{

	private LinearLayout friendsLinearLayout;
	private ImageView friendsImage;
	private TextView friendsText;
	
	private LinearLayout friendsActivityLinearLayout;
	private ImageView friendsActivityImage;
	private TextView friendsActivityText;
	
	private LinearLayout friendsMoreLinearLayout;
	private ImageView friendsMoreImage;
	private TextView friendsMoreText;
	
	private ViewPager friendsViewPager;
	
	private FriendsActivityFragment activityFragment;
	private FriendsListFragment listFragment;
	private FriendsMoreInfoFragment moreInfoFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_menu);
		
		bindEvent();

	}
	
	private void bindEvent(){
		friendsLinearLayout = (LinearLayout)findViewById(R.id.friends_linearlayout);
		friendsImage = (ImageView)findViewById(R.id.frineds_icon);
		friendsText = (TextView)findViewById(R.id.friends_list_text);
		friendsImage.setOnClickListener(this);
		friendsLinearLayout.setOnClickListener(this);
		friendsText.setOnClickListener(this);
		
		friendsActivityLinearLayout = (LinearLayout)findViewById(R.id.friends_activity_linearlayout);
		friendsActivityImage = (ImageView)findViewById(R.id.frineds_activity_image);
		friendsActivityText = (TextView)findViewById(R.id.friends_activity_text);
		friendsActivityImage.setOnClickListener(this);
		friendsActivityLinearLayout.setOnClickListener(this);
		friendsActivityText.setOnClickListener(this);
		
		friendsMoreLinearLayout = (LinearLayout)findViewById(R.id.friends_more_linearlayout);
		friendsMoreImage = (ImageView)findViewById(R.id.frineds_more_image);
		friendsMoreText = (TextView)findViewById(R.id.friends_more_text);
		friendsMoreImage.setOnClickListener(this);
		friendsMoreLinearLayout.setOnClickListener(this);
		friendsMoreText.setOnClickListener(this);
		
		initViewPager();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.friends_linearlayout:
			friendsImage.performClick();
			friendsListClicked();
			break;
		case R.id.frineds_icon:
			friendsListClicked();
			break;
		case R.id.friends_list_text:
			friendsImage.performClick();
			friendsListClicked();
			break;
		case R.id.friends_activity_linearlayout:
			friendsActivityImage.performClick();
			friendsActivityListClicked();
			break;
		case R.id.frineds_activity_image:
			friendsActivityListClicked();
			break;
		case R.id.friends_activity_text:
			friendsActivityImage.performClick();
			friendsActivityListClicked();
			break;			
		case R.id.friends_more_linearlayout:
			friendsMoreImage.performClick();
			friendsMoreClicked();
			break;
		case R.id.frineds_more_image:
			friendsMoreClicked();
			break;
		case R.id.friends_more_text:
			friendsMoreImage.performClick();
			friendsMoreClicked();
			break;
			
		default:
			break;
		}
	}
	
	public void friendsListClicked(){
		
	}
	
	public void friendsActivityListClicked(){
		
	}
	
	public void friendsMoreClicked(){
		
	}	
	
	public void initViewPager(){
		friendsViewPager = (ViewPager)findViewById(R.id.friends_info_viewpage);
		activityFragment = new FriendsActivityFragment();
		listFragment = new FriendsListFragment();
		moreInfoFragment = new FriendsMoreInfoFragment();
		friendsViewPager.setAdapter(new FriendsFragmentPagerAdapter(getSupportFragmentManager()));
	}
	
	public class FriendsFragmentPagerAdapter extends FragmentPagerAdapter{

		public FriendsFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case 0:
				friendsImage.performClick();
				return listFragment;
			case 1:	
				friendsActivityImage.performClick();
				return activityFragment;
			case 2:
				friendsMoreImage.performClick();
				return moreInfoFragment;
			default:
				break;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 3;
		}
		
		
		
	}
}
