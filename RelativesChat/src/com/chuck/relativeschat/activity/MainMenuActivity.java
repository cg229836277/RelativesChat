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
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
//		friendsImage.setFocusable(true);
//		friendsImage.setFocusableInTouchMode(true);
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
		setViewPageListener();
		friendsViewPager.setCurrentItem(0);
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
		friendsViewPager.setCurrentItem(0);
	}
	
	public void friendsActivityListClicked(){
		friendsViewPager.setCurrentItem(1);
	}
	
	public void friendsMoreClicked(){
		friendsViewPager.setCurrentItem(2);
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
				return listFragment;
			case 1:	
				return activityFragment;
			case 2:
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
	
	public void setViewPageListener(){
		friendsViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
//					friendsImage.requestFocus();
//					friendsImage.requestFocusFromTouch();
					break;
				case 1:
//					friendsImage.clearFocus();
//					friendsMoreImage.clearFocus();
//					friendsActivityImage.requestFocus();
//					friendsActivityImage.requestFocusFromTouch();
					break;
				case 2:
//					friendsMoreImage.requestFocus();
//					friendsMoreImage.requestFocusFromTouch();
					break;

				default:
					break;
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
