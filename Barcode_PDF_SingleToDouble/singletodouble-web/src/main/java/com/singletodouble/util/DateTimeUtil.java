package com.singletodouble.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateTimeUtil {
	public static final String STRING_FORMAT_YEAR 		= "yyyy";
	public static final String STRING_FORMAT_MONTH 		= "MM";
	public static final String STRING_FORMAT_YM_KEY 	= "yyyyMM";
	public static final String STRING_FORMAT_YM_KEY2 	= "yyyy/MM";
	public static final String STRING_FORMAT_YMD_KEY 	= "yyyyMMdd";
	public static final String STRING_FORMAT_YMD_KEY2 	= "yyyy/MM/dd";
	public static final String STRING_FORMAT_YMD_KEY3 	= "yyyy-MM-dd";
	public static final String STRING_FORMAT_KEY 		= "yyyyMMddHHmmss";
	public static final String STRING_FORMAT_KEY2 		= "yyyy/MM/dd HH:mm:ss";
	public static final String STRING_MSEC_FORMAT_KEY 	= "yyyyMMddHHmmssSSS";
	public static final String STRING_FORMAT_SHORT	 	= "yyMMdd";
	
	/**
	 * <P>把字符串转换成日期</P>
	 * @param value
	 * @param format
	 * @return
	 */
	public static Date convertString2Date(String value, String format) {
		SimpleDateFormat smFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = smFormat.parse(value);
		} catch (Exception e) {
			
		}
		return date;
	}
	
	/**
	 * <P>把日期转换成指定格式的字符串</P>
	 * @param value
	 * @param format
	 * @return
	 */
	public static String convertDate2String(Date date, String format) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat smFormat = new SimpleDateFormat(format);
		return smFormat.format(date);
	}
	
	/**
	 * <P>把某个格式的日期字符串转换成指定格式的日期字符串</P>
	 * @param value
	 * @param format
	 * @return
	 */
	public static String convertDateString(String date, String originalFormat, String format) {
		try {
			if (StringUtils.isEmpty(date)) {
				return "";
			}
			SimpleDateFormat smFormat = new SimpleDateFormat(originalFormat);
			Date date1 = smFormat.parse(date);
			
			SimpleDateFormat smFormat1 = new SimpleDateFormat(format);		
			return smFormat1.format(date1);
		} catch (ParseException e) {
			
		} 
		return null;
	}
	
	/**
	 * 获取唯一码(用年月日时分秒做KEY)
	 * @return
	 */
	public static String getCurrentMsec() {
		 return convertDate2String(new Date(), STRING_MSEC_FORMAT_KEY);
	}
	
	/**
	 * 获取唯一码(用年月日时分秒做KEY)
	 * @return
	 */
	public static String getDateKey() {
		 return convertDate2String(new Date(), STRING_FORMAT_KEY);
	}
	
	/**
	 * 获取唯一码(用年月日做KEY)
	 * @return
	 */
	public static String getYmdDateKey() {
		 return convertDate2String(new Date(), STRING_FORMAT_YMD_KEY);
	}
	
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getCurrentTime() {
		 return convertDate2String(new Date(), STRING_FORMAT_KEY2);
	}
	
	/**
	 * 日期比较，前者大或者等于返回true
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean compareDate(String date1, String date2, String format) {
		SimpleDateFormat smFormat = new SimpleDateFormat(format);
		try {
			Date d1 = smFormat.parse(date1);
			Date d2 = smFormat.parse(date2);
			int result = d1.compareTo(d2);
			if (result < 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 时间差
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long diffSecond(String date1, String date2, String format) {
		SimpleDateFormat smFormat = new SimpleDateFormat(format);
		try {
			Date d1 = smFormat.parse(date1);
			Date d2 = smFormat.parse(date2);
			long value = d1.getTime() - d2.getTime();
			return value / 1000;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * day天前的日期
	 * @param d
	 * @param day
	 * @return
	 */
    public static Date getDateBefore(Date d, int day) {  
        Calendar now = Calendar.getInstance();  
        now.setTime(d);  
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);  
        return now.getTime();  
    }
    
	/**
	 * 时间差
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long diffDay(String date1, String date2, String format) {
		try {
			long timesInterval = diffSecond(date1, date2, format);
			return (long)Math.ceil(timesInterval / 24 / 3600);
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 月份差
	 * @param date1
	 * @param date2
	 * @return
	 */
    public static int diffMonth(String date1, String date2, String format) {
    	int monthDiff = 0;
		try {
			int year1 = Integer.parseInt(DateTimeUtil.convertDateString(date1, format, STRING_FORMAT_YEAR));
			int year2 = Integer.parseInt(DateTimeUtil.convertDateString(date2, format, STRING_FORMAT_YEAR));
			
			int month1 = Integer.parseInt(DateTimeUtil.convertDateString(date1, format, STRING_FORMAT_MONTH));
			int month2 = Integer.parseInt(DateTimeUtil.convertDateString(date2, format, STRING_FORMAT_MONTH));
			
			monthDiff = (year1 - year2) * 12;
			monthDiff = Math.abs(monthDiff + month1 - month2);
		} catch (Exception e) {
			monthDiff = -1;
		}
		return monthDiff;
    }
    
	/**
	 * <P>月份-1</P>
	 * @param value
	 * @param format
	 * @return
	 * @throws ParseException 
	 */
	public static String addMonthDiff(String date, String format, int monthDiff) throws ParseException {
		if (StringUtils.isEmpty(date)) {
			return "";
		}
		SimpleDateFormat smFormat = new SimpleDateFormat(format);
		Date date1 = smFormat.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		cal.add(Calendar.MONTH, monthDiff);
		return DateTimeUtil.convertDate2String(cal.getTime(), format);	
	}
}

