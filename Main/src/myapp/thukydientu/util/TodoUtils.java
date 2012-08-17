package myapp.thukydientu.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import myapp.thukydientu.database.TodoTable;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.IConstants.event;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.provider.TKDTProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class TodoUtils {
	public static final int FAIL = -1;
	public static final int DO_NOTHING = 0;
	public static final int SUCCESS = 1;
	public static final int REQUEST_TO_UPDATE = 2;
	
	public static int insert(Context context, Todo todo) {
		long Id = getId(context, todo.getDateStart(), todo.getTimeFrom(), todo.getTimeUntil());
		if (Id == -1 || checkDeleted(context, Id)) {
			final long time = System.currentTimeMillis();
			if (TextUtils.isEmpty(todo.getDateSet()))
				todo.setDateSet(time);
			if (TextUtils.isEmpty(todo.getModified()))
				todo.setModified(time);
			
//			if (!addEvent(context, todo)) 
//				return FAIL;
			
			ContentValues values = createContentValues(todo);
			Log.d("insert", "modified: " + values.getAsString(TodoTable.MODIFIED));
			final Uri uri = context.getContentResolver().insert(TKDTProvider.TODO_CONTENT_URI, values);
			if (uri == null) 
				return FAIL;
			else 
				return SUCCESS;
		} else {
			return REQUEST_TO_UPDATE;
		}
	}
	
	public static int update(Context context, Todo todo) {
		if (todo == null)
			return FAIL;
		
//		if (!updateEvent(context, todo))
//			return FAIL;
		
		// initial values to update
		if (TextUtils.isEmpty(todo.getModified()))
			todo.setModified(System.currentTimeMillis());
		
		if (TextUtils.isEmpty(todo.getDateSet()))
			todo.setDateSet(getDateSet(context, todo.getId()));
		
		ContentValues values = createContentValues(todo);
		
		Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, todo.getId());
		
		if (todo.getId() == -1) 
			uriId = TKDTProvider.TODO_CONTENT_URI;
		
		
		final int updateResult = context.getContentResolver().update(uriId, values, null, null);
		if (updateResult < 0)
			return FAIL;
		else 
			return SUCCESS;
	}
	
	public static String getDateSet(Context context, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		String dateSet = TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance());
		final Cursor cursor = context.getContentResolver().query(
				uriId, 
				TodoTable.PROJECTION, 
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				dateSet = cursor.getString(TodoTable.DATE_SET_COLUMN_INDEX);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		return dateSet;
	}
	
	public static void deleteAll(Context context) {
		delete(context, null);
	}
	
	public static void delete(Context context, Todo todo) {
		
		if (todo == null) {
			context.getContentResolver().delete(TKDTProvider.TODO_CONTENT_URI, null, null);
//			deleteEventById(context, -1);
		} else {
//			long eventId = getEventIdByTodo(context, todo);
//			final int deleteEvent = deleteEventById(context, eventId);
//			
//			if (deleteEvent > 0) {
				ContentValues values = new ContentValues();
				values.put(TodoTable.CHANGED, 1);
				values.put(TodoTable.DELETED, 1);
				values.put(TodoTable.MODIFIED, TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance()));
				
				Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, todo.getId());
				context.getContentResolver().update(uriId, values, null, null);
