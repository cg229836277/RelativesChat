package com.chuck.relativeschat.common;

import java.util.Arrays;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-10-13 下午1:09:36
 * @author chengang
 * @version 1.0
 */
public class VideoThumbnailGenerate {
	/**
	 * 获取在线视频的略缩图
	 * 
	 * @author chengang
	 * @date 2014-10-13 下午1:10:52
	 * @param videoUrl
	 * @return
	 */
	public static Bitmap getVideoThumbnail(String videoUrl){
		Bitmap generateBitmap = null;
		FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();
		try {
			fmmr.setDataSource(videoUrl);
			generateBitmap = fmmr.getFrameAtTime();

			if (generateBitmap != null) {
				Bitmap b2 = fmmr.getFrameAtTime(4000000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC);
				if (b2 != null) {
					generateBitmap = b2;
				}
				if (generateBitmap.getWidth() > 640) {// 如果图片宽度规格超过640px,则进行压缩
					generateBitmap = ThumbnailUtils.extractThumbnail(generateBitmap,
							640, 480,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
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
}
