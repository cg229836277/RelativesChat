package com.chuck.relativeschat.Share.activity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.bean.EventBusShareData;
import com.chuck.relativeschat.bean.UserShareFileBean;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.entity.FileRemarkBean;
import com.chuck.relativeschat.entity.ShareFileBean;
import com.chuck.relativeschat.entity.ShareFileRemark;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import de.greenrobot.event.EventBus;

import android.os.Bundle;
import android.os.Parcelable;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WatchShareImageActivity extends BaseActivity implements OnPageChangeListener , OnClickListener{

	private ImageView imageView;
	private int pagerPosition;
	private String currentImageUrl;
	public static final String IMAGE_URL = "url";
	public static final String POSITION = "position";
	public static final String SHARE_USER = "shareUser";
	public static final String SHARE_DATA = "share_data";
	private ViewPager pager;
	public String[] imageUrls;
	public List<String> imageUrlList;
	public List<String> shareUserList;
	private List<UserShareFileBean> dataBean;
	DisplayImageOptions options;
	private HeadViewLayout mHeadViewLayout;
	private LinearLayout shareFeedbackLayout;
	private LinearLayout wordFeedbackLayout;
	private LinearLayout goodFeedbackLayout;
	private String currentSelectedUserName;
	private String currentSelectedFileUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_share_image);
		
		EventBus.getDefault().register(this);
		
		imageUrlList = getIntent().getStringArrayListExtra(IMAGE_URL);
		shareUserList = getIntent().getStringArrayListExtra(SHARE_USER);
		currentImageUrl = getIntent().getStringExtra(POSITION);
		if(IsListNotNull.isListNotNull(imageUrlList) && !StringUtils.isEmpty(currentImageUrl)){
			pagerPosition = imageUrlList.indexOf(currentImageUrl);
			imageUrls = (String[])imageUrlList.toArray(new String[imageUrlList.size()]);
		}
		
		bindEvent();
		
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(POSITION);
		}
		
		mHeadViewLayout = (HeadViewLayout)findViewById(R.id.title_menu_layout);
		mHeadViewLayout.setBackButtonVisiable(View.VISIBLE);
		mHeadViewLayout.setTitleText("查看好友分享的照片");
		
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
		
		pager = (ViewPager) findViewById(R.id.pager);
		pager.setOnPageChangeListener(this);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
	}
	
	public void bindEvent(){
		shareFeedbackLayout = (LinearLayout)findViewById(R.id.feedback_share_layout);
		wordFeedbackLayout = (LinearLayout)findViewById(R.id.feedback_word_layout);
		goodFeedbackLayout = (LinearLayout)findViewById(R.id.feedback_good_layout);
		shareFeedbackLayout.setOnClickListener(this);
		wordFeedbackLayout.setOnClickListener(this);
		goodFeedbackLayout.setOnClickListener(this);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(POSITION, pager.getCurrentItem());
	}

	
	private class ImagePagerAdapter extends PagerAdapter{

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.length;
		}
		
		
		
		@Override
		public Object instantiateItem(ViewGroup view, int position) {			
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			imageLoader.displayImage(images[position], imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(WatchShareImageActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});
			
			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return shareUserList.get(position);
		}
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		if(IsListNotNull.isListNotNull(shareUserList)){
			shareUserList.clear();
		}
		
		if(IsListNotNull.isListNotNull(imageUrlList)){
			imageUrlList.clear();
		}
		
		if(IsListNotNull.isListNotNull(dataBean)){
			dataBean.clear();
		}
		
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		System.out.println("当前页是第" + arg0 + "页");
		if(IsListNotNull.isListNotNull(imageUrlList)){
			System.out.println("对应的图片地址是@@@@" + imageUrlList.get(arg0));
			System.out.println("对应的图片地址的分享人是@@@@" + shareUserList.get(arg0));
			currentSelectedUserName  = shareUserList.get(arg0);
			currentSelectedFileUrl = imageUrlList.get(arg0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.feedback_share_layout:
			remarkByShare();
			break;
		case R.id.feedback_word_layout:
			remarkByWord();
			break;
		case R.id.feedback_good_layout:
			remarkByGood();
			break;

		default:
			break;
		}					
	}
	
	public void remarkByWord(){
		
	}
	
	public void remarkByGood(){
		dialog.show();
		if(IsListNotNull.isListNotNull(dataBean)){
			System.out.println("数组的数据是" + dataBean.size());
			if(!StringUtils.isEmpty(currentSelectedFileUrl) && !StringUtils.isEmpty(currentSelectedUserName)){
				for(UserShareFileBean tempData : dataBean){
					if(currentSelectedFileUrl.equals(tempData.getFileUrl()) && 
							currentSelectedUserName.equals(tempData.getShareUser())){
						final ShareFileBean fileData = new ShareFileBean();
						fileData.setObjectId(tempData.getFileId());
						fileData.setIsGoodNumber("" + (Integer.parseInt(tempData.getIsGoodNumber()) + 1));
						fileData.update(getApplicationContext(), new UpdateListener() {
							
							@Override
							public void onSuccess() {
								final ShareFileRemark remarkData = new ShareFileRemark();
								remarkData.setRemarkFileId(fileData.getObjectId());
								remarkData.setIsGood("1");
								remarkData.setRemarkFileUrl(currentSelectedFileUrl);
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
			}
		}
	}
	
	public void remarkByShare(){
		
	}
	
	public void onEventMainThread(EventBusShareData data){
		if(IsListNotNull.isListNotNull(data.getmFileData())){
			dataBean = data.getmFileData();
		}
	}
	
	public void handleSaveException(boolean isSuccess , String failReason){
		dialog.dismiss();
		if(isSuccess){
			mToast.showMyToast("评论好友的分享成功！", Toast.LENGTH_SHORT);
		}else{
			mToast.showMyToast("评论好友的分享失败！" + failReason, Toast.LENGTH_SHORT);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			Intent intent = new Intent(this , FriendShareActivity.class);
			startActivity(intent);
			this.finish();
			return true;
		}
		return false;
	}
}
