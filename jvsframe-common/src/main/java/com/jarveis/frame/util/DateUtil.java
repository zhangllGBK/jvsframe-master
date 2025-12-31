package com.jarveis.frame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 日期工具类
 * 
 * @author liuguojun
 */
public class DateUtil {

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	// 默认的一个天为24小时,以秒为基数
	public static final long DAY_SECOND = 86400L;

	// 默认的一个月为30天,以秒为基数
	public static final long MONTH_SECOND = 2592000L;

	// 默认的一个年为365天,以秒为基数
	public static final long YEAR_SECOND = 946080000L;

	// 默认的一个天为24小时,以毫秒为基数
	public static final long DAY_MILLISECOND = 86400000L;

	// 默认的一个月为30天,以秒为基数
	public static final long MONTH_MILLISECOND = 2592000000L;

	// 默认的一个年为365天,以秒为基数
	public static final long YEAR_MILLISECOND = 946080000000L;

	// 有符号的年月格式
	public static final String FORMAT_YM = "yyyy-MM";

	// 有符号的年月日格式
	public static final String FORMAT_YMD = "yyyy-MM-dd";

	// 有符号的年月日时分秒格式
	public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

	// 无符号的年月日时分秒格式
	public static final String FORMAT_YMDHMSS = "yyyyMMddHHmmssSSS";

	/**
	 * 获取当前日期
	 * 
	 * <pre>
	 * String dateStr = DateUtil.getDateStr();
	 * ......
	 * </pre>
	 * 
	 * @return 格式化后的当前日期字符串
	 */
	public static String getDateStr() {
		return getDateStr(getDate(), "yyyy-MM-dd");
	}

	/**
	 * 获取指定日期并返回指定格式的字符串
	 * 
	 * <pre>
	 * String dateStr = DateUtil.getDateStr(DateUtil.getDate(), "yyyy/MM/dd");
	 * ......
	 * </pre>
	 * 
	 * @param date
	 *            日期
	 * @param pattern
	 *            返回的日期字符串格式
	 * @return 格式化后的日期字符串
	 */
	public static String getDateStr(Date date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return dateFormat.format(calendar.getTime());
	}

	/**
	 * 获取当前日期
	 * 
	 * <pre>
	 * Date date = DateUtil.getDate();
	 * ......
	 * </pre>
	 * 
	 * @return 当前日期
	 */
	public static Date getDate() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	/**
	 * 获取给定的时间戳（毫秒）的日期对象
	 *
	 * @param time ms
	 * @return
	 */
	public static Date getDate(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return calendar.getTime();
	}

	/**
	 * 获取给定字符串的日期对象
	 * <pre>
	 *     Date date1 = DateUtil.getDate("2011-01-01");
	 *     Date date2 = DateUtil.getDate("2011-01-01 10:00:00");
	 * </pre>
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date getDate(String dateStr) {
		Date date = null;
		String math1 = "\\d{1,4}-\\d{1,2}-\\d{1,2}";
		String math2 = "\\d{1,4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}";
		SimpleDateFormat format = null;
		if (dateStr.matches(math1)) {
			date = getDate(dateStr, FORMAT_YMD);
		} else if (dateStr.matches(math2)) {
			date = getDate(dateStr, FORMAT_YMDHMS);
		}
		return date;
	}

	/**
	 * 获取给定字符串的日期对象
	 * 
	 * <pre>
	 * Date date = DateUtil.getDate("2011/01/01", "yyyy/MM/dd");
	 * ......
	 * </pre>
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param pattern
	 *            日期格式
	 * @return 日期
	 * @throws ParseException
	 */
	public static Date getDate(String dateStr, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat();
		dateFormat.applyPattern(pattern);
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException ex) {
			log.error(ex.getMessage(), ex);
		}

