package myapp.thukydientu.database;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

	public static final String TABLE_NAME		= "todo";
	
	public static final String _ID 				= "_id";
	public static final String DATE_START 		= "datestart";
	public static final String DATE_END 		= "dateend";
	public static final String TIME_FROM 		= "timefrom";
	public static final String TIME_UNTIL 		= "timeuntil";
	public static final String TITLE			= "title";
	public static final String WORK 			= "work";
	public static final String ALARM 			= "alarm";
	public static final String DATE_SET 		= "dateset";
	public static final String MODIFIED 		= "modified";
	public static final String CHANGED			= "changed";
	public static final String DELETED			= "deleted";
	
	public static final String[] PROJECTION = {
		_ID,			// 0
		DATE_START, 	// 1
		DATE_END,		// 2
		TIME_FROM,		// 3
		TIME_UNTIL,		// 4
		TITLE,			// 5
		WORK,			// 6
		ALARM,			// 7
		DATE_SET,		// 8
		MODIFIED,		// 9
		CHANGED,		// 10
		DELETED			// 11
	};
	
	public static final int ID_COLUMN_INDEX 		= 0;
	public static final int DATE_START_COLUMN_INDEX	= 1;
	public static final int DATE_END_COLUMN_INDEX 	= 2;
	public static final int TIME_FROM_COLUMN_INDEX 	= 3;
	public static final int TIME_UNTIL_COLUMN_INDEX = 4;
	public static final int TITLE_COLUMN_INDEX 		= 5;
	public static final int WORK_COLUMN_INDEX 		= 6;
	public static final int ALAMR_COLUMN_INDEX 		= 7;
	public static final int DATE_SET_COLUMN_INDEX 	= 8;
	public static final int MODIFIED_COLUMN_INDEX 	= 9;
	public static final int CHANGED_COLUMN_INDEX	= 10;
	public static final int DELETED_COLUMN_INDEX	= 11;

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
			+ _ID 			+ " INTEGER primary key autoincrement," 
			+ DATE_START 	+ " VARCHAR(8),"
			+ DATE_END 		+ " VARCHAR(8),"
			+ TIME_FROM 	+ " VARCHAR(6),"
			+ TIME_UNTIL	+ " VARCHAR(6),"
			+ TITLE 		+ " VARCHAR(150),"
			+ WORK 			+ " VARCHAR(150),"
			+ ALARM 		+ " INTEGER,"
			+ DATE_SET 		+ " VARCHAR(14),"
			+ MODIFIED		+ " VARCHAR(14),"
			+ DELETED		+ " INTEGER,"
			+ CHANGED 		+ " INTEGER);";
	
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE	+ "/" + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TABLE_NAME;

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.d(TABLE_NAME, "onCreate...");
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TodoTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}