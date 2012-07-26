package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.service.ShareLocationService;
import myapp.thukydientu.util.TimeUtils;
import myapp.thukydientu.util.WebservicesUtils;
import myapp.thukydientu.util.XMLUtils;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends Activity {

	public static final int DIALOG_INFORM_INPUT = 1;
	public static final int DIALOG_SHARE = 2;
	public static final int DIALOG_OPTION_LOCATION = 3;
	
	private static final int SCAN_REQUEST_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

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

			View informView = LayoutInflater.from(this).inflate(
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

			View shareView = LayoutInflater.from(this).inflate(R.layout.share_dialog, null);

			Button shareLocation = (Button) shareView.findViewById(R.id.location);
			shareLocation.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					removeDialog(DIALOG_SHARE);
					showDialog(DIALOG_OPTION_LOCATION);
				}
			});
			
			builder.setTitle(R.string.share);
			builder.setView(shareView);

			return builder.create();
			
		case DIALOG_OPTION_LOCATION:
			
			final View optionLocationView = LayoutInflater.from(this).inflate(R.layout.location_option_dialog, null);
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
			
		default:
			return null;
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
				String contents = data.getStringExtra("SCAN_RESULT");
				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Log.d("format", format);
				Log.d("contents", contents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
