package myapp.thukydientu.model;

import android.net.Uri;

public interface IConstants {
	
	// user infor
	static final String PREF_NAME 		= "logon_prefs";
	static final String LOGON_STATUS 	= "logon_status";
	
	static final int LOGON_RESULT 		= 0;
	static final int LOGON_USER_ID 		= 1;
	
	static final String _ID				="_id";
	
	// schedule index
	static final String RESULT			= "result";
	static final String DATABASE_NAME  	= "TKDTDatabase";
	
	class WebServices {
		public static final String TODO_ID 			= "todoid";
		public static final String SCHEDULE_ID 		= "id";
		public static final String TABLE_NAME 		= "tablename";
	}
	
	class File {
		// file's constants
		public static final String ID 				= "fileid";
		public static final String NAME 			= "filename";
		public static final String DESCRIPTION 		= "description";
		public static final String CATEGORY 		= "category";
		public static final String FORMAT 			= "format";
		public static final String SIZE 			= "size";
		public static final String LINK				= "link";
		public static final String MODE				= "isprivate";
		public static final String PATH 			= "path";
	}

	class User {
		// user's constants
		public static final String ID 				= "userid";
		public static final String USER_NAME		= "username";
		public static final String PASSWORD 		= "password";
		public static final String FULLNAME 		= "fullname";
		public static final String EMAIL 			= "email";
		public static final String PHONE_NUMBER 	= "phone";
		public static final String ADDRESS 			= "address";
		public static final String DATE_OF_BIRTH 	= "datebirth";
		public static final String GENDER 			= "gender";
		public static final String WORKPLACE 		= "workplace";
		public static final String JOB 				= "job";
	}
	
	class event {
		public static final Uri CONTENT_URI 		= Uri.parse("content://com.android.calendar/events");
		public static final int CALENDAR 			= 1;
		public static final String CALENDAR_ID		= "calendar_id";
		public static final String TITLE 			= "title";
		public static final String DESCRIPTION 		= "description";
		public static final String DATE_START 		= "dtstart";
		public static final String DATE_END 		= "dtend";
		public static final String TIMEZONE			= "eventTimezone";
		public static final String HAS_ALARM 		= "hasAlarm";
		
		public static final String PROJECTION[] = {
			IConstants._ID,
			TITLE,
			DESCRIPTION,
			DATE_START,
			DATE_END,
			HAS_ALARM,
			CALENDAR_ID
		};
		
		public static final int ID_COLUMN_INDEX = 0;
		public static final int TITLE_COLUMN_INDEX = 1;
		public static final int DESCRIPTION_COLUMN_INDEX = 2;
		public static final int DATE_START_COLUMN_INDEX = 3;
		public static final int DATE_END_COLUMN_INDEX = 4;
		public static final int HAS_ALARM_COLUMN_INDEX = 5;
		public static final int CALENDAR_ID_COLUMN_INDEX = 6;
	}
	
	class reminder {
		public static final Uri CONTENT_URI 		= Uri.parse("content://com.android.calendar/reminders");
		public static final String EVENT_ID 		= "event_id";
		public static final String METHOD 			= "method";
		public static final String MINUTES 			= "minutes";
	}
	
	class Results {
		public static final int RESULT_FAIL = -1;
		public static final int RESULT_OK = 1;
		public static final String RESULT_RETURN = "result_return";
		public static final int INSERT_SUCCESS = 2;
		public static final int UPDATE_SUCCESS = 3;
	}
	
	class Service {
		public static final String DOWNLOAD_ACTION = "download_action";
		public static final String DOWNLOAD_RESULT = "download_result";
		public static final String DOWNLOAD_ACTION_STARTED = "myapp.thukygiangvien.DOWNLOAD_ACTION_STARTED";
		public static final String DOWNLOAD_ACTION_FINISHED = "myapp.thukygiangvien.DOWNLOAD_ACTION_FINISHED";
		public static final String DOWNLOAD_ACTION_CANCELLED = "myapp.thukygiangvien.DOWNLOAD_ACTION_CANCELED";
	}
	
	class DataType {
		public static final int SCHEDULE = 1;
		public static final int TODO 	= 2;
	}
	
	class ShareLocation {
		public static final String USER_ID = "userid";
		public static final String LATITUDE = "lat";
	    public static final String LONGITUDE = "lng";
	    public static final String MODE = "isprivate";
	    public static final String CREATED = "created";
	}
	
	class Inform {
		public static final String TITLE = "title";
		public static final String BODY = "body";
		public static final String CREATED = "created";
		public static final String ISPRIVATE = "isprivate";
	}
	
	class TodoShare {
		public static final String USER_ID = "userid";
		public static final String TABLE = "table";
		public static final String DATE_START = "datestart";
		public static final String DATE_END = "dateend";
	}
}
