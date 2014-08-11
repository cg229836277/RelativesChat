package com.chuck.relativeschat.tools;

public class StringUtils {

	public static boolean isEmpty(String str){
		if(str != null){
			if(!str.isEmpty()){
				return false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	}
}
