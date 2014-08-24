package com.chuck.relativeschat.activity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.adapter.FriendsShareAdapter;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FriendShareActivity extends BaseActivity {

	private ListView friendsShareListView;
	private FriendsShareAdapter adapter;
	private List<ShareFileBean> shareFileBeanList;
	private HeadViewLayout mHeadViewLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_share);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("好友的分享");
		
		friendsShareListView = (ListView)findViewById(R.id.friend_share_list_view);
		
		getFriendsShareList();
	}
	
	public void getFriendsShareList(){
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected void onPreExecute() {
//				dialog.show();
//				super.onPreExecute();
//			}
//			
//			@Override
//			protected Void doInBackground(Void... arg0) {
				BmobQuery<ShareFileBean> dataQuery = new BmobQuery<ShareFileBean>();
				dataQuery.addWhereEqualTo("isShareToAll", "1");
				dataQuery.findObjects(getApplicationContext(), new FindListener<ShareFileBean>() {			
					@Override
					public void onSuccess(List<ShareFileBean> arg0) {
						if(IsListNotNull.isListNotNull(arg0)){
							shareFileBeanList = arg0;
							adapter = new FriendsShareAdapter(getApplicationContext(), shareFileBeanList);
							friendsShareListView.setAdapter(adapter);
						}else{
							mToast.showMyToast("现在还没有还有分享，你赶紧成为第一个分享的人吧！", Toast.LENGTH_SHORT);
						}	
					}
					
					@Override
					public void onError(int arg0, String arg1) {
						System.out.println("查找好友分享数据  " + arg1);
					}
				});
//				return null;
//			}
			
//			@Override
//			protected void onPostExecute(Void result) {
//				super.onPostExecute(result);
//				if(IsListNotNull.isListNotNull(shareFileBeanList)){
//					adapter = new FriendsShareAdapter(getApplicationContext(), shareFileBeanList);
//					friendsShareListView.setAdapter(adapter);
//				}else{
//					mToast.showMyToast("现在还没有还有分享，你赶紧成为第一个分享的人吧！", Toast.LENGTH_SHORT);
//				}				
//				dialog.dismiss();
//			}
//		}.execute();
	}
}
