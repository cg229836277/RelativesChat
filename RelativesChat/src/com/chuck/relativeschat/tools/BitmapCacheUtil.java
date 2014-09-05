package com.chuck.relativeschat.tools;

import com.chuck.relativeschat.R;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

/**
 * @Title：SAFEYE@
 * @Description： 下面的一些方法根据谷歌官方的文章，尝试利用缓存解决多个图片加载
 * @date 2014-9-5 下午2:49:30
 * @author chengang
 * @version 1.0
 */
public class BitmapCacheUtil {
	private LruCache<String, Bitmap> mMemoryCache;
	
	/**
	 * 构造方法，在一个线程中new一次
	 * @param targetImage
	 */
	public BitmapCacheUtil(){
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	
	/**
	 * 添加bitmap到内存缓存
	 * 
	 * @author chengang
	 * @date 2014-9-5 下午3:00:22
	 * @param key 
	 * @param bitmap
	 */
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}
	
	/**
	 * 获取缓存的图片
	 * 
	 * @author chengang
	 * @date 2014-9-5 下午3:07:12
	 * @param key 添加到缓存时候的标记
	 * @return
	 */
	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	/**
	 * 加载图片，加载的时候首先到缓存中去找，找不到的话，
	 * 
	 * @author chengang
	 * @date 2014-9-5 下午3:07:36
	 * @param resId
	 * @param imageView
	 */
	public boolean loadBitmap(String key, ImageView imageView) {
	    final String imageKey = String.valueOf(key);
	    final Bitmap bitmap = getBitmapFromMemCache(imageKey);
	    if (bitmap != null) {
	    	imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 72,72));
	        return true;
	    } else {
	    	imageView.setImageResource(R.drawable.default_head);
	        return false;
	    }
	}
}
