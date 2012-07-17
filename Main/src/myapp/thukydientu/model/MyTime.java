package myapp.thukydientu.model;

import android.util.Log;

public class MyTime {
	private int mHour;
	private int mMinute;

	public MyTime() {
		new MyTime(0, 0);
	}

	public MyTime(int hour, int minute) {
		setTime(hour, minute);
	}

	public MyTime(String timeModel)
	{
		setTime(timeModel);
	}
	// --- Define some setter

	public void setHour(int hour) {
		// neu set gio < 0 hoac > 24 thi set gia tri mac dinh la 0.
		this.mHour = (hour >= 0 && hour < 24) ? hour : 0;
	}

	public void setMinute(int minute) {
		// neu set phut < 0 hoac >= 60 thi gan gia tri mac dinh la 0.
		this.mMinute = (minute >= 0 && minute < 60) ? minute : 0;
	}

	public void setTime(int hour, int minute) {
		setHour(hour);
		setMinute(minute);
	}

	public void setTime(String timeModel) throws NullPointerException {
		String[] tmp;
		tmp = timeModel.split(":");
		if (tmp.length == 2)
		{
			try 
			{
				setHour(Integer.parseInt(tmp[0]));
				setMinute(Integer.parseInt(tmp[1]));
				return;
			}
			catch (NumberFormatException e)
			{
				setHour(0);
				setMinute(0);
				Log.d("MyTime", "wrong time format: hour and minute must be number");
			}
		}
		setHour(0);
		setMinute(0);
		Log.d("MyTime", "wrong time format: time must be in format \"hh:mm\"");
	}

	// --- Define some getter

	public int getHour() {
		return this.mHour;
	}

	public int getMinute() {
		return this.mMinute;
	}

	public String toString() {
		return (((mHour < 10) ? "0" + String.valueOf(mHour) : String
				.valueOf(mHour)) + ":" + ((mMinute < 10) ? "0"
				+ String.valueOf(mMinute) : String.valueOf(mMinute)));
	}

	public String toSpecialString() {
		return (((mHour < 10) ? "0" + String.valueOf(mHour) : String
				.valueOf(mHour)) + ((mMinute < 10) ? "0"
				+ String.valueOf(mMinute) : String.valueOf(mMinute)));
	}

}
