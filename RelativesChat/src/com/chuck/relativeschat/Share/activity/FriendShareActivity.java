package com.chuck.relativeschat.Share.activity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.bean.UserShareFileBean;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendShareActivity extends BaseActivity implements IXListViewListener{

	private XListView friendsShareListView;
	private FriendsShareAdapter adapter;
	private List<UserShareFileBean> shareFileBeanList = new ArrayList<UserShareFileBean>();
	private HeadViewLayout mHeadViewLayout;
	private int PAGE_INDEX = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_share);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("好友的分享");
		
		friendsShareListView = (XListView)findViewById(R.id.friend_share_list_view);
		friendsShareListView.setXListViewListener(this);
		getFriendsShareList(PAGE_INDEX);
	}
	
	public void getFriendsShareList(final int pageIndex){		
		BmobQuery<ShareFileBean> dataQuery1 = new BmobQuery<ShareFileBean>();
		dataQuery1.addWhereEqualTo("isShareToAll", "1");
		BmobQuery<ShareFileBean> dataQuery2 = new BmobQuery<ShareFileBean>();
		dataQuery2.addWhereEqualTo("isShareToAll", "0");
		dataQuery2.addWhereEqualTo("shareTo", userManager.getCurrentUserObjectId());
		List<BmobQuery<ShareFileBean>> queries = new ArrayList<BmobQuery<ShareFileBean>>();
		queries.add(dataQuery2);
		queries.add(dataQuery1);
		BmobQuery<ShareFileBean> mainQuery = new BmobQuery<ShareFileBean>();
		mainQuery.or(queries);
		mainQuery.setLimit(pageIndex * 10);
		mainQuery.findObjects(getApplicationContext(), new FindListener<ShareFileBean>() {			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					if(pageIndex * 10 - arg0.size() > 10){
						mToast.showMyToast("没有更多的分享数据了！", Toast.LENGTH_SHORT);
						PAGE_INDEX--;
//						System.out.println("现在的页数是" + PAGE_INDEX);
					}else{
						if(IsListNotNull.isListNotNull(shareFileBeanList)){
							shareFileBeanList.clear();
						}
						setShareFileData(arg0);
					}
					
				}else{
					mToast.showMyToast("现在还没有还有分享，你赶紧成为第一个分享的人吧！", Toast.LENGTH_SHORT);
				}	
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				System.out.println("查找好友分享数据  " + arg1);
			}
		});	
	}
	
	public void setShareFileData(final List<ShareFileBean> data){
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {				
				super.onPreExecute();
				dialog.show();
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				for(ShareFileBean dataBean : data){
					UserShareFileBean shareFileData = new UserShareFileBean();
					shareFileData.setCreateDate(dataBean.getCreatedAt());
					shareFileData.setFileName(dataBean.getFileName());
					shareFileData.setFileType(dataBean.getFileType());
					shareFileData.setShareUser(dataBean.getShareUser());
					shareFileData.setFileUrl(dataBean.getFilePath());
					shareFileData.setShareToUser(dataBean.getShareTo());
					shareFileData.setIsShareToAll(dataBean.getIsShareToAll());
					shareFileBeanList.add(shareFileData);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				adapter = new FriendsShareAdapter(getApplicationContext(), shareFileBeanList);
				friendsShareListView.setAdapter(adapter);
				adapter.setList(shareFileBeanList);
				friendsShareListView.setSelection(adapter.getCount() - 1);
				dialog.dismiss();
			}
		}.execute();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			friendsShareListView.setSelection(adapter.getCount() - 1);
//			System.out.println("页数是" + PAGE_INDEX);
		}
	};

	@Override
	public void onRefresh() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				PAGE_INDEX++;
