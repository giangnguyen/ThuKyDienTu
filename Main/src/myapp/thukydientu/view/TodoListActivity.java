package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.adapter.TodoAdapter;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.service.SyncService;
import myapp.thukydientu.util.TimeUtils;
import myapp.thukydientu.util.TodoUtils;
import myapp.thukydientu.util.WebservicesUtils;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class TodoListActivity extends ListActivity {

	private TodoAdapter mAdapter;
	private int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);

		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME,
				MODE_PRIVATE);
		userId = prefs.getInt(IConstants.User.ID, 0);

		mAdapter = new TodoAdapter(this, userId);
		setListAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		createMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0: {
			Intent todoAdd = new Intent(
					"myapp.thukygiangvien.TODO_ADD_ACTIVITY");
			startActivity(todoAdd);
			break;
		}
		case 1: {
			TodoUtils.delete(TodoListActivity.this, -1);
			break;
		}
		case 2:
			new SyncService(this, userId, IConstants.DataType.TODO);
			break;
		case 3:
			shareLocation();
			break;
		}
		return true;

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
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		Location lc = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double latitude = lc.getLatitude();
		double longitude = lc.getLongitude();
		WebservicesUtils.addLocation(userId, latitude, longitude, false,
				TimeUtils.convert2String14(System.currentTimeMillis()));

	}

	private void createMenu(Menu menu) {

		MenuItem addItem = menu.add(0, 0, 0, "Thêm");
		addItem.setIcon(R.drawable.ic_menu_add_picture);
		MenuItem deleteItem = menu.add(0, 1, 1, "Xóa tất cả");
		deleteItem.setIcon(R.drawable.ic_menu_clear_list);
		MenuItem sync = menu.add(0, 2, 2, "Đồng Bộ");
		sync.setIcon(R.drawable.sync);
		MenuItem shareLocation = menu.add(0, 3, 3, "Chia Sẽ Vị Trí");
		shareLocation.setIcon(android.R.drawable.ic_menu_mylocation);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Null out the group cursor. This will cause the group cursor and all
		// of the child cursors
		// to be closed.
		mAdapter.changeCursor(null);
		mAdapter = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	startActivity(new Intent(this, MainActivity.class));
	    	finish();
	        return true;
	    }
	    return false;
	}
}
