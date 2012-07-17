package myapp.thukydientu.provider;

import myapp.thukydientu.database.DayOfWeekTable;
import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.database.TodoTable;
import myapp.thukydientu.model.IConstants;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TKDTDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	public TKDTDatabaseHelper(Context context) {
		super(context, IConstants.DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(this.getClass().getName(), "Constructor...");
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		ScheduleTable.onCreate(database);
		TodoTable.onCreate(database);
		DayOfWeekTable.onCreate(database);
		Log.d(this.getClass().getName(), "onCreate...");
	}

	// Method is called during an upgrade of the database,
	// e.g. if you increase the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.d(this.getClass().getName(), "onUpgrade...");
		ScheduleTable.onUpgrade(database, oldVersion, newVersion);
		TodoTable.onUpgrade(database, oldVersion, newVersion);
		DayOfWeekTable.onUpgrade(database, oldVersion, newVersion);
	}
}
