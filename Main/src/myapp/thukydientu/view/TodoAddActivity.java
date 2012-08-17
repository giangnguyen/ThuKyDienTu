package myapp.thukydientu.view;

import java.util.Calendar;

import myapp.thukydientu.R;
import myapp.thukydientu.database.TodoTable;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.provider.TKDTProvider;
import myapp.thukydientu.util.TaleTimeUtils;
import myapp.thukydientu.util.TodoUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class TodoAddActivity extends Activity {

	EditText mEvent;
	EditText mDescription;
	Button mDateView;
	Button mStartTimeView;
	Button mEndTimeView;
	Button mAdd;
	CheckBox mAlarm;
	Calendar mCalendarStart;
	Calendar mCalendarEnd;
	Todo mTodo;
	
	Bundle mBundle;
	
	int mDateFlag;
	boolean Flag_Add;
	
	int mYear;
	int mMonth;
	int mDate;
	
	int userId;
	long mTodoId;
	
	private static final int TIME_PICKER_DIALOG = 1;
	private static final int MISSING_INFO_DIALOG = 2;
	private static final int CONNECT_ERROR_DIALOG = 3;
	private static final int INSERT_ERROR_DIALOG = 4;
	private static final int UPDATE_ERROR_DIALOG = 5;
	private static final int DATE_PICKER_DIALOG = 6;
	private static final int REQUEST_TO_UPDATE = 8;
	private static final int START_FLAG = 9;
	private static final int END_FLAG = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_add);
		
		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME, MODE_PRIVATE);
		userId = prefs.getInt(IConstants.User.ID, 0);
		
		mTodo = new Todo();
		
		mEvent = (EditText) findViewById(R.id.event);
		mDescription = (EditText) findViewById(R.id.description);
		mDateView = (Button) findViewById(R.id.start_date);
		mStartTimeView = (Button) findViewById(R.id.start_time);
		mEndTimeView = (Button) findViewById(R.id.end_time);
		mAdd = (Button) findViewById(R.id.add);
		mAlarm = (CheckBox) findViewById(R.id.alarm);
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDate = c.get(Calendar.DAY_OF_MONTH);
		mCalendarStart = Calendar.getInstance();
		mCalendarStart.set(Calendar.SECOND, 0);
		mCalendarEnd = Calendar.getInstance();
		mCalendarEnd.set(Calendar.SECOND, 0);
		
		mDateView.setText(TaleTimeUtils.getDateLable(TodoAddActivity.this, mCalendarStart));
		mStartTimeView.setText(TaleTimeUtils.getTimeLable(TodoAddActivity.this, mCalendarStart));
		mEndTimeView.setText(TaleTimeUtils.getTimeLable(TodoAddActivity.this, mCalendarEnd));
		
		mBundle = getIntent().getExtras();
		if (mBundle != null) {
			final long id = mBundle.getLong(IConstants._ID);
			Uri uriId = ContentUris.withAppendedId(TKDTProvider.TODO_CONTENT_URI, id);
			Cursor cursor = managedQuery(uriId, TodoTable.PROJECTION, null, null, null);
			fillExistData(cursor);
			Flag_Add = false;
		} else 
			Flag_Add = true;
		
		mDateView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDateFlag = START_FLAG;
				showDialog(DATE_PICKER_DIALOG);
			}
		});
		
		mStartTimeView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDateFlag = START_FLAG;
				showDialog(TIME_PICKER_DIALOG);
			}
		});
		
		mEndTimeView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDateFlag = END_FLAG;
				showDialog(TIME_PICKER_DIALOG);
			}
		});
		
		mAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isInputFieldEmpty())
					showDialog(MISSING_INFO_DIALOG);
				else {
					mTodo.setDateStart(TaleTimeUtils.getDateStringByCalendar(mCalendarStart));
					mTodo.setDateEnd(TaleTimeUtils.getDateStringByCalendar(mCalendarStart));
					mTodo.setTimeFrom(TaleTimeUtils.getTimeStringByCalendar(mCalendarStart));
					mTodo.setTimeUntil(TaleTimeUtils.getTimeStringByCalendar(mCalendarEnd));
					mTodo.setTitle(mEvent.getText().toString());
					mTodo.setWork(mDescription.getText().toString());
					mTodo.setAlarm(mAlarm.isChecked() ?  1: 0);
					
					if (!Flag_Add) {
						mTodo.setId(mBundle.getLong(IConstants._ID));
						TodoUtils.delete(TodoAddActivity.this, mTodo);
					}
					mTodo.setChanged(1);
					mTodo.setDeleted(0);
					TodoUtils.insert(TodoAddActivity.this, mTodo);
//					long id = TodoUtils.getEventIdByTodo(TodoAddActivity.this, mTodo);
//					Log.d("TodoAddActivity", "eventId: " + id);
					finish();
				}
			}
		});
		
	}
	
	private void fillExistData(Cursor cursor) {
		if (!cursor.moveToFirst()) 
			return;
		mEvent.setText(cursor.getString(TodoTable.TITLE_COLUMN_INDEX));
		mDescription.setText(cursor.getString(TodoTable.WORK_COLUMN_INDEX));
		
		final String startDateString = cursor.getString(TodoTable.DATE_START_COLUMN_INDEX);
		mCalendarStart = TaleTimeUtils.createCalendarByDateString(startDateString);
		mDateView.setText(TaleTimeUtils.getDateLable(this, mCalendarStart));
		mStartTimeView.setText(TaleTimeUtils.getTimeLable(this, mCalendarStart));
		
		final String endDateString = cursor.getString(TodoTable.DATE_END_COLUMN_INDEX);
		mCalendarEnd = TaleTimeUtils.createCalendarByDateString(endDateString);
		mEndTimeView.setText(TaleTimeUtils.getTimeLable(this, mCalendarEnd));
		mAlarm.setChecked(cursor.getInt(TodoTable.ALAMR_COLUMN_INDEX) == 0 ? false : true);
		mAdd.setText(getString(R.string.save));
	}
	
	
	private boolean isInputFieldEmpty() {
		if (TextUtils.isEmpty(mEvent.getText().toString()) 
			|| TextUtils.isEmpty(mDescription.getText().toString())
			)
				return true;
		return false;
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDate = dayOfMonth;
			mCalendarStart.set(mYear, mMonth, mDate);
			mCalendarEnd.set(mYear, mMonth, mDate);
			mDateView.setText(TaleTimeUtils.getDateLable(TodoAddActivity.this, mCalendarStart));
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
			if (mDateFlag == START_FLAG) {
				mCalendarStart.set(Calendar.HOUR_OF_DAY, hour);
				mCalendarStart.set(Calendar.MINUTE, minute);
				mCalendarStart.set(Calendar.SECOND, 0);
				mStartTimeView.setText(TaleTimeUtils.getTimeLable(TodoAddActivity.this, mCalendarStart));
			} else {
				mCalendarEnd.set(Calendar.HOUR_OF_DAY, hour);
				mCalendarEnd.set(Calendar.MINUTE, minute);
				mCalendarEnd.set(Calendar.SECOND, 0);
				mEndTimeView.setText(TaleTimeUtils.getTimeLable(TodoAddActivity.this, mCalendarEnd));
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_PICKER_DIALOG: {
			return new TimePickerDialog(this, mTimeSetListener, mCalendarStart.get(Calendar.HOUR_OF_DAY), mCalendarStart.get(Calendar.MINUTE), true);
		}
		case DATE_PICKER_DIALOG: {
			return new DatePickerDialog(TodoAddActivity.this, mDateSetListener, mYear, mMonth, mDate);
		}
		case REQUEST_TO_UPDATE: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Trùng mốc thời gian!")
					.setMessage("Mốc thời gian đã được thiết lập!\n" +
							"Bạn có muốn cập nhật lại không?")
					.setCancelable(false)
					.setPositiveButton("Đồng ý",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									long Id = TodoUtils.getId(TodoAddActivity.this, mTodo.getDateStart(), mTodo.getTimeFrom(), mTodo.getTimeUntil());
									mTodo.setId(Id);
									TodoUtils.update(TodoAddActivity.this, mTodo);
								}
							})
					.setNegativeButton("Hủy",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
			return builder.create();
		}
		case MISSING_INFO_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Thông tin nhập rỗng!")
					.setMessage(
							"Thông tin không được bỏ trống!\nBạn vui lòng nhập lại.")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
			return builder.create();
		}
		case CONNECT_ERROR_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Lỗi kết nối!")
					.setMessage(
							"Vui lòng kiểm tra kết nối internet trước khi thực hiện thao tác này")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
			return builder.create();
		}
		case INSERT_ERROR_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Thêm sự kiện thất bại!")
					.setMessage("Hệ thống hiện tại không thể thực hiện thao tác này")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
			return builder.create();
		}
		case UPDATE_ERROR_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Cập nhật sự kiên thất bại!")
					.setMessage("Hệ thống hiện tại không thể thực hiện thao tác này")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});
			return builder.create();
		}
		}
		return null;
	}
	
}
