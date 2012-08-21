package myapp.thukydientu.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.text.format.DateUtils;

public class TaleTimeUtils {
	
	final static String FORMAT_DATE = "yyyyMMdd";
	final static String FORMAT_TIME = "HHmm";
	final static String FORMAT_DATE_TIME = FORMAT_DATE + FORMAT_TIME + "ss";
	final static String FORMAT_SHOW_DATE = "dd/MM/yyyy";
	
	// return int @Minute by @TimeString (FORMAT_TIME)
	public static int getMinute(String timeString) throws IndexOutOfBoundsException {
		return Integer.parseInt(timeString.substring(2, 4));
	}
	
	// return int @Hour by @TimeString (FORMAT_TIME)
	public static int getHour(String timeString) throws IndexOutOfBoundsException {
		return Integer.parseInt(timeString.substring(0, 2));
	}
	
	// return int @DayOfMonth by @DateString (FORMAT_DATE)
	public static int getDayOfMonth(String dateString) throws IndexOutOfBoundsException {
		return Integer.parseInt(dateString.substring(6));
	}
	
	// return int @Month by @DateString (FORMAT_DATE)
	public static int getMonth(String dateString) throws IndexOutOfBoundsException {
		return Integer.parseInt(dateString.substring(4, 6)) - 1;
	}
	
	// return int @Year by @DateString (FORMAT_DATE)
	public static int getYear(String dateString) throws IndexOutOfBoundsException {
		return Integer.parseInt(dateString.substring(0, 4));
	}
	
	// return @Calendar object by (Hour:Minute)
	public static Calendar createCalendar(int hour, int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		return calendar;
	}
	
	// return @Calendar object by (DayOfMonth : Hour : Minute)	
	public static Calendar createCalendar(int dayOfMonth, int hour, int minute) {
		Calendar calendar = createCalendar(hour, minute);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return calendar;
	}

	// return @Calendar object by (Month : DayOfMonth : Hour : Minute)	
	public static Calendar createCalendar(int month, int dayOfMonth, int hour, int minute) {
		Calendar calendar = createCalendar(dayOfMonth, hour, minute);
		calendar.set(Calendar.MONTH, month);
		return calendar;
	}
	
	// return @Calendar object by (Year : Month : DayOfMonth : Hour : Minute)	
	public static Calendar createCalendar(int year, int month, int dayOfMonth, int hour, int minute) {
		Calendar calendar = createCalendar(month, dayOfMonth, hour, minute);
		calendar.set(Calendar.YEAR, year);
		return calendar;
	}
	
	// return @Calendar object by @TimeString (FORMAT_TIME)
	public static Calendar createCalendarByTimeString(String timeString) {
		final int hour = getHour(timeString);
		final int minute = getMinute(timeString);
		return createCalendar(hour, minute);
	}
	
	// return @Calendar object by @DateString (FORMAT_DATE)
	public static Calendar createCalendarByDateString(String dateString) {
		Calendar calendar = Calendar.getInstance();

		final int year = getYear(dateString);
		final int month = getMonth(dateString);
		final int dayOfMonth = getDayOfMonth(dateString);
		
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		
		return calendar;
	}
	
	// return @Calendar object by @DateString (FORMAT_DATE) and @TimeString (FORMAT_TIME)
	public static Calendar createCalendarByDateTimeString(String dateString, String timeString) {
		final int year = getYear(dateString);
		final int month = getMonth(dateString);
		final int dayOfMonth = getDayOfMonth(dateString);
		final int hour = getHour(timeString);
		final int minute = getMinute(timeString);
		
		return createCalendar(year, month, dayOfMonth, hour, minute);
	}

	// return @TimeString (FORMAT_TIME) by @Calendar object
	public static String getTimeStringByCalendar(Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME);
		final String timeString = sdf.format(calendar.getTime()) + "00";
		return timeString;
	}
	
	// return @DateString (FORMAT_DATE) by @Calendar object
	public static String getDateStringByCalendar(Calendar calendar) {
		final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		final String dateString = sdf.format(calendar.getTime());
		return dateString;
	}
	
	// return @DateString@TimeString (FORMAT_DATE_TIME) by @Calendar object
	public static String getDateTimeStringByCalendar(Calendar calendar) {
		final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_TIME);
		final String dateString = sdf.format(calendar.getTime());
		return dateString;
	}
	
	// return display time format by @TimeMillis
	public static String getTimeLable(Context context, Calendar calendar) {
		return DateUtils.formatDateTime(context, calendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
	}
	
	// return display date format by @TimeMillis
	public static String getDateLable(Context context, Calendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_SHOW_DATE);
		final String dateDisplay = sdf.format(calendar.getTime());
		return dateDisplay;
	}
	
	// return display Day Of Week name by @DayOfWeek
	public static String getDayOfWeekString(int dayOfWeek) {
		switch (dayOfWeek) {
		case 1: 
			return "Chủ Nhật";
		case 2:
			return "Thứ Hai";
		case 3:
			return "Thứ Ba";
		case 4:
			return "Thứ Tư";
		case 5:
			return "Thứ Năm";
		case 6: 
			return "Thứ Sáu";
		case 7: 
			return "Thứ Bảy";
		default:
			return "Chủ Nhật";
		}
	}
}
