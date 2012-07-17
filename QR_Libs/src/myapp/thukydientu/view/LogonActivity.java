package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.controller.Connection;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.util.WebservicesUtils;
import myapp.thukydientu.util.XMLUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogonActivity extends Activity {

	public SharedPreferences prefs;
	private EditText mUsername;
	private EditText mPassword;
	private static final int LOGON_FAIL_DIALOG = 1;
	private static final int MISSING_INFOR_DIALOG = 2;
	private static final int CONNECTION_FAIL_DIALOG = 3;
	private static final String RESULT_OK = "OK";
	private static final String RESULT_FAIL = "FAIL";
	
	private ProgressDialog mLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = getSharedPreferences(IConstants.PREF_NAME, MODE_PRIVATE);

		boolean status = prefs.getBoolean(IConstants.LOGON_STATUS, false);

		if (status) {
			startActivity(new Intent("myapp.thukygiangvien.MAIN_ACTIVITY"));
			finish();
		}
		
		setContentView(R.layout.logon);

		Button logon = (Button) findViewById(R.id.logon);
		Button register = (Button) findViewById(R.id.register);

		logon.setOnClickListener(logonClicked);
		register.setOnClickListener(registerClicked);
	}

	private View.OnClickListener logonClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (Connection.isInternetConnected(LogonActivity.this)) {
				mUsername = (EditText) findViewById(R.id.username);
				mPassword = (EditText) findViewById(R.id.password);
				if (TextUtils.isEmpty(mUsername.getText().toString())
						|| TextUtils.isEmpty(mPassword.getText().toString())) {
					showDialog(MISSING_INFOR_DIALOG);
				} else {
					LogonTask logonTask = new LogonTask();
					logonTask.execute();	
				}
			} else {
				showDialog(CONNECTION_FAIL_DIALOG);
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		
		case LOGON_FAIL_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LogonActivity.this);
			builder.setTitle("Đăng nhập không thành công");
			builder.setMessage("Tên đăng nhập hoặc mật khẩu không đúng!");
			builder.setPositiveButton("Đồng Ý", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setCancelable(false);
			return builder.create();
		}
		
		case MISSING_INFOR_DIALOG: {
			// Neu dang show dialog thi khong lam gi het
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LogonActivity.this);
			builder.setTitle("Thiếu Thông Tin!");
			builder.setMessage("Vui lòng điền đầy đủ thông tin đăng nhập!");
			builder.setPositiveButton("Đồng Ý", new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setCancelable(false);
			return builder.create();
		}
		
		case CONNECTION_FAIL_DIALOG: {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LogonActivity.this);
			builder.setTitle("Lỗi Kết Nối!");
			builder.setMessage("Không có kết nối internet!");
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

	private View.OnClickListener registerClicked = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(LogonActivity.this, RegisterActivity.class));
		}
	};

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
	
	private class LogonTask extends AsyncTask<Void, Void, String> {

		int[] mLogonResult;
		@Override
		protected String doInBackground(Void... arg0) {
			String xmlResult = WebservicesUtils.login(mUsername.getText().toString(),
					  mPassword.getText().toString());
			mLogonResult = XMLUtils.getLoginResult(xmlResult);
//			mLogonResult = new int[] {1, 1};
			if (mLogonResult != null && mLogonResult[IConstants.LOGON_RESULT] == 1) 
					return RESULT_OK;
			return RESULT_FAIL;
		}

		@Override
		protected void onPostExecute(String result) {
			setLoading(LogonActivity.this, false);
			if (result.equalsIgnoreCase(RESULT_OK)) {
				SharedPreferences.Editor editor = prefs.edit();
				// Thiet dat trang thai dang nhap
				editor.putBoolean(IConstants.LOGON_STATUS, true);
				// Lưu lại userId
				editor.putInt(IConstants.User.ID, mLogonResult[IConstants.LOGON_USER_ID]);
				// Luu lai trang thai dang nhap
				editor.commit();
				
				startActivity(new Intent("myapp.thukygiangvien.MAIN_ACTIVITY"));
				finish();
			} else {
				showDialog(LOGON_FAIL_DIALOG);
			}
		}

		@Override
		protected void onPreExecute() {
			setLoading(LogonActivity.this, true);
		}
	}
}
