package com.chuck.relativeschat.activity;

import cn.bmob.im.bean.BmobChatUser;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.base.RelativesChatApplication;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.PersonBean;
import com.chuck.relativeschat.QrCodeScan.activity.QrCodeScanActivity;
import com.chuck.relativeschat.tools.ImageLoadOptions;
import com.chuck.relativeschat.tools.PhotoUtil;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SystemSettingActivity extends BaseActivity implements OnClickListener{
	
	private RelativeLayout childViewLayout;
	private ImageView myIconImage;
	private TextView myNameText;
	private TextView myDetailText;
	
	private HeadViewLayout mHeadViewLayout;
	private ImageView messageTipsImage;
	
	private BmobChatUser currentUser;
	
	private  RelativesChatApplication rcApp;
	private int[] childViewIds = {R.id.friends_icon_image , R.id.friends_name_text , R.id.friends_personal_sign_text};
	private int[] viewIds = {R.id.current_user_info_layout , R.id.current_add_friends_layout , 
			R.id.add_friends_message_layout , R.id.two_dimensional_scan_layout};
	public static final int VIEWCOUNT = 3; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_more_info_fragment);
		
		rcApp = (RelativesChatApplication)getApplication();
		
		currentUser = rcApp.getCurrentUser();
		
		initView(false);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.current_add_friends_layout:
			intent = new Intent(getApplicationContext() , FindFriendsActivity.class);			
			break;
		case R.id.add_friends_message_layout:
			intent = new Intent(getApplicationContext() , FriendsInvitionMessageActivity.class);
			break;
		case R.id.current_user_info_layout:
			intent = new Intent(getApplicationContext() , ModefyUserInfoActivity.class);
			startActivityForResult(intent, 0);
			intent = null;
			break;
		case R.id.two_dimensional_scan_layout:
			intent = new Intent(getApplicationContext() , QrCodeScanActivity.class);
			break;
		default:
			break;
		}	
		
		if(intent != null){
			startActivity(intent);
		}	
	}
	
	public void initView(boolean isUpdate){		
		for(int i = 0 ; i < viewIds.length ; i++){
			childViewLayout = (RelativeLayout)findViewById(viewIds[i]);
			childViewLayout.setOnClickListener(this);
			for(int j = 0 ; j < childViewIds.length ; j++){
				if(j == 0){
					myIconImage = (ImageView)childViewLayout.findViewById(childViewIds[j]);
				}else if(j == 1){
					myNameText = (TextView)childViewLayout.findViewById(childViewIds[j]);
				}else{
					myDetailText = (TextView)childViewLayout.findViewById(childViewIds[j]);
				}
			}	
			if(i == 0){
				updateCurrentUserInfo(myIconImage , myNameText , myDetailText);
				if(isUpdate){
					break;
				}
			}else if(i == 1){
				setAddFriendsDetail(myIconImage , myNameText , myDetailText);
			}else if(i == 2){
				messageTipsImage = (ImageView)childViewLayout.findViewById(R.id.msg_tips_image);
				setFriendsInvitationMessage(myIconImage , myNameText , myDetailText);
			}else{
				setTwoDimenDetail(myIconImage , myNameText , myDetailText);
			}
		}	
		
		if(rcApp.getIsExistMoreInfoMessage()){
			//有消息的时候就显示小红点
			messageTipsImage.setVisibility(View.VISIBLE);
			rcApp.setExistMoreInfoMessage(false);
		}
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
//		mHeadViewLayout.setBackButtonVisiable(View.GONE);
		mHeadViewLayout.setTitleText("更多");
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		initView(true);		
	}
	
	public void updateCurrentUserInfo(ImageView mImage , TextView nameText , TextView nameDetailText){
		if(rcApp.getPersonDetailData() != null){
			PersonBean currentUser  = rcApp.getPersonDetailData();
			nameText.getPaint().setFakeBoldText(true);//加粗
			if(!StringUtils.isEmpty(currentUser.getNickName())){
				nameText.setText(currentUser.getNickName());
			}else{
				nameText.setText(currentUser.getUsername());
			}
			if(!StringUtils.isEmpty(currentUser.getUserState())){
				nameDetailText.setVisibility(View.VISIBLE);
				nameDetailText.setText(currentUser.getUserState());
			}else{
				nameDetailText.setVisibility(View.GONE);
			}
			
			if(!StringUtils.isEmpty(currentUser.getAvatar())){
				System.out.println("主页面的图像地址是" + currentUser.getAvatar());
				if(currentUser.getAvatar().contains("sdcard")){
					Bitmap image = BitmapFactory.decodeFile(currentUser.getAvatar());
					mImage.setImageBitmap(image);
				}else{
					ImageLoader.getInstance().displayImage(currentUser.getAvatar(), mImage, ImageLoadOptions.getOptions());
				}
			}else{
				setDetailInfoIcon(mImage , R.drawable.default_head);
			}
		} 
	}
	
	public void setAddFriendsDetail(ImageView mImage , TextView nameText , TextView nameDetailText){
		nameDetailText.setVisibility(View.GONE);
		setDetailInfoIcon(mImage , R.drawable.add_user);
		nameText.setText(getResources().getString(R.string.find_friends));
	}
	
	public void setFriendsInvitationMessage(ImageView mImage , TextView nameText , TextView nameDetailText){
		nameDetailText.setVisibility(View.GONE);
		setDetailInfoIcon(mImage , R.drawable.add_user_message);
		nameText.setText(getResources().getString(R.string.invite_friends));
	}
	
	public void setTwoDimenDetail(ImageView mImage , TextView nameText , TextView nameDetailText){
		nameDetailText.setVisibility(View.GONE);
		setDetailInfoIcon(mImage , R.drawable.qr_code);
		nameText.setText(getResources().getString(R.string.qr_code));
	}
	
	/**
	 * 
	 * 
	 * @author chengang
	 * @date 2014-8-12 下午5:09:37
	 * @param iconView 要设置的控件组件
	 * @param resourceId 要显示的资源
	 */
	public void setDetailInfoIcon(ImageView iconView , int resourceId){
		PhotoUtil imageView = new PhotoUtil(getApplicationContext());
		iconView.setImageBitmap(imageView.toRoundCorner(resourceId , 80.f));
	}
}
