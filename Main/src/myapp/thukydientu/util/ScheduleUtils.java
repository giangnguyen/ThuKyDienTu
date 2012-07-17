package myapp.thukydientu.util;

import java.util.ArrayList;
import java.util.List;

import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.provider.TKDTProvider;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ScheduleUtils {
	public static final int FAIL = -1;
	public static final int DO_NOTHING = 0;
	public static final int SUCCESS = 1;
	public static final int REQUEST_TO_UPDATE = 2;
	
	public static int insert(Activity activity, Schedule schedule) {
		final long Id = getId(activity, schedule.getDayName(), schedule.getTime());
		
		if (Id == -1 || checkDeleted(activity, Id)) {
			final long time = System.currentTimeMillis();
			if (TextUtils.isEmpty(schedule.getDateSet()))
				schedule.setDateSet(time);
			if (TextUtils.isEmpty(schedule.getModified()))
				schedule.setModified(time);
			ContentValues values = createContentValues(schedule);
			Log.d("insert", "modified: " + values.getAsString(ScheduleTable.MODIFIED));
			final Uri uri = activity.getContentResolver().insert(TKDTProvider.SCHEDULE_CONTENT_URI, values);
			if (uri == null) 
				return FAIL;
			else 
				return SUCCESS;
		} else {
			return REQUEST_TO_UPDATE;
		}
	}
	
	public static int update(Activity activity, Schedule schedule, long Id) {
		if (schedule == null)
			return FAIL;
		// initial values to update
		if (TextUtils.isEmpty(schedule.getModified()))
			schedule.setModified(System.currentTimeMillis());
		schedule.setDateSet(getDateSet(activity, Id));
		final ContentValues values = createContentValues(schedule);
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		
		try {
			final int result = activity.getContentResolver().update(uriId, values, null, null);
			if (result > 0) 
				return SUCCESS;
			else 
				return FAIL;
		} catch (NullPointerException e) {
			return FAIL;
		}
	}
	
	public static int delete(Activity activity, long Id) {
		final ContentValues values = new ContentValues();
		values.put(ScheduleTable.DELETED, 1);
		values.put(ScheduleTable.CHANGED, 1);
		values.put(ScheduleTable.MODIFIED, TimeUtils.convert2String14(System.currentTimeMillis()));
		Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		if (Id == -1)
			uriId = TKDTProvider.SCHEDULE_CONTENT_URI;
		try {
			return activity.getContentResolver().update(uriId, values, null, null);
		} catch (NullPointerException e) {
			return FAIL;
		}
	}
	
	public static Cursor getListScheduleByDayOfWeek(Activity activity, int dayOfWeek) {
		Cursor cursor = activity.getContentResolver().query(TKDTProvider.SCHEDULE_CONTENT_URI, ScheduleTable.PROJECTION, ScheduleTable.DATE_NAME + "=" + dayOfWeek + " AND " + ScheduleTable.DELETED + "=0", null, null);
		activity.startManagingCursor(cursor);
		return cursor;
	}
	
	private static ContentValues createContentValues(Schedule schedule) {
		// initial values to update
		ContentValues values = new ContentValues();
		values.put(ScheduleTable.DATE_NAME, schedule.getDayName());
		values.put(ScheduleTable.TIME, schedule.getTime());
		values.put(ScheduleTable.DATE_SET, schedule.getDateSet());
		values.put(ScheduleTable.SUBJECT, schedule.getSubject());
		values.put(ScheduleTable.CLASS, schedule.getClassName());
		values.put(ScheduleTable.SCHOOL, schedule.getSchoolName());
		values.put(ScheduleTable.MODIFIED, schedule.getModified());
		values.put(ScheduleTable.CHANGED, schedule.getChanged());
		values.put(ScheduleTable.DELETED, schedule.getDeleted());
		values.put(ScheduleTable.LESSON, schedule.getLessons());
		values.put(ScheduleTable.LESSON_DURATION, schedule.getLessonDuration());

		return values;
	}
	
	public static long getId(Activity activity, int dateName, String time) {
		long Id = -1;
		Log.d("getId", "dateName: " + dateName + " time: " + time);
		final Cursor cursor = activity.getContentResolver().query(
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				ScheduleTable.PROJECTION, 
				ScheduleTable.DATE_NAME + "=" + dateName + " AND " + 
				ScheduleTable.TIME + "='" + time + "'", 
				null, 
				null);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				Id = cursor.getLong(ScheduleTable.ID_COLUMN_INDEX);
			} while (cursor.moveToNext());
		} 
		closeCursor(cursor);
		Log.d("getId", "return id: " + Id);
		return Id;
	}
	
	public static boolean checkDeleted(Activity activity, long id) {
		int deleted = 0;
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, id);
		final Cursor cursor = activity.getContentResolver().query(
				uriId, 
				ScheduleTable.PROJECTION, 
				null, 
				null, 
				null);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				deleted = cursor.getInt(ScheduleTable.DELETED_COLUMN_INDEX);
			} while (cursor.moveToNext());
		} 
		closeCursor(cursor);
		return deleted == 0 ? false : true;
	}
	
	public static String getDateSet(Activity activity, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		String dateSet = TimeUtils.convert2String14(System.currentTimeMillis());
		final Cursor cursor = activity.getContentResolver().query(
				uriId, 
				ScheduleTable.PROJECTION, 
				null, null, null);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				dateSet = cursor.getString(ScheduleTable.DATE_SET_COLUMN_INDEX);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return dateSet;
	}
	
	public static Schedule getSchedule(Activity activity, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		final Cursor cursor = activity.managedQuery(uriId, ScheduleTable.PROJECTION, null, null, null);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			Schedule schedule = new Schedule();
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
			closeCursor(cursor);
			return schedule;
		}
		closeCursor(cursor);
		return null;
	}
	
	public static List<Schedule> getListScheduleChanged(Activity activity) {
		List<Schedule> listChanged = new ArrayList<Schedule>();
		final Cursor cursor = activity.getContentResolver().query(
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				ScheduleTable.PROJECTION, 
				ScheduleTable.CHANGED + "=1", 
				null, 
				null
				);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				listChanged.add(getSchedule(activity, cursor.getLong(ScheduleTable.ID_COLUMN_INDEX)));
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return listChanged;
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
	}
	
	public static void sync(int userId, Activity activity) {
		final String result = WebservicesUtils.sync(userId, ScheduleTable.TABLE_NAME);
		Log.d("sync Schedule", "result from server: " + result);
		if (!TextUtils.isEmpty(result)) {
			List<Schedule> listScheduleChangedFromServer = XMLUtils.getSchedule(result);
			for (Schedule schedule : listScheduleChangedFromServer) {
				syncEachInstance(activity, schedule);
			}
		}
		
		List<Schedule> listChanged = getListScheduleChanged(activity);
		for (Schedule schedule : listChanged) {
			final String sync_app_Schedule = WebservicesUtils.sync_schedule_app(userId, schedule);
			Log.d("sync", "sync_app_schedule result: " + sync_app_Schedule);
			if (sync_app_Schedule.equals("1")) { 
				final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, schedule.getId());
				if (schedule.getDeleted() == 1)
					activity.getContentResolver().delete(uriId, null, null);
				else {
					ContentValues values = new ContentValues();
					values.put(ScheduleTable.CHANGED, 0);
					activity.getContentResolver().update(uriId, values, null, null);
				}
			}
		}
	}
	
	public static void syncEachInstance(Activity activity, Schedule schedule) {
		final long Id = getId(activity, schedule.getDayName(), schedule.getTime());
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		if (Id > 0) {
			Schedule localSchedule = getSchedule(activity, Id);
			if (localSchedule.getModified().compareTo(schedule.getModified()) < 0) {
				if (schedule.getDeleted() == 1) {
					activity.getContentResolver().delete(uriId, null, null);
					Log.d("sync from server", "deleted");
				} else {
					schedule.setChanged(0);
					update(activity, schedule, Id);
				}
			} 
		} else {
			if (schedule.getDeleted() == 0) {
				Log.d("sync from server", "pre insert: modified: " + schedule.getModified());
				schedule.setChanged(0);
				insert(activity, schedule);
			}
		}
	}
	
}
