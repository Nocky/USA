package com.linkloving.rtring_c_watch.logic.reportday.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.util.Log;

import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils.TimeZoneHelper;

/**
 * 时间转换工具类
 * 
 * @author Administrator
 * 
 */
public class TimeUtil
{
	/**
	 * 将秒个数转化为字符串 ，格式为"00:00:00"
	 * 
	 * @param secondCount
	 * @return
	 */
	public static String formatTimeFromSecondCount(int secondCount)
	{
		String str = "";
		if (secondCount < 60)
		{
			String s;
			if (secondCount < 10)
			{
				s = "0" + secondCount;
			}
			else
			{
				s = secondCount + "";
			}
			str = "00:00:" + s;
		}
		else if (secondCount < 3600)
		{
			String m, s;
			if (secondCount / 60 < 10)
			{
				m = "0" + secondCount / 60;
			}
			else
			{
				m = secondCount / 60 + "";
			}
			if (secondCount % 60 < 10)
			{
				s = "0" + secondCount % 60;
			}
			else
			{
				s = secondCount % 60 + "";
			}
			str = "00:" + m + ":" + s;
		}
		else
		{
			String h, m, s;
			if (secondCount / 3600 < 10)
			{
				h = "0" + secondCount / 3600;
			}
			else
			{
				h = secondCount / 3600 + "";
			}
			if (((secondCount % 3600) / 60) < 10)
			{
				m = "0" + (secondCount % 3600) / 60;
			}
			else
			{
				m = (secondCount % 3600) / 60 + "";
			}
			if (secondCount % 60 < 10)
			{
				s = "0" + secondCount % 60;
			}
			else
			{
				s = secondCount % 60 + "";
			}
			str = h + ":" + m + ":" + s;
		}
		return str;
	}

	/**
	 * 格式化分钟数 为 时间串
	 * 
	 * @param minuteCount
	 * @return
	 */
	public static String formatTimeFromMinuteCount(int minuteCount)
	{
		SimpleDateFormat sdfformat = new SimpleDateFormat("hh:mm");
		SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm a");
		if (minuteCount == 0)
		{
			return "00:00";
		}
		
//		Calendar c = Calendar.getInstance(TimeZone.getDefault());
//		TimeZone tz = c.getTimeZone();
//		int hours = minuteCount / 60 +(c.get(Calendar.DST_OFFSET)/ (60 * 60 * 1000));
		
		StringBuilder sb = new StringBuilder();
		int hours = minuteCount / 60;
		int minutes = minuteCount % 60;
//		if (hours < 10)
//		{
//			sb.append("0");
//		}
//		sb.append(hours);
//		sb.append(":");
//		if (minutes < 10)
//		{
//			sb.append("0");
//		}
//		sb.append(minutes);
		
		Log.e("TimeUtil", sb.toString());
		if(hours>12){
			sb.append(ToolKits.int2String(hours-12) + ":" + ToolKits.int2String(minutes)+" PM");
		}else{
			if(hours==0)
				sb.append("12:" + ToolKits.int2String(minutes)+" AM");
			else if(hours==12)
				sb.append("12:" + ToolKits.int2String(minutes)+" PM");
			else
				sb.append(ToolKits.int2String(hours) + ":" + ToolKits.int2String(minutes)+" AM");
		}
		return sb.toString();
	}
	
	public static String format12TimeFromString(String time)
	{
		SimpleDateFormat sdfformat = new SimpleDateFormat("hh:mm");
		SimpleDateFormat sdformat = new SimpleDateFormat("hh:mm a");
		//			return sdformat.format(sdfformat.parse(time));
		return time;
	}