//				System.out.println("页数是@@@@" + PAGE_INDEX);
				getFriendsShareList(PAGE_INDEX);
				friendsShareListView.stopRefresh();
			}
		}, 1000);	
	}

	@Override
	public void onLoadMore() {
		
	}
	
	public class FriendsShareAdapter extends FriendsBaseListAdapter<UserShareFileBean> implements OnClickListener{
		
		private ArrayList<String> urlList = new ArrayList<String>();
		private ArrayList<String> shareUserList = new ArrayList<String>();
		
		public FriendsShareAdapter(Context context, List<UserShareFileBean> list) {
			super(context, list);
			if(IsListNotNull.isListNotNull(urlList)){
				urlList.clear();
			}
			
			if(IsListNotNull.isListNotNull(shareUserList)){
				shareUserList.clear();
			}
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.simple_friends_share_item, null);
			}
			final UserShareFileBean data = (UserShareFileBean) getList().get(position);
			TextView shareDesc = ViewHolder.get(convertView, R.id.share_desc_text);
			LinearLayout watchFeedBaLayout = ViewHolder.get(convertView, R.id.watch_share_layout);
			ImageView fileTypeView = ViewHolder.get(convertView, R.id.file_type_image);
			ImageView fileShareFavouriteView = ViewHolder.get(convertView, R.id.share_favourite_image);
			fileShareFavouriteView.setVisibility(View.INVISIBLE);
//			LinearLayout shareFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_share_layout);
//			LinearLayout wordFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_word_layout);
//			LinearLayout goodFeedBaLayout = ViewHolder.get(convertView, R.id.feedback_good_layout);
			
			watchFeedBaLayout.setOnClickListener(this);
//			shareFeedBaLayout.setOnClickListener(this);
//			wordFeedBaLayout.setOnClickListener(this);
//			goodFeedBaLayout.setOnClickListener(this);
			
			String fileType = null;
			
			if(data != null){
				if(!StringUtils.isEmpty(data.getShareUser())){
					shareUserList.add(data.getShareUser());
				}
				
				if(!StringUtils.isEmpty(data.getFileType())){
					fileType = data.getFileType();
					if(fileType.equals(ShareFileBean.PHOTO)){
						fileTypeView.setImageResource(R.drawable.image);
						fileType = "照片";
						if(!StringUtils.isEmpty(data.getFileUrl())){
							urlList.add(data.getFileUrl());
							watchFeedBaLayout.setTag(data.getFileUrl());
						}
					}else if(fileType.equals(ShareFileBean.MUSIC)){
						fileType = "音乐";
					}else if(fileType.equals(ShareFileBean.VIDEO)){
						fileType = "短视频";
					}else if(fileType.equals(ShareFileBean.SOUNG)){
						fileType = "语音";
					}
					
					if(!StringUtils.isEmpty(data.getShareUser())){	
						String desc = null;
						String date = data.getCreateDate();
						if(!StringUtils.isEmpty(data.getIsShareToAll()) && data.getIsShareToAll().equals("0")){
							desc = "来自" + data.getShareUser() +"私密分享给我的" + fileType +  "		" + date;
							fileShareFavouriteView.setVisibility(View.VISIBLE);
							fileShareFavouriteView.setImageResource(R.drawable.favourite);
						}else{
							desc = "来自" + data.getShareUser() +"的" + fileType +  "		" +date;
						}
						StringBuffer buff = new StringBuffer(desc);
						shareDesc.setText(buff);
					}
				}			
			}		
			return convertView;
		}
		
		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.watch_share_layout:
				if(arg0.getTag() instanceof String){
					String position = (String)arg0.getTag();
//					String[] urlArray = (String[])urlList.toArray(new String[urlList.size()]);
					Intent intent = new Intent(FriendShareActivity.this , WatchShareImageActivity.class);
					intent.putExtra(WatchShareImageActivity.POSITION, position);
					intent.putStringArrayListExtra(WatchShareImageActivity.IMAGE_URL, urlList);
					intent.putStringArrayListExtra(WatchShareImageActivity.SHARE_USER, shareUserList);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}
	}
}
