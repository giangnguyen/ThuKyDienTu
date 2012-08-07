package myapp.thukydientu.view;

import java.util.Calendar;

import myapp.thukydientu.R;
import myapp.thukydientu.controller.Connection;
import myapp.thukydientu.util.WebservicesUtils;
import myapp.thukydientu.util.XMLUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class RegisterActivity extends Activity {

	EditText mUsername;
	EditText mPassword;
	EditText mFullname;
	EditText mEmail;
	EditText mPhoneNumber;
	EditText mAddress;
	EditText mDayOfBirth;
	RadioButton mMale;
	RadioButton mFemale;
	Spinner mFaculty;
	Button mRegister;

	private final int DATE_DIALOG_ID = 1;
	private final int INPUT_ERROR_DIALOG_ID = 2;
	private final int DUPLICATED_USERNAME_DIALOG_ID = 3;
	private final int DUPLICATED_EMAIL_DIALOG_ID = 4;
	private final int SUCCESS_DIALOG_ID = 5;
	private final int CONNECTION_FAIL_DIALOG = 6;
	private final String RESULT_FAIL_EMAIL = "DUPLICATED EMAIL";
	private final String RESULT_FAIL_USERNAME = "DUPLICATED USERNAME";
	private final String RESULT_SUCCESS = "SUCCESS";
	
	private ProgressDialog mLoading;
	private int mYear;
	private int mMonth;
	private int mDay;
	protected int falcuty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		mUsername = (EditText) findViewById(R.id.username);
		mPassword = (EditText) findViewById(R.id.password);
		mFullname = (EditText) findViewById(R.id.fullname);
		mEmail = (EditText) findViewById(R.id.email);
		mPhoneNumber = (EditText) findViewById(R.id.phone);
		mAddress = (EditText) findViewById(R.id.address);
		mDayOfBirth = (EditText) findViewById(R.id.dayofbirth);
		mMale = (RadioButton) findViewById(R.id.gender_male);
		mFemale = (RadioButton) findViewById(R.id.gender_female);
		mFaculty = (Spinner) findViewById(R.id.faculty_spinner);
		mRegister = (Button) findViewById(R.id.register);
		
		mRegister.setOnClickListener(registerClicked);

		mDayOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (v.isFocused())
					showDialog(DATE_DIALOG_ID);
			}
		});
		mDayOfBirth.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		falcuty = 1;
	    ArrayAdapter<CharSequence> lessonAdapter = ArrayAdapter.createFromResource(
	            this, R.array.lessons, android.R.layout.simple_spinner_item);
	    lessonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mFaculty.setAdapter(lessonAdapter);
	    mFaculty.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				falcuty = position + 1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	    
		final Calendar c = Calendar.getInstance();
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mMonth = c.get(Calendar.MONTH);
		mYear = c.get(Calendar.YEAR);
	}

	private View.OnClickListener registerClicked = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (isAllFieldAvailable()) {
				if (Connection.isInternetConnected(RegisterActivity.this)) {
					RegisterTask registerTask = new RegisterTask();
					registerTask.execute();
				} else 
					showDialog(CONNECTION_FAIL_DIALOG);
			} else {
				showDialog(INPUT_ERROR_DIALOG_ID);
			}
		}
	};
	private boolean isAllFieldAvailable() {
		return !TextUtils.isEmpty(mUsername.getText().toString())
				&& !TextUtils.isEmpty(mPassword.getText().toString())
				&& !TextUtils.isEmpty(mFullname.getText().toString())
				&& !TextUtils.isEmpty(mEmail.getText().toString())
				&& !TextUtils.isEmpty(mPhoneNumber.getText().toString())
				&& !TextUtils.isEmpty(mAddress.getText().toString())
				&& !TextUtils.isEmpty(mDayOfBirth.getText().toString());
	}
	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			
			mDayOfBirth.setText("" + mDay + "-" + (mMonth + 1) + "-" + mYear);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	    case DATE_DIALOG_ID:
	        return new DatePickerDialog(this,
	                    mDateSetListener,
	                    mYear, mMonth, mDay);
	    case INPUT_ERROR_DIALOG_ID: 
	    {
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
	    case DUPLICATED_USERNAME_DIALOG_ID:
	    {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Trùng tên đăng nhập")
					.setMessage(
							"Tên đăng nhập đã được đăng ký\nBạn vui lòng chọn tên đăng nhập khác.")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mUsername.setText("");
									mPassword.setText("");
									mUsername.requestFocus();
								}
							});
			return builder.create();
	    }
	    case DUPLICATED_EMAIL_DIALOG_ID:
	    {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Trùng email")
					.setMessage(
							"Email đã được đăng ký\nBạn vui lòng chọn email khác.")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									mEmail.setText("");
									mPassword.setText("");
									mPassword.requestFocus();
								}
							});
			return builder.create();
	    }
	    case SUCCESS_DIALOG_ID:
	    {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Thành công!")
					.setMessage(
							"Chúc mừng bạn đã đăng ký thành công.")
					.setNeutralButton("Chấp nhận",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
			return builder.create();
	    }
	    case CONNECTION_FAIL_DIALOG: {
			// Neu dang show dialog thi khong lam gi het
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RegisterActivity.this);
			builder.setTitle("Connection failed!");
			builder.setMessage("Kiểm tra lại kết nối internet trước khi thực hiện thao tác này!");
			builder.setPositiveButton("Đồng Ý", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setCancelable(false);
			return builder.create();
		}
	    }
	    return null;
	}
	
	public void setLoading(Context context, boolean loading) {
		if (loading) {
			if (context == null || mLoading != null)
				return;
			mLoading = ProgressDialog.show(context, null, context.getString(R.string.loading), true, true);
		} else if (mLoading != null) {
			mLoading.dismiss();
			mLoading = null;
		}
	}
	
	private class RegisterTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			String xmlResult = WebservicesUtils.register(
					mUsername.getText().toString(), 
					mPassword.getText().toString(), 
					mFullname.getText().toString(), 
					mEmail.getText().toString(), 
					mPhoneNumber.getText().toString(), 
					mAddress.getText().toString(), 
					new StringBuilder().append(mYear).append(mMonth).append(mDay).toString(), 
					mMale.isChecked() ? 1 : 0, 
					1, 
					falcuty);
			if (xmlResult != null) {
				if (XMLUtils.getRegisterResult(xmlResult) == 0) {
					return RESULT_FAIL_USERNAME;
				} else if (XMLUtils.getRegisterResult(xmlResult) == -1) 
					return RESULT_FAIL_EMAIL;
			}
			return RESULT_SUCCESS;
		}

		@Override
		protected void onPostExecute(String result) {
			setLoading(RegisterActivity.this, false);
			if (result == RESULT_FAIL_EMAIL)
				showDialog(DUPLICATED_EMAIL_DIALOG_ID);
			else if (result == RESULT_FAIL_USERNAME)
				showDialog(DUPLICATED_USERNAME_DIALOG_ID);
			else {
				Toast.makeText(RegisterActivity.this, "Đăng Ký Thành Công!", Toast.LENGTH_LONG).show();
				finish();
			}
		}

		@Override
		protected void onPreExecute() {
			setLoading(RegisterActivity.this, true);
		}
	}
	
}
