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
import myapp.thukydientu.view.MainActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class TodoUtils {
	public static final int RESULT_FAIL = -1;
	public static final int DO_NOTHING = 0;
	public static final int RESULT_SUCCESS = 1;
	public static final int REQUEST_TO_UPDATE = 2;
	
	public static final int RESULT_NOT_EXISTED = -1;
	
	public static String queryString_SelectTodo(Todo todo) {
		return
				TodoTable.USER_ID + "=" + MainActivity.sUserId 
				+ " AND " +
				TodoTable.DATE_START + "='" + todo.getDateStart()
				+ "' AND " +
				TodoTable.DATE_END + "='" + todo.getDateEnd()
				+ "' AND " + 
				TodoTable.TIME_FROM + "='" + todo.getTimeFrom()
				+ "' AND " + 
				TodoTable.TIME_UNTIL + "='" + todo.getTimeUntil()
				+ "' AND " + 
				TodoTable.DELETED + "=" + 0
				;
	}
	
	public static String queryString_Changed() {
		return 
				TodoTable.USER_ID + "=" + MainActivity.sUserId
					+ " AND " +
				TodoTable.CHANGED + "=" + 1
			;
	}
	
	public static String queryString_All() {
		return 
				TodoTable.USER_ID + "=" + MainActivity.sUserId
					+ " AND " +
				TodoTable.DELETED + "=" + 0
			;
	}

	public static boolean isExisted(Context context, Todo todo) {
		
		boolean result = false;
		
		final Cursor cursor = context.getContentResolver()
								.query(
										TKDTProvider.TODO_CONTENT_URI, 
										TodoTable.PROJECTION, 
										queryString_SelectTodo(todo), 
										null, 
										null
								);
		
		result = cursor.moveToFirst();
		
		closeCursor(cursor);
		
		return result;
	}
	
	public static int insert(Context context, Todo todo) {
		if (isExisted(context, todo)) {
			return REQUEST_TO_UPDATE;
		} else {
			
			try {
				if (!addEvent(context, todo)) 
					return RESULT_FAIL;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			final long time = System.currentTimeMillis();
			if (TextUtils.isEmpty(todo.getDateSet()))
				todo.setDateSet(time);
			if (TextUtils.isEmpty(todo.getModified()))
				todo.setModified(time);
			
			ContentValues values = createContentValues(todo);
			Log.d("insert", "modified: " + values.getAsString(TodoTable.MODIFIED));
			final Uri uri = context.getContentResolver().insert(TKDTProvider.TODO_CONTENT_URI, values);
			if (uri == null) {
				delete(context, todo);
				return RESULT_FAIL;
			} else 
				return RESULT_SUCCESS;
		}
	}
	
	public static void delete(Context context, Todo todo) {

		ContentValues values = new ContentValues();
		values.put(TodoTable.CHANGED, 1);
		values.put(TodoTable.DELETED, 1);
		values.put(TodoTable.MODIFIED, TaleTimeUtils.getDateTimeStringByCalendar(Calendar.getInstance()));
		
		if (todo == null) { 
			context.getContentResolver()
				.update(
					TKDTProvider.TODO_CONTENT_URI, 
					values, 
					null, 
					null
				);
		} else {
			context.getContentResolver()
			.update(
				TKDTProvider.TODO_CONTENT_URI, 
				values, 
				queryString_SelectTodo(todo), 
				null
			);
		}
		
		deleteEvent(context, todo);
	}
	
	public static int update(Context context, Todo todo) {
		if (todo == null)
			return RESULT_FAIL;

		if (!isExisted(context, todo))
			return RESULT_FAIL;
		
		updateEvent(context, todo);
		
		// initial values to update
		ContentValues values = createContentValues(todo);
		
		final int updateResult = context.getContentResolver()
										.update(
												TKDTProvider.TODO_CONTENT_URI, 
												values, 
												queryString_SelectTodo(todo), 
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
	
	private static ContentValues createContentValues(Todo todo) {
		// initial values to update
		ContentValues values = new ContentValues();
		
		values.put(TodoTable.USER_ID, MainActivity.sUserId);
		values.put(TodoTable.DATE_START, todo.getDateStart());
		values.put(TodoTable.DATE_END, todo.getDateEnd());
		values.put(TodoTable.TIME_FROM, todo.getTimeFrom());
		values.put(TodoTable.TIME_UNTIL, todo.getTimeUntil());
		values.put(TodoTable.TITLE, todo.getTitle());
		values.put(TodoTable.WORK, todo.getWork());
		values.put(TodoTable.ALARM, todo.getAlarm());

		if (!TextUtils.isEmpty(todo.getDateSet()))
			values.put(TodoTable.DATE_SET, todo.getDateSet());
		if (!TextUtils.isEmpty(todo.getModified()))	
			values.put(TodoTable.MODIFIED, todo.getModified());
		
		values.put(TodoTable.CHANGED, todo.getChanged());
		values.put(TodoTable.DELETED, todo.getDeleted());

		return values;
	}
	
	public static void closeCursor(Cursor cursor) {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
			cursor = null;
		}
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
	
	private static int deleteEvent(Context context, Todo todo) {
		if (todo == null)
			return context.getContentResolver().delete(IConstants.event.CONTENT_URI, null, null);
		
		final long eventId = getEventId(context, todo); 
		Uri uriId = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, eventId);
		return context.getContentResolver().delete(uriId, null, null);
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
		
		long eventId = getEventId(context, todo);
		Uri updateUri = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, eventId);
		
		int event = context.getContentResolver()
					.update(
							updateUri, 
							values, 
							null, 
							null
					);
		if (event < 0)
			return false;
		return true;
	}
	
	public static long getEventId(Context context, Todo todo) {
		long eventId = RESULT_NOT_EXISTED;
		
		final long dateStart = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeFrom()).getTimeInMillis();
		final long dateEnd = TaleTimeUtils.createCalendarByDateTimeString(todo.getDateStart(), todo.getTimeUntil()).getTimeInMillis();
		
		final Cursor eventCursor = context.getContentResolver().query(
				event.CONTENT_URI, 
				event.PROJECTION, 
				event.DATE_START + "=" + dateStart 
				+ " AND " + 
				event.DATE_END + "=" + dateEnd, 
				null, 
				null);
		
		if (eventCursor.moveToFirst()) 
			eventId = eventCursor.getLong(event.ID_COLUMN_INDEX);
		closeCursor(eventCursor);
		
		return eventId;
	}
	
	public static void bindTodoData(Todo todo, Cursor cursor) {
		todo.setId(cursor.getLong(TodoTable.ID_COLUMN_INDEX));
		todo.setUserId(cursor.getInt(TodoTable.USER_ID_COLUMN_INDEX));
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
	
	public static List<Todo> getAll(Context context) {
		List<Todo> lists = new ArrayList<Todo>();
		
		final Cursor cursor = context.getContentResolver()
								.query(
									TKDTProvider.TODO_CONTENT_URI, 
									TodoTable.PROJECTION, 
									queryString_All(), 
									null, 
									null
								);
		
		if (cursor.moveToFirst()) {
			do {
				Todo todo = new Todo();
				bindTodoData(todo, cursor);
				lists.add(todo);
			} while (cursor.moveToNext());
		}
		closeCursor(cursor);
		
		return lists;
	}
	
	public static List<Todo> getAllChanged(Context context) {
		List<Todo> listChanged = new ArrayList<Todo>();
		
		final Cursor cursor = context.getContentResolver()
								.query(
									TKDTProvider.TODO_CONTENT_URI, 
									TodoTable.PROJECTION, 
									queryString_Changed(), 
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
	
	public static List<Todo> getAllByDay(Context context, int dayOfMonth) {
		List<Todo> listChanged = new ArrayList<Todo>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		final Cursor cursor = context.getContentResolver()
									.query(
										TKDTProvider.TODO_CONTENT_URI, 
										TodoTable.PROJECTION, 
										queryString_All(), 
										null, 
										null
									);
		if (cursor.moveToFirst()) {
			do {
				Todo todo = new Todo();
				bindTodoData(todo, cursor);
				final Calendar todoCal = TaleTimeUtils.createCalendarByDateString(todo.getDateStart());
				if (todoCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
					listChanged.add(todo);
			} while (cursor.moveToNext());
		}
		
		closeCursor(cursor);
		
		return listChanged;
	}
	
	public static Todo getLocalTodo(Context context, Todo serverTodo) {
		final Cursor cursor = context.getContentResolver()
								.query(
										TKDTProvider.TODO_CONTENT_URI, 
										TodoTable.PROJECTION, 
										queryString_SelectTodo(serverTodo), 
										null, 
										null
								);
		if (cursor.moveToFirst()) {
			Todo todo = new Todo();
			bindTodoData(todo, cursor);
			return todo;
		}
		
		return null;
			
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
		List<Todo> listChanged = getAllChanged(context);
		for (Todo todo : listChanged) {
			final String sync_app = WebservicesUtils.sync_todo_app(userId, todo);
			Log.d("sync", "sync_app result: " + sync_app);
			if (sync_app.equals("1")) { 
				if (todo.getDeleted() == 1){
					delete(context, todo);
					Log.d("sync", "sync_app result: " + sync_app);
				}else {
					todo.setChanged(0);
					update(context, todo);
				}
			}
		}
	}
	
	public static void syncEachInstance(Context context, Todo serverTodo) {
		if (isExisted(context, serverTodo)) {
			Todo localTodo = getLocalTodo(context, serverTodo);
			Log.d("syncTodo", "server date modified: " + serverTodo.getModified());
			if (localTodo.getModified().compareTo(serverTodo.getModified()) < 0) {
				if (serverTodo.getDeleted() == 1) { 
					delete(context, localTodo);
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
