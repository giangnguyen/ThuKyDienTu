package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.util.TimeUtils;
import myapp.thukydientu.util.WebservicesUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomeActivity extends Activity {

	public static final int DIALOG_INFORM_INPUT = 1;
	public static final int DIALOG_SHARE = 2;

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

			Button submit = (Button) informView.findViewById(R.id.submit);
			submit.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					final String titleString = title.getText().toString();
					final String contentString = content.getText().toString();
					Toast.makeText(
							HomeActivity.this,
							"title: " + titleString + " content: "
									+ contentString, Toast.LENGTH_LONG).show();
				}
			});

			builder.setView(informView);

			return builder.create();

		case DIALOG_SHARE:

			View shareView = LayoutInflater.from(this).inflate(
					R.layout.share_dialog, null);

			builder.setTitle(R.string.share);
			builder.setView(shareView);

			return builder.create();
		default:
			return null;
		}
	}

	public void shareLocation() {
		final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		LocationListener locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onLocationChanged(Location location) {
				Log.d("Check Location: ", "latitude: " + location.getLatitude());
				Log.d("Check Location: ",
						"longtitude: " + location.getLongitude());
				locationManager.removeUpdates(this);
			}
		};
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		Location lc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		final double latitude = lc.getLatitude();
		final double longitude = lc.getLongitude();
		WebservicesUtils.addLocation(MainActivity.sUserId, latitude, longitude, false, TimeUtils.convert2String14(System.currentTimeMillis()));

	}

}
