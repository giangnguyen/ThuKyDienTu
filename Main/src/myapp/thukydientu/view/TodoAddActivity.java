package myapp.thukydientu.view;

import java.util.Calendar;

import myapp.thukydientu.R;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.Todo;
import myapp.thukydientu.util.TimeUtils;
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
	Calendar mDateStart;
	Calendar mDateEnd;
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
		mDateStart = Calendar.getInstance();
		mDateStart.set(Calendar.SECOND, 0);
		mDateEnd = Calendar.getInstance();
		mDateEnd.set(Calendar.SECOND, 0);
		
		mDateView.setText(TimeUtils.getDateLable(TodoAddActivity.this, mDateStart.getTimeInMillis()));
		mStartTimeView.setText(TimeUtils.getTimeLable(TodoAddActivity.this, mDateStart.getTimeInMillis()));
		mEndTimeView.setText(TimeUtils.getTimeLable(TodoAddActivity.this, mDateEnd.getTimeInMillis()));
		
		mBundle = getIntent().getExtras();
		if (mBundle != null) {
			Uri uriId = ContentUris.withAppendedId(IConstants.event.CONTENT_URI, mBundle.getLong(IConstants._ID));
			Cursor cursor = managedQuery(uriId, IConstants.event.PROJECTION, null, null, null);
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
					mTodo.setDateStart(TimeUtils.getDate(mDateStart.getTimeInMillis()));
					mTodo.setDateEnd(TimeUtils.getDate(mDateStart.getTimeInMillis()));
					mTodo.setTimeFrom(TimeUtils.getTime(mDateStart.getTimeInMillis()));
					mTodo.setTimeUntil(TimeUtils.getTime(mDateEnd.getTimeInMillis()));
					mTodo.setTitle(mEvent.getText().toString());
					mTodo.setWork(mDescription.getText().toString());
					mTodo.setAlarm(mAlarm.isChecked() ?  1: 0);
					
					if (!Flag_Add) {
						TodoUtils.delete(TodoAddActivity.this, mBundle.getLong(IConstants._ID));
					}
					mTodo.setChanged(1);
					mTodo.setDeleted(0);
					TodoUtils.insert(TodoAddActivity.this, mTodo);
					finish();
				}
			}
		});
		
	}
	
	private void fillExistData(Cursor cursor) {
		if (!cursor.moveToFirst()) 
			return;
		mEvent.setText(cursor.getString(IConstants.event.TITLE_COLUMN_INDEX));
		mDescription.setText(cursor.getString(IConstants.event.DESCRIPTION_COLUMN_INDEX));
		final long startTime = cursor.getLong(IConstants.event.DATE_START_COLUMN_INDEX);
		mDateView.setText(TimeUtils.getDateLable(this, startTime));
		mStartTimeView.setText(TimeUtils.getTimeLable(this, startTime));
		final long endTime = cursor.getLong(IConstants.event.DATE_END_COLUMN_INDEX);
		mEndTimeView.setText(TimeUtils.getTimeLable(this, endTime));
		mAlarm.setChecked(cursor.getInt(IConstants.event.HAS_ALARM_COLUMN_INDEX) == 0 ? false : true);
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
			mDateStart.set(mYear, mMonth, mDate);
			mDateEnd.set(mYear, mMonth, mDate);
			mDateView.setText(TimeUtils.getDateLable(TodoAddActivity.this, mDateStart.getTimeInMillis()));
		}
	};
	
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
			if (mDateFlag == START_FLAG) {
				mDateStart.set(Calendar.HOUR_OF_DAY, hour);
				mDateStart.set(Calendar.MINUTE, minute);
				mDateStart.set(Calendar.SECOND, 0);
				mStartTimeView.setText(TimeUtils.getTimeLable(TodoAddActivity.this, mDateStart.getTimeInMillis()));
			} else {
				mDateEnd.set(Calendar.HOUR_OF_DAY, hour);
				mDateEnd.set(Calendar.MINUTE, minute);
				mDateEnd.set(Calendar.SECOND, 0);
				mEndTimeView.setText(TimeUtils.getTimeLable(TodoAddActivity.this, mDateEnd.getTimeInMillis()));
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_PICKER_DIALOG: {
			return new TimePickerDialog(this, mTimeSetListener, mDateStart.get(Calendar.HOUR_OF_DAY), mDateStart.get(Calendar.MINUTE), true);
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
									TodoUtils.update(TodoAddActivity.this, mTodo, Id);
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