		return date;
	}

	/**
	 * 以当前日期为参考，并获取当前日期之前或之后的日期
	 * 
	 * @param dt
	 * @param num
	 * @return
	 */
	public static Date getLast(final int dt, int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(dt, num);
		return calendar.getTime();
	}

	/**
	 * 以指定日期为参考，并获取指定日期之前或之后的日期
	 * 
	 * <pre>
	 * Date lastMonth = DateUtil.getLast(DateUtil.getDate(), Calendar.MONTH, 1);
	 * </pre>
	 * 
	 * @param date
	 * @param dt
	 * @param num
	 * @return String
	 */
	public static Date getLast(Date date, final int dt, int num) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(dt, num);
		return calendar.getTime();
	}

	/**
	 * 把java.util.Date转化为java.sql.Date
	 * 
	 * @param date
	 * @return java.sql.Date
	 */
	public static java.sql.Date convert(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}

	/**
	 * 把java.sql.Date转化为java.util.Date
	 * 
	 * @param date
	 * @return java.util.Date
	 */
	public static java.util.Date convert(java.sql.Date date) {
		return new java.util.Date(date.getTime());
	}

	/**
	 * 获取当前的年
	 * 
	 * <pre>
	 * int year = DateUtil.getYear();
	 * ......
	 * </pre>
	 * 
	 * @return int
	 */
	public static int getYear() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取给定日期的年
	 * 
	 * <pre>
	 * int year = DateUtil.getYear(DateUtil.getDate());
	 * ......
	 * </pre>
	 * 
	 * @param date
	 * @return int
	 */
	public static int getYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * 比较两个日期的大小
	 * 
	 * <pre>
	 * DateUtil.compare(&quot;2010-12-31&quot;, &quot;2011-01-01&quot;) = -1;
	 * DateUtil.compare(&quot;2011-01-01&quot;, &quot;2011-01-01&quot;) = 0;
	 * DateUtil.compare(&quot;2011-01-02&quot;, &quot;2011-01-01&quot;) = 1;
	 * </pre>
	 * 
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return 日期大小，日期1>日期2=1,日期1=日期2=0,日期1<日期2=-1
	 */
	public static int compare(Date date1, Date date2) {
		return date1.compareTo(date2);
	}

	/**
	 * 验证当前所在的年份是否为闰年
	 * 
	 * @return 是否为闰年（true-是，false-否）
	 */
	public static boolean isLeapYear() {
		return isLeapYear(getYear());
	}

	/**
	 * 验证指定的年份是否为闰年
	 * 
	 * @param year 年
	 * @return 是否为闰年（true-是，false-否）
	 */
	public static boolean isLeapYear(int year) {
		if (year % 100 == 0) {
			if (year % 400 == 0) {
				return true;
			} else {
				return false;
			}
		} else if (year % 4 == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取指定月份有多少天
	 * 
	 * <pre>
	 * int days = DateUtil.getMonthDays(1);
	 * ......
	 * </pre>
	 * 
	 * @param month 月份
	 * @return 月份的天数
	 */
	public static int getMonthDays(int month) {
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			return 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		} else {
			if (isLeapYear()) {
				return 29;
			} else {
				return 28;
			}
		}
	}

	/**
	 * 获取当前月份有多少天
	 * 
	 * <pre>
	 * int days = DateUtil.getMonthDays();
	 * ......
	 * </pre>
	 * 
	 * @return 当前月的天数
	 */
	public static int getMonthDays() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		return getMonthDays(month);
	}

	/**
	 * 从字符串中查找出日期
	 * 
	 * @param str 输入字符串
	 * @return 字符串中的日期字符串
	 */
	public static String seekDate(String str) {
		String dateStr = "";

		String[] strs = find(str, "\\d+\\-\\d+\\-\\d+");
		if (strs.length > 0) {
			dateStr = getDateStr(getDate(strs[0], "yyyy-MM-dd"), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "\\d+\\-\\d+");
		if (strs.length > 0) {
			dateStr = getYear() + "-" + strs[0];
			dateStr = getDateStr(getDate(dateStr, "yyyy-MM-dd"), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "\\d+/\\d+/\\d+");
		if (strs.length > 0) {
			dateStr = getDateStr(getDate(strs[0], "yyyy/MM/dd"), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "\\d+/\\d+");
		if (strs.length > 0) {
			dateStr = getYear() + "/" + strs[0];
			dateStr = getDateStr(getDate(dateStr, "yyyy/MM/dd"), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "\\d+年\\d+月\\d+日");
		if (strs.length > 0) {
			dateStr = getDateStr(getDate(strs[0], "yyyy年MM月dd日"), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "\\d+月\\d+日");
		if (strs.length > 0) {
			dateStr = getYear() + "年" + strs[0];
			dateStr = getDateStr(getDate(dateStr, "yyyy年MM月dd日"), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "\\d+\\s?分钟前");
		if (strs.length > 0) {
			dateStr = getDateStr(getDate(), "yyyyMMdd");
			return dateStr;
		}
		
		strs = find(str, "\\d+\\s?小时前");
		if (strs.length > 0) {
			dateStr = getDateStr(getDate(), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "今天\\s?\\d+");
		if (strs.length > 0) {
			dateStr = getDateStr(getDate(), "yyyyMMdd");
			return dateStr;
		}

		strs = find(str, "昨天\\s?\\d+");
		if (strs.length > 0) {
			dateStr = getDateStr(getLast(Calendar.DATE, -1), "yyyyMMdd");
			return dateStr;
		}
		
		strs = find(str, "前天\\s?\\d+");
		if (strs.length > 0) {
			dateStr = getDateStr(getLast(Calendar.DATE, -2), "yyyyMMdd");
			return dateStr;
		}

		return dateStr;
	}
	
	/**
	 * 在内容中查找与给定的正则表达式相匹配的字符串
	 * 
	 * <pre>
	 * StringUtil.find("北风2-3级", "\\d+") = {"2", "3"};
	 * </pre>
	 * 
	 * @param content
	 *            要查找内容
	 * @param regex
	 *            正则表达式
	 * @return String[]
	 */
	public static String[] find(String content, String regex) {
		ArrayList<String> array = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			array.add(matcher.group());
		}
		array.trimToSize();

		return array.toArray(new String[array.size()]);
	}

	public static void main(String[] args) {
//		dateStr = seekDate("张学元 6-1 11:18 没有评论");
//		dateStr = seekDate("张学元 06-02 11:18 没有评论");
//		dateStr = seekDate("2014-6-3, 11:13 | 黄思俊");
//		dateStr = seekDate("张学元 2014-06-04 11:18 没有评论");

//		dateStr = seekDate("张学元 6/1 11:18 没有评论");
//		dateStr = seekDate("张学元 06/02 11:18 没有评论");
//		dateStr = seekDate("2014/6/3, 11:13 | 黄思俊");
//		dateStr = seekDate("张学元 2014/06/04 11:18 没有评论");

//		dateStr = seekDate("张学元 6月1日 11:18 没有评论");
//		dateStr = seekDate("张学元 06月02日 11:18 没有评论");
//		dateStr = seekDate("2014年6月3日, 11:13 | 黄思俊");
//		dateStr = seekDate("张学元 2014年06月04日 11:18 没有评论");

//		dateStr = seekDate("22分钟前");
//		dateStr = seekDate("22小时前");
//		dateStr = seekDate("今天 14:00");
//		dateStr = seekDate("昨天15:08");
//		dateStr = seekDate("前天15:08");

		log.info(getDateStr(getDate("2010-01-01 02:03:14"), FORMAT_YMDHMS));
		log.info(getDateStr(getDate(1321564943100L), FORMAT_YMDHMS));
	}
}
