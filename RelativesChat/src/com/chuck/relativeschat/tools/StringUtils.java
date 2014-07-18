package com.chuck.relativeschat.tools;

public class StringUtils {

	public static boolean isEmpty(String str){
		if(str != null && !str.isEmpty()){
			return false;
		}
		return true;
	}
}
