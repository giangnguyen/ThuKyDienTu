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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorTreeAdapter;

public class ScheduleListActivity extends ExpandableListActivity {

	private CursorTreeAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// Set up our adapter
		mAdapter = new ScheduleAdapter(this);

		setListAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		createMenu(menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void createMenu(Menu menu) {

		MenuItem addItem = menu.add(0, 0, 0, "Đồng bộ");
		addItem.setIcon(R.drawable.sync);

		MenuItem deleteItem = menu.add(0, 1, 1, "Xóa tất cả");
		deleteItem.setIcon(R.drawable.ic_menu_clear_list);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0: {
			new SyncService(this, MainActivity.sUserId, IConstants.DataType.SCHEDULE);
			break;
		}
		case 1: {
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
		}
		return true;

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