//			}
		}
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
	
	public static long getId(Context context, String dateStart, String timeFrom, String timeUtil) {
		long Id = -1;
		final Cursor cursor = context.getContentResolver().query(
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				TodoTable.DATE_START + "='" + dateStart + "' AND " + 
				TodoTable.TIME_FROM + "='" + timeFrom + "' AND " +
				TodoTable.TIME_UNTIL + "='" + timeUtil + "'", 
				null, 
				null);
		if (cursor != null)
			if (cursor.moveToFirst()) 
				Id = cursor.getLong(TodoTable.ID_COLUMN_INDEX);
		closeCursor(cursor);
		return Id;
	}
	
	public static boolean checkDeleted(Context context, long id) {
		int deleted = 0;
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, id);
		final Cursor cursor = context.getContentResolver().query(
				uriId, 
				TodoTable.PROJECTION, 
				null, 
				null, 
				null);
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
	
	private static boolean addEvent(Context context, Todo todo) {

		ContentValues values = new ContentValues();
		values.put(IConstants.event.CALENDAR_ID, IConstants.event.CALENDAR);
		values.put(IConstants.event.TITLE, todo.getTitle());
		values.put(IConstants.event.DESCRIPTION, todo.getWork());
		final long dtstart = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeFrom()).getTimeInMillis();
		final long dtend = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateEnd(), todo.getTimeUntil()).getTimeInMillis();
		values.put(IConstants.event.DATE_START, dtstart);
		values.put(IConstants.event.DATE_END, dtend);
		values.put(IConstants.event.TIMEZONE, TimeZone.getDefault().toString());
		values.put(IConstants.event.HAS_ALARM, todo.getAlarm());
		values.put("eventStatus", 1);
		values.put("allDay", 0);
		
        Uri eventsUri = Uri.parse("content://com.android.calendar/events");   
	        
		ContentResolver contentResovler = context.getContentResolver();
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
	
	private static boolean updateEvent(Context context, Todo todo) {
		
		ContentValues values = new ContentValues();
		values.put(IConstants.event.CALENDAR_ID, IConstants.event.CALENDAR);
		values.put(IConstants.event.TITLE, todo.getTitle());
		values.put(IConstants.event.DESCRIPTION, todo.getWork());
		final long dtstart = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeFrom()).getTimeInMillis();
		final long dtend = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateEnd(), todo.getTimeUntil()).getTimeInMillis();
		values.put(IConstants.event.DATE_START, dtstart);
		values.put(IConstants.event.DATE_END, dtend);
		values.put(IConstants.event.HAS_ALARM, todo.getAlarm());
		Uri updateUri = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, todo.getId());
		if (todo.getId() == -1)
			updateUri = event.CONTENT_URI;
		int event = context.getContentResolver().update(updateUri, values, null, null);
		if (event < 0)
			return false;
		return true;
	}
	
	public static long getEventIdByTodo(Context context, Todo todo) {
		long eventId = -1;
		final long dateStart = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeFrom()).getTimeInMillis();
		final long dateEnd = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeUntil()).getTimeInMillis();
		final Cursor eventCursor = context.getContentResolver().query(
				event.CONTENT_URI, 
				event.PROJECTION, 
				event.DATE_START + "=" + dateStart + " AND " + 
				event.DATE_END + "=" + dateEnd + " AND " + 
				event.TITLE + "='" + todo.getTitle() + "'" + " AND " + 
				event.DESCRIPTION + "='" + todo.getWork() + "'", 
				null, 
				null);
		
		if (eventCursor.moveToFirst()) {
			eventId = eventCursor.getLong(event.ID_COLUMN_INDEX);
		}
		closeCursor(eventCursor);
		
		return eventId;
	}
	
	private static int deleteEventById(Context context, long id) {
		if (id == -1) {
			return context.getContentResolver().delete(IConstants.event.CONTENT_URI, null, null);
		}
		Uri uriId = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, id);
		return context.getContentResolver().delete(uriId, null, null);
	}
	
	
	public static void bindTodoData(Todo todo, Cursor cursor) {
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
	
	public static Todo getTodo(Context context, long Id) {
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		final Cursor cursor = context.getContentResolver().query(uriId, TodoTable.PROJECTION, null, null, null);
		if (cursor.moveToFirst()) {
			Todo todo = new Todo();
			bindTodoData(todo, cursor);
			closeCursor(cursor);
			return todo;
		}
		closeCursor(cursor);
		return null;
	}
	
	public static List<Todo> getListTodoChanged(Context context) {
		List<Todo> listChanged = new ArrayList<Todo>();
		final Cursor cursor = context.getContentResolver().query(
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				TodoTable.CHANGED + "=1", 
				null, 
				null
				);
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
	
	public static List<Todo> getListTodoByDay(Context context, int dayOfMonth) {
		List<Todo> listChanged = new ArrayList<Todo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		final Cursor cursor = context.getContentResolver().query(
				TKDTProvider.TODO_CONTENT_URI, 
				TodoTable.PROJECTION, 
				TodoTable.DATE_START + "=?", 
				new String[]{TaleTimeUtils.getDateStringByCalendar(cal)}, 
				null
				);
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
	
	public static void sync(int userId, Context context) {
		// sync from server
		final String result = WebservicesUtils.sync(userId, TodoTable.TABLE_NAME);
		Log.d("sync Todo", "result from server: " + result);
		if (!TextUtils.isEmpty(result)) {
			List<Todo> listTodoChangedFromServer = XMLUtils.getTodo(result);
			for (Todo todo : listTodoChangedFromServer) {
				syncEachInstance(context, todo);
			}
		}
		
		// sync to server
		List<Todo> listChanged = getListTodoChanged(context);
		for (Todo todo : listChanged) {
			final String sync_app = WebservicesUtils.sync_todo_app(userId, todo);
			Log.d("sync", "sync_app result: " + sync_app);
			if (sync_app.equals("1")) { 
				final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, todo.getId());
				if (todo.getDeleted() == 1){
					TodoUtils.delete(context, todo);
//					getEventIdByTodo(context, todo);
					Log.d("sync", "sync_app result: " + sync_app);
				}else {
					ContentValues values = new ContentValues();
					values.put(TodoTable.CHANGED, 0);
					context.getContentResolver().update(uriId, values, null, null);
				}
			}
		}
	}
	
	public static void syncEachInstance(Context context, Todo serverTodo) {
		final long Id = getId(context, serverTodo.getDateStart(), serverTodo.getTimeFrom(), serverTodo.getTimeUntil());
		serverTodo.setId(Id);
		Log.d("syncEachInstance", "ID: " + Id);
		final Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, Id);
		if (Id > 0) {
			Todo localTodo = getTodo(context, Id);
			Log.d("syncTodo", "server date modified: " + serverTodo.getModified());
			if (localTodo.getModified().compareTo(serverTodo.getModified()) < 0) {
				if (serverTodo.getDeleted() == 1) { 
					context.getContentResolver().delete(uriId, null, null);
					Log.d("syncEachInstance", "deteted:  " + uriId);
				} else {
					serverTodo.setChanged(0);
					update(context, serverTodo);
				}
			} 
		} else {
			if (serverTodo.getDeleted() == 0) {
				serverTodo.setChanged(0);
				insert(context, serverTodo);
			}
		}
	}

}
