package com.chuck.relativeschat.Share.activity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;

import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareVideoToFriendsActivity extends BaseActivity  implements IXListViewListener{

	private HeadViewLayout mHeadViewLayout;
	private XListView sharedVideoListView;
	private TextView noContentView;
	private int PAGE_INDEX = 1;
	private ShareVideoListViewAdapter videoListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_video_to_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("分享短视频");
		mHeadViewLayout.setMoreInfoTest("我要分享");
		
		bindEvent();
		
		initDataToVideoList();
	}
	
	public void bindEvent(){
		sharedVideoListView = (XListView)findViewById(R.id.shared_video_list);
		noContentView = (TextView)findViewById(R.id.no_share_video_text);
	}
	
	public void initDataToVideoList(){
		dialog.show();
		BmobQuery<ShareFileBean> query1 = new BmobQuery<ShareFileBean>();
		//找到我分享的，包括普通分享和私密分享
		query1.addWhereEqualTo("shareUser", userManager.getCurrentUserName());
		query1.addWhereEqualTo("fileType", ShareFileBean.VIDEO);
		BmobQuery<ShareFileBean> query2 = new BmobQuery<ShareFileBean>();
		//找到好友分享给我的
		query2.addWhereEqualTo("shareTo", userManager.getCurrentUserName());
		query2.addWhereEqualTo("isShareToAll", "0");	
		query2.addWhereEqualTo("fileType", ShareFileBean.VIDEO);
		List<BmobQuery<ShareFileBean>> queries = new ArrayList<BmobQuery<ShareFileBean>>();
		queries.add(query1);
		queries.add(query2);
		BmobQuery<ShareFileBean> mainQuery = new BmobQuery<ShareFileBean>();		
		mainQuery.or(queries);
		mainQuery.setLimit(PAGE_INDEX * 10);
		mainQuery.findObjects(getApplicationContext(), new FindListener<ShareFileBean>() {
			
			@Override
			public void onSuccess(List<ShareFileBean> arg0) {
				if(IsListNotNull.isListNotNull(arg0)){
					handleExistDataList();
					if(PAGE_INDEX * 10 - arg0.size() > 10){
						mToast.showMyToast("没有更多的分享数据了！", Toast.LENGTH_SHORT);
						PAGE_INDEX--;
					}else{
						videoListAdapter = new ShareVideoListViewAdapter(getApplicationContext(), arg0);
						sharedVideoListView.setAdapter(videoListAdapter);
						videoListAdapter.setList(arg0);
						sharedVideoListView.setSelection(videoListAdapter.getCount() - 1);
						videoListAdapter.notifyDataSetChanged();
					}
				}else{
					handleBlankDataList();
				}				
				dialog.dismiss();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				handleBlankDataList();
				dialog.dismiss();
			}
		});
	}
	
	/**
	 * 处理没有语音分享数据的时候
	 * 
	 * @author chengang
	 * @date 2014-9-2 上午11:03:49
	 */
	public void handleBlankDataList(){
		sharedVideoListView.setVisibility(View.GONE);
		noContentView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 处理有语音分享数据的时候
	 * 
	 * @author chengang
	 * @date 2014-9-2 上午11:03:49
	 */
	public void handleExistDataList(){
		sharedVideoListView.setVisibility(View.VISIBLE);
		noContentView.setVisibility(View.GONE);
	}
	
	@Override
	public void onRefresh() {
		getMoreData("fresh");
		
	}

	@Override
	public void onLoadMore() {
		getMoreData("load");
		
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			sharedVideoListView.setSelection(videoListAdapter.getCount() - 1);
		}
	};
	
	public void getMoreData(final String type){
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				System.out.println("此处刷新！");
				PAGE_INDEX++;
				initDataToVideoList();
				if(type.equals("fresh")){
					sharedVideoListView.stopRefresh();
				}else{				
					sharedVideoListView.stopLoadMore();
				}
			}
		}, 1000);	
	}
	
	public class ShareVideoListViewAdapter extends FriendsBaseListAdapter<ShareFileBean> implements OnClickListener{

		public ShareVideoListViewAdapter(Context context,List<ShareFileBean> list) {
			super(context, list);
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.simple_video_share_item_layout, null);
			}
			final ShareFileBean data = (ShareFileBean) getList().get(position);
			TextView shareDesc = ViewHolder.get(convertView, R.id.share_desc_text);	
			TextView timeText = ViewHolder.get(convertView, R.id.share_file_time);	
			ImageView thumbNailImage = ViewHolder.get(convertView, R.id.share_small_image);	
			if(data != null){
				String desc = null;
				if(data.getShareUser().equals(userManager.getCurrentUserName()) && data.getIsShareToAll().equals("0")){
					desc = "我在" + data.getCreatedAt() + "分享了视频给" + data.getShareTo();					
				}else if(data.getShareUser().equals(userManager.getCurrentUserName()) && data.getIsShareToAll().equals("1")){
					desc = "我在" + data.getCreatedAt() + "分享了视频给大家";
				}else if(data.getShareTo().equals(userManager.getCurrentUserName()) && data.getIsShareToAll().equals("1")){
					desc = data.getShareUser() + "在" + data.getCreatedAt() +"给我分享了视频";
				}				
				shareDesc.setText(desc);
				timeText.setText(data.getCreatedAt());
				Bitmap microBitmap = ThumbnailUtils.createVideoThumbnail(data.getFilePath(), Thumbnails.MINI_KIND);
				if(microBitmap != null){
					thumbNailImage.setImageBitmap(microBitmap);
					thumbNailImage.setTag(data.getFilePath());
					thumbNailImage.setOnClickListener(this);
				}
			}
			return convertView;
		}

		@Override
		public void onClick(View arg0) {		
			if(arg0.getTag() instanceof String){
				String videoUrl = (String)arg0.getTag();
			}
		}	
	}
}
