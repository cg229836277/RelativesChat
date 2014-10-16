package com.chuck.relativeschat.share.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.common.BmobConstants;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.common.VideoThumbnailGenerateUtil;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;

import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShareVideoToFriendsActivity extends BaseActivity  implements IXListViewListener{

	private HeadViewLayout mHeadViewLayout;
	private XListView sharedVideoListView;
	private TextView noContentView;
	private int PAGE_INDEX = 1;
	private ShareVideoListViewAdapter videoListAdapter;
	
	private SpannableString shareSp = null;
	private final int VIDEO_REQUEST_CODE = 1;
	private final int REVIEW_VIDEO_CODE = 2;
	public final static String VIDEO_URL = "video_url"; 
	public static final String SHARE_TO_USER = "shareToUser";
	private String shareToUserName;
	private Button startShareVideoBtn;
	private VideoThumbnailGenerateUtil dealUtil = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_video_to_friends);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("分享短视频");
		
		shareToUserName = getIntent().getStringExtra(SHARE_TO_USER);
		
		bindEvent();
		
		initDataToVideoList();
	}
	
	public void bindEvent(){
		sharedVideoListView = (XListView)findViewById(R.id.shared_video_list);
		sharedVideoListView.setPullLoadEnable(true);
		sharedVideoListView.setPullRefreshEnable(true);
		sharedVideoListView.setXListViewListener(this);
		noContentView = (TextView)findViewById(R.id.no_share_video_text);
		startShareVideoBtn = (Button)findViewById(R.id.start_take_video);
		startShareVideoBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				startToTakeVideo();
			}
		});
	}
	
	public void initDataToVideoList(){
		dialog.show();
		BmobQuery<ShareFileBean> query1 = new BmobQuery<ShareFileBean>();
		//找到我分享的，包括普通分享和私密分享
		query1.addWhereEqualTo("isShareToAll", "1");
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
		mainQuery.order("-createdAt");
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
						sharedVideoListView.setSelection(0);
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
	 * 处理没有视频分享数据的时候
	 * 
	 * @author chengang
	 * @date 2014-9-2 上午11:03:49
	 */
	public void handleBlankDataList(){
		sharedVideoListView.setVisibility(View.GONE);
		noContentView.setVisibility(View.VISIBLE);
		
		String blankString = "你暂时没有分享视频，赶紧分享吧!";
		shareSp = new SpannableString(blankString);
		shareSp.setSpan(new UnderlineSpan(), 12, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		shareSp.setSpan(new ClickableSpan() {			
			@Override
			public void onClick(View widget) {
				startToTakeVideo();
			}
		}, 12, 14, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		noContentView.setText(shareSp);
		noContentView.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	/**
	 * 处理有视频分享数据的时候
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
			dealUtil = new VideoThumbnailGenerateUtil(getApplicationContext());
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
				}else if(userManager.getCurrentUserName().equals(data.getShareTo()) && "0".equals(data.getIsShareToAll())){
					desc = data.getShareUser() + "在" + data.getCreatedAt() +"给我分享了视频";
				}else{				
					desc = data.getShareUser() + "在" + data.getCreatedAt() +"分享了视频";
				}
				shareDesc.setText(desc);
				timeText.setText(data.getCreatedAt());
				
				String fileOnlinePath = data.getFilePath();
	        	String fileName = fileOnlinePath.substring(fileOnlinePath.lastIndexOf("/") + 1 , fileOnlinePath.length());
	            String localUrl = BmobConstants.RECORD_VIDEO_CACHE_PATH + fileName;
	            File file = new File(localUrl);
	    	    if(file != null && file.exists()){
	    	    	Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(localUrl, Thumbnails.MICRO_KIND);
	    	    	thumbNailImage.setImageBitmap(bitmap);
	    	    }else{
	    	    	dealUtil.loadVideoBitmap(data.getFilePath(), thumbNailImage);
	    	    }
	    	    
				thumbNailImage.setTag(data.getFilePath());
				thumbNailImage.setOnClickListener(this);
			}
			return convertView;
		}

		@Override
		public void onClick(View arg0) {		
			if(arg0.getTag() instanceof String){
				String videoUrl = (String)arg0.getTag();
				String videoCachePath  = BmobConstants.RECORD_VIDEO_CACHE_PATH;
				Intent intent = new Intent(getApplicationContext(), PlaySharedVideoActivity.class);
				intent.putExtra(PlaySharedVideoActivity.VIDEO_URL, videoUrl);
				intent.putExtra(PlaySharedVideoActivity.VIDEO_CACHE_URL, videoCachePath);
				startActivity(intent);
			}
		}	
	}
	
	public void startToTakeVideo(){
		Intent intent = new Intent(ShareVideoToFriendsActivity.this , RecordVideoToServerActivity.class);
		startActivityForResult(intent, VIDEO_REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == VIDEO_REQUEST_CODE){
			String videoUrl = data.getStringExtra(VIDEO_URL);
			File file = new File(videoUrl);
			if(!StringUtils.isEmpty(videoUrl) && file.exists()){
				Intent intent = new Intent(this , ReviewRecordedVideoActivity.class);
				if(!StringUtils.isEmpty(shareToUserName)){
					intent.putExtra(SHARE_TO_USER, shareToUserName);
				}
				intent.putExtra(VIDEO_URL, videoUrl);
				startActivityForResult(intent , REVIEW_VIDEO_CODE);
			}
		}else if(requestCode == REVIEW_VIDEO_CODE){			
			initDataToVideoList();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(dealUtil != null){
			dealUtil.destoryFmmrInstance();
		}
	}
}