	/**
	 * 判断两个日期是否为同一天
	 * 
	 * @param date1
	 *            日期一
	 * @param date2
	 *            日期二
	 * @return 同一天true，不是同一天false
	 */
	public static boolean isSameDate(Date date1, Date date2)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH))
		{
			result = true;
		}
		return result;
	}

	public static boolean isToday(Date targetDate)
	{
		return isSameDate(new Date(), targetDate);
	}

	public static boolean isYesterday(Date currentDate, Date targetDate)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(currentDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(targetDate);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& (c1.get(Calendar.DAY_OF_MONTH) - 1) == c2.get(Calendar.DAY_OF_MONTH))
		{
			result = true;
		}
		return result;
	}

	public static boolean isTheDayBeforeYesterday(Date currentDate, Date targetDate)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(currentDate);

		Calendar c2 = Calendar.getInstance();
		c2.setTime(targetDate);

		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
				&& (c1.get(Calendar.DAY_OF_MONTH) - 2) == c2.get(Calendar.DAY_OF_MONTH))
		{
			result = true;
		}
		return result;
	}

	/**
	 * 判断是否同一周
	 * 
	 * @param date1
	 * @param date2
	 * @return 同一天true，不是同一天false
	 */
	public static boolean isSameWeek(Date date1, Date date2)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR))
		{
			result = true;
		}
		return result;
	}

	/**
	 * 判断是否同一个月
	 * 
	 * @param date1
	 * @param date2
	 */
	public static boolean isSameMonth(Date date1, Date date2)
	{
		boolean result = false;
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);
		if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
		{
			result = true;
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static Date parseDateByYMDFromTime(long time)
	{
		if (time > 0l)
		{
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(time);
			String str = sf.format(date);
			try
			{
				return sf.parse(str);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		return new Date();
	}

	public static Date afterDate(Date time, int n)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(Calendar.DATE, n);
		return c.getTime();
	}

	public static Date afterDateByWeek(Date time, int n)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(Calendar.WEEK_OF_YEAR, n);
		return c.getTime();
	}

	public static Date afterDateByMonth(Date time, int n)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.add(Calendar.MONTH, n);
		return c.getTime();
	}

	// public static String formatDateByZHAndYMD(Date time) {
	// Calendar c = Calendar.getInstance();
	// c.setTime(time);
	// int month = c.get(Calendar.MONTH) + 1;
	// int day = c.get(Calendar.DAY_OF_MONTH);
	// int year = c.get(Calendar.YEAR);
	// String yearStr =
	// MainApplication.Instance().getString(R.string.unit_year);
	// String monthStr =
	// MainApplication.Instance().getString(R.string.unit_month);
	// String dayStr = MainApplication.Instance().getString(R.string.unit_day);
	// return year + yearStr + month + monthStr + day + dayStr;
	//
	// }

	public static String formatDateByYYMMDD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByMMDD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM.dd");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByYYYYMMDD(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return simpleDateFormat.format(time);
	}

	// public static String formatDateByYYYYMMDDWithI18N(Date time) {
	// Context context = MainApplication.Instance().getApplicationContext();
	// String unitYear = context.getString(R.string.unit_year4date);
	// String unitMonth = context.getString(R.string.unit_month4date);
	// String unitDay = context.getString(R.string.unit_day4date);
	// String pattern = "yyyy" + unitYear + "MM" + unitMonth + "dd" + unitDay;
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	// return simpleDateFormat.format(time);
	// }

	public static String formatDateByHM(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		return simpleDateFormat.format(time);
	}

	public static Date parseDateByHM(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateByYMDHM(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseDateByYMDHMS(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return new Date();
	}

	// public static String formatDateByMMDDWithI18N(Date time) {
	// String unitMonth =
	// MainApplication.Instance().getString(R.string.unit_month4date);
	// String unitDay =
	// MainApplication.Instance().getString(R.string.unit_day4date);
	// SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM" + unitMonth
	// + "dd" + unitDay);
	// return simpleDateFormat.format(time);
	// }

	public static String formatDateByYMDHM(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByYMDHMLong(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return simpleDateFormat.format(time);
	}

	public static String formatDateByYMDHMS(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(time);
	}

	public static String formatTimeByHM(long time)
	{
		return formatDateByHM(new Date(time));
	}

	public static Date parseDateByYMD(String time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd");
		try
		{
			return simpleDateFormat.parse(time);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		return new Date();
	}

	public static String formatDateByYMDHMSLong(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(time);
	}

	public static boolean isWeekend(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day == 1 || day == 7)
		{
			return true;
		}
		return false;
	}

	// /**
	// * 获取最近35天的日期文本<br>
	// *
	// * @return <Key:[1-35],Value:<Key:"1.1",Value:"周三">>
	// */
	// public static Map<Integer, Map<String, String>> get35Day() {
	// Map<Integer, Map<String, String>> xMap = new LinkedHashMap<Integer,
	// Map<String, String>>();
	// Date currentDate = new Date();
	// Calendar calendar = Calendar.getInstance();
	// int day = calendar.get(Calendar.DAY_OF_WEEK);
	// int diff = 7 - day;
	// int j = 1;
	// for (int i = 34; i >= 0; i--) {
	// Map<String, String> dayMap = new LinkedHashMap<String, String>();
	// Date date = afterDate(currentDate, -i);
	// Calendar calendar1 = Calendar.getInstance();
	// calendar1.setTime(date);
	// int m = calendar1.get(Calendar.MONTH) + 1;
	// int d = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
	// int dd = calendar1.get(Calendar.DAY_OF_MONTH);
	// dayMap.put(m + "." + dd, getDayOfWeek(d));
	// xMap.put(j, dayMap);
	// j++;
	// }
	// return xMap;
	// }

	// /**
	// * 周几<br>
	// * 1-->"周一"or"Mon"
	// */
	// private static String getDayOfWeek(int n) {
	// Context context = MainApplication.Instance();
	// String result = "";
	// switch (n) {
	// case 0:
	// result = context.getString(R.string.common_sunday_short);
	// break;
	//
	// case 1:
	// result = context.getString(R.string.common_monday_short);
	// break;
	//
	// case 2:
	// result = context.getString(R.string.common_tuesday_short);
	// break;
	//
	// case 3:
	// result = context.getString(R.string.common_wednesday_short);
	// break;
	//
	// case 4:
	// result = context.getString(R.string.common_thursday_short);
	// break;
	//
	// case 5:
	// result = context.getString(R.string.common_friday_short);
	// break;
	//
	// case 6:
	// result = context.getString(R.string.common_saturday_short);
	// break;
	//
	// default:
	// break;
	// }
	// return result;
	// }

	// /**
	// * 获取最近35周的日期文本<br>
	// *
	// * @return <Key:[1-35],Value:<Key:"1-7",Value:"1月">>
	// */
	// public static Map<Integer, Map<String, String>> get35Week() {
	// Map<Integer, Map<String, String>> xMap = new LinkedHashMap<Integer,
	// Map<String, String>>();
	// int j = 1;
	// for (int i = 34; i >= 0; i--) {
	// Map<String, String> dayMap = new LinkedHashMap<String, String>();
	// Calendar calendar1 = Calendar.getInstance();
	// calendar1.add(Calendar.WEEK_OF_YEAR, -i);// 获得每一周
	// calendar1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
	// int dd = calendar1.get(Calendar.DAY_OF_MONTH);// 设置一周开始为周日
	//
	// Calendar calendar2 = Calendar.getInstance();
	// calendar2.add(Calendar.WEEK_OF_YEAR, -i);
	// calendar2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
	// int dd1 = calendar2.get(Calendar.DAY_OF_MONTH);// 设置一周结束日为周六
	// int m = calendar2.get(Calendar.MONTH) + 1;
	//
	// if ((calendar1.get(Calendar.MONTH) != calendar2.get(Calendar.MONTH)) ||
	// calendar1.get(Calendar.DAY_OF_MONTH) == 1) {
	// dayMap.put(dd + "-" + dd1, getMonthOfYear(m));
	// } else {
	// dayMap.put(dd + "-" + dd1, "");
	// }
	//
	// xMap.put(j, dayMap);
	// j++;
	// }
	// return xMap;
	// }

	// /**
	// * 几月<br>
	// * 1-->"1月"or"Jan"
	// */
	// public static String getMonthOfYear(int n) {
	// Context context = MainApplication.Instance();
	// String result = "";
	// switch (n) {
	// case 1:
	// result = context.getString(R.string.common_january);
	// break;
	//
	// case 2:
	// result = context.getString(R.string.common_february);
	// break;
	//
	// case 3:
	// result = context.getString(R.string.common_march);
	// break;
	//
	// case 4:
	// result = context.getString(R.string.common_april);
	// break;
	//
	// case 5:
	// result = context.getString(R.string.common_may);
	// break;
	//
	// case 6:
	// result = context.getString(R.string.common_june);
	// break;
	//
	// case 7:
	// result = context.getString(R.string.common_july);
	// break;
	//
	// case 8:
	// result = context.getString(R.string.common_august);
	// break;
	//
	// case 9:
	// result = context.getString(R.string.common_september);
	// break;
	//
	// case 10:
	// result = context.getString(R.string.common_october);
	// break;
	//
	// case 11:
	// result = context.getString(R.string.common_november);
	// break;
	//
	// case 12:
	// result = context.getString(R.string.common_december);
	// break;
	//
	// default:
	// break;
	// }
	// return result;
	// }

	// /**
	// * 获取最近35月的日期文本<br>
	// *
	// * @return <Key:[1-35],Value:<Key:"1月",Value:"2014">>
	// */
	// public static Map<Integer, Map<String, String>> get35Month() {
	// Map<Integer, Map<String, String>> xMap = new LinkedHashMap<Integer,
	// Map<String, String>>();
	// Date currentDate = new Date();
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(currentDate);
	// int month = calendar.get(Calendar.MONTH) + 1;
	// // 证明是礼拜日
	// int j = 1;
	// for (int i = 34; i >= 0; i--) {
	// Map<String, String> dayMap = new LinkedHashMap<String, String>();
	// Date date = afterDateByMonth(currentDate, -i);
	// Calendar calendar1 = Calendar.getInstance();
	// calendar1.setTime(date);
	// int m = calendar1.get(Calendar.MONTH) + 1;
	// int year = calendar1.get(Calendar.YEAR);
	//
	// Date dateMonth = afterDateByMonth(date, -1);
	// Calendar calendarMonth = Calendar.getInstance();
	// calendarMonth.setTime(dateMonth);
	// int ayear = calendarMonth.get(Calendar.YEAR);
	// if (year != ayear) {
	// dayMap.put(getMonthOfYear(m), year + "");
	// } else {
	// dayMap.put(getMonthOfYear(m), "");
	// }
	// xMap.put(j, dayMap);
	// j++;
	// }
	// return xMap;
	// }

	@SuppressLint("SimpleDateFormat")
	public static String parseDateByYMDH00(Date time)
	{
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		return simpleDateFormat.format(time);
	}

	// TODO:??
	public static List<String> getDateInCurrentWeek()
	{
		List<String> list = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		list.add(formatDateByYYMMDD(calendar.getTime()));
		while (day != Calendar.SUNDAY)
		{
			calendar.add(Calendar.DAY_OF_WEEK, -1);
			list.add(formatDateByYYMMDD(calendar.getTime()));
			// calendar = getDateBefore(calendar.getTime(), 1);
			day = calendar.get(Calendar.DAY_OF_WEEK);
		}
		return list;
	}

	/**
	 * 获取指定日期00:00时间毫秒值
	 * 
	 * @param timeInMillis
	 *            指定日期时间毫秒值
	 * @return
	 */
	public static long getBeginOfDay(long timeInMillis)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * 获取指定日期24:00毫秒值
	 * 
	 * @param timeInMillis
	 *            指定日期时间毫秒值
	 * @return
	 */
	public static long getEndOfDay(long timeInMillis)
	{
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeInMillis);
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}

	/**
	 * 获取当前日期是周几(周日0,周一1...)
	 */
	public static int getCurrentDayOfWeek()
	{
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_WEEK) - 1;// 周日0 周一1 ....
	}

	/**
	 * 获取小时
	 * 
	 * @param minuteCount
	 *            分钟数
	 * @return
	 */
	public static int getHourFromMinuteCount(int minuteCount)
	{
		return (int) (minuteCount / 60);
	}

	/**
	 * 获取分钟
	 * 
	 * @param minuteCount
	 *            分钟数
	 * @return
	 */
	public static int getMinuteFromMinuteCount(int minuteCount)
	{
		return (int) (minuteCount % 60);
	}

	/**
	 * 将分钟数格式化为"1h1m"
	 * 
	 * @param minuteCount
	 *            分钟数
	 * @return
	 */
	public static String formatMinuteCountByHM(int minuteCount)
	{
		int h = (int) (minuteCount / 60);
		int m = (int) (minuteCount % 60);
		StringBuilder builder = new StringBuilder();
		if (h > 0)
		{
			builder.append(h).append("h");
		}
		if (m > 0)
		{
			builder.append(m).append("m");
		}
		if (h == 0 && m == 0)
		{
			builder.append(0);
		}
		return builder.toString();
	}

	/**
	 * 解析出日期
	 * 
	 * @param dayIndex
	 *            相对于1970年的天数
	 * @param hourIndex
	 *            相对于零点的小时数(0-23)
	 * @return
	 */
	public static Date parseDateFromDayIndexAndHourIndex(int dayIndex, int hourIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.add(Calendar.DATE, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, hourIndex);
		long resultTime = calendar.getTimeInMillis();
		return new Date(resultTime);
	}

	/**
	 * 解析出毫秒值
	 * 
	 * @param dayIndex
	 *            相对于1970年的天数
	 * @param hourIndex
	 *            相对于零点的小时数(0-23)
	 * @return
	 */
	public static long parseTimeFromDayIndexAndHourIndex(int dayIndex, int hourIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);// -TimeZone.getDefault().getRawOffset());
		calendar.add(Calendar.DATE, dayIndex);
		calendar.set(Calendar.HOUR_OF_DAY, hourIndex);
		long resultTime = calendar.getTimeInMillis();
		return resultTime;
	}

	/**
	 * 解析出毫秒值
	 * 
	 * @param dayIndex
	 *            相对于1970年的天数
	 * @param seconds30Count
	 *            相对于零点的30秒个数
	 * @return
	 */
	public static long parseTimeFromDayIndexAndSeconds30Count(int dayIndex, int seconds30Count)
	{
		Calendar calendar = Calendar.getInstance();
		// calendar.setTimeInMillis(0);
		calendar.setTimeInMillis(-TimeZone.getDefault().getRawOffset());
		calendar.add(Calendar.DATE, dayIndex);
		calendar.set(Calendar.SECOND, seconds30Count * 30);
		long resultTime = calendar.getTimeInMillis();
		return resultTime;
	}

	/**
	 * 从生日解析出年龄
	 * 
	 * @param birthdayTime
	 * @return
	 */
	public static int parseAgeFromBirthdayTime(long birthdayTime)
	{
		Calendar cal = Calendar.getInstance();
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
		cal.setTimeInMillis(birthdayTime);

		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth)
		{
			if (monthNow == monthBirth)
			{
				if (dayOfMonthNow < dayOfMonthBirth)
				{
					age--;
				}
				else
				{
					// do nothing
				}
			}
			else
			{
				// monthNow>monthBirth
				age--;
			}
		}
		else
		{
			// monthNow<monthBirth
			// do nothing
		}

		return age;
	}

	/**
	 * 获取指定时间与1970.01.01的天数差值
	 */
	public static int getDayIndexFrom1970(long ms)
	{
		return (int) (TimeZoneHelper.getTimeZoneOffsetMills() >= 0 ? ((ms + TimeZoneHelper.getTimeZoneOffsetMills()) / Constant.ONE_DAY_MILLIS) : (((ms + TimeZoneHelper.getTimeZoneOffsetMills()) / Constant.ONE_DAY_MILLIS) + 1));
	}
	/**
	 * 获取与1970.01.01的指定天数差值的时间
	 */
	public static long getTimeByDayIndex(int dayIndex)
	{
		return dayIndex * Constant.ONE_DAY_MILLIS;
	}

	/**
	 * 获取当前时间(东八区)
	 * 
	 * @return
	 */
	public static long getCurrentTime()
	{
		return Calendar.getInstance().getTimeInMillis() + TimeZone.getDefault().getRawOffset();
	}

	/**
	 * 获取相对于1970年的天数
	 * 
	 * @return
	 */
	public static int getCurrentDay()
	{
		return (int) (getCurrentTime() / Constant.ONE_DAY_MILLIS);
	}

	/**
	 * 将睡眠30秒片段数转换为睡眠小时数
	 * 
	 * @param seconds30Count
	 * @return
	 */
	public static float getHourCountFromSeconds30Count(int seconds30Count)
	{
		return (float) (seconds30Count / 2 * 10 / 60) / 10;
	}

	/**
	 * 将相对于1970年的天数转换为日期
	 * 
	 * @param dayIndex
	 * @return
	 */
	public static Date parseDateFromDayIndex(int dayIndex)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(0); //转成格林威治时间
		calendar.add(Calendar.DATE, dayIndex);
