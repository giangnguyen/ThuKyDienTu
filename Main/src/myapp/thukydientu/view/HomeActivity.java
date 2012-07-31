package myapp.thukydientu.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import myapp.thukydientu.R;
import myapp.thukydientu.adapter.ScheduleShareAdapter;
import myapp.thukydientu.model.Schedule;
import myapp.thukydientu.service.ShareLocationService;
import myapp.thukydientu.util.QREncodeUtil;
import myapp.thukydientu.util.TimeUtils;
import myapp.thukydientu.util.WebservicesUtils;
import myapp.thukydientu.util.XMLUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	public static final int DIALOG_INFORM_INPUT = 1;
	public static final int DIALOG_SHARE = 2;
	public static final int DIALOG_OPTION_LOCATION = 3;
	public static final int DIALOG_OPTION_TODO = 4;
	public static final int DIALOG_SCHEDULE_SHARE = 5;
	public static final int DIALOG_DATE_PICKER = 6;
	
	private static final int SCAN_REQUEST_CODE = 100;

	private Calendar mDateStart;
	private Calendar mDateEnd;
	
	private Button dateStartButton;
	private Button dateEndButton;
	
	private boolean isDateStartPick;
	protected final String tag = HomeActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		mDateStart = Calendar.getInstance();
		mDateEnd = Calendar.getInstance();
		
		View share = findViewById(R.id.share);
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_SHARE);
			}
		});

		View inform = findViewById(R.id.inform);
		inform.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DIALOG_INFORM_INPUT);
			}
		});
		
		View scanQR = findViewById(R.id.scan);
		scanQR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, SCAN_REQUEST_CODE);
			}
		});

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		switch (id) {

		case DIALOG_INFORM_INPUT:

			View informView = getLayoutInflater().inflate(
					R.layout.inform, null);

			final EditText title = (EditText) informView
					.findViewById(R.id.title);
			final EditText content = (EditText) informView
					.findViewById(R.id.content);

			final Button submit = (Button) informView.findViewById(R.id.submit);
			submit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_INFORM_INPUT);
					final String titleString = title.getText().toString();
					final String contentString = content.getText().toString();
					new InformTask(titleString, contentString).execute();
				}
			});

			builder.setView(informView);

			return builder.create();

		case DIALOG_SHARE:

			View shareView = getLayoutInflater().inflate(R.layout.share_dialog, null);

			Button shareLocation = (Button) shareView.findViewById(R.id.location);
			shareLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_SHARE);
					showDialog(DIALOG_OPTION_LOCATION);
				}
			});
			
			Button todo = (Button) shareView.findViewById(R.id.todo);
			todo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_SHARE);
					showDialog(DIALOG_OPTION_TODO);
					}
			});
			
			Button schedule = (Button) shareView.findViewById(R.id.schedule);
			schedule.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_SHARE);
					WebservicesUtils.getSchedule(MainActivity.sUserId, -1);
				}
			});
			
			builder.setTitle(R.string.share);
			builder.setView(shareView);

			return builder.create();
			
		case DIALOG_OPTION_LOCATION:
			
			final View optionLocationView = getLayoutInflater().inflate(R.layout.location_option_dialog, null);
			final CheckBox repeat = (CheckBox) optionLocationView.findViewById(R.id.repeat);
			final EditText minuteView = (EditText) optionLocationView.findViewById(R.id.minute);

			repeat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked)
						minuteView.setEnabled(true);
					else 
						minuteView.setEnabled(false);
				}
			});
			
			builder.setTitle(R.string.share_location);
			builder.setView(optionLocationView);
			builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int minutes = 0; 
					if (!TextUtils.isEmpty(minuteView.getText()))
						minutes = Integer.parseInt(minuteView.getText().toString());
					new ShareLocationService(HomeActivity.this, minutes);
				}
			});
			return builder.create();
			
		case DIALOG_SCHEDULE_SHARE:
			final View scheduleShareView = getLayoutInflater().inflate(R.layout.schedule_share_dialog, null);
			final View loading = scheduleShareView.findViewById(R.id.loading);
			final ExpandableListView listScheduleShare = (ExpandableListView) scheduleShareView.findViewById(R.id.schedule_share);
			
			ScheduleShareAdapter scheduleShareAdapter = new ScheduleShareAdapter(this);
			listScheduleShare.setAdapter(scheduleShareAdapter);
			
			new ListScheduleTask(scheduleShareAdapter, loading, listScheduleShare).execute(1,-1);
			
			builder.setView(scheduleShareView);
			
			return builder.create();
			
		case DIALOG_OPTION_TODO:
			
			final View optionTodoView = LayoutInflater.from(this).inflate(R.layout.todo_share_dialog, null);
			dateStartButton = (Button) optionTodoView.findViewById(R.id.start_date);
			dateEndButton = (Button) optionTodoView.findViewById(R.id.end_date);

			dateStartButton.setText(TimeUtils.getDateLable(HomeActivity.this, mDateStart.getTimeInMillis()));
			dateEndButton.setText(TimeUtils.getDateLable(HomeActivity.this, mDateEnd.getTimeInMillis()));
			
			dateStartButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isDateStartPick = true;
					showDialog(DIALOG_DATE_PICKER);
				}
			});
			
			dateEndButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isDateStartPick = false;
					showDialog(DIALOG_DATE_PICKER);
				}
			});
			
			builder.setTitle(R.string.share_todo);
			builder.setView(optionTodoView);
			builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final String startDateString = TimeUtils.getDate(mDateStart.getTimeInMillis());
					final String endDateString = TimeUtils.getDate(mDateEnd.getTimeInMillis());
					final String table = "todo";
					final String QRText = QREncodeUtil.convertTodo2QRText(MainActivity.sUserId, table, startDateString, endDateString);
					QREncodeUtil.textEncode(HomeActivity.this, QRText);
				}
			});
			return builder.create();

		case DIALOG_DATE_PICKER:
			if (isDateStartPick)
				return new DatePickerDialog(this, dateSetListener, mDateStart.get(Calendar.YEAR), mDateStart.get(Calendar.MONTH), mDateStart.get(Calendar.DAY_OF_MONTH));
			else 
				return new DatePickerDialog(this, dateSetListener, mDateEnd.get(Calendar.YEAR), mDateEnd.get(Calendar.MONTH), mDateEnd.get(Calendar.DAY_OF_MONTH));
		default:
			return null;
		}
	}

	private DatePickerDialog.OnDateSetListener dateSetListener = new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Log.d(tag, "Year: " + year + ", Month: " + monthOfYear + ", day: " + dayOfMonth);
			if (isDateStartPick) {
				mDateStart.set(year, monthOfYear, dayOfMonth);
				dateStartButton.setText(TimeUtils.getDateLable(HomeActivity.this, mDateStart.getTimeInMillis()));
			} else { 
				mDateEnd.set(year, monthOfYear, dayOfMonth);
				dateEndButton.setText(TimeUtils.getDateLable(HomeActivity.this, mDateEnd.getTimeInMillis()));
			}
		}
	};
	
	class ListScheduleTask extends AsyncTask<Integer, Void, String> {

		private View loading;
		private View list;
		private ScheduleShareAdapter adapter;
		
		public ListScheduleTask(ScheduleShareAdapter adapter, View loading, View list) {
			this.adapter = adapter;
			this.loading = loading;
			this.list = list;
		}
		
		@Override
		protected String doInBackground(Integer... params) {
			final int userId = params[0];
			final int dateName = params[1];
			return WebservicesUtils.getQRSchedule(userId, dateName);
		}

		@Override
		protected void onPostExecute(String result) {
			if (!TextUtils.isEmpty(result)) {
				adapter.setListSchedule(XMLUtils.getSchedule(result));
				loading.setVisibility(View.GONE);
				list.setVisibility(View.VISIBLE);
			}
		}
		
		
		
	}
	class InformTask extends AsyncTask<Void, Void, String> {

		private String mTitle;
		private String mContent; 
		
		public InformTask(String title, String content) {
			this.mTitle = title;
			this.mContent = content;
		}
		@Override
		protected String doInBackground(Void... params) {
			return WebservicesUtils.addNotice(MainActivity.sUserId, mTitle, mContent, TimeUtils.convert2String14(System.currentTimeMillis()));
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (XMLUtils.addNoticeResult(result) == 1) 
				Toast.makeText(HomeActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
			else 
				Toast.makeText(HomeActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SCAN_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String qrtext = data.getStringExtra("SCAN_RESULT");
				// Handle successful scan
				Log.d(tag, "qrtext: " + qrtext);
				final int userId = QREncodeUtil.getUserIdFromQRText(qrtext);
				final String table = QREncodeUtil.getTableFromQRText(qrtext);
				final String dateStart = QREncodeUtil.getDateStartFromQRText(qrtext);
				final String dateEnd = QREncodeUtil.getDateEndFromQRText(qrtext);
				
				final String result = WebservicesUtils.getTodoShare(userId, table, dateStart, dateEnd);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
