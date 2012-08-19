package myapp.thukydientu.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.provider.TKDTProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class CopyOfScheduleUtils {
	public static final int FAIL = -1;
	public static final int DO_NOTHING = 0;
	public static final int SUCCESS = 1;
	public static final int REQUEST_TO_UPDATE = 2;
	
	public static int insert(Context context, Schedule schedule) {
		final long Id = getId(context, schedule.getDayName(), schedule.getTime());
		
		if (Id == -1 || checkDeleted(context, Id)) {
			final long time = System.currentTimeMillis();
			if (TextUtils.isEmpty(schedule.getDateSet()))
				schedule.setDateSet(time);
			if (TextUtils.isEmpty(schedule.getModified()))
				schedule.setModified(time);
			ContentValues values = createContentValues(schedule);
			Log.d("insert", "modified: " + values.getAsString(ScheduleTable.MODIFIED));
			final Uri uri = context.getContentResolver().insert(TKDTProvider.SCHEDULE_CONTENT_URI, values);
			if (uri == null) 
				return FAIL;
			else 
				return SUCCESS;
		} else {
			return REQUEST_TO_UPDATE;
		}
	}
	
	public static int update(Context context, Schedule schedule, long Id) {
		if (schedule == null)
			return FAIL;
		// initial values to update
		if (TextUtils.isEmpty(schedule.getModified()))
			schedule.setModified(System.currentTimeMillis());
		schedule.setDateSet(getDateSet(context, Id));
		final ContentValues values = createContentValues(schedule);
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		
		try {
			final int result = context.getContentResolver().update(uriId, values, null, null);
			if (result > 0) 
				return SUCCESS;
			else 
				return FAIL;
		} catch (NullPointerException e) {
			return FAIL;
		}
	}
	
	public static int delete(Context context, long Id) {
		final ContentValues values = new ContentValues();
		values.put(ScheduleTable.DELETED, 1);
		values.put(ScheduleTable.CHANGED, 1);
		values.put(ScheduleTable.MODIFIED, TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance()));
		Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		if (Id == -1)
			uriId = TKDTProvider.SCHEDULE_CONTENT_URI;
		try {
			return context.getContentResolver().update(uriId, values, null, null);
		} catch (NullPointerException e) {
			return FAIL;
		}
	}
	
	public static Cursor getListScheduleByDayOfWeek(Context context, int dayOfWeek) {
		Cursor cursor = context.getContentResolver().query(TKDTProvider.SCHEDULE_CONTENT_URI, ScheduleTable.PROJECTION, ScheduleTable.DAY_NAME + "=" + dayOfWeek + " AND " + ScheduleTable.DELETED + "=0", null, null);
		return cursor;
	}
	
	private static ContentValues createContentValues(Schedule schedule) {
		// initial values to update
		ContentValues values = new ContentValues();
		values.put(ScheduleTable.USER_ID, 	schedule.getUserId());
		values.put(ScheduleTable.DAY_NAME, schedule.getDayName());
		values.put(ScheduleTable.TIME, 		schedule.getTime());
		values.put(ScheduleTable.DATE_SET, 	schedule.getDateSet());
		values.put(ScheduleTable.SUBJECT, 	schedule.getSubject());
		values.put(ScheduleTable.CLASS, 	schedule.getClassName());
		values.put(ScheduleTable.SCHOOL, 	schedule.getSchoolName());
		values.put(ScheduleTable.MODIFIED, 	schedule.getModified());
		values.put(ScheduleTable.CHANGED, 	schedule.getChanged());
		values.put(ScheduleTable.DELETED, 	schedule.getDeleted());
		values.put(ScheduleTable.LESSON, 	schedule.getLessons());
		values.put(ScheduleTable.LESSON_DURATION, schedule.getLessonDuration());

		return values;
	}
	
	public static long getId(Context context, int dateName, String time) {
		long Id = -1;
		Log.d("getId", "dateName: " + dateName + " time: " + time);
		final Cursor cursor = context.getContentResolver().query(
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				ScheduleTable.PROJECTION, 
				ScheduleTable.DAY_NAME + "=" + dateName + " AND " + 
				ScheduleTable.TIME + "='" + time + "'", 
				null, 
				null);
		if (cursor.moveToFirst()) {
			do {
				Id = cursor.getLong(ScheduleTable.ID_COLUMN_INDEX);
			} while (cursor.moveToNext());
		} 
		closeCursor(cursor);
		Log.d("getId", "return id: " + Id);
		return Id;
	}
	
	public static boolean checkDeleted(Context context, long id) {
		int deleted = 0;
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, id);
		final Cursor cursor = context.getContentResolver().query(
				uriId, 
				ScheduleTable.PROJECTION, 
				null, 
				null, 
				null);
		if (cursor.moveToFirst()) {
			do {
				deleted = cursor.getInt(ScheduleTable.DELETED_COLUMN_INDEX);
			} while (cursor.moveToNext());
		} 
		closeCursor(cursor);
		return deleted == 0 ? false : true;
	}
	
	public static String getDateSet(Context context, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		String dateSet = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
		final Cursor cursor = context.getContentResolver().query(
				uriId, 
				ScheduleTable.PROJECTION, 
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				dateSet = cursor.getString(ScheduleTable.DATE_SET_COLUMN_INDEX);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return dateSet;
	}
	
	public static void bindScheduleData(Schedule schedule, Cursor cursor) {
		schedule.setId(cursor.getLong(ScheduleTable.ID_COLUMN_INDEX));
		schedule.setDayName(cursor.getInt(ScheduleTable.DATE_NAME_COLUMN_INDEX));
		schedule.setTime(cursor.getString(ScheduleTable.TIME_COLUMN_INDEX));
		schedule.setSubject(cursor.getString(ScheduleTable.SUBJECT_COLUMN_INDEX));
		schedule.setClassName(cursor.getString(ScheduleTable.CLASS_COLUMN_INDEX));
		schedule.setSchoolName(cursor.getString(ScheduleTable.SCHOOL_COLUMN_INDEX));
		schedule.setDateSet(cursor.getString(ScheduleTable.DATE_SET_COLUMN_INDEX));
		schedule.setModified(cursor.getString(ScheduleTable.MODIFIED_COLUMN_INDEX));
		schedule.setChanged(cursor.getInt(ScheduleTable.CHANGE_COLUMN_INDEX));
		schedule.setDeleted(cursor.getInt(ScheduleTable.DELETED_COLUMN_INDEX));
		schedule.setLessons(cursor.getInt(ScheduleTable.LESSON_COLUMN_INDEX));
		schedule.setLessonDuration(cursor.getInt(ScheduleTable.LESSON_DURATION_COLUMN_INDEX));
	}
	
	public static Schedule getSchedule(Context context, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		final Cursor cursor = context.getContentResolver().query(uriId, ScheduleTable.PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			Schedule schedule = new Schedule();
			bindScheduleData(schedule, cursor);
			closeCursor(cursor);
			return schedule;
		}
		closeCursor(cursor);
		return null;
	}
	
	public static List<Schedule> getListScheduleChanged(Context context) {
		List<Schedule> listChanged = new ArrayList<Schedule>();
		final Cursor cursor = context.getContentResolver().query(
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				ScheduleTable.PROJECTION, 
				ScheduleTable.CHANGED + "=1", 
				null, 
				null
				);
		if (cursor.moveToFirst()) {
			do {
				Schedule schedule = new Schedule();
				bindScheduleData(schedule, cursor);
				listChanged.add(schedule);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return listChanged;
	}
	
	public static List<Schedule> getListScheduleByDay(Context context, int dayOfMonth) {
		List<Schedule> listSchedule = new ArrayList<Schedule>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		final Cursor cursor = context.getContentResolver().query(
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				ScheduleTable.PROJECTION, 
				ScheduleTable.DAY_NAME + "=?", 
				new String[]{String.valueOf(dayOfWeek)}, 
				null);
		if (cursor.moveToFirst()) {
			do {
				Schedule schedule = new Schedule();
				bindScheduleData(schedule, cursor);
				listSchedule.add(schedule);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return listSchedule;
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
	}
	
	public static void sync(int userId, Context context) {
		final String result = WebservicesUtils.sync(userId, ScheduleTable.TABLE_NAME);
		Log.d("sync Schedule", "result from server: " + result);
		if (!TextUtils.isEmpty(result)) {
			List<Schedule> listScheduleChangedFromServer = XMLUtils.getSchedule(result);
			for (Schedule schedule : listScheduleChangedFromServer) {
				syncEachInstance(context, schedule);
			}
		}
		
		List<Schedule> listChanged = getListScheduleChanged(context);
		for (Schedule schedule : listChanged) {
			final String sync_app_Schedule = WebservicesUtils.sync_schedule_app(userId, schedule);
			Log.d("sync", "sync_app_schedule result: " + sync_app_Schedule);
			if (sync_app_Schedule.equals("1")) { 
				final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, schedule.getId());
				if (schedule.getDeleted() == 1)
					context.getContentResolver().delete(uriId, null, null);
				else {
					ContentValues values = new ContentValues();
					values.put(ScheduleTable.CHANGED, 0);
					context.getContentResolver().update(uriId, values, null, null);
				}
			}
		}
	}
	
	public static void syncEachInstance(Context context, Schedule schedule) {
		final long Id = getId(context, schedule.getDayName(), schedule.getTime());
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		if (Id > 0) {
			Schedule localSchedule = getSchedule(context, Id);
			if (localSchedule.getModified().compareTo(schedule.getModified()) < 0) {
				if (schedule.getDeleted() == 1) {
					context.getContentResolver().delete(uriId, null, null);
					Log.d("sync from server", "deleted");
				} else {
					schedule.setChanged(0);
					update(context, schedule, Id);
				}
			} 
		} else {
			if (schedule.getDeleted() == 0) {
				Log.d("sync from server", "pre insert: modified: " + schedule.getModified());
				schedule.setChanged(0);
				insert(context, schedule);
			}
		}
	}
	
}
