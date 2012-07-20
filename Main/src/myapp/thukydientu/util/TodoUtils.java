package myapp.thukydientu.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import myapp.thukydientu.database.TodoTable;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.model.IConstants.event;
import myapp.thukydientu.provider.TKDTProvider;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class TodoUtils {
	public static final int FAIL = -1;
	public static final int DO_NOTHING = 0;
	public static final int SUCCESS = 1;
	public static final int REQUEST_TO_UPDATE = 2;
	
	public static int insert(Activity activity, Todo todo) {
		long Id = getId(activity, todo.getDateStart(), todo.getTimeFrom(), todo.getTimeUntil());
		if (Id == -1 || checkDeleted(activity, Id)) {
			final long time = System.currentTimeMillis();
			if (TextUtils.isEmpty(todo.getDateSet()))
				todo.setDateSet(time);
			if (TextUtils.isEmpty(todo.getModified()))
				todo.setModified(time);
			
			if (!addEvent(activity, todo)) 
				return FAIL;
			
			ContentValues values = createContentValues(todo);
			Log.d("insert", "modified: " + values.getAsString(TodoTable.MODIFIED));
			final Uri uri = activity.getContentResolver().insert(TKDTProvider.TODO_CONTENT_URI, values);
			if (uri == null) 
				return FAIL;
			else 
				return SUCCESS;
		} else {
			return REQUEST_TO_UPDATE;
		}
	}
	
	public static int update(Activity activity, Todo todo, long Id) {
		if (todo == null)
			return FAIL;
		
		if (!updateEvent(activity, Id, todo))
			return FAIL;
		
		// initial values to update
		if (TextUtils.isEmpty(todo.getModified()))
			todo.setModified(System.currentTimeMillis());
		
		if (TextUtils.isEmpty(todo.getDateSet()))
			todo.setDateSet(getDateSet(activity, Id));
		
		ContentValues values = createContentValues(todo);
		
		Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		
		if (Id == -1) 
			uriId = TKDTProvider.TODO_CONTENT_URI;
		
		
		final int updateResult = activity.getContentResolver().update(uriId, values, null, null);
		if (updateResult < 0)
			return FAIL;
		else 
			return SUCCESS;
	}
	
	public static String getDateSet(Activity activity, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		String dateSet = TimeUtils.convert2String14(System.currentTimeMillis());
		final Cursor cursor = activity.getContentResolver().query(
				uriId, 
				TodoTable.PROJECTION, 
				null, null, null);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				dateSet = cursor.getString(TodoTable.DATE_SET_COLUMN_INDEX);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return dateSet;
	}
	
	public static int delete(Activity activity, long eventId) {
		
		final long todoId = getTodoIdByEventId(activity, eventId);
		
		final int deleteEvent = deleteEvent(activity, eventId);
		
		if (deleteEvent > 0) {
			ContentValues values = new ContentValues();
			values.put(TodoTable.CHANGED, 1);
			values.put(TodoTable.DELETED, 1);
			values.put(TodoTable.MODIFIED, TimeUtils.convert2String14(System.currentTimeMillis()));
			
			if (todoId == -1) {
				return activity.getContentResolver().update(TKDTProvider.TODO_CONTENT_URI, values, null, null);
			}  else {
				Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, todoId);
				return activity.getContentResolver().update(uriId, values, null, null);
			}
		}
		return deleteEvent;
	}
	
	private static ContentValues createContentValues(Todo todo) {
		// initial values to update
		ContentValues values = new ContentValues();
		
		values.put(TodoTable.DATE_START, todo.getDateStart());
		values.put(TodoTable.DATE_END, todo.getDateEnd());
		values.put(TodoTable.TIME_FROM, todo.getTimeFrom());
		values.put(TodoTable.TIME_UNTIL, todo.getTimeUntil());
		values.put(TodoTable.TITLE, todo.getTitle());
		values.put(TodoTable.WORK, todo.getWork());
		values.put(TodoTable.ALARM, todo.getAlarm());
		values.put(TodoTable.DATE_SET, todo.getDateSet());
		values.put(TodoTable.MODIFIED, todo.getModified());
		values.put(TodoTable.CHANGED, todo.getChanged());
		values.put(TodoTable.DELETED, todo.getDeleted());

		return values;
	}
	
	public static long getId(Activity activity, String dateStart, String timeFrom, String timeUtil) {
		long Id = -1;
		final Cursor cursor = activity.getContentResolver().query(
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				TodoTable.DATE_START + "='" + dateStart + "' AND " + 
				TodoTable.TIME_FROM + "='" + timeFrom + "' AND " +
				TodoTable.TIME_UNTIL + "='" + timeUtil + "'", 
				null, 
				null);
		activity.startManagingCursor(cursor);		
		if (cursor != null)
			if (cursor.moveToFirst()) 
				Id = cursor.getLong(TodoTable.ID_COLUMN_INDEX);
		closeCursor(cursor);
		return Id;
	}
	
	public static long getTodoIdByEventId(Activity activity, long eventId) {
		
		if (eventId == -1)
			return -1;
		
		final Uri uriId = ContentUris.withAppendedId(event.CONTENT_URI, eventId);
		final Cursor eventCursor = activity.getContentResolver().query(uriId, event.PROJECTION, null, null, null);
		
		activity.startManagingCursor(eventCursor);
		
		long Id = -1;
		if (eventCursor.moveToFirst()) {
			final long startTime = eventCursor.getLong(event.DATE_START_COLUMN_INDEX);
			final long endTime = eventCursor.getLong(event.DATE_END_COLUMN_INDEX);
			Id = getId(activity, TimeUtils.getDate(startTime), TimeUtils.getTime(startTime), TimeUtils.getTime(endTime));
		}
		closeCursor(eventCursor);
		
		return Id;
	}
	public static boolean checkDeleted(Activity activity, long id) {
		int deleted = 0;
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, id);
		final Cursor cursor = activity.getContentResolver().query(
				uriId, 
				TodoTable.PROJECTION, 
				null, 
				null, 
				null);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				deleted = cursor.getInt(TodoTable.DELETED_COLUMN_INDEX);
			} while (cursor.moveToNext());
		} 
		closeCursor(cursor);
		return deleted == 0 ? false : true;
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed())
			cursor.close();
	}
	
	private static boolean addEvent(Activity activity, Todo todo) {

		ContentValues values = new ContentValues();
		values.put(IConstants.event.CALENDAR_ID, IConstants.event.CALENDAR);
		values.put(IConstants.event.TITLE, todo.getTitle());
		values.put(IConstants.event.DESCRIPTION, todo.getWork());
		final long dtstart = TimeUtils.toTimeInMilisecond(todo.getDateStart(), todo.getTimeFrom());
		final long dtend = TimeUtils.toTimeInMilisecond(todo.getDateStart(), todo.getTimeUntil());
		values.put(IConstants.event.DATE_START, dtstart);
		values.put(IConstants.event.DATE_END, dtend);
		values.put(IConstants.event.TIMEZONE, TimeZone.getDefault().toString());
		values.put(IConstants.event.HAS_ALARM, todo.getAlarm());
		values.put("eventStatus", 1);
		values.put("allDay", 0);
		
        Uri eventsUri = Uri.parse("content://com.android.calendar/events");   
	        
		ContentResolver contentResovler = activity.getContentResolver();
		Uri event = contentResovler.insert(eventsUri, values);
		if (event == null)
			return false;

		values = new ContentValues();
		values.put(IConstants.reminder.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
		values.put(IConstants.reminder.METHOD, 1);
		values.put(IConstants.reminder.MINUTES, 10);
		Uri remider = contentResovler.insert(IConstants.reminder.CONTENT_URI, values);
		if (remider == null)
			return false;
		
		return true;
	}
	
	private static boolean updateEvent(Activity activity, long id, Todo todo) {
		ContentResolver cr = activity.getContentResolver();

		ContentValues values = new ContentValues();
		values.put(IConstants.event.CALENDAR_ID, IConstants.event.CALENDAR);
		values.put(IConstants.event.TITLE, todo.getTitle());
		values.put(IConstants.event.DESCRIPTION, todo.getWork());
		final long dtstart = TimeUtils.toTimeInMilisecond(todo.getDateStart(), todo.getTimeFrom());
		final long dtend = TimeUtils.toTimeInMilisecond(todo.getDateEnd(), todo.getTimeUntil());
		values.put(IConstants.event.DATE_START, dtstart);
		values.put(IConstants.event.DATE_END, dtend);
		values.put(IConstants.event.HAS_ALARM, todo.getAlarm());
		Uri updateUri = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, id);
		if (id == -1)
			updateUri = event.CONTENT_URI;
		int event = cr.update(updateUri, values, null, null);
		if (event < 0)
			return false;
		return true;
	}
	
	private static int deleteEvent(Activity activity, long id) {
		if (id == -1) {
			return activity.getContentResolver().delete(IConstants.event.CONTENT_URI, null, null);
		}
		Uri uriId = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, id);
		return activity.getContentResolver().delete(uriId, null, null);
	}
	
	private static void bindTodoData(Todo todo, Cursor cursor) {
		todo.setId(cursor.getLong(TodoTable.ID_COLUMN_INDEX));
		todo.setDateStart(cursor.getString(TodoTable.DATE_START_COLUMN_INDEX));
		todo.setDateEnd(cursor.getString(TodoTable.DATE_END_COLUMN_INDEX));
		todo.setTimeFrom(cursor.getString(TodoTable.TIME_FROM_COLUMN_INDEX));
		todo.setTimeUntil(cursor.getString(TodoTable.TIME_UNTIL_COLUMN_INDEX));
		todo.setTitle(cursor.getString(TodoTable.TITLE_COLUMN_INDEX));
		todo.setWork(cursor.getString(TodoTable.WORK_COLUMN_INDEX));
		todo.setAlarm(cursor.getInt(TodoTable.ALAMR_COLUMN_INDEX));
		todo.setDateSet(cursor.getString(TodoTable.DATE_SET_COLUMN_INDEX));
		todo.setModified(cursor.getString(TodoTable.MODIFIED_COLUMN_INDEX));
		todo.setChanged(cursor.getInt(TodoTable.CHANGED_COLUMN_INDEX));
		todo.setDeleted(cursor.getInt(TodoTable.DELETED_COLUMN_INDEX));
	}
	public static Todo getTodo(Activity activity, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		final Cursor cursor = activity.getContentResolver().query(uriId, TodoTable.PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			Todo todo = new Todo();
			bindTodoData(todo, cursor);
			closeCursor(cursor);
			return todo;
		}
		closeCursor(cursor);
		return null;
	}
	
	public static List<Todo> getListTodoChanged(Activity activity) {
		List<Todo> listChanged = new ArrayList<Todo>();
		final Cursor cursor = activity.getContentResolver().query(
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				TodoTable.CHANGED + "=1", 
				null, 
				null
				);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				Todo todo = new Todo();
				bindTodoData(todo, cursor);
				listChanged.add(todo);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return listChanged;
	}
	
	public static List<Todo> getListTodoByDay(Activity activity, int dayOfMonth) {
		List<Todo> listChanged = new ArrayList<Todo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		final Cursor cursor = activity.getContentResolver().query(
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				TodoTable.DATE_START + "=?", 
				new String[]{TimeUtils.getDate(cal.getTimeInMillis())}, 
				null
				);
		activity.startManagingCursor(cursor);
		if (cursor.moveToFirst()) {
			do {
				Todo todo = new Todo();
				bindTodoData(todo, cursor);
				listChanged.add(todo);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return listChanged;
	}
	
	public static void sync(int userId, Activity activity) {
		// sync from server
		final String result = WebservicesUtils.sync(userId, TodoTable.TABLE_NAME);
		Log.d("sync Todo", "result from server: " + result);
		if (!TextUtils.isEmpty(result)) {
			List<Todo> listTodoChangedFromServer = XMLUtils.getTodo(result);
			for (Todo todo : listTodoChangedFromServer) {
				syncEachInstance(activity, todo);
			}
		}
		
		// sync to server
		List<Todo> listChanged = getListTodoChanged(activity);
		for (Todo todo : listChanged) {
			final String sync_app = WebservicesUtils.sync_todo_app(userId, todo);
			Log.d("sync", "sync_app result: " + sync_app);
			if (sync_app.equals("1")) { 
				final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, todo.getId());
				if (todo.getDeleted() == 1)
					activity.getContentResolver().delete(uriId, null, null);
				else {
					ContentValues values = new ContentValues();
					values.put(TodoTable.CHANGED, 0);
					activity.getContentResolver().update(uriId, values, null, null);
				}
			}
		}
	}
	
	public static void syncEachInstance(Activity activity, Todo serverTodo) {
		final long Id = getId(activity, serverTodo.getDateStart(), serverTodo.getTimeFrom(), serverTodo.getTimeUntil());
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		if (Id > 0) {
			Todo localTodo = getTodo(activity, Id);
			Log.d("syncTodo", "server date modified: " + serverTodo.getModified());
			if (localTodo.getModified().compareTo(serverTodo.getModified()) < 0) {
				if (serverTodo.getDeleted() == 1) 
					activity.getContentResolver().delete(uriId, null, null);
				else {
					serverTodo.setChanged(0);
					update(activity, serverTodo, Id);
				}
			} 
		} else {
			if (serverTodo.getDeleted() == 0) {
				serverTodo.setChanged(0);
				insert(activity, serverTodo);
			}
		}
	}

}
