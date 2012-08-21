package myapp.thukydientu.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.provider.TKDTProvider;
import myapp.thukydientu.view.MainActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class ScheduleUtils {
	public static final int RESULT_FAIL = -1;
	public static final int DO_NOTHING = 0;
	public static final int RESULT_SUCCESS = 1;
	public static final int REQUEST_TO_UPDATE = 2;
	
	public static final int RESULT_NOT_EXISTED = -1;
	
	public static String queryString_SelectUndeletedSchedule(Schedule schedule) {
		return
				ScheduleTable.USER_ID + "=" + MainActivity.sUserId 
				+ " AND " +
				ScheduleTable.DAY_NAME + "='" + schedule.getDayName()
				+ "' AND " +
				ScheduleTable.TIME + "='" + schedule.getTime()
				+ "' AND " + 
				ScheduleTable.DELETED + "=" + 0
				;
	}
	
	public static String queryString_SelectSchedule(Schedule schedule) {
		return
				ScheduleTable.USER_ID + "=" + MainActivity.sUserId 
				+ " AND " +
				ScheduleTable.DAY_NAME + "='" + schedule.getDayName()
				+ "' AND " +
				ScheduleTable.TIME + "='" + schedule.getTime() + "'"
			;
	}
	
	public static String queryString_Changed() {
		return 
				ScheduleTable.USER_ID + "=" + MainActivity.sUserId
					+ " AND " +
				ScheduleTable.CHANGED + "=" + 1
			;
	}
	
	public static String queryString_All() {
		return 
				ScheduleTable.USER_ID + "=" + MainActivity.sUserId
				+ " AND " +
				ScheduleTable.DELETED + "=" + 0
			;
	}
	
	public static String queryString_AllByDateName(int dayName) {
		return 
				ScheduleTable.USER_ID + "=" + MainActivity.sUserId
					+ " AND " +
				ScheduleTable.DAY_NAME + "=" + dayName
					+ " AND " +
				ScheduleTable.DELETED + "=" + 0
			;
	}

	public static boolean isExisted(Context context, Schedule schedule) {
		
		boolean result = false;
		
		final Cursor cursor = context.getContentResolver()
								.query(
										TKDTProvider.SCHEDULE_CONTENT_URI, 
										ScheduleTable.PROJECTION, 
										queryString_SelectUndeletedSchedule(schedule), 
										null, 
										null
								);
		
		result = cursor.moveToFirst();
		
		closeCursor(cursor);
		
		return result;
	}
	
	public static int insert(Context context, Schedule schedule) {
		if (isExisted(context, schedule)) {
			return REQUEST_TO_UPDATE;
		} else {
			
			final String DateTimeString = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
			if (TextUtils.isEmpty(schedule.getDateSet()))
				schedule.setDateSet(DateTimeString);
			if (TextUtils.isEmpty(schedule.getModified()))
				schedule.setModified(DateTimeString);
			
			ContentValues values = createContentValues(schedule);
			Log.d("insert", "modified: " + values.getAsString(ScheduleTable.MODIFIED));
			final Uri uri = context.getContentResolver().insert(TKDTProvider.SCHEDULE_CONTENT_URI, values);
			if (uri == null) {
				realDelete(context, schedule);
				return RESULT_FAIL;
			} else 
				return RESULT_SUCCESS;
		}
	}
	
	public static void realDelete(Context context, Schedule schedule) {
		
		if (schedule == null) { 
			context.getContentResolver()
					.delete(
							TKDTProvider.SCHEDULE_CONTENT_URI, 
							null, 
							null
					);
		} else {
			context.getContentResolver()
					.delete(
							TKDTProvider.SCHEDULE_CONTENT_URI, 
							queryString_SelectSchedule(schedule), 
							null
					);
		}
		
	}
	
	public static void delete(Context context, Schedule schedule) {

		ContentValues values = new ContentValues();
		values.put(ScheduleTable.CHANGED, 1);
		values.put(ScheduleTable.DELETED, 1);
		values.put(ScheduleTable.MODIFIED, TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance()));
		
		if (schedule == null) { 
			context.getContentResolver()
				.update(
					TKDTProvider.SCHEDULE_CONTENT_URI, 
					values, 
					null, 
					null
				);
		} else {
			context.getContentResolver()
			.update(
				TKDTProvider.SCHEDULE_CONTENT_URI, 
				values, 
				queryString_SelectUndeletedSchedule(schedule), 
				null
			);
		}
		
	}
	
	public static int update(Context context, Schedule schedule) {
		if (schedule == null)
			return RESULT_FAIL;

		if (!isExisted(context, schedule))
			return RESULT_FAIL;
		
		// initial values to update
		ContentValues values = createContentValues(schedule);
		
		final int updateResult = context.getContentResolver()
										.update(
												TKDTProvider.SCHEDULE_CONTENT_URI, 
												values, 
												queryString_SelectUndeletedSchedule(schedule), 
												null
										);
		if (updateResult < 0)
			return RESULT_FAIL;
		else 
			return RESULT_SUCCESS;
	}
	
	public static void deleteAll(Context context) {
		delete(context, null);
	}
	
	private static ContentValues createContentValues(Schedule schedule) {
		// initial values to update
		ContentValues values = new ContentValues();
		
		values.put(ScheduleTable.USER_ID, 	MainActivity.sUserId);
		values.put(ScheduleTable.DAY_NAME, 	schedule.getDayName());
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

		if (!TextUtils.isEmpty(schedule.getDateSet()))
			values.put(ScheduleTable.DATE_SET, schedule.getDateSet());
		if (!TextUtils.isEmpty(schedule.getModified()))	
			values.put(ScheduleTable.MODIFIED, schedule.getModified());
		
		values.put(ScheduleTable.CHANGED, schedule.getChanged());
		values.put(ScheduleTable.DELETED, schedule.getDeleted());

		return values;
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
	}
	
	public static void bindScheduleData(Schedule schedule, Cursor cursor) {
		schedule.setId(cursor.getLong(ScheduleTable.ID_COLUMN_INDEX));
		schedule.setUserId(cursor.getInt(ScheduleTable.USER_ID_COLUMN_INDEX));
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
	
	public static List<Schedule> getAll(Context context) {
		List<Schedule> lists = new ArrayList<Schedule>();
		
		final Cursor cursor = context.getContentResolver()
								.query(
									TKDTProvider.SCHEDULE_CONTENT_URI, 
									ScheduleTable.PROJECTION, 
									queryString_All(), 
									null, 
									null
								);
		
		if (cursor.moveToFirst()) {
			do {
				Schedule schedule = new Schedule();
				bindScheduleData(schedule, cursor);
				lists.add(schedule);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		
		return lists;
	}
	
	public static List<Schedule> getAllChanged(Context context) {
		List<Schedule> listChanged = new ArrayList<Schedule>();
		
		final Cursor cursor = context.getContentResolver()
								.query(
									TKDTProvider.SCHEDULE_CONTENT_URI, 
									ScheduleTable.PROJECTION, 
									queryString_Changed(), 
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
	
	public static List<Schedule> getAllByDayName(Context context, int dayName) {

		List<Schedule> listChanged = new ArrayList<Schedule>();
		
		final Cursor cursor = context.getContentResolver()
									.query(
										TKDTProvider.SCHEDULE_CONTENT_URI, 
										ScheduleTable.PROJECTION, 
										queryString_AllByDateName(dayName), 
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
	
	public static Schedule getLocalSchedule(Context context, Schedule serverSchedule) {
		
		Schedule schedule = null;
		
		final Cursor cursor = context.getContentResolver()
								.query(
										TKDTProvider.SCHEDULE_CONTENT_URI, 
										ScheduleTable.PROJECTION, 
										queryString_SelectUndeletedSchedule(serverSchedule), 
										null, 
										null
								);
		if (cursor.moveToFirst()) {
			schedule = new Schedule();
			bindScheduleData(schedule, cursor);
		}

		closeCursor(cursor);
		
		return schedule;
			
	}
	
	public static void sync(int userId, Context context) {
		// sync from server
		final String result = WebservicesUtils.sync(userId, ScheduleTable.TABLE_NAME);
		Log.d("sync Schedule", "result from server: " + result);
		if (!TextUtils.isEmpty(result)) {
			List<Schedule> listScheduleChangedFromServer = XMLUtils.getSchedule(result);
			for (Schedule schedule : listScheduleChangedFromServer) {
				syncEachInstance(context, schedule);
			}
		}
		
		// sync to server
		List<Schedule> listChanged = getAllChanged(context);
		for (Schedule schedule : listChanged) {
			final String sync_app = WebservicesUtils.sync_schedule_app(userId, schedule);
			Log.d("sync", "sync_app result: " + sync_app);
			if (sync_app.equals("1")) { 
				if (schedule.getDeleted() == 1){
					realDelete(context, schedule);
					Log.d("sync", "sync_app result: " + sync_app);
				}else {
					schedule.setChanged(0);
					update(context, schedule);
				}
			}
		}
	}
	
	public static void syncEachInstance(Context context, Schedule serverSchedule) {
		if (isExisted(context, serverSchedule)) {
			Schedule localSchedule = getLocalSchedule(context, serverSchedule);
			Log.d("syncSchedule", "server date modified: " + serverSchedule.getModified());
			if (localSchedule.getModified().compareTo(serverSchedule.getModified()) < 0) {
				if (serverSchedule.getDeleted() == 1) { 
					realDelete(context, localSchedule);
				} else {
					serverSchedule.setChanged(0);
					update(context, serverSchedule);
				}
			} 
		} else {
			if (serverSchedule.getDeleted() == 0) {
				serverSchedule.setChanged(0);
				insert(context, serverSchedule);
			}
		}
	}

}
