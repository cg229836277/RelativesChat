package com.chuck.relativeschat.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	
	public final static String FORMAT_YEAR = "yyyy";
	public final static String FORMAT_MONTH_DAY = "MM月dd日";
	
	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_TIME = "HH:mm";
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日  hh:mm";
	
	public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm";
	public final static String FORMAT_DATE1_TIME = "yyyy/MM/dd HH:mm";
	public final static String FORMAT_DATE_TIME_SECOND = "yyyy/MM/dd HH:mm:ss";
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;//
	private static final int MONTH = 30 * 24 * 60 * 60;//
	private static final int DAY = 24 * 60 * 60;//
	private static final int HOUR = 60 * 60;//
	private static final int MINUTE = 60;//

	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;//
		System.out.println("timeGap: " + timeGap);
		String timeStr = null;
		if (timeGap > YEAR) {
			timeStr = timeGap / YEAR + "年";
		} else if (timeGap > MONTH) {
			timeStr = timeGap / MONTH + "月";
		} else if (timeGap > DAY) {//
			timeStr = timeGap / DAY + "日";
		} else if (timeGap > HOUR) {//
			timeStr = timeGap / HOUR + "时";
		} else if (timeGap > MINUTE) {//
			timeStr = timeGap / MINUTE + "分";
		} else {//
			timeStr = "刚刚";
		}
		return timeStr;
	}

	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_DATE_TIME);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

 	public static String dateToString(Date data, String formatType) {
 		return new SimpleDateFormat(formatType).format(data);
 	}
 
 	public static String longToString(long currentTime, String formatType){
 		String strTime="";
		Date date = longToDate(currentTime, formatType);//
		strTime = dateToString(date, formatType); //
 		return strTime;
 	}
 
 	public static Date stringToDate(String strTime, String formatType){
 		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
 		Date date = null;
 		try {
			date = formatter.parse(strTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
 		return date;
 	}
 
 	public static Date longToDate(long currentTime, String formatType){
 		Date dateOld = new Date(currentTime); //
 		String sDateTime = dateToString(dateOld, formatType); //
 		Date date = stringToDate(sDateTime, formatType); //
 		return date;
 	}

 	public static long stringToLong(String strTime, String formatType){
 		Date date = stringToDate(strTime, formatType); // 
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = dateToLong(date); //
 			return currentTime;
 		}
 	}

 	public static long dateToLong(Date date) {
 		return date.getTime();
 	}
	 	
	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	/**
	  * @Title: getChatTime
	  * @Description: TODO
	  * @param @param timesamp
	  * @param @return 
	  * @return String
	  * @throws
	  */
	public static String getChatTime(long timesamp) {
		long clearTime = timesamp*1000;
		String result = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(clearTime);
		int temp = Integer.parseInt(dateFormat.format(today)) - Integer.parseInt(dateFormat.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天" + getHourAndMin(clearTime);
			break;
		case 1:
			result = "昨天" + getHourAndMin(clearTime);
			break;
		case 2:
			result = "前天" + getHourAndMin(clearTime);
			break;

		default:
			result = getTime(clearTime);
			break;
		}

		return result;
	}
}