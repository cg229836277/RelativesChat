package com.chuck.relativeschat.common;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.chuck.relativeschat.R;
import com.chuck.relativeschat.common.BitmapConcurrencyDealUtil.AsyncDrawable;
import com.chuck.relativeschat.common.BitmapConcurrencyDealUtil.BitmapWorkerTask;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-10-13 下午1:09:36
 * @author chengang
 * @version 1.0
 */
public class VideoThumbnailGenerateUtil {
	
	static final String LOG_TAG = "VideoThumbnailGenerateUtil";
	private Context mContext;
	
	public VideoThumbnailGenerateUtil(Context context){
		this.mContext = context;
	}
	
	/**
	 * @author Chuck Chan
	 * @date 2014-10-28 下午4:48:56
	 * @param url 要加载资源的地址
	 * @param imageView 显示略缩图的ImageView控件
	 */
	public void loadVideoBitmap(String url, ImageView imageView) {
		System.out.println("正在获取视频的略缩图");
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			forceDownload(url, imageView);
		} else {
			imageView.setVisibility(View.VISIBLE);
			cancelPotentialDownload(url, imageView);
			// imageView.setTag(bitmap);
			imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap,72, 72));
		}
	}
	
	/**
	 * @author Chuck Chan
	 * @date 2014-10-28 下午4:53:49
	 * @param url 资源地址
	 * @param imageView 加载略缩图的ImageView控件
	 */
	private void forceDownload(String url, ImageView imageView) {
       // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
       if (url == null) {
           imageView.setImageDrawable(null);
           return;
       }
       //判断是否需要下载
       if (cancelPotentialDownload(url, imageView)) {
		   imageView.setVisibility(View.VISIBLE);
		   BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		   Bitmap defaultBimap = BitmapFactory.decodeStream(mContext.getResources().openRawResource(R.drawable.user_icon_default));
	       AsyncDrawable downloadedDrawable = new AsyncDrawable(mContext.getResources() , defaultBimap , task);
	       imageView.setImageDrawable(downloadedDrawable);
	       imageView.setMinimumHeight(72);
	       task.execute(url);
	    }
	}
	
	private static boolean cancelPotentialDownload(String url, ImageView imageView) {
       BitmapWorkerTask bitmapDownloaderTask = getBitmapWorkerTask(imageView);

       if (bitmapDownloaderTask != null) {
           String bitmapUrl = bitmapDownloaderTask.url;
           if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
               bitmapDownloaderTask.cancel(true);
           } else {
               // The same URL is already being downloaded.
               return false;
           }
       }
       return true;
   }
	
	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

	   // Hard cache, with a fixed maximum capacity and a life duration
	   private final HashMap<String, Bitmap> sHardBitmapCache =
	       new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
	       @Override
	       protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest) {
	           if (size() > HARD_CACHE_CAPACITY) {
	               // Entries push-out of hard reference cache are transferred to soft reference cache
	               sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
	               return true;
	           } else
	               return false;
	       }
	   };
	
	   // Soft cache for bitmaps kicked out of hard cache
	   private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
	       new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);
	   
	   /**
	    * Adds this bitmap to the cache.
	    * @param bitmap The newly downloaded bitmap.
	    */
	   private void addBitmapToCache(String url, Bitmap bitmap) {
	       if (bitmap != null) {
	           synchronized (sHardBitmapCache) {
	               sHardBitmapCache.put(url, bitmap);
	           }
	       }
	   }
	   
	   /**
	    * @param url The URL of the image that will be retrieved from the cache.
	    * @return The cached bitmap or null if it was not found.
	    */
	   private Bitmap getBitmapFromCache(String url) {
	       // First try the hard reference cache
	       synchronized (sHardBitmapCache) {
	           final Bitmap bitmap = sHardBitmapCache.get(url);
	           if (bitmap != null) {
	               // Bitmap found in hard cache
	               // Move element to first position, so that it is removed last
	               sHardBitmapCache.remove(url);
	               sHardBitmapCache.put(url, bitmap);
	               return bitmap;
	           }
	       }
	
	       // Then try the soft reference cache
	       SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
	       if (bitmapReference != null) {
	           final Bitmap bitmap = bitmapReference.get();
	           if (bitmap != null) {
	               // Bitmap found in soft cache
	               return bitmap;
	           } else {
	               // Soft reference has been Garbage Collected
	               sSoftBitmapCache.remove(url);
	           }
	       }
	
	       return null;
	   }
	   
	   class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

			private final WeakReference<ImageView> imageViewReference;
			private String url;

		    public BitmapWorkerTask(ImageView imageView) {
		    	if(imageView == null){
		    		cancel(true);
		    	}
		        // Use a WeakReference to ensure the ImageView can be garbage collected
	    		imageViewReference = new WeakReference<ImageView>(imageView);
		    }

		    // Decode image in background.
		    @Override
		    protected Bitmap doInBackground(String... params) {
		    	if(isCancelled()){
		    		return null;
		    	}
		    	url = params[0];
		    	//获取在线视频的帧的图像，返回Bitmap
		    	Bitmap tempBitmap = getVideoThumbnail(url);
		        return tempBitmap;
		    }
			
		    @Override
		    protected void onPostExecute(Bitmap bitmap) {
		        if (isCancelled()) {
		            bitmap = null;
		        }
		        
		        addBitmapToCache(url, bitmap);

		        if (imageViewReference != null && bitmap != null) {
		            final ImageView imageView = imageViewReference.get();
		            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
		            //判断当前的异步对象是否与ImageView所在的异步对象是否相等，以此来防止加载错乱
		            if (this == bitmapWorkerTask && imageView != null) {	            	
//		            	imageView.setTag(bitmap);
		     	       	imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 72,72));
		            }
		        }
		    }
		}
	   
		private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
		}
		
		static class AsyncDrawable extends BitmapDrawable {
		    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		    public AsyncDrawable(Resources res, Bitmap bitmap,BitmapWorkerTask bitmapWorkerTask) {
		        super(res, bitmap);
		        bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		    }

		    public BitmapWorkerTask getBitmapWorkerTask() {
		        return bitmapWorkerTaskReference.get();
		    }
		}
	
	/**
	 * 获取在线视频的略缩图
	 * 
	 * @author chengang
	 * @date 2014-10-13 下午1:10:52
	 * @param videoUrl
	 * @return
	 */
	public Bitmap getVideoThumbnail(String videoUrl){
		Bitmap generateBitmap = null;
		FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
		try {
			fmmr.setDataSource(videoUrl);
			generateBitmap = fmmr.getFrameAtTime();

			if (generateBitmap != null) {
				Bitmap b2 = fmmr.getFrameAtTime(4000000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
				if (b2 != null) {
					//获取到了Bitmap之后用android自带的ThumbnailUtils获取自定义大小的略缩图
					generateBitmap = ThumbnailUtils.extractThumbnail(b2,
							640, 640,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				}
			}
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} finally {
			fmmr.release();
		}
		if(generateBitmap != null){
			return generateBitmap;
		}
		return null;
	}
	
	public void stopGetVideoThumnail(){
		BitmapWorkerTask stopTask = new BitmapWorkerTask(null);
	}
}
