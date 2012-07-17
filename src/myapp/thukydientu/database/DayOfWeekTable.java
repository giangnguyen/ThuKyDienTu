package myapp.thukydientu.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DayOfWeekTable {

	public static final String TABLE_NAME = "dayofweek";
	
	public static final String _ID 			= "_id";
	public static final String DATE_NAME 	= "category";
	public static final String[] PROJECTION = {
		_ID,				// 0
		DATE_NAME			// 1
	};
	
	public static final int ID_COLUMN_INDEX 		= 0;
	public static final int DATE_NAME_COLUMN_INDEX 	= 1;	

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
			+ _ID 			+ " INTEGER primary key autoincrement," 
			+ DATE_NAME 	+ " INTEGER);";
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE	+ "/" + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TABLE_NAME;

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		for (int i = 1; i < 8; i++) {
			ContentValues values = new ContentValues();
			values.put(DATE_NAME, i);
			database.insert(TABLE_NAME, null, values);
		}
		Log.d(TABLE_NAME, "onCreate...");
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(DayOfWeekTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}