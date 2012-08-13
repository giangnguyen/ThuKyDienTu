package myapp.thukydientu.util;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import myapp.thukydientu.model.MyTime;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

public class TimeUtils {

	public static MyTime toTime(String timeModel) {
		String[] tmp;
		int hour, minute;
		tmp = timeModel.split(":");
		if (tmp.length == 2)
		{
			try 
			{
				hour = Integer.parseInt(tmp[0]);
				minute = Integer.parseInt(tmp[1]);
				return new MyTime(hour, minute);
			}
			catch (NumberFormatException e)
			{
				Log.d("MyTime", "wrong time format: hour and minute must be number");
				return new MyTime(0, 0);
			}
		}		
		Log.d("MyTime", "wrong time format: time must be in format \"hh:mm\"");
		return new MyTime(0, 0);
	}
	
	public static String toString(int hour, int minute) {
		return toString(hour, minute, 0);
	}
	
	public static String toString(int hour, int minute, int second) {
		return (((hour < 10) ? "0" + String.valueOf(hour) : String.valueOf(hour)) + 
				((minute < 10) ? "0" + String.valueOf(minute) : String.valueOf(minute)) + 
				((second < 10) ? "0" + String.valueOf(second) : String.valueOf(second)));
	}
	
	public static String toDisplayString(int hour, int minute) {
		return toDisplayString(hour, minute, 0);
	}
	
	public static String toDisplayString(int hour, int minute, int second) {
		return (((hour < 10) ? "0" + String.valueOf(hour) : String.valueOf(hour)) + ":" +
				((minute < 10) ? "0" + String.valueOf(minute) : String.valueOf(minute)) + ":" +
				((second < 10) ? "0" + String.valueOf(second) : String.valueOf(second)));
	}
	
	public static int getSecond(String time) throws IndexOutOfBoundsException {
		return Integer.parseInt(time.substring(4));
	}
	
	public static int getMinute(String time) throws IndexOutOfBoundsException {
		return Integer.parseInt(time.substring(2, 4));
	}
	
	public static int getHour(String time) throws IndexOutOfBoundsException {
		return Integer.parseInt(time.substring(0, 2));
	}
	
	public static int getDayOfMonth(String time) throws IndexOutOfBoundsException {
		return Integer.parseInt(time.substring(6));
	}
	
	public static String getDate(long timeInMilisecond) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		final String tmp = sdf.format(new Date(timeInMilisecond));
		return tmp;
	}
	
	/// this function will convert timeDatabase to time display
	/// @param timeDB hhmmss 
	/// @result return is time display hh:mm:ss
	public static String showTime(String timeDB) {
		final int hour = getHour(timeDB);
		final int minute = getMinute(timeDB);
		final int second = getSecond(timeDB);
		return toDisplayString(hour, minute, second);
	}
	public static long getTimeInMilisecond(String timeString) {
		return new Time(getHour(timeString), getMinute(timeString), getSecond(timeString)).getTime();
	}
	
	public static String getTime(long timeInMilisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		final String time = sdf.format(new Date(timeInMilisecond));
		return time;
	}
	
	public static String convert2String14(long timeInMilisecond) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date(timeInMilisecond));
	}
	
	public static long toTimeInMilisecond(String date, String time) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, getDayOfMonth(date));
		cal.set(Calendar.MONTH, getMonth(date) - 1);
		cal.set(Calendar.YEAR, getYear(date));
		cal.set(Calendar.HOUR_OF_DAY, getHour(time));
		cal.set(Calendar.MINUTE, getMinute(time));
		cal.set(Calendar.SECOND, getSecond(time));
		final long temp = cal.getTimeInMillis() / 1000;
		return temp * 1000;
	}
	public static int getMonth(String time) throws IndexOutOfBoundsException {
		return Integer.parseInt(time.substring(4, 6));
	}
	
	public static int getYear(String time) throws IndexOutOfBoundsException {
		return Integer.parseInt(time.substring(0, 4));
	}
	
	public static String getTimeLable(Context context, long time) {
		return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_TIME);
	}
	
	public static String getDateLable(Context context, long time) {
		return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_YEAR);
	}
	
	public static String getDateName(int dateValue) {
		switch (dateValue) {
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
