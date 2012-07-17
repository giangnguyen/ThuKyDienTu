package myapp.thukydientu.provider;

import java.util.Arrays;
import java.util.HashSet;

import myapp.thukydientu.database.DayOfWeekTable;
import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.database.TodoTable;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TKDTProvider extends ContentProvider {

	// incomeDatabaseHelper
	private TKDTDatabaseHelper databaseHelper;

	// Used for the UriMacher
	private static final int SCHEDULE = 100;
	private static final int SCHEDULE_ID = 200;
	private static final int TODO = 300;
	private static final int TODO_ID = 400;
	private static final int DAY_OF_WEEK = 500;
	private static final int DAY_OF_WEEK_ID = 600;
	
	private static final String AUTHORITY = "myapp.thukydientu.provider.provider.TKDTProvider";

	public static final Uri SCHEDULE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ScheduleTable.TABLE_NAME);
	public static final Uri TODO_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TodoTable.TABLE_NAME);
	public static final Uri DAY_OF_WEEK_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DayOfWeekTable.TABLE_NAME);

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, ScheduleTable.TABLE_NAME, SCHEDULE);
		sURIMatcher.addURI(AUTHORITY, ScheduleTable.TABLE_NAME + "/#", SCHEDULE_ID);
		sURIMatcher.addURI(AUTHORITY, TodoTable.TABLE_NAME, TODO);
		sURIMatcher.addURI(AUTHORITY, TodoTable.TABLE_NAME + "/#", TODO_ID);
		sURIMatcher.addURI(AUTHORITY, DayOfWeekTable.TABLE_NAME, DAY_OF_WEEK);
		sURIMatcher.addURI(AUTHORITY, DayOfWeekTable.TABLE_NAME + "/#", DAY_OF_WEEK_ID);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new TKDTDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		SQLiteDatabase database = databaseHelper.getReadableDatabase();

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SCHEDULE:
			// Check if the caller has requested a column which does not exists
			checkColumns(projection, ScheduleTable.PROJECTION);
			// Set the table
			queryBuilder.setTables(ScheduleTable.TABLE_NAME);
			break;
		case SCHEDULE_ID:
			// Check if the caller has requested a column which does not exists
			checkColumns(projection, ScheduleTable.PROJECTION);
			// Set the table
			queryBuilder.setTables(ScheduleTable.TABLE_NAME);
			// Adding the ID to the original query
			queryBuilder.appendWhere(ScheduleTable._ID + "=" + uri.getLastPathSegment());
			break;
		case TODO:
			// Check if the caller has requested a column which does not exists
			checkColumns(projection, TodoTable.PROJECTION);
			// Set the table
			queryBuilder.setTables(TodoTable.TABLE_NAME);
			break;
		case TODO_ID:
			// Check if the caller has requested a column which does not exists
			checkColumns(projection, TodoTable.PROJECTION);
			// Set the table
			queryBuilder.setTables(TodoTable.TABLE_NAME);
			// Adding the ID to the original query
			queryBuilder.appendWhere(TodoTable._ID + "=" + uri.getLastPathSegment());
			break;
		case DAY_OF_WEEK:
			// Check if the caller has requested a column which does not exists
			checkColumns(projection, DayOfWeekTable.PROJECTION);
			// Set the table
			queryBuilder.setTables(DayOfWeekTable.TABLE_NAME);
			break;
		case DAY_OF_WEEK_ID:
			// Check if the caller has requested a column which does not exists
			checkColumns(projection, DayOfWeekTable.PROJECTION);
			// Set the table
			queryBuilder.setTables(DayOfWeekTable.TABLE_NAME);
			// Adding the ID to the original query
			queryBuilder.appendWhere(DayOfWeekTable._ID + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		Cursor cursor = queryBuilder.query(database, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case SCHEDULE:
			id = sqlDB.insert(ScheduleTable.TABLE_NAME, null, values);
			break;
		case TODO:
			id = sqlDB.insert(TodoTable.TABLE_NAME, null, values);
			break;
		case DAY_OF_WEEK:
			id = sqlDB.insert(DayOfWeekTable.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		Uri tmpUri = ContentUris.withAppendedId(uri, id);
		getContext().getContentResolver().notifyChange(tmpUri, null);
		return tmpUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		int rowsDeleted = 0;
		long _id;
		switch (uriType) {
		case SCHEDULE:
			rowsDeleted = sqlDB.delete(ScheduleTable.TABLE_NAME, selection, selectionArgs);
			break;
		case SCHEDULE_ID:
			_id = Long.parseLong(uri.getLastPathSegment());
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(ScheduleTable.TABLE_NAME,
						ScheduleTable._ID + "=" + _id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(ScheduleTable.TABLE_NAME,
						ScheduleTable._ID + "=" + _id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case TODO:
			rowsDeleted = sqlDB.delete(TodoTable.TABLE_NAME, selection, selectionArgs);
			break;
		case TODO_ID:
			_id = Long.parseLong(uri.getLastPathSegment());
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(TodoTable.TABLE_NAME,
						TodoTable._ID + "=" + _id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(TodoTable.TABLE_NAME,
						TodoTable._ID + "=" + _id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		case DAY_OF_WEEK:
			rowsDeleted = sqlDB.delete(DayOfWeekTable.TABLE_NAME, selection, selectionArgs);
			break;
		case DAY_OF_WEEK_ID:
			_id = Long.parseLong(uri.getLastPathSegment());
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(DayOfWeekTable.TABLE_NAME,
						DayOfWeekTable._ID + "=" + _id, 
						null);
			} else {
				rowsDeleted = sqlDB.delete(DayOfWeekTable.TABLE_NAME,
						DayOfWeekTable._ID + "=" + _id 
						+ " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = databaseHelper.getWritableDatabase();
		int rowsUpdated = 0;
		long _id;
		switch (uriType) {
		case SCHEDULE:
			rowsUpdated = sqlDB.update(ScheduleTable.TABLE_NAME, 
					values, 
					selection,
					selectionArgs);
			break;
		case SCHEDULE_ID:
			_id = Long.parseLong(uri.getLastPathSegment());
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(ScheduleTable.TABLE_NAME, 
						values,
						ScheduleTable._ID + "=" + _id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(ScheduleTable.TABLE_NAME, 
						values,
						ScheduleTable._ID + "=" + _id 
						+ " and " 
						+ selection,
						selectionArgs);
			}
			break;
		case TODO:
			rowsUpdated = sqlDB.update(TodoTable.TABLE_NAME, 
					values, 
					selection,
					selectionArgs);
			break;
		case TODO_ID:
			_id = Long.parseLong(uri.getLastPathSegment());
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(TodoTable.TABLE_NAME, 
						values,
						TodoTable._ID + "=" + _id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(TodoTable.TABLE_NAME, 
						values,
						TodoTable._ID + "=" + _id 
						+ " and " 
						+ selection,
						selectionArgs);
			}
			break;
		case DAY_OF_WEEK:
			rowsUpdated = sqlDB.update(DayOfWeekTable.TABLE_NAME, 
					values, 
					selection,
					selectionArgs);
			break;
		case DAY_OF_WEEK_ID:
			_id = Long.parseLong(uri.getLastPathSegment());
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(DayOfWeekTable.TABLE_NAME, 
						values,
						DayOfWeekTable._ID + "=" + _id, 
						null);
			} else {
				rowsUpdated = sqlDB.update(DayOfWeekTable.TABLE_NAME, 
						values,
						DayOfWeekTable._ID + "=" + _id 
						+ " and " 
						+ selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection, String[] available) {
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException("Unknown columns in projection");
			}
		}
	}

}