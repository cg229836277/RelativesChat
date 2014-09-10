package com.chuck.relativeschat.tools;

import java.io.File;

import android.support.v4.util.LruCache;

/**
 * @Title：SAFEYE@
 * @Description： 下面的一些方法根据谷歌官方的文章，尝试利用缓存解决多个图片加载
 * @date 2014-9-5 下午2:49:30
 * @author chengang
 * @version 1.0
 */
public class MediaFileCacheUtil {
	private LruCache<String, File> mMemoryCache;
	
	/**
	 * 构造方法，在一个线程中new一次
	 * @param targetFile
	 */
	public MediaFileCacheUtil(){
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, File>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, File file) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return (int)file.length() / 1024;
	        }
	    };
	}
	
	/**
	 * 添加File到内存缓存
	 * 
	 * @author chengang
	 * @date 2014-9-5 下午3:00:22
	 * @param key 多媒体文件的网络或者本地地址
	 * @param file 文件
	 */
	public void addMediaFileToMemoryCache(String key, File file) {
	    if (getFileFromMemCache(key) == null) {
	    	mMemoryCache.remove(key);
	        mMemoryCache.put(key, file);
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
	public File getFileFromMemCache(String key) {
		File tempFile = mMemoryCache.get(key);
		if(tempFile != null && tempFile.exists()){
			return tempFile;
		}
	    return null;
	}
	
	public void clearMemoryCache(){
		if(mMemoryCache != null){
			mMemoryCache = null;
		}
	}
}
//	
//	/**
//	 * 加载图片，加载的时候首先到缓存中去找，找不到的话，
//	 * 
//	 * @author chengang
//	 * @date 2014-9-5 下午3:07:36
//	 * @param resId
//	 * @param imageView
//	 */
//	public boolean loadBitmap(String key, ImageView imageView) {
//	    final String imageKey = String.valueOf(key);
//	    final Bitmap bitmap = getBitmapFromMemCache(imageKey);
//	    if (bitmap != null) {
//	    	imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 72,72));
//	        return true;
//	    } else {
//	    	imageView.setImageResource(R.drawable.default_head);
//	        return false;
//	    }
//	}
