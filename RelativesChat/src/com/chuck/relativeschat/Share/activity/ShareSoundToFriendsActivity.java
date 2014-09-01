package com.chuck.relativeschat.Share.activity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.Share.activity.FriendShareActivity.FriendsShareAdapter;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.XListView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ShareSoundToFriendsActivity extends BaseActivity {
	private HeadViewLayout mHeadViewLayout;
	private ImageView pressStartRecordImage;
	private ImageView imageLevelImage;
	private XListView mySoundShareListView;
	private List<ShareFileBean> soundFileList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_sound_to_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("分享语音");
		
		bindEvent();
		initDataToList();
	} 
	
	public void bindEvent(){
		pressStartRecordImage = (ImageView)findViewById(R.id.press_start_record_image);
		imageLevelImage = (ImageView)findViewById(R.id.sound_level_image);
		mySoundShareListView = (XListView)findViewById(R.id.my_sound_recorder_list_view);
	}
	
	public void initDataToList(){
		BmobQuery<ShareFileBean> query = new BmobQuery<ShareFileBean>();
		query.addWhereEqualTo("shareUser", userManager.getCurrentUserName());
		query.addWhereEqualTo("fileType", ShareFileBean.SOUNG);
		query.findObjects(getApplicationContext(), new FindListener<ShareFileBean>() {
			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public class SoundShareListAdapter extends FriendsBaseListAdapter<ShareFileBean>{

		/**
		 * @param context
		 * @param list
		 */
		public SoundShareListAdapter(Context context, List<ShareFileBean> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			return null;
		}		
	}
}
