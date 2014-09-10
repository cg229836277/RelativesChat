package com.chuck.relativeschat.Share.activity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.adapter.FriendsBaseListAdapter;
import com.chuck.relativeschat.bean.UserShareFileBean;
import com.chuck.relativeschat.common.BitmapConcurrencyDealUtil;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.common.ViewHolder;
import com.chuck.relativeschat.entity.FileRemarkBean;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.entity.ShareFileRemark;
import com.chuck.relativeschat.tools.HttpDownloader;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.chuck.relativeschat.tools.XListView;
import com.chuck.relativeschat.tools.XListView.IXListViewListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
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
	private DisplayImageOptions options;
	BitmapConcurrencyDealUtil dealUtil = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_share);
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("好友的分享");
				
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.chat_add_picture_normal)
		.showImageOnFail(R.drawable.chat_fail_resend_normal)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
		
		friendsShareListView = (XListView)findViewById(R.id.friend_share_list_view);
		friendsShareListView.setPullLoadEnable(true);
		friendsShareListView.setPullRefreshEnable(true);
		friendsShareListView.setXListViewListener(this);
		getFriendsShareList(PAGE_INDEX);
	}
	
	public void getFriendsShareList(final int pageIndex){		
		BmobQuery<ShareFileBean> dataQuery1 = new BmobQuery<ShareFileBean>();
		dataQuery1.addWhereEqualTo("isShareToAll", "1");
		BmobQuery<ShareFileBean> dataQuery2 = new BmobQuery<ShareFileBean>();
		dataQuery2.addWhereEqualTo("isShareToAll", "0");
		dataQuery2.addWhereEqualTo("shareTo", userManager.getCurrentUserName());
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
					shareFileData.setFileId(dataBean.getObjectId());
					shareFileData.setCreateDate(dataBean.getCreatedAt());
					shareFileData.setFileName(dataBean.getFileName());
					shareFileData.setFileType(dataBean.getFileType());
					shareFileData.setShareUser(dataBean.getShareUser());
					shareFileData.setFileUrl(dataBean.getFilePath());
					shareFileData.setShareToUser(dataBean.getShareTo());
					shareFileData.setIsShareToAll(dataBean.getIsShareToAll());
					if(StringUtils.isEmpty(dataBean.getShareRemarkNumber())){
						shareFileData.setFileShareNumber("0");
					}else{
						shareFileData.setFileShareNumber(dataBean.getShareRemarkNumber());
					}
					
					if(StringUtils.isEmpty(dataBean.getWordRemarkNumber())){
						shareFileData.setWordRemarkNumber("0");
					}else{
						shareFileData.setWordRemarkNumber(dataBean.getWordRemarkNumber());
					}
					
					if(StringUtils.isEmpty(dataBean.getIsGoodNumber())){
						shareFileData.setIsGoodNumber("0");
					}else{
						shareFileData.setIsGoodNumber(dataBean.getIsGoodNumber());
					}
					
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
				
				adapter.notifyDataSetChanged();
			}
		}.execute();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			friendsShareListView.setSelection(adapter.getCount() - 1);
		}
	};

	@Override
	public void onRefresh() {
		getMoreData("fresh");
		
	}

	@Override
	public void onLoadMore() {
		getMoreData("load");
		
	}
	
	public void getMoreData(final String type){
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				PAGE_INDEX++;
//				adapter.notifyDataSetChanged();
				getFriendsShareList(PAGE_INDEX);				
				if(type.equals("fresh")){
					friendsShareListView.stopRefresh();
				}else{				
					friendsShareListView.stopLoadMore();
				}
			}
		}, 1000);	
	}
	
	public class FriendsShareAdapter extends FriendsBaseListAdapter<UserShareFileBean> implements OnClickListener{
		
		private ArrayList<String> urlList = new ArrayList<String>();
		private ArrayList<String> shareUserList = new ArrayList<String>();
		private final HttpDownloader imageDownloader = new HttpDownloader();
		private boolean num = false;  
		private float scaleWidth;
		private float scaleHeight;
		private Dialog mDialog;
		private TextView goodsNumberText;
		private ShareFileBean fileData;		
//		private BitmapCacheUtil imageCache = null;
		
		public FriendsShareAdapter(Context context, List<UserShareFileBean> list) {
			super(context, list);
			if(IsListNotNull.isListNotNull(urlList)){
				urlList.clear();
			}
			
			if(IsListNotNull.isListNotNull(shareUserList)){
				shareUserList.clear();
			}	
			
//			if(imageCache != null){
//				imageCache = null;
//			}
//			
//			imageCache = new BitmapCacheUtil();
			
			dealUtil = new BitmapConcurrencyDealUtil(getApplicationContext());
		}

		@Override
		public View bindView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.simple_friends_share_item, null);
			}
			final UserShareFileBean data = (UserShareFileBean) getList().get(position);
			TextView shareDesc = ViewHolder.get(convertView, R.id.share_desc_text);
			ImageView smallImageView = ViewHolder.get(convertView, R.id.share_small_image);
			TextView timeTextView = ViewHolder.get(convertView, R.id.share_file_time);
			LinearLayout shareFeedbackLayout =  ViewHolder.get(convertView, R.id.feedback_share_layout);
			LinearLayout wordFeedbackLayout =  ViewHolder.get(convertView, R.id.feedback_word_layout);
			LinearLayout goodFeedbackLayout =  ViewHolder.get(convertView, R.id.feedback_good_layout);
			goodsNumberText = (TextView)goodFeedbackLayout.findViewById(R.id.good_text);
			goodsNumberText.setText("点赞" + "(" + data.getIsGoodNumber() + ")");
			
			smallImageView.setOnClickListener(this);
			shareFeedbackLayout.setOnClickListener(this);
			wordFeedbackLayout.setOnClickListener(this);
			goodFeedbackLayout.setOnClickListener(this);
			
			String fileType = null;
			
			if(data != null){				
				shareFeedbackLayout.setTag(data);
				wordFeedbackLayout.setTag(data);
				goodFeedbackLayout.setTag(data);
				
				if(!StringUtils.isEmpty(data.getFileType())){
					fileType = data.getFileType();
					if(fileType.equals(ShareFileBean.PHOTO)){
						fileType = "照片";						
						setSmallImageView(data, smallImageView);
					}else if(fileType.equals(ShareFileBean.MUSIC)){
						fileType = "音乐";
					}else if(fileType.equals(ShareFileBean.VIDEO)){
						fileType = "短视频";
					}else if(fileType.equals(ShareFileBean.SOUNG)){
						fileType = "语音";
						smallImageView.setVisibility(View.GONE);
					}
					
					if(!StringUtils.isEmpty(data.getShareUser())){	
						String desc = null;
						String date = data.getCreateDate();
						if(!StringUtils.isEmpty(data.getIsShareToAll()) && data.getIsShareToAll().equals("0")){
							desc = data.getShareUser() +"私密分享给我的" + fileType;
						}else{
							desc = data.getShareUser() +"分享的" + fileType;
						}
						StringBuffer buff = new StringBuffer(desc);
						shareDesc.setText(buff);
						timeTextView.setText(date);
					}
				}
			}		
			return convertView;
		}
		
		@Override
		public void onClick(View arg0) {
			UserShareFileBean tempData = null;
			if(arg0.getTag() instanceof UserShareFileBean){
				tempData = (UserShareFileBean)arg0.getTag();
			}
			
			switch (arg0.getId()) {
			case R.id.share_small_image:
				expandSmallImageView(arg0);
				break;
			case R.id.feedback_share_layout:
				
				break;
			case R.id.feedback_word_layout:
				
				break;
			case R.id.feedback_good_layout:
				remarkByGood(tempData);
				break;
			case R.id.expand_image_view:
				if(mDialog != null && mDialog.isShowing()){
					mDialog.dismiss();
				}
				break;				
			default:
				break;
			}
		}
		
		public void setSmallImageView(UserShareFileBean data , final ImageView smallImage){		
//			boolean isCacheExist = imageCache.loadBitmap(data.getFileUrl(), smallImage);
//			if(!isCacheExist){
//				imageLoader.displayImage(data.getFileUrl(), smallImage, options, new SimpleImageLoadingListener() {
//					@Override
//					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//						String message = null;
//						switch (failReason.getType()) {
//							case IO_ERROR:
//								message = "Input/Output error";
//								break;
//							case DECODING_ERROR:
//								message = "Image can't be decoded";
//								break;
//							case NETWORK_DENIED:
//								message = "Downloads are denied";
//								break;
//							case OUT_OF_MEMORY:
//								message = "Out Of Memory error";
//								break;
//							case UNKNOWN:
//								message = "Unknown error";
//								break;
//						}
//						Toast.makeText(FriendShareActivity.this, message, Toast.LENGTH_SHORT).show();
//					}
//
//					@Override
//					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//						smallImage.setTag(loadedImage);
//						imageCache.addBitmapToMemoryCache(imageUri, loadedImage);
////						BitmapUtils.decodeSampledBitmapFromResource(getResources(), R.id.share_small_image, 72, 72);
//						Bitmap newBitmap = ThumbnailUtils.extractThumbnail(loadedImage, 72,72);
//						smallImage.setImageBitmap(newBitmap);		
//					}
//				});
//			}
//			imageDownloader.download(data.getFileUrl(), smallImage);
			dealUtil.loadBitmap(data.getFileUrl(), smallImage);
		}
		
		public void remarkByGood(final UserShareFileBean data){
			dialog.show();
			if(data != null){
				fileData = new ShareFileBean();
				fileData.setObjectId(data.getFileId());
				fileData.setIsGoodNumber("" + (Integer.parseInt(data.getIsGoodNumber()) + 1));
				fileData.update(getApplicationContext(), new UpdateListener() {
					
					@Override
					public void onSuccess() {
						final ShareFileRemark remarkData = new ShareFileRemark();
						remarkData.setRemarkFileId(fileData.getObjectId());
						remarkData.setIsGood("1");
						remarkData.setRemarkFileUrl(data.getFileUrl());
						remarkData.setRemarkType(ShareFileRemark.REMARK_TYPE_GOOD);
						remarkData.setRemarkUser(userManager.getCurrentUserName());
						remarkData.save(getApplicationContext(), new SaveListener() {
							
							@Override
							public void onSuccess() {
								FileRemarkBean mixData = new FileRemarkBean();
								mixData.setShareFileId(fileData.getObjectId());
								mixData.setShareFileRemarkId(remarkData.getObjectId());
								mixData.save(getApplicationContext(), new SaveListener() {
									
									@Override
									public void onSuccess() {	
										goodsNumberText.setText("点赞" + "(" + fileData.getIsGoodNumber() + ")");
										handleSaveException(true , null);
									}
									
									@Override
									public void onFailure(int arg0, String arg1) {
										handleSaveException(false , arg1);
									}
								});
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								handleSaveException(false , arg1);
							}
						});
					}
					
					@Override
					public void onFailure(int arg0, String arg1) {
						handleSaveException(false , arg1);
					}
				});
			}
		}
		
		public void expandSmallImageView(View smallImageView) {			
			Bitmap tempBitmap = null;
			if (smallImageView.getTag() instanceof Bitmap) {
				tempBitmap = (Bitmap) smallImageView.getTag();
			}
			if(tempBitmap == null){
				return;
			}
			
			LayoutInflater flater = LayoutInflater.from(getApplicationContext());
			View view = flater.inflate(R.layout.expand_samll_view_dialog, null);
			ImageView bigImageView = (ImageView)view.findViewById(R.id.expand_image_view);
			bigImageView.setOnClickListener(this);
			bigImageView.setImageBitmap(tempBitmap);	
			mDialog = new AlertDialog.Builder(FriendShareActivity.this).setView(view).create();
			mDialog.show();
		}
		
		public void handleSaveException(boolean isSuccess , String failReason){
			dialog.dismiss();
			if(isSuccess){
//				adapter.notifyDataSetChanged();
				mToast.showMyToast("评论好友的分享成功！", Toast.LENGTH_SHORT);
			}else{
				mToast.showMyToast("评论好友的分享失败！" + failReason, Toast.LENGTH_SHORT);
			}
			fileData = null;
		}
		
		public HttpDownloader getImageDownloader() {
	        return imageDownloader;
	    }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(shareFileBeanList != null){
			shareFileBeanList.clear();
		}
		
		if(dealUtil != null){
			dealUtil.clearCache();
			dealUtil = null;
		}
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(shareFileBeanList != null){
			shareFileBeanList.clear();
		}
		if(adapter != null){
//			adapter.notifyDataSetChanged();
		}		
	}
}
