package myapp.thukydientu.view;

import myapp.thukydientu.R;
import myapp.thukydientu.adapter.ScheduleAdapter;
import myapp.thukydientu.model.IConstants;
import myapp.thukydientu.service.SyncService;
import myapp.thukydientu.util.ScheduleUtils;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorTreeAdapter;

public class ScheduleListActivity extends ExpandableListActivity {

	private CursorTreeAdapter mAdapter;
	private int userId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set up our adapter
		mAdapter = new ScheduleAdapter(this);

		SharedPreferences prefs = getSharedPreferences(IConstants.PREF_NAME,
				MODE_PRIVATE);
		userId = prefs.getInt(IConstants.User.ID, 0);

		setListAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		createMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void createMenu(Menu menu) {

		MenuItem signout = menu.add(0, 0, 0, "Đăng Xuất");
		signout.setIcon(android.R.drawable.ic_menu_more);

		MenuItem addItem = menu.add(0, 1, 1, "Đồng bộ");
		addItem.setIcon(R.drawable.sync);

		MenuItem deleteItem = menu.add(0, 2, 2, "Xóa tất cả");
		deleteItem.setIcon(R.drawable.ic_menu_clear_list);

		menu.add(0, 3, 3, "QR Code");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0:
			SharedPreferences prefs = getSharedPreferences(
					IConstants.PREF_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(IConstants.LOGON_STATUS, false);
			editor.commit();
			startActivity(new Intent(getBaseContext(), LogonActivity.class));
			finish();
		case 1: {
			new SyncService(this, userId, IConstants.DataType.SCHEDULE);
			break;
		}
		case 2: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Xác nhận")
					.setMessage("Xóa Tất Cả")
					.setPositiveButton("Đồng Ý",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									ScheduleUtils.delete(
											ScheduleListActivity.this, -1);
								}
							})
					.setNegativeButton("Hủy",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
			builder.create().show();

			break;
		}
		case 3:
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, 0);
			break;
		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
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

	@Override
	protected void onDestroy() {

		super.onDestroy();

		// Null out the group cursor. This will cause the group cursor and all
		// of the child cursors
		// to be closed.
		mAdapter.changeCursor(null);
		mAdapter = null;
	}
}
