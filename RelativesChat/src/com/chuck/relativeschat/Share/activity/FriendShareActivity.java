package com.chuck.relativeschat.Share.activity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsShareAdapter;
import com.chuck.relativeschat.bean.UserShareFileBean;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.HttpDownloader;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
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
		
		if(IsListNotNull.isListNotNull(shareFileBeanList)){
			shareFileBeanList.clear();
		}

		BmobQuery<ShareFileBean> dataQuery = new BmobQuery<ShareFileBean>();
		dataQuery.addWhereEqualTo("isShareToAll", "1");
		dataQuery.setLimit(pageIndex * 10);
		dataQuery.findObjects(getApplicationContext(), new FindListener<ShareFileBean>() {			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					setShareFileData(arg0);
					
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
//					Bitmap map = HttpDownloader.downfile(dataBean.getFilePath(), dataBean.getFileName(), true);
//					shareFileData.setFileBitmap(map);
					shareFileData.setFileType(dataBean.getFileType());
					shareFileData.setShareUser(dataBean.getShareUser());
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
}
