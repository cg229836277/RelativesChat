package com.chuck.relativeschat.Share.activity;

import java.util.List;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.activity.BaseActivity;
import com.chuck.relativeschat.common.HeadViewLayout;
import com.chuck.relativeschat.tools.IsListNotNull;
import com.chuck.relativeschat.tools.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.os.Bundle;
import android.os.Parcelable;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WatchShareImageActivity extends BaseActivity {

	private ImageView imageView;
	private int pagerPosition;
	private String currentImageUrl;
	public static final String IMAGE_URL = "url";
	public static final String POSITION = "position";
	public static final String SHARE_USER = "shareUser";
	private ViewPager pager;
	public String[] imageUrls;
	public List<String> imageUrlList;
	public List<String> shareUserList;
	DisplayImageOptions options;
	private HeadViewLayout mHeadViewLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch_share_image);
		imageUrlList = getIntent().getStringArrayListExtra(IMAGE_URL);
		shareUserList = getIntent().getStringArrayListExtra(SHARE_USER);
		currentImageUrl = getIntent().getStringExtra(POSITION);
		if(IsListNotNull.isListNotNull(imageUrlList) && !StringUtils.isEmpty(currentImageUrl)){
			pagerPosition = imageUrlList.indexOf(currentImageUrl);
			imageUrls = (String[])imageUrlList.toArray(new String[imageUrlList.size()]);
		}
		
//		if (savedInstanceState != null) {
//			pagerPosition = savedInstanceState.getInt(POSITION);
//		}
		
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
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		outState.putInt(POSITION, pager.getCurrentItem());
//	}

	
	private class ImagePagerAdapter extends PagerAdapter {

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
			System.out.println("当前照片的总数是" + images.length);
			return images.length;
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			System.out.println("当前照片的索引是" + position);
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
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
	}
	
	@Override
	protected void onDestroy() {
		if(IsListNotNull.isListNotNull(shareUserList)){
			shareUserList.clear();
		}
		
		if(IsListNotNull.isListNotNull(imageUrlList)){
			imageUrlList.clear();
		}
		super.onDestroy();
	}
}
