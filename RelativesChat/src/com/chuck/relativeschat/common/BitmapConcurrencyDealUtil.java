package com.chuck.relativeschat.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chuck.relativeschat.R;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-9-9 下午1:49:46
 * @author chengang
 * @version 1.0
 */
public class BitmapConcurrencyDealUtil {
	
	static final String LOG_TAG = "BitmapConcurrencyDeal";
	private Context mContext;
	
	public BitmapConcurrencyDealUtil(Context context){
		this.mContext = context;
	}

	/**
	 * 加载在内存中缩放的图片，减少内存损耗
	 * 
	 * @author chengang
	 * @date 2014-9-9 下午1:52:41
	 * @param is 下载图片的流
	 * @param reqWidth 目标图片的宽
	 * @param reqHeight 目标图片的高
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(InputStream instream, int reqWidth, int reqHeight) throws IOException {

	    //Copy instream for decode twice 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    copy(instream,out);
	    ByteArrayInputStream instream2 = new ByteArrayInputStream(out.toByteArray());

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeStream(instream, null, options);
	    instream2.close();

	    options.inJustDecodeBounds = false;

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    return BitmapFactory.decodeStream(instream, null, options);
	 }
	
	//Copy instream method
	public static void copy(InputStream input, OutputStream output) throws IOException{
		 final int IO_BUFFER_SIZE = 4 * 1024;
	     byte[] buffer = new byte[IO_BUFFER_SIZE];	
		 int n = 0;	
		 while (-1 != (n = input.read(buffer))) {	
		     output.write(buffer, 0, n);
		 }
	 }
	
	/**
	 * 获取图片在内存中被缩放的大小
	 * 
	 * @author chengang
	 * @date 2014-9-9 下午1:51:31
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
	     final int height = options.outHeight;
	     final int width = options.outWidth;
	     int inSampleSize = 1;

	     if (height > reqHeight || width > reqWidth) {
	    	 if (width > height) {
	    		 inSampleSize = Math.round((float) height / (float) reqHeight);
	    	 } else {
	    		 inSampleSize = Math.round((float) width / (float) reqWidth);
     		}
	     }

	     return inSampleSize;
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
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> imageViewReference;
		private String url;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) {
	    	url = params[0];
	    	Bitmap tempBitmap = downloadBitmap(url);
	        return tempBitmap;
//	        		decodeSampledBitmapFromResource(mContext.getResources(), url, 100, 100);
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
	            if (this == bitmapWorkerTask && imageView != null) {	            	
	            	imageView.setTag(bitmap);
	     	       	imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 72,72));
	            }
	        }
	    }
	}
	
	public void loadBitmap(String url, ImageView imageView) {
	   Bitmap bitmap = getBitmapFromCache(url);
	   if (bitmap == null) {
	       forceDownload(url, imageView);
	   } else {
		   imageView.setVisibility(View.VISIBLE);
	       cancelPotentialDownload(url, imageView);	      
	       imageView.setTag(bitmap);
	       imageView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 72,72));
	   }
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
	   
	   /**
	    * Same as download but the image is always downloaded and the cache is not used.
	    * Kept private at the moment as its interest is not clear.
	    */
	   private void forceDownload(String url, ImageView imageView) {
	       // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
	       if (url == null) {
	           imageView.setImageDrawable(null);
	           return;
	       }

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
	   
	   /**
	    * Returns true if the current download has been canceled or if there was no download in
	    * progress on this image view.
	    * Returns false if the download in progress deals with the same url. The download is not
	    * stopped in that case.
	    */
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
	   
	   private static Bitmap downloadBitmap(String url) {	       
		   Bitmap bitmapImage;
		   URL imageUrl = null;
		   try {
		   imageUrl = new URL(url);

		   HttpGet httpRequest = null;
		   httpRequest = new HttpGet(imageUrl.toURI());

		   HttpClient httpclient = new DefaultHttpClient();
		   HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

		   final HttpEntity entity = response.getEntity();
           if (entity != null) {
               InputStream inputStream = null;
               try {
                   inputStream = entity.getContent();
                   BitmapFactory.Options options = new BitmapFactory.Options();
                   options.inSampleSize = 2;
                   bitmapImage = BitmapFactory.decodeStream(inputStream, null, options);
//                   bitmapImage = decodeSampledBitmapFromResource(inputStream, 72, 72);
                   return bitmapImage;
               } catch (Exception e) {
                   e.printStackTrace();
               } finally {
                   if (inputStream != null) {
                       inputStream.close();
                   }
                   entity.consumeContent();
                   
                   System.gc();
               }
           }

		    } catch (URISyntaxException e) {
		       e.printStackTrace();
		       return null;
		    } catch (MalformedURLException e) {
		       e.printStackTrace();
		       return null;
		    } catch (IOException e) {
		       e.printStackTrace();
		       return null;
		    }
		return null;
	   }

	   /*
	    * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
	    */
	   static class FlushedInputStream extends FilterInputStream {
	       public FlushedInputStream(InputStream inputStream) {
	           super(inputStream);
	       }

	       @Override
	       public long skip(long n) throws IOException {
	           long totalBytesSkipped = 0L;
	           while (totalBytesSkipped < n) {
	               long bytesSkipped = in.skip(n - totalBytesSkipped);
	               if (bytesSkipped == 0L) {
	                   int b = read();
	                   if (b < 0) {
	                       break;  // we reached EOF
	                   } else {
	                       bytesSkipped = 1; // we read one byte
	                   }
	               }
	               totalBytesSkipped += bytesSkipped;
	           }
	           return totalBytesSkipped;
	       }
	   }
	   
	   /**
	    * Clears the image cache used internally to improve performance. Note that for memory
	    * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
	    */
	   public void clearCache() {
	       sHardBitmapCache.clear();
	       sSoftBitmapCache.clear();
	   }
}
