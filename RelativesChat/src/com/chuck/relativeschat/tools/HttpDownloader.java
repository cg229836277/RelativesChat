package com.chuck.relativeschat.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.chuck.relativeschat.common.BmobConstants;

/**
 * @Title：SAFEYE@
 * @Description：
 * @date 2014-8-8 下午4:22:27
 * @author chengang
 * @version 1.0
 */
public class HttpDownloader {
	private static URL url=null;
	public static String filePath = BmobConstants.MyAvatarDir;
	/**
	 * 下载文件
	 * 
	 * @author chengang
	 * @date 2014-8-8 下午4:31:56
	 * @param urlStr 下载地址
	 * @param path 下载之后文件存放
	 * @param fileName  文件名
	 * @return
	 */
	public static Bitmap downfile(String urlStr,String fileName){
		File resultFile = new File(filePath + fileName + ".png");
		Bitmap iconMap = null;
		if(resultFile.exists()){
			resultFile.delete();
		}
		try {
			InputStream input=null;
			input = getInputStream(urlStr);
			iconMap = BitmapFactory.decodeStream(input);
			if(iconMap == null){
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}		
		return iconMap;
	}
  //由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
   public static InputStream getInputStream(String urlStr) throws IOException{     
	   InputStream is=null;
	    try {
			url=new URL(urlStr);
			HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
			is=urlConn.getInputStream();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}			
	    return is;
   }
}