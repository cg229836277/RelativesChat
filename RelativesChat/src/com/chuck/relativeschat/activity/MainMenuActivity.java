package com.chuck.relativeschat.activity;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.MyMessageReceiver;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.fragment.FriendsActivityFragment;
import com.chuck.relativeschat.fragment.FriendsListFragment;
import com.chuck.relativeschat.fragment.FriendsMoreInfoFragment;
import com.chuck.relativeschat.tools.NetworkTool;

import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends FragmentActivity implements OnClickListener , EventListener{

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
	
	private HeadViewLayout mHeadViewLayout;
	private ImageView messageTipsImage;
	private RelativesChatApplication rcApp;
	
	private int currentTabIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_menu);
		rcApp = (RelativesChatApplication)getApplication();
		
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
		
		messageTipsImage = (ImageView)findViewById(R.id.message_red_dot_image);
		if(rcApp.getIsExistMoreInfoMessage()){
			messageTipsImage.setVisibility(View.VISIBLE);
		}
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.GONE);
		mHeadViewLayout.setTitleText("亲朋列表");
		
		initViewPager();
		setViewPageListener();
		friendsViewPager.setCurrentItem(0);
	}

	@Override
	public void onClick(View arg0) {
		android.app.FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
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
		
		ft.commit();
	}
	
	public void friendsListClicked(){
		currentTabIndex = 0;
		friendsViewPager.setCurrentItem(0);
	}
	
	public void friendsActivityListClicked(){
		currentTabIndex = 1;
		friendsViewPager.setCurrentItem(1);
	}
	
	public void friendsMoreClicked(){
		currentTabIndex = 2;
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
					break;
				case 1:
					break;
				case 2:
					break;

				default:
					break;
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}

	@Override
	public void onAddUser(BmobInvitation arg0) {
		boolean isAllow = RelativesChatApplication.getInstance().getSpUtil().isAllowVoice();
		
		if(isAllow){
//			RelativesChatApplication.getInstance().getMediaPlayer().start();
		}
		
		String tickerText = arg0.getFromname()+"请求添加你为好友";
		boolean isAllowVibrate = RelativesChatApplication.getInstance().getSpUtil().isAllowVibrate();
		BmobNotifyManager.getInstance(this).showNotify(isAllow,isAllowVibrate,R.drawable.ic_launcher, tickerText, arg0.getFromname(), tickerText.toString(),FriendsInvitionMessageActivity.class);
		
	}

	@Override
	public void onMessage(BmobMsg arg0) {
		BmobChatManager.getInstance(this).saveReceiveMessage(true,arg0);		
	}

	@Override
	public void onNetChange(boolean arg0) {
		if (NetworkTool.isNetworkConnected(getApplicationContext())) {
			Toast.makeText(this , "请先连接网络", Toast.LENGTH_SHORT).show();
		}	
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReaded(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MyMessageReceiver.ehList.add(this);//
		MyMessageReceiver.mNewNum=0;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MyMessageReceiver.ehList.remove(this);//
	}
}
