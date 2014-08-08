package com.chuck.relativeschat.bean;

public class FileTypeBean {
	private String guessFileType;// 通过方法返回的类型
	private String submitFileType;// 需要获取的文件类型

	public void setGuessFileType(String type) {
		this.guessFileType = type;
	}

	public String getGuessFileType() {
		if (!this.guessFileType.isEmpty()) {
			return guessFileType;
		}
		return null;
	}

	public void setSubmitFileType(String type) {
		this.submitFileType = type;
	}

	public String getSubmitFileType() {
		if (!this.submitFileType.isEmpty()) {
			return submitFileType;
		}
		return null;
	}
	
	public void myString()
	{
		
	}
}