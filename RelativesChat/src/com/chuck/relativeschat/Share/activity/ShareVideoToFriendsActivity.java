package com.chuck.relativeschat.Share.activity;

import java.util.List;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.R.layout;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.XListView;

import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShareVideoToFriendsActivity extends Activity {

	private HeadViewLayout mHeadViewLayout;
	private XListView sharedVideoListView;
	private TextView noContentView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_video_to_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("分享短视频");
		mHeadViewLayout.setMoreInfoTest("我要分享");
		
		bindEvent();
	}
	
	public void bindEvent(){
		sharedVideoListView = (XListView)findViewById(R.id.shared_video_list);
		noContentView = (TextView)findViewById(R.id.no_share_video_text);
	}
	
	public class shareVideoListViewAdapter extends FriendsBaseListAdapter<ShareFileBean>{

		public shareVideoListViewAdapter(Context context,List<ShareFileBean> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			return null;
		}
		
	}
}
