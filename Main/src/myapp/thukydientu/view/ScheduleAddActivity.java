package myapp.thukydientu.view;

import java.util.Calendar;

import myapp.thukydientu.R;
import myapp.thukydientu.database.ScheduleTable;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.provider.TKDTProvider;
import myapp.thukydientu.util.ScheduleUtils;
import myapp.thukydientu.util.TaleTimeUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class ScheduleAddActivity extends Activity {

	private TextView tViewDayName;
	private Button btnTime;
	private EditText editSchoolName;
	private EditText editClassName;
	private EditText editSubject;
	private Button btnAdd;

	private Schedule mSchedule;
	private boolean isEdit;
	private long oldId;
	private int mDateName;
	private Calendar mCalendar;
	
	ProgressDialog mLoading;

	static final int TIME_PICKER_DIALOG = 0;
	static final int CONFIRM_DIALOG = 1;
	static final int MISSING_INFO_DIALOG = 2;
	static final int INSERT_ERROR_DIALOG = 3;
	static final int CONNECT_ERROR_DIALOG = 4;
	static final int INSERT_SUCCESS_DIALOG = 5;
	static final int REQUEST_TO_UPDATE = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_add);

		mSchedule = new Schedule();
		
		// lấy các view dựa vào id
		tViewDayName = (TextView) findViewById(R.id.date_name);
		btnTime = (Button) findViewById(R.id.time);
		editSchoolName = (EditText) findViewById(R.id.school);
		editClassName = (EditText) findViewById(R.id.class_name);
		editSubject = (EditText) findViewById(R.id.subject);
		btnAdd = (Button) findViewById(R.id.add);
		
		Spinner lessons = (Spinner) findViewById(R.id.lesson_spinner);
	    ArrayAdapter<CharSequence> lessonAdapter = ArrayAdapter.createFromResource(
	            this, R.array.lessons, android.R.layout.simple_spinner_item);
	    lessonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    lessons.setAdapter(lessonAdapter);
	    lessons.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				final String lessons = (String) parent.getItemAtPosition(position);
				mSchedule.setLessons(Integer.parseInt(lessons));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	    
	    Spinner lesson_duration = (Spinner) findViewById(R.id.lesson_duration_spinner);
	    ArrayAdapter<CharSequence> adapterDuration = ArrayAdapter.createFromResource(
	            this, R.array.lesson_duration, android.R.layout.simple_spinner_item);
	    adapterDuration.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    lesson_duration.setAdapter(adapterDuration);
	    lesson_duration.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				final String lessonDuration =  (String) parent.getItemAtPosition(position);
				mSchedule.setLessonDuration(Integer.parseInt(lessonDuration));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});


		final Bundle bundle = getIntent().getExtras();
		oldId = bundle.getLong(IConstants._ID);
		if (oldId == 0) {
			isEdit = false;
			mDateName = bundle.getInt(ScheduleTable.DATE_NAME);
			tViewDayName.setText(TaleTimeUtils.getDayOfWeekString(mDateName));
			
			btnTime.setText(TaleTimeUtils.getTimeLable(this, Calendar.getInstance()));
			btnAdd.setText("Thêm");
		} else {
			isEdit = true;
			showAlreadyData();
			btnAdd.setText("Lưu");
		}

		// thêm sự kiện click khi click vào button Thêm
		btnTime.setOnClickListener(setTimeClicked());
		btnAdd.setOnClickListener(addButtonClicked());
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
			mCalendar = TaleTimeUtils.createCalendar(hour, minute);
			btnTime.setText(TaleTimeUtils.getTimeLable(ScheduleAddActivity.this, mCalendar));
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_PICKER_DIALOG: {
			return new TimePickerDialog(this, mTimeSetListener, 0, 0, true);
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
									int result = ScheduleUtils.update(ScheduleAddActivity.this, mSchedule, oldId);
									if (result == ScheduleUtils.SUCCESS) {
										Intent intent = new Intent();
										intent.putExtra(IConstants.Results.RESULT_RETURN, IConstants.Results.UPDATE_SUCCESS);
										setResult(IConstants.Results.RESULT_OK, intent);
										finish();
									}
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
			builder.setTitle("Lỗi!")
					.setIcon(android.R.drawable.ic_dialog_alert)
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
		
		case INSERT_SUCCESS_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Thành Công!")
					.setMessage("Đã thêm thành công")
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

	private void showAlreadyData() {
		Cursor cursor = getCursor(oldId);
		if (cursor.moveToFirst()) {
			do {
				mDateName = cursor.getInt(ScheduleTable.DATE_NAME_COLUMN_INDEX);
				tViewDayName.setText(TaleTimeUtils.getDayOfWeekString(mDateName));
				String timeString = cursor.getString(ScheduleTable.TIME_COLUMN_INDEX);
				mCalendar = TaleTimeUtils.createCalendarByTimeString(timeString);
				btnTime.setText(TaleTimeUtils.getTimeLable(ScheduleAddActivity.this, mCalendar));
				editSchoolName.setText(cursor.getString(ScheduleTable.SCHOOL_COLUMN_INDEX));
				editClassName.setText(cursor.getString(ScheduleTable.CLASS_COLUMN_INDEX));
				editSubject.setText(cursor.getString(ScheduleTable.SUBJECT_COLUMN_INDEX));
			} while (cursor.moveToNext());
		}
	}

	private Cursor getCursor(long Id) {
		Uri uriId = ContentUris.withAppendedId(TKDTProvider.SCHEDULE_CONTENT_URI, Id);
		return managedQuery(uriId, ScheduleTable.PROJECTION, null, null, null);
	}
	private OnClickListener setTimeClicked() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_PICKER_DIALOG);
			}
		};
	}

	private OnClickListener addButtonClicked() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isInputEmpty()) {
					showDialog(MISSING_INFO_DIALOG);
				} else {
					mSchedule.setDayName(mDateName);
					mSchedule.setTime(TaleTimeUtils.getTimeStringByCalendar(mCalendar));
					mSchedule.setSchoolName(editSchoolName.getText().toString());
					mSchedule.setClassName(editClassName.getText().toString());
					mSchedule.setSubject(editSubject.getText().toString());
					mSchedule.setChanged(1);
					
					if (isEdit) {
						mSchedule.setDeleted(1);
						ScheduleUtils.update(ScheduleAddActivity.this, mSchedule, oldId);
					}
					mSchedule.setDeleted(0);
					final int result = ScheduleUtils.insert(ScheduleAddActivity.this, mSchedule);
										
					if (result == ScheduleUtils.SUCCESS) {
						Intent intent = new Intent();
						intent.putExtra(IConstants.Results.RESULT_RETURN, IConstants.Results.INSERT_SUCCESS);
						setResult(IConstants.Results.RESULT_OK, intent);
						finish();
					} else if (result == ScheduleUtils.FAIL)
						showDialog(INSERT_ERROR_DIALOG);
					else if (result == ScheduleUtils.REQUEST_TO_UPDATE)
						showDialog(REQUEST_TO_UPDATE);
				}
			}
		};
	}
	
	private boolean isInputEmpty() {
		if (TextUtils.isEmpty(editSchoolName.getText().toString())
			|| TextUtils.isEmpty(editClassName.getText().toString())
			|| TextUtils.isEmpty(editSubject.getText().toString()))
			return true;
		return false;
	}
	
}