//		Calendar c = Calendar.getInstance(TimeZone.getDefault());
//		TimeZone tz = c.getTimeZone();
//		int hours = (c.get(Calendar.DST_OFFSET)/ (60 * 60 * 1000));
//		calendar.add(Calendar.HOUR, hours);
		return calendar.getTime();
	}

	/**
	 * 获取指定时间对应的小时索引
	 * 
	 * @param ms
	 *            绝对毫秒值
	 * @return
	 */
	public static int getHourIndexFromTime(long ms)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(ms);
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取指定时间所对应天的30秒时间片个数
	 * 
	 * @param ms
	 *            绝对毫秒值
	 * @return
	 */
	public static int getSeconds30CountFromTime(long ms)
	{
		return (int) (((ms + TimeZone.getDefault().getRawOffset()) % (24 * 3600 * 1000)) / (30 * 1000));
	}

	/**
	 * 根据相对于1970年的天数换算出绝对毫秒值+1秒<br>
	 * 备注：仅用于用户数据云同步
	 * 
	 * @param dayIndex
	 * @return
	 */
	public static long getBelongDayFromDayIndex(int dayIndex)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(-TimeZone.getDefault().getRawOffset());
		calendar.add(Calendar.DATE, dayIndex);
		calendar.add(Calendar.SECOND, 1);
		return calendar.getTimeInMillis();
	}

	/**
	 * 根据数据所属的日期毫秒值换算出相对于1970年的天数<br>
	 * 备注：仅用于用户数据云同步
	 * 
	 * @param belongDay
	 * @return
	 */
	public static int getDayIndexFromBelongDay(long belongDay)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(-TimeZone.getDefault().getRawOffset());
		long baseTime = calendar.getTimeInMillis();
		int dayIndex = (int) ((belongDay - baseTime) / Constant.ONE_DAY_MILLIS);
		return dayIndex;
	}

	/**
	 * 根据小时数和分钟数计算出30秒时间片段的个数
	 * 
	 * @param hour
	 * @param mins
	 * @return
	 */
	public static int getSeconds30FromHourAndMinute(int hour, int mins)
	{
		return hour * 120 + mins * 2;
	}

	/**
	 * 将30秒时间片段的个数转换成绝对毫秒值
	 * 
	 * @param timeCount
	 * @return
	 */
	public static long getTimeFromSeconds30Count(int seconds30Count)
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.SECOND, seconds30Count * 30);
		return c.getTimeInMillis();
	}

	/**
	 * 获取本周的日期<br>
	 * 
	 * @return <Key:[1-7],Value:Date>
	 */
	public static Map<Integer, Date> getThisWeekDates()
	{
		Map<Integer, Date> datesMap = new HashMap<Integer, Date>();
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);// 周日1,周一2...
		// 转换为以周一为周第一天
		if (day == 1)
		{
			day = 7;
		}
		else
		{
			day -= 1;
		}
		Date currentDate = new Date();
		for (int i = 1; i <= 7; i++)
		{
			Date date = afterDate(currentDate, i - day);
			datesMap.put(i, date);
		}
		return datesMap;
	}

	/**
	 * 获取当前时间对应的时间片（时间片为30秒/个）
	 * 
	 * @return
	 */
	public static int getTicksOfDay()
	{
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return hour * 60 * 2 + minute * 2;
	}

	/**
	 * 获取图表时间片对应秒数（时间片为30秒/个）
	 * 
	 * @param ticks
	 * @return
	 */
	public static int parseSecFromTicks(int ticks)
	{
		return ticks / 2;
	}

}
