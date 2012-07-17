package myapp.thukydientu.database;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleTable {

	public static final String TABLE_NAME = "schedule";

	// schedule's constants
	public static final String _ID = "_id";
	public static final String DATE_SET = "dateset";
	public static final String CREATED = "created";
	public static final String DATE_NAME = "datename";
	public static final String TIME = "time";
	public static final String SUBJECT = "subject";
	public static final String CLASS = "class";
	public static final String SCHOOL = "school";
	public static final String MODIFIED = "modified";
	public static final String CHANGED = "changed";
	public static final String LESSON = "lesson";
	public static final String LESSON_DURATION = "lesson_duration";
	public static final String DELETED = "deleted";

	public static final String[] PROJECTION = { 
			_ID, // 0
			DATE_NAME, // 1
			DATE_SET, // 2
			TIME, // 3
			SUBJECT, // 4
			CLASS, // 5
			SCHOOL, // 6
			MODIFIED, // 7
			CHANGED, // 8
			LESSON, // 9
			LESSON_DURATION, // 10
			DELETED // 11
	};

	public static final int ID_COLUMN_INDEX = 0;
	public static final int DATE_NAME_COLUMN_INDEX = 1;
	public static final int DATE_SET_COLUMN_INDEX = 2;
	public static final int TIME_COLUMN_INDEX = 3;
	public static final int SUBJECT_COLUMN_INDEX = 4;
	public static final int CLASS_COLUMN_INDEX = 5;
	public static final int SCHOOL_COLUMN_INDEX = 6;
	public static final int MODIFIED_COLUMN_INDEX = 7;
	public static final int CHANGE_COLUMN_INDEX = 8;
	public static final int LESSON_COLUMN_INDEX = 9;
	public static final int LESSON_DURATION_COLUMN_INDEX = 10;
	public static final int DELETED_COLUMN_INDEX = 11;

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" 
			+ _ID + " INTEGER primary key autoincrement," 
			+ DATE_NAME 		+ " INTEGER,"
			+ TIME 				+ " VARCHAR(6),"
			+ SUBJECT 			+ " VARCHAR(250),"
			+ CLASS 			+ " VARCHAR(250),"
			+ SCHOOL 			+ " VARCHAR(250),"
			+ DATE_SET			+ " VARCHAR(14),"
			+ MODIFIED			+ " VARCHAR(14),"
			+ DELETED			+ " INTEGER,"
			+ LESSON			+ " INTEGER,"
			+ LESSON_DURATION	+ " INTEGER,"
			+ CHANGED 			+ " INTEGER);";

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + TABLE_NAME;
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + TABLE_NAME;

	public static void onCreate(SQLiteDatabase database) {

		database.execSQL(DATABASE_CREATE);
		Log.d(TABLE_NAME, "onCreate...");
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {

		Log.w(ScheduleTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(database);
	}
}