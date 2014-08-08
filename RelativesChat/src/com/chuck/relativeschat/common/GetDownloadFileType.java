package com.chuck.relativeschat.common;

import java.util.ArrayList;
import java.util.List;

import com.chuck.relativeschat.bean.FileTypeBean;

import android.content.Context;

public class GetDownloadFileType
{
	List<FileTypeBean> typeList = new ArrayList<FileTypeBean>(); 
	private String unknownType;
	
	public GetDownloadFileType(Context context , String type)
	{
		this.unknownType = type;
		
		String[] allType = {"application/msword","application/octet-stream bin",
				"application/pdf","application/postscript","appication/powerpoint",
				"appication/rtf","appication/x-compress","application/x-gzip",
				"application/x-gtar","application/x-shockwave-flash",
				"application/x-tar","application/zip","audio/basic",
				"audio/mpeg","audio/x-aiff","audio/x-pn-realaudio",
				"audio/x-pn-realaudio-plugin","audio/x-wav",
				"image/cgm","image/gif","image/jpeg","image/png","text/html",
				"text/plain","text/xml","text/json"};
		
		String[] fileType = {"doc","dms lha lzh exe class","pdf",
				"ai eps ps","ppt","rtf","z","gz","gtar","swf",
				"tar","zip","au snd","mpeg mp2","mid midi rmf",
				"ram ra","rpm","wav","cgm","gif","jpeg jpg jpe","png",
				"HTML","TXT","XML","json"};
		
		for (int i = 0; i < allType.length; i++) 
		{
			FileTypeBean myFileType = new FileTypeBean();
			myFileType.setGuessFileType(allType[i]);
			myFileType.setSubmitFileType(fileType[i]);
			
			typeList.add(myFileType);
		}
	}
	
	public String getActualFileType()
	{
		for (int i = 0; i < typeList.size(); i++) 
		{
			if(typeList.get(i).getGuessFileType().trim() == this.unknownType.trim())
			{
				return typeList.get(i).getSubmitFileType().trim();
			}
		}
		return null;
	}
}
