package com.chuck.relativeschat.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.chuck.relativeschat.common.BmobConstants;

import android.os.Environment;

public class FileUtils {

	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}
	public FileUtils() {
		//得到当前外部存储设备的目录
		// /SDCARD
		SDPATH = BmobConstants.MyAvatarDir;
	}
	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir() {
		File dir = new File(SDPATH);
		if(!dir.exists()){
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName){
		File file = new File(SDPATH + fileName + ".png");
		if(file.exists()){
			file.delete();
		}
		return false;
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		try{
			creatSDDir();
			file = creatSDFile(fileName);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[4 * 1024];
//			while((input.read(buffer)) != -1){
//				output.write(buffer);
//			}
			
			int len = input.read(buffer);
			while(len != -1){
				output.write(buffer, 0, len);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
}
