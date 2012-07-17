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
import android.view.Menu;
import android.view.MenuItem;

public class TodoListActivity extends ListActivity {

	private TodoAdapter mAdapter;
	private int userId;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_list);

		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME, MODE_PRIVATE);
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
		switch (item.getItemId())
		{
		case 0:
		{
			Intent todoAdd = new Intent("myapp.thukygiangvien.TODO_ADD_ACTIVITY");
			startActivity(todoAdd);
			break;
		}
		case 1:
		{
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
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
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
				Log.d("Check Location: ", "longtitude: " + location.getLongitude());
				locationManager.removeUpdates(this);
			}
		};
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		Location lc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		double latitude = lc.getLatitude();
		double longitude = lc.getLongitude();
		WebservicesUtils.addLocation(userId, latitude, longitude, false, TimeUtils.convert2String14(System.currentTimeMillis()));
		
	}
	
	private void createMenu( Menu menu ) {
		
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

        // Null out the group cursor. This will cause the group cursor and all of the child cursors
        // to be closed.
        mAdapter.changeCursor(null);
        mAdapter = null;
    }
	
//	private static final int ID_COLUMN_INDEX			= 0;
//	private static final int TITLE_COLUMN_INDEX			= 1;
//	private static final int DATE_START_COLUMN_INDEX	= 2;
//	@SuppressWarnings("unused")
//	private static final int DATE_END_COLUMN_INDEX 		= 3;
//	private static final int DESCRIPTION_COLUMN_INDEX	= 4;
//	@SuppressWarnings("unused")
//	private static final int ALARM_COLUMN_INDEX 		= 5;
//	
//	private String[] PROJECTION = {
//			IConstants._ID,					// 0
//			IConstants.event.TITLE,			// 1
//			IConstants.event.DATE_START,	// 2
//			IConstants.event.DATE_END,		// 3
//			IConstants.event.DESCRIPTION,	// 4
//			IConstants.event.HAS_ALARM		// 5
//	};
//
//	private TodoListAdapter mAdapter;
//	
//	private class TodoListAdapter extends CursorAdapter {
//
//		private class ViewHolder {
//			long _id;
//			TextView date_start;
//			TextView title;
//			TextView description;
//		}
//		
//		public TodoListAdapter(Context context, Cursor c) {
//			super(context, c);
//		}
//		
//		public ViewHolder getViewHolder(View view) {
//			ViewHolder holder = (ViewHolder) view.getTag();
//			if (holder == null) {
//				holder = new ViewHolder();
//				holder.date_start 	= (TextView) view.findViewById(R.id.date_start);
//				holder.title 		= (TextView) view.findViewById(R.id.title);
//				holder.description 	= (TextView) view.findViewById(R.id.description);
//			}
//			return holder;
//		}
//		@Override
//		public void bindView(View view, Context context, Cursor cursor) {
//			ViewHolder holder = (ViewHolder) view.getTag();
//			
//			holder._id = cursor.getLong(ID_COLUMN_INDEX);
//			holder.date_start.setText(TimeUtils.getDateLable(TodoListActivity.this, cursor.getLong(DATE_START_COLUMN_INDEX)));
//			holder.title.setText(cursor.getString(TITLE_COLUMN_INDEX));
//			holder.description.setText(cursor.getString(DESCRIPTION_COLUMN_INDEX));
//		}
//
//		@Override
//		public View newView(Context context, Cursor cursor, ViewGroup parent) {
//			LayoutInflater inflater = LayoutInflater.from(context);
//			View view = inflater.inflate(R.layout.todo_item, null);
//			
//			ViewHolder holder = getViewHolder(view);
//			view.setTag(holder);
//			
//			return view;
//		}
//
//	}
//	
//	private int userId;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.todo_list);
//
//		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME, MODE_PRIVATE);
//		userId = prefs.getInt(IConstants.User.ID, 0);
//		
//		Cursor cursor = managedQuery(IConstants.event.CONTENT_URI, PROJECTION, IConstants.event.CALENDAR_ID + "=" + IConstants.event._ID, null, null);
//		mAdapter = new TodoListAdapter(this, cursor);
//		setListAdapter(mAdapter);
//	}
//	
//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		myapp.thukygiangvien.View.TodoListActivity.TodoListAdapter.ViewHolder holder = mAdapter.getViewHolder(v);
//		Bundle bundle = new Bundle();
//		bundle.putLong(IConstants._ID, holder._id);
//		Intent i = new Intent(TodoListActivity.this, TodoAddActivity.class);
//		i.putExtras(bundle);
//		startActivity(i);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		createMenu(menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		switch (item.getItemId())
//		{
//		case 0:
//		{
//			Intent todoAdd = new Intent("myapp.thukygiangvien.TODO_ADD_ACTIVITY");
//			startActivity(todoAdd);
//			break;
//		}
//		case 1:
//		{
//			TodoUtils.delete(TodoListActivity.this, -1);
//			break;
//		}
//		case 2: 
//			TodoUtils.sync(userId, TodoListActivity.this);
//		}
//		return true;
//		
//	}
//
//	private void createMenu( Menu menu ) {
//		
//		MenuItem addItem = menu.add(0, 0, 0, "Thêm");
//		addItem.setIcon(R.drawable.ic_menu_add_picture);
//		MenuItem deleteItem = menu.add(0, 1, 1, "Xóa tất cả");
//		deleteItem.setIcon(R.drawable.ic_menu_clear_list);
//		MenuItem sync = menu.add(0, 2, 2, "Đồng Bộ");
//		sync.setIcon(R.drawable.sync);
//		
//	}

}
